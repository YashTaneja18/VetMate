package com.example.vetmate.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vetmate.data.model.Note;
import com.example.vetmate.databinding.ActivityNoteDetailsBinding;
import com.example.vetmate.ui.dialog.SelectPetDialogFragment;
import com.example.vetmate.viewmodel.NoteViewModel;

public class NoteDetailsActivity extends AppCompatActivity implements SelectPetDialogFragment.OnPetSelectedListener {

    public static final String EXTRA_NOTE_ID = "NOTE_ID";
    public static final String EXTRA_PET_ID = "PET_ID";
    public static final String EXTRA_PET_NAME = "PET_NAME";

    private ActivityNoteDetailsBinding binding;
    private NoteViewModel viewModel;

    private String noteId = null;   // null = new note
    private String petId = null;
    private String petName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Get data from intent (if editing)
        noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);
        petId = getIntent().getStringExtra(EXTRA_PET_ID);
        petName = getIntent().getStringExtra(EXTRA_PET_NAME);

        // Set initial pet name if available
        if (!TextUtils.isEmpty(petName)) {
            binding.petNameText.setText("for " + petName);
        }

        // Set click to open pet selection dialog
        binding.petNameText.setOnClickListener(v -> {
            SelectPetDialogFragment dialog = new SelectPetDialogFragment();
            dialog.show(getSupportFragmentManager(), "SelectPetDialog");
        });

        // Observe selected note if editing
        if (!TextUtils.isEmpty(noteId)) {
            // Fill directly since we already have the note info passed via Intent
            binding.titleEditText.setText(getIntent().getStringExtra("NOTE_TITLE"));
            binding.bodyEditText.setText(getIntent().getStringExtra("NOTE_BODY"));
        }

        // Save button
        binding.saveFab.setOnClickListener(v -> {
            String title = binding.titleEditText.getText().toString().trim();
            String body = binding.bodyEditText.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(petId)) {
                Toast.makeText(this, "Please select a pet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(noteId)) {
                // Add mode
                viewModel.addNote(petId, title, body);
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            } else {
                // Update mode
                viewModel.updateNote(petId, noteId, title, body);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }

    @Override
    public void onPetSelected(String selectedPetId, String selectedPetName) {
        this.petId = selectedPetId;
        this.petName = selectedPetName;
        binding.petNameText.setText("for " + petName);
    }
}
