package com.example.tann.androidchatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.Log.i;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestList;
    private View RequestFradmentView;
    private DatabaseReference chatRequestReference,UserRef,FriendDatabaseRef,FriendReqDatabaseRef;
    private DatabaseReference UserReference;
    private FirebaseAuth mAuth;
    String online_user_id;




    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RequestFradmentView=inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestList=(RecyclerView)RequestFradmentView.findViewById(R.id.chat_requests_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        chatRequestReference= FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        FriendDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        UserReference=FirebaseDatabase.getInstance().getReference().child("Users");








        // Inflate the layout for this fragment
        return RequestFradmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests,RequestViewHolder>firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
                (Requests.class,
                R.layout.friend_request_all_users_layout,
                        RequestsFragment.RequestViewHolder.class,
                        chatRequestReference

                )
        {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, final int position) {
            /*
                final String list_users_id=getRef(position).getKey();

                UserReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();


                            String type=dataSnapshot.getValue().toString();
                            //if(type.equals("received")){
                           // Toast.makeText(getActivity(),type,Toast.LENGTH_SHORT).show();
                                final String userName=dataSnapshot.child("user_name").getValue().toString();
                                final  String thumbImage=dataSnapshot.child("user_thumb_image").getValue().toString();
                                final String user_Status=dataSnapshot.child("user_status").getValue().toString();

                                viewHolder.setUserName(userName);
                                viewHolder.setThumb_user_image(thumbImage,getContext());
                                viewHolder.setUser_Status(user_Status);
                            }


                        }








                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
//
                final String list_user_id=getRef(position).getKey();
                final DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type=dataSnapshot.getValue().toString();
                            if(type.equals("received")){
                                UserReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName=dataSnapshot.child("user_name").getValue().toString();
                                        final  String thumbImage=dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String user_Status=dataSnapshot.child("user_status").getValue().toString();
                                        viewHolder.userName.setVisibility(View.VISIBLE);
                                        viewHolder.userStatus.setVisibility(View.VISIBLE);
                                        viewHolder.profileImage.setVisibility(View.VISIBLE);
                                        viewHolder.acceptBtn.setVisibility(View.VISIBLE);
                                        viewHolder.cancelBtn.setVisibility(View.VISIBLE);

                                        viewHolder.setUserName(userName);
                                        viewHolder.setThumb_user_image(thumbImage,getContext());
                                        viewHolder.setUser_Status(user_Status);
                                        /*
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Accept",
                                                                "Cancel"
                                                        };
                                                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                builder.setTitle(userName+"Chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        //click vao Accept
                                                        if(i==0)
                                                        {
                                                            Calendar calForDate=Calendar.getInstance();
                                                            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String saveCurrentDate=currentDate.format(calForDate.getTime());
                                                            FriendDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            FriendDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if(task.isSuccessful()){

                                                                                                                            Toast.makeText(getContext(),"Friend Request Accept Successfully!",Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }
                                                                                                    });

                                                                                        }
                                                                                    });
                                                                        }
                                                                    });

                                                        }
                                                        if(i==1)
                                                        {
                                                            FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){

                                                                                            Toast.makeText(getContext(),"Friend Request Cacel Successfully",Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }
                                        });*/
                                        viewHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Calendar calForDate=Calendar.getInstance();
                                                SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
                                                final String saveCurrentDate=currentDate.format(calForDate.getTime());
                                                FriendDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                FriendDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){
                                                                                                    FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                final LayoutInflater layoutInflater=getLayoutInflater();
                                                                                                                final View Profile_layout=layoutInflater.inflate(R.layout.activity_profile,null);
                                                                                                                Button unfriend_btn=(Button)Profile_layout.findViewById(R.id.profile_visit_friend_req_btn);
                                                                                                                unfriend_btn.setText("Unfriend this person");
                                                                                                                Toast.makeText(getContext(),"Friend Request Accept Successfully!",Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        });

                                                                            }
                                                                        });
                                                            }
                                                        });

                                            }
                                        });
                                        viewHolder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                FriendReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    FriendReqDatabaseRef.child(list_user_id).child(online_user_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                final LayoutInflater layoutInflater=getLayoutInflater();
                                                                                final View Profile_layout=layoutInflater.inflate(R.layout.activity_profile,null);
                                                                                Button sendfriendReq_btn=(Button)Profile_layout.findViewById(R.id.profile_visit_friend_req_btn);
                                                                                sendfriendReq_btn.setText("Send Friend Request");
                                                                                Toast.makeText(getContext(),"Friend Request Cancel Successfully",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



//

            }
        };

        myRequestList.setAdapter(firebaseRecyclerAdapter);




    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView userName,userStatus;
        CircleImageView profileImage;
        Button acceptBtn,cancelBtn;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            userName=itemView.findViewById(R.id.request_profile_name);
            userStatus=itemView.findViewById(R.id.request_profile_status);
            profileImage=itemView.findViewById(R.id.request_profile_image);
            acceptBtn=itemView.findViewById(R.id.request_accept_btn);
            cancelBtn=itemView.findViewById(R.id.request_cancel_btn);

        }

        public void setUserName(String userName) {
            TextView userNameDisplay=(TextView)mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(userName);
        }

        public void setThumb_user_image(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image=(CircleImageView)mView.findViewById(R.id.request_profile_image);
            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumbImage).into(thumb_image);
                }
            });
        }

        public void setUser_Status(String user_status) {
            TextView status=(TextView)mView.findViewById(R.id.request_profile_status);
            status.setText(user_status);
        }
    }

}
/*public class RequestsFragment extends Fragment {
    private View RequestFragmenView;
    private RecyclerView myRequestList;
    private DatabaseReference ChatRequestRef,UserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public RequestsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        RequestFragmenView=inflater.inflate(R.layout.fragment_requests, container, false);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        myRequestList=(RecyclerView)RequestFragmenView.findViewById(R.id.chat_requests_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        return RequestFragmenView;

    }

    @Override
    public void onStart() {



        super.onStart();
        FirebaseRecyclerAdapter<Requests,RequestsViewHolder> adapter =new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(Requests.class,
                R.layout.friend_request_all_users_layout,
                RequestsFragment.RequestsViewHolder.class,
                ChatRequestRef) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Requests model, int position) {
               //viewHolder.itemView.findViewById(R.id.request_accept_btn)
                final String list_user_id=getRef(position).getKey();
                final DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type=dataSnapshot.getValue().toString();
                            if(type.equals("received")){
                                UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("user_image")){

                                            final String requestUserName=dataSnapshot.child("user_name").getValue().toString();

                                            final String requestUserStatus=dataSnapshot.child("user_status").getValue().toString();
                                            final String requestUserImage=dataSnapshot.child("user_thumb_image").getValue().toString();

                                            viewHolder.userName.setText(requestUserName);
                                            viewHolder.userStatus.setText(requestUserStatus);
                                            Picasso.with(getContext()).load(requestUserImage).into(viewHolder.profileImage);



                                        }
                                        else {
                                            final String requestUserName=dataSnapshot.child("user_name").getValue().toString();

                                            final String requestUserStatus=dataSnapshot.child("user_status").getValue().toString();
                                            viewHolder.userName.setText(requestUserName);
                                            viewHolder.userStatus.setText(requestUserStatus);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        };

        myRequestList.setAdapter(adapter);
        adapter.startListening();

    }
    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView profileImage;
        Button acceptBtn,cancelBtn;
        public RequestsViewHolder(View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.request_profile_name);
            userStatus=itemView.findViewById(R.id.request_profile_status);
            profileImage=itemView.findViewById(R.id.request_profile_image);
            acceptBtn=itemView.findViewById(R.id.request_accept_btn);
            cancelBtn=itemView.findViewById(R.id.request_cancel_btn);

        }


    }

}
*/