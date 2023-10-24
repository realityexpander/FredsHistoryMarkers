package data.loadMarkerInfo

import MapMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import data.LoadingState
import data.loadMarkerInfo.sampleData.almadenVineyardsM2580
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.yield
import network.httpClient
import data.loadMarkers.kBaseHmdbDotOrgUrl

const val kUseFakeData = false

@Composable
fun loadMapMarkerInfo(mapMarker: MapMarker): LoadingState<MapMarker> {
    var loadingState by remember {
        mutableStateOf<LoadingState<MapMarker>>(LoadingState.Loading)
    }

    fun String.calculateMarkerInfoPageUrl(): String {
        val markerKeyNumber = this.substringAfter ("M").toInt()
        return kBaseHmdbDotOrgUrl + "m.asp?m=$markerKeyNumber"
    }
    val markerInfoPageUrl by remember(mapMarker.key) {
        mutableStateOf(mapMarker.key.calculateMarkerInfoPageUrl())
    }

    LaunchedEffect(markerInfoPageUrl) {
        println("loadMapMarkerInfo $markerInfoPageUrl")
        if(markerInfoPageUrl == "") {
            //loadingState = LoadingState.Error("MarkerInfoPageUrl is empty")
            return@LaunchedEffect
        }

        loadingState = LoadingState.Loading
        yield() // Allow UI to render LoadingState.Loading

        try {
            if(kUseFakeData) {
//                loadingState = fakeLoadingStateForParseMarkerInfoPageHtml(mapMarker)

                val markerInfoPageHtml = almadenVineyardsM2580()
                val result = parseMarkerInfoPageHtml(markerInfoPageHtml)
                loadingState = LoadingState.Loaded(
                    result.second!!
                )
            } else {
//                val response = httpClient.get(mapMarker.markerInfoPageUrl)
                val response = httpClient.get(markerInfoPageUrl)
                val markerInfoPageHtml = response.body<String>()

                // parse the page html into a MarkerInfo object
                val parsedMarkerResult = parseMarkerInfoPageHtml(markerInfoPageHtml)
                parsedMarkerResult.second ?: throw Exception(parsedMarkerResult.first)

                // update the passed-in marker with the parsed info and return it
                val markerInfo = parsedMarkerResult.second!!
                loadingState = LoadingState.Loaded(
                    mapMarker.copy(
                        isDescriptionLoaded = true,
                        inscription = markerInfo.inscription,
                        erected = markerInfo.erected,
                        mainPhotoUrl = markerInfo.mainPhotoUrl,
                        markerPhotos = markerInfo.markerPhotos,
                        photoCaptions = markerInfo.photoCaptions,
                        photoAttributions = markerInfo.photoAttributions,
                        credits = markerInfo.credits
                    )
                )
            }
        } catch (e: Exception) {
            loadingState = LoadingState.Error(e.message ?: e.cause?.message ?: "Loading error")
        }
    }

    return loadingState
}


