package com.example.flightsearch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R
import com.example.flightsearch.data.database.entity.Favorite

enum class ContentType {
    Favorites,
    Airports
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightMainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by  viewModel.uiState.collectAsState()
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        FlightTextField(
            uiState = uiState,
            search = viewModel::autoCompleteAgree,
            onValueChange = viewModel::userInputChange
        )
        FlightContent(
            uiState = uiState,
            modifier = Modifier.padding(12.dp),
            onStarClick = viewModel::touchOnFavorite
        )
    }
}

@Composable
fun FlightContent(
    uiState: FlightUIState,
    modifier: Modifier = Modifier,
    onStarClick: (AirportsOnCardUi) -> Unit
){
    val type: ContentType = if (uiState.userInput.isEmpty()) ContentType.Favorites
                            else ContentType.Airports
    val content = if(type == ContentType.Favorites) uiState.favoritesCards else uiState.airportsCards
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = if (type == ContentType.Favorites) stringResource(R.string.favorite_routes)
                else stringResource(R.string.flights_from, uiState.currentAirport?.iata_code ?: "-"),
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        items(content) { item ->
            FlightCard(
                item = item,
                onStarClick = {onStarClick(item)}
            )
        }
    }

}

@Composable
fun FlightCard(
    item: AirportsOnCardUi,
    onStarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.depart),
                    style = MaterialTheme.typography.labelMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = item.iata_dep,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = item.descr_dep,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = stringResource(R.string.arrive),
                    style = MaterialTheme.typography.labelMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.iata_arr,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = item.descr_arr,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            IconButton(
                onClick = onStarClick,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.like),
                    tint = if (item.isFavorite)
                        Color(0xFFFFC107)
                    else
                        LocalContentColor.current
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun FlightTextField(
    uiState: FlightUIState,
    onValueChange: (String) -> Unit,
    search: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val autoComplete = uiState.autoComplete
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = it},
        modifier = modifier.padding(top = 8.dp)
    ) {
        TextField(
            value = uiState.userInput,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                onValueChange(it)
                expanded = it.isNotEmpty()
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        autoComplete.firstOrNull()?.let {
                            val iata = it.iata_code
                            search(iata)
                            onValueChange(iata)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded && autoComplete.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            autoComplete.forEach { airport ->
                DropdownMenuItem(
                    text = { Row {
                        Text(text = airport.iata_code, modifier = Modifier.padding(horizontal = 8.dp))
                        Text(text = airport.name, modifier = Modifier.padding(horizontal = 8.dp))
                    }},
                    onClick = {
                        val iata = airport.iata_code
                        search(iata)
                        onValueChange(iata)
                        expanded = false
                    }
                )
            }
        }
    }
}