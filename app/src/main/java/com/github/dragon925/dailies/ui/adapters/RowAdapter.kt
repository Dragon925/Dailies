package com.github.dragon925.dailies.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.dragon925.dailies.databinding.ItemTaskBinding
import com.github.dragon925.dailies.ui.models.TaskItem

class RowAdapter(
    private val onTaskClick: (id: Int) -> Unit,
    private val onTaskPress: (id: Int) -> Unit
) : ListAdapter<TaskItem, RowAdapter.TaskViewHolder>(
    ItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskViewHolder(
        ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : ViewHolder(binding.root) {

        fun bind(item: TaskItem) {
            val taskInfo = "${item.name} – ${item.start}"
            binding.taskInfo.text = taskInfo
            binding.root.setOnClickListener { onTaskClick(item.id) }
            binding.root.setOnLongClickListener {
                onTaskPress(item.id)
                true
            }
        }
    }

    private class ItemDiffCallback : DiffUtil.ItemCallback<TaskItem>() {

        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem == newItem
        }
    }
}