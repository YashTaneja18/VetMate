package com.example.vetmate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.R;
import com.example.vetmate.databinding.FragmentPetsBinding;
import com.example.vetmate.ui.activity.AddPetActivity;
import com.example.vetmate.ui.activity.PetDetailsActivity;
import com.example.vetmate.ui.adapter.PetAdapter;
import com.example.vetmate.viewmodel.PetViewModel;

import java.util.ArrayList;

public class PetsFragment extends Fragment {

    private FragmentPetsBinding binding;
    private PetViewModel viewModel;
    private PetAdapter petAdapter;

    public PetsFragment() {
        // Required empty public constructor
    }

    public static PetsFragment newInstance() {
        return new PetsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PetViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pets, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        // Setup RecyclerView with Grid Layout
        RecyclerView recyclerView = binding.petRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter with click listener
        petAdapter = new PetAdapter(new ArrayList<>(), pet -> {
            // Navigate to Pet Details
            Intent intent = new Intent(getActivity(), PetDetailsActivity.class);
            intent.putExtra("pet", pet);
            startActivity(intent);
        });

        recyclerView.setAdapter(petAdapter);
    }

    private void setupClickListeners() {
        // Add Pet button click listener
        binding.addPetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddPetActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        // Observe pets list from ViewModel
        viewModel.getPets().observe(getViewLifecycleOwner(), pets -> {
            if (pets != null) {
                petAdapter.updateList(pets);

                // Show/hide empty state
                if (pets.isEmpty()) {
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.petRecyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    binding.petRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // You can show a Snackbar or Toast here
                // For now, we'll keep it simple
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh pets list when fragment becomes visible again
        // This will reload pets if any were added/modified
        if (viewModel != null) {
            viewModel.refreshPets();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}