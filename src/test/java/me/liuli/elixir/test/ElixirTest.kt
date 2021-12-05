package me.liuli.elixir.test

import com.beust.klaxon.JsonObject
import me.liuli.elixir.account.CrackedAccount
import me.liuli.elixir.manage.AccountSerializer

fun main(args: Array<String>) {
    testMojang()
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