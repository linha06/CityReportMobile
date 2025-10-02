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
import com.linha.myreportcity.model.user.RegisterUser
import com.linha.myreportcity.navigation.Screen
import com.linha.myreportcity.viewmodel.UserViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: UserViewModel
) {
    val context = LocalContext.current

    var usernameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Buat Akun Baru")
            OutlinedTextField(
                value = usernameText,
                onValueChange = { usernameText = it },
                singleLine = true,
                placeholder = { Text(text = "Nama Pengguna") }
            )
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                singleLine = true,
                placeholder = { Text(text = "Email") }
            )
            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                singleLine = true,
                placeholder = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.register(
                        RegisterUser(
                            name = usernameText,
                            email = emailText,
                            password = passwordText
                        ),
                        navController = navController,
                        context = context
                    )
                },
                colors = ButtonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.White,
                    disabledContentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp,
                    disabledElevation = 0.dp
                )
            ) { Text(text = "Sign Up") }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.clickable(
                    onClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            ) {
                Text(text = "Sudah punya akun? Login")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}