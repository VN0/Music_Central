/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.absolute.groove.mcentral.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.absolute.groove.appthemehelper.util.ATHUtil
import com.absolute.groove.mcentral.App
import com.absolute.groove.mcentral.R
import com.absolute.groove.mcentral.glide.palette.BitmapPaletteTarget
import com.absolute.groove.mcentral.glide.palette.BitmapPaletteWrapper
import com.absolute.groove.mcentral.util.color.MediaNotificationProcessor
import com.bumptech.glide.request.animation.GlideAnimation


abstract class RetroMusicColoredTarget(view: ImageView) : BitmapPaletteTarget(view) {

    protected val defaultFooterColor: Int
        get() = ATHUtil.resolveColor(getView().context, R.attr.colorControlNormal)

    abstract fun onColorReady(colors: MediaNotificationProcessor)

    override fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
        super.onLoadFailed(e, errorDrawable)
        val colors = MediaNotificationProcessor(App.getContext(), errorDrawable)
        onColorReady(colors)
        /* MediaNotificationProcessor(App.getContext()).getPaletteAsync({
            onColorReady(it)
        }, errorDrawable)*/
    }

    override fun onResourceReady(
        resource: BitmapPaletteWrapper?,
        glideAnimation: GlideAnimation<in BitmapPaletteWrapper>?
    ) {
        super.onResourceReady(resource, glideAnimation)
        resource?.let { bitmapWrap ->
            MediaNotificationProcessor(App.getContext()).getPaletteAsync({
                onColorReady(it)
            }, bitmapWrap.bitmap)
        }
    }
}
