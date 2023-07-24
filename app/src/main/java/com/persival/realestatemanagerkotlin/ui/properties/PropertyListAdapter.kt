package com.persival.realestatemanagerkotlin.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.persival.realestatemanagerkotlin.R

class PropertyListAdapter(
    private val onItemClicked: (PropertyViewStateItem) -> Unit) :
    ListAdapter<PropertyViewStateItem,
        PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()
    ) {
    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyType: TextView = itemView.findViewById(R.id.property_type)
        val propertyAddress: TextView = itemView.findViewById(R.id.property_address)
        val propertyPrice: TextView = itemView.findViewById(R.id.property_price)
        val propertyPicture: ImageView = itemView.findViewById(R.id.property_picture)
        val soldImageView: ImageView = itemView.findViewById(R.id.sold_imageview)
        val soldTextView: TextView = itemView.findViewById(R.id.sold_textview)
    }

    class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyViewStateItem>() {
        override fun areItemsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = getItem(position)
        with(holder) {
            propertyType.text = property.type
            propertyAddress.text = property.address
            propertyPrice.text = property.price
            propertyPicture.setImageResource(property.picture)
            soldImageView.visibility = if (property.isSold) View.VISIBLE else View.GONE
            soldTextView.visibility = if (property.isSold) View.VISIBLE else View.GONE
            itemView.setOnClickListener { onItemClicked(property) }
        }
    }
}
