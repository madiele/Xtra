package com.github.exact7.xtra.ui.videos.top


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.kraken.video.BroadcastType
import com.github.exact7.xtra.model.kraken.video.Period
import com.github.exact7.xtra.model.kraken.video.Sort
import com.github.exact7.xtra.model.kraken.video.Video
import com.github.exact7.xtra.repository.Listing
import com.github.exact7.xtra.repository.PlayerRepository
import com.github.exact7.xtra.repository.TwitchService
import com.github.exact7.xtra.ui.videos.BaseVideosViewModel
import javax.inject.Inject

class TopVideosViewModel @Inject constructor(
        context: Application,
        private val repository: TwitchService,
        playerRepository: PlayerRepository) : BaseVideosViewModel(playerRepository) {

    val sortOptions = listOf(R.string.today, R.string.this_week, R.string.this_month, R.string.all_time)
    private val _sortText = MutableLiveData<CharSequence>()
    val sortText: LiveData<CharSequence>
        get() = _sortText
    private val filter = MutableLiveData<Filter>()
    override val result: LiveData<Listing<Video>> = Transformations.map(filter) {
        repository.loadVideos(null, it.period, it.broadcastType, it.language, Sort.VIEWS, viewModelScope)
    }
    var selectedIndex = 1
        private set

    init {
        _sortText.value = context.getString(sortOptions[selectedIndex])
        filter.value = Filter()
    }

    fun filter(period: Period, index: Int, text: CharSequence) {
        filter.value?.copy(period = period).let {
            if (filter.value != it) {
                filter.value = it
                selectedIndex = index
                _sortText.value = text
            }
        }
    }

    private data class Filter(
            val period: Period = Period.WEEK,
            val broadcastType: BroadcastType = BroadcastType.ALL,
            val language: String? = null)
}
