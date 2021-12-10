package com.adasoraninda.githubuserdts.local

import androidx.room.*
import com.adasoraninda.githubuserdts.data.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveData(user: UserEntity): Long

    @Delete
    suspend fun deleteData(user: UserEntity): Int

    @Query("SELECT * FROM users")
    suspend fun getAllData(): List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun deleteAllData()

    @Query("SELECT COUNT(*) > 0 FROM users WHERE users.id = :userId")
    suspend fun isDataExists(userId: Long): Boolean

}