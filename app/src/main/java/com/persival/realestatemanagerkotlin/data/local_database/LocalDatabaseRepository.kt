package com.persival.realestatemanagerkotlin.data.local_database

import android.util.Log
import com.persival.realestatemanagerkotlin.data.local_database.dao.PhotoDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PointOfInterestDao
import com.persival.realestatemanagerkotlin.data.local_database.dao.PropertyDao
import com.persival.realestatemanagerkotlin.data.local_database.model.PhotoDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PointOfInterestDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyDtoMapper
import com.persival.realestatemanagerkotlin.data.local_database.model.PropertyWithPhotosAndPoisDtoMapper
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
    private val propertyMapper: PropertyDtoMapper,
    private val photoMapper: PhotoDtoMapper,
    private val poiMapper: PointOfInterestDtoMapper,
    private val propertyWithPhotosAndPoisDtoMapper: PropertyWithPhotosAndPoisDtoMapper,
) : LocalRepository {

    // Create
    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val propertyDto = propertyMapper.mapFromDomainModel(propertyEntity)
                propertyDao.insert(propertyDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPhoto(photoEntity: PhotoEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val photoDto = photoMapper.mapFromDomainModel(photoEntity)
                photoDao.insert(photoDto)
            } catch (e: Exception) {
                Log.d("LocalDbRepository", e.toString())
                null
            }
        }

    override suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long? =
        withContext(coroutineDispatcherProvider.io) {
            return@withContext try {
                val poiDto = poiMapper.mapFromDomainModel(pointOfInterestEntity)
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

    override fun getPhotoIdsForProperty(propertyId: Long): Flow<List<Long>> =
        photoDao.getPhotoIdsForProperty(propertyId).flowOn(coroutineDispatcherProvider.io)

    // Update
    override suspend fun updateProperty(propertyEntity: PropertyEntity): Int =
        propertyDao.update(propertyMapper.mapFromDomainModel(propertyEntity))

    override suspend fun updatePhotoWithPropertyId(propertyId: Long, photoEntities: List<PhotoEntity>) {
        withContext(coroutineDispatcherProvider.io) {
            photoDao.deletePhotoByPropertyId(propertyId)
            val photosDto = photoEntities.map { photoMapper.mapFromDomainModel(it) }
            photosDto.forEach { photoDto ->
                photoDao.insert(photoDto)
            }
        }
    }

    override suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int {
        val poiDto = poiMapper.mapFromDomainModel(pointOfInterestEntity)
        return pointOfInterestDao.update(poiDto)
    }

    override suspend fun updatePointOfInterestWithPropertyId(
        propertyId: Long,
        pointOfInterestEntities: List<PointOfInterestEntity>
    ) {
        withContext(coroutineDispatcherProvider.io) {
            pointOfInterestDao.deletePOIsByPropertyId(propertyId)
            val poisDto = pointOfInterestEntities.map { poiMapper.mapFromDomainModel(it) }
            poisDto.forEach { poiDto ->
                pointOfInterestDao.insert(poiDto)
            }
        }
    }

}