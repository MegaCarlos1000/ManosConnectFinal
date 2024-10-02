package com.example.manosconnectfinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Calendario : Fragment(), ServiceAdapter.OnServiceClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewMisServicios: RecyclerView
    private lateinit var recyclerViewServiciosReservados: RecyclerView
    private lateinit var adapterMisServicios: ServiceAdapter
    private lateinit var adapterServiciosReservados: ServiceAdapter
    private lateinit var misServiciosList: MutableList<Service>
    private lateinit var serviciosReservadosList: MutableList<Service>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendario, container, false)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Inicializar listas de servicios
        misServiciosList = mutableListOf()
        serviciosReservadosList = mutableListOf()

        // Inicializar RecyclerView para Mis Servicios
        recyclerViewMisServicios = view.findViewById(R.id.recyclerViewMisServicios)
        recyclerViewMisServicios.layoutManager = LinearLayoutManager(requireContext())
        adapterMisServicios = ServiceAdapter(misServiciosList, this) // Pasar listener al adaptador
        recyclerViewMisServicios.adapter = adapterMisServicios

        // Inicializar RecyclerView para Servicios Reservados
        recyclerViewServiciosReservados = view.findViewById(R.id.recyclerViewServiciosReservados)
        recyclerViewServiciosReservados.layoutManager = LinearLayoutManager(requireContext())
        adapterServiciosReservados = ServiceAdapter(serviciosReservadosList, this) // Pasar listener al adaptador
        recyclerViewServiciosReservados.adapter = adapterServiciosReservados

        // Cargar los servicios del usuario y los servicios reservados
        loadMisServicios()
        loadServiciosReservados()

        return view
    }

    private fun loadMisServicios() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database.child("services").orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    misServiciosList.clear()
                    for (serviceSnapshot in snapshot.children) {
                        val service = serviceSnapshot.getValue(Service::class.java)
                        service?.let {
                            it.id = serviceSnapshot.key ?: "" // Asignar el ID del servicio
                            misServiciosList.add(it)
                        }
                    }
                    adapterMisServicios.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CalendarioFragment", "Error al cargar Mis Servicios: ${error.message}")
                    Toast.makeText(requireContext(), "Error al cargar Mis Servicios.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadServiciosReservados() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database.child("appointments").orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    serviciosReservadosList.clear()
                    for (appointmentSnapshot in snapshot.children) {
                        val serviceId = appointmentSnapshot.child("serviceId").getValue(String::class.java) ?: ""
                        database.child("services").child(serviceId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(serviceSnapshot: DataSnapshot) {
                                val service = serviceSnapshot.getValue(Service::class.java)
                                service?.let {
                                    it.id = serviceSnapshot.key ?: "" // Asignar el ID del servicio
                                    serviciosReservadosList.add(it)
                                }
                                adapterServiciosReservados.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("CalendarioFragment", "Error al cargar servicios reservados: ${error.message}")
                                Toast.makeText(requireContext(), "Error al cargar Servicios Reservados.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CalendarioFragment", "Error al cargar servicios reservados: ${error.message}")
                    Toast.makeText(requireContext(), "Error al cargar Servicios Reservados.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Implementar la lógica cuando se hace clic en un servicio
    override fun onServiceClick(service: Service) {
        if (service.userId == FirebaseAuth.getInstance().currentUser?.uid) {
            // Si el servicio es de "Mis Servicios", navegar al fragmento de Información
            val infoFragment = Informacion().apply {
                arguments = Bundle().apply {
                    putString("serviceId", service.id) // Asegúrate de tener el ID del servicio
                    putString("serviceName", service.serviceName)
                    putString("servicePrice", service.servicePrice)
                    putString("serviceAddress", service.serviceAddress)
                    putString("userId", service.userId)
                    putString("providerName", service.firstName)
                    putString("providerLastName", service.lastName)
                    putDouble("rating", service.rating)
                    putSerializable("availableTimes", ArrayList(service.availableTimes))
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, infoFragment)
                .addToBackStack(null)
                .commit()
        } else {
            // Si el servicio es un servicio reservado, navegar al fragmento de Mensajes
            val mensajesFragment = Mensajes().apply {
                arguments = Bundle().apply {
                    putString("serviceId", service.id) // Asegúrate de tener el ID del servicio
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mensajesFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
