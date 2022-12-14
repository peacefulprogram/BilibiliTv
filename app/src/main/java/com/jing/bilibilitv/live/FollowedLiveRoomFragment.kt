package com.jing.bilibilitv.live

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.LiveRoomCardLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.ext.toShortText
import com.jing.bilibilitv.http.data.FollowedLiveRoom
import com.jing.bilibilitv.live.playback.LiveRoomPlaybackActivity
import com.jing.bilibilitv.view.CustomGridView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowedLiveRoomFragment(
    private val viewModel: BilibiliLiveViewModel,
    private val tabContainer: View
) :
    Fragment() {

    private var startRefresh = false
    private lateinit var videoGrid: CustomGridView
    private lateinit var pagingAdapter: LiveRoomPagingAdapter

    private lateinit var noDataHintImage: ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.followed_live_room_layout, container, false)
        val columnsCount = 3
        videoGrid = root.findViewById<CustomGridView?>(R.id.live_room_grid).apply {
            val gap = 5.dpToPx.toInt()
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.run {
                        left = gap
                        top = gap
                        right = gap
                        bottom = gap
                    }
                }
            })
        }
        noDataHintImage = root.findViewById(R.id.no_data_hint)
        pagingAdapter = LiveRoomPagingAdapter().apply {
            addLoadStateListener {
                when (it.refresh) {
                    LoadState.Loading -> startRefresh = true
                    is LoadState.NotLoading -> if (startRefresh) {
                        startRefresh = false
                        if (pagingAdapter.itemCount > 0) {
                            videoGrid.scrollToPosition(0)
                            videoGrid.selectedPosition = 0
                            videoGrid.visibility = View.VISIBLE
                            noDataHintImage.visibility = View.INVISIBLE
                        } else {
                            videoGrid.visibility = View.INVISIBLE
                            noDataHintImage.visibility = View.VISIBLE
                        }
                    }
                    else -> {}
                }
            }
        }
        videoGrid.apply {
            setNumColumns(columnsCount)
            adapter = pagingAdapter
            interceptorFocusSearch = { direction, position, focused ->
                if (direction == RecyclerView.FOCUS_LEFT && position % columnsCount == 0) {
                    focused
                } else if (position < columnsCount && direction == RecyclerView.FOCUS_UP) {
                    tabContainer
                } else {
                    null
                }
            }
        }

        lifecycleScope.launch {
            viewModel.followedLiveRoomPager.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
        return root
    }

    private val liveRoomDiffCallback = object : DiffUtil.ItemCallback<FollowedLiveRoom>() {
        override fun areItemsTheSame(
            oldItem: FollowedLiveRoom,
            newItem: FollowedLiveRoom
        ): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(
            oldItem: FollowedLiveRoom,
            newItem: FollowedLiveRoom
        ): Boolean {
            return oldItem.roomId == newItem.roomId && oldItem.face == newItem.face
        }

    }

    private inner class LiveRoomPagingAdapter :
        androidx.paging.PagingDataAdapter<FollowedLiveRoom, ViewHolder>(liveRoomDiffCallback) {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val room = getItem(position)!!
            with(holder.itemView.tag as LiveRoomCardLayoutBinding) {
                root.setOnClickListener {
                    LiveRoomPlaybackActivity.navigateTo(
                        room.roomId,
                        root.context
                    )
                }
                cover.load(room.coverFromUser)
                avatar.load(room.face)
                title.text = room.title
                username.text = room.uname
                watchUserCount.text = room.online.toShortText()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vb = LiveRoomCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            vb.root.tag = vb
            return object : ViewHolder(vb.root) {}
        }

    }


}