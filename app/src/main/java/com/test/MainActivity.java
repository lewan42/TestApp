package com.test;

import android.os.Bundle;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.test.ui.player.MyCustomAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {

    private static BadgeDrawable badge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_record, R.id.navigation_play)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (R.id.navigation_play == destination.getId())
                cleanBadge();

            else if (R.id.navigation_record == destination.getId())
                stopAudio();
        });

        badge = navView.getOrCreateBadge(R.id.navigation_play);
        badge.setVisible(false);
    }

    public static void stopAudio() {
        MyCustomAdapter.stopAudio();
    }

    public static void incrementBadge() {
        if (!badge.isVisible()) badge.setVisible(true);
        badge.setNumber(badge.getNumber() + 1);
    }

    public static void cleanBadge() {
        badge.clearNumber();
        badge.setVisible(false);
    }
}
