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

class InternetConnexionTest {
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities

    @Before
    fun setUp() {
        // Initialisation of mocks
        context = mockk()
        connectivityManager = mockk()
        networkCapabilities = mockk()

        // Simulate getSystemService
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }

    @Suppress("DEPRECATION")
    @Test
    fun `isConnexionAvailable returns true when connectivity is available`() {
        // Initialize mocks
        val mockContext = mockk<Context>()
        val mockConnectivityManager = mockk<ConnectivityManager>()
        val mockNetworkCapabilities = mockk<NetworkCapabilities>()
        val mockNetworkInfo = mockk<NetworkInfo>()

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetwork } returns mockk()
        every { mockConnectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockConnectivityManager.activeNetworkInfo } returns mockNetworkInfo  // Mocking getActiveNetworkInfo()

        // Test WiFi
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true
        every { mockNetworkInfo.isConnected } returns true  // Mocking isConnected method in NetworkInfo

        var result = Utils.isConnexionAvailable(mockContext)

        // Assertion for WiFi
        Assert.assertTrue(result)

        // Test Cellular
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true

        result = Utils.isConnexionAvailable(mockContext)

        // Assertion for Cellular
        Assert.assertTrue(result)

        // Test Ethernet
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns true

        result = Utils.isConnexionAvailable(mockContext)

        // Assertion for Ethernet
        Assert.assertTrue(result)
    }

    @Suppress("DEPRECATION")  // To suppress deprecation warnings
    @Test
    fun `isConnexionAvailable returns false when connectivity is not available`() {
        // Initialize mocks
        val mockContext = mockk<Context>()
        val mockConnectivityManager = mockk<ConnectivityManager>()
        val mockNetworkCapabilities = mockk<NetworkCapabilities>()
        val mockNetworkInfo = mockk<NetworkInfo>()

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetwork } returns mockk(relaxed = true) // relaxed mock returns null for all non-mocked calls
        every { mockConnectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockConnectivityManager.activeNetworkInfo } returns mockNetworkInfo  // Mocking getActiveNetworkInfo()

        // Mocking to return false for all transport types
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { mockNetworkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

        // Mocking isConnected method in NetworkInfo to return false
        every { mockNetworkInfo.isConnected } returns false

        // Test
        val result = Utils.isConnexionAvailable(mockContext)

        // Assertion
        Assert.assertFalse(result)
    }

}