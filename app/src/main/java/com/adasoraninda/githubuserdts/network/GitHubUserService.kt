package com.adasoraninda.githubuserdts.network

import com.adasoraninda.githubuserdts.data.response.SearchResponse
import com.adasoraninda.githubuserdts.data.response.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubUserService {

    companion object {
        const val BASE_URL = "http://api.github.com"
    }

    @GET("search/users")
    fun findUsers(
        @Query("q") query: String?
    ): Call<SearchResponse>

    @GET("users/{username}")
    fun getDetailUser(
        @Path("username") username: String?
    ): Call<UserResponse>

    @GET("users/{username}/following")
    fun getUserFollowing(
        @Path("username") username: String?
    ): Call<List<UserResponse>>

    @GET("users/{username}/followers")
    fun getUserFollowers(
        @Path("username") username: String?
    ): Call<List<UserResponse>>

}