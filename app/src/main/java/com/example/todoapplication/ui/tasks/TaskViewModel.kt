package com.example.todoapplication.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.todoapplication.data.PreferencesManager
import com.example.todoapplication.data.SortOrder
import com.example.todoapplication.data.Task
import com.example.todoapplication.data.TaskDao
import com.example.todoapplication.ui.ADD_TASK_RESULT_OK
import com.example.todoapplication.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao ,
    private val preferencesManager: PreferencesManager ,
    @Assisted private val state : SavedStateHandle

        ):ViewModel() {


       // val searchQuery = MutableStateFlow("")
        val searchQuery = state.getLiveData("searchQuery","")
        val preferencesFlow = preferencesManager.preferencesFlow
        private val taskEventChannel = Channel<TaskEvent>()
        val taskEvent = taskEventChannel.receiveAsFlow()
//        val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//        val hideCompleted = MutableStateFlow(false)

        private val taskFlow = combine(
            searchQuery.asFlow() ,
            preferencesFlow
        )   {
             query ,filterPreferences ->
                Pair(query,filterPreferences)
            }
            .flatMapLatest {(query,filterPreferences) ->
                taskDao.getTask(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
            }
    val task = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }
    fun onHideCompletedClick(hideCompleted :Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task :Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task,isChecked:Boolean) =
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }

    fun onTaskSwipe(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) = viewModelScope.launch {
        when(result){
            ADD_TASK_RESULT_OK -> showTaskConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskConfirmationMessage("Task updated")

        }
    }

    private fun showTaskConfirmationMessage(text :String) = viewModelScope.launch {
            taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteCompletedAllClick() = viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    sealed class TaskEvent{
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) :TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) :TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg : String) :TaskEvent()
        object NavigateToDeleteAllCompletedScreen :TaskEvent()
    }

}

