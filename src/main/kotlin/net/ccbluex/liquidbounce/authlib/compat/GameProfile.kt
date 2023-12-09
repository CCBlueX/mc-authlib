package net.ccbluex.liquidbounce.authlib.compat

import java.util.UUID

/**
 * Represents a game profile with a username and UUID.
 *
 * @property username the username of the game profile
 * @property uuid the UUID of the game profile
 */
data class GameProfile(val username: String, val uuid: UUID?) {

    /**
     * Creates a session object using the provided token and type.
     *
     * @param token the token associated with the session
     * @param type the type of the session
     * @return a [Session] object
     */
    fun toSession(token: String, type: String) = Session(username, uuid!!, token, type)

}