package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.OAuthServer
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.*
import java.net.Proxy
import java.util.*

class MicrosoftAccount : MinecraftAccount("Microsoft") {

    private var accessToken = ""
    private var refreshToken = ""
    private var authMethod = AuthMethod.MICROSOFT

    override fun login(): Pair<Session, YggdrasilAuthenticationService> {
        if (profile?.uuid == null || accessToken.isEmpty()) {
            refresh()
        }

        val session = profile!!.toSession(accessToken, "mojang")
        val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, YggdrasilEnvironment.PROD.environment)

        return session to service
    }

    /**
     * get minecraft account info from Microsoft Refresh Token
     * @credit https://wiki.vg/Microsoft_Authentication_Scheme
     */
    override fun refresh() {
        val jsonPostHeader = mapOf("Content-Type" to "application/json", "Accept" to "application/json")

        // get the microsoft access token
        val (code, response) = HttpUtils.post(
            XBOX_AUTH_URL,
            replaceKeys(authMethod, XBOX_REFRESH_DATA) + refreshToken,
            mapOf("Content-Type" to "application/x-www-form-urlencoded")
        )

        if (code != 200) {
            error("Failed to get Microsoft access token")
        }

        val msRefreshJson = JsonParser.parseString(response).asJsonObject
        val msAccessToken = msRefreshJson.string("access_token") ?: error("Microsoft access token is null")
        // refresh token is changed after refresh
        refreshToken = msRefreshJson.string("refresh_token") ?: error("Microsoft new refresh token is null")

        // authenticate with XBL
        val (xblCode, xblText) = HttpUtils.post(
            XBOX_XBL_URL, XBOX_XBL_DATA.replace("<rps_ticket>",
            authMethod.rpsTicketRule.replace("<access_token>", msAccessToken)), jsonPostHeader)

        if (xblCode != 200) {
            error("Failed to get Microsoft XBL token")
        }

        val xblJson = JsonParser.parseString(xblText).asJsonObject
        val xblToken = xblJson.string("Token") ?: error("Microsoft XBL token is null")
        val userHash = xblJson.obj("DisplayClaims")?.array("xui")?.get(0)?.asJsonObject?.string("uhs")
            ?: error("Microsoft XBL userhash is null")

        // authenticate with XSTS
        val (xstsCode, xstsText) = HttpUtils.post(XBOX_XSTS_URL,
            XBOX_XSTS_DATA.replace("<xbl_token>", xblToken), jsonPostHeader)

        if (xstsCode != 200) {
            error("Failed to get Microsoft XSTS token")
        }

        val xstsJson = JsonParser.parseString(xstsText).asJsonObject
        val xstsToken = xstsJson.string("Token") ?: error("Microsoft XSTS token is null")

        // get the minecraft access token
        val (mcCode, mcText) = HttpUtils.post(
            MC_AUTH_URL, MC_AUTH_DATA.replace("<userhash>", userHash)
            .replace("<xsts_token>", xstsToken), jsonPostHeader)

        if (mcCode != 200) {
            error("Failed to get Minecraft access token (Not purchased?)")
        }

        val mcJson = JsonParser.parseString(mcText).asJsonObject
        accessToken = mcJson.string("access_token") ?: error("Minecraft access token is null")

        // get the minecraft account profile
        val (mcProfileCode, mcProfileText) = HttpUtils.get(MC_PROFILE_URL, mapOf("Authorization" to "Bearer $accessToken"))

        if (mcProfileCode != 200) {
            error("Failed to get Minecraft profile")
        }

        val mcProfileJson = JsonParser.parseString(mcProfileText).asJsonObject

        val name = mcProfileJson.string("name") ?: error("Minecraft account name is null")
        val uuid = parseUuid(mcProfileJson.string("id") ?: error("Minecraft account uuid is null"))

        profile = GameProfile(name, uuid)
    }

    /**
     * Saves the account data to a JSON object.
     *
     * @param json The JSON object to save the account data to.
     */
    override fun toRawJson(json: JsonObject) {
        json["name"] = profile!!.username
        if (profile!!.uuid != null) {
            json["uuid"] = profile!!.uuid.toString()
        }
        json["refreshToken"] = refreshToken
        json["authMethod"] = AuthMethod.entries.firstOrNull { it == authMethod }?.name ?: error("Unregistered auth method")
    }

    /**
     * Loads the account data from a JSON object.
     *
     * @param json The JSON object containing the account data.
     */
    override fun fromRawJson(json: JsonObject) {
        val name = json.string("name")!!
        val uuid = if (json.has("uuid")) parseUuid(json.string("uuid")!!) else null
        profile = GameProfile(name, uuid)
        refreshToken = json.string("refreshToken")!!
        authMethod = AuthMethod.valueOf(json.string("authMethod")  ?: error("No auth method in json"))
    }

    /**
     * The [Companion] object contains constants and helper methods for the [MicrosoftAccount] class.
     */
    companion object {

        const val XBOX_PRE_AUTH_URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>&prompt=select_account"
        const val XBOX_AUTH_URL = "https://login.live.com/oauth20_token.srf"
        const val XBOX_XBL_URL = "https://user.auth.xboxlive.com/user/authenticate"
        const val XBOX_XSTS_URL = "https://xsts.auth.xboxlive.com/xsts/authorize"
        const val MC_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox"
        const val MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile"
        const val XBOX_AUTH_DATA = "client_id=<client_id>&redirect_uri=<redirect_uri>&grant_type=authorization_code&code="
        const val XBOX_REFRESH_DATA = "client_id=<client_id>&scope=<scope>&grant_type=refresh_token&redirect_uri=<redirect_uri>&refresh_token="
        const val XBOX_XBL_DATA = """{"Properties":{"AuthMethod":"RPS","SiteName":"user.auth.xboxlive.com","RpsTicket":"<rps_ticket>"},"RelyingParty":"http://auth.xboxlive.com","TokenType":"JWT"}"""
        const val XBOX_XSTS_DATA = """{"Properties":{"SandboxId":"RETAIL","UserTokens":["<xbl_token>"]},"RelyingParty":"rp://api.minecraftservices.com/","TokenType":"JWT"}"""
        const val MC_AUTH_DATA = """{"identityToken":"XBL3.0 x=<userhash>;<xsts_token>"}"""

        /**
         * Create a new [MicrosoftAccount] from a microsoft account authenticate [code]
         */
        fun buildFromAuthCode(code: String, method: AuthMethod): MicrosoftAccount {
            val (responseCode, response) = HttpUtils.post(
                XBOX_AUTH_URL,
                replaceKeys(method, XBOX_AUTH_DATA) + code,
                mapOf("Content-Type" to "application/x-www-form-urlencoded")
            )

            if (responseCode != 200) {
                msError(response)
            }

            val data = JsonParser.parseString(response).asJsonObject
            return if (data.has("refresh_token")) {
                MicrosoftAccount().also {
                    it.refreshToken = data.string("refresh_token")!!
                    it.authMethod = method
                    it.refresh()
                }
            } else {
                error("Failed to get refresh token")
            }
        }

        /**
         * Raises an error message based on the response from Microsoft.
         *
         * @param response The response string from Microsoft.
         */
        private fun msError(response: String) {
            val errorJson = JsonParser.parseString(response).asJsonObject
            val error = errorJson.string("error") ?: "Missing key 'error'"
            val errorDescription = errorJson.string("error_description") ?: "Missing key 'error_description'"
            error("$errorDescription ($error)")
        }

        /**
         * Create a new [MicrosoftAccount] from OAuth
         */
        fun buildFromOpenBrowser(handler: OAuthHandler, authMethod: AuthMethod = AuthMethod.AZURE_APP): OAuthServer {
            return OAuthServer(handler, authMethod).also { it.start() }
        }

        fun replaceKeys(method: AuthMethod, string: String)
            = string.replace("<client_id>", method.clientId)
                .replace("<redirect_uri>", method.redirectUri)
                .replace("<scope>", method.scope)
    }

    enum class AuthMethod(val clientId: String, val redirectUri: String, val scope: String, val rpsTicketRule: String) {

        MICROSOFT(
            "00000000441cc96b",
            "https://login.live.com/oauth20_desktop.srf",
            "service::user.auth.xboxlive.com::MBI_SSL",
            "<access_token>"
        ),
        AZURE_APP(
            "0add8caf-2cc6-4546-b798-c3d171217dd9",
            "http://localhost:${oauthPort}/login",
            "XboxLive.signin%20offline_access",
            "d=<access_token>"
        )

    }

    interface OAuthHandler {

        /**
         * Called when the server has prepared the user for authentication
         */
        fun openUrl(url: String)

        /**
         * Called when the user has completed authentication
         */
        fun authResult(account: MicrosoftAccount)

        /**
         * Called when the user has cancelled the authentication process or the thread has been interrupted
         */
        fun authError(error: String)

    }
}