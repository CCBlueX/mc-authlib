package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class PlayerAttributes(
    @SerializedName("privileges") val privileges: Privileges,
    @SerializedName("profanityFilterPreferences") val profanityFilterPreferences: ProfanityFilterPreferences,
    @SerializedName("friendsPreferences") val friendsPreferences: FriendsPreferences? = null,
    @SerializedName("chatPreferences") val chatPreferences: ChatPreferences? = null,
    @SerializedName("banStatus") val banStatus: BanStatus,
)

@JvmRecord
data class Privileges(
    @SerializedName("onlineChat") val onlineChat: TogglePreference,
    @SerializedName("multiplayerServer") val multiplayerServer: TogglePreference,
    @SerializedName("multiplayerRealms") val multiplayerRealms: TogglePreference,
    @SerializedName("telemetry") val telemetry: TogglePreference,
    @SerializedName("optionalTelemetry") val optionalTelemetry: TogglePreference,
)

@JvmRecord
data class TogglePreference(
    @SerializedName("enable") val enabled: Boolean,
)

@JvmRecord
data class ProfanityFilterPreferences(
    @SerializedName("profanityFilterOn") val profanityFilterOn: Boolean,
)

@JvmRecord
data class FriendsPreferences(
    @SerializedName("friends") val friends: String,
    @SerializedName("acceptInvites") val acceptInvites: String,
)

@JvmRecord
data class ChatPreferences(
    @SerializedName("textCommunication") val textCommunication: String,
)

@JvmRecord
data class BanStatus(
    @SerializedName("bannedScopes") val bannedScopes: BannedScopes? = null,
)

@JvmRecord
data class BannedScopes(
    @SerializedName("MULTIPLAYER") val multiplayer: BanDetail? = null,
)

@JvmRecord
data class BanDetail(
    @SerializedName("banId") val banId: String,
    @SerializedName("expires") val expires: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("reasonMessage") val reasonMessage: String? = null,
)
