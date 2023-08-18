package com.persival.realestatemanagerkotlin.data.local_database

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

    override suspend fun insertProperty(propertyEntity: PropertyEntity): Long =
        withContext(coroutineDispatcherProvider.io) {
            val propertyDto = propertyMapper.mapFromDomainModel(propertyEntity)
            propertyDao.insert(propertyDto)
        }

    override suspend fun insertPhoto(photoEntity: PhotoEntity): Long =
        withContext(coroutineDispatcherProvider.io) {
            val photoDto = photoMapper.mapFromDomainModel(photoEntity)
            photoDao.insert(photoDto)
        }

    override suspend fun insertPointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Long =
        withContext(coroutineDispatcherProvider.io) {
            val poiDto = poiMapper.mapFromDomainModel(pointOfInterestEntity)
            pointOfInterestDao.insert(poiDto)
        }

    override suspend fun updatePropertyWithPhotosAndPOIs(
        property: PropertyEntity,
        photos: List<PhotoEntity>,
        pois: List<PointOfInterestEntity>
    ) {
        val propertyDto = propertyMapper.mapFromDomainModel(property)
        propertyDao.update(propertyDto)

        val photosDto = photos.map { photoMapper.mapFromDomainModel(it) }
        val poisDto = pois.map { poiMapper.mapFromDomainModel(it) }

        photoDao.deletePhotosByPropertyId(propertyDto.id)
        pointOfInterestDao.deletePOIsByPropertyId(propertyDto.id)

        photoDao.insertAll(photosDto)
        pointOfInterestDao.insertAll(poisDto)
    }

    override suspend fun updateProperty(propertyEntity: PropertyEntity): Int = propertyDao.update(
        propertyMapper.mapFromDomainModel(propertyEntity)
    )

    override suspend fun updatePhoto(photoEntity: PhotoEntity): Int {
        val photoDto = photoMapper.mapFromDomainModel(photoEntity)
        return photoDao.update(photoDto)
    }

    override suspend fun updatePointOfInterest(pointOfInterestEntity: PointOfInterestEntity): Int {
        val poiDto = poiMapper.mapFromDomainModel(pointOfInterestEntity)
        return pointOfInterestDao.update(poiDto)
    }

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
}