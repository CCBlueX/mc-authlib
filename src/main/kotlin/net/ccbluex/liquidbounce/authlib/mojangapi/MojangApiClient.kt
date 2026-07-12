package net.ccbluex.liquidbounce.authlib.mojangapi

import com.google.gson.Gson
import net.ccbluex.liquidbounce.authlib.Authlib
import net.ccbluex.liquidbounce.authlib.interceptor.TokenInterceptor
import net.ccbluex.liquidbounce.authlib.mojangapi.service.MinecraftServicesApi
import net.ccbluex.liquidbounce.authlib.mojangapi.service.MojangApi
import net.ccbluex.liquidbounce.authlib.mojangapi.service.SessionServerApi
import net.ccbluex.liquidbounce.authlib.mojangapi.service.XboxLiveApi
import net.ccbluex.liquidbounce.authlib.mojangapi.service.XstsApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Unified entry point for all Mojang HTTP APIs.
 *
 * Provides typed, suspend-based access to:
 * - [mojangApi] — api.mojang.com (public profile lookups)
 * - [mcServicesApi] — api.minecraftservices.com (authenticated profile management)
 * - [sessionServerApi] — sessionserver.mojang.com (session verification, textures)
 * - [xboxLiveApi] — user.auth.xboxlive.com (Xbox auth)
 * - [xstsApi] — xsts.auth.xboxlive.com (XSTS token exchange)
 *
 * Create via [MojangApiClient.Builder]:
 * ```kotlin
 * val client = MojangApiClient.Builder().build()
 * ```
 */
class MojangApiClient internal constructor(
    val mojangApi: MojangApi,
    val mcServicesApi: MinecraftServicesApi,
    val sessionServerApi: SessionServerApi,
    val xboxLiveApi: XboxLiveApi,
    val xstsApi: XstsApi,
) {
    private companion object {
        private val apiGson by lazy {
            com.google.gson.GsonBuilder()
                .setStrictness(com.google.gson.Strictness.LENIENT)
                .serializeNulls()
                .create()
        }
    }

    class Builder {
        private var gson: Gson? = null

        private var baseHttpClient: OkHttpClient? = null

        private var tokenProvider: () -> String? = { null }

        /** Use a custom [Gson] instance for serialization. */
        fun gson(gson: Gson) = apply { this.gson = gson }

        /** Use a custom base [OkHttpClient]. Defaults to [Authlib.client]. */
        fun httpClient(client: OkHttpClient) = apply { this.baseHttpClient = client }

        /** Change the token provider after construction. */
        fun tokenProvider(provider: () -> String?) = apply { this.tokenProvider = provider }

        fun build(): MojangApiClient {
            val tokenInterceptor = TokenInterceptor(tokenProvider)

            val baseClient = baseHttpClient ?: Authlib.client
            val authenticatedClient = baseClient.newBuilder()
                .addInterceptor(tokenInterceptor)
                .build()

            val converter = GsonConverterFactory.create(gson ?: apiGson)

            fun retrofit(baseUrl: String, client: OkHttpClient = baseClient): Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(converter)
                .build()

            return MojangApiClient(
                mojangApi = retrofit("https://api.mojang.com/")
                    .create(MojangApi::class.java),

                mcServicesApi = retrofit("https://api.minecraftservices.com/", authenticatedClient)
                    .create(MinecraftServicesApi::class.java),

                sessionServerApi = retrofit("https://sessionserver.mojang.com/")
                    .create(SessionServerApi::class.java),

                xboxLiveApi = retrofit("https://user.auth.xboxlive.com/")
                    .create(XboxLiveApi::class.java),

                xstsApi = retrofit("https://xsts.auth.xboxlive.com/")
                    .create(XstsApi::class.java),
            )
        }
    }
}
