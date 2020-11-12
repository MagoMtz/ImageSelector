package com.mago.imagenesapdf

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

        if (pathname.isDirectory && !isHideElement(pathname.name))
            return true
        else if (isImageFile(pathname.absolutePath))
            return true

        return false
    }

    private fun isImageFile(filePath: String): Boolean {
        return filePath.endsWith(".jpg") ||
                filePath.endsWith(".png") ||
                filePath.endsWith(".jpeg")
    }

    private fun isHideElement(filePath: String): Boolean {
        return filePath.startsWith(".")
    }

}