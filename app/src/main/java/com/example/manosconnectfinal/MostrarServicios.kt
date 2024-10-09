package com.example.manosconnectfinal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MostrarServicios : Fragment(), ServiceAdapter.OnServiceClickListener {

    private lateinit var serviceAdapter: ServiceAdapter
    private val serviceList = mutableListOf<Service>() // Lista original de servicios
    private val filteredServiceList = mutableListOf<Service>() // Lista filtrada

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mostrar_servicios, container, false)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recycler_view)
        val searchView: SearchView = rootView.findViewById(R.id.search_view)

        // Configuración del RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        serviceAdapter = ServiceAdapter(filteredServiceList, this)
        recyclerView.adapter = serviceAdapter

        // Cargar servicios desde Firebase
        loadServices()

        // Configurar el filtro del SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterServices(newText ?: "")
                return true
            }
        })

        return rootView
    }

    // Método para cargar los servicios desde Firebase
    private fun loadServices() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().getReference("services")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceList.clear()
                for (serviceSnapshot in snapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    service?.let {
                        it.id = serviceSnapshot.key ?: ""
                        if (it.userId != currentUserId && it.servicePrice != "0") {
                            serviceList.add(it)
                        }
                    }
                }
                Log.d("MostrarServicios", "Número de servicios cargados: ${serviceList.size}")
                filterServices("") // Mostrar todos los servicios al principio
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MostrarServicios", "Error al cargar servicios: ${error.message}")
                Toast.makeText(requireContext(), "Error al cargar servicios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Método para filtrar la lista de servicios según la búsqueda
    private fun filterServices(query: String) {
        val filteredList = if (query.isEmpty()) {
            serviceList // Si la búsqueda está vacía, mostrar todos los servicios
        } else {
            serviceList.filter { service ->
                service.serviceName.contains(query, ignoreCase = true) ||
                        service.firstName.contains(query, ignoreCase = true) ||
                        service.lastName.contains(query, ignoreCase = true)
            }
        }
        filteredServiceList.clear()
        filteredServiceList.addAll(filteredList)
        serviceAdapter.notifyDataSetChanged()
    }

    // Cuando se hace clic en un servicio, abrir el fragmento de detalles del servicio
    override fun onServiceClick(service: Service) {
        val serviceDetailFragment = ServiceDetailFragment().apply {
            arguments = Bundle().apply {
                putString("serviceId", service.id)  // Asegúrate de tener el ID del servicio
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
            .replace(R.id.fragmentContainer, serviceDetailFragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }


}
