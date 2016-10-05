package com.clemcreativity.clem.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.clemcreativity.clem.R;
import com.clemcreativity.clem.app.AppConfig;
import com.clemcreativity.clem.app.AppController;
import com.clemcreativity.clem.helper.DrawerHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView state,lblLocation;
    private Button punch;
    private static String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public AccountHeader accountHeader;
    public ArrayList<IDrawerItem> items;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    public Drawer result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
//        toolbar.setBackgroundColor(Color.BLACK);
//        toolbar.getBackground().setAlpha(90);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        //get account info
        accountHeader = DrawerHelper.getAcountHeader(MainActivity.this);
        //get nav drawer items
        items = DrawerHelper.getPrimaryDrawerItems();

         result = new DrawerBuilder()
                .withActivity(this)
                 //.withFullscreen(true)
                .withToolbar(toolbar)
                 .withDrawerItems(items)
                 .withAccountHeader(accountHeader)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        Toast.makeText(MainActivity.this,"You Clicked on : " + position,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }).build();

        if (!session.isLoggedIn()) {
            logoutUser(MainActivity.this);
        }

        punch = (Button) findViewById(R.id.punchin);
        state = (TextView) findViewById(R.id.state);
        lblLocation = (TextView) findViewById(R.id.lblLocation);

        checkState();

        punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpDialog();
                displayLocation();
                if (!session.isPunchedIn()) {
                    punch(AppConfig.IN);
                } else {
                    punch(AppConfig.OUT);
                }
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(false);
//        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
//        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
//        }
//        if (Build.VERSION.SDK_INT >= 19) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//
//        if(Build.VERSION.SDK_INT >= 19){
//            result.getDrawerLayout().setFitsSystemWindows(false);
//        }


    }


//    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
//        Window win = activity.getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            logoutUser(MainActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeState(String color) {
        if (color.equals("red")) {
            punch.setText(R.string.home);
            punch.setBackgroundColor(getResources().getColor(R.color.red));

        }
        if (color.equals("green")) {
            punch.setText(R.string.kazi);
            punch.setBackgroundColor(getResources().getColor(R.color.green));
        }
    }


    private void punch(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String mes = jObj.getString("message");
                            if (session.isPunchedIn()) {
                                session.setKeyIsPunchedin(false);
                                changeState("green");
                                state.setText("Not Working");
                            } else {
                                session.setKeyIsPunchedin(true);
                                changeState("red");
                                state.setText("Working");
                            }
                            displaySuccess(mes);
                            checkState();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            displayMessage("Error: >" + e.getMessage());
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidepDialog();
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        //dealWithError(response);
                        String json = null;
                        if (response != null && response.data != null) {
                            switch (response.statusCode) {
                                case 400:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 401:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 404:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 422:
                                    json = new String(response.data);
                                    json = trimMessage(json, "message");
                                    if (json != null) displayMessage(json);
                                    break;
                                case 500:
                                    displayMessage("Internal Server Error");
                                    break;
                                default:
                                    displayMessage("Debug Sever-Side Code");
                            }
                        } else {
                            Log.e("Yahaya","Unable to Connect to Server. Please Check Internet Connection");
                            displayMessage("Unable to Connect to Server. Please Check Internet Connection");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("long", "testlong");
                params.put("lat", "testlat");
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", session.getKeyApiKey());
                return headers;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }



    public void checkState() {
        if (session.isPunchedIn()) {
            changeState("red");
            state.setText("Working");
        }


        if (!session.isPunchedIn()) {
            state.setText("Not Working");
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            lblLocation.setText(latitude + ", " + longitude);

        } else {
            displayMessage("Couldn't get the location. Make sure location is enabled on the device");
//            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//            intent.putExtra("enabled",true);
//            sendBroadcast(intent);
            //startActivity(,new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            //startActivity(,new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

}
