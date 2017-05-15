package de.thb.ue.android.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by scorp on 18.02.2017.
 *
 * version testing code from: https://github.com/davideas/FlexibleAdapter
 */

public class Utils {

    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;

        if (show && Float.compare(view.getAlpha(), toAlpha) != 0) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

    public static File getImageDirectory(Context context){
        String extMediaState = Environment.getExternalStorageState();
        File out = null;
        // choose external storage if available
        if(extMediaState.equals(Environment.MEDIA_MOUNTED)){
            out = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            out = context.getFilesDir();
        }
        return out;
    }

    /**
     * Colors a string in the given color.
     * Uses Html to do so.
     * @param string the String to be colored
     * @param color the color which colors the string
     * @return magic
     */
//    public static Spanned colorString(String start, String middle, String end, int partToColor, @ColorRes int color){
//        SpannableStringBuilder builder = new SpannableStringBuilder();
//
//        if(partToColor == 1){
//            builder.append(start, new ForegroundColorSpan(), )
//        } else if(partToColor == 2){
//
//        } else if (partToColor == 3){
//
//        } else {
//            throw new IllegalStateException("Method is not used correctly. Parameter partToColor must be have values 1,2 or 3");
//        }
//
//
//
//        if(ActivityUtils.hasNougat()){
//            return Html.fromHtml("<i><font color=\"" + Integer.toHexString(color) + "\">" + string + "</font></i>", Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            return Html.fromHtml("<i><font color=\"" + Integer.toHexString(color) + "\">" + string + "</font></i>");
//        }
//    }

    /**
     * Colors a string in the given color.
     * Uses Html to do so.
     * @param fulltext fulltext of the textView
     * @param subtext to be colored
     * @param color the color which colors the string
     * @return magic
     */
    public static Spannable colorString(String fulltext, String subtext, int color) {
        Spannable str = new SpannableString(fulltext);
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    /**
     * Colors a string in the given color.
     * Uses Html to do so.
     * @param fulltext fulltext of the textView
     * @param subtext to be colored
     * @param color the color which colors the string
     * @return magic
     */
    public static Spannable colorString(Spannable str, String fulltext, String subtext, int color){
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    public static Spannable fattenString(String fulltext, String subtext){
        Spannable str = new SpannableString(fulltext);
        int i = fulltext.indexOf(subtext);
        str.setSpan(new StyleSpan(Typeface.BOLD), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
//        if(ActivityUtils.hasNougat()){
//            return Html.fromHtml("<b>" + string + "</b>", Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            return Html.fromHtml("<b>" + string + "</b>");
//        }
    }

    public static Spannable fattenString(Spannable str, String fulltext, String subtext){
        int i = fulltext.indexOf(subtext);
        str.setSpan(new StyleSpan(Typeface.BOLD), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
//        if(ActivityUtils.hasNougat()){
//            return Html.fromHtml("<b>" + string + "</b>", Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            return Html.fromHtml("<b>" + string + "</b>");
//        }
    }
    @SuppressWarnings("deprecation")
    public static int getColorCompat(Context context, @ColorRes int color){
        if(hasMarshmallow()){
            return context.getResources().getColor(color, context.getTheme());
        } else {
            return context.getResources().getColor(color);
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtmlCompat(String text) {
        if (hasNougat()) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(text);
        }
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawableCompat(Context context, @DrawableRes int drawableId){
        if (hasLollipop()) {
            return context.getResources().getDrawable(drawableId, context.getTheme());
        } else {
            return context.getResources().getDrawable(drawableId);
        }
    }

    /**
     * API 11
     * @see Build.VERSION_CODES#HONEYCOMB
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * API 14
     * @see Build.VERSION_CODES#ICE_CREAM_SANDWICH
     */
    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * API 16
     * @see Build.VERSION_CODES#JELLY_BEAN
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API 19
     * @see Build.VERSION_CODES#KITKAT
     */
    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * API 21
     * @see Build.VERSION_CODES#LOLLIPOP
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * API 23
     * @see Build.VERSION_CODES#M
     */
    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * API 24
     * @see Build.VERSION_CODES#N
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static float dpToPixels(Context context, float dp) {
        return Math.round(dp * getDisplayMetrics(context).density);
    }

    public static float pixelsToDp(Context context, float pixels){
        return  Math.round((pixels/getDisplayMetrics(context).density));
    }

    public static float pixelsToSp(Context context, float px) {
        return px / getDisplayMetrics(context).scaledDensity;
    }

    public static float getScreenWidth(Context context){
        return pixelsToDp(context, getDisplayMetrics(context).widthPixels);
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static Locale getDeviceLocale(Context context){
        if(Utils.hasNougat()){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static View getToolbarNavigationIcon(Toolbar toolbar){
        //check if contentDescription previously was set
        boolean hadContentDescription = TextUtils.isEmpty(toolbar.getNavigationContentDescription());
        String contentDescription = !hadContentDescription ? toolbar.getNavigationContentDescription().toString() : "navigationIcon";
        toolbar.setNavigationContentDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setNavigationContentDescription ensures its existence
        View navIcon = null;
        if(potentialViews.size() > 0){
            navIcon = potentialViews.get(0); //navigation icon is ImageButton
        }
        //Clear content description if not previously present
        if(hadContentDescription)
            toolbar.setNavigationContentDescription(null);
        return navIcon;
    }
}
