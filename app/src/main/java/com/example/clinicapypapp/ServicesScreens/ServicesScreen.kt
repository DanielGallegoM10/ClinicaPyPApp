package com.example.clinicapypapp.ServicesScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.IconUser
import com.example.clinicapypapp.components.ServiceList
import com.example.clinicapypapp.components.TextWithDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(sectionName: String, navigateToBack: () -> Unit, onItemSelected: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                actions = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        CustomBackIcon { navigateToBack() }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.fondo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.4f,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CustomTitleLuxury(sectionName)
                Spacer(modifier = Modifier.height(10.dp))
                TextWithDivider("Seleccione el servicio")
                Spacer(modifier = Modifier.height(10.dp))
                ServiceList(sectionName) { onItemSelected() }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    ServicesScreen("Podología", {}, {})
}