package de.tum.mitfahr.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import de.tum.mitfahr.R;

/**
 * Created by Monika Ullrich on 15.11.2015.
 */

public class GeneralTermsAndConditionsActivity extends ActionBarActivity {

    private WebView mWebView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_terms_and_conditions);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mWebView = ((WebView) findViewById(R.id.web_view));
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        mWebView.loadUrl("file:///android_asset/agb.html");

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
}
