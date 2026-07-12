package net.ccbluex.liquidbounce.authlib.mojangapi

import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import net.ccbluex.liquidbounce.authlib.interceptor.TokenInterceptor
import net.ccbluex.liquidbounce.authlib.mojangapi.model.JoinInfo
import net.ccbluex.liquidbounce.authlib.mojangapi.model.PlayerAttributes
import net.ccbluex.liquidbounce.authlib.mojangapi.model.PresenceResponse
import net.ccbluex.liquidbounce.authlib.mojangapi.service.MinecraftServicesApi
import net.ccbluex.liquidbounce.authlib.mojangapi.service.SessionServerApi
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MojangApiTest {

    private val gson = GsonBuilder().serializeNulls().create()

    @Test
    fun `player privilege uses enable field`() {
        val attributes = gson.fromJson(
            """{
                "privileges": {
                    "onlineChat": {"enable": true},
                    "multiplayerServer": {"enable": true},
                    "multiplayerRealms": {"enable": false},
                    "telemetry": {"enable": true},
                    "optionalTelemetry": {"enable": false}
                },
                "profanityFilterPreferences": {"profanityFilterOn": false},
                "banStatus": {}
            }""",
            PlayerAttributes::class.java,
        )

        assertTrue(attributes.privileges.onlineChat.enabled)
        assertTrue(attributes.privileges.multiplayerServer.enabled)
    }

    @Test
    fun `presence response matches documented shape`() {
        val response = gson.fromJson(
            """{
                "presence": [{
                    "profileId": "profile-id",
                    "pmid": "pmid",
                    "status": "PLAYING_HOSTED_SERVER",
                    "joinInfo": {"value": "pmid", "invited": true},
                    "lastUpdated": "2026-05-15T19:26:12Z"
                }]
            }""",
            PresenceResponse::class.java,
        )

        assertEquals("profile-id", response.presence.single().profileId)
        assertTrue(response.presence.single().joinInfo!!.invited)
    }

    @Test
    fun `presence request supports numeric and explicit null values`() {
        val numeric = JsonParser.parseString(gson.toJson(JoinInfo(JsonPrimitive(42)))).asJsonObject
        val explicitNull = JsonParser.parseString(gson.toJson(JoinInfo())).asJsonObject

        assertEquals(42, numeric["value"].asInt)
        assertTrue(explicitNull.has("value"))
        assertTrue(explicitNull["value"].isJsonNull)
    }

    @Test
    fun `token interceptor preserves explicit authorization`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse())
            val client = OkHttpClient.Builder()
                .addInterceptor(TokenInterceptor { "minecraft-token" })
                .build()
            val request = Request.Builder()
                .url(server.url("/"))
                .header("Authorization", "Custom token")
                .build()

            client.newCall(request).execute().close()

            assertEquals("Custom token", server.takeRequest().headers["Authorization"])
        }
    }

    @Test
    fun `scalar and empty responses are handled`() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("abc\ndef\n"))
            server.enqueue(MockResponse().setResponseCode(204))
            val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val sessionApi = retrofit.create(SessionServerApi::class.java)
            val servicesApi = retrofit.create(MinecraftServicesApi::class.java)

            assertEquals("abc\ndef\n", sessionApi.getBlockedServers())
            assertNull(servicesApi.checkEntitlements("request-id").body())
        }
    }

    @Test
    fun `skin upload uses png multipart request`() = runBlocking {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("""{"id":"id","name":"name","skins":[],"capes":[]}"""))
            val api = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MinecraftServicesApi::class.java)
            val png = byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47)
            val file = MultipartBody.Part.createFormData(
                "file",
                "skin.png",
                png.toRequestBody("image/png".toMediaType()),
            )

            api.uploadSkin("slim".toRequestBody("text/plain".toMediaType()), file)

            val request = server.takeRequest()
            val body = request.body.readUtf8()
            assertEquals("/minecraft/profile/skins", request.requestUrl!!.encodedPath)
            assertTrue(request.headers["Content-Type"]!!.startsWith("multipart/form-data; boundary="))
            assertTrue(body.contains("name=\"variant\""))
            assertTrue(body.contains("slim"))
            assertTrue(body.contains("name=\"file\"; filename=\"skin.png\""))
            assertTrue(body.contains("Content-Type: image/png"))
        }
    }
}
