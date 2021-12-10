package com.adasoraninda.githubuserdts.utils

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.adasoraninda.githubuserdts.local.AppStorageManager.Companion.DATA_STORE_NAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Context.showToastMessage(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

val Context.datastore by preferencesDataStore(DATA_STORE_NAME)

fun <T : ViewModel> ViewModelStoreOwner.obtainViewModel(
    jClass: Class<T>
): T {
    return ViewModelProvider(this)[jClass]
}

fun <T : ViewModel> ViewModelStoreOwner.obtainViewModelWithFactory(
    jClass: Class<T>,
    factory: ViewModelProvider.Factory
): T {
    return ViewModelProvider(this, factory)[jClass]
}

fun <T> Call<T>?.fetch(
    pre: (() -> Unit)? = null,
    onSuccess: ((response: Response<T>) -> Unit)? = null,
    onError: ((t: Throwable) -> Unit)? = null,
    post: (() -> Unit)? = null,
): Call<T>? {
    pre?.invoke()

    this?.enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            onSuccess?.invoke(response)
            post?.invoke()
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onError?.invoke(t)
            post?.invoke()
        }
    })

    return this
}