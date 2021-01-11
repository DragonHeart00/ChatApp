package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendReqBtn;
    private ProgressDialog progressDialog;


    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrent_user;


    private  String mCurrent_state;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        String user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase =FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();



        mProfileName = findViewById(R.id.profile_displayname);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendsCount = findViewById(R.id.profile_friends_info);
        mProfileImage = findViewById(R.id.profile_avatar);
        mProfileSendReqBtn = findViewById(R.id.profile_btnSendFriendRequest);


        mCurrent_state = "not_friends";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String display_name = snapshot.child("name").getValue().toString();
                String display_status = snapshot.child("status").getValue().toString();
                String display_image = snapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(display_status);

                Picasso.get().load(display_image).placeholder(R.drawable.profile).into(mProfileImage);






                //Friend List / Request feature
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if (snapshot.hasChild(user_id)){
                            String req_type = snapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){

                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                            }else if(req_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");

                            }

                            progressDialog.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(user_id)){
                                        mCurrent_state = "Friends";
                                        mProfileSendReqBtn.setText(" Unfriend this Person");
                                    }

                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                // Not Friends state
                if (mCurrent_state.equals("not_friends")){
                    mFriendReqDatabase
                            .child(mCurrent_user.getUid())
                            .child(user_id)
                            .child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mFriendReqDatabase
                                        .child(user_id)
                                        .child(mCurrent_user.getUid())
                                        .child("request_type")
                                        .setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");



                                    }
                                });

                            }else {
                                Toast.makeText(getApplicationContext(),"Failed Sending Request", Toast.LENGTH_LONG).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });
                }


                // Cancel request state
                if (mCurrent_state.equals("req_sent")){

                    mFriendReqDatabase
                            .child(mCurrent_user.getUid())
                            .child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       //     mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())

                            mFriendReqDatabase
                                    .child(user_id)
                                    .child(mCurrent_user.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");
                                }
                            });


                        }
                    });


                }

                //Req received state
                if (mCurrent_state.equals("req_received")){

                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase
                            .child(mCurrent_user.getUid())
                            .child(user_id)
                            .setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase
                                    .child(user_id)
                                    .child(mCurrent_user.getUid())
                                    .setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase
                                            .child(mCurrent_user.getUid())
                                            .child(user_id).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //     mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())

                                                    mFriendReqDatabase
                                                            .child(user_id)
                                                            .child(mCurrent_user.getUid())
                                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mProfileSendReqBtn.setEnabled(true);
                                                            mCurrent_state = "Friends";
                                                            mProfileSendReqBtn.setText(" Unfriend this Person");
                                                        }
                                                    });


                                                }
                                            });

                                }
                            });


                        }
                    });

                }

                //switch to unfriend state
                if (mCurrent_state.equals("Friends")){

                    mFriendDatabase
                            .child(mCurrent_user.getUid())
                            .child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //     mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid())

                                    mFriendDatabase
                                            .child(user_id)
                                            .child(mCurrent_user.getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProfileSendReqBtn.setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            mProfileSendReqBtn.setText("Send Friend Request");
                                        }
                                    });


                                }
                            });

                }
            }
        });



    }
}