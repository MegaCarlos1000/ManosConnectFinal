package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class SubirServicios : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var spinnerServices: Spinner
    private lateinit var editTextServicePrice: EditText
    private lateinit var editTextServiceAddress: EditText
    private lateinit var buttonUploadService: Button
    private lateinit var buttonAddDayTime: Button
    private lateinit var recyclerViewDaysTimes: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var dayTimeAdapter: DayTimeAdapter

    private val services = arrayOf(
        "Masajes", "Mecánica", "Carpintería", "Fontanería",
        "Jardinería", "Pintura", "Limpieza", "Electricidad",
        "Asesoría Legal", "Cuidado de Mascotas", "Clases de Cocina",
        "Reparación de Electrodomésticos", "Entrenamiento Personal",
        "Fotografía", "Diseño Gráfico", "Mudanzas", "Alquiler de Herramientas",
        "Reparación de Teléfonos", "Planificación de Eventos", "Consultoría de Negocios"
    )

    private val selectedDaysTimes: MutableList<AvailableTime> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subir_servicios, container, false)

        spinnerServices = view.findViewById(R.id.spinnerServices)
        editTextServicePrice = view.findViewById(R.id.editTextServicePrice)
        editTextServiceAddress = view.findViewById(R.id.editTextServiceAddress)
        buttonUploadService = view.findViewById(R.id.buttonUploadService)
        buttonAddDayTime = view.findViewById(R.id.buttonAddDayTime)
        recyclerViewDaysTimes = view.findViewById(R.id.recyclerViewDaysTimes)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupSpinner()
        setupRecyclerView()

        buttonAddDayTime.setOnClickListener { showDateTimePicker() }
        buttonUploadService.setOnClickListener { uploadService() }

        return view
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServices.adapter = adapter
    }

    private fun setupRecyclerView() {
        dayTimeAdapter = DayTimeAdapter(selectedDaysTimes)
        recyclerViewDaysTimes.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewDaysTimes.adapter = dayTimeAdapter
    }

    private fun showDateTimePicker() {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog.newInstance(
            this,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show(requireFragmentManager(), "DatePickerDialog")
    }

    override fun onDateSet(dialog: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
        showTimePicker(selectedDate)
    }

    private fun showTimePicker(selectedDate: String) {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog.newInstance(
            this,
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.setTargetFragment(this, 0)
        timePickerDialog.show(requireFragmentManager(), "TimePickerDialog")
        timePickerDialog.arguments = Bundle().apply {
            putString("selectedDate", selectedDate)
        }
    }

    override fun onTimeSet(view: TimePickerDialog, hourOfDay: Int, minute: Int, second: Int) {
        val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        val selectedDate = view.arguments?.getString("selectedDate") ?: return

        selectedDaysTimes.add(AvailableTime(selectedDate, selectedTime))
        dayTimeAdapter.notifyDataSetChanged()
    }

    private fun uploadService() {
        val selectedService = spinnerServices.selectedItem.toString()
        val servicePrice = editTextServicePrice.text.toString()
        val serviceAddress = editTextServiceAddress.text.toString()

        if (servicePrice.isEmpty() || serviceAddress.isEmpty() || selectedDaysTimes.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val serviceId = database.child("services").push().key ?: return // Generar ID único

        // Obtener datos del usuario (proveedor) para asociarlos al servicio
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""

                // Crear el objeto de servicio
                val serviceData = Service(
                    id = serviceId, // Asignar el ID generado
                    serviceName = selectedService,
                    servicePrice = servicePrice,
                    serviceAddress = serviceAddress,
                    userId = userId,
                    firstName = firstName,
                    lastName = lastName,
                    availableTimes = selectedDaysTimes.toList()
                )

                // Subir el servicio a Firebase
                database.child("services").child(serviceId).setValue(serviceData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Servicio subido exitosamente", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al subir el servicio", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearFields() {
        editTextServicePrice.text.clear()
        editTextServiceAddress.text.clear()
        spinnerServices.setSelection(0)
        selectedDaysTimes.clear()
        dayTimeAdapter.notifyDataSetChanged()
    }
}
