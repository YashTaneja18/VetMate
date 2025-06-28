package com.example.vetmate.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.R;
import com.example.vetmate.data.model.Pet;

import java.util.List;

public class SelectPetAdapter extends RecyclerView.Adapter<SelectPetAdapter.PetViewHolder> {

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    private final List<Pet> petList;
    private final OnPetClickListener listener;

    public SelectPetAdapter(List<Pet> petList, OnPetClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_select, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.petNameText.setText(pet.getName());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPetClick(pet);
        });
    }

    @Override
    public int getItemCount() {
        return petList != null ? petList.size() : 0;
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petNameText;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petNameText = itemView.findViewById(R.id.petNameText);
        }
    }
}
