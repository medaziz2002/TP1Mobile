package com.example.tp1application3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ReservationAdapter(options: FirestoreRecyclerOptions<Reservation>) :
    FirestoreRecyclerAdapter<Reservation, ReservationAdapter.ReservationViewHolder>(options) {

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val textNbPersonnes: TextView = itemView.findViewById(R.id.text_nb_personnes)
        val textDetails: TextView = itemView.findViewById(R.id.text_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int, model: Reservation) {
        holder.textDate.text = "Date: ${model.dateReservation}"
        holder.textNbPersonnes.text = "Nombre de personnes: ${model.nbPersonnes}"
        holder.textDetails.text = "Détails: ${model.personnes.joinToString { "${it["nom"]} ${it["prenom"]}" }}"
    }
}