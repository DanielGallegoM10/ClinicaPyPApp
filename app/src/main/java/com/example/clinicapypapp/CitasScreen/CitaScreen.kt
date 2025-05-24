package com.example.clinicapypapp.CitasScreen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.BookedSlotsResult
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomButton
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.DatePickerField
import com.example.clinicapypapp.components.TimeSlotData
import com.example.clinicapypapp.components.TimeSlotGrid
import com.example.clinicapypapp.components.TimeSlotStatus
import com.example.clinicapypapp.components.generateBaseTimeSlots
import com.example.clinicapypapp.data.api.ApiService
import com.example.clinicapypapp.data.api.KtorClient
import com.example.clinicapypapp.data.models.Cita
import com.example.clinicapypapp.data.models.Seccion
import com.example.clinicapypapp.data.models.Servicio
import com.example.clinicapypapp.data.models.Usuario
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaScreen(
    idUsuario: Int,
    idSeccion: Int,
    idServicio: Int,
    idEspecialista: Int,
    sectionName: String,
    navigateToBack: () -> Unit,
    navigateToMain: (idUsuario: Int) -> Unit,
    navigateToMisCitas: (idUsuario: Int) -> Unit,
    navigateToMisDatos: (idUsuario: Int) -> Unit,
    navigateToQuienSomos: (idUsuario: Int) -> Unit
) {
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var selectedTimeSlot by rememberSaveable { mutableStateOf<String?>(null) }

    var isBooking by rememberSaveable { mutableStateOf(false) }
    var bookingError by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    var bookedSlotsState by remember { mutableStateOf<BookedSlotsResult>(BookedSlotsResult.Idle) }

    var timeSlotsForGrid by remember { mutableStateOf<List<TimeSlotData>>(emptyList()) }

    val apiService = remember { ApiService(KtorClient.httpClient) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val apiDateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var selectedItemIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = selectedDate, key2 = idEspecialista) {
        if (selectedDate != null) {
            val fechaApiString = try {
                apiDateFormatter.format(java.util.Date(selectedDate!!))
            } catch (e: Exception) {
                null
            }

            if (fechaApiString != null) {
                bookedSlotsState = BookedSlotsResult.Loading

                try {
                    val horasOcupadasList: List<String> = apiService.getCitasbyEspecialistaAndFecha(
                        idEspecialista = idEspecialista,
                        fecha = fechaApiString
                    )

                    bookedSlotsState = BookedSlotsResult.Success(horasOcupadasList)

                    val horasOcupadasSet = horasOcupadasList.toSet()
                    val baseSlots = generateBaseTimeSlots("09:00", "18:01", 60)
                    timeSlotsForGrid = baseSlots.map { slotData ->
                        if (horasOcupadasSet.contains(slotData.time)) {
                            slotData.copy(status = TimeSlotStatus.Booked)
                        } else {
                            slotData.copy(status = TimeSlotStatus.Available)
                        }
                    }

                } catch (e: CancellationException) {
                    bookedSlotsState = BookedSlotsResult.Idle

                } catch (e: Exception) {
                    bookedSlotsState =
                        BookedSlotsResult.Error(e.message ?: "Error al obtener disponibilidad")
                }
            } else {
                bookedSlotsState =
                    BookedSlotsResult.Error("Error interno con la fecha seleccionada.")
            }
        } else {
            bookedSlotsState = BookedSlotsResult.Idle
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomTitleLuxury(sectionName) },
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomDescriptionTextField(
                    texto = descriptionText,
                    labelName = "Cuentanos tu necesidad",
                    onValueChange = { descriptionText = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerField(
                    labelName = "Fecha de la cita",
                    selectedDateMillis = selectedDate,
                    onDateSelected = { millis ->
                        selectedDate = millis
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                selectedDate?.let {
                    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    Text("Fecha seleccionada: ${dateFormatter.format(Date(it))}")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (val state = bookedSlotsState) {

                        is BookedSlotsResult.Idle -> {
                            if (selectedDate != null) {
                                Text("Selecciona una hora disponible.")
                            }
                        }

                        is BookedSlotsResult.Loading -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Consultando disponibilidad...")
                            }
                        }

                        is BookedSlotsResult.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        is BookedSlotsResult.Success -> {
                            if (timeSlotsForGrid.isNotEmpty()) {
                                TimeSlotGrid(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    timeSlots = timeSlotsForGrid,
                                    onTimeSelected = { timeClicked ->
                                        selectedTimeSlot = timeClicked

                                        timeSlotsForGrid = timeSlotsForGrid.map { slot ->
                                            when {
                                                slot.time == timeClicked && slot.status != TimeSlotStatus.Booked ->
                                                    slot.copy(status = TimeSlotStatus.Selected)

                                                slot.status == TimeSlotStatus.Selected && slot.status != TimeSlotStatus.Booked ->
                                                    slot.copy(status = TimeSlotStatus.Available)

                                                else -> slot
                                            }
                                        }
                                    }
                                )
                            } else {
                                Text("No hay horas disponibles para este día.")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val apiDbDateFormatter = remember {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                }
                CustomButton("Coger cita") {
                    if (selectedDate == null) {
                        Toast.makeText(context, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
                        return@CustomButton
                    }
                    if (selectedTimeSlot == null) {
                        Toast.makeText(context, "Selecciona una hora", Toast.LENGTH_SHORT).show()
                        return@CustomButton
                    }

                    val currentUserId = 1

                    val fechaStringParaGuardar = try {
                        apiDbDateFormatter.format(java.util.Date(selectedDate!!))
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error con la fecha seleccionada",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@CustomButton
                    }
                    val horaString = selectedTimeSlot!!

                    val citaParaEnviar = Cita(
                        idCita = null,
                        servicio = Servicio(
                            idServicio = idServicio,
                            nombreServicio = "",
                            textoExplicativo = null,
                            duracion = null,
                            seccion = null
                        ),
                        seccion = Seccion(
                            idSeccion = idSeccion,
                            nombreSeccion = "",
                            imagenSeccion = "",
                            especialista = null
                        ),
                        usuario = Usuario(
                            idUsuario = idUsuario,
                            nombre = "",
                            apellidos = "",
                            email = "",
                            dni = "",
                            nombreUsuario = "",
                            contrasenia = ""
                        ),
                        texto = descriptionText.takeIf { it.isNotBlank() } ?: "Sin descripción",
                        fecha = fechaStringParaGuardar,
                        hora = horaString
                    )

                    scope.launch {
                        isBooking = true
                        bookingError = null
                        try {
                            val citaCreada = apiService.createCita(citaParaEnviar)
                            showSuccessDialog = true
                        } catch (e: Exception) {
                            bookingError = "Error al crear cita: ${e.message}"
                            e.printStackTrace()
                        } finally {
                            isBooking = false
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navigateToBack()
            },
            title = { Text("¡Cita Registrada!") },
            text = { Text("Tu cita ha sido registrada correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false; navigateToBack()
                }) { Text("Aceptar") }
            }
        )
    }
    if (bookingError != null) {
        AlertDialog(
            onDismissRequest = { bookingError = null },
            title = { Text("Error") },
            text = { Text(bookingError ?: "Error desconocido.") },
            confirmButton = { TextButton(onClick = { bookingError = null }) { Text("Aceptar") } }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CitaScreenPreview() {
//    CitaScreen("Podología") { }
//}