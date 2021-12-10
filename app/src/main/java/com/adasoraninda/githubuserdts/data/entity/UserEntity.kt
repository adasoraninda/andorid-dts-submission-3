package com.adasoraninda.githubuserdts.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adasoraninda.githubuserdts.data.domain.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "photo")
    val photo: String,

    @ColumnInfo(name = "user_name")
    val username: String,

    @ColumnInfo(name = "name")
    val name: String
)

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        name = name,
        photo = photo
    )
}