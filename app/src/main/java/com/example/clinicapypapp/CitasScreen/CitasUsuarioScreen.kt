package com.example.clinicapypapp.CitasScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.clinicapypapp.components.CitaItemView
import com.example.clinicapypapp.components.CustomAlertDialog
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.ServiceList
import com.example.clinicapypapp.components.TextWithDivider
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Cita
import com.example.clinicapypapp.data.models.Servicio
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasUsuarioScreen(idUsuario: Int, navigateToBack: () -> Unit, onItemSelected: (Cita) -> Unit) {
    var misCitas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val apiService = remember { ApiService(KtorClient.httpClient) }
    var idCita by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    var isCancel by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = idUsuario) {
        isLoading = true
        errorMessage = null
        try {

            val citasObtenidas = apiService.getMisCitas(idUsuario)
            if (citasObtenidas != null) {
                misCitas = citasObtenidas
            } else {
                misCitas = emptyList()
            }
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
                title = { Text("Mis Citas", color = Color.Black) },
                navigationIcon = {
                    CustomBackIcon { navigateToBack() }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            // Imagen de fondo
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
                        CircularProgressIndicator()
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "Error desconocido al cargar citas.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }

                    misCitas.isEmpty() -> {
                        Text(
                            text = "No tienes citas programadas.",
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(misCitas) { cita ->
                                CitaItemView(cita = cita) {
                                    isCancel = true
                                    idCita = cita.idCita!!
                                }
                            }
                        }
                    }
                }
            }
            if (isCancel) {
                CustomAlertDialog(
                    "Anulación de cita",
                    "¿Estás seguro de que quieres anular tu cita?",
                    { isCancel = false },
                    {
                        scope.launch {
                            apiService.deleteCita(idCita)
                        }
                        isCancel = false
                    }
                )
            }
        }
    }
}
