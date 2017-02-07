package de.h_da.sqlitetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by User on 08.01.2017.
 */
public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Reference to the Activity
    CommunicatorDatePicker communicatorDatePicker;
    private Calendar calendar;
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.setTitle(null);
        colorizeDatePicker(dialog.getDatePicker());

        return dialog;
    }

    /**
     * Change dividers in DatePickerDialog
     * @param datePicker - current datePicker (DatePickerDialog.getDatePicker)
     */
    public void colorizeDatePicker(DatePicker datePicker) {
        Resources system = Resources.getSystem();
        int dayId = system.getIdentifier("day", "id", "android");
        int monthId = system.getIdentifier("month", "id", "android");
        int yearId = system.getIdentifier("year", "id", "android");

        NumberPicker dayPicker = (NumberPicker) datePicker.findViewById(dayId);
        NumberPicker monthPicker = (NumberPicker) datePicker.findViewById(monthId);
        NumberPicker yearPicker = (NumberPicker) datePicker.findViewById(yearId);

        setDividerColor(dayPicker);
        setDividerColor(monthPicker);
        setDividerColor(yearPicker);
    }

    private void setDividerColor(NumberPicker picker) {
        if (picker == null)
            return;

        final int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            try {
                Field dividerField = picker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
//                ColorDrawable colorDrawable = new ColorDrawable(picker.getResources().getColor(R.color.colorPrimary));
                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context,R.color.colorPrimary));
                dividerField.set(picker, colorDrawable);
                picker.invalidate();
            } catch (Exception e) {
                Log.w("setDividerColor", e);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicatorDatePicker = (CommunicatorDatePicker)activity;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        communicatorDatePicker.respondDatePicker(year, monthOfYear, dayOfMonth);
        dismiss();
    }


    /**
     * Interface for implementing it in a MainActivity for passing info os chosen date to a start Fragment and set Text of this date
     */
    public interface CommunicatorDatePicker {
        void respondDatePicker(int year, int month, int day);
    }
}
