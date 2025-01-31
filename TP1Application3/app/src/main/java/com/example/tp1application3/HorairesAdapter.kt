package com.example.tp1application3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HorairesAdapter(private var horaires: List<String>) :
    RecyclerView.Adapter<HorairesAdapter.HoraireViewHolder>() {

    class HoraireViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textStationName: TextView = view.findViewById(R.id.item_station)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraireViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_horaire, parent, false)
        return HoraireViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoraireViewHolder, position: Int) {
        holder.textStationName.text = horaires[position]
    }

    override fun getItemCount() = horaires.size

    fun updateData(nouveauxHoraires: List<String>) {
        horaires = nouveauxHoraires // ✅ Mise à jour directe de la liste
        notifyDataSetChanged()
    }
}
