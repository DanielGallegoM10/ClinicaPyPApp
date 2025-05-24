package com.example.clinicapypapp.MisDatosScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicapypapp.NavigationWrapper.ServicesDest
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.SectionList
import com.example.clinicapypapp.components.UserDataItem
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Usuario
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDatosScreen(
    idUsuario: Int,
    navigateToBack: () -> Unit,
    navigateToMisCitas: () -> Unit,
    navigateToMain: (idUsuario: Int) -> Unit,
    navigateToQuienSomos: (idUsuario: Int) -> Unit
) {

    var usuario by remember { mutableStateOf<Usuario?>(null) }

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val apiService = remember { ApiService(KtorClient.httpClient) }

    var selectedItemIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            usuario = apiService.getUsuarioById(idUsuario)
        } catch (e: Exception) {
            error = "Error al cargar usuario: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomTitleLuxury("Mis Datos") },
                navigationIcon = {
                    CustomBackIcon { navigateToBack() }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Transparent) {
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
                        },
                        icon = {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Mis Datos"
                            )
                        },
                        label = { Text("Mis Datos") },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = selectedItemIndex == 2,
                        onClick = {
                            selectedItemIndex = 2
                            navigateToMisCitas()
                        },
                        icon = {
                            Icon(
                                Icons.Filled.CalendarToday,
                                contentDescription = "Mis Citas"
                            )
                        },
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFCE4EC).copy(alpha = 0.85f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                UserDataItem(
                                    icon = Icons.AutoMirrored.Filled.Feed,
                                    label = "Nombre",
                                    value = usuario?.nombre ?: "No disponible"
                                )
                                UserDataItem(
                                    icon = Icons.AutoMirrored.Filled.Feed,
                                    label = "Apellidos",
                                    value = usuario?.apellidos ?: "No disponible"
                                )
                                UserDataItem(
                                    icon = Icons.Filled.Email,
                                    label = "Email",
                                    value = usuario?.email ?: "No disponible"
                                )
                                UserDataItem(
                                    icon = Icons.Filled.Badge,
                                    label = "DNI",
                                    value = usuario?.dni ?: "No disponible"
                                )
                                UserDataItem(
                                    icon = Icons.Filled.AccountCircle,
                                    label = "Nombre de Usuario",
                                    value = usuario?.nombreUsuario ?: "No disponible"
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Contraseña",
                                        modifier = Modifier.size(28.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Contraseña",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (passwordVisible) usuario?.contrasenia
                                                ?: "N/A" else "••••••••••",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 18.sp
                                        )
                                    }
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}