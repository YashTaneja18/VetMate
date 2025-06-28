package com.example.vetmate.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void saveUserToFirestore(FirebaseUser user) {
        if (user == null) return;

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("email", user.getEmail());
        userData.put("name", user.getDisplayName());

        firestore.collection("users").document(user.getUid()).set(userData);
    }

    public FirebaseAuth getAuth() {
        return firebaseAuth;
    }
}
