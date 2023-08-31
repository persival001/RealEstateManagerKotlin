package com.persival.realestatemanagerkotlin

import android.content.Context
import android.net.Uri
import com.persival.realestatemanagerkotlin.data.local_database.content_provider.ContentProvider
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ContentProviderTest {

    private lateinit var contentProvider: ContentProvider

    private val mockPropertyDao = mockk<PropertyDao>()
    private val mockPhotoDao = mockk<PhotoDao>()
    private val mockPointOfInterestDao = mockk<PointOfInterestDao>()

    @Before
    fun setUp() {
        contentProvider = ContentProvider()

        contentProvider.propertyDao = mockPropertyDao
        contentProvider.photoDao = mockPhotoDao
        contentProvider.pointOfInterestDao = mockPointOfInterestDao

        val mockContext = mockk<Context> {
            every { applicationContext } returns mockk()
            every { contentResolver } returns mockk(relaxed = true)
        }
        contentProvider.attachInfo(mockContext, null)
    }

    @Test
    fun testQueryForAllProperties() {
        every { mockPropertyDao.getAllPropertiesAsCursor() } returns mockk(relaxed = true)

        val result = contentProvider.query(
            Uri.parse(
                "content://${ContentProvider.AUTHORITY}/properties"
            ),
            null, null, null, null
        )

        assertNotNull(result)
        verify { mockPropertyDao.getAllPropertiesAsCursor() }
    }

}

