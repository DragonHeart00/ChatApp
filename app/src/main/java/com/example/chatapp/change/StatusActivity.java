package com.example.chatapp.change;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSaveBtn;

    //progress dialog
    private ProgressDialog mProgress;


    //firebaseStore
    private DatabaseReference mStatusDatabase;

    //to get user id
    FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //progress dialog
        mProgress = new ProgressDialog(this);

        //toolbar
        mToolbar = findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");



        mStatus = findViewById(R.id.change_status_edit_text);
        mSaveBtn = findViewById(R.id.save_change_bt);


        mStatus.getEditText().setText(status_value);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress.setTitle("Saving changes");
                mProgress.setMessage("Please Wait while we save the changes!");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            onBackPressed();

                        }else {
                            Toast.makeText(getApplicationContext(),"Cannot Save change, Please try again ",Toast.LENGTH_LONG).show();
                            mProgress.hide();

                        }
                    }
                });


            }
        });



    }
}