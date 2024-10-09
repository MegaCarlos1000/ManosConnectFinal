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
    private var serviceId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_informacion, container, false)

        appointmentsLayout = view.findViewById(R.id.appointmentsLayout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Obtener el serviceId de los argumentos
        serviceId = arguments?.getString("serviceId")

        loadUserAppointments()

        return view
    }

    private fun loadUserAppointments() {
        val userId = auth.currentUser?.uid ?: return
        Log.d("userId", "ID del usuario: $userId")

        // Cargar citas solo para el servicio específico
        serviceId?.let { id ->
            database.child("appointments").orderByChild("serviceId").equalTo(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (appointmentSnapshot in snapshot.children) {
                                val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                                appointment?.let {
                                    addAppointmentView(it)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejo de error opcional
                    }
                })
        }
    }

    private fun addAppointmentView(appointment: Appointment) {
        val appointmentView = LayoutInflater.from(requireContext()).inflate(R.layout.appointment_item, null)

        val textViewUserName = appointmentView.findViewById<TextView>(R.id.textViewUserName)
        val textViewAppointmentTime = appointmentView.findViewById<TextView>(R.id.textViewAppointmentTime)
        val buttonComplete = appointmentView.findViewById<Button>(R.id.buttonComplete)

        textViewUserName.text = "${appointment.firstName} ${appointment.lastName}"
        textViewAppointmentTime.text = "${appointment.date} a las ${appointment.time}"

        buttonComplete.setOnClickListener {
            completeAppointment(appointment.appointmentId)
        }

        appointmentsLayout.addView(appointmentView)
    }


    private fun completeAppointment(appointmentId: String?) {
        appointmentId?.let {
            database.child("appointments").child(it).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cita completada", Toast.LENGTH_SHORT).show()
                    // Volver a cargar las citas
                    loadUserAppointments()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al completar la cita", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
