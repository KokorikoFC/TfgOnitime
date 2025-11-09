package com.example.tfgonitime.data.model

data class Furniture(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: Int = 0,
    val theme: String = "",
    val slot: String = "" // floor_l_slot, rug, etc
)
