package com.example.medell.locator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login, register;
    int loginStatus = 0;
    SharedPreferences sharedPreferences;
    DatabaseReference readDatabase;
    String str1, str2;
    int COUNT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_login_btn);
        register = (Button) findViewById(R.id.login_register_btn);
        readDatabase = FirebaseDatabase.getInstance().getReference("user");
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(Login.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,ACCESS_FINE_LOCATION)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Login.this, new String[]{ACCESS_FINE_LOCATION},0);

            } else {

                ActivityCompat.requestPermissions(Login.this, new String[]{ACCESS_FINE_LOCATION},0);

            }
        }






    }

    public void Register(View view) {
        Intent register_intent = new Intent(Login.this, Register.class);
        startActivity(register_intent);
    }


    public void Login(View view) {
        COUNT++;
        if(COUNT==1) {
            str1 = email.getText().toString();
            str2 = password.getText().toString();
            if ((!str1.isEmpty()) && ((!str2.isEmpty()))) {
                readDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                database resultValues = snapshot.getValue(database.class);
                                if (resultValues != null) {
                                    if ((!str1.isEmpty()) && (!str1.isEmpty())) {
                                        if (resultValues.email.equals(str1) && resultValues.password.equals(str2)) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("currentUser", snapshot.getKey());
                                            editor.putBoolean("login", true);
                                            editor.apply();
                                            Toast.makeText(Login.this, "AUTHENTICATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                            loginStatus = 1;
                                            Intent intent = new Intent(Login.this, Channelid.class);
                                            startActivity(intent);
                                            email.setText("");
                                            password.setText("");
                                            COUNT=0;
                                            break;
                                        }
                                    }
                                }
                            }

                            if ((!str1.isEmpty()) && (!str1.isEmpty())) {
                                if (loginStatus == 0) {
                                    Toast.makeText(Login.this, "INVALID CREDENTIALS", Toast.LENGTH_SHORT).show();
                                    email.setText("");
                                    password.setText("");
                                    str1 = "";
                                    str2 = "";
                                    COUNT=0;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "INCOMPLETE CREDENTIALS", Toast.LENGTH_LONG).show();
            }
        }
        if(COUNT>1){
            Toast.makeText(this, "WAITING FOR INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
            COUNT=0;
            str1="";
            str2="";

        }
    }
}