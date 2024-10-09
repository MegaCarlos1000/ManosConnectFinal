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

class MisServiciosFragment : Fragment(), ServiceAdapter.OnServiceClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ServiceAdapter
    private lateinit var misServiciosList: MutableList<Service>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_servicios, container, false)

        database = FirebaseDatabase.getInstance().reference
        misServiciosList = mutableListOf()

        recyclerView = view.findViewById(R.id.recyclerViewMisServicios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ServiceAdapter(misServiciosList, this)
        recyclerView.adapter = adapter

        loadMisServicios()

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
                            it.id = serviceSnapshot.key ?: ""
                            misServiciosList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MisServiciosFragment", "Error: ${error.message}")
                }
            })
    }

    override fun onServiceClick(service: Service) {
        // Navegar al fragmento de Información para un servicio
        val infoFragment = Informacion().apply {
            arguments = Bundle().apply {
                putString("serviceId", service.id)
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
    }
}
