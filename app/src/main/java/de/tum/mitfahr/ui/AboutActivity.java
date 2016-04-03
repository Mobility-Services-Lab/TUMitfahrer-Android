package de.tum.mitfahr.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import de.tum.mitfahr.R;
import de.tum.mitfahr.ui.fragments.AboutFragment;

public class AboutActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                .addViewToParallax(new ParallaxTransformInformation(R.id.about_background, 2, 2))
                .addViewToParallax(new ParallaxTransformInformation(R.id.about_heading,-2f, -2f))
                .addViewToParallax(new ParallaxTransformInformation(R.id.about_description, -1f, -1f));

        mViewPager.setPageTransformer(true, pageTransformer);
        mViewPager.setPageMargin(3);
        mViewPager.setPageMarginDrawable(android.R.color.black);

        Button skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return AboutFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public class ParallaxPageTransformer implements ViewPager.PageTransformer {

        private List<ParallaxTransformInformation> mViewsToParallax
                = new ArrayList<>();

        public ParallaxPageTransformer() {
        }

        public ParallaxPageTransformer(List<ParallaxTransformInformation> viewsToParallax) {
            mViewsToParallax = viewsToParallax;
        }

        public ParallaxPageTransformer addViewToParallax(
                ParallaxTransformInformation viewInfo) {
            if (mViewsToParallax != null) {
                mViewsToParallax.add(viewInfo);
            }
            return this;
        }

        public void transformPage(View view, float position) {

            int pageWidth = view.getWidth();

            if (position < -1) {
                // This page is way off-screen to the left.
                view.setAlpha(1);

            } else if (position <= 1 && mViewsToParallax != null) { // [-1,1]
                for (ParallaxTransformInformation parallaxTransformInformation : mViewsToParallax) {
                    applyParallaxEffect(view, position, pageWidth, parallaxTransformInformation,
                            position > 0);
                }
            } else {
                // This page is way off-screen to the right.
                view.setAlpha(1);
            }
        }

        private void applyParallaxEffect(View view, float position, int pageWidth,
                                         ParallaxTransformInformation information, boolean isEnter) {
            if (information.isValid() && view.findViewById(information.resource) != null) {
                if (isEnter && !information.isEnterDefault()) {
                    view.findViewById(information.resource)
                            .setTranslationX(-position * (pageWidth / information.parallaxEnterEffect));
                } else if (!isEnter && !information.isExitDefault()) {
                    view.findViewById(information.resource)
                            .setTranslationX(-position * (pageWidth / information.parallaxExitEffect));
                }
            }
        }
    }

    /**
     * Information to make the parallax effect in a concrete view.
     * <p/>
     * parallaxEffect positive values reduces the speed of the view in the translation
     * ParallaxEffect negative values increase the speed of the view in the translation
     * Try values to see the different effects. I recommend 2, 0.75 and 0.5
     */
    public static class ParallaxTransformInformation {

        public static final float PARALLAX_EFFECT_DEFAULT = -101.1986f;

        int resource = -1;
        float parallaxEnterEffect = 1f;
        float parallaxExitEffect = 1f;

        public ParallaxTransformInformation(int resource, float parallaxEnterEffect,
                                            float parallaxExitEffect) {
            this.resource = resource;
            this.parallaxEnterEffect = parallaxEnterEffect;
            this.parallaxExitEffect = parallaxExitEffect;
        }

        public boolean isValid() {
            return parallaxEnterEffect != 0 && parallaxExitEffect != 0 && resource != -1;
        }

        public boolean isEnterDefault() {
            return parallaxEnterEffect == PARALLAX_EFFECT_DEFAULT;
        }

        public boolean isExitDefault() {
            return parallaxExitEffect == PARALLAX_EFFECT_DEFAULT;
        }
    }
}
