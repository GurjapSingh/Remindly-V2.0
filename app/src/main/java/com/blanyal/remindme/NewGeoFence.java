package com.blanyal.remindme;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

/**
 * Created by Julian on 3/27/2018.
 */

public class NewGeoFence {
    private PendingIntent pendingIntent;
    private String TAG = "Fence";
    private final static int REQUEST_PERMISSION_RESULT_CODE = 42;
    private final static int BROADCAST_PERMISSION_RESULT_CODE = 99;

    public void addLocationFence(Context context, double longitude, double latitude, String name, String place, String id){
        Log.i(TAG, "in addLocationFence()");
        Intent intent = new Intent(context, FenceService.class);
        intent.putExtra("rname", name);
        intent.putExtra("place", place);
        intent.putExtra("id", id);
        PendingIntent fencePendingIntent = PendingIntent.getBroadcast(context, BROADCAST_PERMISSION_RESULT_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "in addLocationFence() after pending intent");

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "not getting permission");
        }
        else {
            Log.i(TAG, "getting permission");
            AwarenessFence locationFence = LocationFence.in(latitude, longitude, BROADCAST_PERMISSION_RESULT_CODE+1, BROADCAST_PERMISSION_RESULT_CODE+1);
            Awareness.FenceApi.updateFences(LocationService.mGoogleApiClient, new FenceUpdateRequest.Builder().addFence(MainActivity.LOCATION_FENCE_KEY, locationFence, fencePendingIntent).build()).setResultCallback(new ResultCallback<Status>(){
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        Log.i(TAG, "Fence was registered");
                    } else Log.e(TAG, "Fence could not be registered "+ status);
                }
            });
        }
    }

    public void removeLocationFence(final String fencekey, final Context context, GoogleApiClient googleAPIClient){
        if(googleAPIClient == null){
            Log.d("NewGeoFence", "googleAPIClient is null");
            googleAPIClient = new GoogleApiClient.Builder(context).addApi(Awareness.API).build();
            googleAPIClient.connect();
        }
        Awareness.FenceApi.updateFences(googleAPIClient, new FenceUpdateRequest.Builder().removeFence(fencekey).build()).setResultCallback(new ResultCallbacks<Status>(){
            @Override
            public void onSuccess(@NonNull Status status) {
                String info = "Fence " +fencekey+ " successfully removed.";
                Log.i(TAG, info);
                Toast.makeText(context, info, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Status status) {
                String info = "Fence " +fencekey+ " was NOT removed.";
                Log.i(TAG, info);
                Toast.makeText(context, info, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static Activity getActivity(Context context){
        if (context == null) return null;
        else if (context instanceof  ContextWrapper){
            if(context instanceof Activity) return (Activity) context;
            else return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

}

