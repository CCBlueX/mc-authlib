package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class NameChangeInfo(
    @SerializedName("changedAt") val changedAt: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("nameChangeAllowed") val nameChangeAllowed: Boolean,
)
