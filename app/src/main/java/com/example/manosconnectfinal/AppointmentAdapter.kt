package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(
    private val appointments: List<Appointment>,
    private val onCancelClick: (String) -> Unit,
    private val onMessageClick: (String) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceTextView: TextView = view.findViewById(R.id.textViewService)
        val dateTimeTextView: TextView = view.findViewById(R.id.textViewDateTime)
        val cancelButton: Button = view.findViewById(R.id.buttonCancel)
        val messageButton: Button = view.findViewById(R.id.buttonMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.appointement_item2, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // Mostrar el nombre completo del usuario y el servicio correspondiente
        holder.serviceTextView.text = "${appointment.firstName} ${appointment.lastName} - ${appointment.serviceId}"

        // Mostrar la fecha y la hora de la cita
        holder.dateTimeTextView.text = "${appointment.date} ${appointment.time}"

        holder.cancelButton.setOnClickListener {
            onCancelClick(appointment.appointmentId) // Usar appointmentId para cancelar la cita
        }

        holder.messageButton.setOnClickListener {
            onMessageClick(appointment.appointmentId) // Usar appointmentId para enviar un mensaje
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }
}
