package com.github.rubensousa.recyclerviewsnap.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.github.rubensousa.gravitysnaphelper.GravitySnapRecyclerView
import com.github.rubensousa.recyclerviewsnap.R
import com.github.rubensousa.recyclerviewsnap.model.SnapList

class SnapListAdapter : RecyclerView.Adapter<SnapListAdapter.VH>() {

    private var items = listOf<SnapList>()

    fun setItems(list: List<SnapList>) {
        this.items = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(
                parent.context
            ).inflate(viewType, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }


    class VH(view: View) : RecyclerView.ViewHolder(view), GravitySnapHelper.SnapListener {

        private val titleView: TextView = view.findViewById(R.id.snapTextView)
        private val recyclerView: GravitySnapRecyclerView = view.findViewById(R.id.recyclerView)
        private val layoutManager = LinearLayoutManager(
            view.context,
            LinearLayoutManager.HORIZONTAL, false
        )
        private val snapHelper = recyclerView.snapHelper
        private val snapNextButton: View = view.findViewById(R.id.scrollNextButton)
        private val snapPreviousButton: View = view.findViewById(R.id.scrollPreviousButton)
        private var item: SnapList? = null
        private val adapter = AppAdapter().apply {
            itemWidth = view.context.resources.displayMetrics.widthPixels / 4f
        }

        init {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            recyclerView.clipChildren = false
            snapNextButton.setOnClickListener { recyclerView.snapToNextPosition(true) }
            snapPreviousButton.setOnClickListener { recyclerView.snapToPreviousPosition(true) }
            recyclerView.setSnapListener(this)

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // 获取recyclerview的布局管理器
                    val manager = recyclerView.layoutManager as LinearLayoutManager
                    for (i in 0 until manager.childCount){
                        val view = manager.getChildAt(i)?:continue
                        val holder = recyclerView.getChildViewHolder(view)
                        val itemStart = layoutManager!!.getDecoratedLeft(holder.itemView)
                        AppAdapter.setViewAnimFrame(holder,itemStart)
                    }
                }
            })
        }

        fun bind(snapList: SnapList) {
            item = snapList
            snapNextButton.isVisible = snapList.showScrollButtons
            titleView.text = snapList.title
            adapter.setItems(snapList.apps)
            snapHelper.snapLastItem = false
            snapHelper.setGravity(snapList.gravity, false)
            snapHelper.scrollMsPerInch = snapList.scrollMsPerInch
            snapHelper.maxFlingSizeFraction = snapList.maxFlingSizeFraction
            snapHelper.snapToPadding = snapList.snapToPadding
            applyDecoration(snapList)
        }

        private fun applyDecoration(snapList: SnapList) {
            val decorations = recyclerView.itemDecorationCount
            repeat(decorations) {
                recyclerView.removeItemDecorationAt(0)
            }
            if (snapList.addStartDecoration) {
                recyclerView.addItemDecoration(
                    LinearEdgeDecoration(
                        startPadding = recyclerView.resources.getDimensionPixelOffset(
                            R.dimen.extra_padding
                        ),
                        endPadding = 0,
                        orientation = RecyclerView.HORIZONTAL
                    )
                )
            }
            if (snapList.addEndDecoration) {
                recyclerView.addItemDecoration(
                    LinearEdgeDecoration(
                        startPadding = 0,
                        endPadding = recyclerView.resources.getDimensionPixelOffset(
                            R.dimen.extra_padding
                        ),
                        orientation = RecyclerView.HORIZONTAL
                    )
                )
            }
        }

        override fun onSnap(position: Int) {
            Log.d("Snapped ${item?.title}", position.toString())
        }
    }
}
