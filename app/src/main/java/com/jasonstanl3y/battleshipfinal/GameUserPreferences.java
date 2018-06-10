package com.jasonstanl3y.battleshipfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class GameUserPreferences extends BaseActivity {

    TextView txtName;
    TextView txtLevel;
    TextView txtCoins;
    TextView txtWon;
    TextView txtLost;
    TextView txtTied;
    TextView txtExp;
    TextView txtAvailable;
    ImageView imageAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_user_preferences);

//        prefs.setVisible(false);

        txtName = findViewById(R.id.txtAvatarName);
        txtLevel = findViewById(R.id.txtLevel);
        txtCoins = findViewById(R.id.txtCoins);
        txtWon = findViewById(R.id.txtWon);
        txtLost = findViewById(R.id.txtLost);
        txtTied = findViewById(R.id.txtTied);
        txtExp = findViewById(R.id.txtExp);
        txtAvailable = findViewById(R.id.txtAvailable);
        imageAvatar = findViewById(R.id.imageAvatar);


        txtName.setText(userPrefs.getAvatarName());
        txtLevel.setText(String.valueOf(userPrefs.getLevel()));
        txtCoins.setText(String.valueOf(userPrefs.getCoins()));
        txtWon.setText(String.valueOf(userPrefs.getBattlesWon()));
        txtLost.setText(String.valueOf(userPrefs.getBattlesLost()));
        txtTied.setText(String.valueOf(userPrefs.getBattlesTied()));
        txtExp.setText(String.valueOf(userPrefs.getExperiencePoints()));
        txtAvailable.setText(String.valueOf(userPrefs.getAvailable()));
        Picasso.with(getApplicationContext()).load(BATTLE_SERVER_URL + userPrefs.getAvatarImage()).into(imageAvatar);


    }
}
