package net.ccbluex.liquidbounce.authlib.yggdrasil

import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.utils.HttpUtils
import net.ccbluex.liquidbounce.authlib.utils.decode
import net.ccbluex.liquidbounce.authlib.utils.parseUuid
import java.util.*

class GameProfileRepository(val servicesHost: String = "https://api.minecraftservices.com") {

    /**
     * Requests the Uuid of a username from the Mojang API
     *
     * [YggdrasilGameProfileRepository.findProfilesByNames] offers the same functionality, but
     * uses a different API endpoint for bulk requests and is therefore not suitable for this use case.
     */
    fun fetchUuidByUsername(username: String): UUID? = runCatching {
        val (code, text) = HttpUtils.get("$servicesHost/users/profiles/minecraft/$username")

        if (code != 200) {
            error("Failed to get UUID of $username")
        }

        val response = decode<ApiProfileResponse>(text)

        // Format UUID because otherwise it will be invalid
        parseUuid(response.id)
    }.getOrNull()

    /**
     * Fetch profile by session token
     */
    fun fetchBySession(token: String): GameProfile {
        val (code, text) = HttpUtils.get("$servicesHost/minecraft/profile", header =
            mapOf("Authorization" to "Bearer $token")
        )

        println(text)

        if (code != 200) {
            error("Failed to get profile by session")
        }

        val response = decode<ApiProfileResponse>(text)

        // Format UUID because otherwise it will be invalid
        val uuid = parseUuid(response.id)

        return GameProfile(response.name, uuid)
    }

    data class ApiProfileResponse(val id: String, val name: String)

}

