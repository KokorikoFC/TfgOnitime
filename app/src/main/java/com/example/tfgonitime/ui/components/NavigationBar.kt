package com.example.tfgonitime.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tfgonitime.R
import com.example.tfgonitime.ui.theme.Brown
import com.example.tfgonitime.ui.theme.DarkBrown
import com.example.tfgonitime.ui.theme.Green


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onMenuClick: () -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithText(
    title: String,
    text1: String,
    text2: String,
    onActionClick: () -> Unit,
    navController: NavController, // Agrega el NavController como parámetro
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        navigationIcon = {
            Text(
                text = text1,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        navController.popBackStack()
                    },
                fontSize = 16.sp,
                color = Brown
            )
        },
        actions = {
            Text(
                text = text2,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onActionClick() },
                fontSize = 16.sp,
                color = Brown
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}


@Composable
fun CustomBottomNavBar(navController: NavController) {
    // Obtener la ruta actual para determinar el icono activo
    val pantallaActual = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp, // Altura de la sombra
                shape = RectangleShape, // Forma del borde
                ambientColor = Color.Black.copy(alpha = 0.25f), // Sombra difusa con opacidad personalizada
                spotColor = Color.Black.copy(alpha = 0.15f) // Sombra más direccional
            ),
        tonalElevation = 8.dp, // Altura de la sombra tonal
        containerColor = Color.White,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Botón de calendario
        IconButton(
            onClick = { navController.navigate("missionScreen") },
            modifier = Modifier.size(70.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.navbar_icon_mision), // Recurso de drawable
                    contentDescription = "Misión",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(
                        if (pantallaActual == "missionScreen") Green else DarkBrown
                    )
                )
                Text(
                    text = stringResource(R.string.nav_mission),
                    fontSize = 12.sp,
                    color = DarkBrown // Cambia según tu diseño
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Clientes
        IconButton(
            onClick = { navController.navigate("diaryScreen") },
            modifier = Modifier.size(70.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.navbar_icon_diary), // Recurso de drawable
                    contentDescription = "Diario",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(
                        if (pantallaActual == "diaryScreen") Green else DarkBrown
                    )
                )
                Text(
                    text = stringResource(R.string.nav_diary),
                    fontSize = 12.sp,
                    color = DarkBrown // Cambia según tu diseño
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Inicio
        IconButton(
            onClick = { navController.navigate("homeScreen") },
            modifier = Modifier.size(72.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.navbar_icon_onigiribar), // Recurso de drawable
                    contentDescription = "On-itime",
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(
                        if (pantallaActual == "homeScreen") Green else DarkBrown
                    )
                )
                Text(
                    text = stringResource(R.string.nav_home),
                    fontSize = 12.sp,
                    color = DarkBrown // Cambia según tu diseño
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Inventario
        IconButton(
            onClick = { navController.navigate("chatScreen") },
            modifier = Modifier.size(70.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.navbar_icon_chat), // Recurso de drawable
                    contentDescription = "Chat",
                    modifier = Modifier.size(35.dp),
                    colorFilter = ColorFilter.tint(
                        if (pantallaActual == "chatScreen") Green else DarkBrown
                    )
                )
                Text(
                    text = stringResource(R.string.nav_chat),
                    fontSize = 12.sp,
                    color = DarkBrown // Cambia según tu diseño
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Notificaciones
        IconButton(
            onClick = { navController.navigate("settingScreen") },
            modifier = Modifier.size(70.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.navbar_icon_setting), // Recurso de drawable
                    contentDescription = "Ajustes",
                    modifier = Modifier.size(45.dp),
                    colorFilter = ColorFilter.tint(
                        if (pantallaActual == "settingScreen") Green else DarkBrown
                    )
                )
                Text(
                    text = stringResource(R.string.nav_settings),
                    fontSize = 12.sp,
                    color = DarkBrown // Cambia según tu diseño
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

