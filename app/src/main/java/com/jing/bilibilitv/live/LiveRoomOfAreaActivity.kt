package com.jing.bilibilitv.live

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.jing.bilibilitv.R
import com.jing.bilibilitv.databinding.LiveRoomCardLayoutBinding
import com.jing.bilibilitv.ext.dpToPx
import com.jing.bilibilitv.ext.showLongToast
import com.jing.bilibilitv.ext.toShortText
import com.jing.bilibilitv.http.api.LiveApi
import com.jing.bilibilitv.http.data.LiveRoomOfArea
import com.jing.bilibilitv.view.CustomGridView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LiveRoomOfAreaActivity : AppCompatActivity() {

    private var isLoading = false
    private lateinit var liveRoomGrid: CustomGridView

    private lateinit var noDataHintImage: ImageView

    @Inject
    lateinit var liveApi: LiveApi


    lateinit var viewModel: LiveRoomOfAreaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_room_of_area_layout)
        liveRoomGrid = findViewById(R.id.live_room_grid)
        noDataHintImage = findViewById(R.id.no_data_hint)
        val parentAreaId = intent.extras!!.getString(PARENT_AREA_ID_KEY)!!
        val areaId = intent.extras!!.getString(AREA_ID_KEY)!!
        viewModel = ViewModelProvider(
            this,
            LiveRoomOfAreaViewModel.createViewModelFactory(parentAreaId, areaId, liveApi)
        )[LiveRoomOfAreaViewModel::class.java]

        val columnCount = 3
        liveRoomGrid.setNumColumns(columnCount)
        liveRoomGrid.apply {
            setNumColumns(columnCount)
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 10.dpToPx.toInt()
                }

            })
        }
        val pagingAdapter = LiveRoomPagingAdapter().apply {
            addLoadStateListener {
                when (val refreshState = it.refresh) {
                    LoadState.Loading -> isLoading = true
                    is LoadState.NotLoading -> if (isLoading) {
                        isLoading = false
                        if (itemCount > 0) {
                            liveRoomGrid.visibility = View.VISIBLE
                            noDataHintImage.visibility = View.INVISIBLE
                        } else {
                            liveRoomGrid.visibility = View.INVISIBLE
                            noDataHintImage.visibility = View.VISIBLE
                        }
                    }
                    is LoadState.Error -> showLongToast("加载失败:${refreshState.error.message}")
                }
            }
        }
        liveRoomGrid.adapter = pagingAdapter
        lifecycleScope.launch {
            viewModel.pager.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    val liveRoomDiff = object : DiffUtil.ItemCallback<LiveRoomOfArea>() {
        override fun areItemsTheSame(oldItem: LiveRoomOfArea, newItem: LiveRoomOfArea): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: LiveRoomOfArea, newItem: LiveRoomOfArea): Boolean {
            return oldItem.roomId == newItem.roomId
                    && oldItem.title == newItem.title
                    && oldItem.cover == newItem.cover
                    && oldItem.online == newItem.online
        }


    }

    private inner class LiveRoomPagingAdapter :
        PagingDataAdapter<LiveRoomOfArea, ViewHolder>(liveRoomDiff) {
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val room = getItem(position)!!
            with(holder.itemView.tag as LiveRoomCardLayoutBinding) {
                cover.load(room.cover)
                avatar.load(room.face)
                username.text = room.uname
                watchUserCount.text = room.online?.toShortText() ?: "0"
                title.text = room.title
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val vb = LiveRoomCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            vb.root.tag = vb
            return object : ViewHolder(vb.root) {

            }
        }

    }

    companion object {

        const val PARENT_AREA_ID_KEY = "pAreaId"
        const val AREA_ID_KEY = "areaId"

        fun navigateTo(parentAreaId: String, areaId: String, context: Context) {
            Intent(context, LiveRoomOfAreaActivity::class.java).apply {
                putExtra(PARENT_AREA_ID_KEY, parentAreaId)
                putExtra(AREA_ID_KEY, areaId)
                context.startActivity(this)
            }

        }
    }
}