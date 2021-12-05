package me.liuli.elixir.account

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import me.liuli.elixir.compat.Session
import me.liuli.elixir.exception.LoginException
import me.liuli.elixir.utils.HttpUtils

class MicrosoftAccount : MinecraftAccount("Microsoft") {
    override var name = "UNKNOWN"
    private var uuid = ""
    private var accessToken = ""
    private var refreshToken = ""

    override val session: Session
        get() {
            if(uuid.isEmpty() || accessToken.isEmpty()) {
                update()
            }

            return Session(name, uuid, accessToken, "mojang")
        }

    /**
     * get minecraft account info from Microsoft Refresh Token
     * @credit https://wiki.vg/Microsoft_Authentication_Scheme
     */
    override fun update() {
        val jsonPostHeader = mapOf("Content-Type" to "application/json", "Accept" to "application/json")

        // get the microsoft access token
        val msRefreshJson = Klaxon().parseJsonObject(HttpUtils.make(XBOX_AUTH_URL, "POST", XBOX_REFRESH_DATA + refreshToken,
            mapOf("Content-Type" to "application/x-www-form-urlencoded")).inputStream.reader(Charsets.UTF_8))
        val msAccessToken = msRefreshJson.string("access_token") ?: throw LoginException("Microsoft access token is null")
        // refresh token is changed after refresh
        refreshToken = msRefreshJson.string("refresh_token") ?: throw LoginException("Microsoft new refresh token is null")

        // authenticate with XBL
        val xblJson = Klaxon().parseJsonObject(HttpUtils.make(XBOX_XBL_URL, "POST", XBOX_XBL_DATA.replace("<access_token>", msAccessToken), jsonPostHeader).inputStream.reader(Charsets.UTF_8))
        val xblToken = xblJson.string("Token") ?: throw LoginException("Microsoft XBL token is null")
        val userhash = xblJson.obj("DisplayClaims")?.array<JsonObject>("xui")?.get(0)?.string("uhs") ?: throw LoginException("Microsoft XBL userhash is null")

        // authenticate with XSTS
        val xstsJson = Klaxon().parseJsonObject(HttpUtils.make(XBOX_XSTS_URL, "POST", XBOX_XSTS_DATA.replace("<xbl_token>", xblToken), jsonPostHeader).inputStream.reader(Charsets.UTF_8))
        val xstsToken = xstsJson.string("Token") ?: throw LoginException("Microsoft XSTS token is null")

        // get the minecraft access token
        val mcJson = Klaxon().parseJsonObject(HttpUtils.make(MC_AUTH_URL, "POST", MC_AUTH_DATA.replace("<userhash>", userhash).replace("<xsts_token>", xstsToken), jsonPostHeader).inputStream.reader(Charsets.UTF_8))
        accessToken = mcJson.string("access_token") ?: throw LoginException("Minecraft access token is null")

        // get the minecraft account profile
        val mcProfileJson = Klaxon().parseJsonObject(HttpUtils.make(MC_PROFILE_URL, "GET", "", mapOf("Authorization" to "Bearer $accessToken")).inputStream.reader(Charsets.UTF_8))
        name = mcProfileJson.string("name") ?: throw LoginException("Minecraft account name is null")
        uuid = mcProfileJson.string("id") ?: throw LoginException("Minecraft account uuid is null")
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
        json["refreshToken"] = refreshToken
    }

    override fun fromRawJson(json: JsonObject) {
        name = json["name"] as String
        refreshToken = json["refreshToken"] as String
    }

    companion object {
        const val XBOX_PRE_AUTH_URL = "https://login.live.com/oauth20_authorize.srf?client_id=00000000441cc96b&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&display=touch&scope=service::user.auth.xboxlive.com::MBI_SSL&locale=en"
        const val XBOX_AUTH_URL = "https://login.live.com/oauth20_token.srf"
        const val XBOX_XBL_URL = "https://user.auth.xboxlive.com/user/authenticate"
        const val XBOX_XSTS_URL = "https://xsts.auth.xboxlive.com/xsts/authorize"
        const val MC_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox"
        const val MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile"
        const val XBOX_AUTH_DATA = "client_id=00000000441cc96b&redirect_uri=https://login.live.com/oauth20_desktop.srf&grant_type=authorization_code&code="
        const val XBOX_REFRESH_DATA = "client_id=00000000441cc96b&scope=service::user.auth.xboxlive.com::MBI_SSL&grant_type=refresh_token&redirect_uri=https://login.live.com/oauth20_desktop.srf&refresh_token="
        const val XBOX_XBL_DATA = """{"Properties":{"AuthMethod":"RPS","SiteName":"user.auth.xboxlive.com","RpsTicket":"<access_token>"},"RelyingParty":"http://auth.xboxlive.com","TokenType":"JWT"}"""
        const val XBOX_XSTS_DATA = """{"Properties":{"SandboxId":"RETAIL","UserTokens":["<xbl_token>"]},"RelyingParty":"rp://api.minecraftservices.com/","TokenType":"JWT"}"""
        const val MC_AUTH_DATA = """{"identityToken":"XBL3.0 x=<userhash>;<xsts_token>"}"""

        /**
         * Create a new [MicrosoftAccount] from a microsoft account authenticate [code]
         */
        fun buildFromAuthCode(code: String): MicrosoftAccount {
            val data = Klaxon().parseJsonObject(HttpUtils.make(XBOX_AUTH_URL, "POST", XBOX_AUTH_DATA + code, mapOf("Content-Type" to "application/x-www-form-urlencoded")).inputStream.reader(Charsets.UTF_8))
            return if(data.containsKey("refresh_token")) {
                MicrosoftAccount().also { it.refreshToken = data["refresh_token"] as String ; it.update() }
            } else {
                throw LoginException("Failed to get refresh token")
            }
        }

        /**
         * Create a new [MicrosoftAccount] from [username] and [password]
         *
         * @credit https://github.com/XboxReplay/xboxlive-auth
         */
        fun buildFromPassword(username: String, password: String): MicrosoftAccount {
            fun findArgs(resp: String, arg: String): String {
                return if (resp.contains(arg)) {
                    resp.substring(resp.indexOf("$arg:'") + arg.length + 2).let {
                        it.substring(0, it.indexOf("',"))
                    }
                } else {
                    throw LoginException("Failed to find argument in response $arg")
                }
            }

            // first, get the pre-auth url
            val preAuthConnection = HttpUtils.make(XBOX_PRE_AUTH_URL, "GET")
            val html = preAuthConnection.inputStream.reader().readText()
            val cookies = (preAuthConnection.headerFields["Set-Cookie"] ?: emptyList()).joinToString(";")
            val urlPost = findArgs(html, "urlPost")
            val ppft = findArgs(html, "sFTTag").let {
                it.substring(it.indexOf("value=\"") + 7, it.length - 3)
            }
            preAuthConnection.disconnect()

            // then, post the login form
            val authConnection = HttpUtils.make(urlPost, "POST",
                "login=${username}&loginfmt=${username}&passwd=${password}&PPFT=$ppft",
                mapOf("Cookie" to cookies, "Content-Type" to "application/x-www-form-urlencoded"))
            authConnection.inputStream.reader().readText()
            val code = authConnection.url.toString().let {
                if(!it.contains("code=")) {
                    throw LoginException("Failed to get auth code from response")
                }
                val pre = it.substring(it.indexOf("code=") + 5)
                pre.substring(0, pre.indexOf("&"))
            }
            authConnection.disconnect()

            // pass the code to [buildFromAuthCode]
            return buildFromAuthCode(code)
        }
    }
}