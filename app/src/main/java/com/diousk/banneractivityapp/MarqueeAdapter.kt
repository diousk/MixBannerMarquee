package com.diousk.banneractivityapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.diousk.banneractivityapp.widgets.VerticalMarqueeView
import kotlinx.android.synthetic.main.view_holder_marquee_item.view.*

class MarqueeAdapter : VerticalMarqueeView.Adapter<MarqueeItemHolder>() {

    var dataList = mutableListOf<Item>()
        set(value) {
            dataList.clear()
            dataList.addAll(value)
        }

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup?): MarqueeItemHolder {
        return MarqueeItemHolder(parent!!)
    }

    override fun onBindViewHolder(holder: MarqueeItemHolder?, position: Int) {
        val item = dataList[position]
        holder?.title?.text = item.title
    }
}

class MarqueeItemHolder(
    parent: ViewGroup
) : VerticalMarqueeView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.view_holder_marquee_item,
        parent, false
    )
) {
    var title: TextView = itemView.title
}