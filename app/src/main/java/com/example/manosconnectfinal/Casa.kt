package com.example.manosconnectfinal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Casa : Fragment(), ServiceAdapter.OnServiceClickListener {

    private lateinit var database: DatabaseReference
    private lateinit var serviceList: MutableList<Service>
    private lateinit var recyclerViewActivities: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_casa, container, false)

        // Inicializar el botón que abre el fragmento para subir servicios
        val buttonMakeService: Button = view.findViewById(R.id.buttonMakeService)
        buttonMakeService.setOnClickListener { loadSubirServiciosFragment() }

        val buttonRequestService: Button = view.findViewById(R.id.buttonRequestService)
        buttonRequestService.setOnClickListener { loadMostrarServiciosFragment() }


        val imageViewMaps: ImageView = view.findViewById(R.id.imageViewMaps)
        imageViewMaps.setOnClickListener {
            abrirMapaCopiapo(requireContext()) // Usando requireContext() para obtener el contexto
        }


        // Inicializar la referencia de Firebase a "services"
        database = FirebaseDatabase.getInstance().reference.child("services")
        serviceList = mutableListOf()

        // Configurar el RecyclerView
        recyclerViewActivities = view.findViewById(R.id.recyclerViewActivities)
        recyclerViewActivities.layoutManager = LinearLayoutManager(requireContext())
        serviceAdapter = ServiceAdapter(serviceList, this) // Pasar el listener
        recyclerViewActivities.adapter = serviceAdapter

        // Cargar servicios
        loadServices()

        return view
    }

    // Método para cargar el fragmento para subir servicios
    private fun loadSubirServiciosFragment() {
        val subirServiciosFragment = SubirServicios()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, subirServiciosFragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
    private fun loadMostrarServiciosFragment() {
        val subirServiciosFragment = MostrarServicios()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, subirServiciosFragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    // Cargar los servicios desde Firebase y filtrar por el usuario actual
    private fun loadServices() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceList.clear()
                for (serviceSnapshot in snapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    service?.let {
                        it.id = serviceSnapshot.key ?: "" // Asignar el ID del servicio
                        // Solo agregar servicios que no sean del usuario actual y que tengan datos válidos
                        if (it.userId != currentUserId && it.servicePrice != "0") {
                            serviceList.add(it)
                        }
                    }
                }
                Log.d("CasaFragment", "Número de servicios cargados: ${serviceList.size}")
                serviceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CasaFragment", "Error al cargar servicios: ${error.message}")
                Toast.makeText(requireContext(), "Error al cargar servicios: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
    fun abrirMapaCopiapo(context: Context) {
        val mapIntent: Intent = Uri.parse(
            "geo:0,0?q=Copiapó,Chile"
        ).let { location ->
            Intent(Intent.ACTION_VIEW, location)
        }

        // Verificamos que haya una aplicación que pueda manejar el Intent
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Manejo en caso de que no haya aplicaciones para abrir mapas
            Toast.makeText(context, "No hay aplicaciones de mapas disponibles", Toast.LENGTH_SHORT).show()
        }
    }






}

