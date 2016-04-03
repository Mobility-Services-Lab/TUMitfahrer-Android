package de.tum.mitfahr.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.squareup.otto.Subscribe;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.adapters.LocationAutoCompleteAdapter;
import de.tum.mitfahr.events.SearchEvent;
import de.tum.mitfahr.networking.models.Ride;
import de.tum.mitfahr.ui.SearchResultsActivity;
import de.tum.mitfahr.util.StringHelper;

/**
 * Authored by abhijith on 22/05/14.
 */
public class SearchFragment extends AbstractNavigationFragment implements CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";


    public static final int RIDE_TYPE_CAMPUS = 0;
    public static final int RIDE_TYPE_ACTIVITY = 1;
    public static final int RIDE_TYPE_ALL = 2;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private int mRideType = RIDE_TYPE_ALL;
    private double mFromRadius = 1.0;
    private double mToRadius = 1.0;
    private int mHourOfDeparture;
    private int mMinuteOfDeparture;
    private int mYearOfDeparture;
    private int mMonthOfDeparture;
    private int mDayOfDeparture;
    private LocalDate setDate;
    private Boolean invalidDate = false;
    private ArrayAdapter<CharSequence> mSearchRideTypeAdapter;
    private Handler mSearchButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            searchButton.setProgress(0);
            searchButton.setClickable(true);
        }
    };

    @InjectView(R.id.fromSearchEditText)
    AutoCompleteTextView fromText;

    @InjectView(R.id.toSearchEditText)
    AutoCompleteTextView toText;


    @InjectView(R.id.fromRadiusSeekBar)
    SeekBar fromRadiusSeekBar;

    @InjectView(R.id.toRadiusSeekBar)
    SeekBar toRadiusSeekBar;

    @InjectView(R.id.fromRadiusTextView)
    TextView fromRadiusTextView;

    @InjectView(R.id.toRadiusTextView)
    TextView toRadiusTextView;

    @InjectView(R.id.pickTimeButton)
    Button pickTimeButton;

    @InjectView(R.id.pickDateButton)
    Button pickDateButton;

    @InjectView(R.id.searchButton)
    CircularProgressButton searchButton;

    @InjectView(R.id.searchRideTypeSpinner)
    Spinner searchRideTypeSpinner;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        mHourOfDeparture = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteOfDeparture = calendar.get(Calendar.MINUTE);
        mYearOfDeparture = calendar.get(Calendar.YEAR);
        mMonthOfDeparture = calendar.get(Calendar.MONTH);
        mDayOfDeparture = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, rootView);

        searchButton.setIndeterminateProgressMode(true);
        changeActionBarColor(getResources().getColor(R.color.blue3));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

        String[] dateTime = dateTimeString.split(",");

        pickTimeButton.setText(dateTime[1]);
        pickDateButton.setText(dateTime[0]);

        mSearchRideTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getTextArray(R.array.ride_array_search));
        mSearchRideTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchRideTypeSpinner.setAdapter(mSearchRideTypeAdapter);
        searchRideTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mRideType = RIDE_TYPE_ALL;
                } else if (position == 1) {
                    mRideType = RIDE_TYPE_CAMPUS;
                } else {
                    mRideType = RIDE_TYPE_ACTIVITY;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mRideType = RIDE_TYPE_ALL;
            }
        });

        fromRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFromRadius = ((double) progress) / 4.0;
                fromRadiusTextView.setText(mFromRadius + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        toRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mToRadius = ((double) progress) / 4.0;
                toRadiusTextView.setText(mToRadius + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity());
        fromText.setAdapter(adapter);
        toText.setAdapter(adapter);
    }


    @OnClick(R.id.searchButton)
    public void onSearchPressed(Button button) {

        String searchFrom = fromText.getText().toString();
        String searchTo = toText.getText().toString();
        List<Float> departure_latlong = getLatLong(searchFrom);
        List<Float> destination_latlong = getLatLong(searchTo);

        if (departure_latlong == null || destination_latlong == null) {
            Toast.makeText(getActivity(), getString(R.string.googleServerUnavailable), Toast.LENGTH_LONG).show();
            return;
        }


        boolean doSearch = true;

        if (searchFrom.equals(searchTo) && !searchFrom.isEmpty() && !searchTo.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.sameDepAndDestMsg), Toast.LENGTH_SHORT).show();
            doSearch = false;
        }
        if (StringHelper.isBlank(fromText.getText().toString())) {
            fromText.setError(getResources().getString(R.string.error_required));
            doSearch = false;
        } else if (departure_latlong.isEmpty()) {
            fromText.setError(getResources().getString(R.string.error_notValid_departure));
            doSearch = false;
        }
        if (StringHelper.isBlank(toText.getText().toString())) {
            toText.setError(getResources().getString(R.string.error_required));
            doSearch = false;
        } else if (destination_latlong.isEmpty()) {
            toText.setError(getResources().getString(R.string.error_notValid_destination));
            doSearch = false;
        }
        if (!departure_latlong.isEmpty() && departure_latlong.get(0) == 0 && departure_latlong.get(1) == 0) {
            //departure is not in Europe
            fromText.setError(getResources().getString(R.string.error_notInEurope_departure));
            doSearch = false;
        }
        if (!destination_latlong.isEmpty() && destination_latlong.get(0) == 0 && destination_latlong.get(1) == 0) {
            //destination is not in Europe
            toText.setError(getResources().getString(R.string.error_notInEurope_destination));
            doSearch = false;
        }
        if (doSearch) {
            String dateTime = getFormattedDate();
            searchButton.setProgress(50);

            //The backend expects rideType = null if the search should return all rides.
            if (mRideType == RIDE_TYPE_ALL) {
                TUMitfahrApplication.getApplication(getActivity()).getSearchService()
                        .search(searchFrom, mFromRadius, searchTo, mToRadius, dateTime, null, departure_latlong.get(0), departure_latlong.get(1), destination_latlong.get(0), destination_latlong.get(1));

            } else {
                TUMitfahrApplication.getApplication(getActivity()).getSearchService()
                        .search(searchFrom, mFromRadius, searchTo, mToRadius, dateTime, mRideType, departure_latlong.get(0), departure_latlong.get(1), destination_latlong.get(0), destination_latlong.get(1));
            }
        }
    }

    @OnClick(R.id.pickTimeButton)
    public void showTimePickerDialog() {
        RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, mHourOfDeparture, mMinuteOfDeparture, true);
        timePickerDialog.show(getChildFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    @OnClick(R.id.pickDateButton)
    public void showDatePickerDialog() {
        FragmentManager fm = getChildFragmentManager();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(this, mYearOfDeparture, mMonthOfDeparture,
                        mDayOfDeparture);
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    /**
     * Formats the date to yyyy-MM-dd HH:mm
     *
     * @return the formatted date
     */
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

    /**
     * Method that transforms address to latitude and longitude. The address has to be in Europe, otherwise latitude and longitude will be 0.
     *
     * @param address the address to be transformed into latitude and longitude
     */
    private List<Float> getLatLong(String address) {
        List<Float> res = new ArrayList<>();

        Geocoder geocoder = new Geocoder(this.getActivity());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address2 = addressList.get(0);
                float lat = (float) address2.getLatitude();
                float lon = (float) address2.getLongitude();
                if (lat < 34.039745 || lat > 71.755312 || lon < -15.625000 || lon > 49.062500) {
                    lat = 0;
                    lon = 0;
                }
                res.add(lat);
                res.add(lon);

            }
        } catch (IOException e) {
            e.printStackTrace();
            //server not available
            return null;
        }

        return res;
    }

    @Subscribe
    public void onSearchResults(SearchEvent event) {
        if (event.getType() == SearchEvent.Type.SEARCH_SUCCESSFUL) {
            String from = fromText.getText().toString().trim();
            String to = toText.getText().toString().trim();
            searchButton.setProgress(100);
            List<Ride> rideResults = event.getResponse();

            if (rideResults.size() > 0) {
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_RIDES, (java.io.Serializable) rideResults);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_FROM, from);
                intent.putExtra(SearchResultsActivity.SEARCH_RIDE_RESULT_INTENT_TO, to);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.NoResults), Toast.LENGTH_SHORT).show();
            }

        } else if (event.getType() == SearchEvent.Type.SEARCH_FAILED) {
            searchButton.setProgress(0);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle(getResources().getString(R.string.NoRides));
            alertDialogBuilder.setMessage(getResources().getString(R.string.NoRides_Detail)).setCancelable(true).setNegativeButton(getResources().getString(R.string.okButton), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mSearchButtonHandler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {

        String dateString = Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" + Integer.toString(year);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        setDate = formatter.parseLocalDate(dateString);
        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());

        if (setDate.isBefore(localDate) || ((setDate.equals(localDate) && (mHourOfDeparture <= localTime.getHourOfDay()) && (mMinuteOfDeparture <= localTime.getMinuteOfHour())))) {

            Toast.makeText(getActivity(), getString(R.string.searchRide_invalidDate), Toast.LENGTH_SHORT).show();
            invalidDate = true;
            return;
        }

        invalidDate = false;
        mYearOfDeparture = year;
        mMonthOfDeparture = monthOfYear;
        mDayOfDeparture = dayOfMonth;
        Log.e("MONTH:", String.valueOf(mMonthOfDeparture));
        String date = String.format("%02d/%02d/" + year, dayOfMonth, monthOfYear + 1);
        pickDateButton.setText(date);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialog radialPickerLayout, int hourOfDay, int minute) {
        LocalDate newDate;
        LocalDate localDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        LocalTime localTime = LocalTime.fromCalendarFields(Calendar.getInstance());
        if (setDate == null || invalidDate) {
            newDate = LocalDate.fromCalendarFields(Calendar.getInstance());
        } else
            newDate = setDate;
        if (((newDate.equals(localDate)) && hourOfDay < localTime.getHourOfDay()) || ((newDate.equals(localDate)) && ((hourOfDay == localTime.getHourOfDay()) && minute <= localTime.getMinuteOfHour()))) {
            Toast.makeText(getActivity(), getString(R.string.searchRide_invalidTime), Toast.LENGTH_SHORT).show();
            return;
        }

        mHourOfDeparture = hourOfDay;
        mMinuteOfDeparture = minute;
        String time = String.format("%02d:%02d", hourOfDay, minute);
        pickTimeButton.setText(time);
    }

}
