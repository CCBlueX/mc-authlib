package net.ccbluex.liquidbounce.authlib.manage

import com.google.gson.JsonObject
import net.ccbluex.liquidbounce.authlib.account.CrackedAccount
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.account.MinecraftAccount
import net.ccbluex.liquidbounce.authlib.utils.GSON
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.string

object AccountSerializer {

    /**
     * Converts a MinecraftAccount object to a JsonObject.
     *
     * @param account the MinecraftAccount object to convert
     * @return a JsonObject representing the MinecraftAccount object
     */
    fun toJson(account: MinecraftAccount): JsonObject {
        val json = JsonObject()
        account.toRawJson(json)
        json["type"] = account.javaClass.simpleName
        json["favorite"] = account.favorite
        json["bans"] = GSON.toJsonTree(account.bans)
        return json
    }

    /**
     * Converts a JsonObject to a MinecraftAccount object.
     *
     * @param json the JsonObject containing the account data
     * @return a MinecraftAccount object
     */
    fun fromJson(json: JsonObject): MinecraftAccount {
        val typeClass = json.string("type")!!.substringAfterLast(".")
        val typePath = "net.ccbluex.liquidbounce.authlib.account.$typeClass"

        val account = Class.forName(typePath).getDeclaredConstructor().newInstance() as MinecraftAccount
        account.fromRawJson(json)

        if (json.has("bans")) {
            account.bans = GSON.fromJson(json["bans"], account.bans.javaClass)
        }

        if (json.has("favorite") && json["favorite"].asBoolean) {
            account.favorite()
        }

        return account
    }

    /**
     * Returns a new instance of [MinecraftAccount] based on the provided [name].
     * If the [name] starts with "ms@", it creates a [MicrosoftAccount] using
     * the [buildFromAuthCode] method from [MicrosoftAccount] class.
     * Otherwise, it creates a [CrackedAccount] with the name set to [name].
     *
     * @param name The name of the account. If it starts with "ms@", it is treated as a
     * Microsoft account authenticate code, otherwise, it is treated as the name of a
     * cracked account.
     *
     * @return A new instance of [MinecraftAccount] created based on the provided [name].
     */
    fun accountInstance(name: String): MinecraftAccount {
        return if (name.startsWith("ms@")) {
            val realName = name.substring(3)
            MicrosoftAccount.buildFromAuthCode(realName, MicrosoftAccount.AuthMethod.MICROSOFT)
        } else {
            CrackedAccount(username = name)
        }
    }

}