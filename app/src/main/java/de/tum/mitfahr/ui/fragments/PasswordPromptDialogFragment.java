package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.tum.mitfahr.R;

/**
 * Authored by abhijith on 04/11/14.
 */
public class PasswordPromptDialogFragment extends DialogFragment {

    public static PasswordPromptDialogFragment newInstance() {
        return new PasswordPromptDialogFragment();
    }

    private PasswordPromptDialogListener mListener;

    public interface PasswordPromptDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String password);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password_prompt, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("Password");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int id) {
                        EditText passwordView = (EditText) dialogView.findViewById(R.id.passwordEditText); //here
                        String password = passwordView.getText().toString();
                        passwordView.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                ((AlertDialog) dialog).getButton(
                                        AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (TextUtils.isEmpty(s)) {
                                    // Disable ok button
                                    ((AlertDialog) dialog).getButton(
                                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                } else {
                                    // Something into edit text. Enable the button.
                                    ((AlertDialog) dialog).getButton(
                                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                }

                            }
                        });
                        mListener.onDialogPositiveClick(PasswordPromptDialogFragment.this, password);
                    }
                }
        );
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return  builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PasswordPromptDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
