package com.example.vetmate.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vetmate.R;
import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.databinding.ActivityAddReminderBinding;
import com.example.vetmate.ui.dialog.SelectPetDialogFragment;
import com.example.vetmate.viewmodel.ReminderViewModel;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity implements SelectPetDialogFragment.OnPetSelectedListener {

    private ActivityAddReminderBinding binding;
    private ReminderViewModel viewModel;

    private String selectedPetId = null;
    private String selectedPetName = null;
    private Timestamp selectedTimestamp = null;

    private final Calendar calendar = Calendar.getInstance();

    private final String[] reminderTypes = {"Appointment", "Medication", "Deworming", "Vaccination", "Grooming", "Supplies Restock", "Outing"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        setupListeners();
        setupTypeSpinner();
    }

    private void setupListeners() {
        binding.layoutSelectPet.setOnClickListener(v -> showPetPicker());
        binding.layoutSelectDateTime.setOnClickListener(v -> showDateTimePicker());

        binding.fabSaveReminder.setOnClickListener(v -> {
            if (validateInput()) {
                Reminder reminder = new Reminder();
                reminder.setPetId(selectedPetId);
                reminder.setPetName(selectedPetName);
                reminder.setUserId(viewModel.getUserId());
                reminder.setTitle(binding.editTitle.getText().toString().trim());
                reminder.setDescription(binding.editDescription.getText().toString().trim());
                reminder.setReminderTime(selectedTimestamp);
                reminder.setType(binding.spinnerReminderType.getSelectedItem().toString());
                reminder.setCompleted(false);

                viewModel.addReminder(reminder,this);

                Toast.makeText(this, "Reminder added", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });
    }

    private void setupTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reminderTypes);
        binding.spinnerReminderType.setAdapter(adapter);
    }

    private void showPetPicker() {
        SelectPetDialogFragment dialog = new SelectPetDialogFragment();
        dialog.show(getSupportFragmentManager(), "SelectPet");
    }

    private void showDateTimePicker() {
        // First show date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            // Then show time picker
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Date selectedDate = calendar.getTime();
                selectedTimestamp = new Timestamp(selectedDate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                binding.textSelectedDateTime.setText(sdf.format(selectedDate));

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private boolean validateInput() {
        if (selectedPetId == null) {
            Toast.makeText(this, "Please select a pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.editTitle.getText().toString().trim().isEmpty()) {
            binding.editTitle.setError("Title required");
            return false;
        }

        if (selectedTimestamp == null) {
            Toast.makeText(this, "Please select reminder time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onPetSelected(String petId, String petName) {
        this.selectedPetId = petId;
        this.selectedPetName = petName;
        binding.textSelectedPet.setText(petName);
    }
}
