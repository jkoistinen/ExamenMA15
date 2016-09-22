package com.jk.examenma15;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private static String TAG = "ToDoListActivity";

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Firebase myFirebaseRef;
    private Firebase userRef;

    private ArrayList<ToDo> todos = new ArrayList<ToDo>();

    private static List<String> firebasestringkeys = new ArrayList<String>();

    private static CustomTodoAdapter adapter;

    private static String listuidstring;
    private Firebase listRef;
    private Firebase itemRef;
    private FirebaseDatabase mDatabase;

    private String title;
    private String uid;
    private String listuid;
    private String itemuid;

    private ListView mTodosListView;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        firebasestringkeys.clear();
    }
    public void updateList(ToDo itemname) {
        todos.add(itemname);
        adapter.notifyDataSetChanged();
    }

    public void pushFirebase(String text){
        Firebase itemref = listRef.child("items").push();
        itemref.setValue(new ToDo(text, 2016));
    }

    public void removeListItem(Integer pos){
        itemuid  = firebasestringkeys.get(pos);
        firebasestringkeys.remove(pos);
        Firebase itemref = listRef.child("items").child(itemuid);
        itemref.removeValue();

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

        listuidstring = listRefintent;

        uid = mAuth.getCurrentUser().getUid();
        myFirebaseRef = new Firebase("https://examenma15.firebaseio.com");

        userRef = myFirebaseRef.child("todos").child(uid);
        listRef = userRef.child(listRefintent);

        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("todos/"+uid+"/"+listRefintent+"/items");

        Log.d(TAG, "User UID: " + mAuth.getCurrentUser().getUid());
        Log.d(TAG, "myRef is:"+myRef.toString());
        Log.d(TAG, "listRef is:"+listRef.toString());

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d(TAG,"dataSnapshot value is: "+dataSnapshot.getValue());

                firebasestringkeys.add(dataSnapshot.getKey());

                ToDo todoitem = dataSnapshot.getValue(ToDo.class);
                updateList(todoitem);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
                adapter.notifyDataSetChanged();
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

        adapter = new CustomTodoAdapter(ToDoListActivity.this, todos);

        mTodosListView = (ListView) findViewById(R.id.todoitemView);

        mTodosListView.setAdapter(adapter);

        //SetTitle
        listRef.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                Log.d(TAG, "value is: " + dataSnapshot.getValue().toString());
                title = dataSnapshot.getValue().toString();

                setTitle(title);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //END SetTitle

    }
}
