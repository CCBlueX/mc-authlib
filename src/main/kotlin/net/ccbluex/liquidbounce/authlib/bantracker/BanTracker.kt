package net.ccbluex.liquidbounce.authlib.bantracker

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