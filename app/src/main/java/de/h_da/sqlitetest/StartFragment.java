package de.h_da.sqlitetest;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Start Fragment where user can add his records
 */
public class StartFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText editTextSum, editTextDescription;
    private TextView editTextDate;
    private ImageButton buttonAdd, buttonShow;
    private CheckBox checkBoxDescription;
    private LinearLayout linearLayout;

    // Data from MyDatePicker for setting it to a TextView
    private int year, month, day;

    // Sum entered in EditText
    private double sum;

    // Description from EditText
    private String description = "";
    private InputMethodManager imm;

    // Default value of sorting for showing the info which will be passed to MyFragmentA
    private String sortingSelected = null;

    // Default values of sorting for the specific info (month and year)
    private String[] monthAndYearSelected = null;

    // Data from DatePicker (Months are in letters, not in numbers)
    private String resultFromDatePicker;

    // Whether record is successfully added or not, for clearing fields
    private boolean recordAdded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        initializeUI(view);

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        if (sortingSelected == null) {
            sortingSelected = prefs.getString(MainActivity.PREFS_CHOICE_SORTING, MainActivity.GROUP_BY_DATE);
            if (sortingSelected.equals(MainActivity.GROUP_BY_MONTH_AND_YEAR)) {
                monthAndYearSelected = new String[]{prefs.getString(MainActivity.PREFS_CHOICE_MONTH, MainActivity.ALL_MONTH_SELECTED),
                        prefs.getString(MainActivity.PREFS_CHOICE_YEAR, "2017")};
            }
        }

        checkCheckBox();

        if ((year != 0 || month != 0 || day != 0) && (!recordAdded)) {
            editTextDate.setText(resultFromDatePicker);
        }

        setListeners();
        return view;
    }


    /**
     * Set listeners to all xml elements required
     */
    private void setListeners() {
        editTextDate.setOnClickListener(this);
        buttonAdd.setOnClickListener(this);
        buttonShow.setOnClickListener(this);
        checkBoxDescription.setOnCheckedChangeListener(this);

    }

    /**
     * Initilizing all elements from layout file
     * @param view
     */
    private void initializeUI(View view) {
        editTextDescription = (EditText)view.findViewById(R.id.editTextDescription);
        editTextSum = (EditText)view.findViewById(R.id.editTextSum);
        editTextDate = (TextView)view.findViewById(R.id.textViewDate);
        buttonAdd = (ImageButton)view.findViewById(R.id.buttonAdd);
        buttonShow = (ImageButton)view.findViewById(R.id.buttonShow);
        checkBoxDescription = (CheckBox)view.findViewById(R.id.checkBoxDescription);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearLayoutStartFragment);
    }


    /**
     * Setting a text in textView after it was chosen in DatePicker
     * @param year
     * @param month
     * @param day
     */
    public void dataFromDatePicker(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        Calendar calendar = new GregorianCalendar(year, month, day);
        resultFromDatePicker = String.format("%1$te %1$tB %1$tY", calendar);
//        String forText = getString(R.string.edit_text_dateSet, day, month, year);
        editTextDate.setText(resultFromDatePicker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewDate:
                recordAdded = false;
                MyDatePicker datePicker = new MyDatePicker();
                datePicker.show(getFragmentManager(), MainActivity.MY_DATEPICKER);
                break;
            case R.id.buttonAdd:
                if (!(editTextDate.getText().toString().equals("")) && !(editTextSum.getText().toString().equals(""))) {
                    Log.d("TAG", "Date: " + editTextDate.getText() + " SUM: " + editTextSum.getText());
                    getValues();
                    MyDatabaseAdapter databaseAdapter = new MyDatabaseAdapter(getActivity());
                    databaseAdapter.insertData(String.valueOf(day), String.valueOf(month), String.valueOf(year), sum, description);
                    clearValues();
                    hideKeyboard();
                    Toast.makeText(getActivity(), "This record was added successfully", Toast.LENGTH_SHORT).show();
                    recordAdded = true;
                }
                break;
            case R.id.buttonShow:
                MyFragmentA myFragmentA = new MyFragmentA();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                hideKeyboard();
                Bundle args = new Bundle();
                args.putString(MainActivity.DATA_CHOICE_TO_MY_FRAGMENT_A, sortingSelected);

                args.putStringArray(MainActivity.DATA_MONTH_AND_YEAR_TO_MY_FRAGMENT_A, monthAndYearSelected);

                myFragmentA.setArguments(args);
                transaction.replace(R.id.layoutMain, myFragmentA, MainActivity.MY_FRAGMENT_A).addToBackStack(null).commit();
                break;
        }
    }

    public void updateDatatoDefaultSorting() {
        sortingSelected = MainActivity.GROUP_BY_DATE;
        monthAndYearSelected = null;
    }


    /**
     * Receive values from fields for adding to a DB
     */
    private void getValues() {
        sum = Double.parseDouble(editTextSum.getText().toString());
        description = editTextDescription.getText().toString();
    }

    /**
     * Erase fields after adding the info
     */
    private void clearValues() {
        editTextSum.setText("");
        editTextDate.setText("");
        editTextDescription.setText("");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBoxDescription:
                if (checkBoxDescription.isChecked()) {
                    editTextDescription.setVisibility(View.VISIBLE);
                    setImeOptions();
                    editTextDescription.clearFocus();
                }
                else {
                    editTextDescription.setVisibility(View.INVISIBLE);
                    setImeOptions();
                }
                break;
        }
    }

    /**
     * Appearing the description editText depending whether checkbox set or not
     */
    private void checkCheckBox() {
        if (checkBoxDescription.isChecked()) {
            editTextDescription.setVisibility(View.VISIBLE);
            setImeOptions();
        }
        else {
            editTextDescription.setVisibility(View.INVISIBLE);
            setImeOptions();
        }
    }

    /**
     * Hides keyboard
     */
    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e) {}
    }

    /**
     * Changes ImeOptions  of editText(Sum) whether the description checkbox set or not
     */
    private void setImeOptions() {
        hideKeyboard();
        editTextSum.clearFocus();
        if (checkBoxDescription.isChecked()) {
            editTextSum.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
        else {
            editTextSum.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    /**
     * Receives info from MyDialogSorting about the decision and passing to MyFragmentA
     * @param data - chosen optons about the sorting
     * @param monthAndYear - month and year of the specific option
     */
    public void sortingData(String data, String[] monthAndYear) {
        sortingSelected = data;
        monthAndYearSelected = monthAndYear;
    }

}
