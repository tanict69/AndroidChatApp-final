package com.example.tann.androidchatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverId;
    private String messageReceiverName;
//
private DatabaseReference UsersReference;
    //
    private Toolbar chatToolBar;
    private TextView userNameTitle;
    private TextView userLastSeen;
    private CircleImageView userChatProfileImage;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String messageSenderId;


    private ImageButton sendMessageButton;
    private ImageButton selectImageButton;
    private TextView inputMessageText;

    private RecyclerView userMessagesList;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private static int Galarry_pick=1;
    private StorageReference messageImageStorageRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        messageSenderId=mAuth.getCurrentUser().getUid();

        //lay thong tin tu friend fragment gui qua line 111
        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("user_name").toString();
        messageImageStorageRef=FirebaseStorage.getInstance().getReference().child("Messages_Pictures");
        //chen chatbarlayout
        chatToolBar=(Toolbar)findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);
        loadingBar=new ProgressDialog(this);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);
        //

        userNameTitle=(TextView)findViewById(R.id.custom_profile_name);
        userLastSeen=(TextView)findViewById(R.id.custom_user_last_seen);
        userChatProfileImage=(CircleImageView)findViewById(R.id.custom_profile_image);
        sendMessageButton=(ImageButton)findViewById(R.id.send_message_btn);
        selectImageButton=(ImageButton)findViewById(R.id.select_image_btn);
        inputMessageText=(TextView)findViewById(R.id.input_message);

        messageAdapter=new MessageAdapter(messagesList);
        userMessagesList=(RecyclerView)findViewById(R.id.messages_list_of_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        FetchMessages();


        //

        userNameTitle.setText(messageReceiverName);

        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online=dataSnapshot.child("online").getValue().toString();
                final String userThumb=dataSnapshot.child("user_thumb_image").getValue().toString();


                Picasso.with(ChatActivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).into(userChatProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ChatActivity.this).load(userThumb).into(userChatProfileImage);
                    }
                });
                //set last seen
                if(online.equals("true")){
                    userLastSeen.setText("online");
                }
                else {


                  Thread  thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(5000);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LastSeenTime getTime=new LastSeenTime();
                                            long last_seen=Long.parseLong(online);
                                            String lastSeenDisplayTime=getTime.getTimeAgo(last_seen,getApplicationContext()).toString();
                                            userLastSeen.setText(lastSeenDisplayTime);
                                        }
                                    });

                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    thread.start();





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galarryIntent=new Intent();
                galarryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galarryIntent.setType("image/*");
                startActivityForResult(galarryIntent,Galarry_pick);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Galarry_pick&&resultCode==RESULT_OK&&data!=null)
        {
            loadingBar.setTitle("Sending Image");
            loadingBar.setMessage("Please Wait!");
            loadingBar.show();
            Uri ImageUri=data.getData();
            final String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
            final String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;
            DatabaseReference user_message_key=rootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();
            //lay random key
            final String message_push_id=user_message_key.getKey();

            StorageReference filePath=messageImageStorageRef.child(message_push_id+".jpg");
            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        final String dowloadUrl=task.getResult().getDownloadUrl().toString();
                        Map messageTextBody=new HashMap();
                        messageTextBody.put("from",messageSenderId);
                        messageTextBody.put("message",dowloadUrl);
                        messageTextBody.put("type","image");
                        Map messageBodydetails=new HashMap();
                        messageBodydetails.put(messageSenderRef+"/"+message_push_id,messageTextBody);
                        messageBodydetails.put(messageReceiverRef+"/"+message_push_id,messageTextBody);
                        rootRef.updateChildren(messageBodydetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Log.d("chat_log",databaseError.getMessage().toString());
                                }
                                inputMessageText.setText("");
                                loadingBar.dismiss();
                            }
                        });
                        loadingBar.dismiss();
                    }
                    else {
                        Toast.makeText(ChatActivity.this,"Image not sent, please try again",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });

        }

    }



    private void FetchMessages() {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //datasnapshot lay tin nhan roi dua vao messagesList
                        //messageList hien thi len recycle view
                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {
        String messageText=inputMessageText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this,"Please write your message",Toast.LENGTH_LONG).show();
        }
        else
        {
            String messageSenderRef="Messages/"+messageSenderId+"/"+messageReceiverId;
            String messageReceiverRef="Messages/"+messageReceiverId+"/"+messageSenderId;
            DatabaseReference user_message_key=rootRef.child("Messages").child(messageSenderId)
                    .child(messageReceiverId).push();
            //lay random key
            String message_push_id=user_message_key.getKey();
            Map messageTextBody=new HashMap();
            messageTextBody.put("from",messageSenderId);
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            Map messageBodydetails=new HashMap();
            messageBodydetails.put(messageSenderRef+"/"+message_push_id,messageTextBody);
            messageBodydetails.put(messageReceiverRef+"/"+message_push_id,messageTextBody);
            rootRef.updateChildren(messageBodydetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null)
                    {

                    }
                    inputMessageText.setText("");
                }
            });
        }
    }
}
