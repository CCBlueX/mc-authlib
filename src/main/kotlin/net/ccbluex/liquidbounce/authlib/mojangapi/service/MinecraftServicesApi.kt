package net.ccbluex.liquidbounce.authlib.mojangapi.service

import net.ccbluex.liquidbounce.authlib.mojangapi.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Endpoints on [api.minecraftservices.com](https://api.minecraftservices.com).
 * Most endpoints require `Authorization: Bearer <token>` header.
 */
interface MinecraftServicesApi {

    // ── Profile lookup (no auth) ──

    @GET("minecraft/profile/lookup/name/{username}")
    suspend fun lookupUuidByName(@Path("username") username: String): ProfileIdName

    @GET("minecraft/profile/lookup/{uuid}")
    suspend fun lookupNameByUuid(@Path("uuid") uuid: String): ProfileIdName

    @POST("minecraft/profile/lookup/bulk/byname")
    suspend fun lookupProfilesBulk(@Body names: List<String>): List<ProfileIdName>

    // ── Authentication ──

    @POST("authentication/login_with_xbox")
    suspend fun loginWithXbox(@Body identityToken: Map<String, String>): MinecraftAuthResponse

    @GET("entitlements/license")
    suspend fun checkEntitlements(
        @Query("requestId") requestId: String = java.util.UUID.randomUUID().toString(),
    ): Response<EntitlementsResponse>

    // ── Player profile & attributes ──

    @GET("minecraft/profile")
    suspend fun fetchProfile(): PlayerProfile

    @GET("player/attributes")
    suspend fun fetchAttributes(): PlayerAttributes

    @POST("player/attributes")
    suspend fun updateAttributes(@Body update: PlayerAttributesUpdate): PlayerAttributes

    // ── Privacy & certificates ──

    @GET("privacy/blocklist")
    suspend fun getBlockList(): BlockList

    @POST("player/certificates")
    suspend fun getCertificates(): PlayerCertificates

    // ── Name management ──

    @GET("minecraft/profile/namechange")
    suspend fun getNameChangeInfo(): NameChangeInfo

    @GET("minecraft/profile/name/{name}/available")
    suspend fun checkNameAvailability(@Path("name") name: String): NameAvailability

    @PUT("minecraft/profile/name/{name}")
    suspend fun changeName(@Path("name") newName: String): PlayerProfile

    // ── Skin management ──

    @POST("minecraft/profile/skins")
    suspend fun changeSkin(@Body request: ChangeSkinRequest): PlayerProfile

    @Multipart
    @POST("minecraft/profile/skins")
    suspend fun uploadSkin(
        @Part("variant") variant: RequestBody,
        @Part file: MultipartBody.Part,
    ): PlayerProfile

    @DELETE("minecraft/profile/skins/active")
    suspend fun resetSkin(): PlayerProfile

    // ── Cape management ──

    @DELETE("minecraft/profile/capes/active")
    suspend fun hideCape(): PlayerProfile

    @PUT("minecraft/profile/capes/active")
    suspend fun showCape(@Body request: ActivateCapeRequest): PlayerProfile

    // ── Gift code ──

    @GET("productvoucher/giftcode")
    suspend fun checkGiftCode(): Response<Unit>

    // ── Friends ──

    @GET("friends")
    suspend fun getFriends(
        @Header("If-None-Match") etag: String? = null,
    ): Response<FriendsList>

    @PUT("friends")
    suspend fun updateFriend(@Body request: FriendsUpdateRequest): Response<FriendsList>

    // ── Presence ──

    @POST("presence")
    suspend fun reportPresence(@Body request: PresenceRequest): PresenceResponse

    // ── Public keys ──

    @GET("publickeys")
    suspend fun getPublicKeys(): PublicKeys
}
