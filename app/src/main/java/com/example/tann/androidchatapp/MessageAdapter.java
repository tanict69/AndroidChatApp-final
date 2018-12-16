package com.example.tann.androidchatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tann on 11/21/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference userDatabaseRef;
    private Context context;
    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList=userMessagesList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        //  public TextView messageText;
        public TextView senderMessageText,receiverMessageText;
        // public CircleImageView userProfileImage;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderImg;
        public ImageView messageReceiverImg;

        public MessageViewHolder(View itemView)
        {
            super(itemView);
            //  messageText=(TextView)view.findViewById(R.id.sender_message_text);
            //userProfileImage= (CircleImageView) view.findViewById(R.id.message_profile_image);
            senderMessageText=(TextView)itemView.findViewById(R.id.sender_message_text);
            receiverMessageText=(TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_image);

            messageSenderImg=(ImageView)itemView.findViewById(R.id.message_sender_imgView);
            messageReceiverImg=(ImageView)itemView.findViewById(R.id.message_receiver_imgView);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout_of_users,parent,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder,  int position) {

        //holder.messageText.setText(messages.getMessage());
        String messageSenderId=mAuth.getCurrentUser().getUid();
         Messages messages=userMessagesList.get(position);
         String fromUserId=messages.getFrom();
         String fromMessagesType=messages.getType();

         //usersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        usersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
         usersRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("user_thumb_image"))
                {
                    String receiverImage=dataSnapshot.child("user_thumb_image").getValue().toString();
                    Picasso.with(context).load(receiverImage).into(holder.receiverProfileImage);
                }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
         if(fromMessagesType.equals("text"))
         {
             holder.receiverMessageText.setVisibility(View.INVISIBLE);
             holder.receiverProfileImage.setVisibility(View.INVISIBLE);
             if(fromUserId.equals(messageSenderId))
             //if(fromUserId==messageSenderId)
             {
                 holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                 holder.senderMessageText.setText(messages.getMessage());
             }
             else
             {
                 holder.senderMessageText.setVisibility(View.INVISIBLE);
                 holder.receiverMessageText.setVisibility(View.VISIBLE);
                 holder.receiverProfileImage.setVisibility(View.VISIBLE);

                 holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
                 holder.receiverMessageText.setText(messages.getMessage());
             }
         }
         else {
             if(fromUserId.equals(messageSenderId))
             {
                 holder.senderMessageText.setVisibility(View.GONE);
                 holder.receiverMessageText.setVisibility(View.GONE);
                 holder.receiverProfileImage.setVisibility(View.GONE);
                 holder.messageSenderImg.setVisibility(View.VISIBLE);
                 Picasso.with(context).load(messages.getMessage()).into(holder.messageSenderImg);
             }
             else {
                 holder.senderMessageText.setVisibility(View.GONE);
                 holder.receiverMessageText.setVisibility(View.GONE);
                 holder.receiverProfileImage.setVisibility(View.GONE);
                 holder.messageReceiverImg.setVisibility(View.VISIBLE);
                 Picasso.with(context).load(messages.getMessage()).into(holder.messageReceiverImg);
             }
         }



    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }




}
