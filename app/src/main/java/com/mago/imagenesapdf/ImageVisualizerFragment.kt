package com.mago.imagenesapdf

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mago.imagenesapdf.adapter.SelectedImagesAdapter
import com.mago.imagenesapdf.extensions.removeFragment
import com.mago.imagenesapdf.model.ImageItem
import com.mago.imagenesapdf.util.FragmentInstanceManager
import kotlinx.android.synthetic.main.fragment_image_visualizer.*

class ImageVisualizerFragment : Fragment() {
    private lateinit var imagesList: List<ImageItem>
    //private lateinit var imageDescriptionList: List<ImageDescription>
    private lateinit var currentImage: ImageItem
    //private lateinit var cameraFragmentListener: CameraFragmentListener
    private lateinit var listener: Listener

    interface Listener {
        fun onImagesSelected(imageItemList: List<ImageItem>)
    }

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
        FragmentInstanceManager().addFragmentInstance(TAG, this)

        listener = FragmentInstanceManager().findFragmentByTag(CameraFragment.TAG) as Listener

        val imageListJson = arguments?.getString(IMAGES_PARAM) ?: ""
        val type = object : TypeToken<List<ImageItem>>() {}.type
        imagesList = Gson().fromJson<List<ImageItem>>(imageListJson, type) ?: arrayListOf()
        //cameraFragmentListener = activity as CameraFragmentListener
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

    override fun onDetach() {
        FragmentInstanceManager().removeFragmentInstance(TAG)
        super.onDetach()
    }

    private fun setup() {
        setOnClickListeners()
        setupImagesRV()
        setImagePreview(imagesList[0])
    }

    private fun setOnClickListeners() {
        btn_close.setOnClickListener {
            parentFragmentManager.removeFragment(this)
        }
        btn_delete_image.setOnClickListener {

        }
        btn_add_image.setOnClickListener {

        }
        btn_send.setOnClickListener {
            //cameraFragmentListener.onImageSelection(imageDescriptionList)
            //findNavController().navigateUp()
            listener.onImagesSelected(imagesList)
            parentFragmentManager.removeFragment(this)
        }
        et_description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (::currentImage.isInitialized)
                    currentImage.description = s?.toString() ?: ""
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setImagePreview(imageItem: ImageItem) {
        //if (imagesList.isEmpty())
        //    return
/*
        val imageItem = imagesList[pos].apply {
            if (previewBm == null) {
                val imageBm = BitmapUtil.decodeBitmapFromFile(path, 800, 600)
                previewBm = imageBm
            }
        }

 */
        iv_image.setImageBitmap(imageItem.previewBm)
    }

    private fun setupImagesRV() {
        /*
        imageDescriptionList = imagesList.map { imageItem ->
            ImageDescription(imageItem.path, "", imageItem.imageBm, imageItem.previewBm)
        }

        if (imageDescriptionList.size == 1) {
            rv_images.visibility = View.GONE
            return
        }
         */

        val adapter = SelectedImagesAdapter()
        adapter.setupAdapter(imagesList)

        rv_images.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_images.adapter = adapter

        adapter.setOnItemClickListener(object : SelectedImagesAdapter.OnItemClickListener {
            override fun onItemClick(imageItem: ImageItem) {
                setImagePreview(imageItem)
                et_description.setText(imageItem.description)
                currentImage = imageItem
            }
        })
    }

    private fun getFragmentArgs() {
        val mArgs: ImageVisualizerFragmentArgs by navArgs()
        val type = object : TypeToken<List<ImageItem>>() {}.type
        imagesList = Gson().fromJson<List<ImageItem>>(mArgs.imagesListJson, type) ?: arrayListOf()
    }

}
