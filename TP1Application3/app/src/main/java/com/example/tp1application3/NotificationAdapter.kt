package com.example.tp1application3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NotificationAdapter(options: FirestoreRecyclerOptions<Notification>) :
    FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationViewHolder>(options) {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val message: TextView = itemView.findViewById(R.id.notification_message)
        val date: TextView = itemView.findViewById(R.id.notification_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int, model: Notification) {
        holder.title.text = model.title
        holder.message.text = model.message
        holder.date.text = model.date
    }

    fun deleteItem(position: Int) {
        snapshots.getSnapshot(position).reference.delete() // Supprimer la notification de Firestore
    }
}