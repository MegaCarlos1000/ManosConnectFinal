package com.example.manosconnectfinal

data class Service(
    var id: String = "", // Asegúrate de inicializarlo con un valor predeterminado
    var serviceName: String = "",
    var servicePrice: String = "",
    var serviceAddress: String = "",
    var userId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var rating: Double = 0.0,
    var availableTimes: List<AvailableTime> = listOf(),
)
data class Appointment(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val time: String,
    val date: String
)

