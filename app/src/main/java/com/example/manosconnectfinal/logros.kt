package com.example.manosconnectfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class logros : Fragment() {

    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var badgeAdapter: BadgeAdapter
    private lateinit var recyclerViewAchievements: RecyclerView
    private lateinit var recyclerViewBadges: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logros, container, false)

        recyclerViewAchievements = view.findViewById(R.id.recyclerViewAchievements)
        recyclerViewBadges = view.findViewById(R.id.recyclerViewBadges)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Configurar los adaptadores
        achievementAdapter = AchievementAdapter(emptyList())
        badgeAdapter = BadgeAdapter(emptyList())

        recyclerViewAchievements.layoutManager = LinearLayoutManager(context)
        recyclerViewAchievements.adapter = achievementAdapter

        recyclerViewBadges.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewBadges.adapter = badgeAdapter

        loadAchievements()
        loadBadges()

        return view
    }

    private fun loadAchievements() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("serviceHistory").child("appointments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val achievements = mutableListOf<Achievement>()

                    // Contar servicios completados
                    val completedServicesCount = snapshot.childrenCount
                    achievements.add(Achievement("Servicios Completados", completedServicesCount.toInt()))

                    achievementAdapter.updateData(achievements)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores
                }
            })
    }

    private fun loadBadges() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).child("serviceHistory").child("services")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val badges = mutableListOf<Badge>()

                    // Puedes agregar lógica para determinar cuántas insignias cargar
                    val serviceCount = snapshot.childrenCount
                    if (serviceCount >= 3) {
                        badges.add(Badge("Insignia Oro"))
                    }
                    if (serviceCount >= 1) {
                        badges.add(Badge("Insignia Plata"))
                    }

                    badgeAdapter.updateData(badges)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar errores
                }
            })
    }
}
