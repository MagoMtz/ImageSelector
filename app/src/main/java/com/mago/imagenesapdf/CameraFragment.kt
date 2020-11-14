package com.mago.imagenesapdf

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mago.imagenesapdf.adapter.ImageAdapter
import com.mago.imagenesapdf.adapter.OnItemClickListener
import com.mago.imagenesapdf.extensions.addFragment
import com.mago.imagenesapdf.extensions.removeFragment
import com.mago.imagenesapdf.model.ImageItem
import com.mago.imagenesapdf.util.BitmapUtil
import com.mago.imagenesapdf.util.FragmentInstanceManager
import com.mago.imagenesapdf.util.ImageFileFilter
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.camera_bottom_sheet.*
import kotlinx.android.synthetic.main.camera_bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_camera_content.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CameraFragment : Fragment(), ImageVisualizerFragment.Listener {
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private val pathStack = Stack<String>()
    private val disposables = CompositeDisposable()
    private lateinit var cameraFragmentListener: CameraFragmentListener

    private var imageItemList: ArrayList<ImageItem> = arrayListOf()
    private lateinit var peekAdapter: ImageAdapter
    private lateinit var mainAdapter: ImageAdapter

    companion object {
        const val TAG = "CameraFragment"

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FragmentInstanceManager().addFragmentInstance(TAG, this)
        cameraFragmentListener = activity as CameraFragmentListener
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

    override fun onDetach() {
        FragmentInstanceManager().removeFragmentInstance(TAG)
        super.onDetach()
    }

    override fun onImagesSelected(imageItemList: List<ImageItem>) {
        cameraFragmentListener.onImageSelection(imageItemList)
        parentFragmentManager.removeFragment(this)
    }

    override fun onImageRemoved(imageItem: ImageItem) {
        val pos = imageItemList.indexOf(
            imageItemList.find { it.path == imageItem.path }
        )
        imageItemList[pos].isSelected = false
        if (::mainAdapter.isInitialized)
            mainAdapter.setItemNoSelected(pos)
        imageItemList.removeAt(pos)
    }

    private fun setup() {
        setupCameraListener()
        setClickListeners()
        setupSheetBehavior()
        //setupPeekRV()
        setupFirstMainRV()
    }

    private fun setupCameraListener() {
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val fileName = "IMG_${SimpleDateFormat("ddMMyyyy_HHmmSS",Locale.getDefault()).format(Date())}.jpg"
                result.toFile(File(AppConstants.IMAGES_SAVED_PATH.plus("/MCamera/$fileName"))) { file ->
                    if (file == null)
                        return@toFile

                    imageItemList.add(0,
                        ImageItem(
                            file.absolutePath,
                            false,
                            BitmapUtil.decodeBitmapFromFile(file.absolutePath, 100, 100),
                            BitmapUtil.decodeBitmapFromFile(file.absolutePath, 800, 600)
                        )
                    )
                    navigateToImageVisualizer(imageItemList)
                }
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
        btn_subdirectory_back.setOnClickListener {
            subdirectoryBack()
        }
        btn_send_selected_images.setOnClickListener {
            navigateToImageVisualizer(imageItemList)
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
                        camera.close()
                        ly_peek_view.visibility = View.GONE
                        rv_main.suppressLayout(false)
                        sheetBehavior.isDraggable = false
                    }
                    STATE_COLLAPSED -> {
                        ly_peek_view.visibility = View.VISIBLE
                        sheetBehavior.isDraggable = true
                        camera.open()
                    }
                    STATE_DRAGGING -> {
                        rv_main.suppressLayout(true)
                    }

                    else -> {
                    }

                }
            }

        })
    }

    /*
    private fun setupPeekRV() {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .absolutePath.plus("/Camera")
        peekAdapter = ImageAdapter()
        adapterChangeSource(directory, peekAdapter)

        rv_peek.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rv_peek.adapter = peekAdapter

        peekAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onImageClick(imageItem: ImageItem) {
                onSingleImageSelected(imageItem)
            }

            override fun onImageAdd(imageItemList: List<ImageItem>) {
                onMultipleImageSelected(imageItemList)
            }

            override fun onImageRemove(imageItemList: List<ImageItem>) {
                if (imageItemList.isEmpty())
                    btn_send_selected_images.visibility = View.GONE
                this@CameraFragment.imageItemList = ArrayList(imageItemList)
            }

            override fun onNoImageSelected() {
                btn_send_selected_images.visibility = View.GONE
            }
            override fun onDirectoryClick(imageItem: ImageItem) {}
        })
    }

     */

    private fun setupFirstMainRV() {
        val externalStorage = Environment.getExternalStorageDirectory().absolutePath
        val externalStorageDCIM =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val camera = externalStorageDCIM.plus("/Camera")
        pathStack.push(externalStorage)
        pathStack.push(externalStorageDCIM)
        setupMainRV(camera)
    }

    private fun setupMainRV(directory: String) {
        tv_location.text = directory

        mainAdapter = ImageAdapter()
        adapterChangeSource(directory, mainAdapter)

        rv_main.layoutManager = GridLayoutManager(context, 3)
        rv_main.adapter = mainAdapter
        rv_main.scrollToPosition(0)
        rv_main.addOnScrollListener(mainRVScrollListener())

        mainAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onDirectoryClick(imageItem: ImageItem) {
                val file = File(imageItem.path)
                adapterChangeSource(imageItem.path, mainAdapter)
                if (file.parent != null) {
                    pathStack.push(file.parentFile?.absolutePath)
                    tv_location.text = file.absolutePath
                    btn_subdirectory_back.visibility = View.VISIBLE
                }
            }

            override fun onImageClick(imageItem: ImageItem) {
                onSingleImageSelected(imageItem)
            }

            override fun onImageAdd(imageItemList: List<ImageItem>) {
                onMultipleImageSelected(imageItemList)
            }

            override fun onImageRemove(imageItemList: List<ImageItem>) {
                if (imageItemList.isEmpty())
                    btn_send_selected_images.visibility = View.GONE
                this@CameraFragment.imageItemList = ArrayList(imageItemList)
            }

            override fun onNoImageSelected() {
                btn_send_selected_images.visibility = View.GONE
            }
        })
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

    private fun subdirectoryBack() {
        if (pathStack.empty()) {
            return
        }
        removeAllSelectedImages()
        val lastDir = pathStack.pop()

        val parent = File(lastDir).listFiles() ?: arrayOf()
        if (!parent.isNullOrEmpty()) {
            setupMainRV(lastDir)
        }

        if (pathStack.empty())
            btn_subdirectory_back.visibility = View.INVISIBLE
    }

    private fun createImageThumbnails(directoryPath: String): List<ImageItem> {
        val items = ArrayList<ImageItem>()
        val files = File(directoryPath).listFiles(ImageFileFilter())
        files?.forEach { file ->
            if (file.isDirectory && !file.listFiles(ImageFileFilter()).isNullOrEmpty()) {
                items.add(ImageItem(file.absolutePath, true, null, null))
            } else {
                if (!file.isDirectory) {
                    val imageBm = BitmapUtil.decodeBitmapFromFile(file.absolutePath, 100, 100)
                    val previewBm = BitmapUtil.decodeBitmapFromFile(file.absolutePath, 800, 600)
                    items.add(
                        ImageItem(
                            file.absolutePath,
                            false,
                            imageBm,
                            previewBm
                        )
                    )
                }
            }
        }
        return items
    }

    private fun adapterChangeSource(directory: String, adapter: ImageAdapter) {
        ly_shimmer.startShimmerAnimation()
        ly_shimmer.visibility = View.VISIBLE
        rv_main.visibility = View.GONE
        val obs = Observable.fromCallable { createImageThumbnails(directory) }
        disposables.add(
            obs.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data ->
                    adapter.setupAdapter(data)
                    ly_shimmer.visibility = View.GONE
                    rv_main.visibility = View.VISIBLE
                    ly_shimmer.stopShimmerAnimation()
                }
        )
    }

    private fun navigateToImageVisualizer(imagesList: List<ImageItem>) {
        /*
        val navHostFragment = findNavController()
        val action = CameraFragmentDirections.actionCameraFragmentToImageVisualizerFragment(Gson().toJson(imagesList))
        navHostFragment.navigate(action)
         */
        parentFragmentManager.addFragment(
            R.id.ly_container,
            ImageVisualizerFragment.newInstance(imagesList),
            ImageVisualizerFragment.TAG
        )
    }

    private fun onSingleImageSelected(imageItem: ImageItem) {
        navigateToImageVisualizer(listOf(imageItem))
    }

    private fun onMultipleImageSelected(imageItemList: List<ImageItem>) {
        btn_send_selected_images.visibility = View.VISIBLE
        this.imageItemList = ArrayList(imageItemList)
    }

    private fun removeAllSelectedImages() {
        btn_send_selected_images.visibility = View.GONE
        imageItemList = arrayListOf()
        mainAdapter.removeAllSelectedImages()
    }

}