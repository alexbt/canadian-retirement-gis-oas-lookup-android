package com.alexbt.finance.canadian.retirement.results;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alexbt.finance.canadian.retirement.R;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ResultsFragment extends Fragment {
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
        this.root = inflater.inflate(R.layout.fragment_result, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.alexbt.canadian.retirement", 0);

        int year = sharedPreferences.getInt("year", 2021);

        int maritalStatus = sharedPreferences.getInt("marital_status", 0);

        int revenue = sharedPreferences.getInt("combined_revenue", 0);
        BigDecimal initialRevenueWithRrq = new BigDecimal(revenue);

        CanadianPension DEFAULT = new CanadianPension();
        DEFAULT.setGis(BigDecimal.ZERO);
        DEFAULT.setMaximumOas(BigDecimal.ZERO);

        TextView yearView = root.findViewById(R.id.year);
        yearView.setText(Html.fromHtml("&#8226; <b>" + year));

        List<CanadianPension> canadianPensionData;
        TextView view = root.findViewById(R.id.martialStatus);
        TextView field = root.findViewById(R.id.revenue);
        String text = "&#8226; " + "<b>" + sharedPreferences.getInt("combined_revenue", 0) + "$</b> of combined revenues";
        field.setText(Html.fromHtml(text));

        field = root.findViewById(R.id.delayGis);
        text = "&#8226; <b>" + sharedPreferences.getInt("years_gis_delayed", 0) + " year(s)</b> delay for GIS";
        field.setText(Html.fromHtml(text));

        field = root.findViewById(R.id.delayOas);
        text = "&#8226; <b>" + sharedPreferences.getInt("years_oas_delayed", 0) + " year(s)</b> delay for OAS";
        field.setText(Html.fromHtml(text));

        if (maritalStatus == 1) {
            view.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_2)));
            canadianPensionData = getGisData("Table2_GIS_for_spouse_of_someone_receiving_OAS_pension_January2021.csv");
        } else if (maritalStatus == 2) {
            view.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_3)));
            canadianPensionData = getGisData("Table3_GIS_for_spouse_of_someone_who_does_not_receive_OAS_pension_January2021.csv");
        } else if (maritalStatus == 3) {
            view.setText(Html.fromHtml("&#8226; <b>" + getResources().getString(R.string.marital_status_option_4)));
            canadianPensionData = getGisData("Table4_GIS_and_Allowance_for_couple_January2021.csv");
        } else {
            throw new AssertionError();
        }

        root.findViewById(R.id.allowance_parent).setVisibility(View.INVISIBLE);

        List<OasData> oasDatas = getOasData("oas.csv");
        OasData oasData = oasDatas.stream()
                .filter(o -> o.getYear() == year)
                .findFirst().get();

        CanadianPension couplePension = canadianPensionData.stream()
                .filter(p -> p.getFrom().compareTo(initialRevenueWithRrq) <= 0 && p.getTo().compareTo(initialRevenueWithRrq) >= 0)
                .findFirst().orElse(DEFAULT);
        displayGis(couplePension, sharedPreferences);
        if ((maritalStatus == 3)) {
            displayAllowance(couplePension, sharedPreferences);
        }
        displayOas(oasData, initialRevenueWithRrq, sharedPreferences);

        return root;
    }

    private void displayAllowance(CanadianPension pension, SharedPreferences sharedPreferences) {
        View layout = root.findViewById(R.id.allowance_parent);
        layout.setVisibility(View.VISIBLE);

        TextView field = root.findViewById(R.id.allowance);

        String suffix = "";
        BigDecimal value = new BigDecimal(pension.getAllowance().toString());
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            suffix = "<b>0$</b>";
        } else {
            suffix = "<b>" + value.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "$</b>";
        }

        field.setText(Html.fromHtml(suffix));
    }

    private void displayGis(CanadianPension pension, SharedPreferences sharedPreferences) {
        TextView field = root.findViewById(R.id.gis);

        int years = sharedPreferences.getInt("years_gis_delayed", 0);
        String suffix = "";
        BigDecimal value = new BigDecimal(pension.getGis().toString());
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            suffix = "<b>0$</b>";
        } else if (years > 0) {
            String val = "<b>" + value.multiply(BigDecimal.valueOf(1 + (years * 0.072))).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "$</b>";
            suffix = value + "$ + (" + years + " x " + "7.2%) = " + val;
        } else {
            suffix = "<b>" + value.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "$</b>";
        }

        field.setText(Html.fromHtml(suffix));
    }

    private void displayOas(OasData oasData, BigDecimal initialRevenueWithRrq,
                            SharedPreferences sharedPreferences) {
        String suffix;
        TextView field = root.findViewById(R.id.oas);
        int years = sharedPreferences.getInt("years_oas_delayed", 0);
        BigDecimal maxOas = oasData.getAmount();

        if (initialRevenueWithRrq.compareTo(oasData.getHigherBound()) > 0) {
            String finalOas = "<b>" + BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) + "$</b>";
            suffix = maxOas + "$ - (("
                    + oasData.getHigherBound()
                    + " - "
                    + oasData.getLowerBound()
                    + ") x 15%) / 12 = " + finalOas;
        } else if (initialRevenueWithRrq.compareTo(oasData.getLowerBound()) > 0) {
            String finalOas = "<b>" + oasData.getAmount().subtract(initialRevenueWithRrq.subtract(oasData.getLowerBound())
                    .max(BigDecimal.ZERO).multiply(oasData.getRepaymentRate()).divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP))
                    .setScale(2, BigDecimal.ROUND_HALF_UP) + "$</b>";
            suffix = maxOas + " - ("
                    + initialRevenueWithRrq
                    + " - "
                    + oasData.getLowerBound()
                    + " x 15%) / 12 = " + finalOas;
        } else {
            if (years > 0) {
                String finalOas = "<b>" + maxOas.multiply(BigDecimal.valueOf(1 + (years * 0.072))).setScale(2, BigDecimal.ROUND_HALF_UP) + "$</b>";
                suffix = maxOas + "$ + (" + years + " x " + "7.2%) = " + finalOas;
            } else {
                suffix = "<b>" + maxOas + "$</b>";
            }
        }

        field.setText(Html.fromHtml(suffix));
    }


    private List<CanadianPension> getGisData(String filename) {
        List<CanadianPension> list = new ArrayList<>();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(resourceAsStream));
            String[] nextLine;
            String[] titles = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (nextLine[0].trim().startsWith("#") || nextLine[0].trim().isEmpty()) {
                    continue;
                }
                int i = 0;
                CanadianPension canadianPension = new CanadianPension();
                canadianPension.setFrom(new BigDecimal(nextLine[i++].replace(",", "")));
                canadianPension.setTo(new BigDecimal(nextLine[i++].replace(",", "")));
                canadianPension.setGis(new BigDecimal(nextLine[i++].replace(",", "")));
                canadianPension.setMaximumOas(new BigDecimal(nextLine[i++].replace(",", "")).subtract(canadianPension.getGis()));
                if (nextLine.length >= 5) {
                    canadianPension.setAllowance(new BigDecimal(nextLine[i++].replace(",", "")));
                } else {
                    canadianPension.setAllowance(BigDecimal.ZERO);
                }
                list.add(canadianPension);
            }
        } catch (IOException e) {

        }
        return list;
    }

    private List<OasData> getOasData(String filename) {
        List<OasData> list = new ArrayList<>();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(resourceAsStream));
            String[] nextLine;
            String[] titles = reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (nextLine[0].trim().startsWith("#") || nextLine[0].trim().isEmpty()) {
                    continue;
                }
                int i = 0;
                OasData oasData = new OasData();
                oasData.setYear(Integer.parseInt(nextLine[i++]));
                oasData.setLowerBound(new BigDecimal(nextLine[i++].replace(",", "")));
                oasData.setHigherBound(new BigDecimal(nextLine[i++].replace(",", "")));
                oasData.setAmount(new BigDecimal(nextLine[i++].replace(",", "")));
                oasData.setRepaymentRate(new BigDecimal(nextLine[i++].replace(",", "")));
                list.add(oasData);
            }
        } catch (IOException e) {

        }
        return list;
    }
}
