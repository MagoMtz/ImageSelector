package com.mago.imagenesapdf

import com.mago.imagenesapdf.model.ImageDescription

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
interface CameraFragmentListener {
    fun onImageSelection(imageDescriptionList: List<ImageDescription>)
}