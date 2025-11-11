package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.AccountDao

@Database(
    entities = [

    ],
    version = 1,
    exportSchema = false
)
abstract class SRWDatabase: RoomDatabase() {
    abstract fun accountDao(): AccountDao
}