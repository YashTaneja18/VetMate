package com.example.vetmate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.vetmate.R;
import com.example.vetmate.data.model.Pet;
import com.example.vetmate.databinding.ActivityPetDetailsBinding;
import com.example.vetmate.viewmodel.PetViewModel;

public class PetDetailsActivity extends AppCompatActivity {

    private ActivityPetDetailsBinding binding;
    private PetViewModel viewModel;
    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PetViewModel.class);

        // Get pet object from intent
        pet = (Pet) getIntent().getSerializableExtra("pet");

        if (pet == null) {
            Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populatePetDetails();

//        binding.btnEditPet.setOnClickListener(v -> {
//            Intent intent = new Intent(this, EditPetActivity.class);
//            intent.putExtra("pet", (CharSequence) pet);
//            startActivity(intent);
//        });
//
//        binding.btnDeletePet.setOnClickListener(v -> {
//            viewModel.deletePet(pet.getId());
//            Toast.makeText(this, "Pet deleted", Toast.LENGTH_SHORT).show();
//            finish();
//        });
//
//        binding.btnHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(this, TreatmentHistoryActivity.class);
//            intent.putExtra("petId", pet.getId());
//            startActivity(intent);
//        });
//
//        binding.btnViewNotes.setOnClickListener(v -> {
//            Intent intent = new Intent(this, NotesActivity.class);
//            intent.putExtra("petId", pet.getId());
//            startActivity(intent);
//        });
//
//        binding.btnViewReminders.setOnClickListener(v -> {
//            Intent intent = new Intent(this, RemindersActivity.class);
//            intent.putExtra("petId", pet.getId());
//            startActivity(intent);
//        });
    }

    private void populatePetDetails() {
        binding.petName.setText(pet.getName());
        binding.petBreed.setText("Breed: " + pet.getBreed());
        binding.petGender.setText("Gender: " + pet.getGender());
        binding.petSpecies.setText("Species: " + pet.getSpecies());
        binding.petAge.setText("Age: " + pet.getAge() + " years");
        binding.petHeight.setText("Height: " + pet.getHeight() + " cm");
        binding.petWeight.setText("Weight: " + pet.getWeight() + " kg");

        Glide.with(this)
                .load(pet.getPhotoUri() != null && !pet.getPhotoUri().isEmpty() ? pet.getPhotoUri() : R.drawable.placeholder_dog)
                .into(binding.petImage);
    }
}
