package com.example.manosconnectfinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class informacion2 : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private lateinit var appointmentsList: MutableList<Appointment>
    private lateinit var database: DatabaseReference
    private var serviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceId = arguments?.getString("SERVICE_ID") // Obtener el ID del servicio
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_informacion2, container, false)

        database = FirebaseDatabase.getInstance().reference
        appointmentsList = mutableListOf()

        recyclerView = view.findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AppointmentAdapter(appointmentsList, ::onCancelAppointment, ::onMessageAppointment)
        recyclerView.adapter = adapter

        loadAppointments()

        return view
    }

    private fun loadAppointments() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database.child("appointments").orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsList.clear()

                    // Filtrar solo las citas que pertenecen al serviceId especificado
                    for (appointmentSnapshot in snapshot.children) {
                        val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                        appointment?.let {
                            if (it.serviceId == serviceId) { // Solo añadir si el serviceId coincide
                                appointmentsList.add(it)
                            }
                        }
                    }

                    // Cargar la información del servicio solo una vez
                    if (appointmentsList.isNotEmpty() && serviceId != null) {
                        database.child("services").child(serviceId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(serviceSnapshot: DataSnapshot) {
                                val service = serviceSnapshot.getValue(Service::class.java)
                                service?.let {
                                    for (appointment in appointmentsList) {
                                        appointment.firstName = it.firstName
                                        appointment.lastName = it.lastName
                                    }
                                    adapter.notifyDataSetChanged() // Notificar el cambio
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Informacion2", "Error: ${error.message}")
                            }
                        })
                    } else {
                        adapter.notifyDataSetChanged() // Notificar cambio si no hay citas
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Informacion2", "Error: ${error.message}")
                }
            })
    }

    private fun onCancelAppointment(appointmentId: String?) {
        appointmentId?.let {
            database.child("appointments").child(it).removeValue()
                .addOnSuccessListener {
                    Log.d("Informacion2", "Cita cancelada con éxito.")
                    loadAppointments() // Volver a cargar las citas después de cancelar
                }
                .addOnFailureListener { error ->
                    Log.e("Informacion2", "Error al cancelar la cita: ${error.message}")
                }
        }
    }

    private fun onMessageAppointment(appointmentId: String?) {
        // Implementar la lógica para enviar un mensaje
        Log.d("Informacion2", "Enviar mensaje para la cita: $appointmentId")
    }
}
