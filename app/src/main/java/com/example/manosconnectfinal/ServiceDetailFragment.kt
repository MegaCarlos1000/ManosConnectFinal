package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ServiceDetailFragment : Fragment() {
    private lateinit var textViewServiceName: TextView
    private lateinit var textViewServicePrice: TextView
    private lateinit var textViewServiceAddress: TextView
    private lateinit var textViewRating: TextView
    private lateinit var textViewProviderName: TextView
    private lateinit var radioGroupAvailableTimes: RadioGroup
    private lateinit var buttonSchedule: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var selectedServiceId: String? = null  // ID del servicio a agendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_detail, container, false)

        textViewServiceName = view.findViewById(R.id.textViewServiceName)
        textViewServicePrice = view.findViewById(R.id.textViewServicePrice)
        textViewServiceAddress = view.findViewById(R.id.textViewServiceAddress)
        textViewRating = view.findViewById(R.id.textViewRating)
        textViewProviderName = view.findViewById(R.id.textViewProviderName)
        radioGroupAvailableTimes = view.findViewById(R.id.radioGroupAvailableTimes)
        buttonSchedule = view.findViewById(R.id.buttonSchedule)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Aquí puedes establecer el ID del servicio que deseas agendar
        selectedServiceId = arguments?.getString("serviceId")

        // Cargar información del servicio
        loadServiceInfo()

        // Configurar el botón de agendar
        buttonSchedule.setOnClickListener { scheduleService() }
        radioGroupAvailableTimes.setOnCheckedChangeListener { _, _ ->
            buttonSchedule.isEnabled = true  // Habilitar botón si se selecciona un horario
        }

        return view
    }

    private fun loadServiceInfo() {
        selectedServiceId?.let { serviceId ->
            database.child("services").child(serviceId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val serviceName = snapshot.child("serviceName").getValue(String::class.java) ?: ""
                    val servicePrice = snapshot.child("servicePrice").getValue(String::class.java) ?: ""
                    val serviceAddress = snapshot.child("serviceAddress").getValue(String::class.java) ?: ""
                    val rating = snapshot.child("rating").getValue(Double::class.java) ?: 0.0
                    val providerFirstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                    val providerLastName = snapshot.child("lastName").getValue(String::class.java) ?: ""

                    textViewServiceName.text = serviceName
                    textViewServicePrice.text = "Precio: $servicePrice"
                    textViewServiceAddress.text = "Dirección: $serviceAddress"
                    textViewRating.text = "Rating: $rating"
                    textViewProviderName.text = "Proveedor: $providerFirstName $providerLastName"

                    // Cargar horarios disponibles
                    loadAvailableTimes(snapshot.child("availableTimes"))
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al cargar información del servicio.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadAvailableTimes(availableTimesSnapshot: DataSnapshot) {
        radioGroupAvailableTimes.removeAllViews() // Limpiar RadioGroup antes de agregar nuevos elementos

        if (!availableTimesSnapshot.hasChildren()) {
            // Si no hay horarios disponibles, mostrar un mensaje
            Toast.makeText(requireContext(), "No hay horarios disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        for (timeSnapshot in availableTimesSnapshot.children) {
            val availableTime = timeSnapshot.getValue(AvailableTime::class.java)
            availableTime?.let {
                val radioButton = RadioButton(requireContext())
                radioButton.text = "${it.date} a las ${it.time}"
                radioButton.id = View.generateViewId()  // Generar ID único para el RadioButton
                radioGroupAvailableTimes.addView(radioButton)
            }
        }
    }


    private fun scheduleService() {
        val selectedRadioButtonId = radioGroupAvailableTimes.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Por favor, selecciona un horario", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = radioGroupAvailableTimes.findViewById<RadioButton>(selectedRadioButtonId)
        val selectedTime = selectedRadioButton.text.toString()

        // Obtener el ID del usuario actual
        val userId = auth.currentUser?.uid ?: return

        // Obtener la información del usuario
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                val email = userSnapshot.child("email").getValue(String::class.java) ?: ""

                // Crear un objeto de cita
                val appointmentId = database.child("appointments").push().key ?: return // Generar ID único
                val appointmentData = mapOf(
                    "serviceId" to selectedServiceId,
                    "userId" to userId,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                    "time" to selectedTime,
                    "date" to selectedRadioButton.text.toString().split(" a las ")[0] // Extraer la fecha del texto
                )

                // Guardar la cita en Firebase
                database.child("appointments").child(appointmentId).setValue(appointmentData)
                    .addOnSuccessListener {
                        // Eliminar el horario agendado de los horarios disponibles
                        removeScheduledTime(selectedRadioButtonId)

                        Toast.makeText(requireContext(), "Servicio agendado para $selectedTime", Toast.LENGTH_SHORT).show()

                        // Navegar al fragmento Casa después de agendar el servicio
                        navigateToCasaFragment()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al agendar el servicio", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar la información del usuario.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Método para navegar al fragmento Casa
    private fun navigateToCasaFragment() {
        val casaFragment = Casa() // Crea una nueva instancia del fragmento Casa
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, casaFragment) // Asegúrate de que este ID sea el correcto
            .addToBackStack(null) // Añadir a la pila de retroceso
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE) // Transición suave
            .commit()
    }


    private fun removeScheduledTime(selectedRadioButtonId: Int) {
        // Encontrar el RadioButton usando su ID
        val selectedRadioButton = radioGroupAvailableTimes.findViewById<RadioButton>(selectedRadioButtonId)

        if (selectedRadioButton != null) {
            // Obtener el horario seleccionado
            val selectedTime = selectedRadioButton.text.toString()
            val date = selectedTime.split(" a las ")[0] // Extraer la fecha del texto

            // Lógica para eliminar el horario de Firebase
            selectedServiceId?.let { serviceId ->
                database.child("services").child(serviceId).child("availableTimes")
                    .orderByChild("date").equalTo(date) // Filtrar por fecha
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (timeSnapshot in snapshot.children) {
                                    val timeValue = timeSnapshot.getValue(AvailableTime::class.java)
                                    if (timeValue?.time == selectedRadioButton.text.toString().split(" a las ")[1]) {
                                        timeSnapshot.ref.removeValue() // Elimina el horario de Firebase
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), "Error al eliminar el horario de Firebase", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            // Eliminar el RadioButton de la vista
            radioGroupAvailableTimes.removeView(selectedRadioButton)
        } else {
            Toast.makeText(requireContext(), "Error al eliminar el horario", Toast.LENGTH_SHORT).show()
        }
    }



}