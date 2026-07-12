package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.annotations.SerializedName

@JvmRecord
data class PresenceRequest(
    @SerializedName("status") val status: String,
    @SerializedName("joinInfo") val joinInfo: JoinInfo? = null,
)

@JvmRecord
data class JoinInfo(
    @SerializedName("value") val value: JsonElement = JsonNull.INSTANCE,
    @SerializedName("invites") val invites: List<String>? = null,
)

@JvmRecord
data class PresenceResponse(
    @SerializedName("presence") val presence: List<FriendPresence> = emptyList(),
)

@JvmRecord
data class FriendPresence(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("pmid") val pmid: String,
    @SerializedName("status") val status: String,
    @SerializedName("joinInfo") val joinInfo: FriendJoinInfo? = null,
    @SerializedName("lastUpdated") val lastUpdated: String,
)

@JvmRecord
data class FriendJoinInfo(
    @SerializedName("value") val value: String? = null,
    @SerializedName("invited") val invited: Boolean,
)
