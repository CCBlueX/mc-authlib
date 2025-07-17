package net.ccbluex.liquidbounce.authlib.yggdrasil

import com.google.gson.annotations.SerializedName
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.HttpUtils
import net.ccbluex.liquidbounce.authlib.utils.parseUuid
import java.util.*

val clientIdentifier: String = UUID.randomUUID().toString()

/**
 * A user authentication object for Yggdrasil authentication.
 * Written from scratch as it is not available in the original library.
 *
 * Documentation: https://wiki.vg/Authentication
 */
class YggdrasilUserAuthentication(val baseUrl: String) {

    @Deprecated(
        message = "Use Default singleton instead",
        replaceWith = ReplaceWith("YggdrasilUserAuthentication.Default"),
        level = DeprecationLevel.WARNING
    )
    constructor() : this(DEFAULT_BASE_URL)

    companion object {
        const val DEFAULT_BASE_URL = "https://authserver.mojang.com"

        @JvmField
        val Default = YggdrasilUserAuthentication(DEFAULT_BASE_URL)
    }

    enum class Agent(
        @SerializedName("name")
        val agentName: String,
        val version: Int
    ) {
        MINECRAFT("Minecraft", 1)
        // SCROLLS - this authlib is not intended to be used for Scrolls
    }

    class AuthenticationRequest(
        val agent: Agent,
        val username: String,
        val password: String,
        val clientToken: String = clientIdentifier,
        val requestUser: Boolean = true
    )

    class AuthenticationResponse(
        val accessToken: String,
        val clientToken: String,
        val availableProfiles: Array<Profile>,
        /**
         * If a user attempts to log into a valid Mojang account with no attached Minecraft license,
         * the authentication will be successful, but the response will not contain a selectedProfile field,
         * and the availableProfiles array will be empty.
         */
        val selectedProfile: Profile?
    ) {
        class Profile(
            val id: String,
            val name: String
        )
    }

    fun authenticate(username: String, password: String): Session {
        if (username.isBlank()) {
            error("Username cannot be blank")
        }

        if (password.isBlank()) {
            error("Password cannot be blank")
        }

        val request = AuthenticationRequest(Agent.MINECRAFT, username, password)
        val response = HttpUtils.post<AuthenticationResponse>("$baseUrl/authenticate", request)

        if (response.clientToken != clientIdentifier) {
            error("Client identifier mismatch")
        }

        val selectedProfile = response.selectedProfile

        if (selectedProfile == null || response.availableProfiles.isEmpty()) {
            error("Minecraft account not purchased")
        }

        val username = selectedProfile.name
        val uuid = parseUuid(selectedProfile.id)
        val accessToken = response.accessToken

        return Session(username, uuid, accessToken, "mojang")
    }

}