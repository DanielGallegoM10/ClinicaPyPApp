package com.example.clinicapypapp.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import com.example.clinicapypapp.entities.Section
import com.example.clinicapypapp.entities.Service
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale




// --- Definiciones de TimeSlotStatus y TimeSlotData (como arriba) ---
enum class TimeSlotStatus {
    Available, Taken, Selected
}
data class TimeSlotData(
    val time: String,
    val status: TimeSlotStatus
)



@Composable
fun TimeSlotGrid(
    timeSlots: List<TimeSlotData>,
    columns: Int = 3, // Número de columnas en la cuadrícula
    onTimeSelected: (String) -> Unit, // Dejamos la lambda aunque no la usemos aún
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp), // Espacio vertical entre filas
        horizontalArrangement = Arrangement.spacedBy(10.dp) // Espacio horizontal entre columnas
    ) {
        items(timeSlots) { timeSlotData ->
            TimeSlotItem(
                timeSlotData = timeSlotData,
                onClick = {
                    if (timeSlotData.status == TimeSlotStatus.Available) {
                    onTimeSelected(timeSlotData.time)
                } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Necesario para el onClick de Card
@Composable
private fun TimeSlotItem(
    timeSlotData: TimeSlotData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColors = when (timeSlotData.status) {
        TimeSlotStatus.Available -> CardDefaults.cardColors(
            containerColor = Color.White, // Disponible: Fondo blanco
            contentColor = MaterialTheme.colorScheme.onSurface // Texto oscuro
        )
        TimeSlotStatus.Taken -> CardDefaults.cardColors(
            containerColor = Color.LightGray.copy(alpha = 0.6f), // Ocupada: Fondo gris claro semitransparente
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Texto grisáceo
        )
        TimeSlotStatus.Selected -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary, // Seleccionada: Fondo azul (color primario del tema)
            contentColor = MaterialTheme.colorScheme.onPrimary // Texto blanco (o el color sobre primario)
        )
    }

    val cardElevation = if (timeSlotData.status == TimeSlotStatus.Taken) {
        CardDefaults.cardElevation(defaultElevation = 1.dp) // Menos elevación si está ocupada
    } else {
        CardDefaults.cardElevation(defaultElevation = 4.dp)
    }

    val border = if (timeSlotData.status == TimeSlotStatus.Available) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) // Borde sutil si está disponible
    } else {
        null
    }


    Card(
        modifier = modifier
            .aspectRatio(1.8f) // Proporción para que sea más ancho que alto
            .height(IntrinsicSize.Min), // Ajusta la altura al contenido (puede necesitar ajustes)
        shape = RoundedCornerShape(8.dp),
        colors = cardColors,
        elevation = cardElevation,
        border = border,
        onClick = onClick, // Asociamos el click (aunque la lambda esté vacía ahora)
        enabled = timeSlotData.status != TimeSlotStatus.Taken // La tarjeta no es interactiva si está ocupada
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeSlotData.time,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp, // Tamaño de fuente ajustado
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

    // ---- Contenedor Box que captura el clic ----
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Aplicamos el clickable al Box
            .clickable { showDialog = true }
    ) {
        // ---- TextField (ahora deshabilitado para interacción directa) ----
        OutlinedTextField(
            value = displayText,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Padding aplicado aquí
            label = { Text(text = labelName) },
            placeholder = { Text("Selecciona una fecha") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha"
                )
            },
            enabled = false, // <--- Deshabilitamos el TextField
            colors = TextFieldDefaults.colors(
                // --- Colores para cuando está DESHABILITADO ---
                // Queremos que parezca habilitado visualmente
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline, // Borde gris normal
                // --- Colores 'focused' y 'unfocused' que ya no se usarán pero los dejamos ---
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                cursorColor = Color.Transparent // No debería aparecer cursor
            )
        )
    } // Fin del Box

    // ---- El DatePickerDialog (sin cambios) ----
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
        // Opcional: Puedes añadir un número máximo de líneas si quieres limitarlo
        // maxLines = 5
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
                onClick = { onConfirm( nuevaContrasena ) },
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

fun getSections(): List<Section> {
    return listOf(
        Section(R.drawable.medicina_estetica, "Medicina Estética", "Carmen López Martínez"),
        Section(R.drawable.medicina_general, "Medicina General", "Carmen López Martínez"),
        Section(R.drawable.podologia, "Podología", "Lucia García Pérez")
    )
}

@Composable
fun SectionCard(
    section: Section,
    onItemSelected: (Section) -> Unit,
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
            Image(
                painter = painterResource(id = section.image),
                contentDescription = "Section Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = section.sectionName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = section.specialist,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
fun SectionList(onItemSelected: (Section) -> Unit) {
    val sections = getSections()
    val visible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        itemsIndexed(sections) { index, section ->
            AnimatedVisibility(
                visible = visible.value,
                enter = scaleIn(
                    initialScale = 0.8f, // Escala inicial
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = index * 100
                    ) // Retraso para cada elemento
                )
            ) {
                SectionCard(section = section, onItemSelected = onItemSelected)
            }
        }
    }
}


@Composable
fun IconUser(
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    iconColor: Color = Color.Black,
    borderColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
) {
    Box(
        modifier = modifier
            .padding(start = 5.dp)
            .size(iconSize + 10.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { TODO() }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = "User Icon",
            modifier = Modifier.size(iconSize),
            tint = iconColor
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
        // Animación de entrada para el divisor
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
//            .clip(RoundedCornerShape(cornerRadius)),
//            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ServiceCard(
    service: Service,
    onItemSelected: (Service) -> Unit,
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
            .height(100.dp)
            .clickable { onItemSelected(service) }
    ) {
        Column(
            Modifier.fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = service.serviceName,
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
        DialogInfoService(service = service, onDismiss = { showDialog = false }, onConfirm = { showDialog = false })
    }
}

@Composable
fun DialogInfoService(
    service: Service,
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
                Text(
                    text = service.splainText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = service.time.toString() + " minutos",
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
fun ServiceList(sectionName: String, onItemSelected: (Service) -> Unit) {
    val services = getServices(sectionName)
    val visible = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible.value = true
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize().padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 20.dp)
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

fun getServices(sectionName: String): List<Service> {
    return when (sectionName) {
        "Medicina Estética" -> listOf(
            Service(
                "Primera Consulta",
                "Consulta inicial para evaluar el estado de la piel y determinar el tratamiento adecuado.",
                60
            ),
            Service(
                "Consulta Sucesiva",
                "Consulta de seguimiento para evaluar el progreso del tratamiento.",
                60
            )
        )

        "Medicina General" -> listOf(
            Service(
                "Primera Consulta",
                "Consulta inicial para evaluar el estado de salud y determinar el tratamiento adecuado.",
                60
            ),
            Service(
                "Consulta Sucesiva",
                "Consulta de seguimiento para evaluar el progreso del tratamiento.",
                60
            )
        )

        "Podología" -> listOf(
            Service("Quiropodía", "Tratamiento de uñas y hongos en los pies.", 60),
            Service("Estudio Biomecánico", "Evaluación del movimiento y postura del pie.", 60),
            Service("Pie Diabético", "Tratamiento especializado para pacientes con diabetes.", 60),
            Service("Reconstrucción Ungueal", "Tratamiento para reconstruir uñas dañadas.", 60),
            Service(
                "Revisión",
                "Consulta de seguimiento para evaluar el progreso del tratamiento.",
                60
            ),
            Service("Podología Pediatrica", "Tratamiento especializado para niños.", 60)
        )

        else -> listOf()
    }
}

@Composable
fun ComponentsPreview() {

}