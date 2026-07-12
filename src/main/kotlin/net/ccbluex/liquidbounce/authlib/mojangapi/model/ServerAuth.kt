package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class JoinServerRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("selectedProfile") val selectedProfile: String,
    @SerializedName("serverId") val serverId: String,
)

@JvmRecord
data class BlockList(
    @SerializedName("blockedProfiles") val blockedProfiles: List<String> = emptyList(),
)
