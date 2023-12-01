package com.persival.realestatemanagerkotlin.data.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.ContextCompat
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PermissionDataRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var permissionDataRepository: PermissionDataRepository

    @Before
    fun setUp() {
        context = mockk()
        permissionDataRepository = PermissionDataRepository(context)
    }

    @Test
    fun `refreshLocationPermission updates locationPermissionFlow based on permission status`() {
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } returns PackageManager.PERMISSION_GRANTED

        permissionDataRepository.refreshLocationPermission()

        runBlocking {
            assertTrue(permissionDataRepository.isLocationPermission().first())
        }

        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
        } returns PackageManager.PERMISSION_DENIED

        permissionDataRepository.refreshLocationPermission()

        runBlocking {
            assertFalse(permissionDataRepository.isLocationPermission().first())
        }
    }

    @Test
    fun `refreshCameraPermission updates cameraPermissionFlow correctly`() {
        // Test pour la permission accordée
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            )
        } returns PackageManager.PERMISSION_GRANTED

        permissionDataRepository.refreshCameraPermission()

        runBlocking {
            assertTrue(permissionDataRepository.isCameraPermission().first())
        }

        // Test pour la permission refusée
        every {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            )
        } returns PackageManager.PERMISSION_DENIED

        permissionDataRepository.refreshCameraPermission()

        runBlocking {
            assertFalse(permissionDataRepository.isCameraPermission().first())
        }
    }

    @Test
    fun `refreshGpsActivation updates isGpsActivatedLiveData correctly`() {
        val locationManager = mockk<LocationManager>()
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager

        // GPS is activated
        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
        permissionDataRepository.refreshGpsActivation()

        runBlocking {
            assertTrue(permissionDataRepository.isGpsActivated().first())
        }

        // GPS is deactivated
        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
        permissionDataRepository.refreshGpsActivation()

        runBlocking {
            assertFalse(permissionDataRepository.isGpsActivated().first())
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

}
