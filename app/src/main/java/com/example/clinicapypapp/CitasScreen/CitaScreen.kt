package com.example.clinicapypapp.CitasScreen

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
import com.example.clinicapypapp.components.CustomButton
import com.example.clinicapypapp.components.CustomDescriptionTextField
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.DatePickerField
import com.example.clinicapypapp.components.TimeSlotData
import com.example.clinicapypapp.components.TimeSlotGrid
import com.example.clinicapypapp.components.TimeSlotStatus
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitaScreen(sectionName: String, navigateToBack: () -> Unit) {
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var selectedTimeSlot by rememberSaveable { mutableStateOf<String?>(null) }
    val initialTimeSlots = rememberSaveable {
        mutableStateOf(
            listOf(
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
                    // Pasamos la lista actual del estado
                    timeSlots = initialTimeSlots.value,
                    onTimeSelected = { timeClicked ->
                        // --- Lógica de selección ---
                        // 1. Actualiza la hora seleccionada
                        selectedTimeSlot = timeClicked

                        // 2. Crea una NUEVA lista basada en la selección
                        val updatedList = initialTimeSlots.value.map { slot ->
                            when {
                                // Si esta es la hora que se acaba de clicar -> Selected
                                slot.time == timeClicked -> slot.copy(status = TimeSlotStatus.Selected)
                                // Si esta era la hora previamente seleccionada -> Available de nuevo
                                slot.status == TimeSlotStatus.Selected -> slot.copy(status = TimeSlotStatus.Available)
                                // Para las demás (Available, Taken), se quedan como están
                                else -> slot
                            }
                        }
                        // 3. Actualiza el estado de la lista para que Compose redibuje
                        initialTimeSlots.value = updatedList
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomButton("Coger cita") { }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CitaScreenPreview() {
    CitaScreen("Podología") { }
}