package com.example.tp1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }



        val nom = intent.getStringExtra("nom")
        val prenom = intent.getStringExtra("prenom")
        val age = intent.getStringExtra("age")
        val domaine = intent.getStringExtra("domaine")
        val pays = intent.getStringExtra("pays")
        val codePays = intent.getStringExtra("codePays")
        val telephone = intent.getStringExtra("telephone")



        val confirmationText = """
            ${getString(R.string.confirmation_message)}

            ${getString(R.string.label_nom)} $nom
            ${getString(R.string.label_prenom)} $prenom
            ${getString(R.string.label_age)} $age
            ${getString(R.string.label_domaine)} $domaine
            ${getString(R.string.label_pays)} $pays
            ${getString(R.string.label_telephone)} $codePays $telephone
        """.trimIndent()



        findViewById<TextView>(R.id.textConfirmation).text =
            getString(R.string.confirmation_message) + "\n\n" +
                    getString(R.string.label_nom) + " " + nom + "\n" +
                    getString(R.string.label_prenom) + " " + prenom + "\n" +
                    getString(R.string.label_age) + " " + age + "\n" +
                    getString(R.string.label_domaine) + " " + domaine + "\n" +
                    getString(R.string.label_pays) + " " + pays + "\n" +
                    getString(R.string.label_telephone) + " " + codePays + " " + telephone

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
}
