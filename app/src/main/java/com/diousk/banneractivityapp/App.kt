package com.diousk.banneractivityapp

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.disk.NoOpDiskTrimmableRegistry
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import timber.log.Timber
import java.util.concurrent.TimeUnit

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        setupFresco(this)
    }

    private fun setupFresco(application: Application) {
        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

        val diskCacheConfig = DiskCacheConfig.newBuilder(application)
            .setMaxCacheSize(100 * ByteConst.MB.toLong())
            .setMaxCacheSizeOnLowDiskSpace(60 * ByteConst.MB.toLong())
            .setMaxCacheSizeOnVeryLowDiskSpace(20 * ByteConst.MB.toLong())
            .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
            .build()

        val smallImageDiskCacheConfig = DiskCacheConfig.newBuilder(application)
            .setMaxCacheSize((20 * ByteConst.MB).toLong())
            .setMaxCacheSizeOnLowDiskSpace((12 * ByteConst.MB).toLong())
            .setMaxCacheSizeOnVeryLowDiskSpace((4 * ByteConst.MB).toLong())
            .setDiskTrimmableRegistry(NoOpDiskTrimmableRegistry.getInstance())
            .build()


        val config = OkHttpImagePipelineConfigFactory.newBuilder(application, okHttpClient)
            .setDownsampleEnabled(true)
            .setResizeAndRotateEnabledForNetwork(true)
            .setBitmapsConfig(Bitmap.Config.RGB_565)
            .setMainDiskCacheConfig(diskCacheConfig)
            .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
            .build()

        Fresco.initialize(application, config)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
    }
}

object ByteConst {
    const val BYTE = 1
    const val KB = 1024
    const val MB = 1024 * KB
}