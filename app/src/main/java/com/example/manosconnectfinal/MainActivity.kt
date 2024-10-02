package com.example.manosconnectfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón para ir a Iniciar Sesión
        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

        // Botón para ir a Registrar
        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }
    }
}
