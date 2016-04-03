package de.tum.mitfahr.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.tum.mitfahr.R;

/**
 * Authored by abhijith on 04/11/14.
 */
public class PasswordChangeDialogFragment extends DialogFragment {

    public static PasswordChangeDialogFragment newInstance(PasswordChangeDialogListener listener) {
        PasswordChangeDialogFragment frag = new PasswordChangeDialogFragment();
        frag.mListener = listener;
        return frag;
    }

    public PasswordChangeDialogListener mListener;

    public interface PasswordChangeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String passwordOld, String passwordNew);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password_change, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("Change Password");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int id) {
                EditText passwordOldText = (EditText) dialogView.findViewById(R.id.passwordOld); //here
                EditText passwordNewText = (EditText) dialogView.findViewById(R.id.passwordNew);
                String passwordOld = passwordOldText.getText().toString();
                String passwordNew = passwordNewText.getText().toString();
                mListener.onDialogPositiveClick(PasswordChangeDialogFragment.this, passwordOld, passwordNew);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

}
