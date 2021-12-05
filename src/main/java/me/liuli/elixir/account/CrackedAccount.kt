package me.liuli.elixir.account

import com.beust.klaxon.JsonObject
import me.liuli.elixir.compat.Session
import java.util.*

class CrackedAccount : MinecraftAccount("Cracked") {
    override var name = "Player"

    override val session: Session
        get() = Session(name, UUID.randomUUID().toString(), "-", "legacy")

    override fun update() {
        // has nothing to update with cracked account
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
    }

    override fun fromRawJson(json: JsonObject) {
        name = json["name"] as String
    }
}
