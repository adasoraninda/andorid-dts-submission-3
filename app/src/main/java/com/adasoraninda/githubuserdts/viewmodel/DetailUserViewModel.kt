package com.adasoraninda.githubuserdts.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adasoraninda.githubuserdts.R
import com.adasoraninda.githubuserdts.data.domain.User
import com.adasoraninda.githubuserdts.data.domain.toDomain
import com.adasoraninda.githubuserdts.data.entity.toEntity
import com.adasoraninda.githubuserdts.data.response.UserResponse
import com.adasoraninda.githubuserdts.local.UserDao
import com.adasoraninda.githubuserdts.network.GitHubUserService
import com.adasoraninda.githubuserdts.utils.Event
import com.adasoraninda.githubuserdts.utils.fetch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call

private const val TAG = "DetailUserViewModel"

class DetailUserViewModel(
    private val userDao: UserDao,
    private val service: GitHubUserService
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var _call: Call<UserResponse>? = null

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _share = MutableLiveData<Event<User>>()
    val share: LiveData<Event<User>> get() = _share

    private val _back = MutableLiveData<Event<Boolean>>()
    val back: LiveData<Event<Boolean>> get() = _back

    private val _daoMessageResult = MutableLiveData<Event<Int>>()
    val daoMessageResult: LiveData<Event<Int>> get() = _daoMessageResult

    private val _isFavorite = MutableLiveData<Boolean?>(null)
    val isFavorite: LiveData<Boolean?> get() = _isFavorite

    private val _shimmer = MutableLiveData<Boolean>()
    val shimmer: LiveData<Boolean> get() = _shimmer

    override fun onCleared() {
        super.onCleared()
        _call = null
        coroutineScope.cancel()
    }

    fun shareUser() {
        _user.value?.let {
            _share.value = Event(it)
        }
    }

    fun onBackClicked() {
        _back.value = Event(true)
    }

    fun getDetailUser(username: String?) {
        Log.d(TAG, "$username")
        service.getDetailUser(username).fetch(
            pre = {
                _error.value = false
                _shimmer.value = true
                _loading.value = true
            },
            post = {
                _shimmer.value = false
                _loading.value = false
            },
            onError = { t ->
                Log.e(TAG, "${t.message}")
                _error.value = true
            },
            onSuccess = { response ->
                if (response.isSuccessful && response.code() == 200) {
                    val results = response.body()

                    _error.value = results == null
                    _user.value = results?.toDomain()
                    checkDataExists()
                } else {
                    _error.value = true
                }
            }
        ).apply { _call = this }
    }

    fun onFavoriteClicked() {
        val value = _isFavorite.value ?: false
        _isFavorite.value = null

        _isFavorite.value = if (value) {
            deleteUser()
            false
        } else {
            saveUser()
            true
        }
    }

    private fun saveUser() {
        coroutineScope.launch {
            checkUser(
                onInValid = {
                    _daoMessageResult.value =
                        Event(R.string.error_dao_save)
                },
                onValid = {
                    val userEntity = it.toEntity()
                    val result = userDao.saveData(userEntity)
                    if (result > 0) {
                        _daoMessageResult.value =
                            Event(R.string.success_dao_save)
                    } else {
                        _daoMessageResult.value =
                            Event(R.string.error_dao_save)
                    }
                }
            )
        }
    }

    private fun deleteUser() {
        coroutineScope.launch {
            checkUser(
                onInValid = {
                    _daoMessageResult.value =
                        Event(R.string.error_dao_delete)
                },
                onValid = {
                    val userEntity = it.toEntity()
                    val result = userDao.deleteData(userEntity)
                    if (result > 0) {
                        _daoMessageResult.value =
                            Event(R.string.success_dao_delete)
                    } else {
                        _daoMessageResult.value =
                            Event(R.string.error_dao_delete)
                    }
                }
            )
        }
    }

    private fun checkDataExists() {
        coroutineScope.launch {
            checkUser(
                onInValid = {
                    _isFavorite.value = false
                },
                onValid = {
                    val userId = it.id
                    _isFavorite.value = userDao.isDataExists(userId)
                }
            )
        }
    }

    private suspend fun checkUser(onValid: suspend (user: User) -> Unit, onInValid: () -> Unit) {
        val curUser = _user.value
        if (curUser != null) {
            onValid(curUser)
        } else {
            onInValid()
        }
    }

}