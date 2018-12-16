package com.example.tann.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference usersReference;
    private TextView tVForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //GetInstane FireBase
        mAuth=FirebaseAuth.getInstance();
        //
        usersReference= FirebaseDatabase.getInstance().getReference().child("Users");
        //set tirtle của activity_login.xml
        mtoolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginButton=(Button)findViewById(R.id.login_button);
        loginEmail=(EditText)findViewById(R.id.login_email);
        loginPassword=(EditText)findViewById(R.id.login_password);
        tVForgotPassword=(TextView)findViewById(R.id.tv_forgot_password);
        loadingBar=new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=loginEmail.getText().toString();
                String password=loginPassword.getText().toString();
                LoginUserAccount(email,password);
            }
        });
        tVForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentForgotPassword=new Intent(LoginActivity.this,forgot_password.class);
                startActivity(intentForgotPassword);
            }
        });

    }


    private void LoginUserAccount(String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"Please Enter Your Email",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Please Enter Your Password",Toast.LENGTH_SHORT).show();
        }
        else {
            //Set Loadingbar
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please Wait!");
            loadingBar.show();
            //Xác thực User để đăng nhập
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        //lay id cua user
                        String online_user_id=mAuth.getCurrentUser().getUid();
                        // lay token
                        String Device_Token= FirebaseInstanceId.getInstance().getToken();
                        usersReference.child(online_user_id).child("device_token").setValue(Device_Token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                });

                    }
                    else
                    {
                     Toast.makeText(LoginActivity.this,"Error Email or Password !",Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }
}
