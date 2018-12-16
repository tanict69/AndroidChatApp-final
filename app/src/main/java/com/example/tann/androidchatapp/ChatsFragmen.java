package com.example.tann.androidchatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragmen extends Fragment {
private View groupFragmenView;
private ListView list_View;
private ArrayAdapter<String>arrayAdapter;
private ArrayList<String> list_of_groups=new ArrayList<>();
private DatabaseReference GroupRef;


    public ChatsFragmen() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmenView=inflater.inflate(R.layout.fragment_chats, container, false);
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");

        IntializeFields();
        RetrieveAndDisplayGroups();
        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //get name single item click
                String currentGroupName=adapterView.getItemAtPosition(position).toString();
                Intent groupChatIntent=new Intent(getContext(),GroupChatActivity.class);
                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmenView;
    }

    private void RetrieveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set=new HashSet<>();
                //read line by line (tat ca child)
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                list_View.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {
      list_View=(ListView)groupFragmenView.findViewById(R.id.list_view);
      arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
    }

}
