package com.mago.imagenesapdf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mago.imagenesapdf.R
import com.mago.imagenesapdf.model.ImageDescription
import com.mago.imagenesapdf.util.BitmapUtil
import kotlinx.android.synthetic.main.content_selected_images_adapter.view.*

/**
 * @author by jmartinez
 * @since 13/11/2020.
 */
class SelectedImagesAdapter: RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder>() {
    private var imagesSelectedList: List<ImageDescription> = arrayListOf()
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.content_selected_images_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imagesSelectedList[position]

        holder.itemView.iv_image.setImageBitmap(BitmapUtil.decodeBitmapFromFile(image.path, 60, 60))

        holder.itemView.setOnClickListener {
            if (::listener.isInitialized)
                listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = imagesSelectedList.size

    fun setupAdapter(imagesSelectedList: List<ImageDescription>) {
        this.imagesSelectedList = imagesSelectedList
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}