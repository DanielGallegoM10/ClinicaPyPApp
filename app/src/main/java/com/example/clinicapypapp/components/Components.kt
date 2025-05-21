package com.example.clinicapypapp.components

import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.clinicapypapp.R
import com.example.clinicapypapp.data.models.Cita
import com.example.clinicapypapp.data.models.Seccion
import com.example.clinicapypapp.data.models.Servicio
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


enum class TimeSlotStatus {
    Available, Booked, Selected
}

data class TimeSlotData(
    val time: String,
    val status: TimeSlotStatus
)

sealed interface BookedSlotsResult {
    object Idle : BookedSlotsResult
    object Loading : BookedSlotsResult
    data class Success(val bookedSlots: List<String>) : BookedSlotsResult
    data class Error(val message: String) : BookedSlotsResult
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateBaseTimeSlots(
    startTime: String,
    endTime: String,
    intervalMinutes: Long,
    initialStatus: TimeSlotStatus = TimeSlotStatus.Available
): List<TimeSlotData> {
    val slots = mutableListOf<TimeSlotData>()
    try {
        var currentTime = LocalTime.parse(startTime)
        val lastTime = LocalTime.parse(endTime)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        while (currentTime.isBefore(lastTime)) {
            slots.add(TimeSlotData(time = currentTime.format(formatter), status = initialStatus))
            currentTime = currentTime.plusMinutes(intervalMinutes)
        }
    } catch (e: Exception) {
        Log.e("generateBaseTimeSlots", "Error generating time slots: ${e.message}")
    }
    return slots
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateTimeSlots(startTime: String, endTime: String, intervalMinutes: Long): List<String> {
    val slots = mutableListOf<String>()
    try {
        var currentTime = LocalTime.parse(startTime)
        val lastTime = LocalTime.parse(endTime)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        while (currentTime.isBefore(lastTime)) {
            slots.add(currentTime.format(formatter))
            currentTime = currentTime.plusMinutes(intervalMinutes)
        }
    } catch (e: Exception) {
        Log.e("generateTimeSlots", "Error generating time slots: ${e.message}")
    }
    return slots
}

@Composable
fun TimeSlotGrid(
    modifier: Modifier = Modifier,
    timeSlots: List<TimeSlotData>,
    onTimeSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 85.dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(timeSlots, key = { it.time }) { slotData ->

            val isEnabled = slotData.status != TimeSlotStatus.Booked

            val containerColor = when (slotData.status) {
                TimeSlotStatus.Available -> MaterialTheme.colorScheme.surfaceVariant
                TimeSlotStatus.Booked -> Color.Gray
                TimeSlotStatus.Selected -> MaterialTheme.colorScheme.primary
            }
            val contentColor = when (slotData.status) {
                TimeSlotStatus.Available -> MaterialTheme.colorScheme.onSurfaceVariant
                TimeSlotStatus.Booked -> Color.Gray
                TimeSlotStatus.Selected -> MaterialTheme.colorScheme.onPrimary
            }
            val disabledContainerColor =Color.Gray
            val disabledContentColor = Color.Black

            Button(
                onClick = {
                    if (isEnabled) {
                        onTimeSelected(slotData.time)
                    }
                },
                enabled = isEnabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = disabledContainerColor,
                    disabledContentColor = disabledContentColor
                ),
                modifier = Modifier
                    .height(45.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = slotData.time,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeSlotItem(
    timeSlotData: TimeSlotData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColors = when (timeSlotData.status) {
        TimeSlotStatus.Available -> CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        TimeSlotStatus.Booked -> CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        TimeSlotStatus.Selected -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    }

    val cardElevation = if (timeSlotData.status == TimeSlotStatus.Booked) {
        CardDefaults.cardElevation(defaultElevation = 1.dp)
    } else {
        CardDefaults.cardElevation(defaultElevation = 4.dp)
    }

    val border = if (timeSlotData.status == TimeSlotStatus.Available) {
        BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    } else {
        null
    }


    Card(
        modifier = modifier
            .aspectRatio(1.8f)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(8.dp),
        colors = cardColors,
        elevation = cardElevation,
        border = border,
        onClick = onClick,
        enabled = timeSlotData.status != TimeSlotStatus.Booked
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeSlotData.time,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    labelName: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val displayText = selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: ""

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text(text = labelName) },
            placeholder = { Text("Selecciona una fecha") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha"
                )
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                cursorColor = Color.Transparent
            )
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun CustomDescriptionTextField(
    texto: String,
    labelName: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onValueChange,
        label = { Text(text = labelName) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .defaultMinSize(minHeight = 200.dp)
    )
}

@Composable
fun CustomTextField(
    texto: String,
    labelName: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onValueChange,
        label = { Text(text = labelName) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun CustomPassTextField(
    texto: String,
    labelName: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = texto,
        onValueChange = onValueChange,
        label = { Text(text = labelName) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Ver contraseña")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun CustomButton(
    texto: String,
    enabled: Boolean = true,
    accionClick: () -> Unit,
) {
    Button(
        onClick = { accionClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3C98FF),
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .height(45.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
    ) {
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomTitle(texto: String) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .padding(vertical = 20.dp, horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun CustomTitleLuxury(texto: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "GradientAnimation")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientProgress"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF333333),
            Color(0xFF1A1A1A),
            Color(0xFF333333)
        ),
        start = Offset(x = 300f * animatedProgress, y = 0f),
        end = Offset(x = 300f * animatedProgress + 500f, y = 1000f)
    )

    Text(
        text = texto,
        fontSize = 26.sp,
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        color = Color.Transparent,
        letterSpacing = 1.2.sp,
        style = TextStyle(
            brush = brush,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.3f),
                offset = Offset(2f, 2f),
                blurRadius = 6f
            )
        )
    )
}

@Composable
fun CustomAlertDialog(
    titulo: String,
    mensaje: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text(text = titulo, fontWeight = FontWeight.Bold) },
        text = { Text(text = mensaje) },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Aceptar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}

@Composable
fun CustomDialogChangePass(
    usuario: String,
    nuevaContrasena: String,
    onDismiss: () -> Unit,
    onConfirm: (Any?) -> Unit,
    onValueChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text("Cambio de Contraseña", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = usuario,
                    onValueChange = {},
                    label = { Text("Usuario") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                CustomPassTextField(
                    texto = nuevaContrasena,
                    labelName = "Nueva Contraseña",
                    onValueChange = onValueChange
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(nuevaContrasena) },
                enabled = nuevaContrasena.isNotBlank()
            ) {
                Text("Aceptar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}


@Composable
fun CustomBackIcon(navigateToBack: () -> Unit) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Volver",
        modifier = Modifier
            .padding(start = 5.dp)
            .size(30.dp)
            .clickable { navigateToBack() },
        tint = MaterialTheme.colorScheme.onBackground
    )
}


@Composable
fun SearchView(
    busqueda: String,
    onQueryChanged: (String) -> Unit,
    placeholder: String = "Buscar prueba"
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onQueryChanged,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar") },
        trailingIcon = {
            if (busqueda.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar busqueda")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .padding(8.dp)
    )
}

@Composable
fun getDrawableIdFromString(imageIdentifier: String): Int {

    return when (imageIdentifier) {
        "img_med_estetica" -> R.drawable.medicina_estetica
        "img_med_general" -> R.drawable.medicina_general
        "img_podología" -> R.drawable.podologia
        else -> {
            R.drawable.ic_launcher_background
        }
    }
}

@Composable
fun SectionCard(
    section: Seccion,
    onItemSelected: (Seccion) -> Unit,
    cardColor: Color = Color(0xFFFCE4EC),
) {
    Card(
        border = BorderStroke(1.5.dp, color = Color.DarkGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected(section) }
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {

            val drawableId = getDrawableIdFromString(section.imagenSeccion)

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = "Section Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = section.nombreSeccion,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = section.especialista?.let { "${it.nombreEspecialista} ${it.apellidosEspecialista}" }
                    ?: "Especialista no asignado",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun SectionList(sections: List<Seccion>, onItemSelected: (Seccion) -> Unit) {
    val visible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = sections,
            key = { _, section -> section.idSeccion ?: section.nombreSeccion }
        ) { index, section ->
            AnimatedVisibility(
                visible = visible.value,
                enter = scaleIn()
            ) {
                SectionCard(section = section, onItemSelected = onItemSelected)
            }
        }
    }
}


@Composable
fun IconUserMenu(
    onNavigateToMisDatos: () -> Unit,
    onNavigateToMisCitas: () -> Unit,
    onNavigateToQuienSomos: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Menú de Usuario",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }
        UserDropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            onMisDatosClick = {
                onNavigateToMisDatos()
                menuExpanded = false
            },
            onMisCitasClick = {
                onNavigateToMisCitas()
                menuExpanded = false
            },
            onQuienSomosClick = {
                onNavigateToQuienSomos()
                menuExpanded = false
            },
            onCerrarSesionClick = {
                onCerrarSesion()
                menuExpanded = false
            }
        )
    }
}

@Composable
fun TextWithDivider(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    dividerColor: Color = Color.Gray,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    dividerThickness: Dp = 2.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val animatedProgress by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = dividerColor.copy(alpha = animatedProgress),
            thickness = dividerThickness
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = TextAlign.Center,
            color = textColor,
            style = textStyle
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = dividerColor.copy(alpha = animatedProgress),
            thickness = dividerThickness
        )
    }
}

@Composable
fun LogoImage(
    image: Int,
    width: Dp = 270.dp,
    height: Dp = 120.dp,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = Color.Gray,
    borderWidth: Dp = 2.dp
) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "Logo Image",
        modifier = Modifier
            .size(width, height),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ServiceCard(
    service: Servicio,
    onItemSelected: (Servicio) -> Unit,
    cardColor: Color = Color(0xFFFCE4EC),
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Card(
        border = BorderStroke(1.5.dp, color = Color.DarkGray),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onItemSelected(service) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = service.nombreServicio,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info Icon",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showDialog = true },
                    tint = Color.Black
                )
            }
        }
    }
    if (showDialog) {
        DialogInfoService(
            service = service,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false })
    }
}

@Composable
fun DialogInfoService(
    service: Servicio,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                service.textoExplicativo?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = service.duracion.toString() + " minutos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Aceptar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}

@Composable
fun ServiceList(services: List<Servicio>, onItemSelected: (Servicio) -> Unit) {

    val visible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 20.dp)
    ) {
        itemsIndexed(services) { index, service ->
            AnimatedVisibility(
                visible = visible.value,
                enter = scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(
                        durationMillis = 500,
                        delayMillis = index * 100
                    )
                )
            ) {
                ServiceCard(service = service, onItemSelected = onItemSelected)
            }
        }
    }
}

data class DropdownOption(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onMisDatosClick: () -> Unit,
    onMisCitasClick: () -> Unit,
    onQuienSomosClick: () -> Unit,
    onCerrarSesionClick: () -> Unit,
) {

    val customMenuBackgroundColor = Color(0xFFFCE4EC)

    val menuOptions = listOf(
        DropdownOption("Mis Datos", Icons.Filled.AccountCircle, onMisDatosClick),
        DropdownOption("Mis Citas", Icons.Filled.CalendarToday, onMisCitasClick),
        DropdownOption("¿Quien Somos?", Icons.Filled.QuestionMark, onQuienSomosClick),
        DropdownOption("Cerrar Sesión", Icons.AutoMirrored.Filled.ExitToApp, onCerrarSesionClick)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(customMenuBackgroundColor),
    ) {
        menuOptions.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.text) },
                onClick = {
                    option.onClick()
                    onDismissRequest()
                },
                leadingIcon = {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = option.text
                    )
                }
            )
        }
    }
}

@Composable
fun CitaItemView(cita: Cita, onCancelClick: () -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
        border = BorderStroke(1.5.dp, color = Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Sección: ${cita.seccion?.nombreSeccion}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Servicio: ${cita.servicio?.nombreServicio}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Especialista: ${cita.seccion?.especialista?.nombreEspecialista} ${cita.seccion?.especialista?.apellidosEspecialista}",
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column {
                        if (!expanded) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility,
                                contentDescription = "Info Icon",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        expanded = true
                                    },
                                tint = Color.Black
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.VisibilityOff,
                                contentDescription = "Info Icon",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        expanded = false
                                    },
                                tint = Color.Black
                            )
                            Text(
                                text = cita.texto,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = "Cancel Icon",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onCancelClick()
                            },
                        tint = Color.Black
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Fecha: ${cita.fecha}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Hora: ${cita.hora}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun CardQuienSomos(
    nombre: String,
    especialidad: String,
    descripcion: String,
    imagen: Int
){
    Card(
        border = BorderStroke(1.5.dp, color = Color.DarkGray),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(

        ) {
            Image(
                painter = painterResource(id = imagen),
                contentDescription = "Imagen de perfil",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = especialidad,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = descripcion,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }

}

@Composable
fun UserDataItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
fun CardQuienSomos(
    nombre: String,
    especialidad: String,
    descripcion: String,
    @DrawableRes imagen: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = imagen),
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = especialidad,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ComponentsPreview() {

}