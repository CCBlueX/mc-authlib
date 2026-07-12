package net.ccbluex.liquidbounce.authlib.mojangapi.model

import com.google.gson.annotations.SerializedName

@JvmRecord
data class PlayerAttributesUpdate(
    @SerializedName("profanityFilterPreferences") val profanityFilterPreferences: ProfanityFilterPreferences? = null,
    @SerializedName("friendsPreferences") val friendsPreferences: FriendsPreferences? = null,
)
