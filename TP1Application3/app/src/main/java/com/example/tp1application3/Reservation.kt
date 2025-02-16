package com.example.tp1application3

data class Reservation(
    val nbPersonnes: Int = 0,
    val dateReservation: String = "",
    val personnes: List<Map<String, String>> = emptyList(),
    val userId: String = ""
)