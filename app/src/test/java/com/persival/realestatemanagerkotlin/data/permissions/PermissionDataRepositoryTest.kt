package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
class PermissionDataRepositoryTest {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Replace with your TestCoroutineRule
    @get:Rule(order = 3)
    var testCoroutineRule = TestCoroutineRule()

    @Inject
    lateinit var permissionDataRepository: PermissionDataRepository

    private lateinit var context: Context
    private lateinit var locationManager: LocationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext()
        locationManager = mockk()

        hiltRule.inject()

        mockkStatic(ContextCompat::class)

        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager
    }

    @Test
    fun `isLocationPermission returns true when permission is granted`() = testCoroutineRule.runTest {
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } returns PackageManager.PERMISSION_GRANTED

        permissionDataRepository.refreshLocationPermission()

        assertThat(permissionDataRepository.isLocationPermission().first(), `is`(true))
    }

    @Test
    fun `isGpsActivated returns true when GPS is enabled`() = testCoroutineRule.runTest {
        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true

        permissionDataRepository.refreshGpsActivation()

        assertThat(permissionDataRepository.isGpsActivated().first(), `is`(true))
    }

    @Test
    fun `isCameraPermission returns false when permission is denied`() = testCoroutineRule.runTest {
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            )
        } returns PackageManager.PERMISSION_DENIED

        permissionDataRepository.refreshCameraPermission()

        assertThat(permissionDataRepository.isCameraPermission().first(), `is`(false))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

}
