package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class PlayerCertificates(
    @SerializedName("keyPair") val keyPair: KeyPairInfo,
    @SerializedName("publicKeySignature") val publicKeySignature: String,
    @SerializedName("publicKeySignatureV2") val publicKeySignatureV2: String,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("refreshedAfter") val refreshedAfter: String,
)

@JvmRecord
data class KeyPairInfo(
    @SerializedName("privateKey") val privateKey: String,
    @SerializedName("publicKey") val publicKey: String,
)
