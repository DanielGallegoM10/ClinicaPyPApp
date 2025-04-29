package com.example.clinicapypapp.MainScreen

import android.annotation.SuppressLint
import android.graphics.RenderEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomTitleLuxury
import com.example.clinicapypapp.components.IconUser
import com.example.clinicapypapp.components.SectionList
import com.example.clinicapypapp.components.TextWithDivider

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun MainScreen( navigateToServices: (String) -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                },
                actions = {
                    IconUser()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.fondo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.4f,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomTitleLuxury("Bienvenido a Clinica PyP")
                Spacer(modifier = Modifier.weight(0.5f))
                TextWithDivider("Selecciona una sección")
                Spacer(modifier = Modifier.weight(0.5f))
                SectionList{ navigateToServices(it.sectionName) }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme { // Asegúrate de que la preview tenga un tema
        MainScreen(navigateToServices = {})
    }
}
