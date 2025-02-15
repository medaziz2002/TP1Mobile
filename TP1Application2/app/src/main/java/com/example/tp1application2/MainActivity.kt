package com.example.tp1application2

import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import com.example.tp1application2.EventAdapter
import com.example.tp1application2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewEvents: RecyclerView
    private lateinit var btnAddEvent: FloatingActionButton
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var selectedDate: String = ""
    private var eventList = mutableListOf<String>()
    private lateinit var eventAdapter: EventAdapter
    private lateinit var mainLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents)
        btnAddEvent = findViewById(R.id.btnAddEvent)
        mainLayout = findViewById(R.id.mainLayout)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dateFormat.format(Date())

        recyclerViewEvents.layoutManager = LinearLayoutManager(this)


        eventAdapter = EventAdapter(eventList, ::onEditEvent, ::onDeleteEvent)
        recyclerViewEvents.adapter = eventAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            loadEvents()

        }


        btnAddEvent.setOnClickListener {
            showAddEventDialog()
        }

        loadEvents()
    }

    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_event, null)

        val editEvent = dialogView.findViewById<EditText>(R.id.editEvent)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        builder.setView(dialogView)
        val dialog = builder.create()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            mainLayout.setRenderEffect(blurEffect)
        }

        btnAdd.setOnClickListener {
            val newEvent = editEvent.text.toString().trim()
            if (newEvent.isNotEmpty()) {
                saveEventToFirebase(newEvent)
                dialog.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.erreur_evenement), Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mainLayout.setRenderEffect(null)
            }
        }

        dialog.show()
    }

    private fun saveEventToFirebase(event: String) {
        val user = auth.currentUser ?: return

        val eventRef = db.collection("users").document(user.uid)
            .collection("events").document(selectedDate)

        eventRef.get().addOnSuccessListener { document ->
            val events = document.get("events") as? MutableList<String> ?: mutableListOf()
            events.add(event)

            eventRef.set(mapOf("events" to events)).addOnSuccessListener {
                loadEvents()
            }
        }
    }



    private fun loadEvents() {
        val user = auth.currentUser ?: return

        val eventRef = db.collection("users").document(user.uid)
            .collection("events").document(selectedDate)

        eventRef.get().addOnSuccessListener { document ->
            eventList = (document.get("events") as? MutableList<String>) ?: mutableListOf()
            eventAdapter.updateList(eventList)
        }
    }


    private fun onEditEvent(event: String, position: Int) {
        // Ouvrir le dialog pour modifier l'événement
        showEditEventDialog(event, position)
    }



    private fun onDeleteEvent(position: Int) {
        val eventToDelete = eventList[position]
        val user = auth.currentUser ?: return
        val eventRef = db.collection("users").document(user.uid)
            .collection("events").document(selectedDate)

        eventRef.get().addOnSuccessListener { document ->
            val events = document.get("events") as? MutableList<String> ?: mutableListOf()
            events.remove(eventToDelete)
            eventRef.set(mapOf("events" to events)).addOnSuccessListener {
                loadEvents()
                Toast.makeText(this, getString(R.string.event_deleted), Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun showEditEventDialog(event: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_event, null)

        val editEvent = dialogView.findViewById<EditText>(R.id.editEvent)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)


        editEvent.setText(event)

        builder.setView(dialogView)
        val dialog = builder.create()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            mainLayout.setRenderEffect(blurEffect)
        }

        btnAdd.setOnClickListener {
            val updatedEvent = editEvent.text.toString().trim()
            if (updatedEvent.isNotEmpty()) {
                updateEventInFirebase(updatedEvent, position)
                dialog.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.erreur_evenement), Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mainLayout.setRenderEffect(null)
            }
        }

        dialog.show()
    }



    private fun updateEventInFirebase(updatedEvent: String, position: Int) {
        val user = auth.currentUser ?: return
        val eventRef = db.collection("users").document(user.uid)
            .collection("events").document(selectedDate)

        eventRef.get().addOnSuccessListener { document ->
            val events = document.get("events") as? MutableList<String> ?: mutableListOf()

            // Remplacer l'événement existant par le nouvel événement
            events[position] = updatedEvent

            eventRef.set(mapOf("events" to events)).addOnSuccessListener {
                loadEvents()
                Toast.makeText(this, getString(R.string.event_updated), Toast.LENGTH_SHORT).show()

            }
        }
    }





}
