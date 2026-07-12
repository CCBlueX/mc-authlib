package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class ProfileIdName(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("legacy") val legacy: Boolean? = null,
    @SerializedName("demo") val demo: Boolean? = null,
)
