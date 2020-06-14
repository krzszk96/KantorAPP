package com.example.kantorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText inEmail, inPasswd;
    Button login, singup;
    ProgressBar progressLoad;

    FirebaseAuth mAuth;
    FirebaseDatabase userData;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inEmail = (EditText) findViewById(R.id.entEmail);
        inPasswd = (EditText) findViewById(R.id.entPasswd);
        login = (Button) findViewById(R.id.loginBtn);
        singup = (Button) findViewById(R.id.signupBtn);
        progressLoad = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        //new user sign up to Firebase on btn singup click
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoad.setVisibility(View.VISIBLE);
                createUser(inEmail.getText().toString(), inPasswd.getText().toString());

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressLoad.setVisibility(View.VISIBLE);
                loginUser(inEmail.getText().toString(), inPasswd.getText().toString());
            }
        });
    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }
    public void saveUserData(String email, String password){

            userData = FirebaseDatabase.getInstance();
            reference = userData.getReference("users");

            //get the values to save to database
            email = encodeUserEmail(inEmail.getText().toString());
            password = inPasswd.getText().toString();
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Balance balance = new Balance(0,0,0,0);

            UserTransaction tDb = new UserTransaction(); ///this is history
            List<String> transactionstest = new ArrayList<>();
            transactionstest.add("Transaction history initial");
            tDb.setTr(transactionstest);

            //create new record in database
            Users helperClass = new Users(id, email, password, balance, tDb);
            reference.child(id).setValue(helperClass);

    }
    public void createUser(final String email, final String password){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressLoad.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    saveUserData(email, password);
                    Toast.makeText(MainActivity.this, "Registration Succesfull!.", Toast.LENGTH_SHORT).show();
                    inEmail.setText("");
                    inPasswd.setText("");
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressLoad.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    inEmail.setText("");
                    inPasswd.setText("");
                    startActivity(new Intent(MainActivity.this, UserAccount.class));
                }else{
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
