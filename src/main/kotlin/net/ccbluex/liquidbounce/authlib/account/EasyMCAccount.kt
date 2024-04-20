package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.easymc.EasyMCAPI
import net.ccbluex.liquidbounce.authlib.utils.*
import java.net.Proxy
import java.util.*

val easyMcEnvironment = Environment("https://sessionserver.easymc.io", "https://api.minecraftservices.com", "PROD")

/**
 * Represents an Altening account from the TheAltening Account Generator
 *
 * @constructor Creates an AlteningAccount object with the specified type.
 * @param type The type of the account.
 */
class EasyMCAccount(val accountToken: String) : MinecraftAccount("EasyMC") {

    /**
     * Used for JSON deserialize.
     */
    @Suppress("unused")
    constructor() : this("")

    /**
     * Represents a token for authentication.
     */
    var accessToken = ""
        private set

    private val sessionService = YggdrasilAuthenticationService(Proxy.NO_PROXY, easyMcEnvironment)
    private val easyMCApi = EasyMCAPI()

    /**
     * load the account data from json
     * @param json contains the account data
     */
    override fun fromRawJson(json: JsonObject) {
        val name = json.string("name")!!
        val uuid = if (json.has("uuid")) parseUuid(json.string("uuid")!!) else null
        profile = GameProfile(name, uuid!!)
        accessToken = json.string("token")!!
    }

    /**
     * save the account data to json
     * @param json needs to write data in
     */
    override fun toRawJson(json: JsonObject) {
        json["name"] = profile!!.username
        if (profile!!.uuid != null) {
            json["uuid"] = profile!!.uuid.toString()
        }
        json["token"] = accessToken
    }

    override fun refresh() {
        if (profile != null) {
            // Already redeemed
            return
        }

        val session = easyMCApi.redeem(accountToken)

        accessToken = session.token
        profile = GameProfile(session.username, session.uuid)
    }

    override fun login(): Pair<Session, YggdrasilAuthenticationService> {
        if (profile?.uuid == null) {
            refresh()
        }

        val session = profile!!.toSession(accessToken, "mojang")
        return session to sessionService
    }

    companion object {

        fun fromToken(accountToken: String): EasyMCAccount {
            val account = EasyMCAccount(accountToken)
            account.refresh()
            return account
        }

    }

}

