package net.ccbluex.liquidbounce.authlib.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that injects `Authorization: Bearer <token>` into every request.
 * The token provider is evaluated lazily on each request, so it can be refreshed without recreating the client.
 */
internal class TokenInterceptor(
    private val tokenProvider: () -> String?,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().header("Authorization") != null) return chain.proceed(chain.request())
        val token = tokenProvider() ?: return chain.proceed(chain.request())
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
