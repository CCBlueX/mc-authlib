package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class SessionProfile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("legacy") val legacy: Boolean? = null,
    @SerializedName("properties") val properties: List<TextureProperty> = emptyList(),
)

@JvmRecord
data class TextureProperty(
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: String,
    @SerializedName("signature") val signature: String? = null,
)
