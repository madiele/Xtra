package com.github.exact7.xtra.model.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoChatMessage(
        @SerializedName("_id")
        override val id: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("channel_id")
        val channelId: String,
        @SerializedName("content_type")
        val contentType: String,
        @SerializedName("content_id")
        val contentId: String,
        @SerializedName("content_offset_seconds")
        val contentOffsetSeconds: Double,
        val commenter: Commenter,
        val source: String,
        val state: String,
        val messageObj: Message,
        @SerializedName("more_replies")
        val moreReplies: Boolean) : ChatMessage, Parcelable {

    override val userName: String //TODO
        get() = commenter.name

    override val message: String
        get() = messageObj.body

    override var color: String?
        get() = messageObj.userColor
        set(_) {}

    override val emotes: List<Emote>?
        get() = messageObj.emoticons

    override val badges: List<Badge>?
        get() = messageObj.userBadges

    override val subscriberBadge: SubscriberBadge?
        get() = null

    override val displayName: String
        get() = commenter.displayName

    @Parcelize
    data class Commenter(
            @SerializedName("display_name")
            val displayName: String,
            @SerializedName("_id")
            val id: String,
            val name: String,
            val type: String,
            val bio: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            val logo: String) : Parcelable

    @Parcelize
    data class Message(
            val body: String,
            val emoticons: List<Emote>?,
            val fragments: List<Fragment>,
            @SerializedName("is_action")
            val isAction: Boolean,
            @SerializedName("user_badges")
            val userBadges: List<Badge>?,
            @SerializedName("user_color")
            val userColor: String) : Parcelable {

        @Parcelize
        data class Fragment(val text: String, val emoticon: Emoticon) : Parcelable {
            @Parcelize
            data class Emoticon(
                    @SerializedName("emoticon_id")
                    val emoticonId: String,
                    @SerializedName("emoticon_set_id")
                    val emoticonSetId: String) : Parcelable
        }
    }
}