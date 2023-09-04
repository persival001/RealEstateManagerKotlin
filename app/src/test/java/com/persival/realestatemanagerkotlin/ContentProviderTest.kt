package com.persival.realestatemanagerkotlin

import android.content.ContentProvider
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContentProviderTest {

    private lateinit var contentProvider: ContentProvider

    private val mockPropertyDao = mockk<PropertyDao>()
    private val mockPhotoDao = mockk<PhotoDao>()
    private val mockPointOfInterestDao = mockk<PointOfInterestDao>()

    @Before
    fun setUp() {
        contentProvider = com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider()

        (contentProvider as com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider).propertyDao =
            mockPropertyDao
        (contentProvider as com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider).photoDao =
            mockPhotoDao
        (contentProvider as com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider).pointOfInterestDao =
            mockPointOfInterestDao

        val mockContext = mockk<Context>(relaxed = true) {
            every { applicationContext } returns this
        }
        contentProvider.attachInfo(mockContext, null)
    }

    @Test
    fun testQueryForAllProperties() {
        val mockCursor = mockk<Cursor>(relaxed = true)
        every { mockPropertyDao.getAllPropertiesAsCursor() } returns mockCursor

        val result = contentProvider.query(
            Uri.parse(
                "content://${
                    com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider.Companion.AUTHORITY
                }/properties"
            ),
            null, null, null, null
        )

        assertNotNull(result)
        verify { mockPropertyDao.getAllPropertiesAsCursor() }
    }

    @Test
    fun testQueryForAllPhotos() {
        val mockCursor = mockk<Cursor>(relaxed = true)
        every { mockPhotoDao.getAllPhotosAsCursor() } returns mockCursor

        val result = contentProvider.query(
            Uri.parse(
                "content://${
                    com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider.Companion.AUTHORITY
                }/photos"
            ),
            null, null, null, null
        )

        assertNotNull(result)
        verify { mockPhotoDao.getAllPhotosAsCursor() }
    }

    @Test
    fun testQueryForAllPOIs() {
        val mockCursor = mockk<Cursor>(relaxed = true)
        every { mockPointOfInterestDao.getAllPointsOfInterestAsCursor() } returns mockCursor

        val result = contentProvider.query(
            Uri.parse(
                "content://${
                    com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider.Companion.AUTHORITY
                }/points_of_interest"
            ),
            null, null, null, null
        )

        assertNotNull(result)
        verify { mockPointOfInterestDao.getAllPointsOfInterestAsCursor() }
    }

}


