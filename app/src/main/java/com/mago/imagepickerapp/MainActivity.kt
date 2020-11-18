package com.mago.imagepickerapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mago.imagepicker.ui.CameraFragment
import com.mago.imagepicker.ui.CameraFragmentListener
import com.mago.imagepicker.model.ImageItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(),
    CameraFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        btn_open_camera.setOnClickListener {
            checkForPermissions()
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

    private fun checkForPermissions() {
        Dexter.withContext(applicationContext)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted())
                        openCamera()

                    if (report.deniedPermissionResponses.isNotEmpty()) {
                        Toast.makeText(applicationContext, getString(R.string.permissions_denied_message), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(R.string.permissions_title)
                        .setMessage(R.string.permissions_message)
                        .setNegativeButton(
                            android.R.string.cancel
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.cancelPermissionRequest()
                        }
                        .setPositiveButton(
                            android.R.string.ok
                        ) { dialog, _ ->
                            dialog.dismiss()
                            token?.continuePermissionRequest()
                        }
                        .setOnDismissListener { token?.cancelPermissionRequest() }
                        .show()
                }
            })
            .check()
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
