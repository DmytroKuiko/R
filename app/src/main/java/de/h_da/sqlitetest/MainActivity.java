package de.h_da.sqlitetest;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MyFragmentA.Communicator, MyDatePicker.CommunicatorDatePicker,
        MyDialogSorting.Communicator {


    public static final String MY_FRAGMENT_A = "MY_FRAGMENT_A";
    public static final String MY_FRAGMENT_B = "MY_FRAGMENT_B";
    public static final String START_FRAGMENT = "START_FRAGMENT";
    public static final String MY_DATEPICKER = "MY_DATEPICKER";
    public static final String MY_DIALOG_FOR_DELETING = "MY_DIALOG_FOR_DELETING";
    public static final String MY_DIALOG_SORTING = "MY_DIALOG_SORTING";
    public static final String DATA_TO_MY_FRAGMENT_B = "DATA_TO_MY_FRAGMENT_B";
    public static final String DATA_TO_MY_DIALOG_FOR_DELETING = "DATA_TO_MY_DIALOG_FOR_DELETING";
    public static final String DATA_LONG_TO_MY_DIALOG_FOR_DELETING = "DATA_LONG_TO_MY_DIALOG_FOR_DELETING";
    public static final String DATA_CHOICE_TO_MY_FRAGMENT_A = "DATA_CHOICE_TO_MY_FRAGMENT_A"; //For Bundle (StartFragment - MyFragmentA, users choice regarding the info shown in ListView)
    public static final String DATA_MONTH_AND_YEAR_TO_MY_FRAGMENT_A = "DATA_MONTH_AND_YEAR_TO_MY_FRAGMENT_A";
    public static final String GROUP_BY_DATE = "Date";      // For viewing the table by date sorting
    public static final String GROUP_BY_MAX_SUM = "Max Sum";   //For viewing the table by max sum sorting
    public static final String GROUP_BY_MIN_SUM = "Min Sum";   //For viewing the table by min sum sorting
    public static final String GROUP_BY_MONTH_AND_YEAR = "Month and Year"; //For viewing the table by month and year sorting
    public static final String ALL_MONTH_SELECTED = "All Months";
    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    public static final String PREFS_SPINNER_MONTH_SELECTED_POSITION = "PREFS_SPINNER_MONTH_SELECTED_POSITION";
    public static final String PREFS_SPINNER_YEAR_SELECTED_POSITION = "PREFS_SPINNER_YEAR_SELECTED_POSITION";
    public static final String PREFS_CHOICE_SORTING = "PREFS_CHOICE_SORTING";
    public static final String PREFS_CHOICE_MONTH = "PREFS_CHOICE_MONTH";
    public static final String PREFS_CHOICE_YEAR = "PREFS_CHOICE_YEAR";
    private String sorting = "default";
    FragmentManager manager;
    MyFragmentA myFragmentA;
    MyFragmentB myFragmentB;
    MyFragmentB myFragmentBB;
    MyDialogSorting dialogSorting;
    StartFragment startFragment;
    MyDatabaseAdapter db;
    MyDialogInfo dialogInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        startFragment = new StartFragment();
        transaction.add(R.id.layoutMain, startFragment, START_FRAGMENT).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuInfo:
                dialogInfo = new MyDialogInfo();
                dialogInfo.show(manager, MY_DIALOG_SORTING);
                break;
            case R.id.menuClear:
                db = new MyDatabaseAdapter(MainActivity.this);
                if(!db.checkingEmpty()) {
                    db.clearDatabase();
                    Toast.makeText(MainActivity.this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();

                    // Sorting reset to default by Date
                    SharedPreferences prefs = MainActivity.this.getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
                    prefs.edit().putString(MainActivity.PREFS_CHOICE_SORTING, MainActivity.GROUP_BY_DATE).commit();
                    startFragment.updateDatatoDefaultSorting();
                }

                // If we are in a fragmentA - erasing listView
                myFragmentA = (MyFragmentA)getFragmentManager().findFragmentByTag(MY_FRAGMENT_A);
                if (myFragmentA != null) {
                    myFragmentA.updateListView();
                }
                break;
            case R.id.menuSort:
                db = new MyDatabaseAdapter(MainActivity.this);
                if(db.checkingEmpty()) {
                    break;
                }
                dialogSorting = new MyDialogSorting();
                dialogSorting.show(manager, MY_DIALOG_SORTING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Passes data from fragmentA to FragmentB(Dialog Description) and launch this fragment for showing
     * @param data - description of the pressed record
     */
    @Override
    public void respond(String data) {
        if (!data.equals("")) {
            myFragmentB = new MyFragmentB();
            Bundle args = new Bundle();
            args.putString(DATA_TO_MY_FRAGMENT_B, data);
            myFragmentB.setArguments(args);
            myFragmentB.show(getFragmentManager(), MY_FRAGMENT_B);
        }
    }

    /**
     * Sets data(from MyDatePicker) in a StartFragment's textView
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void respondDatePicker(int year, int month, int day) {
        startFragment.dataFromDatePicker(year, month, day);
    }

    /**
     * Receives data and depends in which fragment we are whether invokes a method for updating a list
     * Or pass data to StartFragment where it will be passed further to FragmentA if user go further
     * @param data - data from MyDialogSorting(users choice)
     */
    @Override
    public void respondMyDialogSorting(String data, String[] monthAndYear) {
        myFragmentA = (MyFragmentA)getFragmentManager().findFragmentByTag(MY_FRAGMENT_A);
        if (myFragmentA != null) {
            myFragmentA.updateAfterSorting(data, monthAndYear);
        }
        if (startFragment != null) {
            startFragment.sortingData(data, monthAndYear);
        }
    }
}
