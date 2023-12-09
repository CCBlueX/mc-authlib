package net.ccbluex.liquidbounce.authlib.utils

import java.util.*

object MojangApi {

    /**
     * Requests the Uuid of a username from the Mojang API
     */
    fun getUuid(username: String): UUID? = runCatching {
        val (code, text) = HttpUtils.get("https://api.mojang.com/users/profiles/minecraft/$username")

        if (code != 200) {
            error("Failed to get UUID of $username")
        }

        val response = decode<ApiProfileResponse>(text)

        // Format UUID because otherwise it will be invalid
        parseUuid(response.id)
    }.getOrNull()

    data class ApiProfileResponse(val id: String, val name: String)

}

