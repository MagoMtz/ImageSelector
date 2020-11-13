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
                    onItemClickListener.onDirectoryClick(image.path)
            }
        } else {
            if (image.imageBm != null)
                holder.itemView.iv_thumbnail.setImageBitmap(image.imageBm)
            else
                holder.itemView.iv_thumbnail.setImageDrawable(
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_broken_image)
                )
            holder.itemView.setOnClickListener {
                if (::onItemClickListener.isInitialized)
                    onItemClickListener.onImageClick(image.path)
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

    private fun isImageFile(filePath: String): Boolean {
        return filePath.endsWith(".jpg") ||
                filePath.endsWith(".png") ||
                filePath.endsWith(".jpeg")
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}