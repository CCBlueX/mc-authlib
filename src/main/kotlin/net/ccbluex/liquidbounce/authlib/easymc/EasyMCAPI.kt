package net.ccbluex.liquidbounce.authlib.easymc

import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.HttpUtils
import net.ccbluex.liquidbounce.authlib.utils.parseUuid

/**
 * Interface for the EasyMC API to e.g. redeem tokens.
 */
class EasyMCAPI(val baseUrl: String) {

    @Deprecated(
        message = "Use Default singleton instead",
        replaceWith = ReplaceWith("EasyMCAPI.Default"),
        level = DeprecationLevel.WARNING
    )
    constructor() : this(DEFAULT_BASE_URL)

    companion object {
        const val DEFAULT_BASE_URL = "https://api.easymc.io/v1"

        @JvmField
        val Default = EasyMCAPI(DEFAULT_BASE_URL)
    }

    class RedeemRequest(
        val token: String
    )

    class RedeemResponse(
        val mcName: String,
        val uuid: String,
        val session: String,
        val message: String
    )

    class RedeemErrorResponse(
        val error: String
    )

    fun redeem(token: String): Session {
        require(token.isNotBlank()) { "Token must not be blank" }

        val request = RedeemRequest(token)
        val (response, error) =
            HttpUtils.postWithFallback<RedeemResponse, RedeemErrorResponse>("$baseUrl/token/redeem", request)

        if (error != null) {
            error("Failed to redeem token: ${error.error}")
        }

        if (response == null) {
            // Not possible - the response is always present
            error("Failed to redeem token: Unknown error")
        }

        val username = response.mcName
        val uuid = parseUuid(response.uuid)
        val accessToken = response.session

        return Session(username, uuid, accessToken, "mojang")
    }

}