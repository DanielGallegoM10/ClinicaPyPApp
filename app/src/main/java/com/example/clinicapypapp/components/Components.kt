package com.example.clinicapypapp.components

import android.os.Build
import android.util.Log
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
import androidx.compose.material3.SelectableDates
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
import java.util.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Objetos para representar los estados de las horas de las citas
//Clase enum con tres estados posibles
enum class TimeSlotStatus {
    Available, Booked, Selected
}

//objeto para representar una hora de la cita
data class TimeSlotData(
    val time: String,
    val status: TimeSlotStatus
)

//Interfaz para representar el estado de las citas
sealed interface BookedSlotsResult {
    object Idle : BookedSlotsResult
    object Loading : BookedSlotsResult
    data class Success(val bookedSlots: List<String>) : BookedSlotsResult
    data class Error(val message: String) : BookedSlotsResult
}

//Funciones para generar las horas de las citas
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

//Crea una lazy grid con las horas de las citas
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
            val disabledContainerColor = Color.Gray
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

//Objeto que limita las fechas a la hora de ser seleccionadas, tres posibles condiciones:
// -No puede ser en el pasado
// -No puede ser un domingo o un sábado
// -No puede ser a 2 meses vista de la fecha de hoy
@OptIn(ExperimentalMaterial3Api::class)
object LimitedFutureWeekdaysSelectableDates : SelectableDates {

    private val todayStartMillis: Long
    private val twoMonthsLimitMillis: Long
    private val minSelectableYear: Int
    private val maxSelectableYear: Int

    init {
        val todayCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        todayCal.set(Calendar.HOUR_OF_DAY, 0)
        todayCal.set(Calendar.MINUTE, 0)
        todayCal.set(Calendar.SECOND, 0)
        todayCal.set(Calendar.MILLISECOND, 0)
        todayStartMillis = todayCal.timeInMillis
        minSelectableYear = todayCal.get(Calendar.YEAR)

        val limitCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        limitCal.timeInMillis = todayStartMillis
        limitCal.add(Calendar.MONTH, 2) // Añade 2 meses a la fecha de hoy
        twoMonthsLimitMillis = limitCal.timeInMillis
        maxSelectableYear = limitCal.get(Calendar.YEAR)
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = utcTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val currentMillisToCompare = calendar.timeInMillis

        if (currentMillisToCompare < todayStartMillis) {
            return false
        }

        if (currentMillisToCompare > twoMonthsLimitMillis) {
            return false
        }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return false
        }

        return true
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= minSelectableYear && year <= maxSelectableYear
    }
}

//Componente para seleccionar una fecha
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    labelName: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
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
            initialSelectedDateMillis = selectedDateMillis ?: System.currentTimeMillis(),
            selectableDates = LimitedFutureWeekdaysSelectableDates
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

//Componente usado en la descripcion de la necesidad de la cita
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

//Componente de cuadro de texto personalizado
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

//Cuadro de texto de contraseña personalizado
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

//Componente de botón personalizado
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

//Componente de titulo personalizado, con un estilo bonito
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

//Componente de dialogo personalizado
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

//Componente de icono de volver personalizado
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

//funcion que devuelve el id de la imagen correspondiente al nombre de la seccion
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

//Componente de tarjeta de sección personalizado
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

//Componente de lista de secciones personalizado
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


//Componente de icono de menu personalizado
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

//Componente de texto con separador personalizado
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

//Logo de imagen personalizado, utilizado en el login
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

//Componente de tarjeta de servicio personalizado
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

//Componente de dialogo de informacion de servicio personalizado
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

//Componente de lista de servicios
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

//Clase de opcion del menu personalizado
data class DropdownOption(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

//Componente de menu personalizado, implementado al clicar en el icono de usuario
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

//Componente de cita personalizado
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CitaItemView(cita: Cita, onCancelClick: () -> Unit, onDeleteClick: () -> Unit) {
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
                    if (cita.fecha < LocalDate.now().toString()){
                        Text(
                            text = "Borrar Cita",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                onDeleteClick()
                            }
                        )
                    }else {
                        Text(
                            text = "Cancelar Cita",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                onCancelClick()
                            }
                        )
                    }
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

//Componente de tarjeta de quien somos personalizado
@Composable
fun CardQuienSomos(
    nombre: String,
    especialidad: String,
    descripcion: String,
    imagen: Int
) {
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

//Componente de datos de usuario personalizado
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
