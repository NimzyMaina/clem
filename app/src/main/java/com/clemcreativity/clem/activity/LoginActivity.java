package com.clemcreativity.clem.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.clemcreativity.clem.helper.ColoredSnackBar;
import com.clemcreativity.clem.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button submit;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private static String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    String jsonResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == submit){
                    if(validateEmail() && validatePassword()) {
                        showpDialog();
                        login();
                    }
                }
            }
        });
        // displayMessage("Hello");

        email.addTextChangedListener(new MyTextWatcher(email));
        password.addTextChangedListener(new MyTextWatcher(password));
    }

    private void login() {
        Log.d(TAG, "Making login Request");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONObject data = jObj.getJSONObject("data");
                            //String id = data.getString("id");
                            String fname = data.getString("fname");
                            String lname = data.getString("lname");
                            String email = data.getString("email");
                            String phone = data.getString("phone");
                            String avatar = data.getString("avatar");
                            String apikey = data.getString("apikey");
                            String department = data.getString("department");
                            String role = data.getString("role");

                            session.setLogin(true,apikey,fname,lname,phone,email,department,role);

                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = null;
                        hidepDialog();
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            switch (response.statusCode){
                                case 400:
                                    json = new String(response.data);
                                    json = trimMessage(json,"message");
                                    if(json != null) displayMessage(json);
                                    break;
                                case 401:
                                    json = new String(response.data);
                                    json = trimMessage(json,"message");
                                    if(json != null) displayMessage(json);
                                    break;
                                case 404:
                                    json = new String(response.data);
                                    json = trimMessage(json,"message");
                                    if(json != null) displayMessage(json);
                                    break;
                                case 500:
                                    displayMessage("Internal Server Error");
                                    break;
                                default:
                                    displayMessage("Debug Sever-Side Code");
                            }
                        }
                        else {
                            Log.e("Yahaya","Unable to Connect to Server. Please Check Internet Connection");
                            displayMessage("Unable to Connect to Server. Please Check Internet Connection");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("login", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    //Somewhere that has access to a context
    public void displayMessage(String toastString){
        Snackbar snackbar = Snackbar
                .make(this.getCurrentFocus(), toastString, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //makeJsonArrayRequest();
                    }
                });

        ColoredSnackBar.error(snackbar);

// Changing message text color
        snackbar.setActionTextColor(Color.BLACK);

// Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private boolean validateEmail() {
        String emaill = email.getText().toString().trim();

        if (emaill.isEmpty() || !isValidEmail(emaill)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            //inputLayoutEmail.getChildAt(1).setVisibility(View.GONE);
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
            }
        }
    }

}
