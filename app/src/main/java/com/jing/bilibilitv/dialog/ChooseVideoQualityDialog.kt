package com.jing.bilibilitv.dialog

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.ChooseItemIndicatorLayoutBinding
import com.jing.bilibilitv.ext.getColorWithAlpha
import com.jing.bilibilitv.ext.toPx

class ChooseVideoQualityDialog(
    private val qualityList: List<Pair<Int, String>>,
    private val currentQn: Int,
    private val onChoose: (Pair<Int, String>) -> Unit
) : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.attributes?.run {
            height = WindowManager.LayoutParams.MATCH_PARENT
            width = 130.toPx.toInt()
            gravity = Gravity.END
            dialog!!.window!!.attributes = this
        }
        dialog?.window?.attributes?.dimAmount = 0f
        dialog!!.window!!.setBackgroundDrawable(
            requireContext().getColorWithAlpha(
                R.color.zinc900,
                0.3f
            ).toDrawable()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = RecyclerView(requireContext()).apply {
//            layoutManager = FlexboxLayoutManager(requireContext()).apply {
//                flexDirection = FlexDirection.COLUMN
//                flexWrap = FlexWrap.NOWRAP
//                justifyContent = JustifyContent.CENTER
//            }
//            layoutParams = LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//            )
            layoutManager = LinearLayoutManager(requireContext())
        }
        recyclerView.adapter = object : Adapter<ChooseItemViewHolder>() {

            private val choosenColor = ContextCompat.getColor(requireContext(), R.color.rose400)
            private val blurColor = ContextCompat.getColor(requireContext(), R.color.gray200)
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ChooseItemViewHolder {
                return ChooseItemViewHolder(
                    ChooseItemIndicatorLayoutBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            override fun onBindViewHolder(holder: ChooseItemViewHolder, position: Int) {
                with(holder.viewBinding) {
                    textContainer.text = qualityList[position].second
                    if (currentQn == qualityList[position].first) {
                        textContainer.setTextColor(choosenColor)
                    } else {
                        textContainer.setTextColor(blurColor)
                    }
                }

                with(holder.viewBinding.root) {
                    setOnClickListener {
                        onChoose(qualityList[position])
                        dismissNow()
                    }
                    onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                        holder.viewBinding.root.background = if (hasFocus) {
                            requireContext().getColorWithAlpha(
                                com.jing.bilibilitv.R.color.gray600,
                                0.4f
                            ).toDrawable()
                        } else {
                            ColorDrawable(Color.TRANSPARENT)
                        }
                        with(holder.viewBinding.indicator) {
//                            holder.viewBinding.indicator.text = "12345678"
                            background = ColorDrawable(choosenColor)
                            visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
                        }
                    }
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
                outRect.top = 10
                outRect.bottom = 10
            }
        })
        return recyclerView
    }


    private class ChooseItemViewHolder(val viewBinding: ChooseItemIndicatorLayoutBinding) :
        ViewHolder(viewBinding.root)


}