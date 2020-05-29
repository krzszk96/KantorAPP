package com.example.kantorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.round;

public class Transactions extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    TextView userEmail,balancePLN, balanceEUR, balanceUSD, balanceGBP, rateEUR, rateDOL, rateGBP;
    EditText buyEur;
    Button buyEurBtn, sellEurBtn;


    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        userEmail = (TextView) findViewById(R.id.loggedEmailT);

        balancePLN = (TextView) findViewById(R.id.balanceViewPLN);
        balanceEUR = (TextView) findViewById(R.id.balanceViewEUR);
        balanceUSD = (TextView) findViewById(R.id.balanceViewUSD);
        balanceGBP = (TextView) findViewById(R.id.balanceViewGBP);

        rateEUR = (TextView) findViewById(R.id.EURrateDisp);
        rateDOL = (TextView) findViewById(R.id.USDrateDisp);
        rateGBP = (TextView) findViewById(R.id.GBPrateDisp);

        buyEur = (EditText) findViewById(R.id.inputEur);
        buyEurBtn = (Button) findViewById(R.id.buyEuro);
        sellEurBtn = (Button) findViewById(R.id.sellEuro);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userEmail.setText(firebaseUser.getEmail());

        userDataAccount();

        mQueue = Volley.newRequestQueue(this);
        jsonParseEur();
        jsonParseDol();
        jsonParseGbp();

        buyEurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyEuro();
                buyEur.setText("");
            }
        });
        sellEurBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellEuro();
                buyEur.setText("");
            }
        });

    }
    private void userDataAccount() {

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                String curBalance = Double.toString(user.getAccbalance().getPln());
                String curBalanceE = Double.toString(user.getAccbalance().getEur());
                String curBalanceD = Double.toString(user.getAccbalance().getUsd());
                String curBalanceG = Double.toString(user.getAccbalance().getGbp());
                balancePLN.setText(curBalance);
                balanceEUR.setText(curBalanceE);
                balanceUSD.setText(curBalanceD);
                balanceGBP.setText(curBalanceG);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void buyEuro(){
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final double euroAmount = Double.parseDouble(buyEur.getText().toString()); //how much buy
        final double euroRate = Double.parseDouble(rateEUR.getText().toString());  //rate

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                double curBalance = user.getAccbalance().getPln();  //get user pln amount
                double curBalanceE = user.getAccbalance().getEur(); //get user eur amount
                if((euroAmount * euroRate) < curBalance ){
                    curBalance = curBalance - (euroAmount * euroRate);
                    curBalanceE = curBalanceE + euroAmount;
                }else{
                    Toast.makeText(Transactions.this, "You don't have enough money to buy", Toast.LENGTH_LONG).show();
                }

                reference.child("accbalance").child("pln").setValue(Math.round(curBalance * 100.0)/100.0);
                reference.child("accbalance").child("eur").setValue(Math.round(curBalanceE * 100.0)/100.0); //set new value in database

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sellEuro(){
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final double euroAmount = Double.parseDouble(buyEur.getText().toString()); //how much buy
        final double euroRate = Double.parseDouble(rateEUR.getText().toString());  //rate

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                double curBalance = user.getAccbalance().getPln();  //get user pln amount
                double curBalanceE = user.getAccbalance().getEur(); //get user eur amount
                if( euroAmount <= curBalanceE ){
                    curBalance = curBalance + (euroAmount * euroRate);
                    curBalanceE = curBalanceE - euroAmount;
                }else{
                    Toast.makeText(Transactions.this, "You don't have enough euro to sell", Toast.LENGTH_LONG).show();
                }
                //update amount
                reference.child("accbalance").child("pln").setValue(Math.round(curBalance * 100.0)/100.0);
                reference.child("accbalance").child("eur").setValue(Math.round(curBalanceE * 100.0)/100.0); //set new value in database

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void jsonParseEur() {
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/eur/?format=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("rates");
                    JSONObject rate = jsonArray.getJSONObject(0);
                    double myRate = rate.getDouble("mid");
                    String dispRate = Double.toString(myRate);
                    rateEUR.setText(dispRate);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    private void jsonParseDol() {
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/usd/?format=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("rates");
                    JSONObject rate = jsonArray.getJSONObject(0);
                    double myRate = rate.getDouble("mid");
                    String dispRate = Double.toString(myRate);
                    rateDOL.setText(dispRate);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    private void jsonParseGbp() {
        String url = "https://api.nbp.pl/api/exchangerates/rates/a/gbp/?format=json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("rates");
                    JSONObject rate = jsonArray.getJSONObject(0);
                    double myRate = rate.getDouble("mid");
                    String dispRate = Double.toString(myRate);
                    rateGBP.setText(dispRate);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}
