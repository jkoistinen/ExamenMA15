package com.jk.examenma15;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
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

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Firebase myFirebaseRef;
    private Firebase userRef;

    private List<String> todolists = new ArrayList<String>();
    private static List<String> firebasestringkeys = new ArrayList<String>();
    private static CustomListsAdapter adapter;

    private FirebaseDatabase mDatabase;

    private String url;
    private String uid;

    private TextView mGreetingTextView;
    private ListView mTodolistListView;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeListItem(Integer pos){

        String listtoberemoved = firebasestringkeys.get(pos);

        Firebase ref = new Firebase("https://examenma15.firebaseio.com");

        //path is todos/$UID/List1
        String UID = mAuth.getCurrentUser().getUid();

        //write _ToDoList_ objects here
        userRef = ref.child("todos").child(UID);

        userRef.child(listtoberemoved).removeValue();


    }

    public void showList(Integer pos, Context ctx){

        Log.d(TAG, "Length firebasestringkeys:"+firebasestringkeys.size());
        Log.d(TAG, "clicked item "+pos+" would be mapped against "+ firebasestringkeys.get(pos));

        Intent intent = new Intent(ctx, ToDoListActivity.class);
        intent.putExtra("LISTREF", firebasestringkeys.get(pos));
        ctx.startActivity(intent);

    }

    public void updateList(String listname) {
        todolists.add(listname);
        adapter.notifyDataSetChanged();
    }

    public void pushFirebase(String text){
        userRef.push().setValue(new ToDoList(text));
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

                pushFirebase(newlistname);

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

        mGreetingTextView = (TextView) findViewById(R.id.greetingtextview);
        mGreetingTextView.setText("Logged in as: "+mAuth.getCurrentUser().getEmail().toString());

        adapter = new CustomListsAdapter(MainActivity.this, R.layout.activity_main_customlistview, R.id.todolistTextView, todolists);

        mTodolistListView = (ListView) findViewById(R.id.todolistView);

        mTodolistListView.setAdapter(adapter);

        mTodolistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "clicked a view");
            }
        });

        Intent intent = getIntent();
        url = intent.getStringExtra("FIREBASE_URL");

        myFirebaseRef = new Firebase(url);

        //path is todos/$UID/List1
        uid = mAuth.getCurrentUser().getUid();

        //write _ToDoList_ objects here
        userRef = myFirebaseRef.child("todos").child(uid);

        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("todos/"+uid);

        Log.d(TAG, "User UID: " + mAuth.getCurrentUser().getUid());

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, ""+dataSnapshot.getChildrenCount());

                //clear the ListView, clear the ArrayList and notify the adapter that data has changed.
                todolists.clear();
                adapter.notifyDataSetChanged();

                firebasestringkeys.clear();

                for(DataSnapshot todolistSnapshot: dataSnapshot.getChildren()){

                    firebasestringkeys.add(todolistSnapshot.getKey());

                    ToDoList todolist = todolistSnapshot.getValue(ToDoList.class);
                    updateList(todolist.getTitle());
                }
                if (firebasestringkeys.size() > 0) {
                    Log.d(TAG, firebasestringkeys.get(0));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot todolistSnapshot: dataSnapshot.getChildren()){
                    firebasestringkeys.add(todolistSnapshot.getKey());
                    Log.d(TAG, "singleValueEvent");
                    Log.d(TAG, "Length of keys: "+firebasestringkeys.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
