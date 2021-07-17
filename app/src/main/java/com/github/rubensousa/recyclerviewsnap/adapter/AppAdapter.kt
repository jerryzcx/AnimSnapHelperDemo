package com.github.rubensousa.recyclerviewsnap.adapter

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.recyclerviewsnap.R
import com.github.rubensousa.recyclerviewsnap.model.App


val Number.dp2px get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()
val Number.px2dp get() = (toInt() / Resources.getSystem().displayMetrics.density).toInt()

fun View.getScreenWidth(): Int {
    return this.context.resources.displayMetrics.widthPixels
}

class AppAdapter(private val layoutId: Int = R.layout.adapter) :
        RecyclerView.Adapter<AppAdapter.VH>() {

    private val TEST_COLOR = arrayListOf(android.R.color.holo_blue_dark, android.R.color.holo_green_dark,
            android.R.color.holo_red_dark, android.R.color.holo_orange_dark)


    companion object {
        private val INIT_MARGIN_TOP = arrayListOf(31, 37, 43, 49)
        private val INIT_MARGIN_IMAGE_END = arrayListOf(0, 3, 10, 26)
        private val INIT_ROUND = arrayListOf(70, 58, 48, 40)
        private val INIT_TIME_SIZE = arrayListOf(12, 11, 10, 9)
        private val INIT_NAME_SIZE = arrayListOf(14, 13, 12, 11)
        private val INIT_ZOOM_SCALE = arrayListOf(0.5f, 1f)

        fun setViewAnimFrame(holder: RecyclerView.ViewHolder, marginLeft: Int) {
            if (holder !is AppAdapter.VH) {
                return
            }
            val percent = marginLeft.toFloat() / holder.itemView.getScreenWidth()
            if (percent <= 0f) {
                if (percent <= -0.25f / 2) {
                    resetViewParams(0, holder)
                    holder.contentContainer.scaleX = INIT_ZOOM_SCALE[0]
                    holder.contentContainer.scaleY = INIT_ZOOM_SCALE[0]
                    holder.contentContainer.alpha = INIT_ZOOM_SCALE[0]
                } else {
                    val left = INIT_ZOOM_SCALE[0]
                    val right = INIT_ZOOM_SCALE[1]
                    val result = (percent + 0.25f / 2) * (1 / 0.125f) * (right - left) + left
                    holder.contentContainer.scaleX = result
                    holder.contentContainer.scaleY = result
                    holder.contentContainer.alpha = result
                }
                return
            }
            (holder.contentContainer.layoutParams as FrameLayout.LayoutParams)
                    .apply {
                        val left = INIT_MARGIN_TOP[0]
                        val right = INIT_MARGIN_TOP[3] + 6
                        val result = percent * (right - left) + left
                        topMargin = result.dp2px
                    }

            (holder.imageView.layoutParams as ConstraintLayout.LayoutParams)
                    .apply {
                        var left = INIT_ROUND[0]
                        var right = INIT_ROUND[3] - 6
                        var result = percent * (right - left) + left
                        width = result.dp2px
                        height = result.dp2px

                        left = INIT_MARGIN_IMAGE_END[0]
                        right = INIT_MARGIN_IMAGE_END[3] + 3
                        result = percent * (right - left) + left
                        rightMargin = result.dp2px
                    }
            holder.timeTextView.apply {
                val left = INIT_TIME_SIZE[0]
                val right = INIT_TIME_SIZE[3] - 1
                val result = percent * (right - left) + left
                textSize = result.toFloat()
            }
            holder.nameTextView.apply {
                val left = INIT_NAME_SIZE[0]
                val right = INIT_NAME_SIZE[3] - 1
                val result = percent * (right - left) + left
                textSize = result.toFloat()
            }

            holder.contentContainer.requestLayout()
        }


        fun resetViewParams(position: Int, holder: VH) {
            when (position) {
                in 0..3 -> {
                    (holder.contentContainer.layoutParams as FrameLayout.LayoutParams)
                            .apply {
                                topMargin = INIT_MARGIN_TOP[position].dp2px
                            }
                    (holder.imageView.layoutParams as ConstraintLayout.LayoutParams).apply {
                        width = INIT_ROUND[position].dp2px
                        height = INIT_ROUND[position].dp2px
                        rightMargin = INIT_MARGIN_IMAGE_END[position].dp2px
                    }
                    holder.timeTextView.apply {
                        textSize = INIT_TIME_SIZE[position].toFloat()
                    }
                    holder.nameTextView.apply {
                        textSize = INIT_NAME_SIZE[position].toFloat()
                    }
                }
                else -> {
                    val newMarginTop = INIT_MARGIN_TOP[3] + 6
                    (holder.contentContainer.layoutParams as FrameLayout.LayoutParams)
                            .topMargin = newMarginTop.dp2px
                    (holder.imageView.layoutParams as ViewGroup.LayoutParams).apply {
                        width = (INIT_ROUND[3] - 6).dp2px
                        height = (INIT_ROUND[3] - 6).dp2px
                    }
                    holder.timeTextView.apply {
                        textSize = (INIT_TIME_SIZE[3] - 1).toFloat()
                    }
                    holder.nameTextView.apply {
                        textSize = (INIT_NAME_SIZE[3] - 1).toFloat()
                    }
                }
            }
            holder.itemView.requestLayout()
        }
    }

    var itemWidth = 0f
    private var items = listOf<App>()

    fun setItems(list: List<App>) {
        this.items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val holder = VH(
                LayoutInflater.from(
                        parent.context
                ).inflate(layoutId, parent, false)
        )
        holder.itemView.layoutParams?.let {
            it.width = itemWidth.toInt()
        }

        return holder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        items[position].isChecked = checkPos == position
        holder.bind(items[position])
//        holder.contentContainer.setBackgroundResource(TEST_COLOR[position % 4])

    }

    var callUpdateAllHolder: (() -> Unit?)? = null
    var checkPos = -1
    fun check(pos:Int) {
        checkPos = pos
        items.forEachIndexed { index, app ->
            app.isChecked = index==checkPos
        }
        callUpdateAllHolder?.invoke()
    }


    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        initViewParams(holder = holder)
    }


    private fun initViewParams(holder: VH) {
        holder.itemView.layoutParams?.let {
            it.width = itemWidth.toInt()
        }
        holder.contentContainer.scaleX = INIT_ZOOM_SCALE[1]
        holder.contentContainer.scaleY = INIT_ZOOM_SCALE[1]
        holder.contentContainer.alpha = INIT_ZOOM_SCALE[1]
        val position = holder.layoutPosition
        resetViewParams(position, holder)
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        public val container: View = itemView.findViewById(R.id.itemContainer)
        public val contentContainer: View = itemView.findViewById(R.id.contentContainer)
        public val imageView: ImageView = itemView.findViewById(R.id.imageView)
        public val nameTextView: TextView = itemView.findViewById(R.id.name)
        public val timeTextView: TextView = itemView.findViewById(R.id.time)

        init {
            view.setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bind(app: App) {
            imageView.setImageResource(app.drawable)
            nameTextView.text = app.name
            timeTextView.text = app.rating.toString()

            imageView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, view.width / 2f)
                }
            }
            imageView.clipToOutline = true
            imageView.background =
                    if (app.isChecked)
                        ColorDrawable(Color.parseColor("#3377ff"))
                    else
                        null
        }

        override fun onClick(v: View?) {
            this@AppAdapter.check(this.adapterPosition)
        }
    }
}
