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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

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

        txtUserName.setText(userPrefs.getFirstName());
        Picasso.with(getApplicationContext()).load(BATTLE_SERVER_URL + userPrefs.getAvatarImage()).into(imgAvatar);

    }

    public void ChallengeComputerOnClick(View v) {
        intent = new Intent(getApplicationContext(), BoardSetup.class);
        GameLobby.this.startActivity(intent);
    }

    public void getUsersOnClick(View v ) {

        String url = BATTLE_SERVER_URL + "api/v1/all_users.json";
        StringRequest request = new StringRequest(
                Request.Method.GET, url,
                // Call backs
                new Response.Listener<String>() {
                    @Override
                    public void onResponse( String response ) {
                        // Do something with the returned data
                        Log.d( "INTERNET", response );
                        users = gson.fromJson( response, UserPreferences[].class );
                        adapter = new ArrayAdapter<UserPreferences>( getApplicationContext(), R.layout.activity_listview, users );
                        lstViewUsers.setAdapter( adapter );

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse( VolleyError error ) {
                        // Do something with the error
                        Log.d( "INTERNET", error.toString() );

                        toastIt( "Internet Failure: " + error.toString() );
                    }
                });

        requestQueue.add( request );
    }



}
