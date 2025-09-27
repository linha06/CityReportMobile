package com.linha.myreportcity.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.linha.myreportcity.model.user.LoginUser
import com.linha.myreportcity.navigation.Screen
import com.linha.myreportcity.viewmodel.UserViewModel

val loginButtonColors: ButtonColors
    @Composable
    get() = ButtonDefaults.buttonColors(
        containerColor = Color.Blue,
        contentColor = Color.White
    )

val signupButtonColors: ButtonColors
    @Composable
    get() = ButtonDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = Color.Black
    )

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: UserViewModel
) {
    val context = LocalContext.current

    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome !")
            OutlinedTextField(
                value = loginText,
                onValueChange = { loginText = it },
                singleLine = true,
                placeholder = { Text(text = "Masukan username") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                singleLine = true,
                placeholder = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.login(
                        LoginUser(
                            email = loginText,
                            password = passwordText
                        ),
                        navController = navController,
                        context = context
                    )
                },
                colors = loginButtonColors,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 10.dp,
                    disabledElevation = 0.dp
                )
            ) { Text(text = "Login") }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                colors = signupButtonColors,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 10.dp,
                    disabledElevation = 0.dp
                )
            ) { Text(text = "Sign Up") }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .clickable(enabled = false, onClick = {})
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}