package net.ccbluex.liquidbounce.authlib.utils

import com.google.gson.Gson
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * A utility class for making HTTP requests.
 */
internal object HttpUtils {

    private const val DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36"

    /**
     * Create an HTTP connection.
     *
     * @param url URL to connect to
     * @param method HTTP method to use
     * @param data Data to send
     * @param header HTTP header to send
     * @param agent User agent to use
     * @return The created HTTP connection
     */
    fun make(url: String, method: String, data: String = "", header: Map<String, String> = emptyMap(), agent: String = DEFAULT_AGENT): HttpURLConnection {
        val httpConnection = URL(url).openConnection() as HttpURLConnection

        httpConnection.requestMethod = method
        httpConnection.connectTimeout = 2000
        httpConnection.readTimeout = 10000

        httpConnection.setRequestProperty("User-Agent", agent)
        header.forEach { (key, value) -> httpConnection.setRequestProperty(key, value) }

        httpConnection.instanceFollowRedirects = true
        httpConnection.doOutput = true

        if (data.isNotEmpty()) {
            val dataOutputStream = DataOutputStream(httpConnection.outputStream)
            dataOutputStream.writeBytes(data)
            dataOutputStream.flush()
        }

        httpConnection.connect()

        return httpConnection
    }

    /**
     * Make an HTTP request.
     *
     * @param url URL to connect to
     * @param method HTTP method to use
     * @param data Data to send
     * @param header HTTP header to send
     * @param agent User agent to use
     * @return The response code and response body
     */
    fun request(url: String, method: String, data: String = "", header: Map<String, String> = emptyMap(),
                agent: String = DEFAULT_AGENT
    ): Pair<Int, String> {
        val connection = make(url, method, data, header, agent)

        // Check which stream to read depending on the status code
        val responseCode = connection.responseCode
        val stream = if (responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        return responseCode to stream.reader().readText()
    }

    fun get(url: String, header: Map<String, String> = emptyMap()) =
        request(url, "GET", header = header)

    fun post(url: String, data: String, header: Map<String, String> = emptyMap()) =
        request(url, "POST", data, header)

    inline fun <reified T: GsonDeserializable> post(url: String, data: GsonSerializable): T {
        val (_, text) = post(url, GSON.toJson(data), mapOf("Content-Type" to "application/json"))
        return decode(text)
    }

    inline fun <reified T: GsonDeserializable, reified E: GsonDeserializable>
            postWithFallback(url: String, data: GsonSerializable): Pair<T?, E?> {
        val (code, text) = post(url, GSON.toJson(data), mapOf("Content-Type" to "application/json"))

        return if (code == 200) {
            Pair(decode<T>(text), null)
        } else {
            Pair(null, decode<E>(text))
        }
    }
    
}

interface GsonSerializable

interface GsonDeserializable