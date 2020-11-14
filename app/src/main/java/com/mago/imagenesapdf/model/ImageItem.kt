package com.mago.imagenesapdf.model

import android.graphics.Bitmap

/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
data class ImageItem (
    var path: String,
    var isDirectory: Boolean,
    var imageBm: Bitmap?,
    var previewBm: Bitmap?,
    var description: String = "",
    var isSelected: Boolean = false,
    var isPreviewSelected: Boolean = false
)