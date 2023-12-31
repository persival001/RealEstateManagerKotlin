package com.persival.realestatemanagerkotlin.ui.add_or_modify_property

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.databinding.AddPropertyEmptyStateItemBinding
import com.persival.realestatemanagerkotlin.databinding.ItemAddPropertyBinding

class AddOrModifyPropertyListAdapter :
    ListAdapter<AddOrModifyPropertyViewStateItem, AddOrModifyPropertyListAdapter.PropertyViewHolder>(
        PropertyItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder =
        when (AddOrModifyPropertyViewStateItem.Type.values()[viewType]) {
            AddOrModifyPropertyViewStateItem.Type.PHOTO -> PropertyViewHolder.Photo.create(parent)
            AddOrModifyPropertyViewStateItem.Type.EMPTY_STATE -> PropertyViewHolder.EmptyState.create(parent)
        }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        when (holder) {
            is PropertyViewHolder.Photo -> holder.bind(
                getItem(position) as AddOrModifyPropertyViewStateItem.Photo,
                position
            )

            is PropertyViewHolder.EmptyState -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    fun updateList(list: List<AddOrModifyPropertyViewStateItem>) {
        val updatedList = if (list.isEmpty() || list[0].type == AddOrModifyPropertyViewStateItem.Type.EMPTY_STATE) {
            listOf(AddOrModifyPropertyViewStateItem.EmptyStateOrModify) + list
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

            fun bind(item: AddOrModifyPropertyViewStateItem.Photo, position: Int) {

                Glide.with(binding.itemImageView)
                    .load(item.photoUrl)
                    .override(800, 800)
                    .into(binding.itemImageView)

                binding.itemDescriptionEditText.text = item.description

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

    object PropertyItemDiffCallback : DiffUtil.ItemCallback<AddOrModifyPropertyViewStateItem>() {
        override fun areItemsTheSame(
            oldItem: AddOrModifyPropertyViewStateItem,
            newItem: AddOrModifyPropertyViewStateItem
        ): Boolean {
            return when {
                oldItem is AddOrModifyPropertyViewStateItem.Photo && newItem is AddOrModifyPropertyViewStateItem.Photo -> oldItem.id == newItem.id
                oldItem is AddOrModifyPropertyViewStateItem.EmptyStateOrModify && newItem is AddOrModifyPropertyViewStateItem.EmptyStateOrModify -> true
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: AddOrModifyPropertyViewStateItem,
            newItem: AddOrModifyPropertyViewStateItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}


