package de.tum.mitfahr.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.LruCache;

/**
 * Authored by abhijith on 20/06/14.
 */
public class TypefaceManager {

    private static final String ROBOTO_LIGHT_FILENAME = "Roboto-Light.ttf";
    private static final String ROBOTO_CONDENSED_FILENAME = "RobotoCondensed-Regular.ttf";
    private static final String ROBOTO_CONDENSED_BOLD_FILENAME = "RobotoCondensed-Bold.ttf";
    private static final String ROBOTO_CONDENSED_LIGHT_FILENAME = "RobotoCondensed-Light.ttf";
    private static final String ROBOTO_SLAB_FILENAME = "RobotoSlab-Regular.ttf";

    private static final String ROBOTO_LIGHT_NATIVE_FONT_FAMILY = "sans-serif-light";
    private static final String ROBOTO_CONDENSED_NATIVE_FONT_FAMILY = "sans-serif-condensed";

    private final LruCache<String, Typeface> mCache;
    private final AssetManager mAssetManager;

    public TypefaceManager(AssetManager assetManager) {
        if (assetManager != null) {
            mAssetManager = assetManager;
        } else {
            throw new RuntimeException("AssetManager cannot be null");
        }
        mCache = new LruCache<String, Typeface>(3);
    }

    public Typeface getRobotoLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_LIGHT_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_LIGHT_FILENAME);
    }

    public Typeface getRobotoCondensed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.NORMAL);
        }
        return getTypeface(ROBOTO_CONDENSED_FILENAME);
    }

    public Typeface getRobotoCondensedBold() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return Typeface.create(ROBOTO_CONDENSED_NATIVE_FONT_FAMILY, Typeface.BOLD);
        }
        return getTypeface(ROBOTO_CONDENSED_BOLD_FILENAME);
    }

    public Typeface getRobotoCondensedLight() {
        return getTypeface(ROBOTO_CONDENSED_LIGHT_FILENAME);
    }

    public Typeface getRobotoSlab() {
        return getTypeface(ROBOTO_SLAB_FILENAME);
    }

    private Typeface getTypeface(final String filename) {
        Typeface typeface = mCache.get(filename);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mAssetManager, "fonts/" + filename);
            mCache.put(filename, typeface);
        }
        return typeface;
    }
}
