package com.example.clinicapypapp.QuienSomosScreen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CardQuienSomos
import com.example.clinicapypapp.components.CustomBackIcon
import com.example.clinicapypapp.components.CustomTitleLuxury

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienSomosScreen(
    navigateToBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomTitleLuxury("¿Quiénes somos?") },
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
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Somos una clínica dedicada a la salud y bienestar de nuestros pacientes. " +
                            "Ofrecemos servicios médicos de alta calidad y atención personalizada. " +
                            "Nuestro equipo de profesionales altamente capacitados está comprometido con tu salud.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                Text(
                    text = "Nuestro Equipo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                CardQuienSomos(
                    nombre = "Carmen López Martínez",
                    especialidad = "Medicina General y Medicina Estética",
                    descripcion = "La Dra. Carmen López Martínez es una experta en medicina general y medicina estética. Ha trabajado durante más de 15 años en el campo médico, especializándose en el cuidado integral de la salud y la mejora de la apariencia física de sus pacientes.",
                    imagen = R.drawable.carmen
                )

                CardQuienSomos(
                    nombre = "Lucía García Pérez",
                    especialidad = "Podología",
                    descripcion = "La Dra. Lucía García Pérez es una reconocida podóloga con más de 10 años de experiencia en el cuidado de los pies. Su enfoque personalizado y su dedicación al bienestar de sus pacientes la convierten en una profesional altamente respetada en el campo de la podología.",
                    imagen = R.drawable.lucia
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )

                Text(
                    text = "Encuéntranos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ContactInfoItem(
                            icon = Icons.Filled.LocationOn,
                            text = "Puedes encontrarnos en Calle Falsa Nº22."
                        )
                        ContactInfoItem(
                            icon = Icons.Filled.Phone,
                            text = "Teléfono: 999999999"
                        )
                        ContactInfoItem(
                            icon = Icons.Filled.Email,
                            text = "Correo: clinica@gmail.com"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Image(
                    painter = painterResource(R.drawable.mapa),
                    contentDescription = "Mapa de ubicación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.large)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), MaterialTheme.shapes.large)
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ContactInfoItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}