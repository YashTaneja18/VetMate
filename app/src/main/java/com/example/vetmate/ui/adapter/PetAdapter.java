package com.example.vetmate.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vetmate.R;
import com.example.vetmate.data.model.Pet;
import com.example.vetmate.data.repository.PetRepository;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;

    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }

    private final OnItemClickListener listener;

    public PetAdapter(List<Pet> petList, OnItemClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }
    public void updateList(List<Pet> newPetList) {
        this.petList = newPetList;
        notifyDataSetChanged(); // or use DiffUtil for better performance
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.bind(pet, listener);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petNameText, petBreedText;
        ImageView petImage;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petNameText = itemView.findViewById(R.id.petNameText);
            petBreedText = itemView.findViewById(R.id.petBreedText);
            petImage = itemView.findViewById(R.id.petImage); // placeholder image
        }

        public void bind(Pet pet, OnItemClickListener listener) {
        petNameText.setText(pet.getName());
        petBreedText.setText(pet.getBreed());
        petImage.setImageResource(pet.getLocalImageResId()); // or Glide later
        itemView.setOnClickListener(v -> listener.onItemClick(pet));
    }
    }
}
