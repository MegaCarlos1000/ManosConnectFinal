package com.example.manosconnectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class IniciarSesion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_sesion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Referencias a los EditText
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)

        // Botón de inicio de sesión
        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show()
                    // Redirigir a MainActivity2
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    finish() // Cierra la actividad de inicio de sesión
                } else {
                    Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
