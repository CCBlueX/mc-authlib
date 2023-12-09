package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.thealtening.api.TheAltening
import com.thealtening.api.TheAlteningException
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.MojangApi
import net.ccbluex.liquidbounce.authlib.utils.int
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.string
import net.ccbluex.liquidbounce.authlib.yggdrasil.YggdrasilUserAuthentication
import java.net.Proxy
import java.util.*
import kotlin.jvm.Throws

const val ALTENING_AUTH = "http://authserver.thealtening.com"
const val ALTENING_SESSION = "http://sessionserver.thealtening.com"
val alteningEnvironment = Environment(ALTENING_SESSION, "https://api.minecraftservices.com", "PROD")

/**
 * Represents an Altening account from the TheAltening Account Generator
 *
 * @constructor Creates an AlteningAccount object with the specified type.
 * @param type The type of the account.
 */
class AlteningAccount(var accountToken: String) : MinecraftAccount("TheAltening") {

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

    /**
     * Represents the level of a player on the Hypixel server.
     *
     * @property hypixelLevel The current level of the player on Hypixel.
     */
    var hypixelLevel: Int = 0
        private set

    /**
     * Represents the rank of a player on the Hypixel server.
     * @property hypixelRank The rank of the player on Hypixel.
     */
    var hypixelRank: String = ""
        private set

    private val sessionService = YggdrasilAuthenticationService(Proxy.NO_PROXY, alteningEnvironment)
    private val userAuthentication = YggdrasilUserAuthentication(ALTENING_AUTH)

    /**
     * load the account data from json
     * @param json contains the account data
     */
    override fun fromRawJson(json: JsonObject) {
        val name = json.string("name")!!
        val uuid = runCatching {
            UUID.fromString(json.string("uuid")!!)
        }.getOrElse { MojangApi.getUuid(name) }
        profile = GameProfile(name, uuid!!)

        accessToken = json.string("token")!!
        hypixelLevel = json.int("hypixelLevel")!!
        hypixelRank = json.string("hypixelRank")!!
    }

    /**
     * save the account data to json
     * @param json needs to write data in
     */
    override fun toRawJson(json: JsonObject) {
        json["name"] = profile!!.username
        json["uuid"] = profile!!.uuid.toString()
        json["token"] = accessToken
        json["hypixelLevel"] = hypixelLevel
        json["hypixelRank"] = hypixelRank
    }

    override fun refresh() {
        val session = userAuthentication.authenticate(accountToken, "LiquidBounce")
        accessToken = session.token
        profile = GameProfile(session.username, session.uuid)
    }

    override fun login(): Pair<Session, YggdrasilAuthenticationService> {
        if (profile == null) {
            refresh()
        }

        val session = profile!!.toSession(accessToken, "mojang")
        return session to sessionService
    }

    companion object {

        fun fromToken(accountToken: String): AlteningAccount {
            val account = AlteningAccount(accountToken)
            account.refresh()
            return account
        }

        @Throws(TheAlteningException::class)
        fun generateAccount(apiToken: String): AlteningAccount {
            val alteningAccount = TheAltening.newBasicRetriever(apiToken).account

            val account = AlteningAccount(alteningAccount.token)
            account.refresh()
            return account
        }

    }

}

