package com.example.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatapp.R;
import com.example.chatapp.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mUserList = findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));


    }


/*


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(mUserDatabase, Users.class).build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {

                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
              //  holder.setImage(model.getImage());
                Log.d("TAG", "--------------");
                Log.d("TAG", "users.getName() : " + model.getName());
                Log.d("TAG", "users.getStatus() : " + model.getStatus());
                Log.d("TAG", "users.getThumb_image() : " + model.getImage());
                final String selected_user_id = getRef(position).getKey();



            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false);
                return new UserViewHolder(view);
            }
        };


        mUserList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public static class UserViewHolder extends  RecyclerView.ViewHolder {
        View mView;



        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name){
            TextView show_name = mView.findViewById(R.id.user_single_image);
            show_name.setText(name);
        }

        public void setStatus(String status){
            TextView show_status = mView.findViewById(R.id.user_single_status);
            show_status.setText(status);
        }

//        public void setImage(String url_avatar){
//            CircleImageView userAvatarView = mView.findViewById(R.id.user_single_image);
////                if user hadn't set avatar display default avatar
//            if(!url_avatar.equals("default")){
//                Picasso.get().load(url_avatar).placeholder(R.drawable.defaultavatar).into(userAvatarView);
//            }
//        }

    }
 */
}