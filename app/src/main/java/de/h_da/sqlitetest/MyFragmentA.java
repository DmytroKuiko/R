package de.h_da.sqlitetest;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Second fragment where user can see a list of his records
 */
public class MyFragmentA extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        View.OnClickListener {

    String dataToSend = null;

    // Reference to the MainActivity
    Communicator comm;
    private MyDatabaseAdapter adapterDatabase;
    private MyArrayAdapter arrayAdapter;
    private ArrayList<MyData> arrayListData;

    private ListView listView;
    private TextView text, textViewTotal;
    private ImageButton buttonBack;

    // Data received from MyDialogSorting for showing the data
    private String receivedData;

    // Data received from MyDialogSorting (specific sorting month and year)
    private String[] monthAndYearSelected;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        initializeUI(view);

        setListeners();

        adapterDatabase = new MyDatabaseAdapter(getActivity());

        // Receiving bundles from startFragment regarding the sorting
        receivedData = getArguments().getString(MainActivity.DATA_CHOICE_TO_MY_FRAGMENT_A);
        monthAndYearSelected = getArguments().getStringArray(MainActivity.DATA_MONTH_AND_YEAR_TO_MY_FRAGMENT_A);

        // Receive this data from DB
        arrayListData = adapterDatabase.showSortedValues(receivedData, monthAndYearSelected);

        // Filling ListView and TextView
        if (arrayListData != null) {

            arrayListArrayAdapterCreating();

            textViewTotal.setText(String.valueOf(totalSumCount()).concat(" \u20ac"));
        }
        else {
            textViewTotal.setText(null);
        }

        return view;
    }

    /**
     * Initializes from XML to Java elements
     * @param view
     */
    private void initializeUI(View view) {
        buttonBack = (ImageButton)view.findViewById(R.id.buttonBack);
        listView = (ListView)view.findViewById(R.id.listView);
        textViewTotal = (TextView)view.findViewById(R.id.textViewTotal);
    }

    /**
     * Sets listeners to them
     */
    private void setListeners() {
        buttonBack.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }


    /**
     * Counts total Sum of records which are shown
     * @return
     */
    private Double totalSumCount() {
        Double result=0.0;
        if (arrayListData != null) {
            for (MyData data : arrayListData) {
                result += data.getSum();
            }
        }
        return  result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator)getActivity();
    }

    /**
     * For showing a description
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyDatabaseAdapter db = new MyDatabaseAdapter(getActivity());
        String toB = db.lookingForDescriptonById(String.valueOf(id));
        comm.respond(toB);
    }

    /**
     * For deleting pressed record
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        MyDialogForDeleting dialogForDeleting = new MyDialogForDeleting();
        Bundle args = new Bundle();
        text = (TextView)view.findViewById(R.id.textViewAllInfo);
        String data = (String) text.getText();


        args.putString(MainActivity.DATA_TO_MY_DIALOG_FOR_DELETING, data );
        args.putLong(MainActivity.DATA_LONG_TO_MY_DIALOG_FOR_DELETING, id);
        dialogForDeleting.setArguments(args);
        dialogForDeleting.show(getFragmentManager(), MainActivity.MY_DIALOG_FOR_DELETING);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBack:
                getActivity().getFragmentManager().popBackStack();
                break;
        }
    }

    /**
     * Interface for passing data to Fragment B (descrpton of the record to be shown)
     */
    public interface Communicator {
        void respond (String data);
    }

    /**
     * Creates arrayAdapter and set it for ListView
     */
    private void arrayListArrayAdapterCreating() {
        arrayAdapter = new MyArrayAdapter(arrayListData);
        listView.setAdapter(arrayAdapter);

    }

    /**
     * Clears the list and fill it with another chosen info
     */
    public void updateListView() {

        // Receiving new data after sorting
        ArrayList<MyData> refreshed = adapterDatabase.showSortedValues(receivedData, monthAndYearSelected);

        // If our old data is not empty -> erase it. Otherwise create new ArrayList as well as Adapter and assign Adapter to ListView
        if (arrayListData != null) {
            arrayListData.clear();
        }
        else {
            arrayListData = new ArrayList<>();
            arrayListArrayAdapterCreating();
        }

        // If received ArrayList is not empty -> adding this info to arrayList and changing Total Sum. If empty Total Sum = null
        if (refreshed != null) {

            arrayListData.addAll(adapterDatabase.showSortedValues(receivedData, monthAndYearSelected));
            textViewTotal.setText(String.valueOf(totalSumCount()).concat(" \u20ac"));
        }
        else {
            textViewTotal.setText(null);
        }

        // Notify about changes
        arrayAdapter.notifyDataSetChanged();


    }

    /**
     * Updates ListView after sorting from menu
     * @param data - value of users choice for sorting
     */
    public void updateAfterSorting(String data, String[] monthAndYear) {
        receivedData = data;
        monthAndYearSelected = monthAndYear;
        updateListView();
    }

}
