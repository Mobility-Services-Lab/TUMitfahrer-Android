package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.dd.CircularProgressButton;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.LoginEvent;
import de.tum.mitfahr.util.StringHelper;

public class LoginFragment extends Fragment implements KenBurnsView.TransitionListener {

    private static final int BG_TINT = 0x7F000000;
    @InjectView(R.id.emailEditText)
    EditText emailText;
    @InjectView(R.id.passwordEditText)
    EditText passwordText;
    @InjectView(R.id.loginButton)
    CircularProgressButton loginButton;
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
    private RegisterClickListener mListener;
    private Context mContext;
    private int mTransitionsCount = 0;
    private Handler mLoginButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loginButton.setProgress(0);
            loginButton.setClickable(true);
        }
    };
    private Handler mLoginSuccessHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loginButton.setProgress(0);
            loginButton.setClickable(true);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String email) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        loginButton.setIndeterminateProgressMode(true);
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
        String emailArg = getArguments() != null ? getArguments().getString("email") : "";
        if (emailArg != null) {
            if (!emailArg.equals("")) {
                emailText.setText(emailArg);
            }
        }

    }

    @OnClick(R.id.loginButton)
    public void onLoginPressed() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (!StringHelper.isBlank(email) && !StringHelper.isBlank(password)) {
            loginButton.setClickable(false);
            loginButton.setProgress(50);
            TUMitfahrApplication.getApplication(mContext).getProfileService().login(email, password);

            //get departments if internet was switched off while starting the app
            if( TUMitfahrApplication.getApplication(mContext).getDepartments().isEmpty()){
                TUMitfahrApplication.getApplication(mContext).getDepartmentsFromBackend();
            }
        } else if(StringHelper.isBlank(email)){
            Toast.makeText(getActivity(), getString(R.string.login_emailRequired), Toast.LENGTH_SHORT).show();
        }else if(StringHelper.isBlank(password)){
            Toast.makeText(getActivity(), getString(R.string.login_passwordRequired), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.registerButton)
    public void onRegisterPressed(Button button) {
        if (mListener != null) {
            mListener.onRegisterClicked();
        }
    }

    @OnClick(R.id.forgotPasswordButton)
    public void onForgotPasswordPressed(Button button) {
        if (mListener != null) {
            mListener.onForgotPasswordClicked();
        }
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (event.getType() == LoginEvent.Type.LOGIN_SUCCESSFUL) {
            loginButton.setProgress(100);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        mLoginSuccessHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (event.getType() == LoginEvent.Type.LOGIN_FAILED) {
            loginButton.setProgress(-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mLoginButtonHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegisterClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegisterClickListener");
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
        TUMitfahrApplication.getInstance().trackScreenView("Login Screen");
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

    public interface RegisterClickListener {
        void onRegisterClicked();
        void onForgotPasswordClicked();
    }

}
