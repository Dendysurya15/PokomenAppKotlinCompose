package com.dendysurya.pokemon.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserData(
    @PrimaryKey
    val email: String,
    val name: String,
    val token: String
)