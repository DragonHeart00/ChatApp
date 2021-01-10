package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatapp.MainActivity;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mName, mEmail, mPassword;
    private Button mCreate;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    //Progress Dialog
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName = findViewById(R.id.reg_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreate = findViewById(R.id.create_account_id);
        mToolbar= findViewById(R.id.register_toolbar);
        progressDialog = new ProgressDialog(this);



        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) ){
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please Wait while we create your account !");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(name,email,password);
                }


            }
        });
    }






    private void register_user(String name, String email, String password) {
       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    // to show user data in profile page
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name",name);
                    userMap.put("status","hi there i am online" );
                    userMap.put("image","default" );
                    userMap.put("thumb_image","default" );

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                progressDialog.hide();
                                Toast.makeText(RegisterActivity.this, "Sign up failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),"Cannot Sign in, Please try again ",Toast.LENGTH_SHORT).show();

                }
           }
       });
    }
}
