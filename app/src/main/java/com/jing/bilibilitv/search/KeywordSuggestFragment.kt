package com.jing.bilibilitv.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.SearchKeywordItemBinding
import com.jing.bilibilitv.databinding.SearchKwSuggestLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.ext.showLongToast
import com.jing.bilibilitv.layout.FocusSearchInterceptorLinearLayoutManager
import com.jing.bilibilitv.model.SearchViewModel
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            suggestKwList.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.top = 5.dpToPx.toInt()
                }
            })
            hotKwContainer.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
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
                        is Resource.Success -> resource.data.map { Pair(it.value, it.value) }.let {
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
        RecyclerView.ViewHolder(viewBinding.root)

    private inner class KeywordAdapter(private val keywordList: List<Pair<String, String>>) :
        RecyclerView.Adapter<KeywordViewHolder>() {

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
