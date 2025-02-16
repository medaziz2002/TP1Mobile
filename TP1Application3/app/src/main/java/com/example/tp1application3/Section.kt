package com.example.tp1application3

data class Section(
    val type: String,       // Type de section (ex: "public_transport", "waiting", etc.)
    val from: String,       // Point de départ de la section
    val to: String,         // Point d'arrivée de la section
    val duration: Int       // Durée de la section en secondes
)