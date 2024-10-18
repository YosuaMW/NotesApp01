package com.example.notesapp

import Note
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotesAdapter(private var notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // ViewHolder class to hold individual note views
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.noteContent)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        // Handle edit button click
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            holder.itemView.context.startActivity(intent)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .collection("notes")
                    .document(note.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "Note deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        (notesList as MutableList).removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            holder.itemView.context,
                            "Failed to delete note",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    // Update the list when new data is available
    fun submitList(newNotes: List<Note>) {
        notesList = newNotes
        notifyDataSetChanged()
    }
}
