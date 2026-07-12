package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class PublicKeys(
    @SerializedName("profilePropertyKeys") val profilePropertyKeys: List<String>,
    @SerializedName("playerCertificateKeys") val playerCertificateKeys: List<String>,
    @SerializedName("authenticationKeys") val authenticationKeys: List<String>? = null,
)
