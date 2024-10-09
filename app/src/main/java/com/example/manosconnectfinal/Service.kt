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
    var appointmentId: String = "", // ID único de la cita
    var serviceId: String = "",     // ID del servicio agendado
    var userId: String = "",        // ID del usuario que agenda
    var firstName: String = "",     // Nombre del usuario
    var lastName: String = "",      // Apellido del usuario
    var email: String = "",         // Email del usuario
    var date: String = "",          // Fecha de la cita
    var time: String = "",          // Hora de la cita
) {
    // Constructor vacío para Firebase
    constructor() : this("", "", "", "", "")
}
