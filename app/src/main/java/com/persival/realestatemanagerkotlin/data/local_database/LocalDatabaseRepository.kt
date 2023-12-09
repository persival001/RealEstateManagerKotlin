package com.persival.realestatemanagerkotlin.data.local_database

import android.util.Log
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyEntityMapper
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisEntityMapper
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.model.Photo
import com.persival.realestatemanagerkotlin.domain.point_of_interest.model.PointOfInterest
import com.persival.realestatemanagerkotlin.domain.property.model.Property
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.model.PropertyWithPhotosAndPOI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDatabaseRepository @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val propertyDao: PropertyDao,
    private val photoDao: PhotoDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val propertyEntityMapper: PropertyEntityMapper,
    private val photoEntityMapper: PhotoEntityMapper,
    private val pointOfInterestEntityMapper: PointOfInterestEntityMapper,
    private val propertyWithPhotosAndPoisEntityMapper: PropertyWithPhotosAndPoisEntityMapper,
) : LocalRepository {

    // Create
    override suspend fun insertProperty(property: Property): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val propertyDto = propertyEntityMapper.mapPropertyEntityToPropertyDto(property)
                propertyDao.insert(propertyDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPhoto(photo: Photo): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val photoDto = photoEntityMapper.mapPhotoEntityToPhotoDto(photo)
                photoDao.insert(photoDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val poiDto =
                    pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest)
                pointOfInterestDao.insert(poiDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    // Read
    override fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOI>> =
        propertyDao
            .getAllProperties()
            .map { list ->
                list.map { propertyWithPhotosAndPoisEntityMapper.mapToEntity(it) }
            }
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOI> =
        propertyDao
            .getPropertyById(propertyId)
            .map { entity -> propertyWithPhotosAndPoisEntityMapper.mapToEntity(entity) }
            .flowOn(coroutineDispatcherProvider.io)

    override fun getAllPropertiesLatLng(): Flow<List<String>> =
        propertyDao
            .getAllLatLng()
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyPhotos(propertyId: Long): Flow<List<Photo>> =
        photoDao
            .getPropertyPhotos(propertyId)
            .map { list ->
                list.map { photoEntityMapper.mapPhotoDtoToPhotoEntity(it) }
            }
            .flowOn(coroutineDispatcherProvider.io)

    override fun getSearchedPropertiesWithPOIs(
        type: String?,
        minPrice: Int?,
        maxPrice: Int?,
        minArea: Int?,
        maxArea: Int?,
        isSold: Boolean?,
        timeFilter: String?,
        poiSchool: String?,
        poiRestaurant: String?,
        poiPublicTransport: String?,
        poiHospital: String?,
        poiStore: String?,
        poiGreenSpaces: String?,
    ): Flow<List<PropertyWithPhotosAndPOI>> =
        propertyDao
            .getSearchedPropertiesWithPOIs(
                type = type,
                minPrice = minPrice,
                maxPrice = maxPrice,
                minArea = minArea,
                maxArea = maxArea,
                isSold = isSold,
                timeFilter = timeFilter,
                poiSchool = poiSchool,
                poiRestaurant = poiRestaurant,
                poiPublicTransport = poiPublicTransport,
                poiHospital = poiHospital,
                poiStore = poiStore,
                poiGreenSpaces = poiGreenSpaces,
            )
            .map { list ->
                list.map { propertyWithPhotosAndPoisEntityMapper.mapToEntity(it) }
            }
            .flowOn(coroutineDispatcherProvider.io)

    // Update
    override suspend fun updateProperty(property: Property): Int =
        propertyDao.update(propertyEntityMapper.mapPropertyEntityToPropertyDto(property))

    override suspend fun updatePhoto(photo: Photo): Int =
        photoDao.update(photoEntityMapper.mapPhotoEntityToPhotoDto(photo))

    override suspend fun updatePointOfInterest(pointOfInterest: PointOfInterest): Int {
        val poiDto = pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(pointOfInterest)
        return pointOfInterestDao.update(poiDto)
    }

    override suspend fun updatePointOfInterestWithPropertyId(
        propertyId: Long,
        pointOfInterestEntities: List<PointOfInterest>
    ) {
        withContext(coroutineDispatcherProvider.io) {
            pointOfInterestDao.deletePOIsByPropertyId(propertyId)
            val poisDto =
                pointOfInterestEntities.map {
                    pointOfInterestEntityMapper.mapPointOfInterestEntityToPointOfInterestDto(
                        it
                    )
                }
            poisDto.forEach { poiDto ->
                pointOfInterestDao.insert(poiDto)
            }
        }
    }

    // Delete
    override suspend fun deletePhotoByPropertyIdAndPhotoId(propertyId: Long, photoId: Long) {
        withContext(coroutineDispatcherProvider.io) {
            photoDao.deletePhotoByPropertyIdAndPhotoId(propertyId, photoId)
        }
    }

}