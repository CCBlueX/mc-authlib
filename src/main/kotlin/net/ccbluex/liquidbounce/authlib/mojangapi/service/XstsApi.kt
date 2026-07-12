package net.ccbluex.liquidbounce.authlib.mojangapi.service

import net.ccbluex.liquidbounce.authlib.mojangapi.model.XboxAuthResponse
import net.ccbluex.liquidbounce.authlib.mojangapi.model.XstsAuthRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * XSTS token exchange endpoint on [xsts.auth.xboxlive.com](https://xsts.auth.xboxlive.com).
 */
interface XstsApi {

    @POST("xsts/authorize")
    suspend fun authorizeWithXsts(@Body request: XstsAuthRequest): XboxAuthResponse
}
