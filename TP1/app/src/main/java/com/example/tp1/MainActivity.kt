package com.example.tp1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tp1.helpers.LocaleHelper
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var inputNom: EditText
    private lateinit var inputPrenom: EditText
    private lateinit var inputAge: EditText
    private lateinit var spinnerDomaine: Spinner
    private lateinit var spinnerPays: Spinner
    private lateinit var inputCodePays: EditText
    private lateinit var inputTelephone: EditText
    private lateinit var btnValider: Button
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: Button
    private lateinit var drawerLayout: DrawerLayout

    // Liste des pays avec leurs indicatifs
    private val paysList = arrayOf(
        "Tunisie 🇹🇳",
        "France 🇫🇷",
        "Algérie 🇩🇿",
        "Maroc 🇲🇦",
        "Canada 🇨🇦",
        "États-Unis 🇺🇸",
        "Royaume-Uni 🇬🇧",
        "Allemagne 🇩🇪",
        "Italie 🇮🇹",
        "Espagne 🇪🇸",
        "Portugal 🇵🇹",
        "Belgique 🇧🇪",
        "Suisse 🇨🇭",
        "Pays-Bas 🇳🇱",
        "Suède 🇸🇪",
        "Norvège 🇳🇴",
        "Danemark 🇩🇰",
        "Finlande 🇫🇮",
        "Irlande 🇮🇪",
        "Autriche 🇦🇹",
        "Grèce 🇬🇷",
        "Russie 🇷🇺",
        "Turquie 🇹🇷",
        "Arabie Saoudite 🇸🇦",
        "Émirats Arabes Unis 🇦🇪",
        "Qatar 🇶🇦",
        "Égypte 🇪🇬",
        "Afrique du Sud 🇿🇦",
        "Nigéria 🇳🇬",
        "Sénégal 🇸🇳",
        "Côte d'Ivoire 🇨🇮",
        "Mali 🇲🇱",
        "Gabon 🇬🇦",
        "Kenya 🇰🇪",
        "Éthiopie 🇪🇹",
        "Inde 🇮🇳",
        "Pakistan 🇵🇰",
        "Bangladesh 🇧🇩",
        "Japon 🇯🇵",
        "Chine 🇨🇳",
        "Corée du Sud 🇰🇷",
        "Indonésie 🇮🇩",
        "Malaisie 🇲🇾",
        "Singapour 🇸🇬",
        "Thaïlande 🇹🇭",
        "Vietnam 🇻🇳",
        "Australie 🇦🇺",
        "Nouvelle-Zélande 🇳🇿",
        "Mexique 🇲🇽",
        "Brésil 🇧🇷",
        "Argentine 🇦🇷",
        "Chili 🇨🇱",
        "Colombie 🇨🇴",
        "Pérou 🇵🇪",
        "Venezuela 🇻🇪"
    )

    // Liste des indicatifs correspondants
    private val indicatifs = arrayOf(
        "+216", // Tunisie
        "+33",  // France
        "+213", // Algérie
        "+212", // Maroc
        "+1",   // Canada
        "+1",   // États-Unis
        "+44",  // Royaume-Uni
        "+49",  // Allemagne
        "+39",  // Italie
        "+34",  // Espagne
        "+351", // Portugal
        "+32",  // Belgique
        "+41",  // Suisse
        "+31",  // Pays-Bas
        "+46",  // Suède
        "+47",  // Norvège
        "+45",  // Danemark
        "+358", // Finlande
        "+353", // Irlande
        "+43",  // Autriche
        "+30",  // Grèce
        "+7",   // Russie
        "+90",  // Turquie
        "+966", // Arabie Saoudite
        "+971", // Émirats Arabes Unis
        "+974", // Qatar
        "+20",  // Égypte
        "+27",  // Afrique du Sud
        "+234", // Nigéria
        "+221", // Sénégal
        "+225", // Côte d'Ivoire
        "+223", // Mali
        "+241", // Gabon
        "+254", // Kenya
        "+251", // Éthiopie
        "+91",  // Inde
        "+92",  // Pakistan
        "+880", // Bangladesh
        "+81",  // Japon
        "+86",  // Chine
        "+82",  // Corée du Sud
        "+62",  // Indonésie
        "+60",  // Malaisie
        "+65",  // Singapour
        "+66",  // Thaïlande
        "+84",  // Vietnam
        "+61",  // Australie
        "+64",  // Nouvelle-Zélande
        "+52",  // Mexique
        "+55",  // Brésil
        "+54",  // Argentine
        "+56",  // Chili
        "+57",  // Colombie
        "+51",  // Pérou
        "+58"   // Venezuela
    )

    // Liste des domaines de compétences
    private val domaines = arrayOf(
        "Sélectionnez",
        "Ingénieur",
        "Développeur",
        "Designer",
        "Chef de Projet"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLocale(this, LocaleHelper.getSavedLanguage(this))
        setContentView(R.layout.activity_main)

        // Liaison avec l'interface XML
        inputNom = findViewById(R.id.input_nom)
        inputPrenom = findViewById(R.id.input_prenom)
        inputAge = findViewById(R.id.input_age)
        spinnerDomaine = findViewById(R.id.spinner_domaine)
        spinnerPays = findViewById(R.id.spinner_pays)
        inputCodePays = findViewById(R.id.input_code_pays)
        inputTelephone = findViewById(R.id.input_telephone)
        btnValider = findViewById(R.id.btn_valider)
        navigationView = findViewById(R.id.navigation_view)
        btnMenu = findViewById(R.id.btn_menu)
        drawerLayout = findViewById(R.id.drawer_layout)



        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }


        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_lang_fr -> changeLanguage("fr")
                R.id.menu_lang_en -> changeLanguage("en")
            }
            drawerLayout.closeDrawers()
            true
        }

        val adapterPays = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paysList)
        spinnerPays.adapter = adapterPays


        val adapterDomaine = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, domaines)
        spinnerDomaine.adapter = adapterDomaine


        spinnerPays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                inputCodePays.setText(indicatifs[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                inputCodePays.setText("+216")
            }
        }


        btnValider.setOnClickListener {
            val nom = inputNom.text.toString().trim()
            val prenom = inputPrenom.text.toString().trim()
            val age = inputAge.text.toString().trim()
            val domaine = spinnerDomaine.selectedItem.toString()
            val paysSelectionne = spinnerPays.selectedItem.toString()
            val codePays = inputCodePays.text.toString()
            val telephone = inputTelephone.text.toString().trim()


            if (nom.isEmpty()) {
                inputNom.error = getString(R.string.error_nom)
                return@setOnClickListener
            }
            if (!nom.matches("^[a-zA-ZÀ-ÿ -]+$".toRegex())) {
                inputNom.error = getString(R.string.error_nom_format)
                return@setOnClickListener
            }

            // Vérification du champ Prénom (non vide + alphabétique)
            if (prenom.isEmpty()) {
                inputPrenom.error = getString(R.string.error_prenom)
                return@setOnClickListener
            }
            if (!prenom.matches("^[a-zA-ZÀ-ÿ -]+$".toRegex())) {
                inputPrenom.error = getString(R.string.error_prenom_format)
                return@setOnClickListener
            }

            // Vérification de l'âge
            if (age.isEmpty()) {
                inputAge.error = getString(R.string.error_age)
                return@setOnClickListener
            }

            val ageInt = age.toIntOrNull()

            if (ageInt == null || ageInt <= 0 || ageInt > 150) {
                inputAge.error = getString(R.string.error_age_format)
                return@setOnClickListener
            }

            // Vérification du domaine
            if (domaine == "Sélectionnez") {
                Toast.makeText(this, getString(R.string.error_domaine), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérification du téléphone
            if (telephone.isEmpty()) {
                inputTelephone.error = getString(R.string.error_telephone)
                return@setOnClickListener
            }

            val intent = Intent(this, ConfirmationActivity::class.java).apply {
                putExtra("nom", nom)
                putExtra("prenom", prenom)
                putExtra("age", age)
                putExtra("domaine", domaine)
                putExtra("pays", paysSelectionne)
                putExtra("codePays", codePays)
                putExtra("telephone", telephone)
            }
            startActivity(intent)
        }


    }
    private fun showConfirmationDialog(nom: String, prenom: String, age: String, domaine: String, pays: String, codePays: String, telephone: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmation))
        builder.setMessage(getString(R.string.confirm_message))

        // Bouton "Confirmer"
        builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->

            Toast.makeText(this, getString(R.string.toast_succes, nom, prenom, age, domaine, pays, codePays, telephone), Toast.LENGTH_LONG).show()
        }


        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()

            inputNom.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            inputPrenom.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            inputAge.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            inputCodePays.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            spinnerDomaine.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            spinnerPays.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
            inputTelephone.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun changeLanguage(lang: String) {
        LocaleHelper.setLocale(this, lang)
        recreate()
    }
}
