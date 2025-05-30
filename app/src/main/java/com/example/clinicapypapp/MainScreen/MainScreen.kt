package com.example.clinicapypapp.MainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicapypapp.NavigationWrapper.ServicesDest
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.*
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Seccion
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    idUsuario: Int,
    navigateToServices: (ServicesDest) -> Unit,
    navigateToMisCitas: () -> Unit,
    navigateToMisDatos: (idUsuario: Int) -> Unit,
    navigateToQuienSomos: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    //declaro los estados de la carga de datos y secciones
    var sections by remember { mutableStateOf<List<Seccion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val apiService = remember { ApiService(KtorClient.httpClient) } //objeto para llamar a las funciones API

    //Cargo los servicios de la sección correspondiente
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            delay(500L) //le pongo un delay para que se vea el circulo de carga, sino no se aprecia porque carga rápido
            sections = apiService.getAllSecciones()
        } catch (e: Exception) {
            error = "Error al cargar secciones: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    //Estructura de la pantalla, con Scaffold, TopAppBar y BottomNavigationBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {CustomTitleLuxury("Bienvenido a Clinica PyP") },
                actions = { IconUserMenu(
                    { navigateToMisDatos(idUsuario) },
                    navigateToMisCitas,
                    navigateToQuienSomos,
                    onCerrarSesion
                ) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Transparent){
                NavigationBar(
                    containerColor = Color(0xFFFCE4EC)
                ) {
                    NavigationBarItem(
                        selected = selectedItemIndex == 0,
                        onClick = { selectedItemIndex = 0 },
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
                                   navigateToMisCitas()},
                        icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Mis Citas") },
                        label = { Text("Mis Citas") }
                    )
                    NavigationBarItem(
                        selected = selectedItemIndex == 3,
                        onClick = { selectedItemIndex = 3
                                   navigateToQuienSomos()},
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
                TextWithDivider("Selecciona una sección")
                Spacer(modifier = Modifier.height(16.dp))

                //Manejamos los distintos estados de carga
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: $error", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    //Si todo va bien, mostramos la lista de secciones
                    else -> {
                        SectionList(
                            sections = sections,
                            onItemSelected = { seccionSeleccionada ->
                                val destino = ServicesDest(
                                    idUsuario = idUsuario,
                                    idSeccion = seccionSeleccionada.idSeccion ?: -1,
                                    idEspecialista = seccionSeleccionada.especialista?.idEspecialista
                                        ?: -1,
                                    sectionName = seccionSeleccionada.nombreSeccion
                                )
                                navigateToServices(destino)
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
//fun MainScreenPreview() {
//    MaterialTheme {
//        MainScreen(navigateToServices = {})
//    }
//}