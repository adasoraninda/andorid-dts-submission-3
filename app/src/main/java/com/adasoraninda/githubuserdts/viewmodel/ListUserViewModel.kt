package com.adasoraninda.githubuserdts.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adasoraninda.githubuserdts.data.domain.User
import com.adasoraninda.githubuserdts.data.domain.toDomain
import com.adasoraninda.githubuserdts.data.response.SearchResponse
import com.adasoraninda.githubuserdts.data.response.UserResponse
import com.adasoraninda.githubuserdts.network.GitHubUserService
import com.adasoraninda.githubuserdts.utils.Event
import com.adasoraninda.githubuserdts.utils.fetch
import retrofit2.Call

private const val TAG = "ListUserViewModel"

class ListUserViewModel(private val service: GitHubUserService) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean> get() = _refresh

    private val _shimmer = MutableLiveData<Boolean>()
    val shimmer: LiveData<Boolean> get() = _shimmer

    private val _username = MutableLiveData<Event<String>>()
    val username: LiveData<Event<String>> get() = _username

    private var _call: Call<SearchResponse>? = null

    init {
        onRefresh()
    }

    override fun onCleared() {
        super.onCleared()
        _call?.cancel()
    }

    fun onRefresh() {
        findUsers(randomChar(), _refresh)
    }

    fun onQuerySubmit(query: String?) {
        findUsers(query, _loading)
    }

    fun onItemClick(username: String) {
        _username.value = Event(username)
    }

    @VisibleForTesting
    fun findUsers(
        username: String?,
        loadingType: MutableLiveData<Boolean>
    ) {
        service.findUsers(username).fetch(
            pre = {
                _error.value = false
                _users.value = emptyList()
                _shimmer.value = true
                loadingType.value = true
            },
            post = {
                _shimmer.value = false
                loadingType.value = false
            },
            onError = { e ->
                Log.e(TAG, "${e.message}")
                _error.value = true
            },
            onSuccess = { response ->
                if (response.isSuccessful && response.code() == 200) {
                    val results = response.body()?.users ?: emptyList()

                    _error.value = results.isEmpty()
                    _users.value = results.map(UserResponse::toDomain)
                }
            },
        ).apply { _call = this }
    }

    @VisibleForTesting
    fun randomChar(): String {
        return ('a'..'z').random().toString()
    }

}