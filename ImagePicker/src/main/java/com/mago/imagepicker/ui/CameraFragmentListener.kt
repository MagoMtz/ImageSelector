package com.mago.imagepicker.ui

import com.mago.imagepicker.model.ImageItem

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
interface CameraFragmentListener {
    fun onImageSelection(imageItemList: List<ImageItem>)
}