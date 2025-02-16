package com.example.tp1application3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp1application3.helpers.LocaleHelper
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val apiKey = "cdcef79a-4570-4341-a183-af1aa8d92e78"
    private val client = OkHttpClient()

    private lateinit var inputDepart: EditText
    private lateinit var inputArrivee: EditText
    private lateinit var btnRechercher: Button
    private lateinit var sectionLocalisation: LinearLayout
    private lateinit var sectionTurnOnLocation: LinearLayout
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sectionResults: LinearLayout
    private lateinit var recyclerResults: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        inputDepart = findViewById(R.id.input_depart)
        inputArrivee = findViewById(R.id.input_arrivee)
        btnRechercher = findViewById(R.id.btn_rechercher)
        sectionLocalisation = findViewById(R.id.section_localisation)
        sectionTurnOnLocation = findViewById(R.id.section_turn_on_location)
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu = findViewById(R.id.btn_menu)
        sectionResults = findViewById(R.id.section_results)
        recyclerResults = findViewById(R.id.recycler_results)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }
        recyclerResults.layoutManager = LinearLayoutManager(this)
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_lang_fr -> changeLanguage("fr")
                R.id.menu_lang_en -> changeLanguage("en")
            }
            drawerLayout.closeDrawers()
            true
        }

        // Cacher les sections liées à la localisation
        sectionLocalisation.visibility = View.GONE
        sectionTurnOnLocation.visibility = View.GONE

        // Clic sur le bouton "Rechercher"
        btnRechercher.setOnClickListener {
            val from = inputDepart.text.toString().trim()
            val to = inputArrivee.text.toString().trim()

            if (from.isNotEmpty() && to.isNotEmpty()) {
                // Obtenir les identifiants des stations à partir des noms
                getStationIdFromName(from) { fromId ->
                    getStationIdFromName(to) { toId ->
                        if (fromId != null && toId != null) {
                            // Rendre le datetime dynamique, ici on prend l'heure actuelle
                            val datetime = getCurrentDateTime()

                            // Utilisation de l'URL pour récupérer les trajets entre les deux stations
                            val url = "https://api.sncf.com/v1/coverage/sncf/journeys?from=$fromId&to=$toId&datetime=$datetime"
                            fetchTrainJourneys(url)
                        } else {
                            Log.e("API_ERROR", "Impossible de trouver les identifiants des stations.")
                        }
                    }
                }
            } else {
                Log.e("API_ERROR", "Les champs 'from' ou 'to' sont vides.")
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Fonction pour obtenir l'ID d'une station à partir de son nom
    private fun getStationIdFromName(stationName: String, callback: (String?) -> Unit) {
        val url = "https://api.sncf.com/v1/coverage/sncf/places?q=$stationName"

        val request = Request.Builder()
            .url(url)
            .header("Authorization", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Erreur lors de la requête: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val places = jsonResponse.getJSONArray("places")

                        if (places.length() > 0) {
                            val stationId = places.getJSONObject(0).getString("id")
                            callback(stationId)
                        } else {
                            Log.e("API_ERROR", "Aucun lieu trouvé pour le nom : $stationName")
                            callback(null)
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Erreur lors de l'analyse de la réponse: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e("API_ERROR", "Erreur HTTP: ${response.code}")
                    callback(null)
                }
            }
        })
    }

    private fun fetchTrainJourneys(url: String) {
        val request = Request.Builder()
            .url(url)
            .header("Authorization", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Erreur: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Erreur de connexion. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
                    sectionResults.visibility = View.VISIBLE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.d("API_RESPONSE", responseData ?: "Réponse vide")

                    try {
                        val jsonResponse = JSONObject(responseData)
                        val journeys = jsonResponse.getJSONArray("journeys")

                        if (journeys.length() > 0) {
                            val firstJourney = journeys.getJSONObject(0)

                            // Extraire les sections du trajet
                            val sections = firstJourney.getJSONArray("sections")
                            val sectionsList = mutableListOf<Section>()
                            for (i in 0 until sections.length()) {
                                val section = sections.getJSONObject(i)

                                // Vérifier si les champs "from" et "to" existent
                                val from = if (section.has("from")) {
                                    section.getJSONObject("from").getString("name")
                                } else {
                                    "Inconnu"
                                }

                                val to = if (section.has("to")) {
                                    section.getJSONObject("to").getString("name")
                                } else {
                                    "Inconnu"
                                }

                                sectionsList.add(
                                    Section(
                                        type = section.getString("type"),
                                        from = from,
                                        to = to,
                                        duration = section.getInt("duration")
                                    )
                                )
                            }

                            runOnUiThread {
                                // Mettre à jour le RecyclerView avec les sections
                                recyclerResults.adapter = JourneyAdapter(sectionsList)
                                sectionResults.visibility = View.VISIBLE
                                recyclerResults.visibility = View.VISIBLE
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Aucun trajet trouvé.", Toast.LENGTH_SHORT).show()
                                sectionResults.visibility = View.VISIBLE
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Erreur lors de l'analyse de la réponse: ${e.message}")
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Erreur lors de l'analyse des données.", Toast.LENGTH_SHORT).show()
                            sectionResults.visibility = View.VISIBLE
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Erreur HTTP: ${response.code}")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Erreur HTTP: ${response.code}", Toast.LENGTH_SHORT).show()
                        sectionResults.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun formatDateTime(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date)
    }

    private fun handleMenuAction(itemId: Int) {
        when (itemId) {
            R.id.menu_notifications -> openNotifications()
            R.id.menu_reservations -> openReservations()
            R.id.menu_profile -> openProfile()
            R.id.menu_logout -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun openNotifications() {
        // Implémentez cette fonction
    }

    private fun openReservations() {
        // Implémentez cette fonction
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun changeLanguage(lang: String) {
        LocaleHelper.setLocale(this, lang)
        recreate()
    }






}