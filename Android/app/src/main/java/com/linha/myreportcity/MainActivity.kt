package com.linha.myreportcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.linha.myreportcity.components.SimpleSearchBar
import com.linha.myreportcity.navigation.BottomBar
import com.linha.myreportcity.navigation.Screen
import com.linha.myreportcity.network.RetrofitInstance
import com.linha.myreportcity.repository.CloudinaryRepository
import com.linha.myreportcity.repository.ReportRepository
import com.linha.myreportcity.repository.UserRepository
import com.linha.myreportcity.screen.AdminScreen
import com.linha.myreportcity.screen.CreateReportScreen
import com.linha.myreportcity.screen.HomeScreen
import com.linha.myreportcity.screen.LoginScreen
import com.linha.myreportcity.screen.SignUpScreen
import com.linha.myreportcity.screen.SplashScreen
import com.linha.myreportcity.screen.UserScreen
import com.linha.myreportcity.ui.theme.MyReportCityTheme
import com.linha.myreportcity.viewmodel.CloudinaryViewModel
import com.linha.myreportcity.viewmodel.ReportViewModel
import com.linha.myreportcity.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var cloudinaryViewModel: CloudinaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val reportRepository = ReportRepository(RetrofitInstance.reportsClient)
        val reportViewModelFactory = ReportViewModel.ReportViewModelFactory(reportRepository)
        reportViewModel =
            ViewModelProvider(this, reportViewModelFactory)[ReportViewModel::class.java]

        val userRepository = UserRepository(RetrofitInstance.usersClient)
        val userViewModelFactory = UserViewModel.UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        val cloudinaryRepository = CloudinaryRepository(RetrofitInstance.cloudinaryClient)
        val cloudinaryViewModelFactory =
            CloudinaryViewModel.CloudinaryViewModelFactory(cloudinaryRepository)
        cloudinaryViewModel =
            ViewModelProvider(this, cloudinaryViewModelFactory)[CloudinaryViewModel::class.java]

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyReportCityTheme {
                MainScreen(
                    reportViewModel = reportViewModel,
                    userViewModel = userViewModel,
                    cloudinaryViewModel = cloudinaryViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    reportViewModel: ReportViewModel,
    userViewModel: UserViewModel,
    cloudinaryViewModel: CloudinaryViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showMenuBar = when (currentRoute) {
        Screen.Splash.route, Screen.Login.route, Screen.SignUp.route -> false
        else -> true
    }

    val showTopBarIcon = when (currentRoute) {
        Screen.Splash.route, Screen.Login.route, Screen.SignUp.route -> false
        Screen.User.route, Screen.Create.route, Screen.Admin.route -> false
        else -> true
    }

    val textFieldState = remember { mutableStateOf(TextFieldState()) }.value
    var isSearching by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showMenuBar) {
                if (isSearching) {
                    SimpleSearchBar(
                        textFieldState = textFieldState,
                        onSearch = { query ->
                            userViewModel.searchUsername(
                                userViewModel.tempToken.value.toString(),
                                query
                            )
                        },
                        onClose = {
                            isSearching = false
                            textFieldState.clearText()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        userVM = userViewModel
                    )
                } else {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.height(80.dp),
                        title = {
                            Text(
                                text = "MyReportCity",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.White,
                            titleContentColor = Color.Black
                        ),
                        navigationIcon = {
                            if (showTopBarIcon) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Searching",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            isSearching = true
                                        }
                                )
                            }
                        },
                        actions = {
                            if (showTopBarIcon) {
                                MinimalDropdownMenu(
                                    reportViewModel = reportViewModel,
                                    userViewModel = userViewModel,
                                    navController = navController
                                )
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (showMenuBar) {
                BottomBar(navController = navController, userVM = userViewModel)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = "welcome_graph",
            ) {
                // navigasi welcome
                navigation(
                    startDestination = Screen.Splash.route,
                    route = "welcome_graph"
                ) {
                    composable(Screen.Splash.route) { SplashScreen(navController = navController) }
                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            viewModel = userViewModel
                        )
                    }
                    composable(Screen.SignUp.route) {
                        SignUpScreen(
                            navController = navController,
                            viewModel = userViewModel
                        )
                    }
                }

                // navigasi main content
                navigation(
                    startDestination = Screen.Home.route,
                    route = "main_graph"
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            reportVM = reportViewModel,
                            usersVM = userViewModel
                        )
                    }
                    composable(Screen.Create.route) {
                        CreateReportScreen(
                            reportVM = reportViewModel,
                            userVM = userViewModel,
                            cloudinaryVM = cloudinaryViewModel
                        )
                    }
                    composable(Screen.Admin.route) {
                        AdminScreen(
                            userVM = userViewModel,
                            reportVM = reportViewModel
                        )
                    }
                    composable(Screen.User.route) {
                        UserScreen(
                            reportVM = reportViewModel,
                            usersVM = userViewModel
                        )
                    }
                }
            } // END of NavHost
        }
    }


}

@Composable
fun MinimalDropdownMenu(
    reportViewModel: ReportViewModel,
    userViewModel: UserViewModel,
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    val token by userViewModel.tempToken.collectAsState()

    Box(
        modifier = Modifier
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                Icons.Default.MoreVert,
                modifier = Modifier.size(40.dp),
                contentDescription = "More options"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sort By Status Ascending") },
                onClick = { reportViewModel.getSortedReportByStatusAsc(token.toString()) }
            )
            DropdownMenuItem(
                text = { Text("Sort By Status Descending") },
                onClick = { reportViewModel.getSortedReportByStatusDesc(token.toString()) }
            )
            DropdownMenuItem(
                text = { Text("Logout") },
                onClick = { navController.navigate(Screen.Login.route) }
            )
        }
    }
}
