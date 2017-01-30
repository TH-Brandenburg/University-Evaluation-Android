package de.fhb.campusapp.eval.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.File;

import javax.inject.Inject;

import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.ui.EvaluationApplication;
import de.fhb.campusapp.eval.utility.Utility;

/**
 * Created by Sebastian Müller on 11.12.2015.
 */
public class CleanUpService extends Service{

    private File pathToImages;

    @Inject
    IDataManager mDataManager;

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

        int fileNr = Utility.getImageDirectory(this).listFiles().length;
//        mDataManager.removeAllData();

        for(int i = 0; i < fileNr && Utility.getImageDirectory(this).listFiles().length > 0; i++){
            Utility.getImageDirectory(this).listFiles()[0].delete();
        }

        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }
}
