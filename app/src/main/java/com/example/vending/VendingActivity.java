package com.example.vending;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.utils.SoundManager;

public class VendingActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = VendingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SoundManager.getInstance().loadSounds(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_maintenance) {
            SoundManager.getInstance().playClick();
            Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavigationFragment != null)
            NavHostFragment.findNavController(primaryNavigationFragment)
                    .navigate(R.id.action_maintenance);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SoundManager.getInstance().cleanUp();
        super.onDestroy();
    }
}