package com.persival.realestatemanagerkotlin.ui.view_models

/*class AddOrModifyPropertyViewModelTest {

    // Mocks
    private val getRealEstateAgentUseCase = mockk<GetRealEstateAgentUseCase>()
    private val insertPropertyUseCase = mockk<InsertPropertyUseCase>()
    private val insertPhotoUseCase = mockk<InsertPhotoUseCase>()
    private val insertPointOfInterestUseCase = mockk<InsertPointOfInterestUseCase>()
    private val getPropertyWithPhotoAndPOIUseCase = mockk<GetPropertyWithPhotoAndPOIUseCase>()
    private val getSelectedPropertyIdUseCase = mockk<GetSelectedPropertyIdUseCase>()
    private val getSavedStateForDateConversionButtonUseCase = mockk<GetSavedStateForDateConversionButtonUseCase>()
    private val refreshStoragePermissionUseCase = mockk<RefreshStoragePermissionUseCase>()
    private val refreshCameraPermissionUseCase = mockk<RefreshCameraPermissionUseCase>()
    private val hasCameraPermissionUseCase = mockk<HasCameraPermissionUseCase>()
    private val hasStoragePermissionUseCase = mockk<HasStoragePermissionUseCase>()

    private lateinit var viewModel: AddOrModifyPropertyViewModel

    @Before
    fun setUp() {
        clearAllMocks()
        viewModel = AddOrModifyPropertyViewModel(
            insertPhotoUseCase,
            insertPropertyUseCase,
            insertPointOfInterestUseCase,
            getRealEstateAgentUseCase,
            getPropertyWithPhotoAndPOIUseCase,
            getSelectedPropertyIdUseCase,
            getSavedStateForDateConversionButtonUseCase,
            getPropertyPhotosUseCase,
            refreshStoragePermissionUseCase,
            refreshCameraPermissionUseCase,
            hasCameraPermissionUseCase,
            hasStoragePermissionUseCase,
            updatePointOfInterestUseCase,
            updatePropertyUseCase,
            deletePhotoUseCase,
        )
    }

    @Test
    fun `given a date, when getFormattedDate is called, should return appropriate formatted date`() {
        // Arrange
        val dummyDate = Date()
        val isFrenchDateEnabled = true
        every { getSavedStateForDateConversionButtonUseCase.invoke() } returns isFrenchDateEnabled

        // Act
        val result = viewModel.getFormattedDate(dummyDate)

        // Assert
        assertEquals(Utils.getTodayDateInFrench(dummyDate), result)
    }

    @Test
    fun `when refreshStoragePermission is called, should refresh storage permission`() {
        // Arrange
        coEvery { refreshStoragePermissionUseCase.invoke() } just Runs

        // Act
        viewModel.refreshStoragePermission()

        // Assert
        coVerify { refreshStoragePermissionUseCase.invoke() }
    }

    @Test
    fun `when refreshCameraPermission is called, should refresh camera permission`() {
        // Arrange
        coEvery { refreshCameraPermissionUseCase.invoke() } just Runs

        // Act
        viewModel.refreshCameraPermission()

        // Assert
        coVerify { refreshCameraPermissionUseCase.invoke() }
    }

    @Test
    fun `when hasCameraPermission is called, should return camera permission status`() = runBlockingTest {
        // Arrange
        val hasPermission = true
        coEvery { hasCameraPermissionUseCase.invoke() } returns flowOf(hasPermission)

        // Act
        val result = viewModel.hasCameraPermission().first()

        // Assert
        coVerify { hasCameraPermissionUseCase.invoke() }
        assertEquals(hasPermission, result)
    }

    @Test
    fun `when hasStoragePermission is called, should return storage permission status`() = runBlockingTest {
        // Arrange
        val hasPermission = true
        coEvery { hasStoragePermissionUseCase.invoke() } returns flowOf(hasPermission)

        // Act
        val result = viewModel.hasStoragePermission().first()

        // Assert
        coVerify { hasStoragePermissionUseCase.invoke() }
        assertEquals(hasPermission, result)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}*/
