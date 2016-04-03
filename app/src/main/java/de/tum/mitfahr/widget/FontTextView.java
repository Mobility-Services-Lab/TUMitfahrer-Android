package de.tum.mitfahr.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import de.tum.mitfahr.R;
import de.tum.mitfahr.util.TypefaceManager;

/**
 * Authored by abhijith on 20/06/14.
 */
public class FontTextView extends TextView {
    public static final int FONT_ROBOTO_LIGHT = 1;
    public static final int FONT_ROBOTO_CONDENSED = 2;
    public static final int FONT_ROBOTO_CONDENSED_LIGHT = 3;
    public static final int FONT_ROBOTO_CONDENSED_BOLD = 4;
    public static final int FONT_ROBOTO_SLAB = 5;

    private TypefaceManager mTypefaceManager;

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTypefaceManager = new TypefaceManager(context.getAssets());
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView);
            setFont(a.getInt(R.styleable.FontTextView_font, 0));
            a.recycle();
        }
    }

    public void setFont(final int customFont) {
        Typeface typeface = getFont(mTypefaceManager, customFont);
        if (typeface != null) {
            setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            setTypeface(typeface);
        }
    }

    public static Typeface getFont(TypefaceManager typefaceManager, final int customFont) {
        Typeface typeface = null;

        switch (customFont) {
            case FONT_ROBOTO_LIGHT:
                typeface = typefaceManager.getRobotoLight();
                break;
            case FONT_ROBOTO_CONDENSED:
                typeface = typefaceManager.getRobotoCondensed();
                break;
            case FONT_ROBOTO_CONDENSED_LIGHT:
                typeface = typefaceManager.getRobotoCondensedLight();
                break;
            case FONT_ROBOTO_CONDENSED_BOLD:
                typeface = typefaceManager.getRobotoCondensedBold();
                break;
            case FONT_ROBOTO_SLAB:
                typeface = typefaceManager.getRobotoSlab();
                break;
        }

        return typeface;
    }
}
