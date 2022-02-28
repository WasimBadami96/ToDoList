package com.example.todoapplication.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.data.Task
import com.example.todoapplication.databinding.ItemTaskBinding

class TaskAdapter(private val listener :OnItemClickListener)  : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
       val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
       val item = getItem(position)
        holder.bind(item)
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task,isChecked: Boolean)
    }

    class DiffCallBack :DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
             oldItem == newItem


    }

 inner  class TaskViewHolder(private val binding: ItemTaskBinding)  : RecyclerView.ViewHolder(binding.root)
    {
            init {
                binding.apply {
                    root.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION){
                            val task = getItem(position)
                            listener.onItemClick(task)
                        }
                    }
                    checkBoxCompleted.setOnClickListener {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION)
                        {
                            val task = getItem(position)
                            listener.onCheckBoxClick(task,checkBoxCompleted.isChecked)
                        }
                    }

                }
            }

        fun bind(task: Task)
        {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }



}