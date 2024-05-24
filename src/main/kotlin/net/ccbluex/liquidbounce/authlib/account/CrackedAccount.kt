package net.ccbluex.liquidbounce.authlib.account

import com.google.gson.JsonObject
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.ccbluex.liquidbounce.authlib.compat.GameProfile
import net.ccbluex.liquidbounce.authlib.compat.Session
import net.ccbluex.liquidbounce.authlib.utils.generateOfflinePlayerUuid
import net.ccbluex.liquidbounce.authlib.yggdrasil.GameProfileRepository
import net.ccbluex.liquidbounce.authlib.utils.parseUuid
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.string
import java.net.Proxy
import java.util.*

/**
 * A minecraft cracked account - has no password and no access to premium online servers
 */
class CrackedAccount(private val username: String, private var online: Boolean = false)
    : MinecraftAccount("Cracked") {

    /**
     * Used for JSON deserialize.
     */
    @Suppress("unused")
    constructor() : this("", false)

    override fun refresh() {
        val uuid = if (online) {
            runCatching {
                GameProfileRepository().fetchUuidByUsername(username)
            }.getOrNull()
        } else { null } ?: generateOfflinePlayerUuid(username)

        profile = GameProfile(username, uuid)
    }

    override fun login(): Pair<Session, YggdrasilAuthenticationService> {
        if (profile?.uuid == null) {
            refresh()
        }

        val session = profile!!.toSession("-", "legacy")
        val service = YggdrasilAuthenticationService(Proxy.NO_PROXY, YggdrasilEnvironment.PROD.environment)

        return session to service
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = profile!!.username
        if (profile!!.uuid != null) {
            json["uuid"] = profile!!.uuid.toString()
        }
        json["online"] = online
    }

    override fun fromRawJson(json: JsonObject) {
        val name = json.string("name")!!
        val uuid = if (json.has("uuid")) parseUuid(json.string("uuid")!!) else null
        online = if (json.has("online")) json["online"].asBoolean else false
        profile = GameProfile(name, uuid)
    }

}
