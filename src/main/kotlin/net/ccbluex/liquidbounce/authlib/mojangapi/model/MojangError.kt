package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class MojangError(
    @SerializedName("error") val error: String,
    @SerializedName("errorMessage") val errorMessage: String,
    @SerializedName("cause") val cause: String? = null,
)
