package de.fhb.campusapp.eval.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.io.File;
import java.io.FileFilter;

import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;

/**
 * Created by Admin on 11.12.2015.
 */
public class CleanUpService extends Service{

    private File pathToImages;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pathToImages = Utility.getImageDirectory(this);
//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle("Sicherheitsüberwachung")
//                .setContentText("löscht alle Sicherheitsrelevanten Daten am Ende der Evalaution.")
//                .setSmallIcon(android.R.drawable.ic_delete)
//                .build();
//        startForeground(5192, notification);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        int fileNr = Utility.getImageDirectory(this).listFiles().length;

        for(int i = 0; i < fileNr && Utility.getImageDirectory(this).listFiles().length > 0; i++){
            Utility.getImageDirectory(this).listFiles()[0].delete();
        }

        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }
}
