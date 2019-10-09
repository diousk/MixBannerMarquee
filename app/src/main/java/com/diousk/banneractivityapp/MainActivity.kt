package com.diousk.banneractivityapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diousk.banneractivityapp.extension.loadImgUrl
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mixList = DataManager.getDataList()
        Timber.d("mixList = $mixList")

        val bannerAdapter = BannerAdapter(homeBanner)
        homeBanner.setOnItemClickListener {
            Timber.d("homeBanner click = $it")
        }
        homeBanner.setAdapter(bannerAdapter)
        homeBanner.setPlayDelay(3000)
        bannerAdapter.dataList = mixList.toMutableList()

        val adapter = MarqueeAdapter().apply { dataList = mixList[1].items.toMutableList() }
        demoMarquee.setAdapter(adapter)
        demoMarquee.stop()
        demoMarquee.start()

        demoImage.loadImgUrl(DataManager.sampleImage)
    }
}
