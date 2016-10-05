package com.clemcreativity.clem.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.clemcreativity.clem.R;
import com.clemcreativity.clem.helper.ColoredSnackBar;
import com.clemcreativity.clem.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends AppCompatActivity {

    public static ProgressDialog pDialog;
    public SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        // session manager
        session = new SessionManager(getApplicationContext());
    }

    public void displayMessage(String toastString) {
        Snackbar snackbar = Snackbar
                .make(getWindow().getCurrentFocus(), toastString, Snackbar.LENGTH_LONG)
                .setAction("RETRY", null);


        ColoredSnackBar.error(snackbar);

        // Changing message text color
        snackbar.setActionTextColor(Color.BLACK);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void displaySuccess(String toastString) {
        Snackbar snackbar = Snackbar
                .make(getWindow().getCurrentFocus(), toastString, Snackbar.LENGTH_LONG).setAction("Close", null);

        ColoredSnackBar.confirm(snackbar);

// Changing message text color
        snackbar.setActionTextColor(Color.BLACK);

// Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


    public static void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public static void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void logoutUser(Activity activity) {
        session.setLogin(false, null, null, null, null, null, null, null);
        // Launching the login activity
        startActivity(new Intent(activity, LoginActivity.class));
        finish();
    }

    public void dealWithError(NetworkResponse response){
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

}
