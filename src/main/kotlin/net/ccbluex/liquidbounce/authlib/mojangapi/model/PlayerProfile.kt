package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class PlayerProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("skins") val skins: List<SkinInfo> = emptyList(),
    @SerializedName("capes") val capes: List<CapeInfo> = emptyList(),
)

@JvmRecord
data class SkinInfo(
    @SerializedName("id") val id: String,
    @SerializedName("state") val state: String,
    @SerializedName("url") val url: String,
    @SerializedName("variant") val variant: String,
)

@JvmRecord
data class CapeInfo(
    @SerializedName("id") val id: String,
    @SerializedName("state") val state: String,
    @SerializedName("url") val url: String,
    @SerializedName("alias") val alias: String,
)
