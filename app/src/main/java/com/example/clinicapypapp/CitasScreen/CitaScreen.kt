package com.example.clinicapypapp.CitasScreen

import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomButton
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.DatePickerField
import com.example.clinicapypapp.components.TimeSlotData
import com.example.clinicapypapp.components.TimeSlotGrid
import com.example.clinicapypapp.components.TimeSlotStatus
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
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaScreen(idUsuario: Int, idSeccion: Int, idServicio: Int, sectionName: String, navigateToBack: () -> Unit) {
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var selectedTimeSlot by rememberSaveable { mutableStateOf<String?>(null) }
    val initialTimeSlots = rememberSaveable {
        mutableStateOf(
            listOf( //De momento son horas de prueba
                TimeSlotData("09:00", TimeSlotStatus.Available),
                TimeSlotData("09:30", TimeSlotStatus.Available),
                TimeSlotData("10:00", TimeSlotStatus.Taken),
                TimeSlotData("10:30", TimeSlotStatus.Available),
                TimeSlotData("11:00", TimeSlotStatus.Available),
                TimeSlotData("11:30", TimeSlotStatus.Taken),
                TimeSlotData("12:00", TimeSlotStatus.Available),
                TimeSlotData("12:30", TimeSlotStatus.Available),
                TimeSlotData("13:00", TimeSlotStatus.Taken),
                TimeSlotData("13:30", TimeSlotStatus.Available),
                TimeSlotData("14:00", TimeSlotStatus.Available),
                TimeSlotData("14:30", TimeSlotStatus.Taken),

                TimeSlotData("16:00", TimeSlotStatus.Available),
                TimeSlotData("16:30", TimeSlotStatus.Available),
                TimeSlotData("17:00", TimeSlotStatus.Taken),
                TimeSlotData("17:30", TimeSlotStatus.Available),
                TimeSlotData("18:00", TimeSlotStatus.Available),
                TimeSlotData("18:30", TimeSlotStatus.Taken),

            )
        )
    }
    var isBooking by rememberSaveable { mutableStateOf(false) }
    var bookingError by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    val apiService = remember { ApiService(KtorClient.httpClient) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
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
                TimeSlotGrid(
                    modifier = Modifier
                        .fillMaxWidth().weight(1f),
                    timeSlots = initialTimeSlots.value,
                    onTimeSelected = { timeClicked ->
                        selectedTimeSlot = timeClicked

                        val updatedList = initialTimeSlots.value.map { slot ->
                            when {
                                slot.time == timeClicked -> slot.copy(status = TimeSlotStatus.Selected)
                                slot.status == TimeSlotStatus.Selected -> slot.copy(status = TimeSlotStatus.Available)
                                else -> slot
                            }
                        }
                        initialTimeSlots.value = updatedList
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
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

                    val fechaString = dateFormatter.format(Date(selectedDate!!))
                    val horaString = selectedTimeSlot!!

                    val citaParaEnviar = Cita(
                        idCita = null,
                        servicio = Servicio(idServicio = idServicio, nombreServicio = "", textoExplicativo = null, duracion = null, seccion = null),
                        seccion = Seccion(idSeccion = idSeccion, nombreSeccion = "", imagenSeccion = "", especialista = null),
                        usuario = Usuario(idUsuario = idUsuario, nombre = "", apellidos = "", email = "", dni = "", nombreUsuario = "", contrasenia = ""),
                        texto = descriptionText.takeIf { it.isNotBlank() } ?: "Sin descripción",
                        fecha = fechaString,
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