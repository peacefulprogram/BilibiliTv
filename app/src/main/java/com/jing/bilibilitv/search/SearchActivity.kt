package com.jing.bilibilitv.search

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.SearchFragmentLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.layout.FocusSearchInterceptorGridLayoutManager
import com.jing.bilibilitv.model.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.changeKeyword(keyword)
        showFragment(searchResultListFragment)
        searchResultListFragment.onSearch()
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
