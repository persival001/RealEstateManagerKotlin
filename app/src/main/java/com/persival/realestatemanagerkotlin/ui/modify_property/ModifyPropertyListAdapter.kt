package com.persival.realestatemanagerkotlin.ui.modify_property

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.persival.realestatemanagerkotlin.databinding.AddPropertyEmptyStateItemBinding
import com.persival.realestatemanagerkotlin.databinding.ItemAddPropertyBinding

class ModifyPropertyListAdapter :
    ListAdapter<ModifyPropertyViewStateItem, ModifyPropertyListAdapter.PropertyViewHolder>(
        PropertyItemDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder =
        when (ModifyPropertyViewStateItem.Type.values()[viewType]) {
            ModifyPropertyViewStateItem.Type.PHOTO -> PropertyViewHolder.Photo.create(parent)
            ModifyPropertyViewStateItem.Type.EMPTY_STATE -> PropertyViewHolder.EmptyState.create(parent)
        }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        when (holder) {
            is PropertyViewHolder.Photo -> holder.bind(getItem(position) as ModifyPropertyViewStateItem.Photo)
            is PropertyViewHolder.EmptyState -> Unit
        }
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    fun updateList(list: List<ModifyPropertyViewStateItem>) {
        val updatedList = if (list.isEmpty() || list[0].type == ModifyPropertyViewStateItem.Type.EMPTY_STATE) {
            listOf(ModifyPropertyViewStateItem.EmptyState) + list
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

            fun bind(item: ModifyPropertyViewStateItem.Photo) {
                binding.itemDescriptionEditText.text = item.description

                Glide.with(binding.itemImageView)
                    .load(item.photoUrl)
                    .override(800, 800)
                    .into(binding.itemImageView)

                binding.deleteButton.setOnClickListener { item.onDeleteEvent.invoke() }
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

    object PropertyItemDiffCallback : DiffUtil.ItemCallback<ModifyPropertyViewStateItem>() {
        override fun areItemsTheSame(
            oldItem: ModifyPropertyViewStateItem,
            newItem: ModifyPropertyViewStateItem
        ): Boolean {
            return when {
                oldItem is ModifyPropertyViewStateItem.Photo && newItem is ModifyPropertyViewStateItem.Photo -> oldItem.id == newItem.id
                oldItem is ModifyPropertyViewStateItem.EmptyState && newItem is ModifyPropertyViewStateItem.EmptyState -> true
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: ModifyPropertyViewStateItem,
            newItem: ModifyPropertyViewStateItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}


