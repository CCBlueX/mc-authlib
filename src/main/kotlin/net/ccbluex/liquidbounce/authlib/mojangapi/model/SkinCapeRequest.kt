package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class ChangeSkinRequest(
    @SerializedName("variant") val variant: String,
    @SerializedName("url") val url: String,
)

@JvmRecord
data class ActivateCapeRequest(
    @SerializedName("capeId") val capeId: String,
)
