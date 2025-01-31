package com.example.tp1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)


        val numero = intent.getStringExtra("telephone") ?: ""
        val codePays = intent.getStringExtra("codePays") ?: ""


        val txtPhone = findViewById<TextView>(R.id.textPhone)
        txtPhone.text = "$codePays $numero"


        val btnCall = findViewById<Button>(R.id.btn_call)
        btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$codePays$numero")
            startActivity(intent)
        }
    }
}
