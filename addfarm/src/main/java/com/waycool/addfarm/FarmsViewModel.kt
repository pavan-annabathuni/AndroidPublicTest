package com.waycool.addfarm

import androidx.lifecycle.*
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFindAutocompletePredictions
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.FarmsRepository
import com.waycool.data.repository.domainModels.DashboardDomain
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.*
import kotlinx.coroutines.*

class FarmsViewModel : ViewModel() {

    private val _events = MutableLiveData<PlacesSearchEvent>()
    val events: LiveData<PlacesSearchEvent> = _events

    private var searchJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onSearchQueryChanged(query: String, placesClient: PlacesClient) {
        searchJob?.cancel()

        _events.value = PlacesSearchEventLoading

        val handler = CoroutineExceptionHandler { _, throwable ->
            _events.value = PlacesSearchEventError(throwable)
        }
        searchJob = viewModelScope.launch(handler) {
            // Add delay so that network call is performed only after there is a 300 ms pause in the
            // search query. This prevents network calls from being invoked if the user is still
            // typing.
            delay(300)

//            val bias: LocationBias = RectangularBounds.newInstance(
//                LatLng(37.7576948, -122.4727051), // SW lat, lng
//                LatLng(37.808300, -122.391338) // NE lat, lng
//            )

            val response = placesClient
                .awaitFindAutocompletePredictions {
//                    typeFilter = TypeFilter.CITIES
                    this.query = query
                    countries = listOf("IN")
                }

            _events.value = PlacesSearchEventFound(response.autocompletePredictions)
        }
    }

    fun addFarm(
        accountId: Int,
        farmName: String,
        farm_center: String,
        farm_area: String,
        farm_json: String,
        plot_ids: String?,
        is_primary: Int?=null,
        farm_water_source: String?=null,
        farm_pump_hp: String?=null,
        farm_pump_type: String?=null,
        farm_pump_depth: String?=null,
        farm_pump_pipe_size: String?=null,
        farm_pump_flow_rate: String?=null
    ) =
        FarmsRepository.addFarm(
            accountId,
            farmName,
            farm_center,
            farm_area,
            farm_json,
            plot_ids,
            is_primary,
            farm_water_source,
            farm_pump_hp,
            farm_pump_type,
            farm_pump_depth,
            farm_pump_pipe_size,
            farm_pump_flow_rate
        ).asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData(

        )
    fun getFarms(): LiveData<Resource<List<MyFarmsDomain>>> =
        FarmsRepository.getMyFarms().asLiveData()


    fun getDashBoard(): LiveData<Resource<DashboardDomain?>> =
        CropsRepository.getDashBoard().asLiveData()
}