package com.example.todoapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todoapplication.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class],version = 1)
abstract class TaskDataBase  : RoomDatabase() {

abstract fun taskDao() :TaskDao

        class CallBack  @Inject constructor(
           private val dataBase: Provider<TaskDataBase>,
         @ApplicationScope private  val applicationscope : CoroutineScope
        ) :RoomDatabase.Callback()
        {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
               //db operations
           val dao =  dataBase.get().taskDao()

               applicationscope.launch {
                   dao.insert(Task("abc"))
                   dao.insert(Task("abc",important = true))
                   dao.insert(Task("poho"))
                   dao.insert(Task("ghsf",completed = true))
                   dao.insert(Task("abc"))
                   dao.insert(Task("abc",completed = true))
               }
            }
        }
}