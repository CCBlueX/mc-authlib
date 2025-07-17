package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.ccbluex.liquidbounce.authlib.bantracker.Ban
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session
import java.util.Collections

abstract class MinecraftAccount(val type: String) {

    var profile: GameProfile? = null

    /**
     * Represents the account as favorite or not.
     */
    var favorite = false
        private set

    var bans = mutableMapOf<String, Ban>()

    /**
     * Refreshes the Minecraft account by authenticating the user and updating the access token and profile.
     */
    abstract fun refresh()

    /**
     * Performs a login operation for the Minecraft account. This method is abstract and must be implemented by subclasses.
     *
     * @return a Pair object containing the session object and the YggdrasilAuthenticationService object
     */
    abstract fun login(): Pair<Session, YggdrasilAuthenticationService>

    /**
     * Converts the MinecraftAccount object to a JSON object.
     *
     * @param json the JSON object to store the converted data
     */
    abstract fun toRawJson(json: JsonObject)

    /**
     * Converts the data from a JsonObject to a MinecraftAccount object.
     *
     * @param json the JsonObject containing the account data
     */
    abstract fun fromRawJson(json: JsonObject)

    /**
     * Marks the account as a favorite.
     */
    fun favorite() = apply {
        favorite = true
    }

    /**
     * Marks the account as not a favorite.
     */
    fun unfavorite() = apply {
        favorite = false
    }

    /**
     * Tracks a ban, which should be called when the player is banned. The logic for this needs to be implemented
     * by the client itself.
     */
    fun trackBan(ban: Ban) {
        bans[ban.serverName] = ban
    }

    /**
     * Untracks a ban, which should be called when the player is able to join the server again.
     */
    fun untrackBan(serverName: String) {
        bans.remove(serverName)
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
        bans.values.removeIf { it.bannedUntil != -1L && it.bannedUntil < System.currentTimeMillis() }
        return bans.values.toList()
    }

    companion object {
        @JvmField
        val SERIAL_NAME_TO_TYPE: Map<String, Class<out MinecraftAccount>> = Collections.unmodifiableMap(
            hashMapOf(
                "AlteningAccount" to AlteningAccount::class.java,
                "CrackedAccount" to CrackedAccount::class.java,
                "EasyMCAccount" to EasyMCAccount::class.java,
                "MicrosoftAccount" to MicrosoftAccount::class.java,
                "SessionAccount" to SessionAccount::class.java,
            )
        )

        @JvmField
        val TYPE_TO_SERIAL_NAME: Map<Class<out MinecraftAccount>, String> = Collections.unmodifiableMap(
            hashMapOf(
                AlteningAccount::class.java to "AlteningAccount",
                CrackedAccount::class.java to "CrackedAccount",
                EasyMCAccount::class.java to "EasyMCAccount",
                MicrosoftAccount::class.java to "MicrosoftAccount",
                SessionAccount::class.java to "SessionAccount",
            )
        )
    }

}