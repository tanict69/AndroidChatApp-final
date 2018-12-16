package com.example.tann.androidchatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText edtEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mToolbar=(Toolbar)findViewById(R.id.forgot_pass_bar_layout);
        edtEmail = (EditText) findViewById(R.id.edt_reset_email);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        loadingBar=new ProgressDialog(this);




        mAuth = FirebaseAuth.getInstance();
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingBar.setTitle("Sending Email to reset your password");
                loadingBar.setMessage("Please Wait!");
                loadingBar.show();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(forgot_password.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(forgot_password.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();

                    }

                });

            }
        });
    }
}
