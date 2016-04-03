package de.tum.mitfahr.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.tum.mitfahr.R;

/**
 * Created by abhijith on 17/11/14.
 */
public class MultiDatePickerDialog extends DialogFragment {

    private CalendarPickerView calendarView;

    public static MultiDatePickerDialog newInstance(MultiDatePickerDialogListener listener) {
        MultiDatePickerDialog frag = new MultiDatePickerDialog();
        frag.mListener = listener;
        return frag;
    }

    public MultiDatePickerDialogListener mListener;
    private List<Date> selectedDates = new ArrayList<Date>();

    public interface MultiDatePickerDialogListener {
        public void onMultiDatePicked(android.support.v4.app.DialogFragment dialog, List<Date> selectedDates);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_multi_date, null);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        calendarView = (CalendarPickerView) dialogView.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendarView.init(today, nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("Pick Dates");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int id) {
                selectedDates = calendarView.getSelectedDates();
                mListener.onMultiDatePicked(MultiDatePickerDialog.this, selectedDates);
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
