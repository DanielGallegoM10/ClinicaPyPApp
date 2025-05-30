package com.example.clinicapypapp.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import com.example.clinicapypapp.data.api.*
import kotlinx.coroutines.launch

import com.example.clinicapypapp.R
import com.example.clinicapypapp.components.CustomAlertDialog
import com.example.clinicapypapp.components.CustomButton
import com.example.clinicapypapp.components.CustomPassTextField
import com.example.clinicapypapp.components.CustomTextField
import com.example.clinicapypapp.components.LogoImage



@Composable
fun LoginScreen(navigateToMainScreen: (Int) -> Unit) {
    //estados de texto de usuario y contraseña
    var textoUsuario by rememberSaveable { mutableStateOf("") }
    var textoPass by rememberSaveable { mutableStateOf("") }

    //declaro los estados de carga y error
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope() //declaro un scope para las corrutinas
    val apiService = remember { ApiService(KtorClient.httpClient) } //objeto para llamar a las funciones API

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
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            LogoImage(R.drawable.logo_clinica, width = 350.dp, height = 150.dp)
            Spacer(modifier = Modifier.weight(0.5f))
            CustomTextField(textoUsuario, "Introduzca su usuario") { textoUsuario = it }
            Spacer(modifier = Modifier.weight(1f))
            CustomPassTextField(textoPass, "Introduzca su contraseña") { textoPass = it }

            if (loginError != null) {
                CustomAlertDialog(
                    titulo = "Error de inicio de sesión",
                    mensaje = loginError!!,
                    onDismiss = { loginError = null },
                    onConfirm = { loginError = null }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            CustomButton("Iniciar sesión", enabled = !isLoading) {
                // Realizar la acción de inicio de sesión, comprobando los campos en la API
                scope.launch {
                    isLoading = true
                    loginError = null

                    try {
                        val todosUsuarios = apiService.getAllUsuarios()
                        val usuarioEncontrado = todosUsuarios.find {
                            it.nombreUsuario == textoUsuario && it.contrasenia == textoPass
                        }

                        if (usuarioEncontrado != null) {
                            navigateToMainScreen(usuarioEncontrado.idUsuario ?: -1)
                        } else {
                            loginError = "Usuario o contraseña incorrectos."
                        }

                    } catch (e: Exception) {
                         loginError = "Error al conectar, no se ha podido conectar con la API"
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}