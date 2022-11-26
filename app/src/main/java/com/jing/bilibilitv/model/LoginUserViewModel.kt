package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.bilibilitv.global.GlobalUser
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.UserInfo
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginUserViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi
) : ViewModel() {
    private val _userState = MutableStateFlow<Resource<UserInfo>>(Resource.Loading())

    val userState: StateFlow<Resource<UserInfo>>
        get() = _userState

    suspend fun fetchUser(): UserInfo? {
        val userInfo = bilibiliApi.getLoginUserInfo().data
        if (userInfo == null || !userInfo.isLogin) {
            _userState.emit(Resource.Error("用户未登录"))
        } else {
            _userState.emit(Resource.Success(userInfo))
        }
        return userInfo
    }
}