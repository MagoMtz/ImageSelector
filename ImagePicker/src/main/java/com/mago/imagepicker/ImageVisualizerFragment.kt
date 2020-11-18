package com.mago.imagepicker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mago.imagepicker.adapter.SelectedImagesAdapter
import com.mago.imagepicker.extensions.removeFragment
import com.mago.imagepicker.model.ImageItem
import com.mago.imagepicker.util.FragmentInstanceManager
import kotlinx.android.synthetic.main.fragment_image_visualizer.*

class ImageVisualizerFragment : Fragment() {
    private lateinit var imagesList: ArrayList<ImageItem>
    private lateinit var currentImage: ImageItem
    private lateinit var listener: Listener
    private lateinit var adapter: SelectedImagesAdapter
    private var shouldUpdateDescription = false

    interface Listener {
        fun onImagesSelected(imageItemList: List<ImageItem>)
        fun onImageRemoved(imageItem: ImageItem)
        fun onVisualizerClose()
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
        val type = object : TypeToken<ArrayList<ImageItem>>() {}.type
        imagesList = Gson().fromJson<ArrayList<ImageItem>>(imageListJson, type) ?: arrayListOf()
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
        shouldHideRemoveImageOption()
    }

    private fun setOnClickListeners() {
        btn_close.setOnClickListener {
            imagesList = arrayListOf()
            adapter.removeAllItems()
            listener.onVisualizerClose()
            parentFragmentManager.removeFragment(this)
        }
        btn_delete_image.setOnClickListener {
            removeImage()
        }
        btn_add_image.setOnClickListener {
            parentFragmentManager.removeFragment(this)
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
                if (s != null)
                    et_description.setSelection(s.length)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupImagesRV() {
        adapter = SelectedImagesAdapter()
        adapter.setupAdapter(imagesList)
        adapter.setOnItemClickListener(object : SelectedImagesAdapter.OnItemClickListener {
            override fun onItemClick(imageItem: ImageItem) {
                currentImage = imageItem
                setImagePreview(imageItem)
            }
        })

        rv_images.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_images.adapter = adapter

        currentImage = imagesList[0]
        shouldUpdateDescription = true
        adapter.setItemSelected(0)
    }

    private fun setImagePreview(imageItem: ImageItem) {
        shouldUpdateDescription = false
        iv_image.setImageBitmap(imageItem.previewBm)
        et_description.setText(imageItem.description)
        adapter.setItemSelected(imagesList.indexOf(imageItem))
        currentImage = imageItem
    }

    private fun removeImage() {
        val eliminatedImage = currentImage
        val pos = imagesList.indexOf(currentImage)
        imagesList.removeAt(pos)
        adapter.removeItem(pos)
        if (pos < 1)
            setImagePreview(imagesList[pos])
        else
            setImagePreview(imagesList[pos -1])

        shouldHideRemoveImageOption()

        if (::listener.isInitialized)
            listener.onImageRemoved(eliminatedImage)
    }

    private fun shouldHideRemoveImageOption() {
        if (imagesList.size <= 1)
            btn_delete_image.visibility = View.GONE
        else
            btn_delete_image.visibility = View.VISIBLE
    }

}
