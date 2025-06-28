package com.example.vetmate.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.vetmate.R;
import com.example.vetmate.data.model.Pet;
import com.example.vetmate.data.repository.PetRepository;
import com.example.vetmate.databinding.ActivityAddPetBinding;
import com.example.vetmate.viewmodel.PetViewModel;
import com.google.firebase.storage.StorageReference;
public class AddPetActivity extends AppCompatActivity {

    private ActivityAddPetBinding binding;
    private PetViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_pet);
        viewModel = new ViewModelProvider(this).get(PetViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.btnSavePet.setOnClickListener(v -> {
            String name = binding.editPetName.getText().toString().trim();
            String species = binding.editSpecies.getText().toString().trim();
            String breed = binding.editBreed.getText().toString().trim();
            String gender = binding.editGender.getText().toString().trim();
            int age = TextUtils.isEmpty(binding.editAge.getText()) ? 0 : Integer.parseInt(binding.editAge.getText().toString());
            float height = TextUtils.isEmpty(binding.editHeight.getText()) ? 0 : Float.parseFloat(binding.editHeight.getText().toString());
            float weight = TextUtils.isEmpty(binding.editWeight.getText()) ? 0 : Float.parseFloat(binding.editWeight.getText().toString());

            Pet pet = new Pet(null, name, species, breed, gender, age, height, weight, null);
            pet.setLocalImageResId(R.drawable.placeholder_dog); // Use local image

            viewModel.addPet(pet, new PetRepository.OnPetAddListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddPetActivity.this, "Pet added", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(AddPetActivity.this, PetListActivity.class));
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddPetActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

}
