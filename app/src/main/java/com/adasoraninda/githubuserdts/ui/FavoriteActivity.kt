package com.adasoraninda.githubuserdts.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.githubuserdts.R
import com.adasoraninda.githubuserdts.common.ListUserAdapter
import com.adasoraninda.githubuserdts.databinding.ActivityFavoriteBinding
import com.adasoraninda.githubuserdts.local.UserDatabase
import com.adasoraninda.githubuserdts.navigation.ScreenNavigator
import com.adasoraninda.githubuserdts.utils.obtainViewModelWithFactory
import com.adasoraninda.githubuserdts.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding

    private val listUserAdapter by lazy { ListUserAdapter() }

    private lateinit var viewModel: FavoriteViewModel

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val userDao = UserDatabase.getInstance(this).dao

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FavoriteViewModel(userDao) as T
            }
        }

        viewModel = obtainViewModelWithFactory(FavoriteViewModel::class.java, factory)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.favorite)

        initListUsers()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val deleteMenuItem = menu?.findItem(R.id.action_delete)
        deleteMenuItem?.isVisible = viewModel.users.value?.isNullOrEmpty()?.not() ?: false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                viewModel.onBackClicked()
                true
            }
            R.id.action_delete -> {
                viewModel.deleteAllUsers()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.users.observe(this) {
            listUserAdapter.users =
                if (it.isNullOrEmpty()) emptyList()
                else it

            binding?.textError?.isVisible = it.isNullOrEmpty()
            invalidateOptionsMenu()
        }

        viewModel.username.observe(this) { event ->
            val result = event.getContent()
            result?.let { navigateToDetailUser(it) }
        }

        viewModel.back.observe(this) { event ->
            val result = event.getContent() ?: false
            if (result) onBackPressed()
        }

        viewModel.loading.observe(this) { loading ->
            binding?.progressBar?.isVisible = loading
        }
    }

    private fun initListUsers() {
        binding?.layoutList?.listUsers?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listUserAdapter.apply {
                setItemOnClickListener { username ->
                    viewModel.onItemClick(username)
                }
            }
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    RecyclerView.VERTICAL
                )
            )
        }
    }

    private fun navigateToDetailUser(username: String?) {
        val bundle = Bundle().apply { putString(DetailUserActivity.EXTRA_USERNAME, username) }
        ScreenNavigator.navigate(
            context = this,
            destination = DetailUserActivity::class.java,
            bundle = bundle
        )
    }

}