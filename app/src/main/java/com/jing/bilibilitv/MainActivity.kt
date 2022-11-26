package com.jing.bilibilitv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.jing.bilibilitv.fragment.LoginFragment
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.api.PassportApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Loads [MainFragment].
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}