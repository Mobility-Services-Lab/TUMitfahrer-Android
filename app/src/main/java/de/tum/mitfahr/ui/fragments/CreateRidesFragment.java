package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.events.OfferRideEvent;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.ui.MultiDatePickerDialog;
import de.tum.mitfahr.ui.RideDetailsActivity;
import de.tum.mitfahr.util.InputFilterMinMax;
import de.tum.mitfahr.util.LocationUtil;
import de.tum.mitfahr.util.StringHelper;

/**
 * Authored by Abhijith on 22/05/14.
 */
public class CreateRidesFragment extends AbstractNavigationFragment implements
        CalendarDatePickerDialog.OnDateSetListener,
        RadialTimePickerDialog.OnTimeSetListener,
        MultiDatePickerDialog.MultiDatePickerDialogListener {

    Calendar now;
    LocalDate setDate;

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";

    @InjectView(R.id.departureText)
    AutoCompleteTextView departureText;
    @InjectView(R.id.destinationText)
    AutoCompleteTextView destinationText;
    @InjectView(R.id.meetingText)
    EditText meetingText;
    @InjectView(R.id.seatsText)
    EditText seatsText;
    @InjectView(R.id.rideTypeSpinner)
    Spinner rideTypeSpinner;
    @InjectView(R.id.offerRideButton)
    CircularProgressButton offerRideButton;
    @InjectView(R.id.pickTimeButton)
    Button pickTimeButton;
    @InjectView(R.id.pickDateButton)
    Button pickDateButton;

    private User mCurrentUser;
    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private boolean invalidDate = false;
    private int mHour = 0;
    private int mMinute = 0;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private List<Date> mRepeatDates = null;
    private Geocoder geocoder;
    private Handler mCreateButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            offerRideButton.setProgress(0);
            offerRideButton.setClickable(true);
        }
    };

    public CreateRidesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CreateRidesFragment newInstance(int sectionNumber) {
        CreateRidesFragment fragment = new CreateRidesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 10);
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);
        mCurrentUser = TUMitfahrApplication.getApplication(getActivity()).getProfileService().getUserFromPreferences();
        geocoder = new Geocoder(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_rides, container, false);
        ButterKnife.inject(this, rootView);

        offerRideButton.setIndeterminateProgressMode(true);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

        Calendar cal;
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        String modifiedTime = dateFormat.format(cal.getTime());
        String[] time2 = modifiedTime.split(",");

        pickTimeButton.setText(time2[1]);
        pickDateButton.setText(time2[0]);

        ArrayAdapter<CharSequence> mRideTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.ride_array_create));
        mRideTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideTypeSpinner.setAdapter(mRideTypeAdapter);

        ArrayAdapter<CharSequence> mUserTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.user_array));
        mUserTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity());
        departureText.setAdapter(adapter);
        destinationText.setAdapter(adapter);
        seatsText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
    }

    @OnClick(R.id.offerRideButton)
    public void onOfferRidePressed(Button button) {
        String departureInput = departureText.getText().toString();
        String destinationInput = destinationText.getText().toString();
        String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        LatLng destinationLatLng = LocationUtil.getLocationFromAddress(geocoder, destination);
        LatLng departureLatLng = LocationUtil.getLocationFromAddress(geocoder, departure);

        if (destinationLatLng == null || departureLatLng == null) {
            Toast.makeText(getActivity(), getString(R.string.googleServerUnavailable), Toast.LENGTH_LONG).show();
            return;
        }

        boolean doCreate = true;

        if (departureInput.equals(destinationInput) && !departureInput.isEmpty() && !destinationInput.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.sameDepAndDestMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringHelper.isBlank(departureText.getText().toString())) {
            departureText.setError(getResources().getString(R.string.error_required));
            doCreate = false;
        } else if (departureLatLng.latitude == 0 && departureLatLng.longitude == 0) {
            departureText.setError(getResources().getString(R.string.error_notValid_departure));
            doCreate = false;
        } else {
            departureText.setError(null);
        }

        if (StringHelper.isBlank(destinationText.getText().toString())) {
            destinationText.setError(getResources().getString(R.string.error_required));
            doCreate = false;
        } else if (destinationLatLng.latitude == 0 && destinationLatLng.longitude == 0) {
            destinationText.setError(getResources().getString(R.string.error_notValid_destination));
            doCreate = false;
        } else {
            destinationText.setError(null);
        }

        if (StringHelper.isBlank(meetingText.getText().toString())) {
            meetingText.setError(getResources().getString(R.string.error_required));
            doCreate = false;
        } else {
            meetingText.setError(null);
        }

        if (StringHelper.isBlank(seatsText.getText().toString())) {
            seatsText.setError(getResources().getString(R.string.error_required));
            doCreate = false;
        } else {
            int freeSeats;

            freeSeats = Integer.parseInt(seatsText.getText().toString().trim());

            if (freeSeats < 1) {
                seatsText.setError(getResources().getString(R.string.invalid));
            } else seatsText.setError(null);
        }

        if (doCreate) {

            String meetingPoint = meetingText.getText().toString();
            int freeSeats = 1;
            if (!StringHelper.isBlank(seatsText.getText().toString()))
                freeSeats = Integer.parseInt(seatsText.getText().toString());
            String dateTime = getFormattedDate();
            SimpleDateFormat oF = new SimpleDateFormat(dateTime);
            Calendar cal = oF.getCalendar();
            LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
            LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());

            if (setDate == null) {
                setDate = localDate;
            }

            if (setDate.equals(localDate) && mHourOfDeparture <= (localTime.getHourOfDay()) && mMinuteOfDeparture <= (localTime.getMinuteOfHour())) {

                Toast.makeText(getActivity(), getString(R.string.createRide_invalidTime), Toast.LENGTH_SHORT).show();
                return;

            }

            int rideType = rideTypeSpinner.getSelectedItemPosition();

            if (!StringHelper.isBlank(departure) && !StringHelper.isBlank(destination) && !StringHelper.isBlank(meetingPoint) && !StringHelper.isBlank(dateTime)) {
                offerRideButton.setProgress(50);

                TUMitfahrApplication.getApplication(getActivity()).getRidesService()
                        .offerRide(departure, destination, departureLatLng.latitude, departureLatLng.longitude, destinationLatLng.latitude,
                                destinationLatLng.longitude, meetingPoint, freeSeats, dateTime, rideType,
                                true, mCurrentUser.getCar(), getRepeatDates());
            }
        }
    }

    public String getFormattedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        SimpleDateFormat outputFormat = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
        calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
        calendar.set(Calendar.YEAR, mYearOfDeparture);
        calendar.set(Calendar.MONTH, mMonthOfDeparture);
        calendar.set(Calendar.DAY_OF_MONTH, mDayOfDeparture);
        outputFormat.setTimeZone(TimeZone.getDefault());
        return outputFormat.format(calendar.getTime());
    }

    public List<String> getRepeatDates() {
        if (mRepeatDates == null) {
            return null;
        }
        List<String> repeatDateString = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Date date : mRepeatDates) {
            String[] dateTime = simpleDateFormat.format(date).split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            SimpleDateFormat outputFormat = new SimpleDateFormat(getResources().getString(R.string.dateFormat));
            calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
            calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
            calendar.set(Calendar.YEAR, Integer.parseInt(dateTime[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateTime[1]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime[0]));
            outputFormat.setTimeZone(TimeZone.getDefault());
            repeatDateString.add(outputFormat.format(calendar.getTime()));
        }
        return repeatDateString.size() > 0 ? repeatDateString : null;
    }

    @OnClick(R.id.pickTimeButton)
    public void showTimePickerDialog() {

        RadialTimePickerDialog timePickerDialog;
        LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());
        LocalTime localTime1 = localTime.plusMinutes(10);

        if (mHour == 0) {
            timePickerDialog = RadialTimePickerDialog.newInstance(this, localTime1.getHourOfDay(), localTime1.getMinuteOfHour(), true);
        } else {
            timePickerDialog = RadialTimePickerDialog.newInstance(this, mHour, mMinute, true);

        }
        timePickerDialog.show(getChildFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {

        CalendarDatePickerDialog calendarDatePickerDialog;
        FragmentManager fm = getChildFragmentManager();

        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        if (mDay == 0) {
            calendarDatePickerDialog = CalendarDatePickerDialog
                    .newInstance(this, localDate.getYear(), localDate.getMonthOfYear() - 1,
                            localDate.getDayOfMonth());
        } else {
            calendarDatePickerDialog = CalendarDatePickerDialog
                    .newInstance(this, mYear, mMonth,
                            mDay);
        }
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Subscribe
    public void onOfferRideEvent(OfferRideEvent event) {
        if (event.getType() == OfferRideEvent.Type.RIDE_ADDED) {
            Toast.makeText(getActivity(), getString(R.string.createRide_success), Toast.LENGTH_SHORT).show();
            offerRideButton.setProgress(100);
            if (event.getRide() != null) {
                Intent intent = new Intent(getActivity(), RideDetailsActivity.class);
                intent.putExtra(RideDetailsActivity.RIDE_INTENT_EXTRA, event.getRide());
                intent.putExtra(RideDetailsActivity.DIRECTED_FROM_CREATE, "fromCreate");

                startActivityForResult(intent, RideDetailsActivity.DIRECTED_FROM_TIMELINE);
            }
        } else if (event.getType() == OfferRideEvent.Type.OFFER_RIDE_FAILED) {
            String errors[] = event.getResponse().getErrors();
            for (String error : errors) {
                if (error.contains("departure") && error.contains("Europe")) {
                    departureText.setError(getResources().getString(R.string.error_notInEurope_departure));
                } else if (error.contains("destination") && error.contains("Europe")) {
                    destinationText.setError(getResources().getString(R.string.error_notInEurope_destination));
                }
            }
            offerRideButton.setProgress(-1);
        } else {
            offerRideButton.setProgress(0);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mCreateButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String dateString = Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" + Integer.toString(year);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");


        setDate = formatter.parseLocalDate(dateString);
        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());

        if (mHour == 0 && mMinute == 0) {
            Calendar cal;
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 10);
            mHour = cal.get(Calendar.HOUR_OF_DAY);
            mMinute = cal.get(Calendar.MINUTE);

        }

        if (setDate.isBefore(localDate) || ((setDate.equals(localDate) && (mHour <= localTime.getHourOfDay()) && (mMinute <= localTime.getMinuteOfHour())))) {
            Toast.makeText(getActivity(), getString(R.string.createRide_invalidDate), Toast.LENGTH_SHORT).show();
            invalidDate = true;
            return;
        }

        invalidDate = false;
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));

        String date = String.format("%02d/%02d/" + year, dayOfMonth, monthOfYear + 1);
        pickDateButton.setText(date);

    }

    @Override
    public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {


        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());
        LocalDate newDate;

        if (setDate == null || invalidDate) {
            newDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        } else
            newDate = setDate;

        if (((newDate.equals(localDate)) && hourOfDay < localTime.getHourOfDay()) || ((newDate.equals(localDate)) && ((hourOfDay == localTime.getHourOfDay()) && minute <= localTime.getMinuteOfHour()))) {
            Toast.makeText(getActivity(), getString(R.string.createRide_invalidTime), Toast.LENGTH_SHORT).show();
        } else {
            mHourOfDeparture = hourOfDay;
            mMinuteOfDeparture = minute;

            mHour = hourOfDay;
            mMinute = minute;

            String time = String.format("%02d:%02d", hourOfDay, minute);
            pickTimeButton.setText(time);
        }
    }

    @Override
    public void onMultiDatePicked(DialogFragment dialog, List<Date> selectedDates) {
        if (selectedDates != null && selectedDates.size() > 1) {
            mRepeatDates = selectedDates;
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            String repeatString = "";
            for (Date date : selectedDates) {
                String dateStr = dt.format(date);
                repeatString = repeatString + dateStr + " ; ";
            }
        }
    }
}
