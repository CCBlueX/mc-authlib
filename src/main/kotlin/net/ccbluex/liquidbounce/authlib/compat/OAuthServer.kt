package net.ccbluex.liquidbounce.authlib.compat

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import net.ccbluex.liquidbounce.authlib.account.MicrosoftAccount
import net.ccbluex.liquidbounce.authlib.utils.oauthPort
import java.io.FileNotFoundException
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * Represents an OAuth server for handling authentication process for Microsoft accounts.
 *
 * @param handler The OAuth handler that will handle the authentication result.
 * @param authMethod The authentication method to be used. Default value is `MicrosoftAccount.AuthMethod.AZURE_APP`.
 * @param httpServer The HTTP server to be used. Default value is created on localhost with the given `oauthPort`.
 * @param context The context path for the OAuth redirect HTTP request. Default value is `/login`.
 */
class OAuthServer(val handler: MicrosoftAccount.OAuthHandler,
                  private val authMethod: MicrosoftAccount.AuthMethod = MicrosoftAccount.AuthMethod.AZURE_APP,
                  private val httpServer: HttpServer = HttpServer.create(InetSocketAddress("localhost", oauthPort), 0),
                  private val context: String = "/login") {
    private val threadPoolExecutor = Executors.newFixedThreadPool(10) as ThreadPoolExecutor

    /**
     * Start the server.
     */
    fun start() {
        httpServer.createContext(context, OAuthHttpHandler(this, authMethod))
        httpServer.executor = threadPoolExecutor
        httpServer.start()
        handler.openUrl(MicrosoftAccount.replaceKeys(authMethod, MicrosoftAccount.XBOX_PRE_AUTH_URL))
    }

    /**
     * Stop the server.
     */
    fun stop(isInterrupt: Boolean = true) {
        httpServer.stop(0)
        threadPoolExecutor.shutdown()
        if (isInterrupt) {
            handler.authError("Has been interrupted")
        }
    }

    /**
     * The handler of the OAuth redirect http request.
     */
    class OAuthHttpHandler(private val server: OAuthServer, private val authMethod: MicrosoftAccount.AuthMethod) : HttpHandler {

        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query.split("&").map {
                it.split("=")
            }.associate {
                it[0] to it[1]
            }

            if (query.containsKey("code")) {
                try {
                    server.handler.authResult(MicrosoftAccount.buildFromAuthCode(query["code"]!!, authMethod))
                    response(exchange, "Login Success", 200)
                } catch (e: FileNotFoundException) {
                    val errorMessage = "No minecraft account associated with this Microsoft account. Please check your account and try again."

                    server.handler.authError(errorMessage)
                    response(exchange, "Error: $errorMessage", 500)
                } catch (e: Exception) {
                    server.handler.authError(e.toString())
                    response(exchange, "Error: $e", 500)
                }
            } else {
                server.handler.authError("No code in the query")
                response(exchange, "No code in the query", 500)
            }
            server.stop(false)
        }

        private fun response(exchange: HttpExchange, message: String, code: Int) {
            val byte = message.toByteArray()
            exchange.sendResponseHeaders(code, byte.size.toLong())
            exchange.responseBody.write(byte)
            exchange.close()
        }
    }
}