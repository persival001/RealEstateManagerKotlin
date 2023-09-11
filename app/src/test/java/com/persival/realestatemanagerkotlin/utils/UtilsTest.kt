package com.persival.realestatemanagerkotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNetworkInfo
import java.util.Date

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setup() {
    }

    @Test
    fun testConvertDollarToEuro() {
        val dollars = 100
        val result = Utils.convertDollarToEuro(dollars)
        assertEquals(81, result)
    }

    @Test
    fun testConvertEuroToDollar() {
        val euros = 100
        val result = Utils.convertEuroToDollar(euros)
        assertEquals(123, result)
    }

    @Test
    fun testGetTodayDate() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDate(date)
        assertEquals("2020/01/01", formattedDate)
    }

    @Test
    fun testGetTodayDateInFrench() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDateInFrench(date)
        assertEquals("01/01/2020", formattedDate)
    }

    @Test
    fun testConvertDollarToEuroFailure() {
        val dollars = 100
        val result = Utils.convertDollarToEuro(dollars)
        assertNotEquals(82, result)
    }

    @Test
    fun testConvertEuroToDollarFailure() {
        val euros = 100
        val result = Utils.convertEuroToDollar(euros)
        assertNotEquals(124, result)
    }

    @Test
    fun testGetTodayDateFailure() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDate(date)
        assertNotEquals("2021/01/01", formattedDate)
    }

    @Test
    fun testGetTodayDateInFrenchFailure() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDateInFrench(date)
        assertNotEquals("02/01/2020", formattedDate)
    }


    // Test if wifi is enabled
    @Test
    fun testIsInternetAvailable() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val shadowWifiManager = shadowOf(wifiManager)

        // Simulate Wi-Fi enable
        shadowWifiManager.setConnectionInfo(wifiManager.connectionInfo)

        val result = Utils.isInternetAvailable(context)
        assertEquals(true, result)
    }

    // Test if wifi is disabled
    @Test
    fun testIsInternetAvailableWhenWifiIsOff() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val shadowWifiManager = shadowOf(wifiManager)

        // Simulate deactivated wifi
        shadowWifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED)

        val result = Utils.isInternetAvailable(context)
        assertEquals(false, result)
    }

    @Test
    fun testIsConnexionAvailable() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val shadowConnectivityManager = shadowOf(connectivityManager)

        // Simulate connexion internet
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.CONNECTED,
            ConnectivityManager.TYPE_WIFI,
            0,
            true,
            true
        )
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val result = Utils.isConnexionAvailable(context)
        assertEquals(true, result)
    }

    @Test
    fun testIsConnexionAvailableWhenNoActiveConnection() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val shadowConnectivityManager = shadowOf(connectivityManager)

        // Simulate inactivate connexion
        val networkInfo = ShadowNetworkInfo.newInstance(
            NetworkInfo.DetailedState.DISCONNECTED,
            -1, // -1 is no connexion
            0,
            false,
            false
        )
        shadowConnectivityManager.setActiveNetworkInfo(networkInfo)

        val result = Utils.isConnexionAvailable(context)
        assertEquals(false, result)
    }


}
