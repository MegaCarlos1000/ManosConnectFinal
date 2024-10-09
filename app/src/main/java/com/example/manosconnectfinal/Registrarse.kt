package com.example.manosconnectfinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Registrarse : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var imageViewProfile: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Referencias a los campos del formulario
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        imageViewProfile = findViewById(R.id.imageViewProfile)

        // Selector de imagen
        imageViewProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launcher.launch(intent)
        }

        // Botón de registro
        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener { registerUser() }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data
            imageViewProfile.setImageURI(imageUri) // Muestra la imagen seleccionada
        }
    }

    private fun registerUser() {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        // Validaciones
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Registro de usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    uploadImage(userId, firstName, lastName, email)
                } else {
                    Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImage(userId: String, firstName: String, lastName: String, email: String) {
        imageUri?.let { uri ->
            val fileRef = storageRef.child("profile_images/$userId.jpg")
            fileRef.putFile(uri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveUserData(userId, firstName, lastName, email, downloadUri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al subir imagen.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            // Si no hay imagen seleccionada, guarda solo los datos del usuario
            saveUserData(userId, firstName, lastName, email, "")
        }
    }

    private fun saveUserData(userId: String, firstName: String, lastName: String, email: String, profileImage: String) {
        // Crear una calificación de 5
        val calificacion = Calificacion(score = 5)

        // Crear el usuario con la calificación
        val user = User(firstName, lastName, email, profileImage, calificacion)
        val databaseRef = database.getReference("users").child(userId)

        databaseRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                // Redirigir a la actividad de inicio de sesión
                startActivity(Intent(this, IniciarSesion::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al guardar datos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class User(
        val firstName: String,
        val lastName: String,
        val email: String,
        val profileImage: String,
        val calificacion: Calificacion? = null
    )

    data class Calificacion(val score: Int, val description: String = "")
}
