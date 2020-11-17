package com.mago.imagenesapdf

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mago.imagenesapdf.extensions.replaceFragment
import com.mago.imagenesapdf.model.ImageItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File


class MainActivity : AppCompatActivity(), CameraFragmentListener {

    companion object {
        val imagesFolder = arrayListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //getFile(Environment.getExternalStorageDirectory())

        openCamera()

        btn_open_camera.setOnClickListener {
            openCamera()
        }

    }

    override fun onImageSelection(imageItemList: List<ImageItem>) {
        tv_text.visibility = View.VISIBLE
        btn_open_camera.visibility = View.VISIBLE

        val sb = StringBuilder()
        imageItemList.forEach { imageDescription ->
            sb.append(imageDescription.path)
            sb.append("\n")
        }
        tv_text.text = sb.toString()
    }

    private fun getFile(dir: File) {
        val listFile = dir.listFiles()
        if (listFile != null && listFile.isNotEmpty()) {
            for (file in listFile) {
                if (file.isDirectory) {
                    getFile(file)
                } else {
                    if (file.name.endsWith(".png")
                        || file.name.endsWith(".jpg")
                        || file.name.endsWith(".jpeg")
                        || file.name.endsWith(".gif")
                        || file.name.endsWith(".bmp")
                        || file.name.endsWith(".webp")
                    ) {
                        val temp: String =
                            file.path.substring(0, file.path.lastIndexOf('/'))
                        if (!imagesFolder.contains(temp)) imagesFolder.add(temp)
                    }
                }
            }
        }

    }

    private fun openCamera() {
        supportFragmentManager.replaceFragment(
            R.id.ly_container,
            CameraFragment.newInstance(),
            CameraFragment.TAG
        )
        tv_text.visibility = View.GONE
        btn_open_camera.visibility = View.GONE
    }

}
