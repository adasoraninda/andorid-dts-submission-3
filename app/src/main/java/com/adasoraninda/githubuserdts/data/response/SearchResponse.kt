package com.adasoraninda.githubuserdts.data.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @field:SerializedName("items")
    val users: List<UserResponse>? = null
)