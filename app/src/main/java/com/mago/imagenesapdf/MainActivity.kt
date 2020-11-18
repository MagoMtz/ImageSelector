package com.mago.imagenesapdf

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mago.imagepicker.CameraFragment
import com.mago.imagepicker.CameraFragmentListener
import com.mago.imagepicker.model.ImageItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), CameraFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
            sb.append("path: ${imageDescription.path}")
            sb.append("\n")
            sb.append("desc: ${imageDescription.description}")
            sb.append("\n")
        }
        tv_text.text = sb.toString()
    }

    private fun openCamera() {
        val camera = CameraFragment.Companion.Builder()
            .build()
        supportFragmentManager.replaceFragment(
            R.id.ly_container,
            camera,
            CameraFragment.TAG
        )
        tv_text.visibility = View.GONE
        btn_open_camera.visibility = View.GONE
    }

}
