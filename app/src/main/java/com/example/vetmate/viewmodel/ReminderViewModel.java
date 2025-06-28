package com.example.vetmate.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.data.repository.ReminderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

public class ReminderViewModel extends ViewModel {

    private final ReminderRepository repository = new ReminderRepository();

    private final MutableLiveData<List<Reminder>> remindersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    public LiveData<List<Reminder>> getReminders() {
        return remindersLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Fetch all reminders for current user
    public void fetchReminders() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.fetchAllReminders(new ReminderRepository.ReminderListCallback() {
            @Override
            public void onSuccess(List<Reminder> reminders) {
                isLoading.setValue(false);
                remindersLiveData.setValue(reminders);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    // Add a reminder
    public void addReminder(Reminder reminder, Context context) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.addReminder(reminder, context, new ReminderRepository.ReminderCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchReminders(); // Refresh
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                ReminderViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    // Update a reminder
    public void updateReminder(Reminder reminder) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.updateReminder(reminder, new ReminderRepository.ReminderCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchReminders(); // Refresh
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                ReminderViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    // Delete a reminder
    public void deleteReminder(Reminder reminder) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.deleteReminder(reminder, new ReminderRepository.ReminderCallback() {
            @Override
            public void onSuccess() {
                isLoading.setValue(false);
                fetchReminders(); // Refresh
            }

            @Override
            public void onFailure(String errorMessage) {
                isLoading.setValue(false);
                ReminderViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    // Toggle complete status
    public void setReminderCompleted(Reminder reminder, boolean isCompleted) {
        reminder.setCompleted(isCompleted);
        updateReminder(reminder); // Reuse update logic
    }
    public void fetchRemindersForToday() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.fetchAllReminders(new ReminderRepository.ReminderListCallback() {
            @Override
            public void onSuccess(List<Reminder> allReminders) {
                isLoading.setValue(false);

                // Filter reminders for today
                List<Reminder> todaysReminders = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();

                // Start of today
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date startOfDay = calendar.getTime();

                // End of today
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endOfDay = calendar.getTime();

                for (Reminder reminder : allReminders) {
                    if (reminder.getReminderTime() != null) {
                        Date reminderDate = reminder.getReminderTime().toDate();
                        if (!reminderDate.before(startOfDay) && !reminderDate.after(endOfDay)) {
                            todaysReminders.add(reminder);
                        }
                    }
                }

                remindersLiveData.setValue(todaysReminders);
            }

            @Override
            public void onFailure(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public String getUserId() {
        return repository.getUserId();
    }
}
