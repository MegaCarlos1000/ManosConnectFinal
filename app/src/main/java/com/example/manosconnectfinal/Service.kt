package com.example.manosconnectfinal

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

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
    constructor() : this("", "", "", "", "", "", "", "")
}

class ServiceHistory(
    var userId: String = "",
    var appointments: MutableList<Appointment> = mutableListOf(),
    var services: MutableList<Service> = mutableListOf()
) {
    fun addAppointment(appointment: Appointment) {
        appointments.add(appointment)
    }

    fun addService(service: Service) {
        services.add(service)
    }

    fun loadFromFirebase(userId: String, database: DatabaseReference, onComplete: () -> Unit) {
        database.child("users").child(userId).child("serviceHistory").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointmentsSnapshot = snapshot.child("appointments")
                for (appointmentSnapshot in appointmentsSnapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let { addAppointment(it) }
                }

                val servicesSnapshot = snapshot.child("services")
                for (serviceSnapshot in servicesSnapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    service?.let { addService(it) }
                }

                onComplete() // Llama al callback al finalizar
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }

    fun getSummary(): String {
        return "Total de Citas: ${appointments.size}, Total de Servicios: ${services.size}"
    }
}
data class Achievement(
    val name: String,
    val count: Int
)
data class Badge(
    val name: String
)

