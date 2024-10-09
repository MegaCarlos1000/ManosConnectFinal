package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RatingDialogFragment : DialogFragment() {

    private lateinit var ratingBar: RatingBar
    private lateinit var submitButton: Button
    private lateinit var database: DatabaseReference
    private var appointmentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rating_dialog, container, false)

        ratingBar = view.findViewById(R.id.ratingBar)
        submitButton = view.findViewById(R.id.submitButton)
        database = FirebaseDatabase.getInstance().reference

        appointmentId = arguments?.getString("appointmentId")

        submitButton.setOnClickListener {
            submitRating()
        }

        return view
    }

    private fun submitRating() {
        appointmentId?.let {
            val rating = ratingBar.rating
            database.child("ratings").child(it).setValue(rating)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Calificación enviada", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al enviar la calificación", Toast.LENGTH_SHORT).show()
                }
        }
    }

    companion object {
        fun newInstance(appointmentId: String): RatingDialogFragment {
            val fragment = RatingDialogFragment()
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            fragment.arguments = args
            return fragment
        }
    }
}
