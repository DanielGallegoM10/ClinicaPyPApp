package com.example.clinicapypapp.CitasScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CitaItemView
import com.example.clinicapypapp.components.CustomAlertDialog
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Cita
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasUsuarioScreen(
    idUsuario: Int,
    navigateToBack: () -> Unit,
    navigateToMain: (idUsuario: Int) -> Unit,
    navigateToMisDatos: (idUsuario: Int) -> Unit,
    navigateToQuienSomos: (idUsuario: Int) -> Unit,
) {

    var misCitas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiService = remember { ApiService(KtorClient.httpClient) }
    var idCitaSeleccionada by remember { mutableStateOf<Int?>(null) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedItemIndex by remember { mutableIntStateOf(0) }



    LaunchedEffect(key1 = idUsuario) {
        isLoading = true
        errorMessage = null
        try {
            val citasObtenidas = apiService.getMisCitas(idUsuario)
            misCitas = citasObtenidas ?: emptyList()
        } catch (e: Exception) {
            errorMessage = "Error al cargar tus citas: ${e.localizedMessage}"
            misCitas = emptyList()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomTitleLuxury("Mis Citas") },
                navigationIcon = {
                    IconButton(onClick = navigateToBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFFCE4EC)) {

                NavigationBarItem(
                    selected = selectedItemIndex == 0,
                    onClick = {
                        selectedItemIndex = 0
                        navigateToMain(idUsuario)
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedItemIndex == 1,
                    onClick = {
                        selectedItemIndex = 1
                        navigateToMisDatos(idUsuario)
                    },
                    icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Mis Datos") },
                    label = { Text("Mis Datos") },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = selectedItemIndex == 2,
                    onClick = { selectedItemIndex = 2 },
                    icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Mis Citas") },
                    label = { Text("Mis Citas") }
                )
                NavigationBarItem(
                    selected = selectedItemIndex == 3,
                    onClick = {
                        selectedItemIndex = 3
                        navigateToQuienSomos(idUsuario)
                    },
                    icon = {
                        Icon(
                            Icons.Filled.QuestionMark,
                            contentDescription = "¿Quien Somos?"
                        )
                    },
                    label = { Text("¿Quien Somos?", textAlign = TextAlign.Center) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = painterResource(R.drawable.fondo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.4f,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    misCitas.isEmpty() -> {
                        Text(
                            text = "No tienes citas programadas.",
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(misCitas, key = { it.idCita ?: it.hashCode() }) { cita ->
                                CitaItemView(
                                    cita = cita,
                                    onCancelClick = {
                                        cita.idCita?.let { id ->
                                            idCitaSeleccionada = id
                                            mostrarDialogo = true
                                        } ?: run {
                                            Toast.makeText(
                                                context,
                                                "Error: ID de cita no válido.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (mostrarDialogo) {
                CustomAlertDialog(
                    "Anulación de cita",
                    "¿Estás seguro de que quieres anular tu cita?",
                    { mostrarDialogo = false },
                    {
                        val idParaBorrar = idCitaSeleccionada
                        if (idParaBorrar != null) {
                            scope.launch {
                                try {
                                    apiService.deleteCita(idParaBorrar)
                                    misCitas = misCitas.filterNot { it.idCita == idParaBorrar }
                                    Toast.makeText(
                                        context,
                                        "Cita anulada correctamente.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Error al anular la cita: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    mostrarDialogo = false
                                    idCitaSeleccionada = null
                                }
                            }
                        } else {
                            mostrarDialogo = false
                        }
                    }
                )
            }
        }
    }
}