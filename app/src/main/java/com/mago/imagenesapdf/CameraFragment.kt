package com.mago.imagenesapdf

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mago.imagenesapdf.adapter.ImageAdapter
import com.mago.imagenesapdf.adapter.OnItemClickListener
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.android.synthetic.main.camera_bottom_sheet.*
import kotlinx.android.synthetic.main.camera_bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_camera_content.*
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

class CameraFragment : Fragment() {
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private val pathStack = Stack<String>()
    private lateinit var spAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (sheetBehavior.state == STATE_EXPANDED && !pathStack.empty()) {
                setupFolderSpinner(createImageFolderList(pathStack.pop()))
            } else if (sheetBehavior.state == STATE_EXPANDED) {
                sheetBehavior.state = STATE_COLLAPSED
            }
        }
        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        camera.setLifecycleOwner(viewLifecycleOwner)
        setup()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setup() {
        setupCameraListener()
        setClickListeners()
        setupSheetBehavior()
        setupPeekRV()
        setupMainRV()
        setupFolderSpinner()
    }

    private fun setupCameraListener() {
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {

            }
        })
    }

    private fun setClickListeners() {
        ly_bottom_sheet.btn_back.setOnClickListener {
            sheetBehavior.state = STATE_COLLAPSED
        }
        btn_capture.setOnClickListener {
            camera.takePicture()
        }
    }

    private fun setupSheetBehavior() {
        sheetBehavior = from(ly_bottom_sheet)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                ly_peek_view.alpha = 1f - slideOffset
                ly_collapsed_view.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_HIDDEN -> {
                        sheetBehavior.isDraggable = true
                    }
                    STATE_EXPANDED -> {
                        ly_peek_view.visibility = View.GONE
                        rv_main.suppressLayout(false)
                        sheetBehavior.isDraggable = false
                    }
                    STATE_COLLAPSED -> {
                        ly_peek_view.visibility = View.VISIBLE
                        sheetBehavior.isDraggable = true
                    }
                    STATE_DRAGGING -> {
                        rv_main.suppressLayout(true)
                    }

                    else -> {}

                }
            }

        })
    }

    private fun setupPeekRV() {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath.plus("/Camera")
        val adapter = ImageAdapter()
        adapter.setupAdapter(directory)

        rv_peek.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_peek.adapter = adapter
    }

    private fun setupMainRV() {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath.plus("/Camera")
        val adapter = ImageAdapter()
        adapter.setupAdapter(directory)
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onDirectoryClick(path: String) {
                val file = File(path)
                adapter.setupAdapter(path)
                if (file.parent != null) {
                    pathStack.push(file.parentFile?.absolutePath)
                    val folderList = createImageFolderList(file.parent!!)
                    setupFolderSpinner(folderList)
                }
            }
            override fun onImageClick(path: String) {}
        })

        rv_main.layoutManager = GridLayoutManager(context, 3)
        rv_main.adapter = adapter
        rv_main.scrollToPosition(0)
        rv_main.addOnScrollListener(mainRVScrollListener())
    }

    private fun setupFolderSpinner(folderList: List<String> = createImageFolderList()) {
        spAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, folderList)
        sp_location.adapter = spAdapter
        sp_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val imagesAdapter = rv_main.adapter as ImageAdapter
                imagesAdapter.setupAdapter(folderList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mainRVScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!rv_main.canScrollVertically(-1)) {
                    // RV reached top
                    if (!sheetBehavior.isDraggable) {
                        // now draggable
                        sheetBehavior.isDraggable = true
                    }
                } else {
                    if (sheetBehavior.isDraggable) {
                        sheetBehavior.isDraggable = false
                    }
                }
            }
        }
    }

    private fun createImageFolderList(path: String = Environment.getExternalStorageDirectory().absolutePath): List<String> {
        val items = ArrayList<String>()

        val files = File(path).listFiles(ImageFileFilter())
        files?.forEach { file ->
            if (file.isDirectory && !file.listFiles(ImageFileFilter()).isNullOrEmpty()) {
                items.add(file.absolutePath)
            }
        }

        return items
    }

}
