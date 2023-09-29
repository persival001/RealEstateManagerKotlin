package com.persival.realestatemanagerkotlin.ui.add_property

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.databinding.AddPropertyEmptyStateItemBinding
import com.persival.realestatemanagerkotlin.databinding.ItemAddPropertyBinding

class AddPropertyListAdapter :
    ListAdapter<AddPropertyViewStateItem, AddPropertyListAdapter.PropertyViewHolder>(
        PropertyItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder =
        when (AddPropertyViewStateItem.Type.values()[viewType]) {
            AddPropertyViewStateItem.Type.PHOTO -> PropertyViewHolder.Photo.create(parent)
            AddPropertyViewStateItem.Type.EMPTY_STATE -> PropertyViewHolder.EmptyState.create(parent)
        }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        when (holder) {
            is PropertyViewHolder.Photo -> holder.bind(getItem(position) as AddPropertyViewStateItem.Photo, position)
            is PropertyViewHolder.EmptyState -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    fun updateList(list: List<AddPropertyViewStateItem>) {
        val updatedList = if (list.isEmpty() || list[0].type == AddPropertyViewStateItem.Type.EMPTY_STATE) {
            listOf(AddPropertyViewStateItem.EmptyState) + list
        } else {
            list
        }
        submitList(updatedList)
    }

    sealed class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        class Photo(val binding: ItemAddPropertyBinding) : PropertyViewHolder(binding.root) {
            companion object {
                fun create(parent: ViewGroup): Photo {
                    val binding = ItemAddPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return Photo(binding)
                }
            }

            fun bind(item: AddPropertyViewStateItem.Photo, position: Int) {
                binding.itemDescriptionEditText.text = item.description

                Glide.with(binding.itemImageView)
                    .load(item.photoUrl)
                    .override(800, 800)
                    .into(binding.itemImageView)

                binding.deleteButton.setOnClickListener { item.onDeleteEvent.invoke(position) }
            }

        }

        class EmptyState(val binding: AddPropertyEmptyStateItemBinding) : PropertyViewHolder(binding.root) {
            companion object {
                fun create(parent: ViewGroup): EmptyState {
                    val binding =
                        AddPropertyEmptyStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return EmptyState(binding)
                }
            }
        }
    }

    object PropertyItemDiffCallback : DiffUtil.ItemCallback<AddPropertyViewStateItem>() {
        override fun areItemsTheSame(
            oldItem: AddPropertyViewStateItem,
            newItem: AddPropertyViewStateItem
        ): Boolean {
            return when {
                oldItem is AddPropertyViewStateItem.Photo && newItem is AddPropertyViewStateItem.Photo -> oldItem.id == newItem.id
                oldItem is AddPropertyViewStateItem.EmptyState && newItem is AddPropertyViewStateItem.EmptyState -> true
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: AddPropertyViewStateItem,
            newItem: AddPropertyViewStateItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}


