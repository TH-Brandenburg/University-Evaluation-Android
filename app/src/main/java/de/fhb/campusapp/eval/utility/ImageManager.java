package de.fhb.campusapp.eval.utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;

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
        Intent intent = new CameraActivity.IntentBuilder(activity)
                .skipConfirm()
                .facing(Facing.BACK)
                .to(intentImage)
                .build();


        activity.startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(intentImage));
//
//        fillPhotoList(activity.getContentResolver());
//
//        // start the image capture Intent
//        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) { // ensure that there is an intent that supports the request
//            activity.startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
//        }
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
}
