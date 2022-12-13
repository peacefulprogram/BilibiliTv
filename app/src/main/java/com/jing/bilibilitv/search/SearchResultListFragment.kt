package com.jing.bilibilitv.search

import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.BiliUserSearchLayoutBinding
import com.jing.bilibilitv.databinding.ChooseItemIndicatorLayoutBinding
import com.jing.bilibilitv.databinding.FragmentSearchResultBinding
import com.jing.bilibilitv.databinding.SearchResultVideoCardBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.ext.secondsToDateString
import com.jing.bilibilitv.ext.showShortToast
import com.jing.bilibilitv.ext.toShortText
import com.jing.bilibilitv.http.data.BilibiliSearchResult
import com.jing.bilibilitv.http.data.SearchUserResult
import com.jing.bilibilitv.http.data.SearchVideoResult
import com.jing.bilibilitv.model.SearchViewModel
import com.jing.bilibilitv.playback.VideoPlayActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchResultListFragment(private val viewModel: SearchViewModel) : Fragment() {


    private val SEARCH_TYPE_VIDEO = "video"
    private val SEARCH_TYPE_USER = "bili_user"

    private val searchTypeList = listOf(
        Pair("video", "视频"),
        Pair("bili_user", "用户")
    )

    private var startRefresh = false

    private var started = false

    lateinit var viewBinding: FragmentSearchResultBinding

    private lateinit var pagingAdapter: PagingDataAdapter<BilibiliSearchResult, RecyclerView.ViewHolder>

    private var selectedTypeColor = 0
    private var unselectedTypeColor = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        selectedTypeColor = ContextCompat.getColor(requireContext(), R.color.gray50)
        unselectedTypeColor = ContextCompat.getColor(requireContext(), R.color.gray500)
        viewBinding = FragmentSearchResultBinding.inflate(inflater, container, false)
        viewBinding.searchTypeContainer.apply {
            adapter = SearchTypeAdapter(searchTypeList) {
                if (viewModel.setSearchType(it.first)) {
                    pagingAdapter.refresh()
                }
            }
            viewTreeObserver.addOnGlobalFocusChangeListener { oldView, newView ->
                var newViewInTab = false
                if (newView?.parent == viewBinding.searchTypeContainer) {
                    newViewInTab = true
                    (newView.tag as ChooseItemIndicatorLayoutBinding).textContainer.setTextColor(
                        selectedTypeColor
                    )
                }
                if (oldView?.parent == viewBinding.searchTypeContainer) {
                    if (newViewInTab) {
                        (oldView.tag as ChooseItemIndicatorLayoutBinding).textContainer.setTextColor(
                            unselectedTypeColor
                        )
                    }
                }
            }

        }
        pagingAdapter = BilibiliSearchPagingAdapter().apply {
            addLoadStateListener {
                when (it.refresh) {
                    LoadState.Loading -> startRefresh = true
                    is LoadState.NotLoading -> if (startRefresh) {
                        startRefresh = false
                        if (pagingAdapter.itemCount > 0) {
                            viewBinding.searchVideoContainer.scrollToPosition(0)
                            viewBinding.searchVideoContainer.selectedPosition = 0
                            viewBinding.searchVideoContainer.visibility = View.VISIBLE
                            viewBinding.noDataHint.visibility = View.INVISIBLE
                        } else {
                            viewBinding.searchVideoContainer.visibility = View.INVISIBLE
                            viewBinding.noDataHint.visibility = View.VISIBLE
                        }
                    }
                    else -> {}
                }
            }
        }
        viewBinding.searchVideoContainer.apply {
            setNumColumns(1)
            val gap = 10.dpToPx.toInt()
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.top = gap
                }
            })
            adapter = pagingAdapter
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pager.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        started = true
    }


    fun onSearch() {
        pagingAdapter.refresh()
    }

    private object SearchResultDiff : DiffUtil.ItemCallback<BilibiliSearchResult>() {
        override fun areItemsTheSame(
            oldItem: BilibiliSearchResult,
            newItem: BilibiliSearchResult
        ): Boolean = if (oldItem is SearchUserResult && newItem is SearchUserResult) {
            oldItem.mid == newItem.mid
        } else if (oldItem is SearchVideoResult && newItem is SearchVideoResult) {
            oldItem.aid == newItem.aid
        } else {
            false
        }

        override fun areContentsTheSame(
            oldItem: BilibiliSearchResult,
            newItem: BilibiliSearchResult
        ): Boolean = areItemsTheSame(oldItem, newItem)
    }


    private inner class BilibiliSearchPagingAdapter :
        PagingDataAdapter<BilibiliSearchResult, RecyclerView.ViewHolder>(SearchResultDiff) {

        override fun getItemViewType(position: Int): Int {
            val item = getItem(position)
            return when (item) {
                is SearchVideoResult -> {
                    0
                }
                is SearchUserResult -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }

        private fun String.urlWithScheme(): String {
            if (this.startsWith("http")) {
                return this
            }
            return "https:$this"
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = getItem(position)!!
            if (item is SearchUserResult) {
                with(holder.itemView.tag as BiliUserSearchLayoutBinding) {
                    root.setOnClickListener {
                        requireContext().showShortToast("todo: ${item.uname}")
                    }
                    avatar.load(item.upic.urlWithScheme())
                    username.text = item.uname
                    fansCount.text = "${item.fans.toShortText()}粉丝"
                    videoCount.text = "${item.videos}个视频"
                }
            } else if (item is SearchVideoResult) {
                with(holder.itemView.tag as SearchResultVideoCardBinding) {
                    root.setOnClickListener {
                        VideoPlayActivity.navigateToPlayActivity(
                            it.context,
                            item.aid.toString(),
                            item.bvid,
                            item.title
                        )
                    }
                    title.text = item.title
                    username.text = item.author
                    playCount.text = item.play.toShortText()
                    danmuCount.text = item.danmaku.toShortText()
                    duration.text = item.duration
                    pubDate.text = item.pubdate.secondsToDateString()
                    cover.load(item.pic.urlWithScheme()) {
                        transformations(
                            RoundedCornersTransformation(
                                root.context.resources.getDimension(
                                    R.dimen.video_card_radius
                                )
                            )
                        )
                    }
                }
            }
            if (started && position == 0) {
                holder.itemView.requestFocus()
                started = false
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                1 -> {
                    val binding = BiliUserSearchLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    binding.root.tag = binding
                    return object : RecyclerView.ViewHolder(binding.root) {}
                }
                0 -> {
                    val binding = SearchResultVideoCardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    binding.root.tag = binding
                    return object : RecyclerView.ViewHolder(binding.root) {}
                }
                else -> {
                    return object :
                        RecyclerView.ViewHolder(TextView(parent.context).apply { text = "暂不支持" }) {}
                }
            }
        }

    }


    private inner class SearchTypeAdapter(
        private val typeList: List<Pair<String, String>>,
        private val onFocus: (Pair<String, String>) -> Unit = {}
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding = ChooseItemIndicatorLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            binding.root.tag = binding
            return object : RecyclerView.ViewHolder(binding.root) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val type = typeList[position]
            val color =
                if (type.first == viewModel.currentSearchType()) selectedTypeColor else unselectedTypeColor
            holder.itemView.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    viewBinding.searchTypeContainer.requestFocus()
                    true
                } else {
                    false
                }

            }
            with(holder.itemView.tag as ChooseItemIndicatorLayoutBinding) {
                textContainer.text = type.second
                textContainer.setTextColor(color)
                indicator.background =
                    ContextCompat.getColor(requireContext(), R.color.rose400).toDrawable()
                root.setOnFocusChangeListener { _, hasFocus ->
                    indicator.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
                    if (hasFocus) {
                        onFocus(type)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return typeList.size
        }

    }

}
