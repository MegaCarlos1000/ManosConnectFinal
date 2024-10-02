package com.example.manosconnectfinal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class MainActivity2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewName: TextView
    private lateinit var imageButtonSettings: ImageButton  // Agregar referencia al botón de configuraciones
    private lateinit var imageButtonCasa: ImageButton  // Agregar referencia al botón de configuraciones
    private lateinit var imageButtonNotificacion: ImageButton
    private lateinit var imageButtonCalendario: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Referencias a la imagen de perfil, nombre y configuración
        imageViewProfile = findViewById(R.id.imageViewProfile)
        textViewName = findViewById(R.id.textViewName)
        imageButtonSettings = findViewById(R.id.buttonConfiguraciones)  // Inicializa el botón de configuración
        imageButtonCasa = findViewById(R.id.buttonCasa)
        imageButtonNotificacion = findViewById(R.id.buttonNotificacion)
        imageButtonCalendario = findViewById(R.id.buttonCalendario)

        // Cargar el perfil del usuario
        loadUserProfile()

        loadCasaFragment()

        // Configurar el listener para cargar el fragmento de perfil
        imageViewProfile.setOnClickListener {
            loadUserProfileFragment()
        }

        // Configurar el listener para cargar el fragmento de configuraciones
        imageButtonSettings.setOnClickListener {
            loadConfiguracionesFragment()
        }
        imageButtonCasa.setOnClickListener {
            loadCasaFragment()
        }
        imageButtonNotificacion.setOnClickListener {
            loadNotificacionFragment()
        }
        imageButtonCalendario.setOnClickListener {
            loadCalendarioFragment()
        }


    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            user?.let {
                textViewName.text = "${it.firstName} ${it.lastName}"
                Picasso.get().load(it.profileImage).into(imageViewProfile)
            }
        }
    }

    private fun loadUserProfileFragment() {
        // Cargar el UserProfileFragment
        val userProfileFragment = UserProfileFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, userProfileFragment)
            .addToBackStack(null) // Agrega la transacción al back stack
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun loadConfiguracionesFragment() {
        // Cargar el Configuraciones Fragment
        val configuracionesFragment = Configuraciones()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, configuracionesFragment)
            .addToBackStack(null) // Agrega la transacción al back stack
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
    private fun loadCasaFragment() {
        // Cargar el Casa Fragment
        val casaFragment = Casa() //
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, casaFragment)
            .addToBackStack(null) // Agrega la transacción al back stack
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
    private fun loadNotificacionFragment() {
        // Cargar el Notificacion
        val NotificacionFragment = Notificaciones() //
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, NotificacionFragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
    private fun loadCalendarioFragment() {
        val CalendarioFragment = Calendario() //
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CalendarioFragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }


    data class User(val firstName: String = "", val lastName: String = "", val email: String = "", val profileImage: String = "")
}
