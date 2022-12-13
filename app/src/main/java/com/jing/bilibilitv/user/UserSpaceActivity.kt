package com.jing.bilibilitv.user

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import coil.load
import coil.transform.RoundedCornersTransformation
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.SearchResultVideoCardBinding
import com.jing.bilibilitv.databinding.UserSpaceLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.ext.secondsToDateString
import com.jing.bilibilitv.ext.showLongToast
import com.jing.bilibilitv.ext.toShortText
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.UserVideoVlist
import com.jing.bilibilitv.playback.VideoPlayActivity
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserSpaceActivity : AppCompatActivity() {

    private lateinit var viewModel: UserSpaceViewModel

    private lateinit var viewBinding: UserSpaceLayoutBinding

    private lateinit var pagingDataAdapter: UserVideoPagingAdapter

    private var isLoading = false

    @Inject
    lateinit var bilibiliApi: BilibiliApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mid = intent.extras!!.getLong(MID_KEY)
        viewModel = ViewModelProvider(
            this,
            UserSpaceViewModel.createViewModelFactory(mid, bilibiliApi)
        )[UserSpaceViewModel::class.java]
        viewBinding = UserSpaceLayoutBinding.inflate(LayoutInflater.from(this))
        initVideoPaging()
        initUserDetailListener()
        setContentView(viewBinding.root)
    }

    private fun initUserDetailListener() {
        viewBinding.isFollow.setOnClickListener {
            viewModel.toggleFollow()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userDetail.collectLatest {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            viewBinding.avatar.load(it.data.face)
                            viewBinding.username.text = it.data.name
                        }
                        is Resource.Error -> showLongToast(it.getErrorMessage())
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isFollowing.collectLatest {
                    when (it) {
                        is Resource.Loading -> viewBinding.isFollow.visibility = View.INVISIBLE
                        is Resource.Success -> viewBinding.isFollow.apply {
                            visibility = View.VISIBLE
                            text = if (it.data) "已关注" else "未关注"
                        }
                        is Resource.Error -> showLongToast(it.getErrorMessage())
                    }
                }
            }
        }
    }

    private fun initVideoPaging() {
        pagingDataAdapter = UserVideoPagingAdapter().apply {
            addLoadStateListener {
                when (val refreshState = it.refresh) {
                    LoadState.Loading -> isLoading = true
                    is LoadState.NotLoading -> if (isLoading) {
                        isLoading = false
                        if (itemCount > 0) {
                            viewBinding.videoGrid.visibility = View.VISIBLE
                            viewBinding.noDataHint.visibility = View.INVISIBLE
                        } else {
                            viewBinding.videoGrid.visibility = View.INVISIBLE
                            viewBinding.noDataHint.visibility = View.VISIBLE
                        }
                    }
                    is LoadState.Error -> showLongToast("加载失败:${refreshState.error.message}")
                }
            }
        }

        val columnsCount = 2
        viewBinding.videoGrid.apply {
            val gap = 5.dpToPx.toInt()
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.top = gap
                    outRect.left = gap
                    outRect.right = gap
                    outRect.bottom = gap
                }
            })
            setNumColumns(columnsCount)
            adapter = pagingDataAdapter
            interceptorFocusSearch = { direction, position, _ ->
                if (position < columnsCount && direction == FOCUS_UP) {
                    viewBinding.isFollow
                } else {
                    null
                }
            }
        }
        lifecycleScope.launch {
            viewModel.pager.collectLatest {
                pagingDataAdapter.submitData(it)
            }
        }

    }

    private val UserVideoDiff = object : DiffUtil.ItemCallback<UserVideoVlist>() {
        override fun areItemsTheSame(oldItem: UserVideoVlist, newItem: UserVideoVlist): Boolean {
            return oldItem.aid == newItem.aid
        }

        override fun areContentsTheSame(oldItem: UserVideoVlist, newItem: UserVideoVlist): Boolean =
            areItemsTheSame(oldItem, newItem)
    }

    private inner class UserVideoPagingAdapter :
        PagingDataAdapter<UserVideoVlist, ViewHolder>(UserVideoDiff) {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)!!
            with(holder.itemView.tag as SearchResultVideoCardBinding) {
                root.setOnClickListener {
                    VideoPlayActivity.navigateToPlayActivity(
                        root.context,
                        item.aid.toString(),
                        item.bvid,
                        item.title
                    )
                    true
                }
                username.text = item.author
                duration.text = item.length
                title.text = item.title
                danmuCount.text = item.comment.toShortText()
                playCount.text = item.play.toShortText()
                pubDate.text = item.created.secondsToDateString()
                cover.load(item.pic) {
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vb = SearchResultVideoCardBinding.inflate(LayoutInflater.from(parent.context))
            vb.root.tag = vb
            return object : ViewHolder(vb.root) {
            }
        }

    }


    companion object {
        const val MID_KEY = "mid"
        fun navigateTo(mid: Long, context: Context) {
            Intent(context, UserSpaceActivity::class.java).apply {
                putExtra(MID_KEY, mid)
                context.startActivity(this)
            }

        }
    }
}