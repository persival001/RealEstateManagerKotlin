package com.persival.realestatemanagerkotlin.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.R

class PropertyListAdapter(
    private val onItemClicked: (PropertyViewStateItem) -> Unit
) : ListAdapter<PropertyViewStateItem, PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()) {
    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO ViewBinding instead !
        private val propertyType: TextView = itemView.findViewById(R.id.property_type)
        private val propertyAddress: TextView = itemView.findViewById(R.id.property_address)
        private val propertyPrice: TextView = itemView.findViewById(R.id.property_price)
        private val propertyPicture: ImageView = itemView.findViewById(R.id.property_picture)
        private val soldImageView: ImageView = itemView.findViewById(R.id.sold_imageview)
        private val soldTextView: TextView = itemView.findViewById(R.id.sold_textview)

        fun bind(item: PropertyViewStateItem, callback : (PropertyViewStateItem) -> Unit) {
            propertyType.text = item.type
            propertyAddress.text = item.address
            propertyPrice.text = item.price
            Glide.with(itemView.context)
                .load(item.pictureUri)
                .into(propertyPicture)
            soldImageView.visibility = if (item.isSold) View.VISIBLE else View.GONE
            soldTextView.visibility = if (item.isSold) View.VISIBLE else View.GONE
            itemView.setOnClickListener { callback.invoke(item) }
        }
    }

    class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyViewStateItem>() {
        override fun areItemsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PropertyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
    )

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }
}
