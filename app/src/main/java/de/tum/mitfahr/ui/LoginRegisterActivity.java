package de.tum.mitfahr.ui;

import android.app.Activity;
import android.os.Bundle;

import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;

public class LoginRegisterActivity extends Activity implements LoginFragment.RegisterClickListener,
        RegisterFragment.RegistrationFinishedListener,
        ForgotPasswordFragment.ForgotPasswordClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.login_bg);
        setContentView(R.layout.activity_login_register);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void onRegisterClicked() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onForgotPasswordClicked() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ForgotPasswordFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onForgotPasswordFinished() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onRegistrationFinished(String email) {
        LoginFragment loginFragment = LoginFragment.newInstance(email);
        getFragmentManager().popBackStack();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, loginFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("Login Register Screen");
    }
}
