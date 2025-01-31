package com.example.tp1application2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var listViewEvents: ListView
    private lateinit var editEvent: EditText
    private lateinit var btnAddEvent: Button
    private val eventsMap = mutableMapOf<String, MutableList<String>>()
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calendarView = findViewById(R.id.calendarView)
        listViewEvents = findViewById(R.id.listViewEvents)
        editEvent = findViewById(R.id.editEvent)
        btnAddEvent = findViewById(R.id.btnAddEvent)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


        selectedDate = dateFormat.format(Date())

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            updateEventList()
        }
        btnAddEvent.setOnClickListener {
            val newEvent = editEvent.text.toString().trim()
            if (newEvent.isNotEmpty()) {
                eventsMap.getOrPut(selectedDate) { mutableListOf() }.add(newEvent)
                editEvent.text.clear()
                updateEventList()
            } else {
                Toast.makeText(this, getString(R.string.erreur_evenement), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateEventList() {
        val events = eventsMap[selectedDate] ?: listOf(getString(R.string.pas_evenement))
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, events)
        listViewEvents.adapter = adapter
    }
}