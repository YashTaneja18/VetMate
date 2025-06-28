package com.example.vetmate;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.vetmate.databinding.ActivityMainBinding;
import com.example.vetmate.ui.activity.LoginActivity;
import com.example.vetmate.ui.fragment.AppointmentsFragment;
import com.example.vetmate.ui.fragment.HomeFragment;
import com.example.vetmate.ui.fragment.PetsFragment;
import com.example.vetmate.ui.fragment.NotesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.navigation.NavController;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth auth;
    private FirebaseUser user;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // Prompt user once
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }


        setupDrawer();
        setupTopAppBar();
        setupBottomNav();

        // Load Home fragment initially
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new HomeFragment())
                .commit();
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerlayout,
                binding.topAppBar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        binding.drawerlayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
            binding.drawerlayout.closeDrawer(GravityCompat.START);
            if (menuItem.getItemId()==R.id.nav_settings)
            {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(menuItem.getItemId()==R.id.nav_reminders)
            {
                Toast.makeText(this, "Reminders clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(menuItem.getItemId()==R.id.nav_account)
            {
                Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            else return false;
        });
    }

    private void setupTopAppBar() {
        // Set username and handle logout dropdown
        Menu menu = binding.topAppBar.getMenu();
        MenuItem userItem = menu.findItem(R.id.action_user);

        TextView userView = new TextView(this);
        userView.setText(user.getDisplayName() != null ? user.getDisplayName() : "Logout ▼");
        userView.setTextColor(Color.WHITE);
        userView.setPadding(20, 10, 20, 10);
        userView.setOnClickListener(v -> showLogoutDialog());
        userItem.setActionView(userView);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId()==R.id.nav_home)
            {
                loadFragment(new HomeFragment());
                return true;
            }
            if(menuItem.getItemId()==R.id.nav_pets)
            {
                loadFragment(new PetsFragment());
                return true;
            }
            if(menuItem.getItemId()==R.id.nav_appointments)
            {
                loadFragment(new AppointmentsFragment());
                return true;
            }
            if(menuItem.getItemId()==R.id.nav_notes)
            {
                loadFragment(new NotesFragment());
                return true;
            }
            else return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
