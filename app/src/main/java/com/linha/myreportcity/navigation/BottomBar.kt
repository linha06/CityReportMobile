package com.linha.myreportcity.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.linha.myreportcity.viewmodel.UserViewModel

data class NavigationItem(val title: String, val icon: ImageVector, val screen: Screen)

@Composable
fun BottomBar(modifier: Modifier = Modifier, navController: NavController, userVM: UserViewModel) {

    val context = LocalContext.current
    val token = userVM.tempToken.collectAsState()
    val userRole = userVM.user.observeAsState().value?.adminRole

    LaunchedEffect(key1 = token.toString()) {
        userVM.getUserProfiles(token.value.toString(), context)
    }

    NavigationBar(
        modifier = modifier.height(80.dp),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = Icons.Default.Home,
                screen = Screen.Home
            ),
            if (userRole == true){
                NavigationItem(
                    title = "Admin",
                    icon = Icons.Default.AccountBox,
                    screen = Screen.Admin
                )
            } else {
                NavigationItem(
                    title = "Create",
                    icon = Icons.Default.Add,
                    screen = Screen.Create
                )
            },
            NavigationItem(
                title = "User",
                icon = Icons.Default.Person,
                screen = Screen.User
            )
        )

        navigationItems.map { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        // satu instance tiap navigasi
                        launchSingleTop = true
                        popUpTo("main_graph") {
                            saveState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}