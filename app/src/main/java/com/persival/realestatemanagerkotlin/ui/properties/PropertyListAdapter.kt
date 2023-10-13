package com.persival.realestatemanagerkotlin.ui.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.R
import com.persival.realestatemanagerkotlin.databinding.ItemPropertyBinding

class PropertyListAdapter(
    private val onItemClicked: (PropertyViewStateItem) -> Unit
) : ListAdapter<PropertyViewStateItem, PropertyListAdapter.PropertyViewHolder>(PropertyDiffCallback()) {

    var selectedPosition = RecyclerView.NO_POSITION

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PropertyViewStateItem, selectedPos: Int, callback: (PropertyViewStateItem) -> Unit) {

            val formattedString = binding.root.context.getString(R.string.property_description, item.type, item.surface)

            binding.propertyType.text = formattedString
            binding.propertyAddress.text = item.address
            binding.propertyPrice.text = item.price
            binding.roomsTextView.text = item.rooms
            binding.bathroomsTextView.text = item.bathrooms
            binding.bedroomsTextView.text = item.bedrooms
            Glide.with(binding.root.context)
                .load(item.pictureUri)
                .into(binding.propertyPicture)
            binding.soldImageview.visibility = if (item.isSold) View.VISIBLE else View.GONE
            binding.soldTextview.visibility = if (item.isSold) View.VISIBLE else View.GONE

            binding.root.isSelected = selectedPos == adapterPosition

            binding.root.setOnClickListener {
                val currentPosition = adapterPosition
                if (selectedPosition != currentPosition && currentPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = currentPosition
                    notifyItemChanged(selectedPosition)
                }
                callback.invoke(item)
            }
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
        holder.bind(getItem(position), selectedPosition, onItemClicked)
    }

}
