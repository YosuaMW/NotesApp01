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

class AddNoteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText

    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        db = FirebaseFirestore.getInstance()
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)

        noteId = intent.getStringExtra("noteId")
        if (noteId != null) {
            loadNoteData(noteId!!)
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            if (noteId != null) {
                updateNote()
            } else {
                saveNote()
            }
        }
    }

    private fun loadNoteData(noteId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("notes").document(noteId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val note = document.toObject(Note::class.java)
                        if (note != null) {
                            titleEditText.setText(note.title)
                            contentEditText.setText(note.content)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val newNoteRef = db.collection("users").document(userId)
                .collection("notes").document()

            val note = Note(
                id = newNoteRef.id,
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            newNoteRef.set(note)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()

                    // Mengirim hasil sukses ke NotesActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("noteAdded", true)
                    setResult(RESULT_OK, resultIntent)

                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNote() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && noteId != null) {
            val noteRef = db.collection("users").document(userId)
                .collection("notes").document(noteId!!)

            val updatedNote = mapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            noteRef.update(updatedNote)
                .addOnSuccessListener {
                    Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()

                    // Mengirim hasil sukses ke NotesActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("noteUpdated", true)
                    setResult(RESULT_OK, resultIntent)

                    finish() // Tutup AddNoteActivity
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: User not authenticated or note not found", Toast.LENGTH_SHORT).show()
        }
    }
}