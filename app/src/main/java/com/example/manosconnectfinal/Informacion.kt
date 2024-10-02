package com.example.manosconnectfinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Informacion : Fragment() {

    private lateinit var appointmentsLayout: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_informacion, container, false)

        appointmentsLayout = view.findViewById(R.id.appointmentsLayout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        loadUserAppointments()

        return view
    }

    private fun loadUserAppointments() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("userId", "ID del usuario: $userId")

        // Cargar los servicios creados por el usuario actual (proveedor)
        database.child("services").orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(serviceSnapshot: DataSnapshot) {
                    if (serviceSnapshot.exists()) {
                        for (service in serviceSnapshot.children) {
                            val serviceId = service.key ?: continue

                            // Ahora buscar las citas asociadas a ese servicio
                            database.child("appointments").orderByChild("serviceId").equalTo(serviceId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        Log.d("FirebaseData", "Datos recibidos: $snapshot")
                                        if (snapshot.exists()) {
                                            for (appointmentSnapshot in snapshot.children) {
                                                val firstName = appointmentSnapshot.child("firstName").getValue(String::class.java) ?: ""
                                                val lastName = appointmentSnapshot.child("lastName").getValue(String::class.java) ?: ""
                                                val time = appointmentSnapshot.child("time").getValue(String::class.java) ?: ""

                                                Log.d("AppointmentData", "Cita: $firstName $lastName a las $time")
                                                addAppointmentView(firstName, lastName, time, appointmentSnapshot.key)
                                            }
                                        } else {
                                            Toast.makeText(requireContext(), "No tienes citas agendadas para este servicio", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(requireContext(), "Error al cargar las citas", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(requireContext(), "No has creado servicios", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar los servicios", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun addAppointmentView(firstName: String, lastName: String, time: String, appointmentId: String?) {
        val appointmentView = LayoutInflater.from(requireContext()).inflate(R.layout.appointment_item, null)

        val textViewUserName = appointmentView.findViewById<TextView>(R.id.textViewUserName)
        val textViewAppointmentTime = appointmentView.findViewById<TextView>(R.id.textViewAppointmentTime)
        val buttonComplete = appointmentView.findViewById<Button>(R.id.buttonComplete)

        textViewUserName.text = "$firstName $lastName"
        textViewAppointmentTime.text = time

        buttonComplete.setOnClickListener {
            completeAppointment(appointmentId)
        }

        appointmentsLayout.addView(appointmentView)
    }

    private fun completeAppointment(appointmentId: String?) {
        appointmentId?.let {
            database.child("appointments").child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cita completada", Toast.LENGTH_SHORT).show()
                    // Eliminar la vista de la cita
                    loadUserAppointments() // Volver a cargar las citas
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al completar la cita", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
