package de.h_da.sqlitetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * Created by User on 11.01.2017.
 */
public class MyDialogSorting extends DialogFragment implements CompoundButton.OnCheckedChangeListener{
    private CheckBox checkBoxDate, checkBoxMinSum, checkBoxMaxSum, checkBoxMonthYear;
    private Spinner spinnerMonth, spinnerYear;
    private int spinnerMonthPositionSelected, spinnerYearPositionSelected;

    // ArrayLists for spinners data
    private ArrayList<String> arrayMonth, arrayMonthLetters;

    // Reference to the MainActivity
    Communicator comm;

    //Users choice for sorting
    private String choise = null; //

    String[] monthAndYear = null;

    private String dataFromPrefs = null;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_my_dialog_sorting, null);





        builder.setView(view);

        initializeUI(view);

        fillingSpinners();

        // Retrieving data about previous sorting and set appropriate checkbox
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        if (prefs != null) {
            dataFromPrefs = prefs.getString(MainActivity.PREFS_CHOICE_SORTING, MainActivity.GROUP_BY_DATE);
            setCheckBoxesPreferences(dataFromPrefs);
//            if (dataFromPrefs.equals(MainActivity.GROUP_BY_MONTH_AND_YEAR)) {
//                spinnerMonthPositionSelected = prefs.getInt(MainActivity.PREFS_SPINNER_MONTH_SELECTED_POSITION, 0);
//                spinnerYearPositionSelected = prefs.getInt(MainActivity.PREFS_SPINNER_YEAR_SELECTED_POSITION, 0);
//                //if (spinnerMonth.getItemAtPosition(spinnerMonthPositionSelected) != null) {
//                    try {
//                        spinnerMonth.setSelection(spinnerMonthPositionSelected);
//                    } catch (Exception e) {
//                        spinnerMonth.setSelection(spinnerMonthPositionSelected);
//                    }
//                //}
////                if (spinnerMonth.getItemAtPosition(spinnerYearPositionSelected) != null) {
//                    try {
//                        spinnerYear.setSelection(spinnerYearPositionSelected);
//                    } catch (Exception e) {
//                        spinnerMonth.setSelection(spinnerYearPositionSelected);
//                    }
//                }
//            }
        }
//          -----------------------------------------------------------



        setListeners();

        Resources res = getResources();

        builder.setPositiveButton(res.getString(R.string.set_positive_button_sorting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                whichCheckBoxChecked();
                if (choise == MainActivity.GROUP_BY_MONTH_AND_YEAR) {
                    monthAndYear = new String[] {"", ""};
                    monthAndYear[1] = String.valueOf(spinnerYear.getSelectedItem());

                    spinnerMonthPositionSelected = spinnerMonth.getSelectedItemPosition();
                    spinnerYearPositionSelected = spinnerYear.getSelectedItemPosition();

                    int a = spinnerMonth.getSelectedItemPosition();
                    //Receiving the month in digits
                    if (a != 0) {
                        monthAndYear[0] = arrayMonth.get(a - 1);
                    }
                    else {
                        monthAndYear[0] = MainActivity.ALL_MONTH_SELECTED;
                    }
                }

                comm.respondMyDialogSorting(choise, monthAndYear); // Invokes method from MainActivity
            }
        }).setNegativeButton(res.getString(R.string.set_negative_button_sorting), null);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Communicator)activity;


    }

    @Override
    public void onStop() {
        super.onStop();
        if (choise != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(MainActivity.PREFS_CHOICE_SORTING, choise);
                    if (monthAndYear != null) {
                        edit.putString(MainActivity.PREFS_CHOICE_MONTH, monthAndYear[0]). putString(MainActivity.PREFS_CHOICE_YEAR, monthAndYear[1]);
//                        edit.putInt(MainActivity.PREFS_SPINNER_MONTH_SELECTED_POSITION, spinnerMonthPositionSelected);
//                        edit.putInt(MainActivity.PREFS_SPINNER_YEAR_SELECTED_POSITION, spinnerYearPositionSelected);
                    }
            edit.commit();
        }
    }

    /**
     * Set appropriate checkbox regards the data
     * @param data - Users choice about sorting
     */
    void setCheckBoxesPreferences(String data) {
        switch(data) {
            case MainActivity.GROUP_BY_DATE:
                checkBoxDate.setChecked(true);
                break;
            case MainActivity.GROUP_BY_MAX_SUM:
                checkBoxMaxSum.setChecked(true);
                break;
            case MainActivity.GROUP_BY_MIN_SUM:
                checkBoxMinSum.setChecked(true);
                break;
            case MainActivity.GROUP_BY_MONTH_AND_YEAR:
                checkBoxMonthYear.setChecked(true);
                spinnerSetVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Initializes all view from xml
     * @param v
     */
    private void initializeUI(View v) {
        checkBoxDate = (CheckBox)v.findViewById(R.id.checkBoxDate);
        checkBoxMinSum = (CheckBox)v.findViewById(R.id.checkBoxMinSum);
        checkBoxMaxSum = (CheckBox)v.findViewById(R.id.checkBoxMaxSum);
        checkBoxMonthYear = (CheckBox)v.findViewById(R.id.checkBoxMonthYear);
        spinnerMonth = (Spinner)v.findViewById(R.id.spinnerMonth);
        spinnerYear = (Spinner)v.findViewById(R.id.spinnerYear);
    }

    /**
     * Sets listeners
     */
    private void setListeners() {
        checkBoxDate.setOnCheckedChangeListener(this);
        checkBoxMinSum.setOnCheckedChangeListener(this);
        checkBoxMaxSum.setOnCheckedChangeListener(this);
        checkBoxMonthYear.setOnCheckedChangeListener(this);
    }

    /**
     * Fills spinners with data from DB
     */
    private void fillingSpinners() {
        // Receiving all info
        MyDatabaseAdapter adapterDatabase = new MyDatabaseAdapter(getActivity());
        ArrayList<MyData> arrayListData = adapterDatabase.showSortedValues(MainActivity.GROUP_BY_DATE, null);

        // ArrayLists for months and years
        arrayMonth = new ArrayList<>();
        ArrayList<String> arrayYear = new ArrayList<>();

        // Adding unique info to arrayMonth and arrayYear
        for (MyData a: arrayListData) {
            if (!arrayMonth.contains(a.getMonth())) {
                arrayMonth.add(a.getMonth());
            }
            if (!arrayYear.contains(a.getYear())) {
                arrayYear.add(a.getYear());
            }
        }

        // Sorting arrayMonth from 0 to 11
        Collections.sort(arrayMonth, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return  (Integer.parseInt(lhs) - Integer.parseInt(rhs));

            }
        });

        // Add "All month" to the beginning and Converting month numbers to letters
        Calendar calendar;
        arrayMonthLetters = new ArrayList<>();
        arrayMonthLetters.add("All months");

        // Months is shown in letters
        for (String s: arrayMonth) {
            calendar = new GregorianCalendar(1,Integer.parseInt(s), 1);
            String month = String.format("%1$tB", calendar);
            arrayMonthLetters.add(month);
        }

        // Setting arrayadapters to spinners
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayMonthLetters); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(spinnerArrayAdapter);

        spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayYear); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(spinnerArrayAdapter);
    }

    /**
     * Unchecks other checkboxes apart from pressed and if the checkbox which respons for spinners is checked - show spinners
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.checkBoxDate:

                if(isChecked) {
                    uncheckCheckBoxes(0);
                }
                break;
            case R.id.checkBoxMinSum:

                if (isChecked) {
                    uncheckCheckBoxes(1);
                }
                break;
            case R.id.checkBoxMaxSum:

                if (isChecked) {
                    uncheckCheckBoxes(2);
                }
                break;
            case R.id.checkBoxMonthYear:

                if (isChecked) {
                    uncheckCheckBoxes(3);
                }
                else {
                    spinnerSetVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    /**
     * Evaluates and sets the data to pass which checkBox checked before user presses Ok
     */
    private void whichCheckBoxChecked() {
        CheckBox[] arrayCheckbox = {checkBoxDate, checkBoxMinSum, checkBoxMaxSum, checkBoxMonthYear};
        int checkedNumberCheckBox;
        for (checkedNumberCheckBox = 0; checkedNumberCheckBox<arrayCheckbox.length; checkedNumberCheckBox++) {

            if ( arrayCheckbox[checkedNumberCheckBox].isChecked() ) {
                break;
            }

        }
        switch (checkedNumberCheckBox) {
            case 0:
                choise = MainActivity.GROUP_BY_DATE;
                break;
            case 1:
                choise = MainActivity.GROUP_BY_MIN_SUM;
                break;
            case 2:
                choise = MainActivity.GROUP_BY_MAX_SUM;
                break;
            case 3:
                choise = MainActivity.GROUP_BY_MONTH_AND_YEAR;
                break;

        }
    }

    /**
     * Unchecks other checkboxes
     * @param data - the value which checkbox was pressed (0 - date, 1- sum, 2 - month and year)
     */
    private void uncheckCheckBoxes(int data) {
        CheckBox[] arrayCheckbox = {checkBoxDate, checkBoxMinSum, checkBoxMaxSum, checkBoxMonthYear};
        for(int i=0; i<arrayCheckbox.length; i++) {
            if (i == data) {
                continue;
            }
            arrayCheckbox[i].setChecked(false);
        }
        int visibility;
        if (arrayCheckbox[3].isChecked()) {
            visibility = View.VISIBLE;
        }
        else {
            visibility = View.INVISIBLE;
        }
        spinnerSetVisibility(visibility);
    }

    /**
     * Changes visibility of spinners
     * @param vis - value of visibility
     */
    private void spinnerSetVisibility(int vis) {
        spinnerMonth.setVisibility(vis);
        spinnerYear.setVisibility(vis);
    }

    /**
     * Interface to implement which passes data which optons to sort was chosen and if the specific sorting - which month as well as year
     */
    public interface Communicator {
        void respondMyDialogSorting (String data, String[] monthAndYear);
    }
}
