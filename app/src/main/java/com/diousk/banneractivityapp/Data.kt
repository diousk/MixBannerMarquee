package com.diousk.banneractivityapp

object DataManager {
    fun getDataList(): List<Data> {
        val list = mutableListOf<Data>()
        // add rank data
        val itemsIcon = mutableListOf<Item>()
        itemsIcon.add(Item(icon = "https://www.pngfind.com/pngs/m/35-351451_baby-winnie-the-pooh-and-friends-clipart-pooh.png"))
        itemsIcon.add(Item(icon = "https://www.pngfind.com/pngs/m/131-1319935_download-transparent-png-baby-christmas-winnie-the-pooh.png"))
        itemsIcon.add(Item(icon = "https://www.pngfind.com/pngs/m/35-350265_at-the-movies-winnie-the-pooh-png-transparent.png"))
        val rankData = Data(TYPE_HOUR_RANK, itemsIcon, 120)
        list.add(rankData)

        // add activity data
        val itemsMarquee = mutableListOf<Item>()
        itemsMarquee.add(Item(title = "臣亮言：先帝創業未半"))
        itemsMarquee.add(Item(title = "而中道崩殂，今天下三分"))
        itemsMarquee.add(Item(title = "益州疲敝"))
        val marqueeData = Data(TYPE_VERTICAL_MARQUEE, itemsMarquee)
        list.add(marqueeData)
        return list
    }

    const val sampleImage = "https://cdn.images.express.co.uk/img/dynamic/78/590x/winnie-the-pooh-banned-china-why-ban-xi-jingping-829586.jpg"
}

data class Data(val type: Int, val items: List<Item>, val remains: Long = 0)

data class Item(val title: String = "", val icon: String = "")

const val TYPE_HOUR_RANK = 0
const val TYPE_VERTICAL_MARQUEE = 1