package com.github.exact7.xtra.model.chat

interface ChatMessage {

    val id: String
    val userName: String
    val displayName: String
    val message: String
    var color: String? //needed to set user color, so that adapter doesn't always use random color on configuration change
    val emotes: List<Emote>?
    val badges: List<Badge>?
    val subscriberBadge: SubscriberBadge?
}