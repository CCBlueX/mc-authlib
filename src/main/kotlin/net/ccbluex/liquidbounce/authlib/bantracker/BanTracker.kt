package net.ccbluex.liquidbounce.authlib.bantracker

class BanTracker {

    private val banMap = mutableMapOf<String, Ban>()

    /**
     * Tracks a ban, which should be called when the player is banned. The logic for this needs to be implemented
     * by the client itself.
     */
    fun trackBan(ban: Ban) {
        banMap[ban.serverName] = ban
    }

    /**
     * Untracks a ban, which should be called when the player is able to join the server again.
     */
    fun untrackBan(serverName: String) {
        banMap.remove(serverName)
    }

    /**
     * Checks if the player is banned on the specified server.
     */
    fun isBanned(serverName: String): Boolean {
        return listActiveBans().any { it.serverName == serverName }
    }

    /**
     * Returns a list of all active bans.
     */
    fun listActiveBans(): List<Ban> {
        // Remove expired bans
        banMap.values.removeIf { it.bannedUntil != -1L && it.bannedUntil < System.currentTimeMillis() }
        return banMap.values.toList()
    }

}

/**
 * Keeps track of a ban.
 *
 * @param serverName the name of the server the ban was issued on
 * @param reason the reason for the ban
 * @param bannedUntil the time the ban will expire (in milliseconds, if -1 the ban is either permanent or
 * the unban time is unknown)
 */
data class Ban(val serverName: String, val reason: String, val bannedUntil: Long = -1L) {

    val isPermanent: Boolean
        get() = bannedUntil == -1L

}