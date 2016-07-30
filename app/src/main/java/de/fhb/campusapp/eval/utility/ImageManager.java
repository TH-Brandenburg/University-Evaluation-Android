package de.fhb.campusapp.eval.utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Sebastian Müller on 07.07.2016.
 * Calling the photo intent with EXTRA_OUTPUT does not always lead to the desired result.
 * According to the internetz there are 3 possibilities:
 * - Some devices use it completely and skip the gallery.
 * - Some devices ignore it completely and ONLY use the gallery.
 * - Some devices really suck and save a full sized image to the gallery,
 *   and save a thumbnail only to the location specified.
 * - Some devices save full sized images both to the gallery and the specified location.
 *
 * All must be addressed and dealt with.
 * Poss 1: Best case -> we dont need to do anything
 * Poss 2: Copy the Gallery pic to the intended location.
 * Poss 3: Delete the thumbnail and copy the full sized picture to specified location.
 * Poss 4: Delete the Gallery picture.
 *
 * This class provides methods to solve these issues
 */

public class ImageManager {
    private static final int REQUEST_CAPTURE_IMAGE = 1;
    /**
     * Some devices use it completely and skip the gallery.
     * Best case -> we dont need to do anything.
     * Just test if this is the case.
     *
     * @param intentImage the image captured by the camera intent
     * @param contentResolver provides access to the systems mediaStore database
     * @return weather this is the possibility implied by method name
     */
    public boolean isPossibility1(File intentImage, ContentResolver contentResolver){
        // array containing properties we want to know about the images on this system.
        String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,// the path to the image including name
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns._ID};

        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if(intentImage != null && intentImage.exists() && intentImage.length() > 0){

            if (uri != null) {
                cursor = contentResolver.query(uri, projection, null, null, null);
            }
            ImageInfo info = findGalleryImageInDatabase(cursor);

            // if image exists in Gallery than its not possibility 1
            // if it is there there is nothing left to do
            if(info.isGalleryImageExisting()){
                return false;
            } else {
                return true;
            }
        }
        // if intent image is not there or empty than it automatically possibility 2
        return false;
    }

    /**
     * Some devices ignore it completely and ONLY use the gallery
     * Solution: Copy the Gallery pic to the intended location.
     *
     * @param intentImage the image captured by the camera intent
     * @param contentResolver provides access to the systems mediaStore database
     * @return weather this is the possibility implied by method name
     */
    public boolean solvePossibility2(File intentImage, ContentResolver contentResolver){

        // array containing properties we want to know about the images on this system.
        String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,// the path to the image including name
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns._ID};

        boolean result = false;
        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if(!intentImage.exists() || intentImage.length() == 0){

            if (uri != null) {
                cursor = contentResolver.query(uri, projection, null, null, null);
            }
            ImageInfo info = findGalleryImageInDatabase(cursor);
            File galleryImage = new File(info.getCursor().getString(2));

            if(!info.isGalleryImageExisting() || !galleryImage.exists() || galleryImage.length() == 0){
                return false;
            }

            result = true;
            // copy gallery image data into intentImage file
            try{
                intentImage.createNewFile();
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(galleryImage).getChannel();
                    destination = new FileOutputStream(intentImage).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } finally {
                    if (source != null) {
                        source.close();
                    }
                    if (destination != null) {
                        destination.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(galleryImage.delete()){
                contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
            }
        }
        return result;
    }

    /**
     * Some devices really suck and save a full sized image to the gallery,
     * and save a thumbnail only to the location specified.
     * Solution: Delete the thumbnail and copy the full sized picture to specified location.
     *
     * @param intentImage the image captured by the camera intent
     * @param contentResolver provides access to the systems mediaStore database
     * @return weather this is the possibility implied by method name
     */
    public boolean solvePossibility3(File intentImage, ContentResolver contentResolver){

        // array containing properties we want to know about the images on this system.
        String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,// the path to the image including name
                MediaStore.Images.ImageColumns._ID};
        boolean result = false;
        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // its not possibility 2 if intentImage exists
        if(intentImage.exists() && intentImage.length() > 0) {

            if (uri != null) {
                cursor = contentResolver.query(uri, projection, null, null, null);
            }
            ImageInfo info = findGalleryImageInDatabase(cursor);

            File galleryImage = new File(info.getCursor().getString(2));
            if(!info.isGalleryImageExisting() || !galleryImage.exists() || galleryImage.length() == 0){
                return false;
            }

            //intentImage is only a thumbnail so delete and use gallery Image instead
            if(galleryImage.length() > intentImage.length()){
                result = true;
                intentImage.delete();
                try{
                    intentImage.createNewFile();
                    FileChannel source = null;
                    FileChannel destination = null;
                    try {
                        source = new FileInputStream(galleryImage).getChannel();
                        destination = new FileOutputStream(intentImage).getChannel();
                        destination.transferFrom(source, 0, source.size());
                    } finally {
                        if (source != null) {
                            source.close();
                        }
                        if (destination != null) {
                            destination.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                galleryImage.delete();
                contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
            }

        }
        return result;
    }

    /**
     * Some devices save full sized images both to the gallery and the specified location.
     * Solution: Delete the Gallery picture.
     * @param intentImage the image captured by the camera intent
     * @param contentResolver provides access to the systems mediaStore database
     * @return weather this is the possibility implied by method name
     */
    public boolean solvePossibility4(File intentImage, ContentResolver contentResolver){
        // array containing properties we want to know about the images on this system.
        String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,// the path to the image including name
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns._ID};

        boolean result = false;
        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if (uri != null) {
            cursor = contentResolver.query(uri, projection, null, null, null);
        }
        ImageInfo info = findGalleryImageInDatabase(cursor);

        if(!info.isGalleryImageExisting() || !intentImage.exists()){
            return false;
        }
        File galleryImage = new File(info.getCursor().getString(2));

        if(galleryImage.exists() && galleryImage.length() == intentImage.length()){
            result = true;
            if(galleryImage.delete()){
                contentResolver.delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);

            }
        }
        return result;
    }

    public void testForPossibility(ContentResolver contentResolver,File intentImage) {
        boolean caseFound;

        caseFound = this.isPossibility1(intentImage, contentResolver);
        if(!caseFound){
            caseFound = this.solvePossibility4(intentImage, contentResolver);
        }

        if(!caseFound){
            caseFound = this.solvePossibility2(intentImage, contentResolver);
        }

        if(!caseFound){
            caseFound = this.solvePossibility3(intentImage, contentResolver);
        }

        if(!caseFound){
            Log.e("CAMERA_FAILURE", "Possibility was not recognised by ImageManager");
        }
    }

    public File startCameraIntent(Activity activity, String intentImageName){
        // create Intent to take a picture and return control to the calling application
        File intentImage =  Utility.createImageFile(intentImageName, activity);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(intentImage));

        fillPhotoList(activity.getContentResolver());

        // start the image capture Intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) { // ensure that there is an intent that supports the request
            activity.startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
        }
        return intentImage;
    }

    private ImageInfo findGalleryImageInDatabase(Cursor cursor){

        ImageInfo imageInfo = new ImageInfo(cursor);

        if(cursor != null && cursor.moveToFirst()){
            do {
                if (!DataHolder.getGalleryList().contains(cursor.getString(1))) {
                    imageInfo.setUnknownImageExisting(true);
                    break;
                }
            }while (cursor.moveToNext());
        }
        return imageInfo;
    }

    /**
     * Stores references to all existing image files into DataHolder.
     * Its a snapshot that can be used as reference later.
     * <p>
     * (Can be used to answer the question: Which images were taken after the last execution of this method?)
     */
    private void fillPhotoList(ContentResolver contentResolver) {
        DataHolder.setGalleryList(null);
        DataHolder.getGalleryList().clear();
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if (uri != null) {
            cursor = contentResolver.query(uri, projection, null, null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DataHolder.getGalleryList().add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }



    private class ImageInfo {
        private boolean unknownImageExisting;
        private Cursor cursor;

        ImageInfo(Cursor cursor) {
            this.cursor = cursor;
            unknownImageExisting = false;
        }

        boolean isGalleryImageExisting() {
            return unknownImageExisting;
        }

        void setUnknownImageExisting(boolean unknownImageExisting) {
            this.unknownImageExisting = unknownImageExisting;
        }

        Cursor getCursor() {
            return cursor;
        }


    }



//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
//
//            // array containing properties we want to know about the images on this system.
//            String[] projection = { MediaStore.Images.ImageColumns.SIZE,
//                    MediaStore.Images.ImageColumns.DISPLAY_NAME,// the path to the image including name
//                    MediaStore.Images.ImageColumns.DATA,
//                    MediaStore.Images.ImageColumns._ID};
//
//            Cursor cursor = null;
//            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//            // 3 possibilities:
//            // - Some devices use it completely and skip the gallery.
//            // - Some devices ignore it completely and ONLY use the gallery.
//            // - Some devices really suck and save a full sized image to the gallery,
//            //   and save a thumbnail only to the location specified.
//            // All must be addressed and dealt with.
//            // Poss 1: Best case -> we dont need to do anything
//            // Poss 2: Copy the Gallery pick to the intended location.
//            // Poss 3: Delete the thumbnail and copy the full sized picture to specified location.
//
//            // test for poss 2
//            if(mCurrentImageFile.exists() && mCurrentImageFile.length() > 0){
//                // test passed -> the intent stored SOMETHING into the intended file
//
//                // test for poss 3 -> search for image in gallery
//                if (uri != null)
//                {
//                    cursor = getContentResolver().query(uri, projection, null, null, null);
//                }
//
//                if(cursor != null && cursor.moveToFirst()) {
//                    cursor = findGalleryImageInDatabase(cursor);
//                    if(pair.second != null && !pair.second.isEmpty()) {
//
//                    }
//                    // if this is false -> we found the new Image!
//                    File file = new File(cursor.getString(2));
//                    // if our image is smaller than the gallery one ours is only a thumbnail
//                    // delete and copy
//                    if (file.exists() && mCurrentImageFile.length() < cursor.getLong(0) && file.delete()) {
//                        try{
//                            mCurrentImageFile.createNewFile();
//                            FileChannel source = null;
//                            FileChannel destination = null;
//                            try {
//                                source = new FileInputStream(file).getChannel();
//                                destination = new FileOutputStream(mCurrentImageFile).getChannel();
//                                destination.transferFrom(source, 0, source.size());
//                            } finally {
//                                if (source != null) {
//                                    source.close();
//                                }
//                                if (destination != null) {
//                                    destination.close();
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        getContentResolver().delete(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
//                        break;
//                    }
//                }
//
//
//            }
//        } else {
//            // the intent stored nothing inside the file
//        }
//
//
//
//        if(mCurrentImageFile != null){
//            if (uri != null && mCurrentImageFile.length() > 0)
//            {
//                cursor = getContentResolver().query(uri, projection, null, null, null);
//            }
//
//            if(cursor != null && cursor.moveToFirst()){
//                do{
//                    boolean imageFound = false;
//                    if(DataHolder.getGalleryList().contains(cursor.getString(1))){
//                        imageFound = true;
//                    }
//
//                    if(!imageFound){
//                        // if this is false -> we found the new Image!
//                        File file = new File(cursor.getString(2));
//
//                        // Delete it and remove its entry from the MediaStore Database.
//                        // Congrats. You have removed the offending duplicate.
//                        if(file.exists() && file.delete()){
//                            getContentResolver().delete(
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
//                            break;
//                        } else {
//                            Log.e("FILE_DELETE_FAILED", "Image could not be deleted!");
//                            throw new IllegalStateException("Could not delete file. Either it does not exist or its locked.");
//                        }
//                    }
//                } while (cursor.moveToNext());
//            }
//            if(cursor != null){
//                cursor.close();
//            }
//        }
//        TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
//        fragment.onPhotoTaken(mCurrentQuestionText, mCurrentImageFile.getAbsolutePath());
//    }



//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // based on the result we either set the preview or show a quick toast splash.
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//
//        // This is ##### ridiculous.  Some versions of Android save
//        // to the MediaStore as well.  Not sure why!  We don't know what
//        // name Android will give either, so we get to search for this
//        // manually and remove it.
//        String[] projection = {MediaStore.Images.ImageColumns.SIZE,
//                MediaStore.Images.ImageColumns.DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.DATA,
//                BaseColumns._ID,};
//        //
//        // intialize the Uri and the Cursor, and the current expected size.
//        Cursor cursor = null;
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        //
//        if (mCurrentImageFile != null) {
//            // Query the Uri to get the data path.  Only if the Uri is valid,
//            // and we had a valid size to be searching for.
//            if ((uri != null) && (mCurrentImageFile.length() > 0)) {
//                cursor = getContentResolver().query(uri, projection, null, null, null);
//            }
//            //
//            // If we found the cursor and found a record in it (we also have the size).
//            if ((cursor != null) && (cursor.moveToFirst())) {
//                do {
//                    // Check each area in the gallary we built before.
//                    boolean imageFound = false;
//                    if (DataHolder.getGalleryList().contains(cursor.getString(1))) {
//                        imageFound = true;
//                    }
//                    //
//                    // To here we looped the full gallery.
//                    if (!imageFound) {
//                        // This is the NEW image.  If the size is bigger, copy it.
//                        // Then delete it!
//                        File file = new File(cursor.getString(2));
//
//                        // Ensure it's there, check size, and delete!
//                        if ((file.exists()) && (mCurrentImageFile.length() < cursor.getLong(0)) && (mCurrentImageFile.delete())) {
//                            // Finally we can stop the copy.
//                            try {
//                                mCurrentImageFile.createNewFile();
//                                FileChannel source = null;
//                                FileChannel destination = null;
//                                try {
//                                    source = new FileInputStream(file).getChannel();
//                                    destination = new FileOutputStream(mCurrentImageFile).getChannel();
//                                    destination.transferFrom(source, 0, source.size());
//                                } finally {
//                                    if (source != null) {
//                                        source.close();
//                                    }
//                                    if (destination != null) {
//                                        destination.close();
//                                    }
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        //
//                        ContentResolver cr = getContentResolver();
//                        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                BaseColumns._ID + "=" + cursor.getString(3), null);
//                        break;
//                    }
//                }
//                while (cursor.moveToNext());
//                cursor.close();
//
//            }
//            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
//            fragment.onPhotoTaken(mCurrentQuestionText, mCurrentImageFile.getAbsolutePath());
//        }
//    }


}
