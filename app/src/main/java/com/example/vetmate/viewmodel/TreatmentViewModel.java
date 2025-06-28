package com.example.vetmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Treatment;
import com.example.vetmate.data.repository.TreatmentRepository;

import java.util.List;

public class TreatmentViewModel extends AndroidViewModel {

    private final TreatmentRepository treatmentRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public TreatmentViewModel(@NonNull Application application) {
        super(application);
        treatmentRepository = new TreatmentRepository();
        isLoading.setValue(false);
    }

    public LiveData<List<Treatment>> getTreatments() {
        return treatmentRepository.getTreatments();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    private void setError(String message) {
        errorMessage.setValue(message);
    }

    private void clearError() {
        errorMessage.setValue(null);
    }

    public void loadTreatments(String petId) {
        setLoading(true);
        clearError();

        treatmentRepository.loadTreatments(petId);
        setLoading(false);
    }

    public void addTreatment(String petId, Treatment treatment, TreatmentRepository.AddTreatmentCallback callback) {
        setLoading(true);
        clearError();

        treatmentRepository.addTreatment(petId, treatment, new TreatmentRepository.AddTreatmentCallback() {
            @Override
            public void onSuccess() {
                setLoading(false);
                callback.onSuccess();
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                setError(error);
                callback.onError(error);
            }
        });
    }
}
