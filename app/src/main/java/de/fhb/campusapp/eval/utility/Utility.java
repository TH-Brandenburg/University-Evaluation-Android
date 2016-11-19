package de.fhb.campusapp.eval.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Utility {


    public static void increaseClickArea(View parent, View child) {

        // increase the click area with delegateArea, can be used in + create
        // icon
        final View chicld = child;
        parent.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the
                // parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                View delegate = chicld;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 100;
                delegateArea.bottom += 100;
                delegateArea.left -= 100;
                delegateArea.right += 100;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                        delegate);
                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent())
                            .setTouchDelegate(expandedArea);
                }
            }
        });

    }

    public static void showSoftKeyboard(View view, Activity activity){
        if(view != null) {
            if(view.requestFocus()){
                InputMethodManager imm =(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            throw new IllegalArgumentException("View parameter was null!");
        }
    }

    public static void hideSoftKeyboard(View view, Activity activity){
        if(view != null){
            InputMethodManager imm =(InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            throw new IllegalArgumentException("View parameter was null!");
        }
    }

    public static void setKeyboardOverlapping(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    public static void setKeyboardResizing(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    /*
    * Hash string with SHA-256 and return the result
    * */
    public static String createStringHash(String input) {
        long start = System.currentTimeMillis();
        StringBuilder outputBuilder = new StringBuilder();
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(input.getBytes());
            for (int i = 0; i < array.length; ++i) {
                outputBuilder.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.e("TIME2", Long.toString(System.currentTimeMillis() - start));
        return outputBuilder.toString();
    }

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



    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int)px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return convertPixelsToDp(px, metrics);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, DisplayMetrics displayMetrics){
        float dp = px / (displayMetrics.densityDpi / 160f);
        return (int)dp;
    }

    /*
    * This method zips file
    */
    public static File zipFiles(Context context, ArrayList<File> fileList){
        File storageDir = getImageDirectory(context);
        try {
            OutputStream os = null;

            // choose external storage if available
            os = new BufferedOutputStream(new FileOutputStream(storageDir + "/zippedImages.zip"));
            ZipOutputStream outputStream = new ZipOutputStream(os);
            for(File file : fileList){
                ZipEntry entry = new ZipEntry(file.getName());
                outputStream.putNextEntry(entry);
                byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
                outputStream.write(bytes);
                outputStream.closeEntry();
            }
            outputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return new File(storageDir,"zippedImages.zip");
    }

    public static void writeBytesToFile(InputStream is, File file) throws IOException {
        FileOutputStream fos = null;
        try {
            byte[] data = new byte[2048];
            int nbread = 0;
            int total = 0;
            fos = new FileOutputStream(file);
            while((nbread=is.read(data))>-1){
                fos.write(data,0,nbread);
                total += nbread;
            }
            Log.e("FILE_SIZE","Bytes:" + total);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally{
            if (fos!=null){
                fos.close();
            }
        }
    }

    public static File createImageFile(String imageName, Context context) {
        File image = null;
        File storageDir = null;

        try {
            // choose external storage if available
            storageDir = getImageDirectory(context);
            image = File.createTempFile(imageName, ".png", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
     * Gets the image pointed to by path and scales it to required width and required height.
     * @param filePath The path at which the original image is stored
     * @param reqWidth The width the decoded image shall have
     * @param reqHeight The height the decoded image shall have
     * @return
     */
    public static Bitmap resizeImage(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotates a specified image to the orientation it was originally shot in
     * @param filePath the path to the image
     * @param bitmap the decoded bitmap of the image
     * @return a rotated bitmap
     */
    public static Bitmap rotateBitmap(String filePath, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(filePath);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Gets some fancy meta-data of images (in this case the orientation it was taken in)
     * @param src
     * @return
     * @throws IOException
     */
    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;
        try {
              //if your are targeting only api level >= 5
              ExifInterface exif = new ExifInterface(src);
              orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch ( SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    public static String removeSpecialCharacters(String input){
        return input.replaceAll("[^A-Z^a-z^0-9^\\s][!\"§$%&()=?äöü|]*", "");
    }

    /*
    Legacy attempt to identify images via their creation date. It works but the time differences are too great for my liking
    Maybe something happens so this becomes viable? Maybe?
     */
//    public static void fileDelete(File directory, int thresholdMillis, int recDepthCounter) {
//
//        //loop through all files of directory
//        for(int i = 0; i< directory.listFiles().length; i++){
//            File file = directory.listFiles()[i];
//            //test if any of them are directories themselves
//            if(file.isDirectory() && recDepthCounter < 10){
//                fileDelete(file, thresholdMillis, recDepthCounter+1);
//            } else {
//                // if yes, loop through all images in the subdir and delete them if applicable
//                DataHolder.getmAppStartTime();
//                String extension = FilenameUtils.getExtension(file.getAbsolutePath());
//
//                if(!(extension.equals("PNG") || extension.equals("png")
//                        || extension.equals("JPG") || extension.equals("jpg")
//                        || extension.equals("JPEG") || extension.equals("jpeg"))){
//
//                    // if file is no image...
//                    continue;
//                }
//
//                Instant modified = new Instant(file.lastModified());
//                // picture was modified after app was started (first test)
//                if(modified.isAfter(DataHolder.getmAppStartTime())){
//                    // second test
//                    for(ImageDataVO imageData : DataHolder.getCommentaryImageMap().values()){
//                        Duration creationDifference = new Duration(modified, imageData.getmCreationTime());
//                        // if the difference is less than {@thresholdMillis } seconds delete the picture
//                        if(creationDifference.getMillis() < thresholdMillis){
//                            directory.listFiles()[i].delete();
//                        }
//                    }
//                }
//            }
//        }
//    }

}
