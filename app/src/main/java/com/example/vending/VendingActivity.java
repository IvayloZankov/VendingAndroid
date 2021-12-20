package com.example.vending;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.Menu;
import android.view.MenuItem;

import com.example.vending.utils.Utils;

public class VendingActivity extends AppCompatActivity {

    private static final String TAG = VendingActivity.class.getSimpleName();

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
            Utils.playSound(this, R.raw.click_default);
            Fragment primaryNavigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavigationFragment != null)
            NavHostFragment.findNavController(primaryNavigationFragment)
                    .navigate(R.id.action_maintenance);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(VendingActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.loading_products));
        loadingDialog.setIndeterminate(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }
}