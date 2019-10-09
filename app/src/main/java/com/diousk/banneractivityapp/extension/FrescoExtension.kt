@file:JvmName("FrescoUtils")

package com.diousk.banneractivityapp.extension

import android.content.res.Resources
import android.graphics.drawable.Animatable
import android.net.Uri
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.diousk.banneractivityapp.extension.ImageResizeOptions.BANNER_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.COVER_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.DEFAULT_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.HEAD_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.ICON_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.LARGE_COVER_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.LARGE_HEAD_IMG
import com.diousk.banneractivityapp.extension.ImageResizeOptions.bannerImgOptions
import com.diousk.banneractivityapp.extension.ImageResizeOptions.coverImgOptions
import com.diousk.banneractivityapp.extension.ImageResizeOptions.headImgOptions
import com.diousk.banneractivityapp.extension.ImageResizeOptions.iconImgOptions
import com.diousk.banneractivityapp.extension.ImageResizeOptions.largeCoverImgOptions
import com.diousk.banneractivityapp.extension.ImageResizeOptions.largeHeadImgOptions
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.util.UriUtil
import com.facebook.datasource.BaseDataSubscriber
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

// dp/px conversion
val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.formatWithSeparator(): String {
    if (this < 1000) return "" + this
    return String.format(Locale.US, "%,d", this)
}

fun Int?.formatWithSeparator(): String {
    if (this == null) return "0"
    if (this < 1000) return "" + this
    return String.format(Locale.US, "%,d", this)
}

object ImageResizeOptions {
    val iconImgOptions = ResizeOptions(40.toPx, 40.toPx)
    val bannerImgOptions = ResizeOptions(1600, 900)
    val coverImgOptions = ResizeOptions(650, 350)
    val largeCoverImgOptions = ResizeOptions(1300, 700)
    val headImgOptions = ResizeOptions(36.toPx, 36.toPx)
    val largeHeadImgOptions = ResizeOptions(96.toPx, 96.toPx)

    const val DEFAULT_IMG = 2000
    const val ICON_IMG = 2001
    const val BANNER_IMG = 2002
    const val COVER_IMG = 2003
    const val HEAD_IMG = 2004
    const val LARGE_COVER_IMG = 2005
    const val LARGE_HEAD_IMG = 2006
}

object ImagePrefetcher {
    private const val DISK_PREFETCH = 0
    private const val MEMORY_PREFETCH = 1

    // prefetch to disk
    fun prefetch(@NonNull url: String, type: Int = DISK_PREFETCH): DataSource<Void> {
        val imagePipeline = Fresco.getImagePipeline()
        val request = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(url))
            .build()

        return if (type == MEMORY_PREFETCH) {
            imagePipeline.prefetchToBitmapCache(request, null)
        } else {
            imagePipeline.prefetchToDiskCache(request, null, Priority.HIGH)
        }
    }
}

fun SimpleDraweeView.setBorderColor(color: Int) {
    val params = hierarchy.roundingParams
    params?.borderColor = color
    hierarchy.roundingParams = params
}

fun SimpleDraweeView.loadImgUrl(imgUrl: String?, viewType: Int = DEFAULT_IMG) {
    val resizeOptions = when (viewType) {
        ICON_IMG -> iconImgOptions
        BANNER_IMG -> bannerImgOptions
        COVER_IMG -> coverImgOptions
        HEAD_IMG -> headImgOptions
        LARGE_COVER_IMG -> largeCoverImgOptions
        else -> iconImgOptions
    }

    val cacheChoice = when (viewType) {
        ICON_IMG -> ImageRequest.CacheChoice.SMALL
        HEAD_IMG -> ImageRequest.CacheChoice.SMALL
        else -> ImageRequest.CacheChoice.DEFAULT
    }

    val request = ImageRequestBuilder
        .newBuilderWithSource(Uri.parse(imgUrl))
        .setResizeOptions(resizeOptions)
        .setCacheChoice(cacheChoice)
        .build()

    // Load image url.
    controller = Fresco.newDraweeControllerBuilder().apply {
        oldController = controller
        imageRequest = request
    }.build()
}

fun SimpleDraweeView.loadImgFile(filePath: String, viewType: Int = DEFAULT_IMG) {
    val resizeOptions = when (viewType) {
        ICON_IMG -> iconImgOptions
        BANNER_IMG -> bannerImgOptions
        COVER_IMG -> coverImgOptions
        HEAD_IMG -> headImgOptions
        LARGE_COVER_IMG -> largeCoverImgOptions
        LARGE_HEAD_IMG -> largeHeadImgOptions
        else -> null
    }

    val cacheChoice = when (viewType) {
        ICON_IMG, HEAD_IMG -> ImageRequest.CacheChoice.SMALL
        else -> ImageRequest.CacheChoice.DEFAULT
    }

    val request = ImageRequestBuilder
        .newBuilderWithSource(Uri.fromFile(File(filePath)))
        .setResizeOptions(resizeOptions)
        .setCacheChoice(cacheChoice)
        .build()

    // Load image
    controller = Fresco.newDraweeControllerBuilder().apply {
        oldController = controller
        imageRequest = request
    }.build()
}

// NOTE: this method doesn't  work with adaptive icon.
// fresco not support in 1.11.0
fun SimpleDraweeView.loadResourceUri(
    resId: Int,
    packageName: String
) {

    val resizeOptions = ImageResizeOptions.iconImgOptions

    val iconUri = UriUtil.getUriForQualifiedResource(packageName, resId)

    val request = ImageRequestBuilder.newBuilderWithSource(iconUri)
        .setResizeOptions(resizeOptions)
        .setCacheChoice(ImageRequest.CacheChoice.SMALL)
        .build()

    controller = Fresco.newDraweeControllerBuilder().apply {
        oldController = controller
        imageRequest = request
    }.build()
}

/**
 * Changes Simple Drawee View Rounding params.
 * @param imgResId Image resource id.
 * @param isRound Is image round?
 * @param backgroundColorId Background color resource id.
 * @param scaleType Scale type.
 */
fun SimpleDraweeView.loadDrawable(
    imgResId: Int,
    isRound: Boolean = false,
    backgroundColorId: Int = android.R.color.black,
    scaleType: ScalingUtils.ScaleType = ScalingUtils.ScaleType.CENTER_CROP
) {

    hierarchy = GenericDraweeHierarchyBuilder(context.resources)
        .setPlaceholderImage(imgResId)
        .setPlaceholderImageScaleType(scaleType)
        .setRoundingParams(RoundingParams().apply {
            roundAsCircle = isRound
            roundingMethod = RoundingParams.RoundingMethod.BITMAP_ONLY
        })
        .setBackground(ContextCompat.getDrawable(context, backgroundColorId))
        .build()

    // Reset controller to avoid old images.
    controller = Fresco.newDraweeControllerBuilder().apply {
        callerContext = context
        oldController = controller
    }.build()
}

fun SimpleDraweeView.loadWrapImageUrl(uri: String) {
    fun updateSize(imageInfo: ImageInfo?) {
        if (imageInfo != null) {
            this.aspectRatio = imageInfo.width.toFloat() / imageInfo.height.toFloat()
        }
    }

    val baseControllerListener = object : BaseControllerListener<ImageInfo>() {

        override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
            super.onIntermediateImageSet(id, imageInfo)
            updateSize(imageInfo)
        }

        override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
            super.onFinalImageSet(id, imageInfo, animatable)
            updateSize(imageInfo)
        }
    }

    this.controller = Fresco.newDraweeControllerBuilder().also {
        it.setUri(uri)
        it.controllerListener = baseControllerListener
    }.build()
}