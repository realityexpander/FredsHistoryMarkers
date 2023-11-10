package presentation.maps

import GoogleMaps
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger as Log

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    isFinishedLoadingMarkerData: Boolean = false,  // only sets the initial position, not tracked. Use `userLocation` for tracking.
    initialUserLocation: Location,
    userLocation: Location,
    markers: List<Marker>,
    mapBounds: List<LatLong>? = null,
    shouldRedrawMarkers: Boolean,
    onDidRedrawMarkers: () -> Unit = {},
    isTrackingEnabled: Boolean = false,
    shouldCenterCameraOnLocation: Location? = null,
    onDidCenterCameraOnLocation: () -> Unit = {},
    seenRadiusMiles: Double = .5,
    cachedMarkersLastUpdatedLocation: Location? = null,  // todo change to "settings"?
    onToggleIsTrackingEnabled: (() -> Unit)? = null,
    onFindMeButtonClicked: (() -> Unit)? = null,
    isMarkersLastUpdatedLocationVisible: Boolean = false,
    isMapOptionSwitchesVisible: Boolean = true,
    onMarkerInfoClick: ((Marker) -> Unit)? = null,
    shouldShowInfoMarker: Marker? = null,
    onDidShowInfoMarker: () -> Unit = {}
) {
    var isFirstUpdate by remember { mutableStateOf(true) } // force map to update at least once
    var didSetInitialCameraPosition by remember { mutableStateOf(false) } // Move to initial location on first update

    // Guard against initial location being (0.0, 0.0) (prevents initial map drawing in the middle of the atlantic ocean near africa)
    if(!initialUserLocation.isLocationValid()) return

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Log.d("💿 MapContent: isFinishedLoadingMarkerData=$isFinishedLoadingMarkerData, isFirstUpdate=$isFirstUpdate, shouldRedrawMapMarkers=$shouldRedrawMarkers")
        if (isFinishedLoadingMarkerData || !isFirstUpdate || shouldRedrawMarkers) { // todo use shouldRedrawMapMarkers instead of didMapMarkersRedraw
            GoogleMaps(
                modifier = modifier,
                isMapOptionSwitchesVisible = isMapOptionSwitchesVisible,
                isTrackingEnabled = isTrackingEnabled,
                userLocation = LatLong( // passed to map to track location
                    userLocation.latitude,
                    userLocation.longitude
                ),
                markers = markers.ifEmpty { null },
                shouldRedrawMapMarkers = shouldRedrawMarkers,
                onDidRedrawMapMarkers = onDidRedrawMarkers,
                shouldSetInitialCameraPosition =
                    if (!isFirstUpdate && !didSetInitialCameraPosition) {  // set initial camera position after first update
                        //Log.d("💿 MapContent.shouldSetInitialCameraPosition: isFirstUpdate=true,\n" +
                        //        "  setting initial camera position,\n" +
                        //        "  initialUserLocation=(${initialUserLocation.latitude}, ${initialUserLocation.longitude})"
                        //)
                        didSetInitialCameraPosition = true // only allow set initial camera position once
                        CameraPosition(
                            target = LatLong(
                                initialUserLocation.latitude,
                                initialUserLocation.longitude
                            ),
                            zoom = 12f  // note: forced zoom level
                        )
                    } else
                        null,
                shouldCenterCameraOnLatLong = remember(shouldCenterCameraOnLocation) {
                    // 37.422160,
                    // -122.084270  // googleplex
                    if(!isFirstUpdate) {
                        Log.d("💿 MapContent.shouldCenterCameraOnLatLong: ➤➤➤ Centering camera position")
                        shouldCenterCameraOnLocation?.let {
                            LatLong(
                                shouldCenterCameraOnLocation.latitude,
                                shouldCenterCameraOnLocation.longitude
                            )
                        } ?: run {
                            null
                        }
                    } else {
                        null
                    }
                },
                onDidCenterCameraOnLatLong = onDidCenterCameraOnLocation,
                cameraLocationBounds = remember {  // Center around bound of markers // note: does not allow user to move map
                    mapBounds?.let {
                        CameraLocationBounds(
                            coordinates = mapBounds,
                            padding = 80  // in pixels
                        )
                    } ?: run {
                        null // won't center around bounds
                    }
                },
                onMarkerInfoClick = onMarkerInfoClick,
                seenRadiusMiles = seenRadiusMiles,
                cachedMarkersLastUpdatedLocation = cachedMarkersLastUpdatedLocation,
                onToggleIsTrackingEnabledClick = onToggleIsTrackingEnabled,
                onFindMeButtonClick = onFindMeButtonClicked,
                isMarkersLastUpdatedLocationVisible = isMarkersLastUpdatedLocationVisible,
                shouldShowInfoMarker = shouldShowInfoMarker,
                onDidShowInfoMarker = onDidShowInfoMarker
            )

            // Indicate first update has occurred
            isFirstUpdate = false
        }
    }
}
