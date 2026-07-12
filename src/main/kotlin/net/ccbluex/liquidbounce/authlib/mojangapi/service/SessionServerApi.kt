package net.ccbluex.liquidbounce.authlib.mojangapi.service

import net.ccbluex.liquidbounce.authlib.mojangapi.model.JoinServerRequest
import net.ccbluex.liquidbounce.authlib.mojangapi.model.SessionProfile
import retrofit2.Response
import retrofit2.http.*

/**
 * Endpoints on [sessionserver.mojang.com](https://sessionserver.mojang.com).
 */
interface SessionServerApi {

    /** Query skin and cape textures by UUID. Returns 204 if UUID has no player. */
    @GET("session/minecraft/profile/{uuid}")
    suspend fun fetchProfile(@Path("uuid") uuid: String): Response<SessionProfile>

    /** Query skin and cape with signature verification. */
    @GET("session/minecraft/profile/{uuid}")
    suspend fun fetchProfileSigned(
        @Path("uuid") uuid: String,
        @Query("unsigned") unsigned: Boolean = false,
    ): Response<SessionProfile>

    /** Client-side session verification. Returns 204 on success. */
    @POST("session/minecraft/join")
    suspend fun joinServer(@Body request: JoinServerRequest): Response<Unit>

    /** Server-side session verification. Returns profile on success, 204/empty on failure. */
    @GET("session/minecraft/hasJoined")
    suspend fun hasJoined(
        @Query("username") username: String,
        @Query("serverId") serverId: String,
        @Query("ip") ip: String? = null,
    ): Response<SessionProfile>

    /** Get list of SHA-1 hashes of blocked servers, one per line. */
    @GET("blockedservers")
    suspend fun getBlockedServers(): String
}
