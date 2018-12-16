package com.example.tann.androidchatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private EditText RegisterUserName;
    private EditText RegisterEmail;
    private EditText RegisterPassWord;
    private EditText RegisterConfirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;
    private DatabaseReference storeUserDefaulDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        mtoolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        mAuth=FirebaseAuth.getInstance();
        //

        RegisterUserName=(EditText)findViewById(R.id.register_name);
        RegisterEmail=(EditText) findViewById(R.id.register_email);
        RegisterPassWord=(EditText)findViewById(R.id.register_password);
        RegisterConfirmPassword=(EditText)findViewById(R.id.register_confirm_password);
        CreateAccountButton=(Button)findViewById(R.id.create_account_button);

        loadingBar=new ProgressDialog(this);
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String name=RegisterUserName.getText().toString();
                String email=RegisterEmail.getText().toString();
                String password=RegisterPassWord.getText().toString();
                String ConfirmPassword=RegisterConfirmPassword.getText().toString();
                RegisterAccount(name,email,password,ConfirmPassword);

            }

            private void RegisterAccount(final String name, String email, String password,String ConfirmPassword) {
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(Register_activity.this,
                            "Please enter your Name!", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(Register_activity.this,
                            "Please enter your Email!", Toast.LENGTH_LONG).show();
                }
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(Register_activity.this,
                            "Please enter your Password!", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(ConfirmPassword)){
                    Toast.makeText(Register_activity.this,
                            "Please Confirm your Password!", Toast.LENGTH_LONG).show();
                }
                if(!password.equals(ConfirmPassword))
                {
                    Toast.makeText(Register_activity.this,
                            "Password doesn't match, Please try again!", Toast.LENGTH_LONG).show();
                }
                else {
                    loadingBar.setTitle("Creating new account");
                    loadingBar.setMessage("Please wait!");
                    loadingBar.show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String Device_Token= FirebaseInstanceId.getInstance().getToken();
                                 //Lấy uid từ authencaiton firebase
                                String current_user_Id=mAuth.getCurrentUser().getUid();
                                //Tạo thông tin user trong Dbs
                                /*
                                *
                                * dbs_Name---users---current_user_Id1 ---UserName: ....
                                *                                    ---UserStatus: ....
                                *                                    ---user_image: ....
                                *                                    ---user_thumb_image:....
                                *
                                *                  ---curent_userId2  ----UserName: ....
                                *                                       ...............
                                * */
                                storeUserDefaulDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(current_user_Id);
                                storeUserDefaulDatabaseReference.child("user_name").setValue(name);
                                storeUserDefaulDatabaseReference.child("user_status").setValue("None Status");
                                storeUserDefaulDatabaseReference.child("user_image").setValue("https://firebasestorage.googleapis.com/v0/" +
                                        "b/androidchatapp-f5e30.appspot.com/o/Profile_Images%2Fdefault_profile.jpg?alt=media&token=f8d00685-" +
                                        "7324-4495-8e9a-1215d3472ee2");
                                storeUserDefaulDatabaseReference.child("device_token").setValue(Device_Token);
                                storeUserDefaulDatabaseReference.child("user_thumb_image").setValue("https://firebasestorage.googleapis.com/v0/" +
                                        "b/androidchatapp-f5e30.appspot.com/o/Profile_Images%2Fdefault_profile.jpg?alt=media&token=f8d00685-" +
                                        "7324-4495-8e9a-1215d3472ee2")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //Nếu Hoàn thành thì chuyển sang MainActivity
                                                    Intent mainIntent=new Intent(Register_activity.this,MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(Register_activity.this,
                                        "Error, Please try again!", Toast.LENGTH_LONG).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
                }
            }
        });
    }
}
