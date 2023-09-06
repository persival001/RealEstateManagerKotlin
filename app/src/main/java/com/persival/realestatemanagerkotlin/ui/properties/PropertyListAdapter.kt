package com.persival.realestatemanagerkotlin.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.databinding.ItemPropertyBinding

class PropertyListAdapter(
    private val onItemClicked: (PropertyViewStateItem) -> Unit
) : ListAdapter<PropertyViewStateItem, PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()) {
    class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PropertyViewStateItem, callback: (PropertyViewStateItem) -> Unit) {
            binding.propertyType.text = item.type
            binding.propertyAddress.text = item.address
            binding.propertyPrice.text = item.price
            Glide.with(binding.root.context)
                .load(item.pictureUri)
                .into(binding.propertyPicture)
            binding.soldImageview.visibility = if (item.isSold) View.VISIBLE else View.GONE
            binding.soldTextview.visibility = if (item.isSold) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { callback.invoke(item) }
        }
    }


    class PropertyDiffCallback : DiffUtil.ItemCallback<PropertyViewStateItem>() {
        override fun areItemsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PropertyViewStateItem, newItem: PropertyViewStateItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding = ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }
}
