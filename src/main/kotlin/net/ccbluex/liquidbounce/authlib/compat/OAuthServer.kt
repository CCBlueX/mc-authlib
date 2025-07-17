package net.ccbluex.liquidbounce.authlib.compat

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.utils.oauthPort
import java.io.FileNotFoundException
import java.net.InetSocketAddress
import java.util.concurrent.ForkJoinPool

/**
 * Represents an OAuth server for handling authentication process for Microsoft accounts.
 *
 * @param handler The OAuth handler that will handle the authentication result.
 * @param authMethod The authentication method to be used. Default value is `MicrosoftAccount.AuthMethod.AZURE_APP`.
 * @param httpServer The HTTP server to be used. Default value is created on localhost with the given `oauthPort`.
 * @param context The context path for the OAuth redirect HTTP request. Default value is `/login`.
 */
class OAuthServer(
    val handler: MicrosoftAccount.OAuthHandler,
    private val authMethod: MicrosoftAccount.AuthMethod = MicrosoftAccount.AuthMethod.AZURE_APP,
    private val httpServer: HttpServer = HttpServer.create(InetSocketAddress("localhost", oauthPort), 0),
    private val context: String = "/login"
) {

    /**
     * Start the server.
     */
    fun start() {
        httpServer.createContext(context) { exchange ->
            val query = exchange.requestURI.query.split("&").map {
                it.split("=", limit = 2)
            }.associate { it[0] to it[1] }

            val code = query["code"]

            if (code != null) {
                try {
                    handler.authResult(MicrosoftAccount.buildFromAuthCode(code, authMethod))
                    exchange.response(200, "Login Success")
                } catch (e: FileNotFoundException) {
                    val errorMessage =
                        "No Minecraft account associated with this Microsoft account. Please check your account and try again."

                    handler.authError(errorMessage)
                    exchange.response(500, "Error: $errorMessage")
                } catch (e: Exception) {
                    handler.authError(e.toString())
                    exchange.response(500, "Error: $e")
                }
            } else {
                handler.authError("No code in the query")
                exchange.response(500, "No code in the query")
            }
            stop(false)
        }
        httpServer.executor = ForkJoinPool.commonPool()
        httpServer.start()
        handler.openUrl(MicrosoftAccount.replaceKeys(authMethod, MicrosoftAccount.XBOX_PRE_AUTH_URL))
    }

    /**
     * Stop the server.
     */
    fun stop(isInterrupt: Boolean = true) {
        httpServer.stop(0)
        if (isInterrupt) {
            handler.authError("Has been interrupted")
        }
    }

}

private fun HttpExchange.response(code: Int, message: String) {
    val byte = message.toByteArray()
    sendResponseHeaders(code, byte.size.toLong())
    responseBody.write(byte)
    close()
}
