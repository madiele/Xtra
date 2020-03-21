package com.github.exact7.xtra.ui.player

import com.github.exact7.xtra.model.chat.VideoChatMessage
import com.github.exact7.xtra.repository.TwitchService
import com.github.exact7.xtra.util.chat.OnChatMessageReceivedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.math.max

class ChatReplayManager @Inject constructor(
        private val repository: TwitchService,
        private val videoId: String,
        private val startTime: Double,
        private val currentPosition: () -> Double,
        private val messageListener: OnChatMessageReceivedListener,
        private val clearMessages: () -> Unit,
        private val coroutineScope: CoroutineScope) {

    private lateinit var job: Job
    private val timer: Timer
    private var cursor: String? = null
    private val list = LinkedList<VideoChatMessage>()
    private var isLoading = false

    init {
        load(startTime)
        var lastCheckedPosition = 0.0
        timer = fixedRateTimer(period = 1000L, action = {
            val position = currentPosition()
            if (position - lastCheckedPosition !in 0.0..20.0) {
                job.cancel()
                list.clear()
                clearMessages()
                load(startTime + position)
            }
            lastCheckedPosition = position
        })
    }

    fun stop() {
        job.cancel()
        timer.cancel()
    }

    private fun load(offset: Double) {
        job = coroutineScope.launch {
            try {
                isLoading = true
                val log = repository.loadVideoChatLog(videoId, offset)
                isLoading = false
                list.addAll(log.messages)
                cursor = log.next
                while (isActive) {
                    val message: VideoChatMessage? = try {
                        list.poll()
                    } catch (e: NoSuchElementException) { //wtf?
                        null
                    }
                    if (message != null) {
                        val messageOffset = message.contentOffsetSeconds
                        var position: Double
                        while ((currentPosition() + startTime).also { p -> position = p } < messageOffset) {
                            delay(max((messageOffset - position) * 1000.0, 0.0).toLong())
                        }
                        if (position - messageOffset < 20.0) {
                            messageListener.onMessage(message)
                            if (list.size == 15) {
                                loadNext()
                            }
                        }
                    } else if (isLoading) {
                        delay(1000L)
                    } else if (cursor == null) {
                        break
                    }
                }
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    private fun loadNext() {
        cursor?.let { c ->
            job = coroutineScope.launch {
                try {
                    isLoading = true
                    val log = repository.loadVideoChatAfter(videoId, c)
                    list.addAll(log.messages)
                    cursor = log.next
                } catch (e: Exception) {

                } finally {
                    isLoading = false
                }
            }
        }
    }
}