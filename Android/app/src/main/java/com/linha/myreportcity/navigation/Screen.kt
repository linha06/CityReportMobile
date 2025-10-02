package com.linha.myreportcity.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("Splash")
    object Login : Screen("Login")
    object SignUp : Screen("SignUp")
    object Home : Screen("Home")
    object Create : Screen("Create")
    object User : Screen("User")
    object Admin : Screen("Admin")
}