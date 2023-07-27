package com.persival.realestatemanagerkotlin.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.R


class DetailImageAdapter(
    private val context: Context,
    private val imageUrls: List<String>,
    private val captions: List<String>
) : RecyclerView.Adapter<DetailImageAdapter.DetailImageViewHolder>() {

    inner class DetailImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val captionView: TextView = itemView.findViewById(R.id.imageCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_layout, parent, false)
        return DetailImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailImageViewHolder, position: Int) {
        Glide.with(context)
            .load(imageUrls[position])
            .into(holder.imageView)

        holder.captionView.text = captions[position]
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}





