package com.example.vetmate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetmate.data.model.NoteWithPet;
import com.example.vetmate.databinding.FragmentNotesBinding;
import com.example.vetmate.ui.activity.NoteDetailsActivity;
import com.example.vetmate.ui.adapter.NotesAdapter;
import com.example.vetmate.viewmodel.NoteViewModel;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private NoteViewModel noteViewModel;
    private NotesAdapter notesAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Setup RecyclerView
        notesAdapter = new NotesAdapter(new ArrayList<>(), this::onNoteClicked);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(notesAdapter);

        // Observe LiveData
        noteViewModel.getNotesWithPet().observe(getViewLifecycleOwner(), notes -> {
            if (notes != null && !notes.isEmpty()) {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyStateLayout.setVisibility(View.GONE);
                notesAdapter.updateNotes(notes);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
            }
        });

        noteViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        noteViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // You may show a Snackbar or Toast here
            }
        });

        // Load notes
        noteViewModel.fetchAllNotesWithPetNames();

        // FAB click: open empty NoteDetailsActivity for adding
        binding.fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NoteDetailsActivity.class);
            startActivity(intent);
        });
    }

    private void onNoteClicked(NoteWithPet note) {
        Intent intent = new Intent(requireContext(), NoteDetailsActivity.class);
        intent.putExtra(NoteDetailsActivity.EXTRA_NOTE_ID, note.getNoteId());
        intent.putExtra(NoteDetailsActivity.EXTRA_PET_ID, note.getPetId());
        intent.putExtra(NoteDetailsActivity.EXTRA_PET_NAME, note.getPetName());
        intent.putExtra("NOTE_TITLE", note.getTitle());
        intent.putExtra("NOTE_BODY", note.getBody());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        noteViewModel.fetchAllNotesWithPetNames(); // 🔄 Refresh the list when fragment resumes
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
