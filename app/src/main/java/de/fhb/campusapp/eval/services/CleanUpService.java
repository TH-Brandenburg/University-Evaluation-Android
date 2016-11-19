package de.fhb.campusapp.eval.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.Utility;

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
        DataManager.deleteAllData();

        for(int i = 0; i < fileNr && Utility.getImageDirectory(this).listFiles().length > 0; i++){
            Utility.getImageDirectory(this).listFiles()[0].delete();
        }

        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }
}
