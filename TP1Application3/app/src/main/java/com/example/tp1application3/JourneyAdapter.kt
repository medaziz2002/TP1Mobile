package com.example.tp1application3

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class JourneyAdapter(private val sections: List<Section>) : RecyclerView.Adapter<JourneyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSection: TextView = itemView.findViewById(R.id.txt_section)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]

        // Vérifier si la section est de type "public_transport"
        if (section.type == "public_transport") {
            // Afficher les informations de la section
            holder.txtSection.text = """
                De: ${section.from}
                À: ${section.to}
                Durée: ${section.duration / 60} minutes
            """.trimIndent()

            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.reservation_message))
                    .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                        val intent = Intent(context, ReservationActivity::class.java)
                        context.startActivity(intent)
                    }
                    .setNegativeButton(context.getString(R.string.no), null)
                    .show()
            }
        } else {
            // Masquer la vue si ce n'est pas une section de type "public_transport"
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    override fun getItemCount(): Int {
        return sections.size
    }
}
