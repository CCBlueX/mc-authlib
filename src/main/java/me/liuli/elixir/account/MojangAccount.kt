package me.liuli.elixir.account

import com.google.gson.JsonObject
import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.exceptions.AuthenticationUnavailableException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import me.liuli.elixir.compat.Session
import me.liuli.elixir.exception.LoginException
import me.liuli.elixir.utils.set
import me.liuli.elixir.utils.string
import java.net.Proxy

@Deprecated("Mojang removed support for MojangAccount")
class MojangAccount : MinecraftAccount("Mojang") {

    override var name = ""
    var email = ""
    var password = ""
    private var uuid = ""
    private var accessToken = ""

    override val session: Session
        get() {
            if(name.isEmpty() || uuid.isEmpty() || accessToken.isEmpty()) {
                update()
            }

            return Session(name, uuid, accessToken, "mojang")
        }

    override fun update() {
        val userAuthentication = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication

        userAuthentication.setUsername(email)
        userAuthentication.setPassword(password)

        try {
            userAuthentication.logIn()
            name = userAuthentication.selectedProfile.name
            uuid = userAuthentication.selectedProfile.id.toString()
            accessToken = userAuthentication.authenticatedToken
        } catch (exception: AuthenticationUnavailableException) {
            throw LoginException("Mojang server is unavailable")
        } catch (exception: AuthenticationException) {
            throw LoginException(exception.message ?: "Unknown error")
        }
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
        json["email"] = email
        json["password"] = password
    }

    override fun fromRawJson(json: JsonObject) {
        name = json.string("name")!!
        email = json.string("email")!!
        password = json.string("password")!!
    }
}