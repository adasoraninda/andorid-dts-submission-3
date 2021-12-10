package com.adasoraninda.githubuserdts.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle

object ScreenNavigator {

    fun <T : Class<*>> navigate(
        context: Context,
        destination: T,
        bundle: Bundle = Bundle(),
        flag: Int = Intent.FLAG_ACTIVITY_NEW_TASK
    ) {
        val intent = Intent(context, destination)
        intent.putExtras(bundle)
        intent.addFlags(flag)
        context.startActivity(intent)
    }

}