package com.example.vetmate.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vetmate.data.model.Note;
import com.example.vetmate.data.model.NoteWithPet;
import com.example.vetmate.data.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository noteRepository;

    private final MutableLiveData<List<NoteWithPet>> notesWithPetLiveData = new MutableLiveData<>();
    private final MutableLiveData<Note> selectedNote = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository();
    }

    // Observables
    public LiveData<List<NoteWithPet>> getNotesWithPet() {
        return notesWithPetLiveData;
    }

    public LiveData<Note> getSelectedNote() {
        return selectedNote;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Operations
    public void selectNote(Note note) {
        selectedNote.setValue(note);
    }

    public void fetchAllNotesWithPetNames() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        noteRepository.fetchAllNotesWithPetNames(new NoteRepository.NoteWithPetListCallback() {
            @Override
            public void onSuccess(List<NoteWithPet> notes) {
                isLoading.setValue(false);
                notesWithPetLiveData.setValue(notes);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void addNote(String petId, String title, String body) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        Note note = new Note();
        note.setTitle(title);
        note.setBody(body);
        note.setPetId(petId); // Make sure to set this so `NoteWithPet` works

        noteRepository.addNote(petId, note, new NoteRepository.NoteCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchAllNotesWithPetNames(); // Refresh after add
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void updateNote(String petId, String noteId, String title, String body) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        Note note = new Note();
        note.setNoteId(noteId);
        note.setPetId(petId);
        note.setTitle(title);
        note.setBody(body);

        noteRepository.updateNote(petId, note, new NoteRepository.NoteCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchAllNotesWithPetNames(); // Refresh after update
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }


    public void deleteNote(String petId, String noteId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        noteRepository.deleteNote(petId, noteId, new NoteRepository.NoteCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchAllNotesWithPetNames(); // Refresh after delete
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void getNoteById(String petId, String noteId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        noteRepository.getNoteById(petId, noteId, new NoteRepository.SingleNoteCallback() {
            @Override
            public void onSuccess(Note note) {
                isLoading.setValue(false);
                selectedNote.setValue(note);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
}
