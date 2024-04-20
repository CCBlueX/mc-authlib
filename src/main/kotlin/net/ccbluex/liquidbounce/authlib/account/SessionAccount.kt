package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.yggdrasil.GameProfileRepository
import net.ccbluex.liquidbounce.authlib.utils.parseUuid
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.string
import java.net.Proxy
import java.util.*

/**
 * A minecraft session account - premium account without credentials.
 */
class SessionAccount(private val session: String) : MinecraftAccount("Cracked") {

    /**
     * Used for JSON deserialize.
     */
    @Suppress("unused")
    constructor() : this("")

    companion object {
        fun fromToken(token: String): SessionAccount {
            val account = SessionAccount(token)
            account.refresh()
            return account
        }
    }

    override fun refresh() {
        profile = GameProfileRepository().fetchBySession(session)
    }

    override fun login(): Pair<Session, YggdrasilAuthenticationService> {
        if (profile?.uuid == null) {
            refresh()
        }

        val session = profile!!.toSession(session, "mojang")
        val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, YggdrasilEnvironment.PROD.environment)

        return session to service
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = profile!!.username
        if (profile!!.uuid != null) {
            json["uuid"] = profile!!.uuid.toString()
        }
    }

    override fun fromRawJson(json: JsonObject) {
        val name = json.string("name")!!
        val uuid = if (json.has("uuid")) parseUuid(json.string("uuid")!!) else null
        profile = GameProfile(name, uuid)
    }
}
