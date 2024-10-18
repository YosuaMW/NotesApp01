package com.example.notesapp

import Note
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)

        noteId = intent.getStringExtra("noteId")

        if (noteId != null) {
            loadNote() // Load existing note if noteId is provided
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            updateNote()
        }
    }

    private fun loadNote() {
        val userId = auth.currentUser?.uid
        if (userId != null && noteId != null) {
            db.collection("users").document(userId).collection("notes").document(noteId!!)
                .get()
                .addOnSuccessListener { document ->
                    val note = document.toObject(Note::class.java)
                    note?.let {
                        titleEditText.setText(it.title)
                        contentEditText.setText(it.content)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: No note found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNote() {
        val title = titleEditText.text.toString()
        val content = contentEditText.text.toString()

        // Check if title or content is empty
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId != null && noteId != null) {
            val updatedNote = mapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("users").document(userId).collection("notes").document(noteId!!)
                .update(updatedNote)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, NotesActivity::class.java))
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: Unable to update note", Toast.LENGTH_SHORT).show()
        }
    }
}
