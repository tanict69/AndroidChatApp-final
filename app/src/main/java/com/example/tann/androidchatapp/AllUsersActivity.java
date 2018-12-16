package com.example.tann.androidchatapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUserReference;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mtoolbar=(Toolbar)findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList=(RecyclerView)findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUserReference= FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUserReference.keepSynced(true);

        searchView =(SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchUserName=searchView.getQuery().toString();
                SearchFriend(searchUserName);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }
private void SearchFriend(String searchUserName){
        Query SearchFr=allDatabaseUserReference.orderByChild("user_name")
                .startAt(searchUserName).endAt(searchUserName+"\uf8ff");

    FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter
            =new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
            (
                    AllUsers.class,
                    R.layout.all_users_display_layout,
                    AllUsersViewHolder.class,
                    SearchFr
            ) {
        @Override
        protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
            viewHolder.setUser_name(model.getUser_name());
            viewHolder.setUser_status(model.getUser_status());
            viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_image());
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //lấy user id của của item được click
                    String visit_user_id=getRef(position).getKey();
                    Intent profileIntent=new Intent(AllUsersActivity.this,ProfileActivity.class);
                    profileIntent.putExtra("visit_user_id",visit_user_id);
                    startActivity(profileIntent);
                }
            });



        }
    };
    allUsersList.setAdapter(firebaseRecyclerAdapter);

}


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
                (
                        AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUsersViewHolder.class,
                        allDatabaseUserReference
                ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_image());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lấy user id của của item được click
                        String visit_user_id=getRef(position).getKey();
                        Intent profileIntent=new Intent(AllUsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });



            }
        };
        allUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder
    {
        View mView; // username,status,image
        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setUser_name(String user_name){
            TextView name=(TextView)mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }
        public void setUser_status(String user_status){
            TextView status=(TextView)mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }
        public void setUser_thumb_image(final Context ctx, final String setUser_thumb_image){
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.all_users_profile_image);
            //set image offline
            Picasso.with(ctx).load(setUser_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(setUser_thumb_image).into(thumb_image);
                }
            });


        }
    }
}
