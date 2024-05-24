package net.ccbluex.liquidbounce.authlib.utils

import java.util.*

/**
 * Parses a string representation of a UUID into a UUID object.
 *
 * @param string The string representation of the UUID.
 * @return The UUID object.
 */
fun parseUuid(string: String): UUID {
    return try {
        UUID.fromString(string)
    } catch (e: IllegalArgumentException) {
        uuidFromUnformatted(string)
    }
}

/**
 * Converts an unformatted String representation of a UUID to a UUID object.
 *
 * @param string The unformatted String representation of the UUID.
 * @return The UUID object.
 */
private fun uuidFromUnformatted(string: String): UUID = UUID.fromString(string.replaceFirst(
    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
    "$1-$2-$3-$4-$5"
))

/**
 * Generates an offline player UUID from a player name.
 * Minecraft does this by putting "OfflinePlayer:" in front of the player name.
 *
 * @param name The player name.
 * @return The offline player UUID.
 */
fun generateOfflinePlayerUuid(name: String): UUID {
    return UUID.nameUUIDFromBytes("OfflinePlayer:$name".toByteArray())
}