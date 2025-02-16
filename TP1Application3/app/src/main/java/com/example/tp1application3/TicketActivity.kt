package com.example.tp1application3

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TicketActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationAdapter
    private lateinit var btnPrint: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation)

        // Initialiser les vues
        recyclerView = findViewById(R.id.reservations_recyclerview)
        btnPrint = findViewById(R.id.print_button)

        // Configurer le RecyclerView
        setupRecyclerView()

        // Configurer le bouton d'impression
        btnPrint.setOnClickListener {
            Toast.makeText(this, "Impression du ticket...", Toast.LENGTH_SHORT).show()
            // Ajouter ici la logique pour imprimer le ticket
        }
    }

    private fun setupRecyclerView() {
        // Récupérer l'ID de l'utilisateur connecté
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        // Configurer la requête Firestore pour récupérer les réservations de l'utilisateur
        val query: Query = FirebaseFirestore.getInstance()
            .collection("reservations")
            .whereEqualTo("userId", userId)
            .orderBy("dateReservation", Query.Direction.DESCENDING)

        // Configurer les options pour le FirestoreRecyclerAdapter
        val options = FirestoreRecyclerOptions.Builder<Reservation>()
            .setQuery(query, Reservation::class.java)
            .build()

        // Configurer l'adaptateur
        adapter = ReservationAdapter(options)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening() // Démarrer l'écoute des données Firestore
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening() // Arrêter l'écoute des données Firestore
    }
}