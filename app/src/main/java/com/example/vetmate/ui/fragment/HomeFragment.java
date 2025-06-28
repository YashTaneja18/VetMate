package com.example.vetmate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.databinding.FragmentHomeBinding;
import com.example.vetmate.ui.activity.ReminderActivity;
import com.example.vetmate.ui.adapter.ReminderAdapter;
import com.example.vetmate.viewmodel.ReminderViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ReminderViewModel reminderViewModel;
    private ReminderAdapter reminderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        // Setup RecyclerView for reminders
        reminderAdapter = new ReminderAdapter(new ArrayList<>(), new ReminderAdapter.ReminderActionListener() {
            @Override
            public void onReminderClicked(Reminder reminder) {
                // Optional: do something when clicked
            }
            @Override
            public void onReminderCompletionChanged(Reminder reminder, boolean isChecked) {
                reminderViewModel.setReminderCompleted(reminder, isChecked);
            }
        },ReminderAdapter.ReminderLayoutType.COMPACT);

        binding.recyclerReminders.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerReminders.setAdapter(reminderAdapter);

        // Observe today's reminders
        reminderViewModel.getReminders().observe(getViewLifecycleOwner(), reminders -> {
            if (reminders != null && !reminders.isEmpty()) {
                binding.recyclerReminders.setVisibility(View.VISIBLE);
                binding.textNoReminders.setVisibility(View.GONE);
                reminderAdapter.updateReminders(reminders);
            } else {
                binding.recyclerReminders.setVisibility(View.GONE);
                binding.textNoReminders.setVisibility(View.VISIBLE);
            }
        });

        // Fetch only today's reminders
        reminderViewModel.fetchRemindersForToday();

        // Handle "More ➔" click
        binding.textMoreReminders.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ReminderActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
