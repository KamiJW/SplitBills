/**
 # Copyright Google Inc. 2016
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 # http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 **/

package com.google.cloud.solutions.flexenv;
//Xy
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.cloud.solutions.flexenv.common.Friend;
import com.google.cloud.solutions.flexenv.common.Message;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Main activity to select a channel and exchange messages with other users
 * The app expects users to authenticate with Google ID. It also sends user
 * activity logs to a Servlet instance through Firebase.
 */

//Hi


public class PlayActivity
        extends AppCompatActivity
        //implements NavigationView.OnNavigationItemSelectedListener,
        implements
        GoogleApiClient.OnConnectionFailedListener,
        //View.OnKeyListener,
        View.OnClickListener {
    public static PlayActivity instance = null;

    // Firebase keys commonly used with backend Servlet instances
    private static final String IBX = "inbox";
    //private static final String CHS = "channels";
    private static final String REQLOG = "requestLogger";

    private static final int RC_SIGN_IN = 9001;

    private static String TAG = "PlayActivity";
    private static FirebaseLogger fbLog;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount acct;
    private DatabaseReference firebase;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String token;
    private String inbox;
    //private String currentChannel;
    //private List<String> channels;
    //private ChildEventListener channelListener;
    private SimpleDateFormat fmt;

    //private Menu channelMenu;
    //private TextView channelLabel;
    //private ListView messageHistory;
    private List<Map<String, String>> messages;
    private SimpleAdapter messageAdapter;
    //private EditText messageText;
    //private TextView status;

    //private Account acc;
    private String cuser;
    private List<String> friends;
    private List<String> debts;
    private List<String> photoUris;
    private List<String> transactions;
    private String userEmail;
    private String photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_play);
        //setContentView(R.layout.content_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        channelMenu = navigationView.getMenu();
//        navigationView.setNavigationItemSelectedListener(this);
//        initChannels(getResources().getString(R.string.channels));

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        auth = FirebaseAuth.getInstance();

//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    inbox = "client-" + Integer.toString(Math.abs(user.getUid().hashCode()));
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + inbox);
//                    status.setText("Signin as " + user.getDisplayName());
//                    updateUI(true);
//                } else {
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    updateUI(false);
//                }
//            }
//        };
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                // Start authenticating with Google ID first.
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);


        messages = new ArrayList<Map<String, String>>();
        messageAdapter = new SimpleAdapter(this, messages, android.R.layout.simple_list_item_2,
                new String[]{"message", "meta"}, new int[]{android.R.id.text1, android.R.id.text2});
        //messageHistory = (ListView) findViewById(R.id.messageHistory);
        //messageHistory.setAdapter(messageAdapter);
        //messageText = (EditText) findViewById(R.id.messageText);
        //messageText.setOnKeyListener(this);
        fmt = new SimpleDateFormat("yy.MM.dd HH:mm z");

        //status = (TextView) findViewById(R.id.status);

        updateUI(false);
        initFirebase();
    }

    @Override
    public void onStart() {
        super.onStart();
        //auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "SignInResult : " + result.isSuccess());
            // If Google ID authentication is succeeded, obtain a token for Firebase authentication.
            if (result.isSuccess()) {
                acct = result.getSignInAccount();
                //status.setText("Authenticating with Firebase...");
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithCredential", task.getException());
                                    //status.setText("Firebase authentication failed : " + task.getException());
                                }
                                else {
                                    firebase = FirebaseDatabase.getInstance().getReference();
                                    String email = auth.getCurrentUser().getEmail();
                                    userEmail = email;
                                    Uri photo = auth.getCurrentUser().getPhotoUrl();
                                    photoUri = photo.toString();
                                    Picasso.with(PlayActivity.this).load(photo).into((ImageView)findViewById(R.id.userphoto));
                                    ((TextView)findViewById(R.id.email)).setText(filterEmail(userEmail));

                                    firebase.child("userlist").child(filterEmail(userEmail)).setValue(photo.toString());

                                    initializeFriendlist();
                                    updateUI(true);
                                }
                            }
                        });
            } else {
                updateUI(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_out_button) {
            signOut();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        //firebase.removeEventListener(channelListener);
                        if(fbLog == null){
                            return;
                        }
                        fbLog.log(inbox, "Signed out");
                        firebase.onDisconnect();
                        token = inbox = null;
                        acct = null;
                    }
                });
        updateUI(false);
    }

    private void initializeFriendlist(){
        firebase.child("user").child(filterEmail(userEmail)).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends = new ArrayList<String>();
                debts = new ArrayList<String>();
                photoUris = new ArrayList<String>();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    friends.add(dsp.getKey());
                    debts.add((String)dsp.child("debt").getValue());
                    photoUris.add((String)dsp.child("photo").getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void initializeTransactions(){
        firebase.child("user").child(filterEmail(userEmail)).child("Transactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transactions = new ArrayList<String>();
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    transactions.add(dsp.getKey());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void addFriend(final String friend){
        firebase.child("userlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(filterEmail(friend))) {
                    firebase.child("userlist").child(filterEmail(friend)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            addFireFriend(friend,(String)dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    });


                }else{
                    Log.d("fk","No");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void addFireFriend(String friend,String photo){
        Friend f = new Friend(friend,"0",photo);
        firebase.child("user").child(filterEmail(userEmail)).child("Friends").child(filterEmail(friend)).setValue(f);
        Friend f2 = new Friend(filterEmail(userEmail),"0",photoUri);
        firebase.child("user").child(filterEmail(friend)).child("Friends").child(filterEmail(userEmail)).setValue(f2);
    }



    private String filterEmail(String Email){
        String path = "";
        for(int i = 0;i<Email.length();i++){
            if(Email.charAt(i) != '.'){
                path += Email.charAt(i);
            }
        }
        return path;
    }

    private void getTransaction(){

    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_in_block).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_block).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        messages.clear();
//
//        String msg = "Switching channel to '" + item.toString() + "'";
//        try {
//            fbLog.log(inbox, msg);
//        } catch(NullPointerException e) {
//            updateUI(false);
//            return false;
//        }
//
//        // Switching a listener to the selected channel.
//        firebase.child(CHS + "/" + currentChannel).removeEventListener(channelListener);
//        currentChannel = item.toString();
//        firebase.child(CHS + "/" + currentChannel).addChildEventListener(channelListener);
//
//        channelLabel.setText(currentChannel);
//
//        return true;
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    private void initFirebase() {
        //channels = new ArrayList<String>();
        firebase = FirebaseDatabase.getInstance().getReference();
    }



// [START requestLogger]
    /*
     * Request that a Servlet instance be assigned.
     */
//    private void requestLogger() {
//        firebase.child(IBX + "/" + inbox).removeValue();
//        firebase.child(IBX + "/" + inbox).addValueEventListener(new ValueEventListener() {
//            public void onDataChange(DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    fbLog = new FirebaseLogger(firebase, IBX + "/" + snapshot.getValue().toString()
//                            + "/logs");
//                    firebase.child(IBX + "/" + inbox).removeEventListener(this);
//                    fbLog.log(inbox, "Signed in");
//
//                    //sjw request friend list
//                    //friends
//                }
//            }
//
//            public void onCancelled(DatabaseError error) {
//                Log.e(TAG, error.getDetails());
//            }
//        });
//
//        firebase.child(REQLOG).push().setValue(inbox);
//    }
// [END requestLogger]

    /*
     * Initialize pre-defined channels as Activity menu.
     * Once a channel is selected, ChildEventListener is attached and
     * waits for messages.
     */
//    private void initChannels(String channelString) {
//        Log.d(TAG, "Channels : " + channelString);
//        channels = new ArrayList<String>();
//        String[] topicArr = channelString.split(",");
//        for (int i = 0; i < topicArr.length; i++) {
//            channels.add(i, topicArr[i]);
//            channelMenu.add(topicArr[i]);
//        }
//
//        channelListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String prevKey) {
//                Message message = (Message) snapshot.getValue(Message.class);
//                // Extract attributes from Message object to display on the screen.
//                addMessage(message.getText(), fmt.format(new Date(message.getTimeLong())) + " "
//                        + message.getDisplayName());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.e(TAG, error.getDetails());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot snapshot, String prevKey) {}
//
//            @Override
//            public void onChildRemoved(DataSnapshot snapshot) {}
//
//            @Override
//            public void onChildMoved(DataSnapshot snapshot, String prevKey) {}
//        };
//    }
    public void friendButton(View v){
        Intent it = new Intent(PlayActivity.this,FriendActivity.class);
        it.putStringArrayListExtra("Emails", new ArrayList<String>(friends));
        it.putStringArrayListExtra("Debts", new ArrayList<String>(debts));
        it.putStringArrayListExtra("Photos", new ArrayList<String>(photoUris));
        startActivity(it);
    }
}
