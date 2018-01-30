package com.happypeople.pharmacygo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnSuccessListener<Location>,
        LoaderManager.LoaderCallbacks<List<Pharmacy>>{

    public static final String TAG = MapsActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 7171;
    private static final int PHARMACY_LOADER_ID = 1;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;

    private String mUrlPharmacies;
    private List<Pharmacy> mPharmacies;

    private boolean mLocationPermissionGranted = false;
    private boolean mShowMap = false;

    private FrameLayout mFrameMap;
    private ProgressBar mLoadingProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFrameMap = (FrameLayout) findViewById(R.id.frameMap);
        mLoadingProgress = (ProgressBar) findViewById(R.id.loading_indicator);

        showProgress();

        ///// Call google Api Configuration /////
        googleApiClientConfig();

        ///// Instantiate Pharmacy List /////
        mPharmacies = new ArrayList<>();

        ///// Instanciate Location Provider /////
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /***** Start Map UI  *****/

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i(TAG, "life cycle : onMapready");

        mMap = googleMap;

        updateLocationUI();

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, this);

    }


    /*****  Google Api Client Configuration *****/
    private void googleApiClientConfig() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

    }

    /***** BEGIN Listening to Connection *****/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    /***** END Listening to Connection *****/


    @Override
    protected void onResume() {

        Log.i(TAG, "life cycle : onResume");

        super.onResume();

        checkLocationPermission();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                mLocationPermissionGranted = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
    }


    /***** Check if Location is permeted  *****/

    private void checkLocationPermission() {

        if (
                ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED
                        &&
                        ContextCompat.checkSelfPermission(this.getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED ) {

            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }


    }


    private void getDevicePlace() {

        //////// GET PLACE BY ID /////////
        Places.GeoDataApi.getPlaceById(mGoogleApiClient,
                mPharmacies.get(0).getPlace_id()).setResultCallback(
                new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        for(Place place: places) {
                            Log.i(TAG, "My Fucking place : " + place.getName());
                        }
                    }
                }
        );

    }


    /***** Make UI Map a little creative *****/
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                checkLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /***** BEGIN Listenign to Succee when we found the device Location  ******/

    @Override
    public void onSuccess(Location location) {

        // Got last known location. In some rare situations this can be null.
        if (location != null) {

            // Logic to handle location object
            Log.i(TAG, "My Fucking location " +
                    ", Latitude : " +  location.getLatitude() +
                    ", Longtitude : " + location.getLongitude());
            mLocation = location;

            // Add a marker in device location and move the camera
            LatLng myLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLng).title("My place"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLng));

            mMap.setMinZoomPreference(100.0f);
            mMap.setMaxZoomPreference(100.0f);

            mUrlPharmacies = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location=" +
                    myLng.latitude + "," +
                    myLng.longitude +
                    "&radius=1000&type=pharmacy" +
                    "&key=" + getResources().getString(R.string.google_maps_key);

            ///// Instantiate Loader Manager /////
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(PHARMACY_LOADER_ID, null, this);

        }

    }

    /***** END Listenign to Succee when we found the device Location  ******/


    /***** BEGIN Listening to Loader *****/

    @Override
    public Loader<List<Pharmacy>> onCreateLoader(int i, Bundle bundle) {
        return new PharmacyLoader(this,mUrlPharmacies);
    }

    @Override
    public void onLoadFinished(Loader<List<Pharmacy>> loader, List<Pharmacy> pharmacies) {
        Log.i(TAG, "Pharmacies count : " + pharmacies.size());
        mPharmacies.clear();
        if(pharmacies!=null && !pharmacies.isEmpty()) {
            mPharmacies.addAll(pharmacies);
            showMap();
            getDevicePlace();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Pharmacy>> loader) {
        mPharmacies.clear();
    }

    /***** BEGIN Listening to Loader *****/


    private void showMap() {
        mFrameMap.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
    }

    private void showProgress() {
        mFrameMap.setVisibility(View.GONE);
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

}
