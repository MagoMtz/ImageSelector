package com.mago.imagenesapdf

import com.mago.imagenesapdf.model.ImageItem

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
interface CameraFragmentListener {
    fun onImageSelection(imageItemList: List<ImageItem>)
}