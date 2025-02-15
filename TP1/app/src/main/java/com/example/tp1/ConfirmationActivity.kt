package com.example.tp1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tp1.helpers.LocaleHelper
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConfirmationActivity : AppCompatActivity() {


    private lateinit var inputNom: EditText
    private lateinit var inputPrenom: EditText
    private lateinit var inputAge: EditText
    private lateinit var spinnerDomaine: EditText
    private lateinit var spinnerPays: EditText
    private lateinit var inputCodePays: EditText
    private lateinit var inputTelephone: EditText
    private lateinit var navigationView: NavigationView
    private lateinit var btnMenu: Button
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.data)
        inputNom = findViewById(R.id.input_nom)
        inputPrenom = findViewById(R.id.input_prenom)
        inputAge = findViewById(R.id.input_age)
        spinnerDomaine = findViewById(R.id.spinner_domaine)
        spinnerPays = findViewById(R.id.spinner_pays)
        inputCodePays = findViewById(R.id.input_code_pays)
        inputTelephone = findViewById(R.id.input_telephone)
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenu = findViewById(R.id.btn_menu) // Ajouté pour lier btnMenu

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

        val nom = intent.getStringExtra("nom")
        val prenom = intent.getStringExtra("prenom")
        val age = intent.getStringExtra("age")
        val domaine = intent.getStringExtra("domaine")
        val pays = intent.getStringExtra("pays")
        val codePays = intent.getStringExtra("codePays")
        val telephone = intent.getStringExtra("telephone")


        inputNom.setText(nom)
        inputPrenom.setText(prenom)
        inputAge.setText(age)
        inputCodePays.setText(codePays)
        inputTelephone.setText(telephone)
        spinnerDomaine.setText(domaine)
        spinnerPays.setText(pays)



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }






        val confirmationText = """
            ${getString(R.string.confirmation_message)}

            ${getString(R.string.label_nom)} $nom
            ${getString(R.string.label_prenom)} $prenom
            ${getString(R.string.label_age)} $age
            ${getString(R.string.label_domaine)} $domaine
            ${getString(R.string.label_pays)} $pays
            ${getString(R.string.label_telephone)} $codePays $telephone
        """.trimIndent()





        val btnOk = findViewById<Button>(R.id.btn_ok)
        val btnRetour = findViewById<Button>(R.id.btn_retour)
        val btnImprimer = findViewById<Button>(R.id.btn_imprimer)


        btnOk.text = getString(R.string.ok)
        btnOk.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("telephone", telephone)
            intent.putExtra("codePays", codePays)
            startActivity(intent)
        }

        btnImprimer.text = getString(R.string.imprimer_pdf)
        btnImprimer.setOnClickListener {
            generatePdf(confirmationText)
        }

        btnRetour.text = getString(R.string.retour)
        btnRetour.setOnClickListener {
            finish()
        }
    }
    private fun generatePdf(text: String) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()
        paint.textSize = 14f
        paint.color = android.graphics.Color.BLACK

        val lines = text.split("\n")
        var yPosition = 50f

        for (line in lines) {
            canvas.drawText(line, 40f, yPosition, paint)
            yPosition += 30f
        }

        document.finishPage(page)


        val fileName = "Confirmation_${System.currentTimeMillis()}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filePath = File(downloadsDir, fileName)

        try {
            document.writeTo(FileOutputStream(filePath))
            document.close()
            showToast(getString(R.string.pdf_saved) + " " + filePath.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
            showToast(getString(R.string.pdf_error))
        }
    }


    private fun showToast(message: String) {
        runOnUiThread {
            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
        }
    }


    private fun changeLanguage(lang: String) {
        LocaleHelper.setLocale(this, lang)
        recreate()
    }
}
