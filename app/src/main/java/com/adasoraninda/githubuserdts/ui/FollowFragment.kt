package com.adasoraninda.githubuserdts.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.githubuserdts.common.FollowType
import com.adasoraninda.githubuserdts.common.ListUserAdapter
import com.adasoraninda.githubuserdts.databinding.FragmentFollowBinding
import com.adasoraninda.githubuserdts.navigation.ScreenNavigator
import com.adasoraninda.githubuserdts.network.ApiConfig
import com.adasoraninda.githubuserdts.utils.obtainViewModelWithFactory
import com.adasoraninda.githubuserdts.viewmodel.FollowViewModel

private const val TAG = "FollowFragment"

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding

    private val listUserAdapter by lazy { ListUserAdapter() }

    private lateinit var viewModel: FollowViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return FollowViewModel(ApiConfig.service) as T
            }
        }

        viewModel = obtainViewModelWithFactory(FollowViewModel::class.java, factory)

        val type = arguments?.get(ARGS_TYPE) as? FollowType
        val username = arguments?.getString(ARGS_USERNAME)

        type?.let { viewModel.getUserFollow(it, username) }

        initListUsers()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) {
            Log.d(TAG, "$it")
            listUserAdapter.users = it
        }

        viewModel.username.observe(viewLifecycleOwner) { event ->
            val result = event.getContent()
            Log.d(TAG, "username: $result")
            result?.let { navigateToDetailUser(it) }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            binding?.textError?.isVisible = it
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding?.progressBar?.isVisible = it
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
            context = requireContext(),
            destination = DetailUserActivity::class.java,
            bundle = bundle,
            flag = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        )
    }

    companion object {
        private const val ARGS_TYPE = "ARGS_TYPE"
        private const val ARGS_USERNAME = "ARGS_USERNAME"

        fun getInstances(username: String?, type: FollowType): FollowFragment {
            return FollowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGS_USERNAME, username)
                    putSerializable(ARGS_TYPE, type)
                }
            }
        }
    }

}