package com.clemcreativity.clem.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_LNAME = "lname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DEP = "department";
    private static final String KEY_ROLE = "role";



    // Shared preferences file name
    private static final String PREF_NAME = "Clem";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static final String KEY_IS_PUNCHEDIN = "isPunchedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String apikey,String fname,String lname,String phone,String email,String dep,String role) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_API_KEY,apikey);
        editor.putString(KEY_FNAME,fname);
        editor.putString(KEY_LNAME,lname);
        editor.putString(KEY_PHONE,phone);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_DEP,dep);
        editor.putString(KEY_ROLE,role);
        // commit changes
        editor.commit();
    }

    public void setKeyIsPunchedin(boolean isPunchedin){
        editor.putBoolean(KEY_IS_PUNCHEDIN,isPunchedin);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isPunchedIn(){
        return pref.getBoolean(KEY_IS_PUNCHEDIN,false);
    }

    public String getKeyApiKey(){
        return pref.getString(KEY_API_KEY,"");
    }
    public String getKeyFname(){
        return pref.getString(KEY_FNAME,"");
    }
    public String getKeyLname(){
        return pref.getString(KEY_LNAME,"");
    }

    public String getKeyPhone(){
        return pref.getString(KEY_PHONE,"");
    }

    public String getKeyEmail(){
        return pref.getString(KEY_EMAIL,"");
    }

    public String getKeyRole(){
        return pref.getString(KEY_ROLE,"");
    }
}

