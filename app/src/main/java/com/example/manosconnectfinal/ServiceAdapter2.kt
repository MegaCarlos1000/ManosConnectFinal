package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter2(private var services: List<Service>) : RecyclerView.Adapter<ServiceAdapter2.ServiceViewHolder>() {

    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewServiceName: TextView = view.findViewById(R.id.textViewServiceName)
        val textViewServicePrice: TextView = view.findViewById(R.id.textViewServicePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.textViewServiceName.text = service.serviceName
        holder.textViewServicePrice.text = service.servicePrice
    }

    override fun getItemCount() = services.size

    fun updateData(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }
}
