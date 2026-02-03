package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.util.copy
import com.example.flightsearch.FlightSearchApplication
import com.example.flightsearch.data.database.entity.Airport
import com.example.flightsearch.data.database.entity.Favorite
import com.example.flightsearch.data.repository.AirportRepository
import com.example.flightsearch.data.repository.FavoriteRepository
import com.example.flightsearch.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightMainViewModel(
    private val dataStoreRepository: UserPreferencesRepository,
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {

    val uiState: StateFlow<FlightUIState> = combine(
        dataStoreRepository.prevRequest,
        airportRepository.getAllAirports(),
        favoriteRepository.getFavAirports()
    ) {
        request, airports, favorites ->
        FlightUIState(
            request = request,
            airports = airports,
            favorites = favorites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FlightUIState()
    )


    fun getAllFavoritesCard(): List<AirportsOnCardUi> {
        return uiState.value.favorites.map{ favorite ->
            favorite.onAirportsOnCardUi().copy(
                descr_dep = uiState.value.airports
                    .find { it.iata_code == favorite.departure_code}
                    ?.name ?: "" ,
                descr_arr = uiState.value.airports
                    .find { it.iata_code == favorite.destination_code}
                    ?.name ?: "" ,
            )
        }
    }

    fun getAllRequestCard(): List<AirportsOnCardUi> {
        return TODO()
    }

    fun touchOnFavorite(card: AirportsOnCardUi) {
        viewModelScope.launch {
            if (!card.isFavorite) {
                favoriteRepository.insert(card.onFavorite())
            } else favoriteRepository.delete(card.onFavorite())
        }
    }

    fun searchAutoComplete(request: String): List<Airport> {
        return airportRepository.getByCodeOrName(request).stateIn(
            viewModelScope,

        )
    }

    fun autoCompleteAgree(airport: String) {
        viewModelScope.launch {
            dataStoreRepository.saveRequest(airport)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as FlightSearchApplication).container
                FlightMainViewModel(
                    dataStoreRepository = container.dataStoreRepository,
                    airportRepository = container.airportRepository,
                    favoriteRepository = container.favoriteRepository
                )
            }
        }
    }


}

fun AirportsOnCardUi.onFavorite(): Favorite {
    return Favorite(
        destination_code = iota_arr,
        departure_code = iota_dep,
        id = id
    )
}

fun Favorite.onAirportsOnCardUi(): AirportsOnCardUi {
    return AirportsOnCardUi(
        iota_dep = departure_code,
        iota_arr = destination_code,
        isFavorite = true
    )
}


data class FlightUIState(
    val request: String = "",
    val airports: List<Airport> = listOf(),
    val favorites: List<Favorite> = listOf()
)

data class AirportsOnCardUi(
    val id: Int = 0,
    val iota_dep: String = "",
    val descr_dep: String = "",
    val iota_arr: String = "",
    val descr_arr: String = "",
    val isFavorite: Boolean = false
)