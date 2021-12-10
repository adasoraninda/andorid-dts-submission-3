package com.adasoraninda.githubuserdts.data.domain

import com.adasoraninda.githubuserdts.data.entity.UserEntity
import com.adasoraninda.githubuserdts.data.response.UserResponse

data class User(
    val id: Long,
    val photo: String,
    val username: String,
    val name: String
)

fun UserResponse.toDomain(): User {
    return User(
        id = id ?: 0,
        username = username.orEmpty(),
        name = name.orEmpty(),
        photo = photoUrl.orEmpty()
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        name = name,
        photo = photo
    )
}
