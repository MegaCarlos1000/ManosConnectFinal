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

class ServiciosReservadosFragment : Fragment(), ServiceAdapter.OnServiceClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServiceAdapter
    private lateinit var serviciosReservadosList: MutableList<Service>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_servicios_reservados, container, false)

        database = FirebaseDatabase.getInstance().reference
        serviciosReservadosList = mutableListOf()

        recyclerView = view.findViewById(R.id.recyclerViewServiciosReservados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ServiceAdapter(serviciosReservadosList, this)
        recyclerView.adapter = adapter

        loadServiciosReservados()

        return view
    }

    private fun loadServiciosReservados() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database.child("appointments").orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    serviciosReservadosList.clear()
                    val uniqueServiceIds = mutableSetOf<String>() // Conjunto para almacenar IDs únicos de servicios

                    for (appointmentSnapshot in snapshot.children) {
                        val serviceId = appointmentSnapshot.child("serviceId").getValue(String::class.java) ?: ""

                        // Verificar si el ID del servicio ya ha sido agregado
                        if (uniqueServiceIds.add(serviceId)) {
                            // Solo agregar el servicio si su ID es único
                            database.child("services").child(serviceId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(serviceSnapshot: DataSnapshot) {
                                    val service = serviceSnapshot.getValue(Service::class.java)
                                    service?.let {
                                        it.id = serviceId // Asignar el ID del servicio
                                        serviciosReservadosList.add(it) // Agregar servicio a la lista
                                        adapter.notifyDataSetChanged() // Notificar al adaptador
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("ServiciosReservadosFragment", "Error: ${error.message}")
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ServiciosReservadosFragment", "Error: ${error.message}")
                }
            })
    }


    override fun onServiceClick(service: Service) {
        val informacion2Fragment = informacion2().apply {
            arguments = Bundle().apply {
                putString("SERVICE_ID", service.id) // Pasar el ID del servicio seleccionado
            }
        }

        // Navegar al nuevo fragmento
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, informacion2Fragment) // Asegúrate de que R.id.fragmentContainer sea el contenedor correcto
            .addToBackStack(null)
            .commit()
    }
}
