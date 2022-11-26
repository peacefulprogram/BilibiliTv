package com.jing.bilibilitv.dialog

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jing.bilibilitv.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChooseVideoQualityDialog(
    private val qualityList: List<Pair<Int, String>>,
    private val currentQn: Int,
    private val onChoose: (Pair<Int, String>) -> Unit
) : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = object : Adapter<ViewHolder>() {

            private val focusColor = ContextCompat.getColor(requireContext(), R.color.rose400)
            private val blurColor = ContextCompat.getColor(requireContext(), R.color.gray200)
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object : ViewHolder(TextView(requireContext())) {}
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val textView = holder.itemView as TextView
                textView.setTextColor(blurColor)
                textView.apply {
                    text = qualityList[position].second
                    textView.focusable = View.FOCUSABLE
                }
                textView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    textView.setTextColor(if (hasFocus) focusColor else blurColor)
                }
                textView.setOnClickListener {
                    onChoose(qualityList[position])
                    dismissNow()
                }
            }

            override fun getItemCount(): Int {
                return qualityList.size
            }

        }
        recyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = 20
                outRect.right = 20
                outRect.left = 20
            }
        })
        return recyclerView
    }


}