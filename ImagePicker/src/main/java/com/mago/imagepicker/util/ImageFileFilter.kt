package com.mago.imagepicker.util

import java.io.File
import java.io.FileFilter

/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
class ImageFileFilter: FileFilter {

    override fun accept(pathname: File?): Boolean {
        if (pathname == null)
            return false


        if (pathname.isDirectory &&
            !isHiddenElement(pathname.name) &&
            !isIgnoredDirectory(pathname.name)
        )
            return true
        else if (isImageFile(pathname.absolutePath))
            return true

        return isImageFile(pathname.absolutePath)
    }

    private fun isImageFile(filePath: String): Boolean {
        return filePath.endsWith(".jpg") ||
                filePath.endsWith(".png") ||
                filePath.endsWith(".jpeg")
    }

    private fun isHiddenElement(filePath: String): Boolean {
        return filePath.startsWith(".")
    }

    private fun isIgnoredDirectory(dirName: String): Boolean {
        return dirName == "Android" ||
                dirName.contains("com.")
    }

}