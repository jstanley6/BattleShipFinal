package com.jasonstanl3y.battleshipfinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseActivity extends AppCompatActivity {

   static String username, password;
   Gson gson;
   RequestQueue requestQueue;
    public static MenuItem prefs;
   Intent intent;
   static UserPreferences userPrefs;
   final String BATTLE_SERVER_URL = "http://www.battlegameserver.com/";
    static  UserPreferences[] users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); //2018-05-07T21:12:27.000Z
        gson = gsonBuilder.create();


        prefs = findViewById(R.id.menu_preferences);

        //Volley Library
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }


    public void toastIt(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate Menu
        if (username != "" ) {
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {

                case R.id.menu_Logout:

                    username = "";
                    password = "";

                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    return true;


            case R.id.menu_preferences:
                //switch to the add record activity

                intent = new Intent(this, GameUserPreferences.class);

//                prefs = item;
//                 prefs.setVisible(false);
//                intent.putExtra("item", item);
//                intent.putExtra("name", (Parcelable) prefs);
                startActivity(intent);


                return true;

            case R.id.menu_gameLobby:

                intent = new Intent(this, GameLobby.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
