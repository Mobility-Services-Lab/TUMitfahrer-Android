package de.tum.mitfahr.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import de.tum.mitfahr.R;
import de.tum.mitfahr.TUMitfahrApplication;
import de.tum.mitfahr.networking.models.User;
import de.tum.mitfahr.util.RoundTransform;
import de.tum.mitfahr.util.StringHelper;

/**
 * Created by abhijith on 03/10/14.
 */
public class PassengerItemView extends RelativeLayout implements View.OnClickListener {

    public static final int TYPE_PASSENGER = 0;
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_NONE = 2;
    private int mItemType = TYPE_NONE;
    private final CircularImageView mProfileImage;
    private final TextView mPassengerName;
    private final ImageButton mRemoveButton;
    private final ImageButton mActionButton;
    private final Context mContext;
    private User mPassenger;

    private PassengerItemClickListener mListener;
    private PassengerItemClickListener sDummyListener = new PassengerItemClickListener() {
        @Override
        public void onRemoveClicked(User passenger) {

        }

        @Override
        public void onActionClicked(User passenger) {

        }

        @Override
        public void onUserClicked(User passenger) {

        }
    };

    public PassengerItemView(Context context) {
        this(context, null, 0);
    }

    public PassengerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PassengerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.details_passenger_composite_view, this, true);
        mProfileImage = (CircularImageView) findViewById(R.id.passenger_image_view);
        mPassengerName = (TextView) findViewById(R.id.passenger_name);
        mRemoveButton = (ImageButton) findViewById(R.id.remove_image_button);
        mActionButton = (ImageButton) findViewById(R.id.action_image_button);

        mListener = sDummyListener;

        mActionButton.setBackgroundDrawable(getRoundedShapeDrawable(getResources().getColor(android.R.color.holo_green_light)));
        mActionButton.setImageResource(R.drawable.ic_check_white_24dp);

        mRemoveButton.setBackgroundDrawable(getRoundedShapeDrawable(getResources().getColor(android.R.color.holo_red_light)));
        mRemoveButton.setImageResource(R.drawable.ic_cancel_white_24dp);

        mRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveClicked(mPassenger);
            }
        });

        mActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onActionClicked(mPassenger);
            }
        });

        mProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onUserClicked(mPassenger);
            }
        });
        setItemType(TYPE_NONE);
    }

    private void showButtons(boolean action, boolean remove) {
        if (action) {
            mActionButton.setVisibility(VISIBLE);
        } else {
            mActionButton.setVisibility(GONE);
        }
        if (remove) {
            mRemoveButton.setVisibility(VISIBLE);
        } else {
            mRemoveButton.setVisibility(GONE);
        }
    }

    public User getPassenger() {
        return this.mPassenger;
    }

    public void setPassenger(User passenger) {
        this.mPassenger = passenger;
        updateView();
    }

    public void setListener(PassengerItemClickListener listener) {
        this.mListener = listener;
    }

    private void updateView() {

        User currentUser = TUMitfahrApplication.getApplication(mContext).getProfileService().getUserFromPreferences();
        String profileImageUrl = TUMitfahrApplication.getApplication(mContext).getProfileService().getProfileImageURL(mPassenger.getId());
        if(currentUser.getId() == mPassenger.getId()) { //if the user is the passenger, use stored url
            profileImageUrl = TUMitfahrApplication.getApplication(mContext).getProfileService().getStoredAvatarURL();
            if( StringHelper.isBlank(profileImageUrl) ) profileImageUrl = TUMitfahrApplication.getApplication(mContext).getProfileService().getProfileImage();

        }

        mPassengerName.setText(mPassenger.getFirstName() + " " + mPassenger.getLastName());
        Picasso.with(mContext)
                .load(profileImageUrl)
                .skipMemoryCache()
                .placeholder(R.drawable.ic_account_dark)
                .error(R.drawable.ic_account_dark)
                .fit().centerCrop()
                .transform(new RoundTransform())
                .into(mProfileImage);
    }

    public void setItemType(int type) {
        this.mItemType = type;
        if (mItemType == TYPE_PASSENGER) {
            showButtons(false, true);
        } else if (mItemType == TYPE_REQUEST) {
            showButtons(true, true);
        } else {
            showButtons(false, false);
        }
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    private Drawable getRoundedShapeDrawable(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(8);
        shape.setColor(color);
        return shape;
    }

    public interface PassengerItemClickListener {

        void onRemoveClicked(User passenger);

        void onActionClicked(User passenger);

        void onUserClicked(User passenger);
    }
}