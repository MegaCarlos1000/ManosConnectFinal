package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter2(private var appointments: List<Appointment>) : RecyclerView.Adapter<AppointmentAdapter2.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewTime: TextView = view.findViewById(R.id.textViewTime)
        val textViewUser: TextView = view.findViewById(R.id.textViewUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.textViewDate.text = appointment.date
        holder.textViewTime.text = appointment.time
        holder.textViewUser.text = "${appointment.firstName} ${appointment.lastName}"
    }

    override fun getItemCount() = appointments.size

    fun updateData(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}
