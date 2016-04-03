package de.tum.mitfahr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

//import com.google.analytics




import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.StringHelper;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by abhijith on 04/11/14.
 */
public class EditRideActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

    public static final String RIDE_INTENT_EXTRA = "selected_ride";

    public static final long INTERVAL = 1000 * 60 * 10;

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";

    public static final int RIDE_TYPE_CAMPUS = 0;
    public static final int RIDE_TYPE_ACTIVITY = 1;

    private User mCurrentUser;
    private Ride mRide;

   // private Tracker appTracker;


    @InjectView(R.id.segmentedRequestType)
    SegmentedGroup requestTypeSegmentedGroup;

    @InjectView(R.id.departureText)
    AutoCompleteTextView departureText;

    @InjectView(R.id.destinationText)
    AutoCompleteTextView destinationText;

    @InjectView(R.id.meetingText)
    EditText meetingText;

    @InjectView(R.id.seatsText)
    EditText seatsText;

    @InjectView(R.id.seatsTextContainer)
    View seatsTextContainer;

    @InjectView(R.id.rideTypeSpinner)
    Spinner rideTypeSpinner;

    @InjectView(R.id.editRideButton)
    CircularProgressButton editRideButton;

    @InjectView(R.id.pickTimeButton)
    Button pickTimeButton;

    @InjectView(R.id.pickDateButton)
    Button pickDateButton;

    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private int mRideType = RIDE_TYPE_CAMPUS;
    private boolean driver = false;
    private ArrayAdapter<CharSequence> mRideTypeAdapter;
    private Handler mEditButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            editRideButton.setProgress(0);
            editRideButton.setClickable(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if (intent.hasExtra(RIDE_INTENT_EXTRA)) {
            mRide = (Ride) intent.getSerializableExtra(RIDE_INTENT_EXTRA);
        } else {
            finish();
        }
        mCurrentUser = TUMitfahrApplication.getApplication(this).getProfileService().getUserFromPreferences();
        populateUI();
//        appTracker.setScreenName("Ride");
//        appTracker.se
    }

    private void populateUI() {
        destinationText.setText(mRide.getDestination());
        departureText.setText(mRide.getDeparturePlace());
        meetingText.setText(mRide.getMeetingPoint());
        if (mRide.isRideRequest()) {
            requestTypeSegmentedGroup.check(R.id.radioButtonPassenger);
        } else {
            requestTypeSegmentedGroup.check(R.id.radioButtonDriver);
            seatsText.setText(mRide.getFreeSeats());
        }
        mRideType = mRide.getRideType();
        rideTypeSpinner.setSelection(mRideType);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date departureTime = new Date();
        try {
            departureTime = outputFormat.parse(mRide.getDepartureTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(departureTime);
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        String dateTimeString = dateFormat.format(calendar.getTime());

        String[] dateTime = dateTimeString.split(",");


        long defaultTime = (System.currentTimeMillis() + INTERVAL);
        String defaultTime2 = Long.toString(defaultTime);

        DateFormat dateFormatOne = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String dateTimeStringOne = dateFormatOne.format(calendar.getTime());

        String[] dateTimeOne = dateTimeString.split(",");

        pickTimeButton.setText(dateTimeOne[1]);
        pickDateButton.setText(dateTimeOne[0]);
    }

    private void setUpViews() {
        requestTypeSegmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonDriver:
                        driver = true;
                        updateLayoutForDriverAndPassenger();
                        break;
                    case R.id.radioButtonPassenger:
                        driver = false;
                        updateLayoutForDriverAndPassenger();
                        break;
                    default:
                        driver = false;
                        updateLayoutForDriverAndPassenger();
                        break;
                }
            }
        });

        editRideButton.setIndeterminateProgressMode(true);
        mRideTypeAdapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.ride_array_create));
        mRideTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rideTypeSpinner.setAdapter(mRideTypeAdapter);
        rideTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mRideType = RIDE_TYPE_CAMPUS;
                } else {
                    mRideType = RIDE_TYPE_ACTIVITY;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRideType = RIDE_TYPE_CAMPUS;
            }
        });
        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(this);
        departureText.setAdapter(adapter);
        destinationText.setAdapter(adapter);

    }

    @OnClick(R.id.editRideButton)
    public void onEditRidePressed(Button button) {
        if (StringHelper.isBlank(departureText.getText().toString())) {
            departureText.setError("Required");
            return;
        } else if (StringHelper.isBlank(destinationText.getText().toString())) {
            destinationText.setError("Required");
            return;
        } else if (StringHelper.isBlank(meetingText.getText().toString())) {
            destinationText.setError("Required");
            return;
        } else if (driver && StringHelper.isBlank(seatsText.getText().toString())) {
            seatsText.setError("Required");
            return;
        }
        editRideButton.setClickable(false);
        String departure = departureText.getText().toString();
        String destination = destinationText.getText().toString();
        String meetingPoint = meetingText.getText().toString();
        int freeSeats = 0;
        if (!StringHelper.isBlank(seatsText.getText().toString()))
            freeSeats = Integer.parseInt(seatsText.getText().toString());
        String dateTime = getFormattedDate();
        int rideType = rideTypeSpinner.getSelectedItemPosition();
        int isDriver = (driver) ? 1 : 0;

        if (!StringHelper.isBlank(departure) && !StringHelper.isBlank(destination) && !StringHelper.isBlank(meetingPoint) && !StringHelper.isBlank(dateTime)) {
            editRideButton.setProgress(50);
            mRide.setDeparturePlace(departure);
            mRide.setDestination(destination);
            mRide.setMeetingPoint(meetingPoint);
            mRide.setFreeSeats(freeSeats);
            mRide.setRideType(rideType);
            mRide.setRideRequest(!driver);
            TUMitfahrApplication.getApplication(this).getRidesService()
                    .updateRide(mRide);
        }
    }

    public String getFormattedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        calendar.set(Calendar.HOUR_OF_DAY, mHourOfDeparture);
        calendar.set(Calendar.MINUTE, mMinuteOfDeparture);
        calendar.set(Calendar.SECOND, 0); // I just set them to 0
        calendar.set(Calendar.YEAR, mYearOfDeparture);
        calendar.set(Calendar.MONTH, mMonthOfDeparture);
        calendar.set(Calendar.DAY_OF_MONTH, mDayOfDeparture);
        outputFormat.setTimeZone(TimeZone.getDefault());
        return outputFormat.format(calendar.getTime());
    }

    private void updateLayoutForDriverAndPassenger() {
        if (driver) {
            seatsTextContainer.setVisibility(View.VISIBLE);
        } else {
            seatsTextContainer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.pickTimeButton)
    public void showTimePickerDialog() {
        DateTime now = DateTime.parse(mRide.getDepartureTime());
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), false);
        timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.parse(mRide.getDepartureTime());
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear + 1;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));
        String date = String.format("%02d/%02d/" + year, dayOfMonth, monthOfYear + 1);
        pickDateButton.setText(date);

    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
        mHourOfDeparture = hourOfDay;
        mMinuteOfDeparture = minute;
        if (hourOfDay > 12) {
            hourOfDay = hourOfDay - 12;
            String time = String.format("%02d:%02d pm", hourOfDay, minute);
            pickTimeButton.setText(time);
        } else {
            String time = String.format("%02d:%02d am", hourOfDay, minute);
            pickTimeButton.setText(time);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("Create Ride Screen");
    }
}
