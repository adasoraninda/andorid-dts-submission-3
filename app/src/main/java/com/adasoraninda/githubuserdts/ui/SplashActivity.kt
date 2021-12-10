package com.adasoraninda.githubuserdts.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adasoraninda.githubuserdts.R
import com.adasoraninda.githubuserdts.local.AppStorageManager
import com.adasoraninda.githubuserdts.navigation.ScreenNavigator
import com.adasoraninda.githubuserdts.utils.datastore
import com.adasoraninda.githubuserdts.utils.obtainViewModelWithFactory
import com.adasoraninda.githubuserdts.viewmodel.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val appStorageManager = AppStorageManager.getInstance(datastore)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SplashViewModel(appStorageManager) as T
            }
        }

        viewModel = obtainViewModelWithFactory(SplashViewModel::class.java, factory)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.themes.observe(this) {
            AppCompatDelegate.setDefaultNightMode(it ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        viewModel.navigation.observe(this) { event ->
            val result = event.getContent()
            result?.let { if (it) navigateToList() }
        }
    }

    private fun navigateToList() {
        ScreenNavigator.navigate(
            context = this,
            destination = ListUserActivity::class.java,
        ).also { finishAffinity() }
    }

}