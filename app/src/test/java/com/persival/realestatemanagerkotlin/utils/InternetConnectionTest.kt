package com.persival.realestatemanagerkotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("DEPRECATION")
class InternetConnectionTest {

    private val mockContext = mockk<Context>()
    private val mockConnectivityManager = mockk<ConnectivityManager>()
    private val mockNetworkCapabilities = mockk<NetworkCapabilities>()
    private val mockNetworkInfo = mockk<NetworkInfo>()

    @Before
    fun setUp() {
        // Common mock initialization for all tests
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetwork } returns mockk()
        every { mockConnectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockConnectivityManager.activeNetworkInfo } returns mockNetworkInfo
    }

    // Helper method to setup conditions for each test
    private fun configureMocks(
        transportType: Int,
        isConnected: Boolean
    ) {
        every { mockNetworkCapabilities.hasTransport(transportType) } returns true
        every { mockNetworkInfo.isConnected } returns isConnected
    }

    @Test
    fun `isConnectionAvailable returns true when connectivity is available`() {
        // Configure mocks for a successful connection
        configureMocks(NetworkCapabilities.TRANSPORT_WIFI, true)
        Assert.assertTrue(Utils.isConnexionAvailable(mockContext))

        configureMocks(NetworkCapabilities.TRANSPORT_CELLULAR, true)
        Assert.assertTrue(Utils.isConnexionAvailable(mockContext))

        configureMocks(NetworkCapabilities.TRANSPORT_ETHERNET, true)
        Assert.assertTrue(Utils.isConnexionAvailable(mockContext))
    }

    @Test
    fun `isConnectionAvailable returns false when connectivity is not available`() {
        // Configure mocks for no connection
        configureMocks(NetworkCapabilities.TRANSPORT_WIFI, false)
        Assert.assertFalse(Utils.isConnexionAvailable(mockContext))

        configureMocks(NetworkCapabilities.TRANSPORT_CELLULAR, false)
        Assert.assertFalse(Utils.isConnexionAvailable(mockContext))

        configureMocks(NetworkCapabilities.TRANSPORT_ETHERNET, false)
        Assert.assertFalse(Utils.isConnexionAvailable(mockContext))
    }
}
