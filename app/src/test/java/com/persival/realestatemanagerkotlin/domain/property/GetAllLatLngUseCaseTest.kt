package com.persival.realestatemanagerkotlin.domain.property

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.persival.realestatemanagerkotlin.domain.property_with_photos_and_poi.LocalRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetAllLatLngUseCaseTest {

    private lateinit var localRepository: LocalRepository
    private lateinit var getAllLatLngUseCase: GetAllLatLngUseCase

    @Before
    fun setUp() {
        localRepository = mockk()
        getAllLatLngUseCase = GetAllLatLngUseCase(localRepository)
    }

    @Test
    fun `invoke returns non-empty latLng list successfully`() = runBlocking {
        val fakeLatLngList = listOf("40.712776,-74.005974", "34.052235,-118.243683")
        val latLngFlow: Flow<List<String>> = flowOf(fakeLatLngList)

        coEvery { localRepository.getAllPropertiesLatLng() } returns latLngFlow

        val result = getAllLatLngUseCase.invoke().collect { list ->
            assertThat(list).isEqualTo(fakeLatLngList)
        }
    }

    @Test
    fun `invoke handles empty latLng list`() = runBlocking {
        val emptyLatLngList = emptyList<String>()
        val latLngFlow: Flow<List<String>> = flowOf(emptyLatLngList)

        coEvery { localRepository.getAllPropertiesLatLng() } returns latLngFlow

        val result = getAllLatLngUseCase.invoke().collect { list ->
            assertThat(list).isEqualTo(emptyLatLngList)
        }
    }
}
