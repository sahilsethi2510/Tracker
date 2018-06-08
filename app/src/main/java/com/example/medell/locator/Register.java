package com.example.medell.locator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText username, email, password, cpassword;
    Button register;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.register_username);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        cpassword = (EditText) findViewById(R.id.register_confirm_password);
        register = (Button) findViewById(R.id.register_register_btn);
        databaseReference =FirebaseDatabase.getInstance().getReference("user");
    }

    public void Register(View view) {
        String str1 = username.getText().toString();
        String str2 = email.getText().toString();
        String str3 = password.getText().toString();
        String str4 = cpassword.getText().toString();
        if ((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && ((!str4.isEmpty())) && (str3.equals(str4))) {
            String id=databaseReference.push().getKey();
            database database=new database(id,str1,str2,str3);
            databaseReference.child(id).setValue(database);
            Intent login_intent = new Intent(Register.this, Login.class);
            startActivity(login_intent);
            Toast.makeText(getApplicationContext(), "REGISTERED SUCCESSFUL", Toast.LENGTH_LONG).show();
        } else if (((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && ((!str4.isEmpty()))) && (!str3.equals(str4))) {
            Toast.makeText(getApplicationContext(), "PASSWORD DO NOT MATCH", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter correct credentials", Toast.LENGTH_LONG).show();
        }

    }

}
