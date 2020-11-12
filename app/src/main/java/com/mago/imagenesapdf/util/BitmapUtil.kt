package com.mago.imagenesapdf.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory


/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
object BitmapUtil {

    fun decodeBitmapFromFile(
        imagePath: String?,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? { // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(imagePath, options)
    }

    private fun calculateSampleSize(
        options: BitmapFactory.Options,
        reqHeight: Int,
        reqWidth: Int
    ): Int { // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight
                && halfWidth / inSampleSize > reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}