package com.example.manosconnectfinal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class Configuraciones : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var imageViewProfile: ImageView
    private lateinit var editTextName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var buttonSave: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuraciones, container, false)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Referencias a los elementos de la interfaz
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        editTextName = view.findViewById(R.id.editTextName)
        editTextLastName = view.findViewById(R.id.editTextLastName)
        buttonSave = view.findViewById(R.id.buttonSave)

        // Cargar información del usuario
        loadUserProfile()

        // Cambiar la foto de perfil al hacer clic en la imagen
        imageViewProfile.setOnClickListener {
            openImageChooser()
        }

        // Guardar cambios al hacer clic en el botón
        buttonSave.setOnClickListener {
            saveUserProfile()
        }

        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            user?.let {
                editTextName.setText(it.firstName)
                editTextLastName.setText(it.lastName)
                Picasso.get().load(it.profileImage).into(imageViewProfile)
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageViewProfile.setImageURI(imageUri)
        }
    }

    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        val firstName = editTextName.text.toString()
        val lastName = editTextLastName.text.toString()

        if (imageUri != null) {
            // Aquí puedes implementar el código para subir la imagen a Firebase Storage y obtener la URL
            // Por ahora, simplemente usaremos la URI local.
            val user = User(firstName, lastName, imageUri.toString())
            database.child("users").child(userId).setValue(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Información guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la información", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Si no se ha seleccionado una nueva imagen, actualizamos solo los nombres
            val user = User(firstName, lastName)
            database.child("users").child(userId).child("firstName").setValue(firstName)
            database.child("users").child(userId).child("lastName").setValue(lastName).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Información guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la información", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class User(val firstName: String = "", val lastName: String = "", val profileImage: String = "")
}
