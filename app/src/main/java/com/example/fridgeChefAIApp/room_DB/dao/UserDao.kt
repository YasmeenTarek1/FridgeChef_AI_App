package com.example.fridgeChefAIApp.room_DB.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fridgeChefAIApp.room_DB.model.UserInfo

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userInfo: UserInfo)

    @Update
    suspend fun updateUser(userInfo: UserInfo)

    @Query("SELECT * FROM user_info WHERE id = :userId")
    suspend fun getUserById(userId: String): UserInfo?

}
