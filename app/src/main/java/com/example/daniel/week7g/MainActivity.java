package com.example.daniel.week7g;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ResultCallback<LocationSettingsResult> {
    private String TAG = "SampleGoogleAPI";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingRequest;
    private TextView tvLat, tvLong;



    public static final long UPDATE_INTERVAL_IN_MILISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILISECONDS = UPDATE_INTERVAL_IN_MILISECONDS / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLat = (TextView) findViewById(R.id.textViewLat);
        tvLong = (TextView) findViewById(R.id.textViewLong);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }


    public void onclickStart(View view) {
        LocationSettingsRequest.Builder builder= new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingRequest = builder.build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,mLocationSettingRequest);
        result.setResultCallback(this);
    }

    public void onclickStop(View view) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "???", Toast.LENGTH_LONG).show();
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation!=null){
            tvLat.setText(lastLocation.getLatitude()+"");
            tvLong.setText(lastLocation.getLongitude()+"");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_LONG).show();
        Log.d(TAG,"onConnectionFailed " + connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        tvLat.setText(location.getLatitude()+"");
        tvLong.setText(location.getLongitude()+"");
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status= locationSettingsResult.getStatus();
        switch (status.getStatusCode()){
            case LocationSettingsStatusCodes.SUCCESS:

                if (mGoogleApiClient.isConnected()) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "???", Toast.LENGTH_LONG).show();
                        return;
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Toast.makeText(this,"LocationSettingsStatusCodes.RESOLUTION_REQUIRED",Toast.LENGTH_LONG).show();
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Toast.makeText(this,"LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE",Toast.LENGTH_LONG).show();
                break;
        }

    }
}
