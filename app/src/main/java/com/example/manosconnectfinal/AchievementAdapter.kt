package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(private var achievements: List<Achievement>) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewAchievementName: TextView = view.findViewById(R.id.textViewAchievementName)
        val textViewAchievementCount: TextView = view.findViewById(R.id.textViewAchievementCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.textViewAchievementName.text = achievement.name
        holder.textViewAchievementCount.text = achievement.count.toString()
    }

    override fun getItemCount() = achievements.size

    fun updateData(newAchievements: List<Achievement>) {
        achievements = newAchievements
        notifyDataSetChanged()
    }
}
