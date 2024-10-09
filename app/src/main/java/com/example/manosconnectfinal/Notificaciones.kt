package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Notificaciones : Fragment() {

    private lateinit var notificationsLayout: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificaciones, container, false)

        notificationsLayout = view.findViewById(R.id.notificationsLayout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        loadNotifications()

        return view
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return

        database.child("notifications").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (notificationSnapshot in snapshot.children) {
                            val notification = notificationSnapshot.getValue(Notification::class.java)
                            notification?.let {
                                addNotificationView(it, notificationSnapshot.key!!)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejo de error opcional
                }
            })
    }

    private fun addNotificationView(notification: Notification, notificationId: String) {
        val notificationView = LayoutInflater.from(requireContext()).inflate(R.layout.item_notification, null)

        val textViewNotification = notificationView.findViewById<TextView>(R.id.textViewNotification)
        textViewNotification.text = notification.message

        notificationView.setOnClickListener {
            // Abrir el diálogo para calificar el servicio
            val ratingDialogFragment = RatingDialogFragment.newInstance(notificationId)
            ratingDialogFragment.show(parentFragmentManager, "RatingDialogFragment")

            // Eliminar la notificación después de que el usuario haga clic en ella
            database.child("notifications").child(auth.currentUser!!.uid).child(notificationId).removeValue()
        }

        notificationsLayout.addView(notificationView)
    }
}
