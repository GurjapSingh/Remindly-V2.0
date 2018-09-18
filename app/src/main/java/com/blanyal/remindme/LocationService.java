package com.blanyal.remindme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Julian on 3/29/2018.
 */

public class LocationService extends Service {
    FenceService mFenceReceiver;
    static NewGeoFence mAddGeoFence;
    static GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Awareness.API)
                    .build();
        }
        mGoogleApiClient.connect();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.i("Fence","in onStartCommand()");
        mFenceReceiver = new FenceService();
        mAddGeoFence = new NewGeoFence();
        Log.i("Fence","got addgeofence()");
        double lat = (double) intent.getExtras().get("lat");
        double lng = (double) intent.getExtras().get("lng");
        String place = (String) intent.getExtras().get("place");
        String rname=(String) intent.getExtras().get("rname");
        String id=(String) intent.getExtras().get("id");
        Log.i("Fence","Location Service : "+lat+" "+lng);
        mAddGeoFence.addLocationFence(getApplicationContext(),lng,lat,rname,place,id);
        Log.i("Fence","out onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}