package com.example.tann.androidchatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    private DatabaseReference UsersReference,RootRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            String online_user_id=mAuth.getCurrentUser().getUid();
            UsersReference= FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(online_user_id);
        }


        //Tab cho MainActivity bao gồm friend,request,chat
        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter =new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout=(TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);




        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("appChat");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            LogOutUser();
        }
        //set trang thai user khi login
        else if (currentUser!=null)
        {
            //UsersReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UsersReference.child("online").setValue("true");
    }


    @Override
    protected void onStop() {
        //Khi user minimize doi trang thai online: false
        super.onStop();
        if (currentUser!=null)
        {
           // UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOutUser() {
        Intent startPageIntent= new Intent(MainActivity.this,StartPageActivity.class);
        //bắt buộc phải xác thực mới chuyển qua startPageActivity
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }
    //tạo opption logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         //Kiểm tra xem thằng logout hay Accountsetting được click bằng ID
        if(item.getItemId()==R.id.main_logout_button){
            if(currentUser!=null){
                //
                UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            //Nếu thằng signOut được click thì logout user
            mAuth.signOut();
            LogOutUser();
        }
        //Nếu thằng account setting được click thì chuyển nó sang SettingActivity
        if(item.getItemId()==R.id.main_account_setting_button){

            Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingIntent);
        }
        // Nếu thằng allusers được click thì chuyển sang allUserActivity
        if(item.getItemId()==R.id.main_all_users_button){

            Intent allUsersIntent=new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(allUsersIntent);
        }
        // group option duoc click
        if(item.getItemId()==R.id.main_create_group_option){
            RequestNewGroup();
        }
        return true;
    }
    private void RequestNewGroup(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name:");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("Uit");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String GroupName=groupNameField.getText().toString();
                if(TextUtils.isEmpty(GroupName))
                {
                    Toast.makeText(MainActivity.this,"Please Enter Group Name!",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    CreateNewGroup(GroupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.show();
    }

    private void CreateNewGroup(String GroupName) {
        RootRef=FirebaseDatabase.getInstance().getReference();
        RootRef.child("Groups").child(GroupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()
                        ) {
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
