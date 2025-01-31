package com.example.tp1application3

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {


    private lateinit var recyclerHoraires: RecyclerView
    private lateinit var recyclerSuggestionsDepart: RecyclerView
    private lateinit var recyclerSuggestionsArrivee: RecyclerView
    private lateinit var horairesAdapter: HorairesAdapter
    private lateinit var inputDepart: EditText
    private lateinit var inputArrivee: EditText
    private lateinit var btnRechercher: Button
    private lateinit var sectionLocalisation: LinearLayout
    private lateinit var sectionTurnOnLocation: LinearLayout
    private lateinit var suggestionsAdapterDepart: SuggestionsAdapter
    private lateinit var suggestionsAdapterArrivee: SuggestionsAdapter
    private val listeStationsDisponibles = mutableListOf<String>()


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerHoraires = findViewById(R.id.recycler_horaires)
        recyclerSuggestionsDepart = findViewById(R.id.recycler_suggestions_depart)
        recyclerSuggestionsArrivee = findViewById(R.id.recycler_suggestions_arrivee)
        inputDepart = findViewById(R.id.input_depart)
        inputArrivee = findViewById(R.id.input_arrivee)
        btnRechercher = findViewById(R.id.btn_rechercher)
        sectionLocalisation = findViewById(R.id.section_localisation)
        sectionTurnOnLocation = findViewById(R.id.section_turn_on_location)
        chargerStationsDisponibles()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        recyclerHoraires.layoutManager = LinearLayoutManager(this)
        recyclerSuggestionsDepart.layoutManager = LinearLayoutManager(this)
        recyclerSuggestionsArrivee.layoutManager = LinearLayoutManager(this)


        horairesAdapter = HorairesAdapter(mutableListOf())
        recyclerHoraires.adapter = horairesAdapter


        suggestionsAdapterDepart = SuggestionsAdapter(emptyList()) { station ->
            inputDepart.setText(station)
            sectionTurnOnLocation.visibility = View.GONE
            recyclerSuggestionsDepart.visibility = View.GONE
        }
        suggestionsAdapterArrivee = SuggestionsAdapter(emptyList()) { station ->
            inputArrivee.setText(station)
            recyclerSuggestionsArrivee.visibility = View.GONE
        }

        recyclerSuggestionsDepart.adapter = suggestionsAdapterDepart
        recyclerSuggestionsArrivee.adapter = suggestionsAdapterArrivee


        inputDepart.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                recyclerHoraires.visibility = View.GONE
                sectionTurnOnLocation.visibility = View.VISIBLE
                detecterPositionUtilisateur { stationsProches ->

                    suggestionsAdapterDepart.updateData(stationsProches)
                    recyclerSuggestionsDepart.visibility = View.VISIBLE
                    recyclerSuggestionsArrivee.visibility = View.GONE
                }
            } else {
                sectionTurnOnLocation.visibility = View.GONE
            }
        }


        inputArrivee.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                recyclerHoraires.visibility = View.GONE
                sectionTurnOnLocation.visibility = View.GONE
                detecterPositionUtilisateur { stationsProches ->

                    suggestionsAdapterArrivee.updateData(stationsProches)
                    recyclerSuggestionsArrivee.visibility = View.VISIBLE
                    recyclerSuggestionsDepart.visibility = View.GONE
                }
            } else {
                sectionTurnOnLocation.visibility = View.GONE
            }
        }





        inputDepart.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerHoraires.visibility = View.GONE
                if (s.isNullOrEmpty()) {
                    recyclerSuggestionsDepart.visibility = View.GONE
                    btnRechercher.visibility = View.GONE
                } else {
                    btnRechercher.visibility = View.VISIBLE
                    filtrerSuggestions(s.toString(), suggestionsAdapterDepart, recyclerSuggestionsDepart)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputArrivee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerHoraires.visibility = View.GONE
                if (s.isNullOrEmpty()) {
                    recyclerSuggestionsArrivee.visibility = View.GONE
                    btnRechercher.visibility = View.GONE
                } else {
                    btnRechercher.visibility = View.VISIBLE
                    filtrerSuggestions(s.toString(), suggestionsAdapterArrivee, recyclerSuggestionsArrivee)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })











        sectionTurnOnLocation.setOnClickListener {
            detecterPositionUtilisateur()
        }


        sectionLocalisation.setOnClickListener {
            detecterPositionUtilisateur()
        }


        btnRechercher.setOnClickListener {

            recyclerSuggestionsDepart.visibility = View.GONE
            btnRechercher.visibility = View.GONE

            rechercherHoraires()
        }

    }


    fun filtrerSuggestions(texte: String, adapter: SuggestionsAdapter, recyclerView: RecyclerView) {
        val suggestionsFiltrees = listeStationsDisponibles.filter { it.startsWith(texte, ignoreCase = true) }
        adapter.updateData(suggestionsFiltrees)
        recyclerView.visibility = if (suggestionsFiltrees.isEmpty()) View.GONE else View.VISIBLE
    }


    private fun detecterPositionUtilisateur(callback: (List<String>) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude


                val stationsProches = trouverStationsLesPlusProches(this, latitude, longitude,4)

                if (stationsProches.isNotEmpty()) {
                    callback(stationsProches)
                } else {

                    callback(emptyList())
                }
            } else {

                callback(emptyList())
            }
        }.addOnFailureListener { e ->

            callback(emptyList())
        }
    }




    private fun trouverStationsLesPlusProches(context: Context, latitude: Double, longitude: Double, nombreStations: Int): List<String> {
        val stations = mutableListOf<Pair<String, Double>>() // Liste des stations avec leur distance

        try {
            val inputStream = context.assets.open("TAM_MMM_TpsReel.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            var ligne: String?


            while (reader.readLine().also { ligne = it } != null) {
                val colonnes = ligne!!.split(",")

                if (colonnes.size >= 3) {
                    val stopName = colonnes[0].uppercase().trim()
                    val latStop = colonnes[3].toDoubleOrNull()
                    val lonStop = colonnes[4].toDoubleOrNull()

                    if (latStop != null && lonStop != null) {
                        val distance = calculerDistance(latitude, longitude, latStop, lonStop)
                        stations.add(Pair(stopName, distance))
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("DEBUG", "Erreur lors de la lecture du fichier CSV", e)
        }

        return stations.sortedBy { it.second }.take(nombreStations).map { it.first }
    }






    private fun chargerStationsDisponibles() {
        try {
            val inputStream = assets.open("TAM_MMM_TpsReel.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            var ligne: String?
            while (reader.readLine().also { ligne = it } != null) {
                val colonnes = ligne!!.split(",")
                if (colonnes.isNotEmpty()) {
                    val stationDepart = colonnes[0].uppercase().trim()
                    val stationArrivee = colonnes[1].uppercase().trim()

                    if (!listeStationsDisponibles.contains(stationDepart)) {
                        listeStationsDisponibles.add(stationDepart)
                    }
                    if (!listeStationsDisponibles.contains(stationArrivee)) {
                        listeStationsDisponibles.add(stationArrivee)
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("DEBUG", "Erreur lors du chargement des stations", e)
        }
    }




private fun detecterPositionUtilisateur() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        return
    }

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude



            val arretProche = trouverArretLePlusProche(this, latitude, longitude)

            if (arretProche != null) {
                inputDepart.setText(arretProche)
                sectionTurnOnLocation.visibility = View.GONE
            } else {
                Log.e("DEBUG", "Aucun arrêt trouvé à proximité.")
            }
        } else {
            Log.e("DEBUG", "Impossible d'obtenir la position.")
        }
    }.addOnFailureListener { e ->
        Log.e("DEBUG", "Erreur lors de la récupération de la position", e)
    }
}



    private fun trouverArretLePlusProche(context: Context, latitude: Double, longitude: Double): String {
        var arretProche = "Aucun arrêt trouvé"
        var distanceMin = Double.MAX_VALUE

        try {
            val inputStream = context.assets.open("TAM_MMM_TpsReel.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            var ligne: String?
            while (reader.readLine().also { ligne = it } != null) {
                val colonnes = ligne!!.split(",")

                if (colonnes.size >= 3) {
                    val stopName = colonnes[0].uppercase().trim()
                    val latStop = colonnes[3].toDoubleOrNull()
                    val lonStop = colonnes[4].toDoubleOrNull()


                    if (latStop != null && lonStop != null) {
                        val distance = calculerDistance(latitude, longitude, latStop, lonStop)

                        if (distance < distanceMin) {
                            distanceMin = distance
                            arretProche = stopName
                        }
                    }




                }
            }

            reader.close()
        } catch (e: Exception) {
            Log.e("DEBUG", "Erreur lors de la lecture du fichier CSV", e)
        }

        return arretProche
    }



    private fun calculerDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Rayon de la Terre en mètres
        val phi1 = lat1 * PI / 180
        val phi2 = lat2 * PI / 180
        val deltaPhi = (lat2 - lat1) * PI / 180
        val deltaLambda = (lon2 - lon1) * PI / 180

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) * sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance en mètres
    }

    private fun rechercherHoraires() {
        val depart = inputDepart.text.toString().uppercase().trim()
        val arrivee = inputArrivee.text.toString().uppercase().trim()

        if (depart.isNotEmpty() && arrivee.isNotEmpty()) {
            val horaires = lireHorairesDepuisCSV(this, depart, arrivee)

            if (horaires.isEmpty()) {

                recyclerHoraires.visibility = View.GONE
            } else {

                recyclerHoraires.visibility = View.VISIBLE
                horairesAdapter.updateData(horaires.map { "${it.first} ${it.second} -> ${it.third}" })
            }
        } else {
            Log.e("DEBUG", "Les champs de départ et/ou d'arrivée sont vides")
        }
    }







    private fun lireHorairesDepuisCSV(context: Context, depart: String, arrivee: String): List<Triple<String, String, String>> {
        val horaires = mutableListOf<Triple<String, String, String>>()
        try {
            val inputStream = context.assets.open("TAM_MMM_TpsReel.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            var ligne: String?
            var ajouter = false

            while (reader.readLine().also { ligne = it } != null) {
                val colonnes = ligne!!.split(",")

                if (colonnes.size >= 5) {
                    val stopName = colonnes[0].uppercase().trim()
                    val destination = colonnes[1].uppercase().trim()
                    val departureTime = colonnes[2].trim()

                    if (stopName == depart) {
                        ajouter = true
                    }

                    if (ajouter) {
                        horaires.add(Triple(stopName, departureTime, ""))
                    }

                    if (destination == arrivee) {
                        horaires.add(Triple(destination, "", departureTime))
                        break
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("DEBUG", "Erreur lors de la lecture du fichier CSV", e)
        }
        return horaires
    }








}
