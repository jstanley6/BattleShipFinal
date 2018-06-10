package com.jasonstanl3y.battleshipfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BoardSetup extends BaseActivity {

    Spinner shipSpinner;
    Spinner directionSpinner;
    Spinner rowSpinner;
    Spinner colSpinner;
    ArrayAdapter shipSpinnerArrayAdapter;
    ArrayAdapter directionSpinnerArrayAdapter;
    String[] shipsArray;
    String[] directionArray;
    static TreeMap<String, Integer> shipsMap = new TreeMap<String, Integer>();
    static TreeMap<String, Integer> directionsMap = new TreeMap<String, Integer>();
    int directions;
    ImageView imgDefensiveGrid;
    ImageView imgAttackGrid;
    public static GameCell[][] defendingGrid = new GameCell[11][11];
    public static GameCell[][] attackingGrid = new GameCell[11][11];
    RadioButton rdoAttack;
    RadioButton rdoDefend;
    int row;
    int shipLength;
    int col;
    int shipCounter = 0;
    Button btnAddShip;
    int compRow;
    MediaPlayer myBomb;
    MediaPlayer missShip;
    MediaPlayer startMusic;
    MediaPlayer shipExploding;
    TextView txtShip;
    TextView txtDirection;
    TextView txtRow;
    TextView txtCol;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_setup);

        InitializeApp();
        shipSpinner = findViewById(R.id.spinnerShips);
        directionSpinner = findViewById(R.id.spinnerDirections);
//        txtGameID = findViewById(R.id.txtGameID);
        rowSpinner = findViewById(R.id.spinnerRow);
        colSpinner = findViewById(R.id.spinnerColumns);
        rdoAttack = findViewById(R.id.rdoAttack);
        rdoDefend = findViewById(R.id.rdoDefend);
        imgDefensiveGrid = findViewById(R.id.imgDefensiveGrid);
        imgAttackGrid = findViewById(R.id.imgAttackGrid);
        txtShip = findViewById(R.id.txtShip);
        txtDirection = findViewById(R.id.txtDirection);
        txtCol = findViewById(R.id.txtColumn);
        txtRow = findViewById(R.id.txtRow);

        btnAddShip = findViewById(R.id.btnAddShip);
        imgAttackGrid.setVisibility(View.INVISIBLE);

        GetAvailableShips();
        GetAvailableDirections();

        // /api/v1/game/:id/attack/:row/:col.json

        imgAttackGrid.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("SHIP", "OnTouch: x( " + event.getX() + ") y(" + event.getY());
                    Log.i("SHIP", "OnTouch: cellX( " + (int) (event.getX() / BoardView.cellWidth) + ") cellY(" + (int) (event.getY() / BoardView.cellWidth));

                    attackShip((int) event.getY() / BoardView.cellWidth, (int) event.getX() / BoardView.cellWidth);
                }

                return true; //We have handled the event
            }
        });

        myBomb = MediaPlayer.create(this, R.raw.bomb);
        missShip = MediaPlayer.create(this, R.raw.miss);
        shipExploding = MediaPlayer.create(this, R.raw.ship_exploding);

//       startMusic = MediaPlayer.create(this, R.raw.starter);
//        startMusic.setVolume(1, 1);
//        startMusic.start();

    }

    private void attackShip(final int y, final int x) {


        String url = BATTLE_SERVER_URL + "api/v1/game/" + gameID + "/attack/" + (char) (y + 64) + "/" + x + ".json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                // Call backs
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Do something with the returned data
                        Log.i("INTERNET", response.toString());


                        try {


                            switch (response.getString("comp_row")) {
                                case "a":
                                    compRow = 1;
                                    break;
                                case "b":
                                    compRow = 2;
                                    break;
                                case "c":
                                    compRow = 3;
                                    break;
                                case "d":
                                    compRow = 4;
                                    break;
                                case "e":
                                    compRow = 5;
                                    break;
                                case "f":
                                    compRow = 6;
                                    break;
                                case "g":
                                    compRow = 7;
                                    break;
                                case "h":
                                    compRow = 8;
                                    break;
                                case "i":
                                    compRow = 9;
                                    break;
                                case "j":
                                    compRow = 10;
                                    break;

                                default:
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {


                            if (response.getBoolean("hit")) {
                                attackingGrid[x][y].setHit(true);
                                myBomb.start();
                                Log.i("INTERNET", response.toString());

                            } else {
                                attackingGrid[x][y].setMiss(true);
                                missShip.start();
                                Log.i("INTERNET", response.toString());


                            }
                            if (defendingGrid[response.getInt("comp_col") + 1][compRow].getHasShip()) {

                                defendingGrid[response.getInt("comp_col") + 1][compRow].setHit(true);
                            } else {

                                if (!response.getBoolean("comp_hit")) {
                                    defendingGrid[response.getInt("comp_col") + 1][compRow].setMiss(true);


                                } else {
                                    defendingGrid[response.getInt("comp_col") + 1][compRow].setHit(true);

                                }

                            }

                            if ((!response.getString("comp_ship_sunk").equals("no")) && (response.getInt("num_your_ships_sunk") != 5 )) {

                                shipExploding.start();

                                final AlertDialog alertDialog = new AlertDialog.Builder(
                                        BoardSetup.this).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("COMPUTER SHIP SUNK");

                                // Setting Dialog Message
                                alertDialog.setMessage("You sunk, the " + response.getString("comp_ship_sunk"));

                                // Setting OK Button
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                         dialog.dismiss();

                                    }

                                });


                                alertDialog.show();

                            } else if((!response.getString("user_ship_sunk").equals("no")) && (response.getInt("num_computer_ships_sunk") != 5))  {

                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        BoardSetup.this).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("YOUR SHIP SUNK");

                                // Setting Dialog Message
                                alertDialog.setMessage("Computer sunk, " + response.getString("user_ship_sunk"));


                                // Setting OK Button
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }

                                });


                                alertDialog.show();
                            }

                            if (response.getString("winner").equals("computer")) {

                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        BoardSetup.this, R.style.Theme_AppCompat_DayNight_DarkActionBar).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("Congratulations!");

                                // Setting Dialog Message
                                alertDialog.setMessage("You sunk, the "  + response.getString("comp_ship_sunk") + "\nYou beat the computer! Do you want to play again?");


                                // Setting Yes Button
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = new Intent(getApplicationContext(), GameLobby.class);
                                        BoardSetup.this.startActivity(intent);

                                    }

                                });
                                // Setting No Button
                                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = new Intent(getApplicationContext(), MainActivity.class);
                                        BoardSetup.this.startActivity(intent);
                                    }
                                });


                                alertDialog.show();
                            } else if (response.getString("winner").equals("you")) {

                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        BoardSetup.this).create();

                                // Setting Dialog Title
                                alertDialog.setTitle("You Lose!");

                                // Setting Dialog Message
                                alertDialog.setMessage("Computer sunk, "  + response.getString("user_ship_sunk") + "\nYou lost to the computer, what were you thinking?? Do you want to play again? ");


                                // Setting Yes Button
                                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = new Intent(getApplicationContext(), GameLobby.class);
                                        BoardSetup.this.startActivity(intent);

                                    }

                                });
                                // Setting No Button
                                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        intent = new Intent(getApplicationContext(), MainActivity.class);
                                        BoardSetup.this.startActivity(intent);
                                    }
                                });


                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imgAttackGrid.invalidate();
                        imgDefensiveGrid.invalidate();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with the error
                        Log.d("INTERNET", error.toString());

                        toastIt("Internet Failure: " + error.toString());
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

        requestQueue.add(request);
    }

    private void InitializeApp() {
        //Initialize the game grid

        for (int y = 0; y < 11; y++) {
            for (int x = 0; x < 11; x++) {
                defendingGrid[x][y] = new GameCell();
                attackingGrid[x][y] = new GameCell();
            }
        }

//        attackingGrid[5][5].setMiss(true);
//        attackingGrid[5][6].setHit(true);
//        attackingGrid[5][7].setWaiting(true);
//        attackingGrid[5][7].setHasShip(true);

    }

    private void GetAvailableShips() {

        String url = BATTLE_SERVER_URL + "api/v1/available_ships.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("INTERNET", response.toString());
                        Iterator iter = response.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            try {
                                Integer value = (Integer) response.get(key);
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

        requestQueue.add(request);
    }

    private void GetAvailableDirections() {

        String url = BATTLE_SERVER_URL + "api/v1/available_directions.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("INTERNET", response.toString());
                        Iterator iter = response.keys();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            try {
                                Integer value = (Integer) response.get(key);
                                directionsMap.put(key, value);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        int size = directionsMap.keySet().size(); //how many elements in the map
                        directionArray = new String[size];
                        directionArray = directionsMap.keySet().toArray(new String[]{});
                        directionSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, directionArray);
                        directionSpinner.setAdapter(directionSpinnerArrayAdapter);


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

        requestQueue.add(request);
    }


    public void onClickAddShip(View v) {
        final String ships = shipSpinner.getSelectedItem().toString();
        final String rows = rowSpinner.getSelectedItem().toString();
        row = (int) rows.charAt(0) - 64;
        final String columns = colSpinner.getSelectedItem().toString();
        col = Integer.valueOf(colSpinner.getSelectedItem().toString());

        switch (directionSpinner.getSelectedItem().toString()) {

            case "north":
                directions = 0;
                break;

            case "south":
                directions = 4;
                break;

            case "east":
                directions = 2;
                break;

            case "west":
                directions = 6;
                break;

            default:
                break;

        }

        switch (shipSpinner.getSelectedItem().toString()) {

            case "battleship":
                shipLength = 4;
                break;

            case "cruiser":
                shipLength = 3;
                break;

            case "carrier":
                shipLength = 5;
                break;

            case "submarine":
                shipLength = 3;
                break;

            case "destroyer":
                shipLength = 2;
                break;

            default:
                break;

        }

        String url = BATTLE_SERVER_URL + "api/v1/game/" + gameID + "/add_ship/" + ships + "/" + rows + "/" + columns + "/" + directions + ".json";
        StringRequest request = new StringRequest(
                Request.Method.GET, url,
                // Call backs
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the returned data
                        Log.d("INTERNET", response);

                        if (response.contains("error")) {
                            //Display Error
                            AlertDialog alertDialog = new AlertDialog.Builder(
                                    BoardSetup.this).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Oops!");

                            // Setting Dialog Message
                            alertDialog.setMessage("Your ship was placed off the Grid or on another ship, please try again!");


                            // Setting Try Again Button
                            alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alertDialog.show();

                        } else {
                            //place ship
                            //Add ship to grid
                            if (shipCounter < 5) {


                                drawShip(col, row, directions, shipLength);

                                shipsMap.remove(shipSpinner.getSelectedItem());
                                shipsArray = shipsMap.keySet().toArray(new String[]{});
                                shipSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, shipsArray);
                                shipSpinner.setAdapter(shipSpinnerArrayAdapter);
                                shipSpinnerArrayAdapter.notifyDataSetChanged();
                                imgAttackGrid.invalidate();
                                imgDefensiveGrid.invalidate();
                                shipCounter += 1;

                                if (shipsMap.isEmpty()) {
                                    rdoAttack.setChecked(true);
                                    imgDefensiveGrid.setVisibility(View.INVISIBLE);
                                    imgAttackGrid.setVisibility(View.VISIBLE);
                                    shipSpinner.setVisibility(View.INVISIBLE);
                                    rowSpinner.setVisibility(View.INVISIBLE);
                                    colSpinner.setVisibility(View.INVISIBLE);
                                    directionSpinner.setVisibility(View.INVISIBLE);
                                    txtCol.setVisibility(View.INVISIBLE);
                                    txtDirection.setVisibility(View.INVISIBLE);
                                    txtRow.setVisibility(View.INVISIBLE);
                                    txtShip.setVisibility(View.INVISIBLE);


                                }

                                if (shipCounter >= 5) {
                                    btnAddShip.setVisibility(View.INVISIBLE);
                                }

                            } else {
                                btnAddShip.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with the error
                        Log.d("INTERNET", error.toString());

                        toastIt("Internet Failure: " + error.toString());
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

        requestQueue.add(request);
    }

    private void drawShip(int startX, int startY, int direction, int length) {
        //Loop through and call hasShip "length" times
        if (direction == 4) { //South
            for (int y = 0; y < length; y++) {
                defendingGrid[startX][startY + y].setHasShip(true);


            }
        } else if (direction == 2) { //East

            for (int y = 0; y < length; y++) {
                defendingGrid[startX + y][startY].setHasShip(true);

            }
        } else if (direction == 6) { //West
            for (int y = 0; y < length; y++) {
                defendingGrid[startX - y][startY].setHasShip(true);

            }
        } else if (direction == 0) { //North
            for (int y = 0; y < length; y++) {
                defendingGrid[startX][startY - y].setHasShip(true);
            }
        }
    }

    public void RadioOnClick(View v) {

        if (rdoDefend.isChecked()) {

            imgDefensiveGrid.setVisibility(View.VISIBLE);
            imgAttackGrid.setVisibility(View.INVISIBLE);
            imgAttackGrid.invalidate();
        }
        if (rdoAttack.isChecked()) {
            imgAttackGrid.setVisibility(View.VISIBLE);
            imgDefensiveGrid.setVisibility(View.INVISIBLE);
            imgDefensiveGrid.invalidate();
        }

    }


}
