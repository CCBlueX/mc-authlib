package me.liuli.elixir.account

import com.google.gson.JsonObject
import me.liuli.elixir.compat.Session
import me.liuli.elixir.utils.set
import me.liuli.elixir.utils.string
import java.util.*

class CrackedAccount : MinecraftAccount("Cracked") {
    override var name = ""

    override val session: Session
        get() = Session(name, UUID.nameUUIDFromBytes(name.toByteArray(Charsets.UTF_8)).toString(), "-", "legacy")

    override fun update() {
        // has nothing to update with cracked account
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
    }

    override fun fromRawJson(json: JsonObject) {
        name = json.string("name")!!
    }
}
