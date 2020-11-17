package com.mago.imagenesapdf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mago.imagenesapdf.R
import com.mago.imagenesapdf.model.ImageItem
import kotlinx.android.synthetic.main.content_image_adapter.view.*


/**
 * @author by jmartinez
 * @since 10/11/2020.
 */
class ImageAdapter() : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private var images: List<ImageItem> = arrayListOf()
    private lateinit var onItemClickListener: OnItemClickListener

    private var selected = 0
    private var selectedItemPosition: ArrayList<Int> = arrayListOf()
    private var imagesSelected: ArrayList<ImageItem> = arrayListOf()
    //private var originalValues: List<ImageItem> = images

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.content_image_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        holder.itemView.tv_path.text = getFileName(image.path)
        if (image.isDirectory) {
            holder.itemView.iv_thumbnail.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_folder)
            )
            holder.itemView.setOnClickListener {
                if (::onItemClickListener.isInitialized)
                    onItemClickListener.onDirectoryClick(image)
            }
        } else {
            if (image.isSelected)
                holder.itemView.iv_selected.visibility = View.VISIBLE
            else
                holder.itemView.iv_selected.visibility = View.GONE

            if (image.thumbBm != null)
                holder.itemView.iv_thumbnail.setImageBitmap(image.thumbBm)
            else
                holder.itemView.iv_thumbnail.setImageDrawable(
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_broken_image)
                )
            holder.itemView.setOnClickListener {
                val hasItemSelected = !images.none { it.isSelected }
                if (hasItemSelected) {
                    multipleImageSelected(image, holder.itemView, position)
                } else {
                    if (::onItemClickListener.isInitialized)
                        onItemClickListener.onImageClick(image)
                }
            }
            holder.itemView.setOnLongClickListener {
                multipleImageSelected(image, holder.itemView, position)
                true
            }
        }

    }

    override fun getItemCount(): Int = images.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setupAdapter(imagesList: List<ImageItem>) {
        images = imagesList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    private fun getFileName(file: String): String {
        return file.split("/").last()
    }

    private fun multipleImageSelected(image: ImageItem, view: View, position: Int) {
        when {
            image.isSelected -> {
                onImageRemoved(position, image)
                view.iv_selected.visibility = View.GONE
            }
            else -> {
                if (!imagesSelected.contains(image)) {
                    selected++
                    imagesSelected.add(image)
                }
                view.iv_selected.visibility = View.VISIBLE
            }
        }
        image.isSelected = !image.isSelected
        notifyDataSetChanged()

        if (::onItemClickListener.isInitialized)
            if (image.isSelected)
                onItemClickListener.onImageAdd(imagesSelected)
            else
                onItemClickListener.onImageRemove(imagesSelected)
    }

    fun setItemNoSelected(position: Int) {
        onImageRemoved(position, imagesSelected[position])
        notifyDataSetChanged()
    }

    fun removeAllSelectedImages() {
        selected = 0
        imagesSelected = arrayListOf()
        images.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    private fun onImageRemoved(position: Int, image: ImageItem) {
        selected--
        selectedItemPosition.remove(position)
        imagesSelected.remove(image)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}