package com.jing.bilibilitv.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.BuildConfig
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.*
import com.jing.bilibilitv.ext.*
import com.jing.bilibilitv.http.data.BilibiliSearchResult
import com.jing.bilibilitv.http.data.SearchUserResult
import com.jing.bilibilitv.http.data.SearchVideoResult
import com.jing.bilibilitv.layout.FocusSearchInterceptorGridLayoutManager
import com.jing.bilibilitv.layout.FocusSearchInterceptorLinearLayoutManager
import com.jing.bilibilitv.model.SearchViewModel
import com.jing.bilibilitv.playback.VideoPlayActivity
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : FragmentActivity() {
    private lateinit var currentShownFragment: Fragment
    private val TAG = SearchActivity::class.java.simpleName

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var viewBinding: SearchFragmentLayoutBinding

    private val keywordSuggestFragment by lazy {
        KeywordSuggestFragment(viewModel) {
            onSearch(it)
        }
    }

    private val searchResultListFragment by lazy { SearchResultListFragment(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = SearchFragmentLayoutBinding.inflate(LayoutInflater.from(this))
        initKeyboard()
        viewBinding.kwInput.addTextChangedListener {
            val kw = it?.toString() ?: ""
            viewModel.keywordInputChange(kw)
            showFragment(keywordSuggestFragment)

        }
        showFragment(keywordSuggestFragment)
        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG) {
            lifecycleScope.launch {
                while (true) {
                    delay(1000L)
                    val v = this@SearchActivity.currentFocus
                    Log.d(TAG, "focused view: $v:text:${if (v is TextView) v.text else ""}")
                }
            }
        }
    }

    private fun initKeyboard() {
        viewBinding.keyboardGrid.apply {
            val columnCount = 6
            val keyBoardKeyList = buildKeyBoardKeyList()
            layoutManager = FocusSearchInterceptorGridLayoutManager(
                this@SearchActivity,
                columnCount
            ) { focused, direction ->
                val position = getChildAdapterPosition(focused)
                if (position < columnCount && direction == View.FOCUS_UP) {
                    viewBinding.kwInput
                } else if (direction == View.FOCUS_RIGHT && (position == keyBoardKeyList.size - 1 || position % columnCount == columnCount - 1)) {
                    if (currentShownFragment is SearchResultListFragment) {
                        (currentShownFragment as SearchResultListFragment).viewBinding.searchVideoContainer
                    } else {
                        val fragment = currentShownFragment as KeywordSuggestFragment
                        if (fragment.viewBinding.suggestContainer.visibility == View.VISIBLE) {
                            fragment.viewBinding.suggestKwList
                        } else {
                            fragment.viewBinding.hotKwContainer
                        }
                    }
                } else {
                    null
                }
            }.apply {
                spanSizeLookup =
                    object : SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            if (position >= keyBoardKeyList.size - 3) {
                                return 2
                            }
                            return 1
                        }
                    }
            }
            val gap = 10.dpToPx.toInt()
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: State
                ) {
                    val pos = getChildAdapterPosition(view)
                    outRect.top = gap
                    outRect.right = gap / 2
                    outRect.left = gap / 2
                    outRect.bottom = 0
                    if (pos < columnCount) {
                        outRect.top = 0
                    }

                }
            })
            adapter = KeyBoardAdapter(this@SearchActivity, keyBoardKeyList)
        }
    }

    private fun buildKeyBoardKeyList(): List<KeyBoardKey> {
        val list = mutableListOf<KeyBoardKey>()
        val textList = mutableListOf<String>()
        for (c in 'A'..'Z') {
            textList.add(c.toString())
        }
        for (c in 1..9) {
            textList.add(c.toString())
        }
        textList.add("0")

        textList.forEach { text ->
            list.add(KeyBoardKey(text) {
                viewBinding.kwInput.setText(
                    "${viewBinding.kwInput.text}${text}",
                    TextView.BufferType.EDITABLE
                )
            })
        }

        list.add(KeyBoardKey("清空") {
            viewBinding.kwInput.setText("", TextView.BufferType.EDITABLE)
        })

        list.add(KeyBoardKey("删除") {
            val currentText = viewBinding.kwInput.text
            if (currentText.isNotEmpty()) {
                val newText = currentText.subSequence(0, currentText.length - 1)
                viewBinding.kwInput.setText(newText, TextView.BufferType.EDITABLE)
            }
        })

        list.add(KeyBoardKey("搜索") {
            onSearch(viewBinding.kwInput.text.toString())
        })

        return list
    }

    private fun onSearch(keyword: String) {
        if (keyword.isEmpty()) {
            return
        }
        showFragment(searchResultListFragment)
        searchResultListFragment.onSearch(keyword)
    }

    private fun showFragment(fragment: Fragment) {
        if (supportFragmentManager.fragments.isNotEmpty() && supportFragmentManager.fragments[0] == fragment) {
            return
        }
        currentShownFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.result_fragment_container, fragment)
            .commitNow()
    }

    private class KeyBoardKeyViewHolder(val textView: TextView) : ViewHolder(textView)

    private class KeyBoardAdapter(context: Context, private val keyList: List<KeyBoardKey>) :
        Adapter<KeyBoardKeyViewHolder>() {

        private val blurTextColor = ContextCompat.getColor(context, R.color.gray50)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyBoardKeyViewHolder {
            return KeyBoardKeyViewHolder(TextView(parent.context).apply {
                background = ContextCompat.getDrawable(parent.context, R.drawable.keyboard_bg)
                focusable = View.FOCUSABLE
            })
        }

        override fun onBindViewHolder(holder: KeyBoardKeyViewHolder, position: Int) {
            with(holder.textView) {
                setTextColor(blurTextColor)
                val key = keyList[position]
                text = key.text
                setOnClickListener {
                    key.onKeyClick()
                }
                setPadding(10.dpToPx.toInt())
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                if (position == 0) {
                    requestFocus()
                }
            }
        }

        override fun getItemCount(): Int {
            return keyList.size
        }

    }

    private data class KeyBoardKey(val text: String, val onKeyClick: () -> Unit)


}

class KeywordSuggestFragment(
    private val viewModel: SearchViewModel,
    private val onKeywordClick: (String) -> Unit
) : Fragment() {

    lateinit var viewBinding: SearchKwSuggestLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()
        viewBinding = SearchKwSuggestLayoutBinding.inflate(inflater, container, false)
        with(viewBinding) {
            suggestKwList.layoutManager =
                FocusSearchInterceptorLinearLayoutManager(context) { _, direction ->
                    if (direction == View.FOCUS_RIGHT) {
                        viewBinding.hotKwContainer
                    } else {
                        null
                    }
                }
            hotKwContainer.layoutManager =
                FocusSearchInterceptorLinearLayoutManager(context) { _, direction ->
                    if (direction == View.FOCUS_LEFT && viewBinding.suggestContainer.visibility == View.VISIBLE) {
                        viewBinding.suggestKwList
                    } else {
                        null
                    }
                }
            suggestKwList.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: State
                ) {
                    outRect.top = 5.dpToPx.toInt()
                }
            })
            hotKwContainer.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: State
                ) {
                    outRect.top = 5.dpToPx.toInt()
                }
            })
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.keywordSuggest.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> resource.data.map { Pair(it.name, it.value) }.let {
                            if (it.isEmpty()) {
                                viewBinding.suggestContainer.visibility = View.GONE
                            } else {
                                viewBinding.suggestContainer.visibility = View.VISIBLE
                            }
                            viewBinding.suggestKwList.adapter = KeywordAdapter(it)
                        }
                        is Resource.Error -> requireContext().showLongToast(resource.getErrorMessage())
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.hotSearchKeyword.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> resource.data.map { Pair(it.showName, it.keyword) }
                            .let {
                                viewBinding.hotKwContainer.adapter = KeywordAdapter(it)
                            }
                        is Resource.Error -> context.showLongToast(resource.getErrorMessage())
                    }
                }
            }
        }
        return viewBinding.root
    }

    override fun onStart() {
        viewModel.loadHotSearchKeywords()
        super.onStart()
    }


    private class KeywordViewHolder(val viewBinding: SearchKeywordItemBinding) :
        ViewHolder(viewBinding.root)

    private inner class KeywordAdapter(private val keywordList: List<Pair<String, String>>) :
        Adapter<KeywordViewHolder>() {

        private val TAG = KeywordAdapter::class.java.simpleName

        val textColor = ContextCompat.getColor(requireContext(), R.color.gray100)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
            return KeywordViewHolder(
                SearchKeywordItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
            val kw = keywordList[position]
            with(holder.viewBinding.kwText) {
                setTextColor(textColor)
                textSize = 15f
                maxLines = 1
                focusable = View.FOCUSABLE
                text = kw.first
            }
            holder.viewBinding.root.setOnClickListener {
                onKeywordClick(kw.second)
            }
        }

        override fun getItemCount(): Int {
            return keywordList.size
        }

    }


}


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

    private lateinit var pagingAdapter: PagingDataAdapter<BilibiliSearchResult, ViewHolder>

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
                        viewBinding.searchVideoContainer.scrollToPosition(0)
                        viewBinding.searchVideoContainer.selectedPosition = 0
                    }
                    else -> {}
                }
            }
        }
        viewBinding.searchVideoContainer.apply {
            setNumColumns(1)
            val gap = 10.dpToPx.toInt()
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: State
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


    fun onSearch(keyword: String) {
        viewModel.changeKeyword(keyword)
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
        PagingDataAdapter<BilibiliSearchResult, ViewHolder>(SearchResultDiff) {

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

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            when (viewType) {
                1 -> {
                    val binding = BiliUserSearchLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    binding.root.tag = binding
                    return object : ViewHolder(binding.root) {}
                }
                0 -> {
                    val binding = SearchResultVideoCardBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    binding.root.tag = binding
                    return object : ViewHolder(binding.root) {}
                }
                else -> {
                    return object : ViewHolder(TextView(parent.context).apply { text = "暂不支持" }) {}
                }
            }
        }

    }


    private inner class SearchTypeAdapter(
        private val typeList: List<Pair<String, String>>,
        private val onFocus: (Pair<String, String>) -> Unit = {}
    ) : Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ChooseItemIndicatorLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            binding.root.tag = binding
            return object : ViewHolder(binding.root) {}
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val color = if (position == 0) selectedTypeColor else unselectedTypeColor
            val type = typeList[position]
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
