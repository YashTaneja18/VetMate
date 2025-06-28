package com.example.vetmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Pet;
import com.example.vetmate.data.repository.PetRepository;

import java.util.List;

public class PetViewModel extends AndroidViewModel {

    private final PetRepository petRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<Pet> selectedPet = new MutableLiveData<>();

    public PetViewModel(@NonNull Application application) {
        super(application);
        petRepository = new PetRepository();
    }

    // All Pets
    public LiveData<List<Pet>> getPets() {
        return petRepository.getPets();
    }

    // UI state observers
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Selected pet (used for edit/detail views)
    public LiveData<Pet> getSelectedPet() {
        return selectedPet;
    }

    public void selectPet(Pet pet) {
        selectedPet.setValue(pet);
    }

    // Refresh the list of pets from Firestore
    public void refreshPets() {
        setLoading(true);
        clearErrorMessage();

        petRepository.refreshPets(new PetRepository.RefreshCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                setErrorMessage(error);
            }
        });
    }

    // Add a new pet
    public void addPet(Pet pet, PetRepository.OnPetAddListener callback) {
        setLoading(true);
        clearErrorMessage();

        petRepository.addPet(pet, new PetRepository.OnPetAddListener() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                setErrorMessage(e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    // Update an existing pet
    public void updatePet(Pet pet, PetRepository.OnPetUpdateListener callback) {
        setLoading(true);
        clearErrorMessage();

        petRepository.updatePet(pet, new PetRepository.OnPetUpdateListener() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                setErrorMessage(e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    // Delete a pet
    public void deletePet(String petId, PetRepository.OnPetDeleteListener callback) {
        setLoading(true);
        clearErrorMessage();

        petRepository.deletePet(petId, new PetRepository.OnPetDeleteListener() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                setErrorMessage(e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    // UI state helpers
    private void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    private void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    private void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up if necessary (e.g., detach listeners)
    }
}
