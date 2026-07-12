package net.ccbluex.liquidbounce.authlib.mojangapi.service

import net.ccbluex.liquidbounce.authlib.mojangapi.model.ProfileIdName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Endpoints on [api.mojang.com](https://api.mojang.com).
 */
interface MojangApi {

    /** Username → UUID. Returns 404 if no player exists. */
    @GET("users/profiles/minecraft/{username}")
    suspend fun fetchUuidByUsername(@Path("username") username: String): ProfileIdName

    /** Username → UUID (alternative endpoint). */
    @GET("minecraft/profile/lookup/name/{username}")
    suspend fun lookupUuidByName(@Path("username") username: String): ProfileIdName

    /** Batch username → UUID, max 10 names. Missing names are omitted from response. */
    @POST("profiles/minecraft")
    suspend fun fetchProfiles(@Body names: List<String>): List<ProfileIdName>

    /** Batch username → UUID (alternative endpoint). */
    @POST("minecraft/profile/lookup/bulk/byname")
    suspend fun lookupProfilesBulk(@Body names: List<String>): List<ProfileIdName>
}
