package com.example.clinicapypapp.LoginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
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
fun LoginScreen(navigateToMainScreen: (Int) -> Unit) {
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

            if (loginError != null) {
                CustomAlertDialog(
                    titulo = "Error de inicio de sesión",
                    mensaje = loginError!!,
                    onDismiss = { loginError = null },
                    onConfirm = { loginError = null }
                )
            }

            Text("¿Ha olvidado su contraseña?", fontSize = 16.sp, modifier = Modifier.clickable {
                if (textoUsuario.isEmpty()){
                    incorrectoCambioPass = true
                }else{
                    cambiaContrasena = true }}, color = Color.Blue)
            Spacer(modifier = Modifier.weight(1f))

            CustomButton("Iniciar sesión", enabled = !isLoading) { // <<== Modifica el bloque onClick
                // === Lógica de llamada a la API dentro de una Coroutine ===
                // Lanzar una coroutine cuando se presiona el botón
                scope.launch { // <<== Lanza una coroutine
                    isLoading = true // <<== Empezamos a cargar (actualiza el estado isLoading)
                    loginError = null // <<== Limpiamos errores anteriores (actualiza el estado loginError)

                    try {
                        // 1. Obtenemos TODOS los usuarios de la API usando la instancia de apiService del Paso 12a
                        val todosUsuarios = apiService.getAllUsuarios() // <<== Hacemos la llamada API (es suspendida)

                        // 2. Buscamos localmente si existe un usuario con el nombre y contraseña ingresados
                        // Usamos textoUsuario y textoPass que son los estados de los TextFields
                        val usuarioEncontrado = todosUsuarios.find {
                            it.nombreUsuario == textoUsuario && it.contrasenia == textoPass
                        }

                        // 3. Decidimos si navegar o mostrar error basándonos en el resultado de la búsqueda local
                        if (usuarioEncontrado != null) {
                            // Si encontramos un usuario, el login es "exitoso" (según esta lógica temporal)
                            navigateToMainScreen(usuarioEncontrado.idUsuario ?: -1) // <<== Llamamos a la lambda de navegación que viene del NavigationWrapper
                        } else {
                            // Si no se encuentra, mostramos un error (actualiza el estado loginError)
                            loginError = "Usuario o contraseña incorrectos."
                        }

                    } catch (e: Exception) {
                        // === Manejo de errores de la llamada API ===
                        // Si ocurre un error durante la llamada API (red, servidor, JSON inválido, etc.)
                        loginError = "Error al conectar: ${e.message}" // <<== Mostramos el mensaje de error de la excepción (actualiza loginError)
                        e.printStackTrace() // Imprime el stack trace en Logcat para depurar el error (Ctrl+Alt+2 en Android Studio)
                    } finally {
                        // === Bloque finally (se ejecuta siempre) ===
                        // Esto se ejecuta al finalizar la coroutine, tanto si hubo éxito como si hubo error.
                        isLoading = false // <<== Ocultamos el indicador de carga (actualiza isLoading)
                    }
                    // === Fin Lógica de llamada a la API ===
                } // <<== Cierre de scope.launch
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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