package com.jing.bilibilitv.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.UserDetailResponse
import com.jing.bilibilitv.http.data.UserVideoVlist
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

class UserSpaceViewModel(private val mid: Long, private val bilibiliApi: BilibiliApi) :
    ViewModel() {

    private val TAG = UserSpaceViewModel::class.java.simpleName

    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 3
        ),
    ) {
        UserVideoPagingSource(mid, bilibiliApi)
    }.flow

    init {
        loadUserDetail()
    }

    private val _isFollowing: MutableStateFlow<Resource<Boolean>> =
        MutableStateFlow(Resource.Loading())

    val isFollowing: StateFlow<Resource<Boolean>>
        get() = _isFollowing

    private val _userDetail: MutableStateFlow<Resource<UserDetailResponse>> =
        MutableStateFlow(Resource.Loading())

    val userDetail: StateFlow<Resource<UserDetailResponse>>
        get() = _userDetail

    fun loadUserDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val detail = bilibiliApi.getUserDetail(mid = mid).data!!
                _isFollowing.emit(Resource.Success(detail.isFollowed))
                _userDetail.emit(Resource.Success(detail))
            } catch (e: Exception) {
                _userDetail.emit(Resource.Error("加载用户详情失败:${e.message}", e))
            }
        }

    }


    private fun checkIsFollowing() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isFollowing.emit(Resource.Success(bilibiliApi.checkIsFollowing(mid).data!!.isFollowing()))
            } catch (e: Exception) {
                _isFollowing.emit(Resource.Error("查询是否关注失败:${e.message}", e))
            }
        }
    }

    private fun changeFollowState(action: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isFollowing.emit(Resource.Loading())
            bilibiliApi.changeRelation(mid, action)
            checkIsFollowing()
        }

    }

    fun toggleFollow() {
        val followState = _isFollowing.value
        if (followState is Resource.Success) {
            changeFollowState(if (followState.data) 2 else 1)
        }
    }

    private class UserVideoPagingSource(
        private val mid: Long,
        private val bilibiliApi: BilibiliApi
    ) : PagingSource<Int, UserVideoVlist>() {

        private val TAG = UserVideoPagingSource::class.java.simpleName

        override fun getRefreshKey(state: PagingState<Int, UserVideoVlist>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserVideoVlist> {
            val pageNumber = params.key ?: 1
            try {
                val data = bilibiliApi.queryVideoOfUser(
                    mid = mid,
                    pageNumber = pageNumber,
                    pageSize = 20
                ).data!!
                val totalPage = ceil(data.page.count.toDouble().div(data.page.ps)).toInt()
                return LoadResult.Page(
                    data = data.list.vlist,
                    prevKey = if (pageNumber > 1) pageNumber - 1 else null,
                    nextKey = if (pageNumber < totalPage) pageNumber + 1 else null
                )
            } catch (e: Exception) {
                Log.e(TAG, "加载用户视频失败:${e.message}", e)
                return LoadResult.Error(e)
            }
        }

    }

    companion object {
        fun createViewModelFactory(mid: Long, bilibiliApi: BilibiliApi) = viewModelFactory {
            initializer {
                UserSpaceViewModel(mid, bilibiliApi)
            }
        }
    }

}