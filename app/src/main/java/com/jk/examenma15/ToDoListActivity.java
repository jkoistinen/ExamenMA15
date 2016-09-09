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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private static String TAG = "ToDoListActivity";

    private static String teststring;

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Firebase myFirebaseRef;
    private Firebase userRef;
    private Firebase listRef;
    private Firebase itemRef;

    private List<String> todos = new ArrayList<String>();

    private static List<String> firebasestringkeys = new ArrayList<String>();

    private static CustomTodoAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void updateList(String itemname) {
        todos.add(itemname);
        adapter.notifyDataSetChanged();
    }

    public void pushFirebase(String text){
        listRef.child("items").push().setValue(new ToDo(text, 2016));
    }

    public void removeListItem(Integer pos){

        String UID = mAuth.getCurrentUser().getUid();
        String ListUID = teststring;
        String ItemUID  = firebasestringkeys.get(pos);
        myFirebaseRef = new Firebase("https://examenma15.firebaseio.com");
        itemRef = myFirebaseRef.child("todos").child(UID).child(ListUID).child("items").child(ItemUID);
        Log.d(TAG, itemRef.getKey());
        todos.remove(pos);
        itemRef.removeValue();

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

        teststring = listRefintent;

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

                firebasestringkeys.add(dataSnapshot.getKey());

                ToDo todoitem = dataSnapshot.getValue(ToDo.class);
                updateList(todoitem.getText());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");

                Log.d(TAG, "todos size: "+todos.size() + "firebasestringkeys size: "+firebasestringkeys.size());

                //adapter.notifyDataSetChanged();

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

        adapter = new CustomTodoAdapter(ToDoListActivity.this, R.layout.activity_to_do_customlistview, R.id.todoTextView, todos);

        final ListView mTodos = (ListView) findViewById(R.id.todoitemView);

        mTodos.setAdapter(adapter);

        mTodos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "clicked a view");
            }
        });

    }


}
