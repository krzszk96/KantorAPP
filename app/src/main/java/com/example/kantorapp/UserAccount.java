package com.example.kantorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserAccount extends AppCompatActivity {

    TextView userEmail, userBalancePLN, userBalanceEUR, userBalanceDOL, userBalanceGBP;
    TextView rateEUR, rateDOL, rateGBP;
    EditText chargeAccInput;
    Button chargeAccBtn;
    Button buyNew, history, transactionsView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        userEmail = (TextView) findViewById(R.id.loggedEmail);
        userBalancePLN = (TextView) findViewById(R.id.balanceViewPLN);
        userBalanceEUR = (TextView) findViewById(R.id.balanceViewEUR);
        userBalanceDOL = (TextView) findViewById(R.id.balanceViewDOL);
        userBalanceGBP = (TextView) findViewById(R.id.balanceViewGBP);
        chargeAccInput = (EditText) findViewById(R.id.chargeInput);
        chargeAccBtn = (Button) findViewById(R.id.chargeBtn);

        rateEUR = (TextView) findViewById(R.id.displayEUR);
        rateDOL = (TextView) findViewById(R.id.displayDOL);
        rateGBP = (TextView) findViewById(R.id.displayGBP);

        buyNew = (Button) findViewById(R.id.buyScreen);
        transactionsView = (Button) findViewById(R.id.transScreen);
        history = (Button) findViewById(R.id.archScreen);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userBalancePLN.setText("0");
        userBalanceEUR.setText("0");
        userBalanceDOL.setText("0");
        userBalanceGBP.setText("0");

        userEmail.setText(firebaseUser.getEmail());
        userDataAccount();

        chargeAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpAcc();
                chargeAccInput.setText("");
            }
        });

        mQueue = Volley.newRequestQueue(this);
        jsonParseEur();
        jsonParseDol();
        jsonParseGbp();

        buyNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAccount.this, Transactions.class));
            }
        });
        transactionsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAccount.this, TransactionView.class));
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserAccount.this, ArchiveRates.class));
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
                userBalancePLN.setText(curBalance);
                userBalanceEUR.setText(curBalanceE);
                userBalanceDOL.setText(curBalanceD);
                userBalanceGBP.setText(curBalanceG);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void topUpAcc(){
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final double UpdateAcc = Double.parseDouble(chargeAccInput.getText().toString());

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                double curBalance = user.getAccbalance().getPln();
                List<String> userTr = user.getHistTrans().getTr(); //get user transaction history
                curBalance = curBalance + UpdateAcc;
                String hist = "Top up : " + UpdateAcc + " PLN"; //save new transaction to string
                userTr.add(hist); //add new transaction to list
                reference.child("histTrans").child("tr").setValue(userTr); //update database with transaction list
                reference.child("accbalance").child("pln").setValue(curBalance);
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
