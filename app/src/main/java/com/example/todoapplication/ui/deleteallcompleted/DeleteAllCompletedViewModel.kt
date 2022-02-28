package com.example.todoapplication.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.example.todoapplication.data.TaskDao
import com.example.todoapplication.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao ,
    @ApplicationScope private val applicationScope :CoroutineScope
) :ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTask()
    }
}