package com.example.vetmate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.databinding.ActivityReminderBinding;
import com.example.vetmate.ui.adapter.ReminderAdapter;
import com.example.vetmate.viewmodel.ReminderViewModel;

import java.util.ArrayList;

public class ReminderActivity extends AppCompatActivity {

    private ActivityReminderBinding binding;
    private ReminderViewModel viewModel;
    private ReminderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.fetchReminders();
        });

        // Initial fetch
        viewModel.fetchReminders();
        binding.fabAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(ReminderActivity.this, AddReminderActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new ReminderAdapter(new ArrayList<>(), new ReminderAdapter.ReminderActionListener() {
            @Override
            public void onReminderClicked(Reminder reminder) {
                // You can open edit screen in future here
                Toast.makeText(ReminderActivity.this, "Clicked: " + reminder.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReminderCompletionChanged(Reminder reminder, boolean isChecked) {
                viewModel.setReminderCompleted(reminder, isChecked);
            }
        },ReminderAdapter.ReminderLayoutType.FULL);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getReminders().observe(this, reminders -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (reminders != null && !reminders.isEmpty()) {
                adapter.updateReminders(reminders);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.swipeRefreshLayout.setRefreshing(isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
