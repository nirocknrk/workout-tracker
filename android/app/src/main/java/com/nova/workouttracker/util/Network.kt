package com.nova.workouttracker.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** True when the device currently has an internet-capable network. */
fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
