package de.h_da.sqlitetest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Third fragment(dialog) which is shown where user presses on the list of items (to see  description)
 */
public class MyFragmentB extends DialogFragment {

    // Data from MyFragmentA (Description of the record)
    String data = null;
//    TextView textView;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_b, container, false);
//        textView = (TextView)view.findViewById(R.id.textView1FragmentB);
//
//        Bundle arg = getArguments();
//        data = arg.getString("DATA", "null");
//        textView.setText(data);
//        return view;
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arg = getArguments();
        data = arg.getString(MainActivity.DATA_TO_MY_FRAGMENT_B, "null");
        builder.setMessage(data);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;

    }

//    public void deleteText() {
//        textView.setText("");
//    }
}
