package net.ccbluex.liquidbounce.authlib.utils

import java.util.*

/**
 * Parses a string into a UUID object.
 *
 * This function accepts both:
 * - Standard UUID strings with dashes (e.g., "123e4567-e89b-12d3-a456-426655440000")
 * - Compact UUID strings without dashes (32 hex digits, e.g., "123e4567e89b12d3a456426655440000")
 *
 * If the input is not a valid standard UUID string, it will attempt to parse it as a compact format.
 *
 * @param string The UUID string, with or without dashes.
 * @return The corresponding UUID object.
 * @throws IllegalArgumentException if the input is not a valid UUID in either format.
 */
fun parseUuid(string: String): UUID {
    return try {
        UUID.fromString(string)
    } catch (e: IllegalArgumentException) {
        uuidFromUnformatted(string)
    }
}

/**
 * Parses a UUID from a 32-character unformatted hex string (without dashes).
 *
 * Example:
 *   Input:  "123e4567e89b12d3a456426655440000"
 *   Output: UUID("123e4567-e89b-12d3-a456-426655440000")
 *
 * @param input A 32-character hexadecimal string representing a UUID (no dashes).
 * @return The parsed UUID object.
 * @throws IllegalArgumentException if the input is not exactly 32 hex characters.
 */
private fun uuidFromUnformatted(input: String): UUID {
    require(input.length == 32) { "UUID string must be 32 characters long without dashes" }
    val mostSigBits = input.substring(0, 16).toULong(radix = 16).toLong()
    val leastSigBits = input.substring(16, 32).toULong(radix = 16).toLong()
    return UUID(mostSigBits, leastSigBits)
}

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