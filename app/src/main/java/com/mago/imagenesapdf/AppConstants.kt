package com.mago.imagenesapdf

import android.os.Environment

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
object AppConstants {

    private val IMAGES_SAVED_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath

    fun getPicturesPath(subDirName: String): String {
        return IMAGES_SAVED_PATH.plus("/$subDirName")
    }

}