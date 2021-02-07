package com.alexbt.finance.canadian.retirement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //TODO
    //private Map<Class, Object> cache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_marital_status, R.id.navigation_infos, R.id.navigation_results, R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        SharedPreferences preferences = getSharedPreferences("com.alexbt.canadian.retirement", Context.MODE_PRIVATE);
        int years_gis_delayed = preferences.getInt("years_gis_delayed", 0);
        int years_oas_delayed = preferences.getInt("years_oas_delayed", 0);
        if (years_gis_delayed < 0 || years_gis_delayed > 5) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("years_gis_delayed", 0);
            edit.apply();
        }
        if (years_oas_delayed < 0 || years_oas_delayed > 5) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("years_oas_delayed", 0);
            edit.apply();
        }

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //TODO
//    public <T> T getData(Class<T> clazz) {
//        return (T)cache.get(clazz);
//    }

    //TODO
//    public <T> void setData(T o) {
//        cache.put(o.getClass(), o);
//    }
}
