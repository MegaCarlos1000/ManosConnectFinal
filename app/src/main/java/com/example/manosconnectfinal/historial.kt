package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class historial : Fragment() {

    private lateinit var serviceHistory: ServiceHistory
    private lateinit var database: DatabaseReference
    private lateinit var textViewSummary: TextView
    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var recyclerViewAppointments: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter2
    private lateinit var appointmentAdapter: AppointmentAdapter2
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial, container, false)

        textViewSummary = view.findViewById(R.id.textViewSummary)
        recyclerViewServices = view.findViewById(R.id.recyclerViewServices)
        recyclerViewAppointments = view.findViewById(R.id.recyclerViewAppointments)

        // Configurar los adaptadores
        serviceAdapter = ServiceAdapter2(emptyList())
        appointmentAdapter = AppointmentAdapter2(emptyList())

        recyclerViewServices.layoutManager = LinearLayoutManager(context)
        recyclerViewServices.adapter = serviceAdapter

        recyclerViewAppointments.layoutManager = LinearLayoutManager(context)
        recyclerViewAppointments.adapter = appointmentAdapter

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            database = FirebaseDatabase.getInstance().reference
            serviceHistory = ServiceHistory(userId)

            // Cargar datos desde Firebase
            loadServiceHistory()
        }

        return view
    }

    private fun loadServiceHistory() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            serviceHistory.loadFromFirebase(userId, database) {
                updateUI()
            }
        }
    }

    private fun updateUI() {
        textViewSummary.text = serviceHistory.getSummary()
        serviceAdapter.updateData(serviceHistory.services)
        appointmentAdapter.updateData(serviceHistory.appointments)
    }
}
