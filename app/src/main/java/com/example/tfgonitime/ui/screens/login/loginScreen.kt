package com.example.tfgonitime.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.AuthViewModel


@Composable
fun LoginScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green)
    ) {
        // 🌟 Primera columna (muñeco superpuesto)
        Column(
            modifier = Modifier
                .border(2.dp, DarkBrown)
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .offset(y = 15.dp) // Baja la columna para superponerse
                .zIndex(1f)
                .padding(end = 20.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Image(
                painter = painterResource(id = R.drawable.onigiri_apoyado),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(130.dp)
            )
        }

        // 🌟 FORMULARIO
        Box(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .background(White, shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp))
                .padding(bottom = 60.dp) // Espacio reservado para el Row
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, DarkBrown)
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "INICIO",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = { navHostController.navigate("signupScreen") }) {
                    Text("¿Olvidaste la contraseña?")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        authViewModel.login(email, password, {
                            navHostController.navigate("homeScreen") {
                                popUpTo("loginScreen") { inclusive = true }
                            }
                        }, { error -> errorMessage = error })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBrown
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(20.dp)

                ) {
                    Text(
                        "Login", style = TextStyle(
                            fontSize = 20.sp,
                            color = White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿No tienes cuenta?")
                    TextButton(onClick = { navHostController.navigate("signupScreen") }) {
                        Text("Regístrate")
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // 🌟 Row fijo al fondo, fuera del formulario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter) // Siempre en la parte inferior
                //.border(2.dp, DarkBrown)
                .padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(50.dp)
                    .background(
                        color = Green,
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.daifuku),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(30.dp)
                    .rotate(25f)
            )
            Image(
                painter = painterResource(id = R.drawable.onigiri),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(30.dp)
                    .rotate(-25f)
            )
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(85.dp)
                    .padding(start = 10.dp, end = 10.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.taiyaki),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(30.dp)
                    .rotate(-15f)
            )
            Image(
                painter = painterResource(id = R.drawable.takoyaki),
                contentDescription = "Muñeco apoyado",
                modifier = Modifier
                    .size(30.dp)
                    .rotate(-25f)
            )
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(50.dp)
                    .background(
                        color = Green,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    }
}