package com.example.tann.androidchatapp;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private Button sendFriendRequestButton;
    private Button declineFriendRequestButton;
    private TextView profileName;
    private TextView profileStatus;
    private ImageView profileImage;
    private DatabaseReference UserReference;


    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;
    private DatabaseReference FriendReference;
    private DatabaseReference NotificationsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // Tạo 1 node mới có tên Friend_Requets để luu trạng thái có kết bạn hay chưa
        FriendRequestReference=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);
        mAuth=FirebaseAuth.getInstance();

        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id=getIntent().getExtras().get("visit_user_id").toString();




        sender_user_id=mAuth.getCurrentUser().getUid();
        FriendReference=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendReference.keepSynced(true);

        NotificationsReference=FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);


        sendFriendRequestButton = (Button) findViewById(R.id.profile_visit_friend_req_btn);
        declineFriendRequestButton = (Button) findViewById(R.id.profile_decline_friend_req_btn);
        profileName = (TextView) findViewById(R.id.profile_visit_user_name);
        profileStatus = (TextView) findViewById(R.id.profile_visit_user_status);
        profileImage = (ImageView) findViewById(R.id.profile_visit_user_image);


        CURRENT_STATE="not_friends";

        UserReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
          String name=dataSnapshot.child("user_name").getValue().toString();
                String status=dataSnapshot.child("user_status").getValue().toString();
                String image=dataSnapshot.child("user_image").getValue().toString();
                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(profileImage);
                FriendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.hasChild(receiver_user_id)){
                                        String req_type=dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                                        if(req_type.equals("sent")){
                                            CURRENT_STATE="request_sent";
                                            sendFriendRequestButton.setText("cancel friend request");
                                            declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                            declineFriendRequestButton.setEnabled(false);
                                        }
                                        else if(req_type.equals("received")) {
                                            CURRENT_STATE="request_received";
                                            sendFriendRequestButton.setText("Accept friend request");
                                            declineFriendRequestButton.setVisibility(View.VISIBLE);
                                            declineFriendRequestButton.setEnabled(true);

                                            declineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    DeClineFriendRequest();
                                                }
                                            });
                                        }
                                    }

                                }
                                else {
                                    FriendReference.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(receiver_user_id)){
                                                        CURRENT_STATE="friends";
                                                        sendFriendRequestButton.setText("Unfriend this person");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        declineFriendRequestButton.setVisibility(View.INVISIBLE);
        declineFriendRequestButton.setEnabled(false);
        // tranh cho nguoi dung tu add friend minh
        if(!sender_user_id.equals(receiver_user_id))
        {
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequestButton.setEnabled(false);
                    if(CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToAperson();
                    }
                    if(CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        UnFriendaFriend();
                    }
                }
            });
        }else {
            declineFriendRequestButton.setVisibility(View.INVISIBLE);
            sendFriendRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void AcceptFriendRequest() {
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate=currentDate.format(calForDate.getTime());
        FriendReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){

                                                                        sendFriendRequestButton.setEnabled(true);
                                                                        CURRENT_STATE="friends";
                                                                        sendFriendRequestButton.setText("Unfriend this person");
                                                                        declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                                        declineFriendRequestButton.setEnabled(false);
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

    private void UnFriendaFriend() {
        FriendReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                sendFriendRequestButton.setText("Send Friend Request");
                                                declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void DeClineFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        sendFriendRequestButton.setEnabled(true);
                                        CURRENT_STATE="not_friends";
                                        sendFriendRequestButton.setText("Send Friend Request");
                                        declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        declineFriendRequestButton.setEnabled(false);
                                    }
                                }
                            });
                        }
                    }
                });
    }



    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        sendFriendRequestButton.setEnabled(true);
                                        CURRENT_STATE="not_friends";
                                        sendFriendRequestButton.setText("Send Friend Request");
                                        declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                        declineFriendRequestButton.setEnabled(false);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void SendFriendRequestToAperson() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            HashMap<String,String>notificationData=new HashMap<String, String>();
                                            notificationData.put("From",sender_user_id);
                                            notificationData.put("Type","request");

                                           NotificationsReference.child(receiver_user_id).push().setValue(notificationData)
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if(task.isSuccessful()){
                                                               sendFriendRequestButton.setEnabled(true);
                                                               CURRENT_STATE="request_sent";
                                                               sendFriendRequestButton.setText("Cancel friend request");
                                                               declineFriendRequestButton.setVisibility(View.INVISIBLE);
                                                               declineFriendRequestButton.setEnabled(false);
                                                           }
                                                       }
                                                   });


                                        }
                                    }
                                });
                    }
                    }
                });
    }
}
