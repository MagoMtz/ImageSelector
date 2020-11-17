package com.mago.imagenesapdf.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.renderscript.*
import com.mago.imagenesapdf.AppConstants
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.io.FileOutputStream


/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
object BitmapUtil {

    fun decodeBitmapFromFile(
        imagePath: String?,
        reqWidth: Int,
        reqHeight: Int,
        rotation: Int
    ): Bitmap? { // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        val bm = BitmapFactory.decodeFile(imagePath, options)
        return if (rotation == 0)
            bm
        else rotatedBitmap(bm, rotation)
    }

    fun decodeBitmapFromByteArray(
        byteArray: ByteArray,
        reqWidth: Int,
        reqHeight: Int,
        rotation: Int
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = calculateSampleSize(options, reqHeight, reqWidth)
        options.inJustDecodeBounds = false
        val bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return if (rotation == 0)
            bm
        else rotatedBitmap(bm, rotation)
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

    private fun rotatedBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun saveBitmapToFileAsync(bm: Bitmap, name: String, dirName: String): String {
        val filePath = AppConstants.getPicturesPath(dirName).plus("/$name")
        val dirs = File(AppConstants.getPicturesPath(dirName))
        if (!dirs.exists())
            dirs.mkdirs()

        val outputStream = FileOutputStream(filePath)
        bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return filePath
    }

    fun createBitmapThumbnailFromByteArray(
        byteArray: ByteArray,
        reqWidth: Int,
        reqHeight: Int,
        rotation: Int
    ): Bitmap {
        val bm = decodeBitmapFromByteArray(byteArray, reqWidth, reqHeight, rotation)
        return Bitmap.createScaledBitmap(bm!!, reqWidth, reqHeight, false)
    }

    fun createBitmapThumbnailFromFile(
        imagePath: String?,
        reqWidth: Int,
        reqHeight: Int,
        rotation: Int
    ): Bitmap {
        val bm = decodeBitmapFromFile(imagePath, reqWidth, reqHeight, rotation)
        return Bitmap.createScaledBitmap(bm!!, reqWidth, reqHeight, false)
    }

}