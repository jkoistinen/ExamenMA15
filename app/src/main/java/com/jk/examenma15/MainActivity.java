package com.jk.examenma15;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Firebase myFirebaseRef;

    private List<String> todolists = new ArrayList<String>();

    private ArrayAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void updateList(String listname) {

        todolists.add(listname);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newlistEditText = (EditText) findViewById(R.id.newListEditText);
                String newlistname = newlistEditText.getText().toString();
                newlistEditText.setText("");
                updateList(newlistname);

            }
        });

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_multichoice, todolists);

        todolists.add("List 1");
        todolists.add("List 2");
        todolists.add("List 3");
        todolists.add("List 4");
        todolists.add("List 5");

        ListView mTodolists = (ListView) findViewById(R.id.todolistView);


        mTodolists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "clicked item "+i);

                //Send to TodoListActivity
                //Package int i to TodoListActivity, send in Bundle with the Intent.
            }
        });

        mTodolists.setAdapter(adapter);


        Intent intent = getIntent();
        final String url = intent.getStringExtra("FIREBASE_URL");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myFirebaseRef = new Firebase(url);

        //path is todos/$UID/List1
        String UID = mAuth.getCurrentUser().getUid();

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("todos/"+UID);

        myRef.setValue("Testing...");

        Log.d(TAG, "User UID: " + mAuth.getCurrentUser().getUid());

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"db changed!" + dataSnapshot.getValue());

                String value = dataSnapshot.getValue().toString();

                updateList(value);

                for(DataSnapshot todoSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG,"db changed!");
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void updateTextView(String value){

        TextView textview = (TextView) findViewById(R.id.textview);
        textview.setText(value);

    }




}
