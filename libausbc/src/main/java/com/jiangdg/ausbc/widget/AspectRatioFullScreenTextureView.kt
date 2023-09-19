/*
 * Copyright 2017-2023 Jiangdg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiangdg.ausbc.widget

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView
import com.blankj.utilcode.util.ScreenUtils
import com.jiangdg.ausbc.utils.Logger

/**
 *  Adaptive TextureView
 */
class AspectRatioFullScreenTextureView : TextureView, IAspectRatio {

    private var mAspectRatio = -1.0
    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun setAspectRatio(width: Int, height: Int) {
        this.mWidth = width
        this.mHeight = height
        post {
            val orientation = context.resources.configuration.orientation
            // 处理竖屏和横屏情况
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setAspectRatio(height.toDouble() / width)
            }
            setAspectRatio(width.toDouble() / height)
        }

    }

    override fun getSurfaceWidth(): Int = width

    override fun getSurfaceHeight(): Int = height

    override fun getSurface(): Surface? {
        return try {
            Surface(surfaceTexture)
        } catch (e: Exception) {
            null
        }
    }

    override fun postUITask(task: () -> Unit) {
        post {
            task()
        }
    }

    private fun setAspectRatio(aspectRatio: Double) {
        if (aspectRatio < 0 || mAspectRatio == aspectRatio) {
            return
        }
        mAspectRatio = aspectRatio
        Logger.i(TAG, "AspectRatio = $mAspectRatio")
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wMeasureSpec = widthMeasureSpec
        var hMeasureSpec = heightMeasureSpec

        val measureWidthExactly = if (mWidth > 0) mWidth else ScreenUtils.getScreenWidth()
        val measureHeightExactly = if (mHeight > 0) mHeight else ScreenUtils.getScreenHeight()

        wMeasureSpec =
            MeasureSpec.makeMeasureSpec(measureWidthExactly, MeasureSpec.EXACTLY)
        hMeasureSpec =
            MeasureSpec.makeMeasureSpec(measureHeightExactly, MeasureSpec.EXACTLY)
        super.onMeasure(wMeasureSpec, hMeasureSpec)
    }

    companion object {
        private const val TAG = "FullScreenTextureView"
    }
}