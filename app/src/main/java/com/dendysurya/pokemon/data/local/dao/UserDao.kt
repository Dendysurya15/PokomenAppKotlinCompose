package com.dendysurya.pokemon.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dendysurya.pokemon.model.UserData

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserData): Long

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserData?

    @Query("SELECT * FROM user WHERE email = :email AND token = :token")
    suspend fun getUserByCredentials(email: String, token: String): UserData?

    @Update
    suspend fun updateUser(user: UserData)

    @Delete
    suspend fun deleteUser(user: UserData)
}