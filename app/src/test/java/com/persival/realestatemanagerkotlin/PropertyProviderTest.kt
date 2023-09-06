package com.persival.realestatemanagerkotlin

import android.content.ContentProvider
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.persival.realestatemanagerkotlin.data.local_database.content_provider.PropertyProvider
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
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PropertyProviderTest {

    private lateinit var shadowContentResolver: ShadowContentResolver
    private lateinit var provider: PropertyProvider

    @Before
    fun setUp() {
        val contentResolver = ApplicationProvider.getApplicationContext<Context>().contentResolver

        shadowContentResolver = shadowOf(contentResolver)

        provider = Robolectric.buildContentProvider(PropertyProvider::class.java).create(
            ProviderInfo().apply {
                grantUriPermissions = true
            }
        ).get()
    }

    @Test
    fun testQueryForAllProperties() {


        val result = shadowContentResolver.query(
            Uri.parse("content://${PropertyProvider.Companion.AUTHORITY}/properties"),
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
                    com.persival.realestatemanagerkotlin.data.local_database.content_provider.PropertyProvider.Companion.AUTHORITY
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
                    com.persival.realestatemanagerkotlin.data.local_database.content_provider.PropertyProvider.Companion.AUTHORITY
                }/points_of_interest"
            ),
            null, null, null, null
        )

        assertNotNull(result)
        verify { mockPointOfInterestDao.getAllPointsOfInterestAsCursor() }
    }

}

