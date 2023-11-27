package com.persival.realestatemanagerkotlin.utils

import android.content.Context
import android.net.wifi.WifiManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowWifiManager
import java.util.Date

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var wifiManager: WifiManager
    private lateinit var shadowWifiManager: ShadowWifiManager

    @Before
    fun setup() {
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        shadowWifiManager = shadowOf(wifiManager)
    }

    @Test
    fun testConvertDollarToEuro() {
        assertEquals(81, Utils.convertDollarToEuro(100))
    }

    @Test
    fun testConvertEuroToDollar() {
        assertEquals(123, Utils.convertEuroToDollar(100))
    }

    @Test
    fun testGetTodayDate() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDate(date)
        assertEquals("2020-01-01", formattedDate)
    }


    @Test
    fun testGetTodayDateInFrench() {
        val date = Date(120, 0, 1)
        val formattedDate = Utils.getTodayDateInFrench(date)
        assertEquals("01-01-2020", formattedDate)
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
        // Simulate Wi-Fi enable
        shadowWifiManager.setConnectionInfo(wifiManager.connectionInfo)
        assertEquals(true, Utils.isInternetAvailable(context))
    }

    // Test if wifi is disabled
    @Test
    fun testIsInternetAvailableWhenWifiIsOff() {
        // Simulate deactivated wifi
        shadowWifiManager.setWifiState(WifiManager.WIFI_STATE_DISABLED)
        assertEquals(false, Utils.isInternetAvailable(context))
    }

}
