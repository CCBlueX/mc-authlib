package me.liuli.elixir.test

import com.google.gson.JsonObject
import me.liuli.elixir.account.MicrosoftAccount
import me.liuli.elixir.manage.AccountSerializer
import me.liuli.elixir.utils.set
import me.liuli.elixir.utils.toJsonString

fun main(args: Array<String>) {
    testCracked()
    testMojang()
    val custom = MicrosoftAccount.AuthMethod("c6cd7b0f-077d-4fcf-ab5c-9659576e38cb", "vI87Q~GkhVHJSLN5WKBbEKbK0TJc9YRDyOYc5", "http://localhost:1919/login", "XboxLive.signin%20offline_access", "d=<access_token>").also {
        MicrosoftAccount.AuthMethod.Companion.registry["CUSTOM"] = it
    }
    testMicrosoftBrowser(custom)
    testMicrosoftDirect() // you can only use microsoft official key to login with direct mode
}

private fun testCracked() {
    val name = "Liulihaocai"

    println("--- Cracked Account Dynamic ---")
    var crackedAccount = AccountSerializer.accountInstance(name, "")
    crackedAccount.update()
    println(crackedAccount.session)
    println(AccountSerializer.toJson(crackedAccount).toJsonString())

    println("--- Cracked Account Static ---")
    crackedAccount = AccountSerializer.fromJson(JsonObject().also {
        it["type"] = "me.liuli.elixir.account.CrackedAccount"
        it["name"] = name
    })
    crackedAccount.update()
    println(crackedAccount.session)
    println(AccountSerializer.toJson(crackedAccount).toJsonString())
}

private fun testMojang() {
    val name = "" // email of your Mojang account
    val password = ""

    println("--- Mojang Account Dynamic ---")
    var mojangAccount = AccountSerializer.accountInstance(name, password)
    mojangAccount.update()
    println(mojangAccount.session)
    println(AccountSerializer.toJson(mojangAccount).toJsonString())

    println("--- Mojang Account Static ---")
    mojangAccount = AccountSerializer.fromJson(JsonObject().also {
        it["type"] = "me.liuli.elixir.account.MojangAccount"
        it["name"] = name
        it["password"] = password
    })
    mojangAccount.update()
    println(mojangAccount.session)
    println(AccountSerializer.toJson(mojangAccount).toJsonString())
}

fun testMicrosoftDirect() {
    val mail = ""
    val password = ""

    println("--- Microsoft Account Dynamic ---")
    var microsoftAccount = AccountSerializer.accountInstance("ms@$mail", password)
    // it will update when login
    //microsoftAccount.update()
    println(microsoftAccount.session)
    println(AccountSerializer.toJson(microsoftAccount).toJsonString())

    println("--- Microsoft Account Static ---")
    microsoftAccount = AccountSerializer.fromJson(AccountSerializer.toJson(microsoftAccount))
    microsoftAccount.update()
    println(microsoftAccount.session)
    println(AccountSerializer.toJson(microsoftAccount).toJsonString())
}

fun testMicrosoftBrowser(authMethod: MicrosoftAccount.AuthMethod) {
    val microsoftAccount = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
        override fun openUrl(url: String) {
            println("Open url: $url")
        }

        override fun authResult(account: MicrosoftAccount) {
            println("Auth result: ${account.session}")
            println(AccountSerializer.toJson(account).toJsonString(prettyPrint = true))
        }

        override fun authError(error: String) {
            println("Auth error: $error")
        }
    }, authMethod)
}