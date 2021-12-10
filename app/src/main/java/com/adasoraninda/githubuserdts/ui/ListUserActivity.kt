package com.adasoraninda.githubuserdts.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.githubuserdts.R
import com.adasoraninda.githubuserdts.common.ListUserAdapter
import com.adasoraninda.githubuserdts.databinding.ActivityListUserBinding
import com.adasoraninda.githubuserdts.navigation.ScreenNavigator
import com.adasoraninda.githubuserdts.network.ApiConfig
import com.adasoraninda.githubuserdts.utils.obtainViewModelWithFactory
import com.adasoraninda.githubuserdts.viewmodel.ListUserViewModel

private const val TAG = "ListUserActivity"

class ListUserActivity : AppCompatActivity() {

    private var _binding: ActivityListUserBinding? = null
    private val binding get() = _binding

    private val listUserAdapter by lazy { ListUserAdapter() }

    private lateinit var viewModel: ListUserViewModel

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ListUserViewModel(ApiConfig.service) as T
            }
        }

        viewModel = obtainViewModelWithFactory(ListUserViewModel::class.java, factory)

        initListUsers()
        initSearch()
        initSwipeRefresh()

        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                ScreenNavigator.navigate(this, FavoriteActivity::class.java)
                true
            }
            R.id.action_settings -> {
                ScreenNavigator.navigate(this, SettingsActivity::class.java)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.users.observe(this) {
            Log.d(TAG, "$it")
            listUserAdapter.users = it
        }

        viewModel.username.observe(this) { event ->
            val result = event.getContent()
            result?.let { navigateToDetailUser(it) }
        }

        viewModel.error.observe(this) {
            binding?.textError?.isVisible = it
        }

        viewModel.loading.observe(this) {
            binding?.progressBar?.isVisible = it
        }

        viewModel.refresh.observe(this) {
            binding?.swipeRefresh?.isRefreshing = it
        }

        viewModel.shimmer.observe(this) {
            if (it) {
                binding?.layoutShimmer?.root?.isVisible = true
                binding?.layoutShimmer?.shimmerFrame?.startShimmer()
            } else {
                binding?.layoutShimmer?.shimmerFrame?.stopShimmer()
                binding?.layoutShimmer?.root?.isVisible = false
            }
        }
    }

    private fun initSwipeRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener {
            viewModel.onRefresh()
        }
    }

    private fun initSearch() {
        binding?.searchUsers?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.onQuerySubmit(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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