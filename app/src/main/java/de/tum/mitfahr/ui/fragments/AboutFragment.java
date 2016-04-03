package de.tum.mitfahr.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.tum.mitfahr.R;

/**
 * Authored by abhijith on 17/10/14.
 */
public class AboutFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    int mNum;

    @InjectView(R.id.about_background)
    ImageView mBackgroundView;

    @InjectView(R.id.about_image)
    ImageView mImageView;

    @InjectView(R.id.about_heading)
    TextView mHeadingView;

    @InjectView(R.id.about_description)
    TextView mDescriptionView;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, rootView);
        switch (mNum) {
            case 1:
                mBackgroundView.setBackgroundResource(R.drawable.about1);
                mImageView.setImageResource(R.drawable.about_campus);
                mHeadingView.setText(R.string.about_heading1);
                mDescriptionView.setText(R.string.about_description1);
                break;
            case 2:
                mBackgroundView.setBackgroundResource(R.drawable.about2);
                mImageView.setImageResource(R.drawable.about_activity);
                mHeadingView.setText(R.string.about_heading2);
                mDescriptionView.setText(R.string.about_description2);
                break;
            case 3:
                mBackgroundView.setBackgroundResource(R.drawable.about3);
                mImageView.setImageResource(R.drawable.about_seats);
                mHeadingView.setText(R.string.about_heading3);
                mDescriptionView.setText(R.string.about_description3);
                break;
        }
        return rootView;
    }
}
