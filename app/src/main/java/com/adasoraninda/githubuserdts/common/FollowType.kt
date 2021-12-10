package com.adasoraninda.githubuserdts.common

import androidx.annotation.StringRes
import com.adasoraninda.githubuserdts.R

enum class FollowType(@StringRes val title: Int) {
    FOLLOWING(R.string.following),
    FOLLOWERS(R.string.followers)
}