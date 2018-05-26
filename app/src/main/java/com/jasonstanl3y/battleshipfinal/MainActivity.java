package com.jasonstanl3y.battleshipfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    EditText edtUserName;
    EditText edtPassword;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        username = "";
        password = "";

    }

    public void loginOnClick(View v) {

       username = edtUserName.getText().toString();
       password = edtPassword.getText().toString();


        String url = "http://www.battlegameserver.com/api/v1/login.json";
        StringRequest request = new StringRequest(
                Request.Method.GET, url,
                // Call backs
                new Response.Listener<String>() {
                    @Override
                    public void onResponse( String response ) {
                        // Do something with the returned data
                        Log.d( "INTERNET", response );
                      userPrefs = gson.fromJson( response, UserPreferences.class );

                        intent = new Intent(getApplicationContext(), GameLobby.class);
                        MainActivity.this.startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse( VolleyError error ) {
                        // Do something with the error
                        Log.d( "INTERNET", error.toString() );
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                MainActivity.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Bad Internet Connection or Bad Login ");

                        // Setting Dialog Message
                        alertDialog.setMessage("Please make sure you are entering the right credentials or check internet connection and try again.");


                        // Setting Try Again Button
                        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                MainActivity.this.startActivity(intent);

                            }
                        });

                        alertDialog.show();

                        toastIt( "Internet Failure: " + error.toString() );
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String credentials = username + ":" + password;
                Log.d("AUTH", "Login Info: " + credentials);
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };

        requestQueue.add( request );
    }
}
