package com.example.kantorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class UserAccount extends AppCompatActivity {

    TextView userEmail, userBalancePLN, userBalanceEUR, userBalanceDOL, userBalanceGBP;
    TextView rateEUR, rateDOL, rateGBP;
    EditText chargeAccInput;
    Button chargeAccBtn;

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

    }

    private void userDataAccount() {

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                String curBalance = Double.toString(user.getAccbalance());
                userBalancePLN.setText(curBalance);
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
                double curBalance = user.getAccbalance();
                curBalance = curBalance + UpdateAcc;
                reference.child("accbalance").setValue(curBalance);
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
