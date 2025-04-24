package com.example.clinicapypapp.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import com.example.clinicapypapp.data.api.*
import kotlinx.coroutines.launch

import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomAlertDialog
import com.example.clinicapypapp.components.CustomButton
import com.example.clinicapypapp.components.CustomDialogChangePass
import com.example.clinicapypapp.components.CustomPassTextField
import com.example.clinicapypapp.components.CustomTextField
import com.example.clinicapypapp.components.LogoImage



@Composable
fun LoginScreen(navigateToMainScreen: () -> Unit) {
    var textoUsuario by rememberSaveable { mutableStateOf("") }
    var textoPass by rememberSaveable { mutableStateOf("") }
    var cambiaContrasena by rememberSaveable { mutableStateOf(false) }
    var incorrectoCambioPass by rememberSaveable { mutableStateOf(false) }
    var nuevaContraseña by rememberSaveable { mutableStateOf("") }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val apiService = remember { ApiService(KtorClient.httpClient) }

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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            LogoImage(R.drawable.logo_clinica, width = 350.dp, height = 150.dp)
            Spacer(modifier = Modifier.weight(0.5f))
            CustomTextField(textoUsuario, "Introduzca su usuario") { textoUsuario = it }
            Spacer(modifier = Modifier.weight(1f))
            CustomPassTextField(textoPass, "Introduzca su contraseña") { textoPass = it }
            Text("¿Ha olvidado su contraseña?", fontSize = 16.sp, modifier = Modifier.clickable {
                if (textoUsuario.isEmpty()){
                    incorrectoCambioPass = true
                }else{
                    cambiaContrasena = true }}, color = Color.Blue)
            Spacer(modifier = Modifier.weight(1f))
            CustomButton("Iniciar sesión", enabled = !isLoading) {
                navigateToMainScreen()
            }
            Spacer(modifier = Modifier.weight(1f))

            if (incorrectoCambioPass){
                CustomAlertDialog("Error de Cambio de Contraseña", "Debe introducir un usuario para cambiar la contraseña", {incorrectoCambioPass = false}, {incorrectoCambioPass = false})
            }

            if (cambiaContrasena){
                CustomDialogChangePass(textoUsuario, nuevaContraseña, {cambiaContrasena = false}, { cambiaContrasena = false }, { nuevaContraseña = it })
            }

        }
    }
}