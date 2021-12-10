package com.adasoraninda.githubuserdts.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("login")
    val username: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("avatar_url")
    val photoUrl: String? = null
)

