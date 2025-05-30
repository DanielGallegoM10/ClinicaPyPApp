package com.example.clinicapypapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.clinicapypapp.MainScreen.MainScreen
import com.example.clinicapypapp.NavigationWrapper.NavigationWrapper
import com.example.clinicapypapp.ui.theme.ClinicaPyPAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClinicaPyPAppTheme {
                NavigationWrapper() //Aqui simplemente llamamos a la funcion que se encarga de la navegación
            }
        }
    }
}