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
    private lateinit var currentImage: ImageItem
    private lateinit var listener: Listener
    private lateinit var adapter: SelectedImagesAdapter
    private var shouldUpdateDescription = false

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
            listener.onImagesSelected(imagesList)
            parentFragmentManager.removeFragment(this)
        }
        et_description.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (::currentImage.isInitialized && shouldUpdateDescription) {
                    val description = s?.toString() ?: ""
                    val pos = imagesList.indexOf(currentImage)
                    adapter.updateImageDescription(pos, description)
                }
                shouldUpdateDescription = true
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setImagePreview(imageItem: ImageItem) {
        shouldUpdateDescription = false
        iv_image.setImageBitmap(imageItem.previewBm)
        et_description.setText(imageItem.description)
    }

    private fun setupImagesRV() {
        adapter = SelectedImagesAdapter()
        adapter.setupAdapter(imagesList)

        rv_images.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_images.adapter = adapter

        adapter.setOnItemClickListener(object : SelectedImagesAdapter.OnItemClickListener {
            override fun onItemClick(imageItem: ImageItem) {
                currentImage = imageItem
                setImagePreview(imageItem)
            }
        })
    }

    private fun getFragmentArgs() {
        val mArgs: ImageVisualizerFragmentArgs by navArgs()
        val type = object : TypeToken<List<ImageItem>>() {}.type
        imagesList = Gson().fromJson<List<ImageItem>>(mArgs.imagesListJson, type) ?: arrayListOf()
    }

}
