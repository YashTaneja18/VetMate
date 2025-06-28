package com.example.vetmate.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectPetDialogFragment extends DialogFragment {

    public interface OnPetSelectedListener {
        void onPetSelected(String petId, String petName);
    }

    private OnPetSelectedListener listener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPetSelectedListener) {
            listener = (OnPetSelectedListener) context;
        } else if (getParentFragment() instanceof OnPetSelectedListener) {
            listener = (OnPetSelectedListener) getParentFragment();
        } else {
            throw new ClassCastException("Host must implement OnPetSelectedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_pet, null);
        RecyclerView recyclerView = view.findViewById(R.id.petRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PetAdapter adapter = new PetAdapter();
        recyclerView.setAdapter(adapter);

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        db.collection("users")
                .document(uid)
                .collection("pets")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<PetItem> petList = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        if (name != null) {
                            petList.add(new PetItem(id, name));
                        }
                    }
                    adapter.setPetList(petList);
                })
                .addOnFailureListener(e -> {
                    Log.e("SelectPetDialog", "Error fetching pets", e);
                    Toast.makeText(getContext(), "Failed to fetch pets", Toast.LENGTH_SHORT).show();
                    dismiss();
                });

        return new AlertDialog.Builder(requireContext())
                .setTitle("Select Pet")
                .setView(view)
                .create();
    }

    private class PetItem {
        String id, name;
        PetItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private class PetAdapter extends RecyclerView.Adapter<PetViewHolder> {

        private List<PetItem> petList = new ArrayList<>();

        public void setPetList(List<PetItem> petList) {
            this.petList = petList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pet_select, parent, false);
            return new PetViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
            PetItem item = petList.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return petList.size();
        }
    }

    private class PetViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.petNameText);
        }

        public void bind(PetItem pet) {
            nameText.setText(pet.name);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPetSelected(pet.id, pet.name);
                }
                dismiss();
            });
        }
    }
}
