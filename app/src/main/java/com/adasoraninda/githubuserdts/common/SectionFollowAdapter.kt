package com.adasoraninda.githubuserdts.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.adasoraninda.githubuserdts.ui.FollowFragment

class SectionFollowAdapter(
    private val username: String?,
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return FollowType.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return FollowType.values().map {
            FollowFragment.getInstances(username, it)
        }[position]
    }
}