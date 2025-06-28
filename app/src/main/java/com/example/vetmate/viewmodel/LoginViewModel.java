package com.example.vetmate.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.vetmate.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository = new AuthRepository();

    public boolean isUserSignedIn() {
        return authRepository.isUserSignedIn();
    }

    public void saveUserToFirestore(FirebaseUser user) {
        authRepository.saveUserToFirestore(user);
    }

    public FirebaseAuth getAuth() {
        return authRepository.getAuth();
    }
}
