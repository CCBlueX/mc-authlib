package net.ccbluex.liquidbounce.authlib.manage

import com.google.gson.JsonObject
import net.ccbluex.liquidbounce.authlib.account.CrackedAccount
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.account.MinecraftAccount

@Deprecated("All features are moved to MinecraftAccount class")
object AccountSerializer {

    /**
     * Converts a MinecraftAccount object to a JsonObject.
     *
     * @param account the MinecraftAccount object to convert
     * @return a JsonObject representing the MinecraftAccount object
     */
    @Deprecated(
        message = "Use MinecraftAccount.toJson instead",
        replaceWith = ReplaceWith("account.toJson()"),
        level = DeprecationLevel.WARNING
    )
    fun toJson(account: MinecraftAccount): JsonObject {
        return account.toJson()
    }

    /**
     * Converts a JsonObject to a MinecraftAccount object.
     *
     * @param json the JsonObject containing the account data
     * @return a MinecraftAccount object
     */
    @Deprecated(
        message = "Use MinecraftAccount.fromJson instead",
        replaceWith = ReplaceWith(
            "MinecraftAccount.fromJson(json)",
            "net.ccbluex.liquidbounce.authlib.account.MinecraftAccount"
        ),
        level = DeprecationLevel.WARNING
    )
    fun fromJson(json: JsonObject): MinecraftAccount {
        return MinecraftAccount.fromJson(json)
    }

    /**
     * Returns a new instance of [MinecraftAccount] based on the provided [name].
     * If the [name] starts with "ms@", it creates a [MicrosoftAccount] using
     * [MicrosoftAccount.buildFromAuthCode].
     * Otherwise, it creates a [CrackedAccount] with the name set to [name].
     *
     * @param name The name of the account. If it starts with "ms@", it is treated as a
     * Microsoft account authenticate code, otherwise, it is treated as the name of a
     * cracked account.
     *
     * @return A new instance of [MinecraftAccount] created based on the provided [name].
     */
    @Deprecated(
        message = "Use MinecraftAccount.fromName instead",
        replaceWith = ReplaceWith(
            "MinecraftAccount.fromName(name)",
            "net.ccbluex.liquidbounce.authlib.account.MinecraftAccount"
        ),
        level = DeprecationLevel.WARNING
    )
    fun accountInstance(name: String): MinecraftAccount {
        return MinecraftAccount.fromName(name)
    }

}