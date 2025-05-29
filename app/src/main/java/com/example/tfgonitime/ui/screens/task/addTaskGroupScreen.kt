package com.example.tfgonitime.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tfgonitime.R
import com.example.tfgonitime.data.model.TaskGroup
import com.example.tfgonitime.ui.components.AnimatedMessage
import com.example.tfgonitime.ui.components.CustomButton
import com.example.tfgonitime.ui.components.CustomTextField
import com.example.tfgonitime.ui.components.GoBackArrow
import com.example.tfgonitime.ui.components.HeaderArrow
import com.example.tfgonitime.ui.theme.*
import com.example.tfgonitime.viewmodel.GroupViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun AddTaskGroupScreen(
    navHostController: NavHostController,
    groupViewModel: GroupViewModel,
    userId: String
) {
    var groupName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("Green") }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorVisible by remember { mutableStateOf(false) }

    val colorMap = mapOf(
        "LightRed" to LightRed,
        "LightOrange" to LightOrange,
        "Yellow" to Yellow,
        "LightGreen" to LightGreen,
        "LightBlue" to LightBlue,
        "LightPink" to LightPink,
        "LightPurple" to LightPurple,
        "Purple" to Purple,
        "LightBrown" to LightBrown
    )
    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)

            ) {
                // Flecha atrás y título
                HeaderArrow(
                    onClick = {
                        navHostController.navigate("homeScreen") {
                            popUpTo("homeScreen") { inclusive = true }
                        }
                    },
                    title = stringResource(R.string.add_task_group_title)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CustomTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = stringResource(R.string.add_task_group_label),
                            placeholder = stringResource(R.string.add_task_group_placeholder),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        Text(
                            stringResource(R.string.add_task_group_color_selection),
                            color = Brown,
                            fontSize = 18.sp
                        )
                    }

                    item {
                        FlowRow(
                            mainAxisSpacing = 10.dp,
                            crossAxisSpacing = 10.dp,
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
                        ) {
                            colorMap.forEach { (colorName, color) ->
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp)
                                        .shadow(4.dp, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(White)
                                        .then(
                                            if (selectedColor == colorName) {
                                                Modifier.border(
                                                    2.dp,
                                                    color,
                                                    RoundedCornerShape(10.dp)
                                                )
                                            } else Modifier
                                        )
                                        .clickable {
                                            selectedColor = colorName
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.4f)
                                            .background(LightBeige),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "#" + Integer.toHexString(color.toArgb())
                                                .uppercase()
                                                .takeLast(6),
                                            color = Gray,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.65f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(145.dp))
                    }
                    item {
                        CustomButton(
                            onClick = {
                                groupViewModel.addGroup(
                                    userId = userId,
                                    group = TaskGroup(
                                        groupName = groupName,
                                        groupColor = selectedColor
                                    ),
                                    onSuccess = {
                                        navHostController.popBackStack()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        isErrorVisible = true
                                    }
                                )
                            },
                            buttonText = stringResource(R.string.add_task_group_button),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }


                // Mensaje de error animado arriba
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    AnimatedMessage(
                        message = errorMessage,
                        isVisible = isErrorVisible,
                        onDismiss = { isErrorVisible = false },
                        isWhite = false
                    )
                }
            }
        }
    }

}

