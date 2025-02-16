package com.example.tp1application3

import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationActivity : AppCompatActivity() {

    private lateinit var formContainer: LinearLayout
    private lateinit var spinnerNbPersonnes: Spinner
    private lateinit var btnReserver: Button
    private lateinit var btnBack: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)
        spinnerNbPersonnes = findViewById(R.id.spinner_nb_personnes)
        btnReserver = findViewById(R.id.btn_reserver)
        btnBack=findViewById(R.id.back)
        formContainer = findViewById(R.id.form_container)
        val nbPersonnes = arrayOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nbPersonnes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNbPersonnes.adapter = adapter

        spinnerNbPersonnes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val nb = nbPersonnes[position].toIntOrNull() ?: 1
                afficherFormulaire(nb)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnBack.setOnClickListener {
            // Utiliser onBackPressed pour revenir à l'activité précédente
            onBackPressed()
        }
        btnReserver.setOnClickListener {
            val nb = spinnerNbPersonnes.selectedItem.toString().toIntOrNull() ?: 1
            val dateReservation = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            // Récupérer l'ID de l'utilisateur connecté
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid ?: "inconnu"

            // Créer une liste pour stocker les personnes
            val personnes = mutableListOf<HashMap<String, String>>()

            // Ajouter les personnes à la liste
            for (i in 0 until nb) {
                val nom = edtNoms[i].text.toString()
                val prenom = edtPrenoms[i].text.toString()

                if (nom.isNotEmpty() && prenom.isNotEmpty()) {
                    personnes.add(hashMapOf("nom" to nom, "prenom" to prenom))
                } else {
                    Toast.makeText(this, "Veuillez remplir les informations de toutes les personnes.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Créer un objet Reservation
            val reservation = hashMapOf(
                "nbPersonnes" to nb,
                "dateReservation" to dateReservation,
                "personnes" to personnes,
                "userId" to userId
            )

            // Créer un objet Notification
            val notification = hashMapOf(
                "title" to "Réservation Confirmée",
                "message" to "Réservation effectuée pour le $dateReservation pour $nb personne(s)",
                "date" to dateReservation,
                "userId" to userId
            )

            // Référence à la base de données Firestore
            val db = FirebaseFirestore.getInstance()

            // Ajouter la réservation dans la collection 'reservations'
            db.collection("reservations")
                .add(reservation)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "Réservation ajoutée avec ID: ${documentReference.id}")

                    // Ajouter la notification dans la collection 'notifications'
                    db.collection("notifications")
                        .add(notification)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Notification ajoutée avec succès")
                            Toast.makeText(this, "Réservation et notification enregistrées !", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erreur lors de l'ajout de la notification", e)
                            Toast.makeText(this, "Erreur lors de l'ajout de la notification : ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Erreur lors de l'ajout de la réservation", e)
                    Toast.makeText(this, "Erreur lors de la réservation : ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private val edtNoms = mutableListOf<EditText>()
    private val edtPrenoms = mutableListOf<EditText>()

    private fun afficherFormulaire(nbPersonnes: Int) {
        formContainer.removeAllViews()
        edtNoms.clear()
        edtPrenoms.clear()

        for (i in 1..nbPersonnes) {
            val inputLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8.dpToPx(), 0, 8.dpToPx()) }
            }

            val edtNom = EditText(this).apply {
                hint = "Nom de la personne $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            edtNoms.add(edtNom)
            inputLayout.addView(edtNom)

            val edtPrenom = EditText(this).apply {
                hint = "Prénom de la personne $i"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            edtPrenoms.add(edtPrenom)
            inputLayout.addView(edtPrenom)

            formContainer.addView(inputLayout)
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
