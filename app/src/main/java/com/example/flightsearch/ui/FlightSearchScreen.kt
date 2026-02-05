package com.example.flightsearch.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.flightsearch.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightMainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState =  viewModel.uiState.collectAsState()
    Column(modifier = modifier) {
        FlightTextField(
            uiState = uiState.value,
            search = viewModel::autoCompleteAgree,
            onValueChange = viewModel::userInputChange
        )
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
        modifier = modifier
    ) {
        TextField(
            value = uiState.userInput,
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