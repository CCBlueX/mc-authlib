package net.ccbluex.liquidbounce.authlib.easymc

import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.GsonDeserializable
import net.ccbluex.liquidbounce.authlib.utils.GsonSerializable
import net.ccbluex.liquidbounce.authlib.utils.HttpUtils
import net.ccbluex.liquidbounce.authlib.utils.parseUuid

/**
 * Interface for the EasyMC API to e.g. redeem tokens.
 */
class EasyMCAPI(val server: String = "https://api.easymc.io/v1") {

    class RedeemRequest(
        val token: String
    ) : GsonSerializable

    class RedeemResponse(
        val mcName: String,
        val uuid: String,
        val session: String,
        val message: String
    ) : GsonDeserializable

    class RedeemErrorResponse(
        val error: String
    ) : GsonDeserializable

    fun redeem(token: String): Session {
        require(token.isNotBlank()) { "Token must not be blank" }

        val request = RedeemRequest(token)
        val (response, error) =
            HttpUtils.postWithFallback<RedeemResponse, RedeemErrorResponse>("$server/token/redeem", request)

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