package com.jing.bilibilitv.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.jing.bilibilitv.GlobalState
import com.jing.bilibilitv.R
import com.jing.bilibilitv.http.cookie.BilibiliCookieJar
import com.jing.bilibilitv.http.cookie.BilibiliCookieName
import com.jing.bilibilitv.http.data.UserInfo
import com.jing.bilibilitv.model.LoginUserViewModel
import com.jing.bilibilitv.room.dao.BlCookieDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : Fragment() {
    val userViewModel: LoginUserViewModel by activityViewModels()

    @Inject
    lateinit var blCookieDao: BlCookieDao

    @Inject
    lateinit var cookieJar: BilibiliCookieJar

    fun navigateToLoginOrHomePage(hasLogin: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            findNavController().let {
                val navDirections = if (hasLogin) {
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                } else {
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                }
                it.navigate(navDirections, navOptions {
                    popUpTo(R.id.splashFragment) {
                        inclusive = true
                    }
                })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch(Dispatchers.IO) {
            val fetchUser = async { userViewModel.fetchUser() }
            val jb = async { cookieJar.loadCookieFromDatabase() }
            val jb1 =
                async {
                    GlobalState.csrfToken =
                        blCookieDao.findByCookieName(BilibiliCookieName.BILI_JCT.cookieName)?.cookieValue
                            ?: ""
                }
            val result = listOf(fetchUser, jb, jb1).awaitAll()
            val userInfo = result[0] as UserInfo?
            val hasLogin = userInfo != null && userInfo.isLogin
            navigateToLoginOrHomePage(hasLogin)
        }
        return inflater.inflate(R.layout.splash_fragment_layout, container, false)
    }
}