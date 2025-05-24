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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.ServiceList
import com.example.clinicapypapp.components.TextWithDivider
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Servicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    idUsuario: Int,
    sectionId: Int,
    sectionName: String,
    navigateToBack: () -> Unit,
    onItemSelected: (Servicio) -> Unit,
    navigateToMain: (idUsuario: Int) -> Unit,
    navigateToMisCitas: (idUsuario: Int) -> Unit,
    navigateToMisDatos: (idUsuario: Int) -> Unit,
    navigateToQuienSomos: (idUsuario: Int) -> Unit) {

    var services by rememberSaveable { mutableStateOf<List<Servicio>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedItemIndex by remember { mutableIntStateOf(0) }

    val apiService = remember { ApiService(KtorClient.httpClient) }

    LaunchedEffect(key1 = sectionId) {
        isLoading = true
        error = null
        try {
            services = apiService.getServiciosPorSeccion(sectionId)
        } catch (e: Exception) {
            error = "Error al cargar servicios: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {CustomTitleLuxury(sectionName)},
                navigationIcon = {
                    CustomBackIcon { navigateToBack() }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Transparent){
                NavigationBar(
                    containerColor = Color(0xFFFCE4EC)
                ) {
                    NavigationBarItem(
                        selected = selectedItemIndex == 0,
                        onClick = { selectedItemIndex = 0
                                   navigateToMain(idUsuario) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") },
                        colors =  NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = selectedItemIndex == 1,
                        onClick = { selectedItemIndex = 1
                            navigateToMisDatos(idUsuario) },
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Mis Datos") },
                        label = { Text("Mis Datos") }
                    )
                    NavigationBarItem(
                        selected = selectedItemIndex == 2,
                        onClick = { selectedItemIndex = 2
                            navigateToMisCitas(idUsuario)},
                        icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Mis Citas") },
                        label = { Text("Mis Citas") }
                    )
                    NavigationBarItem(
                        selected = selectedItemIndex == 3,
                        onClick = { selectedItemIndex = 3
                            navigateToQuienSomos(idUsuario)},
                        icon = { Icon(Icons.Filled.QuestionMark, contentDescription = "¿Quien Somos?") },
                        label = { Text("¿Quien Somos?", textAlign = TextAlign.Center) }
                    )
                }
            }
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
                    .padding(innerPadding)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextWithDivider("Seleccione el servicio")
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: $error",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    services.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay servicios disponibles para esta sección.",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        ServiceList(
                            services = services,
                            onItemSelected = { servicioSeleccionado ->
                                onItemSelected(servicioSeleccionado)
                            }
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ServicesScreenPreview() {
//    ServicesScreen("Podología", {}, {})
//}