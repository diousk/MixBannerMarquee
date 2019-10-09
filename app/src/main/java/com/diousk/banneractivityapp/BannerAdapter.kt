package com.diousk.banneractivityapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diousk.banneractivityapp.extension.loadImgUrl
import com.diousk.banneractivityapp.widgets.LRollPagerView
import com.diousk.banneractivityapp.widgets.LoopPagerAdapter
import kotlinx.android.synthetic.main.pager_layout_hour_rank.view.*
import kotlinx.android.synthetic.main.pager_layout_marquee.view.*
import timber.log.Timber

class BannerAdapter(
    viewPager: LRollPagerView
) : LoopPagerAdapter(viewPager) {

    var dataList = mutableListOf<Data>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    override fun getView(container: ViewGroup?, position: Int): View {
        val itemView = LayoutInflater.from(container!!.context).inflate(
            getLayoutId(dataList[position].type),
            container, false
        )
        getViewHolder(dataList[position].type, itemView)?.bind(dataList[position])
        return itemView
    }

    override fun getRealCount(): Int = dataList.size

    private fun getLayoutId(type: Int): Int {
        return when (type) {
            TYPE_HOUR_RANK -> R.layout.pager_layout_hour_rank
            TYPE_VERTICAL_MARQUEE -> R.layout.pager_layout_marquee
            else -> 0
        }
    }

    private fun getViewHolder(type: Int, itemView: View): ViewHolder? {
        return when (type) {
            TYPE_HOUR_RANK -> HourRankHolder(itemView)
            TYPE_VERTICAL_MARQUEE -> MarqueeViewHolder(itemView)
            else -> null
        }
    }

    abstract class ViewHolder(val itemView: View) {
        abstract fun bind(data: Data)
    }
}

class HourRankHolder(view: View) : BannerAdapter.ViewHolder(view) {
    override fun bind(data: Data) {
        itemView.iconRank1.loadImgUrl(data.items[0].icon)
        itemView.iconRank2.loadImgUrl(data.items[1].icon)
        itemView.iconRank3.loadImgUrl(data.items[2].icon)
    }
}

class MarqueeViewHolder(view: View) : BannerAdapter.ViewHolder(view) {
    private val adapter by lazy { MarqueeAdapter() }

    init {
        itemView.marqueeView.setAdapter(adapter)
    }

    override fun bind(data: Data) {
        Timber.d("MarqueeViewHolder bind $data")
        adapter.dataList = data.items.toMutableList()
        itemView.marqueeView.run {
            stop()
            start()
        }
    }
}