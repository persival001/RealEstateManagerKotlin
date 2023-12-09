package com.persival.realestatemanagerkotlin.data.local_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntity
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisEntity
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisEntityMapper
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import com.persival.realestatemanagerkotlin.utils_for_tests.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class LocalDatabaseRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocks
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider = mockk()
    private val propertyDao: PropertyDao = mockk()
    private val photoDao: PhotoDao = mockk()
    private val pointOfInterestDao: PointOfInterestDao = mockk()
    private val propertyEntityMapper: PropertyEntityMapper = mockk()
    private val photoEntityMapper: PhotoEntityMapper = mockk()
    private val pointOfInterestEntityMapper: PointOfInterestEntityMapper = mockk()
    private val propertyWithPhotosAndPoisEntityMapper: PropertyWithPhotosAndPoisEntityMapper = mockk()

    private lateinit var repository: LocalDatabaseRepository
    private lateinit var property: Property
    private lateinit var photo: Photo
    private lateinit var pointOfInterest: PointOfInterest

    @Before
    fun setUp() {
        every { coroutineDispatcherProvider.io } returns Dispatchers.IO
        // Set up the mocks with default responses
        coJustRun { propertyDao.insert(any()) }
        coJustRun { photoDao.insert(any()) }
        coJustRun { pointOfInterestDao.insert(any()) }
        coEvery { propertyEntityMapper.mapPropertyEntityToPropertyDto(any()) } returns mockk()
        coEvery { photoEntityMapper.mapPhotoEntityToPhotoDto(any()) } returns mockk()
        coEvery { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(any()) } returns mockk()
        coEvery { propertyWithPhotosAndPoisEntityMapper.mapToEntity(any()) } returns mockk()

        repository = LocalDatabaseRepository(
            coroutineDispatcherProvider = coroutineDispatcherProvider,
            propertyDao = propertyDao,
            photoDao = photoDao,
            pointOfInterestDao = pointOfInterestDao,
            propertyEntityMapper = propertyEntityMapper,
            photoEntityMapper = photoEntityMapper,
            pointOfInterestEntityMapper = pointOfInterestEntityMapper,
            propertyWithPhotosAndPoisEntityMapper = propertyWithPhotosAndPoisEntityMapper
        )

        // Initialize the Entity instances
        property = Property(
            id = 0L,
            type = "Maison",
            address = "123 rue de Exemple",
            latLng = "45.5017,-73.5673",
            area = 120,
            rooms = 5,
            bathrooms = 2,
            bedrooms = 3,
            description = "Belle maison en centre-ville",
            price = 250000,
            isSold = false,
            entryDate = "-1 month",
            saleDate = null,
            agentName = "John Doe"
        )

        photo = Photo(
            id = 0L,
            propertyId = 0L,
            description = "Façade de la maison",
            photoUrl = "http://exemple.com/photo.jpg"
        )

        pointOfInterest = PointOfInterest(
            id = 0L,
            propertyId = 0L,
            poi = "Parc à proximité"
        )

        every { photoEntityMapper.mapPhotoDtoToPhotoEntity(any()) } answers {
            Photo(
                id = firstArg<PhotoEntity>().id,
                propertyId = firstArg<PhotoEntity>().propertyId,
                description = firstArg<PhotoEntity>().description,
                photoUrl = firstArg<PhotoEntity>().photoUrl
            )
        }
    }

    @Test
    fun `insertProperty calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val propertyDto = propertyEntityMapper.mapPropertyEntityToPropertyDto(property)
        coEvery { propertyDao.insert(any()) } returns 1L

        // Act
        val resultId = repository.insertProperty(property)
        runCurrent()

        // Assert
        assertEquals(1L, resultId)
        coVerify(exactly = 1) { propertyDao.insert(propertyDto) }
    }

    @Test
    fun `insertProperty calls dao and returns no id on failure`() = testCoroutineRule.runTest {
        // Arrange
        val expectedFailureId = -1L
        coEvery { propertyDao.insert(any()) } returns expectedFailureId

        // Act
        val resultId = repository.insertProperty(property)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { propertyDao.insert(any()) }
    }

    @Test
    fun `insertPhoto calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val photoDto = photoEntityMapper.mapPhotoEntityToPhotoDto(photo)
        coEvery { photoDao.insert(any()) } returns 1L  // Assume success is represented by returning ID 1

        // Act
        val resultId = repository.insertPhoto(photo)
        runCurrent()

        // Assert
        assertEquals(1L, resultId)
        coVerify(exactly = 1) { photoDao.insert(photoDto) }
    }

    @Test
    fun `insertPhoto calls dao and returns no id on failure`() = testCoroutineRule.runTest {
        // Arrange
        val expectedFailureId = -1L
        coEvery { photoDao.insert(any()) } returns expectedFailureId

        // Act
        val resultId = repository.insertPhoto(photo)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { photoDao.insert(any()) }
    }

    @Test
    fun `insertPointOfInterest calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val poiDto = pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest)
        coEvery { pointOfInterestDao.insert(any()) } returns 1L  // Assume success is represented by returning ID 1

        // Act
        val resultId = repository.insertPointOfInterest(pointOfInterest)
        runCurrent()

        // Assert
        assertEquals(1L, resultId)
        coVerify(exactly = 1) { pointOfInterestDao.insert(poiDto) }
    }

    @Test
    fun `insertPointOfInterest calls dao and returns no id on failure`() = testCoroutineRule.runTest {
        // Arrange
        val expectedFailureId = -1L
        coEvery { pointOfInterestDao.insert(any()) } returns expectedFailureId

        // Act
        val resultId = repository.insertPointOfInterest(pointOfInterest)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { pointOfInterestDao.insert(any()) }
    }

    @Test
    fun `getAllProperties emits correct data`() = testCoroutineRule.runTest {
        // Arrange
        val propertyDtoList = listOf(mockk<PropertyWithPhotosAndPoisEntity>())
        val expectedEntityList = listOf(mockk<PropertyWithPhotosAndPOI>())
        coEvery { propertyDao.getAllProperties() } returns flowOf(propertyDtoList)
        coEvery { propertyWithPhotosAndPoisEntityMapper.mapToEntity(any()) } returnsMany expectedEntityList

        // Act
        val results = repository.getAllProperties().toList()

        // Assert
        assertEquals(listOf(expectedEntityList), results)
        coVerify(exactly = 1) { propertyDao.getAllProperties() }
    }

    @Test
    fun `getAllProperties emits error when dao throws exception`() = testCoroutineRule.runTest {
        // Arrange
        val exception = IOException("Database access error")
        coEvery { propertyDao.getAllProperties() } returns flow { throw exception }

        // Act & Assert
        val flow = repository.getAllProperties()
        flow
            .catch { e ->
                assertEquals("Database access error", e.message)
            }
            .collect {
                fail("No data should be emitted in case of an error")
            }

        coVerify(exactly = 1) { propertyDao.getAllProperties() }
    }

    @Test
    fun `getPropertyById emits correct data`() = testCoroutineRule.runTest {
        // Arrange
        val propertyId = 1L
        val propertyDto = mockk<PropertyWithPhotosAndPoisEntity>()
        val expectedEntity = mockk<PropertyWithPhotosAndPOI>()
        coEvery { propertyDao.getPropertyById(propertyId) } returns flowOf(propertyDto)
        coEvery { propertyWithPhotosAndPoisEntityMapper.mapToEntity(propertyDto) } returns expectedEntity

        // Act
        val result = repository.getPropertyById(propertyId).first()

        // Assert
        assertEquals(expectedEntity, result)
        coVerify(exactly = 1) { propertyDao.getPropertyById(propertyId) }
    }

    @Test
    fun `getPropertyById emits error when dao throws exception`() = testCoroutineRule.runTest {
        // Arrange
        val propertyId = 1L
        val exception = IOException("Database access error")
        coEvery { propertyDao.getPropertyById(propertyId) } returns flow { throw exception }

        // Act & Assert
        val flow = repository.getPropertyById(propertyId)
        flow
            .catch { e -> assertEquals("Database access error", e.message) }
            .collect {
                fail("No data should be emitted in case of an error")
            }

        coVerify(exactly = 1) { propertyDao.getPropertyById(propertyId) }
    }

    @Test
    fun `getAllPropertiesLatLng emits correct data`() = testCoroutineRule.runTest {
        // Arrange
        val latLngList = listOf("45.5017,-73.5673", "48.8566,2.3522")
        coEvery { propertyDao.getAllLatLng() } returns flowOf(latLngList)

        // Act
        val results = repository.getAllPropertiesLatLng().toList()

        // Assert
        assertEquals(listOf(latLngList), results)
        coVerify(exactly = 1) { propertyDao.getAllLatLng() }
    }

    @Test
    fun `getAllPropertiesLatLng emits error when dao throws exception`() = testCoroutineRule.runTest {
        // Arrange
        val exception = IOException("Database access error")
        coEvery { propertyDao.getAllLatLng() } returns flow { throw exception }

        // Act & Assert
        val flow = repository.getAllPropertiesLatLng()
        flow
            .catch { e -> assertEquals("Database access error", e.message) }
            .collect {
                fail("No data should be emitted in case of an error")
            }

        coVerify(exactly = 1) { propertyDao.getAllLatLng() }
    }

    @Test
    fun `getPropertyPhotos emits correct data`() = testCoroutineRule.runTest {
        // Arrange
        val photoEntityLists = listOf(
            PhotoEntity(1, 1, "Description", "url", 123456789, true),
            PhotoEntity(2, 1, "Description 2", "url2", 123456789, false)
        )
        val expectedEntityList = photoEntityLists.map { photoEntityMapper.mapPhotoDtoToPhotoEntity(it) }
        coEvery { photoDao.getPropertyPhotos(any()) } returns flowOf(photoEntityLists)

        // Act
        val results = repository.getPropertyPhotos(1L).toList()
        runCurrent()

        // Assert
        assertEquals(listOf(expectedEntityList), results)
        coVerify(exactly = 1) { photoDao.getPropertyPhotos(1L) }
    }

    @Test
    fun `getPropertyPhotos emits error when dao throws exception`() = testCoroutineRule.runTest {
        // Arrange
        val exception = IOException("Database access error")
        coEvery { photoDao.getPropertyPhotos(any()) } returns flow { throw exception }

        // Act & Assert
        val flow = repository.getPropertyPhotos(1L)
        flow
            .catch { e -> assertEquals("Database access error", e.message) }
            .collect {
                fail("No data should be emitted in case of an error")
            }

        coVerify(exactly = 1) { photoDao.getPropertyPhotos(1L) }
    }

    @Test
    fun `updateProperty calls dao update with correct data and returns result`() = testCoroutineRule.runTest {
        // Arrange
        val property = mockk<Property>()
        val propertyEntity = mockk<PropertyEntity>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { propertyDao.update(propertyEntity) } returns updateResult
        coEvery { propertyEntityMapper.mapPropertyEntityToPropertyDto(property) } returns propertyEntity

        // Act
        val result = repository.updateProperty(property)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { propertyDao.update(propertyEntity) }
    }

    @Test
    fun `updateProperty returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val property = mockk<Property>()
        val propertyEntity = mockk<PropertyEntity>()
        coEvery { propertyDao.update(propertyEntity) } returns 0
        coEvery { propertyEntityMapper.mapPropertyEntityToPropertyDto(property) } returns propertyEntity

        // Act
        val updateResult = repository.updateProperty(property)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { propertyDao.update(propertyEntity) }
    }

    @Test
    fun `updatePhoto calls dao update with correct data and returns result`() = testCoroutineRule.runTest {
        // Arrange
        val photo = mockk<Photo>()
        val photoEntity = mockk<PhotoEntity>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { photoDao.update(photoEntity) } returns updateResult
        coEvery { photoEntityMapper.mapPhotoEntityToPhotoDto(photo) } returns photoEntity

        // Act
        val result = repository.updatePhoto(photo)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { photoDao.update(photoEntity) }
    }

    @Test
    fun `updatePhoto returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val photo = mockk<Photo>()
        val photoEntity = mockk<PhotoEntity>()
        coEvery { photoDao.update(photoEntity) } returns 0 // No rows updated
        coEvery { photoEntityMapper.mapPhotoEntityToPhotoDto(photo) } returns photoEntity

        // Act
        val updateResult = repository.updatePhoto(photo)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { photoDao.update(photoEntity) }
    }

    @Test
    fun `updatePointOfInterest calls dao update with correct data and returns result`() = testCoroutineRule.runTest {
        // Arrange
        val pointOfInterest = mockk<PointOfInterest>()
        val poiDto = mockk<PointOfInterestEntity>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { pointOfInterestDao.update(poiDto) } returns updateResult
        coEvery { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest) } returns poiDto

        // Act
        val result = repository.updatePointOfInterest(pointOfInterest)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { pointOfInterestDao.update(poiDto) }
    }

    @Test
    fun `updatePointOfInterest returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val pointOfInterest = mockk<PointOfInterest>()
        val poiDto = mockk<PointOfInterestEntity>()
        coEvery { pointOfInterestDao.update(poiDto) } returns 0 // No rows updated
        coEvery { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest) } returns poiDto

        // Act
        val updateResult = repository.updatePointOfInterest(pointOfInterest)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { pointOfInterestDao.update(poiDto) }
    }

    @Test
    fun `updatePointOfInterestWithPropertyId deletes old POIs and inserts new ones`() = testCoroutineRule.runTest {
        // Arrange
        val propertyId = 1L
        val pointOfInterestEntities = listOf(mockk<PointOfInterest>(relaxed = true))
        val poisDto =
            pointOfInterestEntities.map { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(it) }

        every { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(any()) } answers { poisDto.first() }

        coEvery { pointOfInterestDao.deletePOIsByPropertyId(propertyId) } coAnswers { }
        poisDto.forEach { poiDto ->
            coEvery { pointOfInterestDao.insert(poiDto) } coAnswers { 1L }
        }

        // Act
        repository.updatePointOfInterestWithPropertyId(propertyId, pointOfInterestEntities)

        // Assert
        coVerify(exactly = 1) { pointOfInterestDao.deletePOIsByPropertyId(propertyId) }
        poisDto.forEach { poiDto ->
            coVerify(exactly = 1) { pointOfInterestDao.insert(poiDto) }
        }
        confirmVerified(pointOfInterestDao)
    }

    @Test
    fun `updatePointOfInterestWithPropertyId handles errors during deletion and insertion`() =
        testCoroutineRule.runTest {
            // Arrange
            val propertyId = 1L
            val pointOfInterestEntities = listOf(mockk<PointOfInterest>())
            val poisDto =
                pointOfInterestEntities.map {
                    pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(
                        it
                    )
                }
            val exception = RuntimeException("Database operation failed")

            every { pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(any()) } answers { poisDto.first() }
            coEvery { pointOfInterestDao.deletePOIsByPropertyId(propertyId) } throws exception
            poisDto.forEach { poiDto ->
                coEvery { pointOfInterestDao.insert(poiDto) } coAnswers { 1L }
            }

            // Act & Assert
            var thrownException: Exception? = null
            try {
                repository.updatePointOfInterestWithPropertyId(propertyId, pointOfInterestEntities)
            } catch (e: Exception) {
                thrownException = e
            }
            assertNotNull(thrownException)
            assertEquals("Database operation failed", thrownException?.message)
            coVerify(exactly = 1) { pointOfInterestDao.deletePOIsByPropertyId(propertyId) }
            poisDto.forEach { poiDto ->
                coVerify(exactly = 0) { pointOfInterestDao.insert(poiDto) }
            }
            confirmVerified(pointOfInterestDao)
        }


}
