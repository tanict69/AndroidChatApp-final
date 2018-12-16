package com.example.tann.androidchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayName;
    private TextView settingsDisplayStatus;
    private Button settingsChangeProfileImageButton;
    private Button settingChangeStatusButton;
    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;
    private final static int Gallery_Pick=1;
    private StorageReference storeProfileImageRef;
    private StorageReference thumbImageRef;
    private ProgressDialog loadingBar;
    private Toolbar mtoolbar;
    Bitmap thumb_bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //
        mtoolbar=(Toolbar) findViewById(R.id.profile_users_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mAuth=FirebaseAuth.getInstance();
        String online_user_id=mAuth.getCurrentUser().getUid();
        //Lấy theo UID của User
        getUserDataReference= FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getUserDataReference.keepSynced(true);
        //Tao folder trong Storerage co ten Profile_Image de luu anh cua user
        storeProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile_Images");
        thumbImageRef=FirebaseStorage.getInstance().getReference().child("Thumb_Images");
        settingsDisplayProfileImage=(CircleImageView)findViewById(R.id.settings_profile_image);
        settingsDisplayName=(TextView)findViewById(R.id.settings_username);
        settingsDisplayStatus=(TextView)findViewById(R.id.settings_user_status);
        settingsChangeProfileImageButton=(Button)findViewById(R.id.settings_change_profile_image_button);
        settingChangeStatusButton=(Button)findViewById(R.id.settings_change_profile_status);
        loadingBar =new ProgressDialog(this);

        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Lấy user_name từ DBS truyền vào name
                String name=dataSnapshot.child("user_name").getValue().toString();
                //Lấy user_status
                String status=dataSnapshot.child("user_status").getValue().toString();
                final String image=dataSnapshot.child("user_image").getValue().toString();
                String thumb_image=dataSnapshot.child("user_thumb_image").getValue().toString();

                //Set name trong Activity_setting.xml
                settingsDisplayName.setText(name);
                //set status trong Activity_setting.xml
                settingsDisplayStatus.setText(status);
                //set ảnh trong Activity_xml sử dụng thư viện Picaso
                /*do ảnh người dùng tải lên mình phải lưu trên firebase, muốn hiển thị phải
                * tải ảnh của người dùng từ firebase về mới hiển thị lên được, phải đợi nó tải về
                * xong mới hiển thị lên imageVierw được=> app dễ bị đơ nên xài thư viện Picaso khắc
                * phục được việc app bị đơ
                * */
                if(!image.equals("default_profile"))

                {

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile).into(settingsDisplayProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            //offline
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_profile).into(settingsDisplayProfileImage);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingsChangeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });
        settingChangeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_status=settingsDisplayStatus.getText().toString();
                Intent statusIntent=new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("user_status",old_status);
                startActivity(statusIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Sử dụng Android-Image-Cropper trên github
        //Link: https://github.com/ArthurHub/Android-Image-Cropper
        if(requestCode==Gallery_Pick&&resultCode==RESULT_OK&&data!=null)
        {
            Uri ImageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Updating Profile Image");
                loadingBar.setMessage("Please wait!");
                loadingBar.show();
                Uri resultUri = result.getUri();
                //user select anh tu galerry trong dien thoai

                String user_id=mAuth.getCurrentUser().getUid();
                // lay duong dan cua anh duoc nguoi dung chon
                File thumb_filePathUri=new File(resultUri.getPath());
                // sử dụng thư viện compressor giảm độ phân giải của ảnh
                // link: https://github.com/zetbaitsu/Compressor
                try {
                    thumb_bitmap=new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] thumb_byte=byteArrayOutputStream.toByteArray();



                //ten anh duoc luu co dang id.jpg
                StorageReference filePath=storeProfileImageRef.child(user_id+".jpg");
                final StorageReference thumb_filePath=thumbImageRef.child(user_id+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this,"Saving your Profile",Toast.LENGTH_LONG).show();
                            final String dowloadUrl=task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask=thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                String thumb_downloadUri=thumb_task.getResult().getDownloadUrl().toString();
                                if(task.isSuccessful()){
                                    Map update_user_data=new HashMap();
                                    update_user_data.put("user_image",dowloadUrl);
                                    update_user_data.put("user_thumb_image",thumb_downloadUri);
                                    // //set link của ảnh được lưu vào trong user_image
                                    getUserDataReference.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(SettingsActivity.this,"Profile Image Updated Succesfully",Toast.LENGTH_LONG).show();
                                            loadingBar.dismiss();
                                        }
                                    });
                                }
                                }
                            });



                        }
                        else
                            {
                                Toast.makeText(SettingsActivity.this,"Error, Please try again!",Toast.LENGTH_LONG).show();
                            }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                loadingBar.dismiss();
            }
        }
    }
}
