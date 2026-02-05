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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightMainViewModel(
    private val dataStoreRepository: UserPreferencesRepository,
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository
): ViewModel() {

    private val userInput = MutableStateFlow<String>("")
    val uiState: StateFlow<FlightUIState> = combine(
        dataStoreRepository.prevRequest,
        airportRepository.getAllAirports(),
        favoriteRepository.getFavAirports(),
        userInput.debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { text ->
                if(text.isBlank()) {
                    flowOf(emptyList())
                } else {
                    airportRepository.getByCodeOrName(text)
                }
            },
        userInput
    ) {
        request, airports, favorites, autocomplete, input ->
        val airportsMap = airports.associate { it.iata_code to it.name }
        val favoritesSet = favorites.map { it.departure_code to it.destination_code }.toSet()
        val currentAirport = airports.find {it.iata_code == request}
        FlightUIState(
            userInput = input,
            dataStoreRequest = request,
            airports = airports,
            currentAirport = currentAirport,
            favorites = favorites,
            autoComplete = autocomplete,
            airportsCards = allAirportsCardGen(airportsMap, favoritesSet, currentAirport),
            favoritesCards = allFavoritesCardGen(favoritesSet, airportsMap),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FlightUIState()
    )

    init {
        viewModelScope.launch {
            userInput.value = dataStoreRepository.prevRequest.first()
        }
    }

    private fun allFavoritesCardGen(
        favorites: Set<Pair<String, String>>,
        airports: Map<String, String>): List<AirportsOnCardUi> {
        return favorites.map{ favorite ->
            val departure = favorite.first
            val destination = favorite.second
            AirportsOnCardUi(
                iata_dep = departure,
                iata_arr = destination,
                descr_dep = airports[departure] ?: "",
                descr_arr = airports[destination] ?: "",
                isFavorite = true
            )
        }
    }

    private fun allAirportsCardGen
                (airports: Map<String, String>,
                 favorites: Set<Pair<String, String>>,
                 currentAirport: Airport?): List<AirportsOnCardUi> {
        return if (currentAirport != null) {
            val airportsDistCur = airports - currentAirport.iata_code
            airportsDistCur.map { airport ->
                AirportsOnCardUi(
                    iata_dep = currentAirport.iata_code,
                    descr_dep = currentAirport.name,
                    iata_arr = airport.key,
                    descr_arr = airport.value,
                    isFavorite = (currentAirport.iata_code to airport.key) in favorites
                )
            }
        } else emptyList()
    }


    fun touchOnFavorite(card: AirportsOnCardUi) {
        viewModelScope.launch {
            if (!card.isFavorite) {
                favoriteRepository.insert(card.onFavorite())
            } else favoriteRepository.delete(card.onFavorite())
        }
    }

    fun autoCompleteAgree(request: String) {
        viewModelScope.launch {
            dataStoreRepository.saveRequest(request)
        }
    }
    
    fun userInputChange(input: String) {
        userInput.value = input
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
        destination_code = iata_arr,
        departure_code = iata_dep,
        id = id
    )
}


data class FlightUIState(
    val dataStoreRequest: String = "",
    val userInput: String = "",
    val currentAirport: Airport? = null,
    val airports: List<Airport> = listOf(),
    val favorites: List<Favorite> = emptyList(),
    val autoComplete: List<Airport> = emptyList(),
    val airportsCards: List<AirportsOnCardUi> = emptyList(),
    val favoritesCards: List<AirportsOnCardUi> = emptyList()
)

data class AirportsOnCardUi(
    val id: Int = 0,
    val iata_dep: String = "",
    val descr_dep: String = "",
    val iata_arr: String = "",
    val descr_arr: String = "",
    val isFavorite: Boolean = false
)