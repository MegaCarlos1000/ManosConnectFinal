package com.example.manosconnectfinal

import ChatAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

class Chat2 : AppCompatActivity() {

    private lateinit var serviceId: String
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat3)

        // Obtener el ID del servicio pasado a través del Intent
        serviceId = intent.getStringExtra("SERVICE_ID") ?: return
        Log.d("Chat2", "ID del servicio recibido: $serviceId")

        // Inicializar RecyclerView
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        chatAdapter = ChatAdapter(messages)
        recyclerViewMessages.adapter = chatAdapter
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        // Configurar el campo de entrada y el botón de enviar
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val buttonSend: Button = findViewById(R.id.buttonSend)

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString()
            if (messageText.isNotBlank()) {
                // Agregar el mensaje enviado a la lista
                messages.add(Message(messageText, true)) // true significa que es un mensaje enviado
                chatAdapter.notifyItemInserted(messages.size - 1)
                recyclerViewMessages.scrollToPosition(messages.size - 1) // Desplazarse al último mensaje
                editTextMessage.text.clear() // Limpiar el campo de entrada

                // Simular un mensaje recibido
                simulateReceivedMessage(messageText)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun simulateReceivedMessage(sentMessage: String) {
        // Simular un mensaje recibido tras un pequeño delay
        val receivedMessage = "Echo: $sentMessage" //
        messages.add(Message(receivedMessage, false)) // false significa que es un mensaje recibido
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerViewMessages.scrollToPosition(messages.size - 1) // Desplazarse al último mensaje
    }
}
