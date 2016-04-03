package de.thb.ue.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

import de.thb.ue.android.utility.Utility;

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
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        for(int i = 0; i < pathToImages.listFiles().length; i++){
            pathToImages.listFiles()[i].delete();
        }
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
