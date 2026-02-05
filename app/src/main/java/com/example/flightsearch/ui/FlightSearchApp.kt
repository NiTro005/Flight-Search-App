package com.example.flightsearch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flightsearch.R

@Composable
fun FlightSearchApp(viewModel: FlightMainViewModel) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.flight_search),
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp).align(Alignment.BottomStart)
                )
            }
        }
    ) {
        FlightSearchScreen(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(it))
    }
}
