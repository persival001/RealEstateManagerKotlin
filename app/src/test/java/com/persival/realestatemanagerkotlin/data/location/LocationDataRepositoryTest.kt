package com.persival.realestatemanagerkotlin.data.location

import android.location.Location
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationDataRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val fusedLocationProviderClient: FusedLocationProviderClient = mockk()
    private val locationCallbackCaptor = slot<LocationCallback>()

    private lateinit var locationDataRepository: LocationDataRepository
    private lateinit var taskVoid: Task<Void>

    @Before
    fun setUp() {
        locationDataRepository = LocationDataRepository(fusedLocationProviderClient)
        taskVoid = mockk()

        coEvery {
            fusedLocationProviderClient.requestLocationUpdates(
                any(),
                any(),
                capture(locationCallbackCaptor)
            )
        } returns taskVoid

        every { fusedLocationProviderClient.removeLocationUpdates(any<LocationCallback>()) } returns taskVoid

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `getLocationFlow emits LocationEntity on location update`() = testCoroutineRule.runTest {
        // Arrange
        val expectedLatitude = 10.0
        val expectedLongitude = 20.0
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns expectedLatitude
        every { mockLocation.longitude } returns expectedLongitude
        val locationResult = LocationResult.create(listOf(mockLocation))

        // Act
        val job = launch {
            locationDataRepository.getLocationFlow().collect { locationEntity ->
                // Assert
                assertEquals(expectedLatitude, locationEntity.latitude, 0.001)
                assertEquals(expectedLongitude, locationEntity.longitude, 0.001)
            }
        }

        delay(100)
        locationCallbackCaptor.captured.onLocationResult(locationResult)

        // Clean up
        job.cancel()
    }

    @Test
    fun `getLocationFlow emits nothing when location update fails`() = testCoroutineRule.runTest {
        // Arrange
        val exception = Exception("Location update failed")

        // Mock the task to be unsuccessful
        val taskVoid: Task<Void> = mockk()
        every { taskVoid.isComplete } returns true
        every { taskVoid.isSuccessful } returns false
        every { taskVoid.exception } returns exception

        coEvery {
            fusedLocationProviderClient.requestLocationUpdates(
                any(),
                capture(locationCallbackCaptor),
                any()
            )
        } returns taskVoid

        // Act & Assert
        val job = launch {
            locationDataRepository.getLocationFlow().collect { locationEntity ->
                fail("Should not emit any location updates")
            }
        }

        delay(100) // Give some time for the location update request to be processed

        // Clean up
        job.cancel()
    }


}
