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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserAccount extends AppCompatActivity {

    TextView userEmail, userBalancePLN, userBalanceEUR, userBalanceDOL, userBalanceGBP;
    EditText chargeAccInput;
    Button chargeAccBtn;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase userData;
    DatabaseReference reference;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userBalancePLN.setText("");
        userBalanceEUR.setText("");
        userBalanceDOL.setText("");
        userBalanceGBP.setText("");

        userEmail.setText(firebaseUser.getEmail());
        userDataAccount();

        chargeAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpAcc();
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
}
