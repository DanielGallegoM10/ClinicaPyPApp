package com.example.clinicapypapp.NavigationWrapper

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.clinicapypapp.LoginScreen.LoginScreen
import com.example.clinicapypapp.MainScreen.MainScreen
import com.example.clinicapypapp.ServicesScreens.ServicesScreen
import com.example.clinicapypapp.entities.Section

@Composable
fun NavigationWrapper(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginScreen){
        composable<LoginScreen>{
            LoginScreen{
                navController.navigate(MainScreen)
            }
        }

        composable<MainScreen>{
            MainScreen { sectionName -> navController.navigate(ServicesScreen(sectionName = sectionName)) }
        }

        composable<ServicesScreen> { backStackEntry ->
            val section: ServicesScreen = backStackEntry.toRoute()
            ServicesScreen(section.sectionName, { navController.popBackStack() }, {})
        }

    }
}