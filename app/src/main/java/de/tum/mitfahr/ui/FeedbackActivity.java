package de.tum.mitfahr.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.tum.mitfahr.BusProvider;
import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.events.SendFeedbackEvent;
import de.tum.mitfahr.util.StringHelper;

public class FeedbackActivity extends ActionBarActivity {

    @InjectView(R.id.feedbackButton)
    CircularProgressButton feedbackButton;
    @InjectView(R.id.feedbackEditText)
    EditText feedbackText;
    private Handler mSendButtonHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);
        feedbackButton.setIndeterminateProgressMode(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.feedbackButton)
    public void onFeedbackClicked() {
        if (!StringHelper.isBlank(feedbackText.getText().toString())) {
            feedbackText.setError(null);
            feedbackButton.setClickable(false);
            feedbackButton.setProgress(50);
            TUMitfahrApplication.getApplication(this).getFeedbackService().
                    sendFeedback("Feedback", feedbackText.getText().toString());
        }else if(StringHelper.isBlank(feedbackText.getText().toString())){
            feedbackText.setError("Please enter text");

        }
    }

    @Subscribe
    public void onSendFeedbackResult(SendFeedbackEvent result) {
        Log.d("Feedback","InFeedback");
        if (result.getType() == SendFeedbackEvent.Type.SUCCESSFUL) {
            Log.d("Feedback", "feedback success");
            feedbackButton.setProgress(100);
        } else {
            feedbackButton.setProgress(-1);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        TUMitfahrApplication.getInstance().trackScreenView("Feedback Screen");
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }
}
