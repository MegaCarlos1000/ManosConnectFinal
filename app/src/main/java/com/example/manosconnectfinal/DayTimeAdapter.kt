package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DayTimeAdapter(private val dayTimes: List<AvailableTime>) : RecyclerView.Adapter<DayTimeAdapter.DayTimeViewHolder>() {

    class DayTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDateTime: TextView = itemView.findViewById(R.id.textViewDateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_time, parent, false)
        return DayTimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayTimeViewHolder, position: Int) {
        val dayTime = dayTimes[position]
        holder.textViewDateTime.text = "${dayTime.date} a las ${dayTime.time}"
    }

    override fun getItemCount(): Int {
        return dayTimes.size
    }
}
