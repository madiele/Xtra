package com.github.exact7.xtra.ui.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.exact7.xtra.R
import com.github.exact7.xtra.model.kraken.game.Game
import com.github.exact7.xtra.model.kraken.game.GameWrapper
import com.github.exact7.xtra.ui.common.BasePagedListAdapter
import com.github.exact7.xtra.ui.common.PagedListFragment
import com.github.exact7.xtra.ui.common.Scrollable
import com.github.exact7.xtra.ui.main.MainActivity
import kotlinx.android.synthetic.main.common_recycler_view_layout.*
import kotlinx.android.synthetic.main.fragment_games.*

class GamesFragment : PagedListFragment<GameWrapper, GamesViewModel, BasePagedListAdapter<GameWrapper>>(), Scrollable {

    interface OnGameSelectedListener {
        fun openGame(game: Game)
    }

    override val viewModel by viewModels<GamesViewModel> { viewModelFactory }
    override val adapter: BasePagedListAdapter<GameWrapper> by lazy { GamesAdapter(requireActivity() as MainActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search.setOnClickListener { (requireActivity() as MainActivity).openSearch() }
    }

    override fun scrollToTop() {
        appBar?.setExpanded(true, true)
        recyclerView?.scrollToPosition(0)
    }
}