package me.jamesfrost.simpledo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Asks a user to confirm they want to delete a task.
 * <p/>
 * Created by James Frost on 26/09/2014.
 */
public class DeleteDialog extends DialogFragment implements Constants {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String[] checkBoxName = bundle.getString(KEY_CHECKBOXTOBODELETEDNAME).split(SEPARATOR);
        System.out.println("Checkbox length: " + checkBoxName.length);
        String DELETE_CONFORMATION_DIALOG = "Are you sure you want to delete " + checkBoxName[0] + "?";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(DELETE_CONFORMATION_DIALOG);
        builder.setMessage(DELETE_CONFORMATION_MESSAGE);
        builder.setPositiveButton(POSITIVE_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(DeleteDialog.this);
            }
        })
                .setNegativeButton(NEGATIVE_BUTTON_TEXT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DeleteDialog.this);
                    }
                });
        return builder.create();
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

