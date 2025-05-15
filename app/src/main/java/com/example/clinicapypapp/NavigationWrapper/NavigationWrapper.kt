package com.example.clinicapypapp.NavigationWrapper

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.clinicapypapp.CitasScreen.CitaScreen
import com.example.clinicapypapp.CitasScreen.CitasUsuarioScreen
import com.example.clinicapypapp.LoginScreen.LoginScreen
import com.example.clinicapypapp.MainScreen.MainScreen
import com.example.clinicapypapp.ServicesScreens.ServicesScreen
import com.example.clinicapypapp.entities.Section

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

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

            }, {

            }, {

            }

            )
        }

        composable<ServicesDest> { backStackEntry ->
            val section: ServicesDest = backStackEntry.toRoute()
            ServicesScreen(section.idUsuario, section.idSeccion, section.sectionName,
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
                })
        }

        composable<CitasUsuarioDest> { backStackEntry ->
            val section: CitasUsuarioDest = backStackEntry.toRoute()
            CitasUsuarioScreen(section.idUsuario, {
                navController.popBackStack()
            }, {


            })
        }
    }
}