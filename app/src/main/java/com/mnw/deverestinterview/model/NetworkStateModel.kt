package com.mnw.deverestinterview.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mnw.deverestinterview.net.NetworkUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkStateModel @Inject constructor(@ApplicationContext applicationContext: Context) {


    private val _networkState: MutableLiveData<NetworkState> = MutableLiveData(NetworkState.NO_ACTIVITY)
    val networkState: LiveData<NetworkState> = _networkState

    private val connectivityManager = applicationContext.getSystemService(ConnectivityManager::class.java)
    private var networkValidated = true

    private val networkChangeListener = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            networkValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            Log.i("ASD", "onCapabilitiesChanged validated: $networkValidated $networkCapabilities")
            if (networkValidated && _networkState.value == NetworkState.OFFLINE) {
                _networkState.postValue(NetworkState.NO_ACTIVITY)
            }

        }

        override fun onLost(network: Network) {
            networkValidated = false
            _networkState.postValue(NetworkState.OFFLINE)
        }

    }

    init {
        connectivityManager.registerDefaultNetworkCallback(networkChangeListener)
        networkValidated = NetworkUtil.isNetworkConnected(connectivityManager)
        _networkState.postValue(if (networkValidated) NetworkState.NO_ACTIVITY else NetworkState.OFFLINE)
    }

    fun requestState(requestedState: NetworkState, message: String? = null) {
        when(requestedState) {
            NetworkState.ERROR -> {
                if (!NetworkUtil.isNetworkConnected(connectivityManager)) {
                    _networkState.postValue(NetworkState.OFFLINE)
                } else {
                    _networkState.postValue(NetworkState.ERROR)
                }
            }
            else -> {
                _networkState.postValue(requestedState)
            }
        }


    }


}

enum class NetworkState {
    NO_ACTIVITY,
    REFRESHING,
    OFFLINE,
    ERROR,

}
