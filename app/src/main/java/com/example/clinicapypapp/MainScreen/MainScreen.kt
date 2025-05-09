package com.example.clinicapypapp.MainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    navigateToMisDatos: () -> Unit,
    navigateToConfiguracion: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    var sections by remember { mutableStateOf<List<Seccion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val apiService = remember { ApiService(KtorClient.httpClient) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = { IconUserMenu(
                    navigateToMisDatos,
                    navigateToMisCitas,
                    navigateToConfiguracion,
                    onCerrarSesion
                ) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                    .padding(innerPadding)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomTitleLuxury("Bienvenido a Clinica PyP")
                Spacer(modifier = Modifier.height(16.dp))
                TextWithDivider("Selecciona una sección")
                Spacer(modifier = Modifier.height(16.dp))

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

                    else -> {
                        SectionList(
                            sections = sections,
                            onItemSelected = { seccionSeleccionada ->
                                val destino = ServicesDest(
                                    idUsuario = idUsuario,
                                    idSeccion = seccionSeleccionada.idSeccion ?: -1,
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