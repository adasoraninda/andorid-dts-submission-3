package com.adasoraninda.githubuserdts.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adasoraninda.githubuserdts.R
import com.adasoraninda.githubuserdts.databinding.ActivitySettingsBinding
import com.adasoraninda.githubuserdts.local.AppStorageManager
import com.adasoraninda.githubuserdts.utils.datastore
import com.adasoraninda.githubuserdts.utils.obtainViewModelWithFactory
import com.adasoraninda.githubuserdts.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: SettingsViewModel

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val appStorageManager = AppStorageManager.getInstance(datastore)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SettingsViewModel(appStorageManager) as T
            }
        }

        viewModel = obtainViewModelWithFactory(SettingsViewModel::class.java, factory)

        binding?.switchMode?.setOnCheckedChangeListener { _, b ->
            val mode = if (b) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            viewModel.saveThemes(mode)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings)

        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.onBackClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.back.observe(this) { event ->
            val result = event.getContent() ?: false
            if (result) onBackPressed()
        }

        viewModel.themes.observe(this) {
            binding?.switchMode?.isChecked = it == AppCompatDelegate.MODE_NIGHT_YES
            AppCompatDelegate.setDefaultNightMode(it ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }

}