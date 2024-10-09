package com.example.manosconnectfinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ActivityChat : AppCompatActivity() {

    private lateinit var listViewChats: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var chatList: MutableList<String> // Para almacenar los IDs de los chats
    private lateinit var namesList: MutableList<String> // Para almacenar los nombres de los usuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat2)

        listViewChats = findViewById(R.id.listViewChats)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        chatList = mutableListOf() // Inicializamos la lista de chats
        namesList = mutableListOf() // Inicializamos la lista de nombres
        loadChats() // Cargamos los chats

        // Manejar clics en los elementos del ListView
        listViewChats.setOnItemClickListener { parent, view, position, id ->
            val selectedServiceId = chatList[position] // Obtener el ID del servicio seleccionado
            loadChatActivity(selectedServiceId) // Cargar la actividad de chat único
        }
    }

    private fun loadChats() {
        // Obtener el ID del usuario actual
        val userId = auth.currentUser?.uid ?: return

        // Escuchar cambios en la base de datos para obtener las citas
        database.child("appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear() // Limpiamos la lista antes de agregar nuevos datos
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let {
                        // Verificar si el usuario actual es uno de los usuarios en la cita
                        if (it.userId == userId || it.serviceId == userId) {
                            val chatPartnerId = if (it.userId == userId) it.serviceId else it.userId
                            chatList.add(chatPartnerId) // Agregar el ID del usuario con quien chatear
                        }
                    }
                }
                updateChatListView() // Actualizar el ListView
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
            }
        })
    }

    private fun updateChatListView() {
        val serviceIds = chatList.distinct() // Obtener IDs únicos de chat
        namesList.clear() // Limpiar la lista de nombres antes de llenarla

        for (serviceId in serviceIds) {
            database.child("services").child(serviceId).get().addOnSuccessListener { serviceSnapshot ->
                val firstName = serviceSnapshot.child("firstName").value?.toString() ?: "Desconocido"
                val lastName = serviceSnapshot.child("lastName").value?.toString() ?: "Desconocido"
                val serviceName = serviceSnapshot.child("serviceName").value?.toString() ?: "Servicio Desconocido"

                // Agregar nombre completo del servicio a la lista
                namesList.add("$firstName $lastName - $serviceName")

                // Registro de depuración
                Log.d("ActivityChat", "Nombre recuperado: $firstName $lastName para serviceId: $serviceId")

                // Verificar si hemos obtenido todos los nombres
                if (namesList.size == serviceIds.size) {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesList)
                    listViewChats.adapter = adapter
                }
            }.addOnFailureListener {
                // Manejo de errores al obtener el servicio
                Log.e("ActivityChat", "Error al obtener servicio: $serviceId", it)
            }
        }
    }

    private fun loadChatActivity(serviceId: String) {
        val intent = Intent(this, Chat2::class.java)
        intent.putExtra("SERVICE_ID", serviceId) // Pasar el ID del servicio
        startActivity(intent)
    }
}
