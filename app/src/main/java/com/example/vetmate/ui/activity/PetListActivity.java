package com.example.vetmate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.R;
import com.example.vetmate.databinding.ActivityPetListBinding;
import com.example.vetmate.viewmodel.PetViewModel;
import com.google.firebase.firestore.*;
import com.example.vetmate.data.model.Pet;
import com.example.vetmate.ui.adapter.PetAdapter;

import java.util.ArrayList;
import java.util.List;

public class PetListActivity extends AppCompatActivity {

    private RecyclerView petRecyclerView;
    private PetAdapter petAdapter;
    private final List<Pet> petList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPetListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_list);

        PetViewModel viewModel = new ViewModelProvider(this).get(PetViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        RecyclerView recyclerView = binding.petRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        PetAdapter adapter = new PetAdapter(new ArrayList<>(), pet -> {
            // TODO: Handle click
            Intent intent = new Intent(this, PetDetailsActivity.class);
            intent.putExtra("pet", pet);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        viewModel.getPets().observe(this, adapter::updateList);

        binding.addPetButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddPetActivity.class));
        });
    }


    private void loadPetsFromFirestore() {
        db.collection("pets")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        petList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Pet pet = doc.toObject(Pet.class);
                            petList.add(pet);
                        }
                        petAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting pets", task.getException());
                    }
                });
    }
}
