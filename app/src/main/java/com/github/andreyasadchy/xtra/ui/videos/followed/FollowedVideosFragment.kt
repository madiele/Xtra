package com.github.andreyasadchy.xtra.ui.videos.followed

import androidx.fragment.app.viewModels
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.model.Account
import com.github.andreyasadchy.xtra.model.ui.BroadcastTypeEnum
import com.github.andreyasadchy.xtra.model.ui.VideoPeriodEnum
import com.github.andreyasadchy.xtra.model.ui.VideoSortEnum
import com.github.andreyasadchy.xtra.ui.main.MainActivity
import com.github.andreyasadchy.xtra.ui.videos.BaseVideosAdapter
import com.github.andreyasadchy.xtra.ui.videos.BaseVideosFragment
import com.github.andreyasadchy.xtra.ui.videos.VideosAdapter
import com.github.andreyasadchy.xtra.ui.videos.VideosSortDialog
import com.github.andreyasadchy.xtra.util.C
import com.github.andreyasadchy.xtra.util.TwitchApiHelper
import com.github.andreyasadchy.xtra.util.prefs
import com.github.andreyasadchy.xtra.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.android.synthetic.main.sort_bar.*

@AndroidEntryPoint
class FollowedVideosFragment : BaseVideosFragment<FollowedVideosViewModel>(), VideosSortDialog.OnFilter {

    override val viewModel: FollowedVideosViewModel by viewModels()

    override val adapter: BaseVideosAdapter by lazy {
        val activity = requireActivity() as MainActivity
        VideosAdapter(this, activity, activity, activity, {
            lastSelectedItem = it
            showDownloadDialog()
        }, {
            lastSelectedItem = it
            viewModel.saveBookmark(
                context = requireContext(),
                video = it)
        })
    }

    override fun initialize() {
        super.initialize()
        viewModel.sortText.observe(viewLifecycleOwner) {
            sortText.text = it
        }
        viewModel.setUser(
            context = requireContext(),
            account = Account.get(requireContext()),
            gqlClientId = requireContext().prefs().getString(C.GQL_CLIENT_ID, "kimne78kx3ncx6brgo4mv6wki5h1ko"),
            apiPref = TwitchApiHelper.listFromPrefs(requireContext().prefs().getString(C.API_PREF_FOLLOWED_VIDEOS, ""), TwitchApiHelper.followedVideosApiDefaults)
        )
        sortBar.visible()
        sortBar.setOnClickListener {
            VideosSortDialog.newInstance(
                sort = viewModel.sort,
                period = viewModel.period,
                type = viewModel.type,
                saveDefault = requireContext().prefs().getBoolean(C.SORT_DEFAULT_FOLLOWED_VIDEOS, false)
            ).show(childFragmentManager, null)
        }
    }

    override fun onChange(sort: VideoSortEnum, sortText: CharSequence, period: VideoPeriodEnum, periodText: CharSequence, type: BroadcastTypeEnum, languageIndex: Int, saveSort: Boolean, saveDefault: Boolean) {
        adapter.submitList(null)
        viewModel.filter(
            sort = sort,
            period = period,
            type = type,
            text = getString(R.string.sort_and_period, sortText, periodText),
            saveDefault = saveDefault
        )
    }
}
