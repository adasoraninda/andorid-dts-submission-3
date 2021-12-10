package com.adasoraninda.githubuserdts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adasoraninda.githubuserdts.data.domain.User
import com.adasoraninda.githubuserdts.data.domain.toDomain
import com.adasoraninda.githubuserdts.data.entity.UserEntity
import com.adasoraninda.githubuserdts.local.UserDao
import com.adasoraninda.githubuserdts.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private val _back = MutableLiveData<Event<Boolean>>()
    val back: LiveData<Event<Boolean>> get() = _back

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _username = MutableLiveData<Event<String>>()
    val username: LiveData<Event<String>> get() = _username

    init {
        getAllUsers()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    fun deleteAllUsers() {
        coroutineScope.launch {
            _loading.value = true
            userDao.deleteAllData()
            _users.value = emptyList()
            _loading.value = false
        }
    }

    fun getAllUsers() {
        coroutineScope.launch {
            _loading.value = true
            _users.value = userDao.getAllData().map(UserEntity::toDomain)
            _loading.value = false
        }
    }

    fun onBackClicked() {
        _back.value = Event(true)
    }

    fun onItemClick(username: String) {
        _username.value = Event(username)
    }

}