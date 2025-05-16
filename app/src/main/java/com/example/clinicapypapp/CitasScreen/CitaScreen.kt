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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun CitaScreen(idUsuario: Int, idSeccion: Int, idServicio: Int, idEspecialista: Int, sectionName: String, navigateToBack: () -> Unit) {
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


    LaunchedEffect(key1 = selectedDate, key2 = idEspecialista) {
        if (selectedDate != null && idEspecialista != null) {
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

                    val horasOcupadasSet = horasOcupadasList.toSet() // Convertir a Set para búsqueda rápida O(1)
                    // Genera la lista base (ej: 9:00 a 18:00, cada 30 min)
                    val baseSlots = generateBaseTimeSlots("09:00", "18:00", 30)
                    // Mapea la lista base, ajustando el estado si la hora está ocupada
                    timeSlotsForGrid = baseSlots.map { slotData ->
                        if (horasOcupadasSet.contains(slotData.time)) {
                            // Si la hora está en la lista de ocupadas, marcar como Booked
                            slotData.copy(status = TimeSlotStatus.Booked)
                        } else {
                            // Si no está ocupada, mantener como Available (o Selected si ya estaba seleccionada)
                            // Por ahora, simplemente la dejamos Available. La selección se maneja aparte.
                            slotData.copy(status = TimeSlotStatus.Available)
                        }
                    }

                } catch (e: CancellationException) {
                    bookedSlotsState = BookedSlotsResult.Idle

                } catch (e: Exception) {
                    bookedSlotsState = BookedSlotsResult.Error(e.message ?: "Error al obtener disponibilidad")
                }
            } else {
                bookedSlotsState = BookedSlotsResult.Error("Error interno con la fecha seleccionada.")
            }
        } else {
            bookedSlotsState = BookedSlotsResult.Idle
        }
    }

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
                Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTitleLuxury(sectionName)
                Spacer(modifier = Modifier.height(16.dp))
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
                        .padding(vertical = 8.dp), // Espacio vertical
                    contentAlignment = Alignment.Center
                ) {
                    // Decidimos qué mostrar según el estado de bookedSlotsState
                    when (val state = bookedSlotsState) { // Usamos 'state' para smart casting

                        is BookedSlotsResult.Idle -> {
                            // Si hay fecha seleccionada, indicamos que seleccione hora.
                            // Si no hay fecha, el LaunchedEffect ya puso Idle, no mostramos nada extra aquí.
                            if (selectedDate != null) {
                                Text("Selecciona una hora disponible.")
                            } else {
                                // Opcional: Podrías poner un mensaje si quieres, pero usualmente no se muestra nada
                                // Text("Selecciona una fecha.")
                            }
                        }

                        is BookedSlotsResult.Loading -> {
                            // Mostrar indicador de progreso mientras carga
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Consultando disponibilidad...")
                            }
                        }

                        is BookedSlotsResult.Error -> {
                            // Mostrar mensaje de error
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp) // Padding por si el mensaje es largo
                            )
                        }

                        is BookedSlotsResult.Success -> {
                            if (timeSlotsForGrid.isNotEmpty()) {
                                TimeSlotGrid(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        // .weight(1f) // Quita el weight si el Box no está dentro de una Column con peso
                                        .padding(bottom = 8.dp), // Añade padding si es necesario
                                    timeSlots = timeSlotsForGrid, // <-- Pasa la lista actualizada
                                    onTimeSelected = { timeClicked ->
                                        // --- Lógica de selección (siguiente sub-paso) ---
                                        // Actualizar selectedTimeSlot y el estado visual de timeSlotsForGrid
                                        // (Lo detallamos en 5.5)
                                        selectedTimeSlot = timeClicked // Guarda la hora "HH:mm"

                                        // Actualiza visualmente la lista para reflejar la selección
                                        timeSlotsForGrid = timeSlotsForGrid.map { slot ->
                                            when {
                                                // Marcar el clickeado como Selected (si no está Booked)
                                                slot.time == timeClicked && slot.status != TimeSlotStatus.Booked ->
                                                    slot.copy(status = TimeSlotStatus.Selected)
                                                // Desmarcar el previamente seleccionado (si no está Booked)
                                                slot.status == TimeSlotStatus.Selected && slot.status != TimeSlotStatus.Booked ->
                                                    slot.copy(status = TimeSlotStatus.Available)
                                                // Mantener los demás como están (Available o Booked)
                                                else -> slot
                                            }
                                        }
                                    }
                                )
                            } else {
                                // Mostrar mensaje si, después de cargar, no hay slots (ej: día festivo)
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
                        Toast.makeText(context, "Error con la fecha seleccionada", Toast.LENGTH_SHORT).show()
                        return@CustomButton
                    }
                    val horaString = selectedTimeSlot!!

                    val citaParaEnviar = Cita(
                        idCita = null,
                        servicio = Servicio(idServicio = idServicio, nombreServicio = "", textoExplicativo = null, duracion = null, seccion = null),
                        seccion = Seccion(idSeccion = idSeccion, nombreSeccion = "", imagenSeccion = "", especialista = null),
                        usuario = Usuario(idUsuario = idUsuario, nombre = "", apellidos = "", email = "", dni = "", nombreUsuario = "", contrasenia = ""),
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
            confirmButton = { TextButton(onClick = { showSuccessDialog = false; navigateToBack() }) { Text("Aceptar") } }
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