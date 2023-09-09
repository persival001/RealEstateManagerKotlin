package com.persival.realestatemanagerkotlin.ui.detail

import android.content.Context
import android.net.Uri
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
    private val items: List<DetailViewStateItem>
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
        val item = items[position]

        if (item.url.startsWith("http://") || item.url.startsWith("https://")) {
            // It's a web URL, load it directly
            Glide.with(context)
                .load(item.url)
                .centerCrop()
                .into(holder.imageView)
        } else if (item.url.startsWith("content://") || item.url.startsWith("file://")) {
            // It's a local file URI, load it directly
            Glide.with(context)
                .load(Uri.parse(item.url))
                .centerCrop()
                .into(holder.imageView)
        }

        holder.captionView.text = item.caption
    }

    override fun getItemCount(): Int {
        return items.size
    }
}