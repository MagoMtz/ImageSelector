package com.mago.imagenesapdf.adapter

import com.mago.imagenesapdf.model.ImageItem

/**
 * @author by jmartinez
 * @since 11/11/2020.
 */
interface OnItemClickListener {
    fun onDirectoryClick(imageItem: ImageItem)
    fun onImageClick(imageItem: ImageItem)
    fun onImageAdd(imageItemList: List<ImageItem>)
    fun onNoImageSelected()
}