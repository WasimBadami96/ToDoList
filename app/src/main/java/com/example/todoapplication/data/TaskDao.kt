package com.example.todoapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
 interface TaskDao{

    fun getTask(query: String, sortOrder: SortOrder, hideCompleted: Boolean) :Flow<List<Task>> =
        when(sortOrder){
            SortOrder.BY_DATE ->
                getTaskSortByDateCreated(query,hideCompleted)
            SortOrder.BY_NAME ->
                getTaskSortByName(query,hideCompleted)
        }

    @Query("select * from task_table where (completed != :hideCompleted OR completed = 0) AND name Like '%' || :searchQuery || '%' Order by important DESC ,name")
     fun  getTaskSortByName(searchQuery: String,hideCompleted :Boolean) : Flow<List<Task>>

    @Query("select * from task_table where (completed != :hideCompleted OR completed = 0) AND name Like '%' || :searchQuery || '%' Order by important DESC ,created")
    fun  getTaskSortByDateCreated(searchQuery: String,hideCompleted :Boolean) : Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insert(task: Task)

     @Update
     suspend fun update(task: Task)

     @Delete
     suspend fun  delete(task: Task)

     @Query("Delete from task_table where completed = 1")
     suspend fun deleteCompletedTask()
 }
