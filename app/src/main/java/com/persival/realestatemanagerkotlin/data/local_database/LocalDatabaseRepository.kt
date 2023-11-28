package com.persival.realestatemanagerkotlin.data.local_database

import android.util.Log
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.photo.PhotoDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.point_of_interest.PointOfInterestDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.property.PropertyDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.property_with_photos_and_pois.PropertyWithPhotosAndPoisDtoMapper
import com.persival.realestatemanagerkotlin.domain.CoroutineDispatcherProvider
import com.persival.realestatemanagerkotlin.domain.photo.PhotoEntity
import com.persival.realestatemanagerkotlin.domain.point_of_interest.PointOfInterestEntity
import com.persival.realestatemanagerkotlin.domain.property.PropertyEntity
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.PropertyWithPhotosAndPOIEntity
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
    private val propertyDtoMapper: PropertyDtoMapper,
    private val photoDtoMapper: PhotoDtoMapper,
    private val pointOfInterestDtoMapper: PointOfInterestDtoMapper,
    private val propertyWithPhotosAndPoisDtoMapper: PropertyWithPhotosAndPoisDtoMapper,
) : LocalRepository {

    // Create
    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val propertyDto = propertyDtoMapper.mapFromDomainModel(propertyEntity)
                propertyDao.insert(propertyDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPhoto(photoEntity: PhotoEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val photoDto = photoDtoMapper.mapFromDomainModel(photoEntity)
                photoDao.insert(photoDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val poiDto = pointOfInterestDtoMapper.mapFromDomainModel(pointOfInterestEntity)
                pointOfInterestDao.insert(poiDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    // Read
    override fun getAllProperties(): Flow<List<PropertyWithPhotosAndPOIEntity>> =
        propertyDao
            .getAllProperties()
            .map { list ->
                list.map { propertyWithPhotosAndPoisDtoMapper.mapToEntity(it) }
            }
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyById(propertyId: Long): Flow<PropertyWithPhotosAndPOIEntity> =
        propertyDao
            .getPropertyById(propertyId)
            .map { dto -> propertyWithPhotosAndPoisDtoMapper.mapToEntity(dto) }
            .flowOn(coroutineDispatcherProvider.io)

    override fun getAllPropertiesLatLng(): Flow<List<String>> =
        propertyDao
            .getAllLatLng()
            .flowOn(coroutineDispatcherProvider.io)

    override fun getPropertyPhotos(propertyId: Long): Flow<List<PhotoEntity>> =
        photoDao
            .getPropertyPhotos(propertyId)
            .map { list ->
                list.map { photoDtoMapper.mapToEntity(it) }
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
    ): Flow<List<PropertyWithPhotosAndPOIEntity>> =
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
                list.map { propertyWithPhotosAndPoisDtoMapper.mapToEntity(it) }
            }
            .flowOn(coroutineDispatcherProvider.io)

    // Update
    override suspend fun updateProperty(propertyEntity: PropertyEntity): Int =
        propertyDao.update(propertyDtoMapper.mapFromDomainModel(propertyEntity))

    override suspend fun updatePhoto(photoEntity: PhotoEntity): Int =
        photoDao.update(photoDtoMapper.mapFromDomainModel(photoEntity))

    override suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int {
        val poiDto = pointOfInterestDtoMapper.mapFromDomainModel(pointOfInterestEntity)
        return pointOfInterestDao.update(poiDto)
    }

    override suspend fun updatePointOfInterestWithPropertyId(
        propertyId: Long,
        pointOfInterestEntities: List<PointOfInterestEntity>
    ) {
        withContext(coroutineDispatcherProvider.io) {
            pointOfInterestDao.deletePOIsByPropertyId(propertyId)
            val poisDto = pointOfInterestEntities.map { pointOfInterestDtoMapper.mapFromDomainModel(it) }
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