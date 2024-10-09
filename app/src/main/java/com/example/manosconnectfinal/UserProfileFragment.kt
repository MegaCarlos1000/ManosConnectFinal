package com.example.manosconnectfinal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class UserProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var textViewName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewCalificacion: TextView // Añadido para la calificación
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonLogout: Button // Agrega la referencia al botón

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los elementos de la interfaz
        textViewName = view.findViewById(R.id.textViewName)
        textViewEmail = view.findViewById(R.id.textViewEmail)
        textViewCalificacion = view.findViewById(R.id.textViewcalificacion) // Inicializa el TextView de calificación
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        buttonLogout = view.findViewById(R.id.buttonLogout) // Inicializa el botón de cerrar sesión

        loadUserProfile()

        // Configura el listener para el botón de cerrar sesión
        buttonLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            user?.let {
                textViewName.text = "${it.firstName} ${it.lastName}"
                textViewEmail.text = it.email
                textViewCalificacion.text = "Calificación: ${it.calificacion?.score ?: "N/A"}" // Mostrar calificación
                Picasso.get().load(it.profileImage).into(imageViewProfile)
            }
        }
    }

    private fun logout() {
        auth.signOut() // Cerrar sesión en Firebase

        Toast.makeText(requireContext(), "Cerraste sesión", Toast.LENGTH_SHORT).show() // Mensaje de cierre de sesión

        // Redirigir a MainActivity y limpiar la pila de actividades
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    data class User(
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val profileImage: String = "",
        val calificacion: Calificacion? = null // Asegúrate de incluir la calificación
    )

    data class Calificacion(
        val score: Int = 0,
        val description: String = ""
    )
}
