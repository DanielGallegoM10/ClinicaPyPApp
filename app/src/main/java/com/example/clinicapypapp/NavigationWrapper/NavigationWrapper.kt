package com.example.clinicapypapp.NavigationWrapper

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.clinicapypapp.CitasScreen.CitaScreen
import com.example.clinicapypapp.CitasScreen.CitasUsuarioScreen
import com.example.clinicapypapp.LoginScreen.LoginScreen
import com.example.clinicapypapp.MainScreen.MainScreen
import com.example.clinicapypapp.MisDatosScreen.MisDatosScreen
import com.example.clinicapypapp.QuienSomosScreen.QuienSomosScreen
import com.example.clinicapypapp.ServicesScreens.ServicesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper() {
    //Instancio el controlador de navegación
    val navController = rememberNavController()

    //Declaro las rutas de navegación y ruta de inicio
    NavHost(navController = navController, startDestination = LoginDest) {
        composable<LoginDest> {
            LoginScreen { idUsuario ->
                navController.navigate(MainDest(idUsuario))
            }
        }

        composable<MainDest> { backStackEntry ->
            val mainDest: MainDest = backStackEntry.toRoute()
            MainScreen(mainDest.idUsuario, { destinoServices ->
                navController.navigate(destinoServices)
            }, {
                navController.navigate(CitasUsuarioDest(mainDest.idUsuario))
            }, {
                navController.navigate(MisDatosDest(mainDest.idUsuario))
            }, {
                navController.navigate(QuienSomosDest(mainDest.idUsuario))
            }, {
                navController.navigate(LoginDest){
                    popUpTo(LoginDest){
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            )
        }

        composable<ServicesDest> { backStackEntry ->
            val section: ServicesDest = backStackEntry.toRoute()
            ServicesScreen(
                section.idUsuario, section.idSeccion, section.sectionName,
                { navController.popBackStack() },
                { servicioSeleccionado ->
                    val destinoCita = CitaDest(
                        section.idUsuario,
                        section.idSeccion,
                        servicioSeleccionado.idServicio ?: -1,
                        section.idEspecialista,
                        section.sectionName
                    )
                    navController.navigate(destinoCita)
                },
                {
                    navController.navigate(MainDest(section.idUsuario))
                },
                {
                    navController.navigate(CitasUsuarioDest(section.idUsuario))
                },
                {
                    navController.navigate(MisDatosDest(section.idUsuario))
                }, {
                    navController.navigate(QuienSomosDest(section.idUsuario))
                })

        }

        composable<CitaDest> { backStackEntry ->
            val section: CitaDest = backStackEntry.toRoute()
            CitaScreen(
                section.idUsuario,
                section.idSeccion,
                section.idServicio,
                section.idEspecialista,
                section.sectionName,
                {
                    navController.popBackStack()
                },
                {
                    navController.navigate(MainDest(section.idUsuario))
                },
                {
                    navController.navigate(CitasUsuarioDest(section.idUsuario))
                },
                {
                    navController.navigate(MisDatosDest(section.idUsuario))
                }, {
                    navController.navigate(QuienSomosDest(section.idUsuario))
                }
            )
        }

        composable<CitasUsuarioDest> { backStackEntry ->
            val section: CitasUsuarioDest = backStackEntry.toRoute()
            CitasUsuarioScreen(section.idUsuario, {
                navController.popBackStack()
            }, {
                navController.navigate(MainDest(section.idUsuario))
            }, {
                navController.navigate(MisDatosDest(section.idUsuario))
            }, {
                navController.navigate(QuienSomosDest(section.idUsuario))
            })
        }

        composable<QuienSomosDest> { backStackEntry ->
            val obj: QuienSomosDest = backStackEntry.toRoute()
            QuienSomosScreen(idUsuario = obj.idUsuario, {
                navController.popBackStack()
            }, {
                navController.navigate(CitasUsuarioDest(idUsuario = obj.idUsuario))
            }, {
                navController.navigate(MisDatosDest(idUsuario = obj.idUsuario))
            }, {
                navController.navigate(MainDest(idUsuario = obj.idUsuario))
            })
        }

        composable<MisDatosDest> { backStackEntry ->
            val datos: MisDatosDest = backStackEntry.toRoute()
            MisDatosScreen(
                datos.idUsuario, {
                navController.popBackStack()
            },
                {
                    navController.navigate(CitasUsuarioDest(datos.idUsuario))
                },
                {
                    navController.navigate(MainDest(datos.idUsuario))
                }, {
                    navController.navigate(QuienSomosDest(datos.idUsuario))
                })
        }
    }
}