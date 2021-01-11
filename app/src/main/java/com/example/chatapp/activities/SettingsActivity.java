package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import com.example.chatapp.R;
import com.example.chatapp.change.StatusActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import id.zelory.compressor.Compressor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {
    private CircleImageView imageView;
    private TextView mName, mStatus;
    private Button change_image, change_status;
    private static final int GALLERY_PICK = 1;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;



    private ProgressDialog progressDialog;


    //Storage database
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);




        mName = findViewById(R.id.display_name_id);
        mStatus = findViewById(R.id.status_id_text);
        imageView = findViewById(R.id.imageView_d);
        change_status=findViewById(R.id.change_status_id);
        change_image = findViewById(R.id.change_image_id);




        //Storage database
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);



        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              String name = snapshot.child("name").getValue().toString();
              final String image = snapshot.child("image").getValue().toString();
              String status = snapshot.child("status").getValue().toString();
              String thumb_image = snapshot.child("thumb_image").getValue().toString();


              mName.setText(name);
              mStatus.setText(status);

              if (!image.equals("default")){
                  Picasso.get()
                          .load(image)
                          .networkPolicy(NetworkPolicy.OFFLINE)
                          .placeholder(R.drawable.profile)
                          .into(imageView, new Callback() {
                              @Override
                              public void onSuccess() {

                              }

                              @Override
                              public void onError(Exception e) {
                                  Picasso.get()
                                          .load(image)
                                          .placeholder(R.drawable.profile)
                                          .into(imageView);
                              }
                          });
              }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value = mStatus.getText().toString();

                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
                intent.putExtra("status_value",status_value);
                startActivity(intent);
            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);


                /*
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                           */

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode ==RESULT_OK ){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

          //  Toast.makeText(getApplicationContext(),imageUri ,Toast.LENGTH_SHORT).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please Wait while we upload and process the image !");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String current_user_id = mCurrentUser.getUid();


                File thumb_file_path = new File(resultUri.getPath());
                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_file_path);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
                byte[] thumb_byte = baos.toByteArray();





                StorageReference filePath = mImageStorage.child("profile_images").child(current_user_id+".jpg");
                StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id +".jpg" );







                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            @SuppressWarnings("VisibleForTests")
                            String download_url = resultUri.toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = resultUri.toString();

                                    if (thumb_task.isSuccessful()){

                                        Map<String, String> update_hash_map = new HashMap();
                                        update_hash_map.put("image", download_url);
                                        update_hash_map.put("thumb_image",thumb_downloadUrl);
                                        update_hash_map.put("name", mName.getText().toString() );
                                        update_hash_map.put("status",mStatus.getText().toString());


                                        mUserDatabase.setValue(update_hash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Successful",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }else {
                                        Toast.makeText(SettingsActivity.this,"Error in uploading thumbnail",Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }

                                }
                            });





                        }else {
                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),"error" +error,Toast.LENGTH_SHORT).show();
            }
        }




    }



}