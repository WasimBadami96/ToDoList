package com.example.todoapplication.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.R
import com.example.todoapplication.data.SortOrder
import com.example.todoapplication.data.Task
import com.example.todoapplication.databinding.FragmentTaskBinding
import com.example.todoapplication.utils.exhaustive
import com.example.todoapplication.utils.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment :Fragment(R.layout.fragment_task) ,TaskAdapter.OnItemClickListener{

        private val viewModel :TaskViewModel by viewModels()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

             val binding = FragmentTaskBinding.bind(view)
                val taskAdapter = TaskAdapter(this)

                binding.apply {
                        recyclerViewTasks.apply {
                                adapter = taskAdapter
                                layoutManager = LinearLayoutManager(requireContext())
                                setHasFixedSize(true)
                        }
                    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val task = taskAdapter.currentList[viewHolder.adapterPosition]
                            viewModel.onTaskSwipe(task)
                        }
                    }).attachToRecyclerView(recyclerViewTasks)

                    fabAddTasks.setOnClickListener {
                        viewModel.onAddNewTaskClick()
                    }

                }

            setFragmentResultListener("add_edit_request") { _, bundle ->
                val result = bundle.getInt("add_edit_result")
                viewModel.onAddEditResult(result)
            }
                viewModel.task.observe(viewLifecycleOwner){
                        taskAdapter.submitList(it)
                }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.taskEvent.collect { event ->

                    when(event){
                        is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage ->
                        {
                            Snackbar.make(requireView(),"Task deleted",Snackbar.LENGTH_LONG)
                                .setAction("Undo"){
                                    viewModel.onUndoDeleteClick(event.task)
                                }.show()
                        }
                        is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTask(null,"New Task")
                            findNavController().navigate(action)
                        }
                        is TaskViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTask(event.task,"Edit Task")
                            findNavController().navigate(action)
                        }
                        is TaskViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                            Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_SHORT).show()
                        }
                        TaskViewModel.TaskEvent.NavigateToDeleteAllCompletedScreen -> {
                            val action = TaskFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                            findNavController().navigate(action)
                        }
                    }.exhaustive
                }
            }
            setHasOptionsMenu(true)
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_task,menu)

        val searchitem = menu.findItem(R.id.action_search)
        val searchView = searchitem.actionView as SearchView

            searchView.onQueryTextChanged {
                //update search query
                viewModel.searchQuery.value = it
            }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return  when(item.itemId)
        {
            R.id.action_sort_by_name ->
            {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
          R.id.sort_by_date_created ->
          {
              viewModel.onSortOrderSelected(SortOrder.BY_DATE)
              true
          }
          R.id.action_hide_completed ->
          {
              viewModel.onHideCompletedClick(item.isChecked)
            item.isChecked = !item.isChecked

              true
          }
          R.id.delete_all_completed_item ->
          {
            viewModel.onDeleteCompletedAllClick()
              true
          }
            else -> super.onOptionsItemSelected(item)
      }

    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
       viewModel.onTaskCheckedChanged(task,isChecked)
    }


}