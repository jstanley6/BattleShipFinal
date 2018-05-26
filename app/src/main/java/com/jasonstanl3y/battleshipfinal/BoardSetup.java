package com.jasonstanl3y.battleshipfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BoardSetup extends BaseActivity {

    Spinner shipSpinner;
    Spinner directionSpinner;
    ArrayAdapter shipSpinnerArrayAdapter;
    ArrayAdapter directionSpinnerArrayAdapter;
    String[] shipsArray;
    String[] directionArray;
    static TreeMap<String, Integer>shipsMap = new TreeMap<String, Integer>();
    static TreeMap<String, Integer>directionsMap = new TreeMap<String, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_setup);

        shipSpinner = findViewById(R.id.spinnerShips);
        directionSpinner = findViewById(R.id.spinnerDirections);
        GetAvailableShips();
        GetAvailableDirections();
    }

    private void GetAvailableShips() {

        String url = BATTLE_SERVER_URL + "api/v1/available_ships.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse( JSONObject response ) {
                        Log.d( "INTERNET", response.toString() );
                        Iterator iter = response.keys();
                        while ( iter.hasNext() ) {
                            String key = (String)iter.next();
                            try {
                                Integer value = (Integer)response.get(key);
                                shipsMap.put(key, value);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //shipsMao convert into array that the spinner can use
                        int size = shipsMap.keySet().size(); //how many elements in the map
                        shipsArray = new String[size];
                        shipsArray = shipsMap.keySet().toArray(new String[]{});
                        shipSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, shipsArray);
                        shipSpinner.setAdapter(shipSpinnerArrayAdapter);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse( VolleyError error ) {
                        Log.d( "INTERNET", error.toString() );
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String credentials = username + ":" + password;
                Log.d( "AUTH", "Login Info: " + credentials );
                String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), Base64.NO_WRAP );
                headers.put( "Authorization", auth );
                return headers;
            }

        };

        requestQueue.add( request );
    }
///api/v1/available_directions.json
    private void GetAvailableDirections() {

        String url = BATTLE_SERVER_URL + "api/v1/available_directions.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse( JSONObject response ) {
                        Log.d( "INTERNET", response.toString() );
                        Iterator iter = response.keys();
                        while ( iter.hasNext() ) {
                            String key = (String)iter.next();
                            try {
                                Integer value = (Integer)response.get(key);
                                directionsMap.put(key, value);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //shipsMao convert into array that the spinner can use
                        int size = directionsMap.keySet().size(); //how many elements in the map
                        directionArray = new String[size];
                        directionArray = directionsMap.keySet().toArray(new String[]{});
                        directionSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, directionArray);
                        directionSpinner.setAdapter(directionSpinnerArrayAdapter);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse( VolleyError error ) {
                        Log.d( "INTERNET", error.toString() );
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String credentials = username + ":" + password;
                Log.d( "AUTH", "Login Info: " + credentials );
                String auth = "Basic " + Base64.encodeToString( credentials.getBytes(), Base64.NO_WRAP );
                headers.put( "Authorization", auth );
                return headers;
            }

        };

        requestQueue.add( request );
    }
}
