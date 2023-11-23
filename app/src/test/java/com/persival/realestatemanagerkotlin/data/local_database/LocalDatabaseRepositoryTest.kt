package com.persival.realestatemanagerkotlin.data.local_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDto
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDto
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDto
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisDto
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisDtoMapper
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
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
    private val propertyDtoMapper: PropertyDtoMapper = mockk()
    private val photoDtoMapper: PhotoDtoMapper = mockk()
    private val pointOfInterestDtoMapper: PointOfInterestDtoMapper = mockk()
    private val propertyWithPhotosAndPoisDtoMapper: PropertyWithPhotosAndPoisDtoMapper = mockk()

    private lateinit var repository: LocalDatabaseRepository
    private lateinit var propertyEntity: PropertyEntity
    private lateinit var photoEntity: PhotoEntity
    private lateinit var pointOfInterestEntity: PointOfInterestEntity

    @Before
    fun setUp() {
        every { coroutineDispatcherProvider.io } returns Dispatchers.IO
        // Set up the mocks with default responses
        coJustRun { propertyDao.insert(any()) }
        coJustRun { photoDao.insert(any()) }
        coJustRun { pointOfInterestDao.insert(any()) }
        coEvery { propertyDtoMapper.mapFromDomainModel(any()) } returns mockk()
        coEvery { photoDtoMapper.mapFromDomainModel(any()) } returns mockk()
        coEvery { pointOfInterestDtoMapper.mapFromDomainModel(any()) } returns mockk()
        coEvery { propertyWithPhotosAndPoisDtoMapper.mapToEntity(any()) } returns mockk()

        repository = LocalDatabaseRepository(
            coroutineDispatcherProvider = coroutineDispatcherProvider,
            propertyDao = propertyDao,
            photoDao = photoDao,
            pointOfInterestDao = pointOfInterestDao,
            propertyDtoMapper = propertyDtoMapper,
            photoDtoMapper = photoDtoMapper,
            pointOfInterestDtoMapper = pointOfInterestDtoMapper,
            propertyWithPhotosAndPoisDtoMapper = propertyWithPhotosAndPoisDtoMapper
        )

        // Initialize the Entity instances
        propertyEntity = PropertyEntity(
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
            entryDate = "2023-01-01",,
            saleDate = null,
            agentName = "John Doe"
        )

        photoEntity = PhotoEntity(
            id = 0L,
            propertyId = 0L,
            description = "Façade de la maison",
            photoUrl = "http://exemple.com/photo.jpg"
        )

        pointOfInterestEntity = PointOfInterestEntity(
            id = 0L,
            propertyId = 0L,
            poi = "Parc à proximité"
        )

        every { photoDtoMapper.mapToEntity(any()) } answers {
            PhotoEntity(
                id = firstArg<PhotoDto>().id,
                propertyId = firstArg<PhotoDto>().propertyId,
                description = firstArg<PhotoDto>().description,
                photoUrl = firstArg<PhotoDto>().photoUrl
            )
        }
    }

    @Test
    fun `insertProperty calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val propertyDto = propertyDtoMapper.mapFromDomainModel(propertyEntity)
        coEvery { propertyDao.insert(any()) } returns 1L

        // Act
        val resultId = repository.insertProperty(propertyEntity)
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
        val resultId = repository.insertProperty(propertyEntity)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { propertyDao.insert(any()) }
    }

    @Test
    fun `insertPhoto calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val photoDto = photoDtoMapper.mapFromDomainModel(photoEntity)
        coEvery { photoDao.insert(any()) } returns 1L  // Assume success is represented by returning ID 1

        // Act
        val resultId = repository.insertPhoto(photoEntity)
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
        val resultId = repository.insertPhoto(photoEntity)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { photoDao.insert(any()) }
    }

    @Test
    fun `insertPointOfInterest calls dao and returns id on success`() = testCoroutineRule.runTest {
        // Arrange
        val poiDto = pointOfInterestDtoMapper.mapFromDomainModel(pointOfInterestEntity)
        coEvery { pointOfInterestDao.insert(any()) } returns 1L  // Assume success is represented by returning ID 1

        // Act
        val resultId = repository.insertPointOfInterest(pointOfInterestEntity)
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
        val resultId = repository.insertPointOfInterest(pointOfInterestEntity)
        runCurrent()

        // Assert
        assertEquals(expectedFailureId, resultId)
        coVerify(exactly = 1) { pointOfInterestDao.insert(any()) }
    }

    @Test
    fun `getAllProperties emits correct data`() = testCoroutineRule.runTest {
        // Arrange
        val propertyDtoList = listOf(mockk<PropertyWithPhotosAndPoisDto>())
        val expectedEntityList = listOf(mockk<PropertyWithPhotosAndPOIEntity>())
        coEvery { propertyDao.getAllProperties() } returns flowOf(propertyDtoList)
        coEvery { propertyWithPhotosAndPoisDtoMapper.mapToEntity(any()) } returnsMany expectedEntityList

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
        val propertyDto = mockk<PropertyWithPhotosAndPoisDto>()
        val expectedEntity = mockk<PropertyWithPhotosAndPOIEntity>()
        coEvery { propertyDao.getPropertyById(propertyId) } returns flowOf(propertyDto)
        coEvery { propertyWithPhotosAndPoisDtoMapper.mapToEntity(propertyDto) } returns expectedEntity

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
        val photoDtoList = listOf(
            PhotoDto(1, 1, "Description", "url", 123456789, true),
            PhotoDto(2, 1, "Description 2", "url2", 123456789, false)
        )
        val expectedEntityList = photoDtoList.map { photoDtoMapper.mapToEntity(it) }
        coEvery { photoDao.getPropertyPhotos(any()) } returns flowOf(photoDtoList)

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
        val propertyEntity = mockk<PropertyEntity>()
        val propertyDto = mockk<PropertyDto>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { propertyDao.update(propertyDto) } returns updateResult
        coEvery { propertyDtoMapper.mapFromDomainModel(propertyEntity) } returns propertyDto

        // Act
        val result = repository.updateProperty(propertyEntity)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { propertyDao.update(propertyDto) }
    }

    @Test
    fun `updateProperty returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val propertyEntity = mockk<PropertyEntity>()
        val propertyDto = mockk<PropertyDto>()
        coEvery { propertyDao.update(propertyDto) } returns 0
        coEvery { propertyDtoMapper.mapFromDomainModel(propertyEntity) } returns propertyDto

        // Act
        val updateResult = repository.updateProperty(propertyEntity)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { propertyDao.update(propertyDto) }
    }

    @Test
    fun `updatePhoto calls dao update with correct data and returns result`() = testCoroutineRule.runTest {
        // Arrange
        val photoEntity = mockk<PhotoEntity>()
        val photoDto = mockk<PhotoDto>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { photoDao.update(photoDto) } returns updateResult
        coEvery { photoDtoMapper.mapFromDomainModel(photoEntity) } returns photoDto

        // Act
        val result = repository.updatePhoto(photoEntity)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { photoDao.update(photoDto) }
    }

    @Test
    fun `updatePhoto returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val photoEntity = mockk<PhotoEntity>()
        val photoDto = mockk<PhotoDto>()
        coEvery { photoDao.update(photoDto) } returns 0 // No rows updated
        coEvery { photoDtoMapper.mapFromDomainModel(photoEntity) } returns photoDto

        // Act
        val updateResult = repository.updatePhoto(photoEntity)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { photoDao.update(photoDto) }
    }

    @Test
    fun `updatePointOfInterest calls dao update with correct data and returns result`() = testCoroutineRule.runTest {
        // Arrange
        val pointOfInterestEntity = mockk<PointOfInterestEntity>()
        val poiDto = mockk<PointOfInterestDto>()
        val updateResult = 1 // Assume that if one row was updated, the result is 1
        coEvery { pointOfInterestDao.update(poiDto) } returns updateResult
        coEvery { pointOfInterestDtoMapper.mapFromDomainModel(pointOfInterestEntity) } returns poiDto

        // Act
        val result = repository.updatePointOfInterest(pointOfInterestEntity)

        // Assert
        assertEquals(updateResult, result)
        coVerify(exactly = 1) { pointOfInterestDao.update(poiDto) }
    }

    @Test
    fun `updatePointOfInterest returns 0 when no rows are updated`() = testCoroutineRule.runTest {
        // Arrange
        val pointOfInterestEntity = mockk<PointOfInterestEntity>()
        val poiDto = mockk<PointOfInterestDto>()
        coEvery { pointOfInterestDao.update(poiDto) } returns 0 // No rows updated
        coEvery { pointOfInterestDtoMapper.mapFromDomainModel(pointOfInterestEntity) } returns poiDto

        // Act
        val updateResult = repository.updatePointOfInterest(pointOfInterestEntity)

        // Assert
        assertEquals(0, updateResult)
        coVerify(exactly = 1) { pointOfInterestDao.update(poiDto) }
    }

    @Test
    fun `updatePointOfInterestWithPropertyId deletes old POIs and inserts new ones`() = testCoroutineRule.runTest {
        // Arrange
        val propertyId = 1L
        val pointOfInterestEntities = listOf(mockk<PointOfInterestEntity>(relaxed = true))
        val poisDto = pointOfInterestEntities.map { pointOfInterestDtoMapper.mapFromDomainModel(it) }

        every { pointOfInterestDtoMapper.mapFromDomainModel(any()) } answers { poisDto.first() }

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
            val pointOfInterestEntities = listOf(mockk<PointOfInterestEntity>())
            val poisDto = pointOfInterestEntities.map { pointOfInterestDtoMapper.mapFromDomainModel(it) }
            val exception = RuntimeException("Database operation failed")

            every { pointOfInterestDtoMapper.mapFromDomainModel(any()) } answers { poisDto.first() }
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
