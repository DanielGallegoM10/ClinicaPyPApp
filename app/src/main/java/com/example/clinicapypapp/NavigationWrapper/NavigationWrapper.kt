package com.example.clinicapypapp.NavigationWrapper

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.clinicapypapp.CitasScreen.CitaScreen
import com.example.clinicapypapp.LoginScreen.LoginScreen
import com.example.clinicapypapp.MainScreen.MainScreen
import com.example.clinicapypapp.ServicesScreens.ServicesScreen
import com.example.clinicapypapp.entities.Section

@Composable
fun NavigationWrapper(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginDest){
        composable<LoginDest>{
            LoginScreen{
                navController.navigate(MainDest)
            }
        }

        composable<MainDest>{
            MainScreen { sectionName -> navController.navigate(ServicesDest(sectionName = sectionName)) }
        }

        composable<ServicesDest> { backStackEntry ->
            val section: ServicesDest = backStackEntry.toRoute()
            ServicesScreen(section.sectionName, { navController.popBackStack() }, {navController.navigate(CitaDest(section.sectionName))})
        }

        composable<CitaDest> { backStackEntry ->
            val section: CitaDest = backStackEntry.toRoute()
            CitaScreen(section.sectionName, { navController.popBackStack() })
        }

    }
}