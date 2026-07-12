package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

// Xbox Live authenticate request
@JvmRecord
data class XboxAuthRequest(
    @SerializedName("Properties") val properties: XboxAuthProperties,
    @SerializedName("RelyingParty") val relyingParty: String = "http://auth.xboxlive.com",
    @SerializedName("TokenType") val tokenType: String = "JWT",
)

@JvmRecord
data class XboxAuthProperties(
    @SerializedName("AuthMethod") val authMethod: String = "RPS",
    @SerializedName("SiteName") val siteName: String = "user.auth.xboxlive.com",
    @SerializedName("RpsTicket") val rpsTicket: String,
)

// Xbox Live authenticate response
@JvmRecord
data class XboxAuthResponse(
    @SerializedName("Token") val token: String,
    @SerializedName("DisplayClaims") val displayClaims: XboxDisplayClaims,
    @SerializedName("IssueInstant") val issueInstant: String,
    @SerializedName("NotAfter") val notAfter: String,
)

@JvmRecord
data class XboxDisplayClaims(
    @SerializedName("xui") val xui: List<XboxUserInfo>,
)

@JvmRecord
data class XboxUserInfo(
    @SerializedName("uhs") val uhs: String,
)

// XSTS authorize request
@JvmRecord
data class XstsAuthRequest(
    @SerializedName("Properties") val properties: XstsAuthProperties,
    @SerializedName("RelyingParty") val relyingParty: String = "rp://api.minecraftservices.com/",
    @SerializedName("TokenType") val tokenType: String = "JWT",
)

@JvmRecord
data class XstsAuthProperties(
    @SerializedName("SandboxId") val sandboxId: String = "RETAIL",
    @SerializedName("UserTokens") val userTokens: List<String>,
)

// XSTS authorize response - same shape as XboxAuthResponse, response reuses the same type

// Minecraft login response
@JvmRecord
data class MinecraftAuthResponse(
    @SerializedName("username") val username: String,
    @SerializedName("roles") val roles: List<Any> = emptyList(),
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
)

// Minecraft license/entitlements
@JvmRecord
data class EntitlementsResponse(
    @SerializedName("items") val items: List<EntitlementItem> = emptyList(),
    @SerializedName("signature") val signature: String? = null,
    @SerializedName("keyId") val keyId: String? = null,
)

@JvmRecord
data class EntitlementItem(
    @SerializedName("name") val name: String,
)
