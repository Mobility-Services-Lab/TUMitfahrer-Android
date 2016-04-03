package de.tum.mitfahr.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.tum.mitfahr.events.ForgotPasswordEvent;
import de.tum.mitfahr.util.StringHelper;

public class ForgotPasswordFragment extends Fragment implements KenBurnsView.TransitionListener {

    private ForgotPasswordClickListener mListener;
    private static final int BG_TINT = 0x7F000000;
    private Context mContext;

    @InjectView(R.id.emailEditText)
    EditText emailText;

    @InjectView(R.id.sendPasswordButton)
    CircularProgressButton sendButton;

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

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    public ForgotPasswordFragment() {
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
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.inject(this, view);
        sendButton.setIndeterminateProgressMode(true);
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
    }

    @OnClick(R.id.sendPasswordButton)
    public void onSendPasswordButtonPressed() {
        String email = emailText.getText().toString();
        if (!StringHelper.isBlank(email)) {
            sendButton.setClickable(false);
            sendButton.setProgress(50);
            TUMitfahrApplication.getApplication(mContext).getProfileService().forgotPassword(email.trim());
        } else {
            Toast.makeText(getActivity(), "Please fill all the required fields to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onPasswordReset(ForgotPasswordEvent event) {
        if(event.getType() == ForgotPasswordEvent.Type.SUCCESS) {
            Toast.makeText(getActivity(), "Password requested.", Toast.LENGTH_SHORT).show();
            sendButton.setProgress(100);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mSendButtonHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if(event.getType() == ForgotPasswordEvent.Type.NOT_USER) {
            Toast.makeText(getActivity(), "No such user found. Please register.", Toast.LENGTH_LONG).show();
            sendButton.setProgress(-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mSendButtonHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else if(event.getType() == ForgotPasswordEvent.Type.FAIL) {
            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            sendButton.setProgress(-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        mSendButtonHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    private Handler mSendButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendButton.setProgress(0);
            sendButton.setClickable(true);
            mListener.onForgotPasswordFinished();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ForgotPasswordClickListener) activity;
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

    public interface ForgotPasswordClickListener {
        void onForgotPasswordFinished();
    }

}
