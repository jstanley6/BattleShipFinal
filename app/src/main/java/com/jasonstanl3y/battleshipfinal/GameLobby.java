package com.jasonstanl3y.battleshipfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GameLobby extends BaseActivity {

    TextView txtUserName;
    ImageView imgAvatar;
    ListView lstViewUsers;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);


        txtUserName = findViewById(R.id.txtUserName);
        imgAvatar = findViewById(R.id.imgAvatar);
        lstViewUsers = findViewById(R.id.lstViewUsers);

        txtUserName.setText(userPrefs.getAvatarName());
        Picasso.with(getApplicationContext()).load(BATTLE_SERVER_URL + userPrefs.getAvatarImage()).into(imgAvatar);

    }

    public void ChallengeComputerOnClick(View v) {

        //Challegen Computer
        String url = BATTLE_SERVER_URL + "api/v1/challenge_computer.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("INTERNET", response.toString());

                        try {
                            gameID = response.getInt("game_id");
                            intent = new Intent(getApplicationContext(), BoardSetup.class);
                            GameLobby.this.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ;

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("INTERNET", error.toString());
                    }
                }
        ) {
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

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        requestQueue.add(request);

    }

    public void getUsersOnClick(View v) {

        String url = BATTLE_SERVER_URL + "api/v1/all_users.json";
        StringRequest request = new StringRequest(
                Request.Method.GET, url,
                // Call backs
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the returned data
                        Log.d("INTERNET", response);
                        users = gson.fromJson(response, UserPreferences[].class);
                        adapter = new ArrayAdapter<UserPreferences>(getApplicationContext(), R.layout.activity_listview, users);
                        lstViewUsers.setAdapter(adapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with the error
                        Log.d("INTERNET", error.toString());

                        toastIt("Internet Failure: " + error.toString());
                    }
                });

        requestQueue.add(request);
    }


}
