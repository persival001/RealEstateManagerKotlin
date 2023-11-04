package com.persival.realestatemanagerkotlin.data.content_provider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PropertyProviderTest {

    private lateinit var propertyProvider: PropertyProvider
    private lateinit var mockPropertyDao: PropertyDao
    private lateinit var mockPhotoDao: PhotoDao
    private lateinit var mockPointOfInterestDao: PointOfInterestDao
    private lateinit var contentResolver: ContentResolver
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        contentResolver = mockk(relaxed = true)

        mockPropertyDao = mockk()
        mockPhotoDao = mockk()
        mockPointOfInterestDao = mockk()

        propertyProvider = PropertyProvider().apply {
            propertyDao = mockPropertyDao
            photoDao = mockPhotoDao
            pointOfInterestDao = mockPointOfInterestDao
        }

        val fakeCursor: Cursor = mockk(relaxed = true)
        every { mockPropertyDao.getAllPropertiesAsCursor() } returns fakeCursor
        every { fakeCursor.setNotificationUri(any(), any()) } just Runs
    }

    // This unit test ensures that the query method of the PropertyProvider class behaves as expected when given a certain URI.
    // It also verifies that the correct methods are called on the mocked objects.
    @Test
    fun `query should return correct Cursor based on URI`() {
        val fakeCursor: Cursor = mockk(relaxed = true)
        every {
            fakeCursor.setNotificationUri(
                any(),
                any()
            )
        } just Runs

        every { mockPropertyDao.getAllPropertiesAsCursor() } returns fakeCursor

        val uri = Uri.parse("content://${PropertyProvider.AUTHORITY}/properties")
        val resultCursor = propertyProvider.query(uri, null, null, null, null)

        verify { mockPropertyDao.getAllPropertiesAsCursor() }
        verify { fakeCursor.setNotificationUri(any(), any()) }

        Assert.assertEquals(fakeCursor, resultCursor)
    }

}