package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dd.CircularProgressButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.RegisterEvent;
import de.tum.mitfahr.networking.models.Department;
import de.tum.mitfahr.util.StringHelper;

public class RegisterFragment extends Fragment implements KenBurnsView.TransitionListener {

    private RegistrationFinishedListener mListener;
    private Context mContext;
    private static final int BG_TINT = 0x7F000000;
    private ArrayList<Department> departments = new ArrayList<>();

    @InjectView(R.id.emailEditText)
    EditText emailText;

    @InjectView(R.id.firstNameEditText)
    EditText firstNameText;

    @InjectView(R.id.lastNameEditText)
    EditText lastNameText;

    @InjectView(R.id.departmentSpinner)
    Spinner departmentSpinner;

    @InjectView(R.id.registerButton)
    CircularProgressButton registerButton;

    @InjectView(R.id.img1)
    KenBurnsView heroBg1;

    @InjectView(R.id.img2)
    KenBurnsView heroBg2;

    @InjectView(R.id.img3)
    KenBurnsView heroBg3;

    @InjectView(R.id.img4)
    KenBurnsView heroBg4;

    @InjectView(R.id.viewFlipper)
    ViewFlipper viewFlipper;

    private int mTransitionsCount = 0;


    private ArrayAdapter<String> mDepartmentAdapter;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.inject(this, view);
        registerButton.setIndeterminateProgressMode(true);
        heroBg1.setColorFilter(BG_TINT);
        heroBg1.setTransitionListener(this);
        heroBg2.setColorFilter(BG_TINT);
        heroBg2.setTransitionListener(this);
        heroBg3.setColorFilter(BG_TINT);
        heroBg3.setTransitionListener(this);
        heroBg4.setColorFilter(BG_TINT);
        heroBg4.setTransitionListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        departments = TUMitfahrApplication.getApplication(mContext).getDepartments();
        if (departments.isEmpty()){
            TUMitfahrApplication.getApplication(mContext).getDepartmentsFromBackend();
        }
        List<String> department_displayNames = new LinkedList<>();
        for (Department d : departments){
            department_displayNames.add(d.getFriendly_name());
        }

        mDepartmentAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_white, department_displayNames);
        departmentSpinner.setAdapter(mDepartmentAdapter);
        mDepartmentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // do nothing
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.registerButton)
    public void onRegisterPressed(Button button) {

        if (!StringHelper.isBlank(emailText.getText().toString())
                && !StringHelper.isBlank(firstNameText.getText().toString())
                && !StringHelper.isBlank(lastNameText.getText().toString())) {
            registerButton.setClickable(false);
            registerButton.setProgress(50);
            String email = emailText.getText().toString();
            String firstName = firstNameText.getText().toString();
            String lastName = lastNameText.getText().toString();
            String department = departments.get(departmentSpinner.getSelectedItemPosition()).getName();
            TUMitfahrApplication.getApplication(mContext).getProfileService().register(email, firstName, lastName, department);
        } else {
            Toast.makeText(getActivity(), "Please fill all the required fields to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onRegister(RegisterEvent event) {
        if (event.getType() == RegisterEvent.Type.REGISTER_SUCCESSFUL && mListener != null) {
            registerButton.setProgress(100);
            if (!StringHelper.isBlank(emailText.getText().toString())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            mRegisterationSuccessHandler.sendEmptyMessage(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } else if (event.getType() == RegisterEvent.Type.REGISTER_FAILED) {

            //Displaying error in the wrong entered fields

            String errors[] = event.getResponse().getErrors();
            for(String error : errors) {
                if(error.contains("mail address")) {
                    SpannableStringBuilder spannableStringMail = new SpannableStringBuilder(error);
                    emailText.setError(spannableStringMail);

                }
                if(error.contains("Email already exists.")) {
                    SpannableStringBuilder spannableStringMail = new SpannableStringBuilder(error);
                    emailText.setError(spannableStringMail);

                }
                if(error.contains("First name"))  {
                    SpannableStringBuilder spannableStringFirstName = new SpannableStringBuilder(error);
                    firstNameText.setError(spannableStringFirstName);

                }
                if(error.contains("Last name"))  {
                    SpannableStringBuilder spannableStringLastName = new SpannableStringBuilder(error);
                    lastNameText.setError(spannableStringLastName);
                }
            }
            registerButton.setProgress(-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mRegisterButtonHandler.sendEmptyMessage(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private Handler mRegisterButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            registerButton.setProgress(0);
            registerButton.setClickable(true);
        }
    };

    private Handler mRegisterationSuccessHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            registerButton.setProgress(0);
            registerButton.setClickable(true);
            mListener.onRegistrationFinished(emailText.getText().toString());
        }
    };

    @OnClick(R.id.cancelButton)
    public void onCancelClicked() {
        mListener.onRegistrationFinished("");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegistrationFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegistrationFinishedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == 3) {
            viewFlipper.showNext();
            mTransitionsCount = 0;
        }
    }

    public interface RegistrationFinishedListener {
        void onRegistrationFinished(String email);
    }

}
