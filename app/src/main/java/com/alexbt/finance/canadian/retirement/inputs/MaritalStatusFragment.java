package com.alexbt.finance.canadian.retirement.inputs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexbt.finance.canadian.retirement.R;

public class MaritalStatusFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private View root;

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_marital_status, container, false);
        initYearSpinner();
        initRadioGroup();
        initButton();
        return root;
    }

    private void initButton() {
        Button button = root.findViewById(R.id.button_next);
        button.setOnClickListener(view -> {
            RadioGroup radioGroup = root.findViewById(R.id.rdGroup);
            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(checkedRadioButtonId);
            int index = radioGroup.indexOfChild(radioButton);
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("marital_status", index);
            edit.apply();

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_infos);
        });
        //button.callOnClick();
    }

    private void initYearSpinner() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", Context.MODE_PRIVATE);
        Spinner spin = root.findViewById(R.id.yearSelection);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new String[]{"2021"});
        spin.setAdapter(adapter);
        spin.setSelection(2021 - sharedPreferences.getInt("year", 2021));
        spin.setSelected(true);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences.Editor edit = getContext().getSharedPreferences("com.alexbt.canadian.retirement", 0).edit();
                int val = 0;
                try {
                    val = Integer.parseInt(spin.getSelectedItem().toString());
                } catch (Exception e) {
                }
                edit.putInt("year", val);
                edit.apply();

                spin.setSelection(position);
                spin.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initRadioGroup() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", Context.MODE_PRIVATE);
        int maritalStatus = sharedPreferences.getInt("marital_status", 0);
        RadioGroup radioGroup = root.findViewById(R.id.rdGroup);
        RadioButton childAt = (RadioButton) radioGroup.getChildAt(maritalStatus);
        childAt.setChecked(true);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        View radioButton = radioGroup.findViewById(checkedId);
        int index = radioGroup.indexOfChild(radioButton);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("marital_status", index);
        edit.apply();

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_infos);
    }
}
