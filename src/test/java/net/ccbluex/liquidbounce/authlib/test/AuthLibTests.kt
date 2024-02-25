package net.ccbluex.liquidbounce.authlib.test

import com.google.gson.JsonObject
import net.ccbluex.liquidbounce.authlib.account.AlteningAccount
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.account.SessionAccount
import net.ccbluex.liquidbounce.authlib.bantracker.Ban
import net.ccbluex.liquidbounce.authlib.manage.AccountSerializer
import net.ccbluex.liquidbounce.authlib.utils.set
import net.ccbluex.liquidbounce.authlib.utils.toJsonString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthLibTests {

    @Test
    fun testBans() {
        val crackedAccount = AccountSerializer.accountInstance("1zun4")
        crackedAccount.refresh()

        crackedAccount.trackBan(Ban("hypixel.net", "You are banned!", -1))
        crackedAccount.trackBan(Ban("mineplex.com", "You are banned!", 0))

        val json = AccountSerializer.toJson(crackedAccount)
        println(json.toJsonString(prettyPrint = true))

        val account = AccountSerializer.fromJson(json)
        println(account.bans)

        assertEquals(account.bans.size, 2)
    }

    @Test
    fun testCracked() {
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

        val (session, _) = crackedAccount.login()
        println(session)
        println(AccountSerializer.toJson(crackedAccount).toJsonString())

        assertTrue(true)
    }

    @Test
    fun testMicrosoftBrowser() {
        MicrosoftAccount.buildFromOpenBrowser(object : MicrosoftAccount.OAuthHandler {
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
        }, MicrosoftAccount.AuthMethod.AZURE_APP)

        assertTrue(true)
    }

    @Test
    fun testAltening() {
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

        assertTrue(true)
    }

    @Test
    fun testSession() {
        val sessionToken = ""

        val sessionAccount = SessionAccount.fromToken(sessionToken)
        println(sessionAccount.login())
        println(AccountSerializer.toJson(sessionAccount).toJsonString())

        assertTrue(true)
    }

}
