package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class NameAvailability(
    @SerializedName("status") val status: String,
)
