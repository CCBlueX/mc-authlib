package net.ccbluex.liquidbounce.authlib.compat

import java.util.UUID

/**
 * Represents a session object with user authentication information.
 *
 * @property username the username associated with the session
 * @property uuid the UUID associated with the session
 * @property token the token associated with the session
 * @property type the type of the session
 *
 * Compat layer for [net.minecraft.client.session.Session]
 */
data class Session(val username: String, val uuid: UUID, val token: String, val type: String)
