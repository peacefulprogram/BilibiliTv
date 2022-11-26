package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jing.bilibilitv.databinding.DemoLayoutBinding
import com.jing.bilibilitv.http.api.BilibiliApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DemoFragment(val contentText: String,val bilibiliApi: BilibiliApi) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = DemoLayoutBinding.inflate(inflater, container, false)
        viewBinding.textView.text = contentText
        viewBinding.textView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            val scale = if (hasFocus) 1.2f else 1f
            view.scaleX = scale
            view.scaleY = scale
        }
        if (contentText == "推荐") {
            lifecycleScope.launch(Dispatchers.IO) {
                bilibiliApi.getRecommendation()
            }
        }
        return viewBinding.root
    }
}