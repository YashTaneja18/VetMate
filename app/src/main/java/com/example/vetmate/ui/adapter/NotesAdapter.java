package com.example.vetmate.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.R;
import com.example.vetmate.data.model.NoteWithPet;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onNoteClick(NoteWithPet note);
    }

    private List<NoteWithPet> noteList;
    private final OnNoteClickListener listener;

    public NotesAdapter(List<NoteWithPet> noteList, OnNoteClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    public void updateNotes(List<NoteWithPet> notes) {
        this.noteList = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteWithPet note = noteList.get(position);

        holder.titleText.setText(note.getTitle());
        holder.petNameText.setText("for " + note.getPetName());

        if (note.getLastModified() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.dateText.setText(sdf.format(note.getLastModified().toDate()));
        } else {
            holder.dateText.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, petNameText, dateText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.noteTitleText);
            petNameText = itemView.findViewById(R.id.notePetNameText);
            dateText = itemView.findViewById(R.id.noteDateText);
        }
    }
}
