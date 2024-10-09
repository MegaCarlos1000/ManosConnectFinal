package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgeAdapter(private var badges: List<Badge>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewBadgeName: TextView = view.findViewById(R.id.textViewBadgeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.textViewBadgeName.text = badge.name
    }

    override fun getItemCount() = badges.size

    fun updateData(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}
