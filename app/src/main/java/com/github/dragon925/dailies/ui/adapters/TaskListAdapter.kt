package com.github.dragon925.dailies.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.dragon925.dailies.databinding.ItemRowBinding
import com.github.dragon925.dailies.ui.models.TimeRow

class TaskListAdapter(
    private val onTaskClick: (id: Int) -> Unit,
    private val onTaskPress: (id: Int) -> Unit
) : ListAdapter<TimeRow, TaskListAdapter.RowViewHolder>(
    ItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RowViewHolder(
        ItemRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RowViewHolder(
        private val binding: ItemRowBinding
    ) : ViewHolder(binding.root) {

        fun bind(row: TimeRow) {
            binding.time.text = row.time
            val adapter = (binding.listTasks.adapter as? RowAdapter) ?: run {
                val newAdapter = RowAdapter(onTaskClick, onTaskPress)
                binding.listTasks.adapter = newAdapter
                return@run newAdapter
            }
            adapter.submitList(row.tasks)
        }
    }

    private class ItemDiffCallback : DiffUtil.ItemCallback<TimeRow>() {

        override fun areItemsTheSame(oldItem: TimeRow, newItem: TimeRow): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: TimeRow, newItem: TimeRow): Boolean {
            return oldItem == newItem
        }
    }
}