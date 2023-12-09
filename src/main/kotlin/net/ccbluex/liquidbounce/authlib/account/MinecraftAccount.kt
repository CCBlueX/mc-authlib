package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session

abstract class MinecraftAccount(val type: String) {

    var profile: GameProfile? = null

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

}