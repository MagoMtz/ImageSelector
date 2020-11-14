package com.mago.imagenesapdf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mago.imagenesapdf.R
import com.mago.imagenesapdf.model.ImageItem
import kotlinx.android.synthetic.main.content_selected_images_adapter.view.*

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
class SelectedImagesAdapter: RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder>() {
    private var imagesSelectedList: List<ImageItem> = arrayListOf()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(imageItem: ImageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.content_selected_images_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imagesSelectedList[position]

        holder.itemView.iv_image.setImageBitmap(image.imageBm)
        if (image.isSelected) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
        } else {
            holder.itemView.background = null
        }
        holder.itemView.setOnClickListener {
            //holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
            imagesSelectedList.forEach { it.isSelected = false }
            image.isSelected = true
            //image.isSelected = !image.isSelected
            notifyDataSetChanged()
            if (::listener.isInitialized)
                listener.onItemClick(image)
        }
    }

    override fun getItemCount(): Int = imagesSelectedList.size

    fun setupAdapter(imagesSelectedList: List<ImageItem>) {
        this.imagesSelectedList = imagesSelectedList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateImageDescription(position: Int, description: String) {
        imagesSelectedList[position].description = description
        notifyItemChanged(position)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}