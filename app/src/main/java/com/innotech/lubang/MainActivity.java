package com.innotech.lubang;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innotech.lib.GPSTracker;

/**
 * Created by itman on 15/3/2015.
 */
public class MainActivity extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Marker mMarker;
    private  ShareDialog shareDialog;
    private double currentLatitude;
    private double currentLogitude;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Facebook
        facebookSDKInitialize();

        setContentView(R.layout.activity_main);

        if (!isMyServiceRunning(LubangService.class)) {
            startService(new Intent(getBaseContext(), LubangService.class));
        }

        setUpMapIfNeeded();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email,publish_actions");
        getLoginDetails(loginButton);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        shareDialog = new ShareDialog(this);  // intialize facebook shareDialog.

    }

    /*
    Initialize the facebook sdk.
    And then callback manager will handle the login responses.
    */
    CallbackManager callbackManager = null;

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    /*
    Register a callback function with LoginButton to respond to the login result.
    */
    protected void getLoginDetails(LoginButton login_button) {

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                //Do nothing for now

                //Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                //startActivity(intent);

                //Display share and comments

            }

            @Override
            public void onCancel() {
                // code for cancellation

            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error


            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this.getApplication());

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this.getApplication());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // create class object
        GPSTracker gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            currentLatitude = gps.getLatitude();
            currentLogitude = gps.getLongitude();

            //PLACE THE INITIAL MARKER
            drawMarker(gps.location);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLatitude, currentLogitude), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(currentLatitude, currentLogitude))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            /*
            SmsManager sms = SmsManager.getDefault();
            try
            {
                sms.sendTextMessage("+60143830918", null, "Aiyoh, I am here " + currentLatitude + "," + currentLogitude, null, null);
            }
            catch(IllegalArgumentException e)
            {

            }
*/
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }
    private void drawMarker(Location location) {
        // Remove any existing markers on the map
        mMap.clear();
        LatLng currentPosition = new LatLng(currentLatitude, currentLogitude);

        MarkerOptions maker = new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + currentLatitude + "Lng:" + currentLogitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("You hit a bump here!!!");

        mMap.addMarker(maker);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.innotech.lubang/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.innotech.lubang/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public void shareLubang(View v) {

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            //  http://maps.google.com/maps?q=24.197611,120.780512
            String uri = "http://maps.google.com/maps?q=" + currentLatitude + "," + currentLogitude;
            Uri uriObj = Uri.parse(uri);

            EditText lubangComment = (EditText) findViewById(R.id.lubang_comment);

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Lubang App (Android)")
                    .setContentDescription(lubangComment.getText().toString())
                    .setContentUrl(uriObj)
                    .build();

            shareDialog.show(linkContent);  // Show facebook ShareDialog
        }
    }
}
