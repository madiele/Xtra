package com.github.andreyasadchy.xtra.ui.videos.channel

import androidx.fragment.app.viewModels
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.model.Account
import com.github.andreyasadchy.xtra.model.ui.BroadcastTypeEnum
import com.github.andreyasadchy.xtra.model.ui.VideoPeriodEnum
import com.github.andreyasadchy.xtra.model.ui.VideoSortEnum
import com.github.andreyasadchy.xtra.ui.main.MainActivity
import com.github.andreyasadchy.xtra.ui.videos.BaseVideosAdapter
import com.github.andreyasadchy.xtra.ui.videos.BaseVideosFragment
import com.github.andreyasadchy.xtra.ui.videos.VideosSortDialog
import com.github.andreyasadchy.xtra.util.C
import com.github.andreyasadchy.xtra.util.TwitchApiHelper
import com.github.andreyasadchy.xtra.util.prefs
import com.github.andreyasadchy.xtra.util.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.android.synthetic.main.sort_bar.*

@AndroidEntryPoint
class ChannelVideosFragment : BaseVideosFragment<ChannelVideosViewModel>(), VideosSortDialog.OnFilter {

    override val viewModel: ChannelVideosViewModel by viewModels()
    override val adapter: BaseVideosAdapter by lazy {
        ChannelVideosAdapter(this, requireActivity() as MainActivity, requireActivity() as MainActivity, {
            lastSelectedItem = it
            showDownloadDialog()
        }, {
            lastSelectedItem = it
            viewModel.saveBookmark(requireContext(), it)
        })
    }

    override fun initialize() {
        super.initialize()
        viewModel.sortText.observe(viewLifecycleOwner) {
            sortText.text = it
        }
        viewModel.setChannelId(
            context = requireContext(),
            channelId = requireArguments().getString(C.CHANNEL_ID),
            channelLogin = requireArguments().getString(C.CHANNEL_LOGIN),
            helixClientId = requireContext().prefs().getString(C.HELIX_CLIENT_ID, "ilfexgv3nnljz3isbm257gzwrzr7bi"),
            helixToken = Account.get(requireContext()).helixToken,
            gqlClientId = requireContext().prefs().getString(C.GQL_CLIENT_ID, "kimne78kx3ncx6brgo4mv6wki5h1ko"),
            apiPref = TwitchApiHelper.listFromPrefs(requireContext().prefs().getString(C.API_PREF_CHANNEL_VIDEOS, ""), TwitchApiHelper.channelVideosApiDefaults)
        )
        sortBar.visible()
        sortBar.setOnClickListener {
            VideosSortDialog.newInstance(
                sort = viewModel.sort,
                period = viewModel.period,
                type = viewModel.type,
                saveSort = viewModel.saveSort,
                saveDefault = requireContext().prefs().getBoolean(C.SORT_DEFAULT_CHANNEL_VIDEOS, false)
            ).show(childFragmentManager, null)
        }
    }

    override fun onChange(sort: VideoSortEnum, sortText: CharSequence, period: VideoPeriodEnum, periodText: CharSequence, type: BroadcastTypeEnum, languageIndex: Int, saveSort: Boolean, saveDefault: Boolean) {
        adapter.submitList(null)
        viewModel.filter(
            sort = sort,
            type = type,
            text = getString(R.string.sort_and_period, sortText, periodText),
            saveSort = saveSort,
            saveDefault = saveDefault
        )
    }
}