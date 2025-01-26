package com.example.tfgonitime.data.model

import java.util.UUID

data class TaskGroup(
    val groupId: String = UUID.randomUUID().toString(),
    val groupName: String = "",
    val groupColor: String = ""
)
