package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class FriendsList(
    @SerializedName("friends") val friends: List<FriendInfo> = emptyList(),
    @SerializedName("incomingRequests") val incomingRequests: List<FriendInfo> = emptyList(),
    @SerializedName("outgoingRequests") val outgoingRequests: List<FriendInfo> = emptyList(),
    @SerializedName("empty") val empty: Boolean,
)

@JvmRecord
data class FriendInfo(
    @SerializedName("profileId") val profileId: String,
    @SerializedName("name") val name: String,
)

@JvmRecord
data class FriendsUpdateRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("profileId") val profileId: String? = null,
    @SerializedName("updateType") val updateType: String,
)
