package com.example.kantorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    TextView userEmail, userBalance;
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
        userBalance = (TextView) findViewById(R.id.balanceView);
        chargeAccInput = (EditText) findViewById(R.id.chargeInput);
        chargeAccBtn = (Button) findViewById(R.id.chargeBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userEmail.setText(firebaseUser.getEmail());
        userDataAccount();

    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
    private void userDataAccount() {

        //final String userEmailDec = firebaseUser.getEmail();
        //final String userEmailCod = encodeUserEmail(userEmailDec);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("users").child(id);
                reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                String curBalance = Double.toString(user.getAccbalance());
                userBalance.setText(curBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
