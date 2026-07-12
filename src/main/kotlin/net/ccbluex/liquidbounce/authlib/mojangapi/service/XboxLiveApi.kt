package net.ccbluex.liquidbounce.authlib.mojangapi.service

import net.ccbluex.liquidbounce.authlib.mojangapi.model.XboxAuthRequest
import net.ccbluex.liquidbounce.authlib.mojangapi.model.XboxAuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Xbox Live authentication endpoint on [user.auth.xboxlive.com](https://user.auth.xboxlive.com).
 */
interface XboxLiveApi {

    /** Exchange Microsoft access token for Xbox Live token. */
    @POST("user/authenticate")
    suspend fun authenticateWithXbox(@Body request: XboxAuthRequest): XboxAuthResponse
}
