package com.alexbt.finance.canadian.retirement.inputs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexbt.finance.canadian.retirement.R;

public class InfosFragment extends Fragment implements View.OnClickListener {
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
        this.root = inflater.inflate(R.layout.fragment_infos, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", 0);

        int year = sharedPreferences.getInt("year", 2021);
        TextView yearView = root.findViewById(R.id.year);
        yearView.setText(Html.fromHtml("&#8226; <b>" + year));

        int maritalStatus = sharedPreferences.getInt("marital_status", 0);
        TextView maritalView = root.findViewById(R.id.martialStatus);
        if (maritalStatus == 1) {
            maritalView.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_2)));
        } else if (maritalStatus == 2) {
            maritalView.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_3)));
        } else if (maritalStatus == 3) {
            maritalView.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_4)));
        } else {
            throw new AssertionError();
        }

        TextView viewById = root.findViewById(R.id.revenue);
        viewById.setText("" + sharedPreferences.getInt("combined_revenue", 0));
        viewById.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharedPreferences.Editor edit = getContext().getSharedPreferences("com.alexbt.canadian.retirement", 0).edit();
                int val = 0;
                try {
                    val = Integer.parseInt(charSequence.toString());
                } catch (Exception e) {
                }
                edit.putInt("combined_revenue", val);
                edit.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        doIt(root, R.id.yearsGis, "years_gis_delayed", sharedPreferences);
        doIt(root, R.id.yearsOas, "years_oas_delayed", sharedPreferences);

        Button button = root.findViewById(R.id.button_results);
        button.setOnClickListener(this);

        return root;
    }

    private void doIt(View root, int delayYears, String propertyName, SharedPreferences sharedPreferences) {
        Spinner spin = root.findViewById(delayYears);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.spinner_item, new String[]{"0", "1", "2", "3", "4", "5"});

        spin.setAdapter(adapter);
        spin.setSelection(sharedPreferences.getInt(propertyName, 0));
        spin.setSelected(true);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences.Editor edit = getContext().getSharedPreferences("com.alexbt.canadian.retirement", 0).edit();
                int val = 0;
                try {
                    val = Integer.parseInt(spin.getSelectedItem().toString().trim());
                } catch (Exception e) {
                }
                edit.putInt(propertyName, val);
                edit.apply();

                spin.setSelection(position);
                spin.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_results);
    }
}
