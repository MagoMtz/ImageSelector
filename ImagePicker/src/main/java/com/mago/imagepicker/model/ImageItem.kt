package com.mago.imagepicker.model

import android.graphics.Bitmap

/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
data class ImageItem (
    var path: String,
    var isDirectory: Boolean,
    var thumbBm: Bitmap?,
    var previewBm: Bitmap?,
    var description: String = "",
    var isSelected: Boolean = false,
    var isPreviewSelected: Boolean = false,
    var isFromCamera: Boolean = false
)