package com.proximity.labs

import com.proximity.labs.network.SocketUpdate
import com.proximity.labs.network.WebServicesProvider
import kotlinx.coroutines.channels.Channel

class MainRepository(private val webServicesProvider: WebServicesProvider) {
    fun startSocket(): Channel<SocketUpdate> =
        webServicesProvider.startSocket()

    fun closeSocket() {
        webServicesProvider.stopSocket()
    }
}