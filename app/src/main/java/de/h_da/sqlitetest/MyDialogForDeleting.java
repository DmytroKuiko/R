package de.h_da.sqlitetest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Dialog for deleting a specific record from the list (which is shown where user click(LONG) on the record
 */
public class MyDialogForDeleting extends DialogFragment {

    // Id of record which was pressed in MyFragmentA
    long id;
    private MyFragmentA myFragmentA;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Retrieving passed data which is shown in dialog
        Bundle arg = getArguments();
        String data = arg.getString(MainActivity.DATA_TO_MY_DIALOG_FOR_DELETING);
        id = arg.getLong(MainActivity.DATA_LONG_TO_MY_DIALOG_FOR_DELETING);

        builder.setMessage(res.getString(R.string.dialog_message_delete).concat("\n").concat(data));
        builder.setPositiveButton(res.getString(R.string.set_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseAdapter db = new MyDatabaseAdapter(getActivity());
                db.deleteSpecificInfo(id);
                myFragmentA = (MyFragmentA)getFragmentManager().findFragmentByTag(MainActivity.MY_FRAGMENT_A);
                if (myFragmentA != null) {
                    myFragmentA.updateListView();
                }
//                Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(res.getString(R.string.set_negative_button), null);
        Dialog dialog =  builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
