package com.mnw.deverestinterview.net

import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkUtil {

    fun isNetworkConnected(connectivityManager: ConnectivityManager): Boolean {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }
}
