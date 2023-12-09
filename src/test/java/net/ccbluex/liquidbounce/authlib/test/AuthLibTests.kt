package net.ccbluex.liquidbounce.authlib.test

import com.google.gson.JsonObject
import net.ccbluex.liquidbounce.authlib.account.AlteningAccount
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.manage.AccountSerializer
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.toJsonString

fun main(args: Array<String>) {
    testCracked()
    testAltening()
    testMicrosoftBrowser(MicrosoftAccount.AuthMethod.AZURE_APP)
}

private fun testCracked() {
    val name = "1zun4"

    println("--- Cracked Account Dynamic ---")
    var crackedAccount = AccountSerializer.accountInstance(name)
    crackedAccount.refresh()
    println(crackedAccount.login())
    println(AccountSerializer.toJson(crackedAccount).toJsonString())

    println("--- Cracked Account Static ---")
    crackedAccount = AccountSerializer.fromJson(JsonObject().also {
        it["type"] = "net.ccbluex.liquidbounce.authlib.account.types.CrackedAccount"
        it["name"] = name
    })
    crackedAccount.refresh()
    println(crackedAccount.login())
    println(AccountSerializer.toJson(crackedAccount).toJsonString())
}

fun testMicrosoftBrowser(authMethod: MicrosoftAccount.AuthMethod) {
    val microsoftAccount = MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
        override fun openUrl(url: String) {
            println("Open url: $url")
        }

        override fun authResult(account: MicrosoftAccount) {
            println("Auth result: ${account.login()}")
            println(AccountSerializer.toJson(account).toJsonString(prettyPrint = true))
        }

        override fun authError(error: String) {
            println("Auth error: $error")
        }
    }, authMethod)
}

private fun testAltening() {
    val apiToken = ""
    val accountToken = ""

    println("--- Altening Account Token ---")

    val alteningAccount2 = AlteningAccount.fromToken(accountToken)
    println(alteningAccount2.login())
    println(AccountSerializer.toJson(alteningAccount2).toJsonString())

    println("--- Altening Account API ---")
    val alteningAccount = AlteningAccount.generateAccount(apiToken)
    println(alteningAccount.login())
    println(AccountSerializer.toJson(alteningAccount).toJsonString())

}