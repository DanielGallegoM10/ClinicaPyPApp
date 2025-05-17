package com.example.clinicapypapp.QuienSomosScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CardQuienSomos
import com.example.clinicapypapp.components.CustomBackIcon
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuienSomosScreen(
    navigateToBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¿Quiénes somos?", color = Color.Black) },
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Somos una clínica dedicada a la salud y bienestar de nuestros pacientes.\n" +
                        "Ofrecemos servicios médicos de alta calidad y atención personalizada.\n" +
                        "Nuestro equipo de profesionales altamente capacitados está comprometido con tu salud.\n",
                    modifier = Modifier.padding(6.dp))

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


                Text("Puedes encontrarnos en Calle Falsa Nº22. \n" +
                        "Teléfono: 999999999 \n" +
                        "Correo: clinica@gmail.com", modifier = Modifier.padding(6.dp))

                Image(
                    painter = painterResource(R.drawable.mapa),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                        .height(220.dp)
                        .padding(6.dp)
                )

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Prewiew(){
    QuienSomosScreen {  }
}