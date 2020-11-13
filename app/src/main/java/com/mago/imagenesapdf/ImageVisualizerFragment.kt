package com.mago.imagenesapdf

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mago.imagenesapdf.model.ImageDescription
import com.mago.imagenesapdf.model.ImageItem
import com.mago.imagenesapdf.util.BitmapUtil
import kotlinx.android.synthetic.main.fragment_image_visualizer.*

class ImageVisualizerFragment : Fragment() {
    private lateinit var imagesList: List<ImageItem>
    private lateinit var imageDescriptionList: List<ImageDescription>
    private lateinit var currentImage: Bitmap

    companion object {
        const val TAG = "ImageVisualizerFragment"
        const val IMAGES_PARAM = "images_param"

        @JvmStatic
        fun newInstance(imagesList: List<ImageItem>): ImageVisualizerFragment {
            val imagesJson = Gson().toJson(imagesList)

            return ImageVisualizerFragment().apply {
                arguments = Bundle().apply {
                    putString(IMAGES_PARAM, imagesJson)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imagesJson = arguments?.getString(IMAGES_PARAM) ?: ""
        val type = object : TypeToken<List<ImageItem>>() {}.type
        imagesList = Gson().fromJson<List<ImageItem>>(imagesJson, type) ?: arrayListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_visualizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        setOnClickListeners()
        setupImagesRV()
        setImagePreview(0)
    }

    private fun setOnClickListeners() {
        btn_close.setOnClickListener {

        }
        btn_delete_image.setOnClickListener {

        }
        btn_add_image.setOnClickListener {

        }
    }

    private fun setImagePreview(pos: Int) {
        if (imagesList.isEmpty())
            return

        val imageDescription = imagesList[pos]
        val ivWidth = iv_image.width
        val ivHeight = iv_image.height
        val imageBm = BitmapUtil.decodeBitmapFromFile(imageDescription.path, ivWidth, ivHeight)
        iv_image.setImageBitmap(imageBm)
    }

    private fun setupImagesRV() {
        imageDescriptionList = imagesList.map { imageItem ->
            ImageDescription(imageItem.path, "")
        }

        if (imageDescriptionList.isEmpty()) {
            rv_images.visibility = View.GONE
            return
        }
    }

}
