package com.realityexpander.data

import data.AppSettings
import data.FakeSettings
import data.MarkersRepo
import presentation.maps.LatLong
import presentation.maps.Marker
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

// Use real MarkerRepo and fake Settings
class FakeSettingsRealMarkersRepo(
    appSettings: AppSettings = AppSettings.use(FakeSettings())
): MarkersRepo(appSettings)


class MarkersRepoTest {

    private lateinit var fakeSettings: FakeSettings
    private lateinit var markerRepo: FakeSettingsRealMarkersRepo

    @BeforeTest
    fun setUp() {
        fakeSettings = FakeSettings()
        markerRepo = FakeSettingsRealMarkersRepo(AppSettings.use(fakeSettings))
    }

    @Test
    fun `MarkerRepo should be able to add a marker`() {
        // Arrange
        val marker = Marker(id = "M1")

        // Act
        markerRepo.addMarker(marker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), marker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], marker)
    }

    @Test
    fun `MarkerRepo should be able to remove a marker`() {
        // Arrange
        val marker = Marker(id = "M1")
        markerRepo.addMarker(marker)

        // Act
        markerRepo.removeMarker(marker.id)

        // Assert
        assertEquals(markerRepo.marker(marker.id), null)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], null)
    }

    @Test
    fun `MarkerRepo should be able to update a marker`() {
        // Arrange
        val marker = Marker(id = "M1")
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(title = "updated title")

        // Act
        markerRepo.replaceMarker(updatedMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to clear all markers`() {
        // Arrange
        val marker = Marker(id = "M1")
        val marker2 = Marker(id = "M2")
        markerRepo.addMarker(marker)
        markerRepo.addMarker(marker2)

        // Act
        markerRepo.clearAllMarkers()

        // Assert
        assertEquals(markerRepo.marker(marker.id), null)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], null)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M2"], null)
    }

    @Test
    fun `MarkerRepo should be able to get all markers`() {
        // Arrange
        val marker1 = Marker(id = "M1")
        val marker2 = Marker(id = "M2")
        markerRepo.addMarker(marker1)
        markerRepo.addMarker(marker2)

        // Act
        val markers = markerRepo.markers()

        // Assert
        assertEquals(markers, listOf(marker1, marker2))
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap.size, 2)
    }
    
    @Test
    fun `MarkerRepo should be able to get a marker`() {
        // Arrange
        val marker = Marker(id = "M1")
        markerRepo.addMarker(marker)

        // Act
        val result = markerRepo.marker(marker.id)

        // Assert
        assertEquals(result, marker)
    }
    
    @Test
    fun `MarkerRepo should return null for a marker that doesn't exist`() {
        // Arrange
        val marker = Marker(id = "M1")

        // Act
        val result = markerRepo.marker(marker.id)

        // Assert
        assertEquals(result, null)
    }
    
    @Test
    fun  `MarkerRepo should be able to update a marker's details and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            isDetailsLoaded = true,
            markerDetailsPageUrl = "updated markerDetailsPageUrl",
            mainPhotoUrl = "updated mainPhotoUrl",
            markerPhotos = listOf("updated markerPhotos"),
            photoCaptions = listOf("updated photoCaptions"),
            photoAttributions = listOf("updated photoAttributions"),
            inscription = "updated inscription",
            englishInscription = "updated englishInscription",
            spanishInscription = "updated spanishInscription",
        )

        // Act
        markerRepo.updateMarkerDetails(updatedMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to upsert a marker's details and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
        )
        val updatedDetailsMarker = marker.copy(
            isDetailsLoaded = true,
            markerDetailsPageUrl = "updated markerDetailsPageUrl",
            mainPhotoUrl = "updated mainPhotoUrl",
            markerPhotos = listOf("updated markerPhotos"),
            photoCaptions = listOf("updated photoCaptions"),
            photoAttributions = listOf("updated photoAttributions"),
            inscription = "updated inscription",
            englishInscription = "updated englishInscription",
            spanishInscription = "updated spanishInscription",
            erected = "updated erected",
            credits = "updated credits",
            location = "updated location",
        )

        // Act
        markerRepo.upsertMarkerDetails(updatedDetailsMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedDetailsMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap["M1"], updatedDetailsMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker's basic info and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isDetailsLoaded = true,
            isSeen = false,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            position = LatLong(3.0, 4.0),
            title = "updated title",
            subtitle = "updated subtitle",
            alpha = 0.75f
        )

        // Act
        markerRepo.updateMarkerBasicInfo(updatedMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to upsert basic info for a marker that doesn't exist`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isDetailsLoaded = true,
            isSeen = false,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        val updatedMarker = marker.copy(
            position = LatLong(3.0, 4.0),
            title = "updated title",
            subtitle = "updated subtitle",
            alpha = 0.75f
        )

        // Act
        markerRepo.upsertMarkerBasicInfo(updatedMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker completely `() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            position = LatLong(2.0, 3.0),
            title = "updated title",
            subtitle = "updated subtitle",
            alpha = 0.75f,
            isSeen = true,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "updated markerDetailsPageUrl",
            mainPhotoUrl = "updated mainPhotoUrl",
            markerPhotos = listOf("updated markerPhotos"),
            photoCaptions = listOf("updated photoCaptions"),
            photoAttributions = listOf("updated photoAttributions"),
            inscription = "updated inscription",
            englishInscription = "updated englishInscription",
            spanishInscription = "updated spanishInscription",
        )

        // Act
        markerRepo.replaceMarker(updatedMarker)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker's isSeen and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            isSeen = true
        )

        // Act
        markerRepo.updateMarkerIsSeen(marker, true)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker's isAnnounced and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
            isAnnounced = false,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            isAnnounced = true
        )

        // Act
        markerRepo.updateMarkerIsAnnounced(marker, true)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker's isSpoken and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            isSpoken = true
        )

        // Act
        markerRepo.updateMarkerIsSpoken(marker, isSpoken = true)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }

    @Test
    fun `MarkerRepo should be able to update a marker's isSpoken by id and retain all other info`() {
        // Arrange
        val marker = Marker(
            id = "M1",
            position = LatLong(1.0, 2.0),
            title = "title",
            subtitle = "subtitle",
            alpha = 0.5f,
            isSeen = false,
            isDetailsLoaded = true,
            markerDetailsPageUrl = "markerDetailsPageUrl",
            mainPhotoUrl = "mainPhotoUrl",
            markerPhotos = listOf("markerPhotos"),
            photoCaptions = listOf("photoCaptions"),
            photoAttributions = listOf("photoAttributions"),
            inscription = "inscription",
            englishInscription = "englishInscription",
            spanishInscription = "spanishInscription",
        )
        markerRepo.addMarker(marker)
        val updatedMarker = marker.copy(
            isSpoken = true
        )

        // Act
        markerRepo.updateMarkerIsSpoken(id = marker.id, isSpoken = true)

        // Assert
        assertEquals(markerRepo.marker(marker.id), updatedMarker)
        assertEquals(markerRepo.markersResult().markerIdToMarkerMap[marker.id], updatedMarker)
    }
}
