package com.jk.examenma15;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        //Other setup code

        Firebase myFirebaseRef = new Firebase("https://examenma15.firebaseio.com/");

        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

    }
}
