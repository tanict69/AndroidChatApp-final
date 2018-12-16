package com.example.tann.androidchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {
    private Button NeedNewAccountButton;
    private Button AlreadyHaveAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        NeedNewAccountButton=findViewById(R.id.Need_new_account);
        AlreadyHaveAccountButton=findViewById(R.id.Already_have_an_account);
        NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent= new Intent(StartPageActivity.this,Register_activity.class);
                startActivity(registerIntent);
            }
        });

        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent=new Intent(StartPageActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
