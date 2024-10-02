package com.example.manosconnectfinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private val serviceList: List<Service>,
    private val listener: OnServiceClickListener // Añadido listener
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    interface OnServiceClickListener {
        fun onServiceClick(service: Service)
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewServiceName: TextView = itemView.findViewById(R.id.textViewServiceName)
        val textViewServicePrice: TextView = itemView.findViewById(R.id.textViewServicePrice)
        val textViewProviderName: TextView = itemView.findViewById(R.id.textViewProviderName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]

        holder.textViewServiceName.text = currentService.serviceName
        holder.textViewServicePrice.text = "Precio: ${currentService.servicePrice}"
        holder.textViewProviderName.text = "${currentService.firstName} ${currentService.lastName}"

        holder.itemView.setOnClickListener {
            listener.onServiceClick(currentService) // Manejar el clic en el servicio
        }
    }

    override fun getItemCount() = serviceList.size
}
