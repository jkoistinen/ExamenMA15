package com.jk.examenma15;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private static String TAG = "ToDoListActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Firebase myFirebaseRef;
    private Firebase userRef;

    private Firebase listRef;

    private List<String> todolistitems = new ArrayList<String>();

    private ArrayAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void updateList(String itemname) {
        todolistitems.add(itemname);
        adapter.notifyDataSetChanged();
    }

    public void pushFirebase(String text){
        listRef.child("items").push().setValue(new ToDo(text, 2016));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newlistEditText = (EditText) findViewById(R.id.newTodoEditText);
                String newtodoname = newlistEditText.getText().toString();
                newlistEditText.setText("");

                pushFirebase(newtodoname);
            }
        });

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

        Intent intent = getIntent();
        final String listRefintent = intent.getStringExtra("LISTREF");

        String UID = mAuth.getCurrentUser().getUid();
        myFirebaseRef = new Firebase("https://examenma15.firebaseio.com");
        userRef = myFirebaseRef.child("todos").child(UID);

        listRef = userRef.child(listRefintent);

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("todos/"+UID+"/"+listRefintent+"/items");

        Log.d(TAG, "User UID: " + mAuth.getCurrentUser().getUid());

        Log.d(TAG, "myRef is:"+myRef.toString());

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d(TAG,"dataSnapshot value is: "+dataSnapshot.getValue());

                ToDo todoitem = dataSnapshot.getValue(ToDo.class);

                Log.d(TAG, todoitem.getText());

                updateList(todoitem.getText()+" "+"Expires:"+todoitem.getExpiredate());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });

        adapter = new ArrayAdapter<String>(ToDoListActivity.this, android.R.layout.select_dialog_multichoice, todolistitems);

        final ListView mTodoItems = (ListView) findViewById(R.id.todoitemView);

        mTodoItems.setAdapter(adapter);

    }


}
