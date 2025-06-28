package com.example.vetmate.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetmate.data.model.Reminder;
import com.example.vetmate.databinding.ItemReminderBinding;
import com.example.vetmate.databinding.ItemReminderHomeBinding;
import com.example.vetmate.utils.TimeUtils;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    public enum ReminderLayoutType {
        FULL,      // item_reminder.xml
        COMPACT    // item_reminder_home.xml
    }

    public interface ReminderActionListener {
        void onReminderClicked(Reminder reminder);
        void onReminderCompletionChanged(Reminder reminder, boolean isChecked);
    }

    private List<Reminder> reminders;
    private final ReminderActionListener listener;
    private final ReminderLayoutType layoutType;

    public ReminderAdapter(List<Reminder> reminders, ReminderActionListener listener, ReminderLayoutType layoutType) {
        this.reminders = reminders;
        this.listener = listener;
        this.layoutType = layoutType;
    }

    public void updateReminders(List<Reminder> newList) {
        this.reminders = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (layoutType == ReminderLayoutType.COMPACT) {
            ItemReminderHomeBinding compactBinding = ItemReminderHomeBinding.inflate(inflater, parent, false);
            return new ReminderViewHolder(compactBinding);
        } else {
            ItemReminderBinding fullBinding = ItemReminderBinding.inflate(inflater, parent, false);
            return new ReminderViewHolder(fullBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.bind(reminders.get(position));
    }

    @Override
    public int getItemCount() {
        return reminders != null ? reminders.size() : 0;
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {

        private final ItemReminderBinding fullBinding;
        private final ItemReminderHomeBinding compactBinding;

        ReminderViewHolder(ItemReminderBinding binding) {
            super(binding.getRoot());
            this.fullBinding = binding;
            this.compactBinding = null;
        }

        ReminderViewHolder(ItemReminderHomeBinding binding) {
            super(binding.getRoot());
            this.compactBinding = binding;
            this.fullBinding = null;
        }

        void bind(Reminder reminder) {
            if (layoutType == ReminderLayoutType.FULL && fullBinding != null) {
                fullBinding.textReminderTitle.setText(reminder.getTitle());
                fullBinding.textReminderTime.setText(TimeUtils.formatTimestamp(reminder.getReminderTime()));
                fullBinding.textReminderType.setText(reminder.getType());
                fullBinding.textPetName.setText("For: " + reminder.getPetName());
                fullBinding.checkboxComplete.setChecked(reminder.isCompleted());

                fullBinding.checkboxComplete.setOnCheckedChangeListener((buttonView, isChecked) ->
                        listener.onReminderCompletionChanged(reminder, isChecked)
                );

                fullBinding.getRoot().setOnClickListener(v ->
                        listener.onReminderClicked(reminder)
                );

            } else if (layoutType == ReminderLayoutType.COMPACT && compactBinding != null) {
                compactBinding.textReminderTitle.setText(reminder.getTitle());
                compactBinding.textReminderTime.setText(TimeUtils.formatTimestamp(reminder.getReminderTime()));
                compactBinding.textPetName.setText("For: " + reminder.getPetName());

                compactBinding.getRoot().setOnClickListener(v ->
                        listener.onReminderClicked(reminder)
                );
            }
        }
    }
}
