package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.data.local.entity.account.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun awaitSingle(id: Int): AccountEntity?

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun subscribeSingle(id: String): Flow<AccountEntity?>

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()

}