package com.example.lakshya.readthefeed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lakshya.readthefeed.POJO.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_phoneSignUp, et_passwordSignUp, et_name, et_age, et_email, et_gender;
    private String phoneSignUp, passwordSignUp, name, age, email, gender;
    private Button bv_registerSignUp;
    private ImageView iv_profilePicture;
    private Bitmap bitmap;
    private Uri uri;
    private String checkMobileNumber;
    private int flag=0;
    public static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int CAMERA_REQUEST = 110;
    public static final int ACTIVITY_SELECT_IMAGE = 120;
    public static final int READ_EXTERNAL_STORAGE_CODE = 130;
    public static final int THUMBNAIL_SIZE = 230;
    private Context context = this;

    private DatabaseReference userDatabase;
    //private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        //mAuth = FirebaseAuth.getInstance();

        et_phoneSignUp = findViewById(R.id.et_phoneSignUp);
        et_passwordSignUp = findViewById(R.id.et_passwordSignUp);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_email = findViewById(R.id.et_email);
        bv_registerSignUp = findViewById(R.id.bv_registerSignUp);
        et_gender = findViewById(R.id.et_gender);
        iv_profilePicture = findViewById(R.id.iv_profilePicture);

        bv_registerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneSignUp = et_phoneSignUp.getText().toString().trim();
                passwordSignUp = et_passwordSignUp.getText().toString().trim();
                name = et_name.getText().toString().trim();
                age = et_age.getText().toString().trim();
                email = et_email.getText().toString().trim();
                gender = et_gender.getText().toString().trim();

                Boolean validation = validate(name, email, age, phoneSignUp, gender);
                //createUser(phoneSignUp,passwordSignUp);

                if (validation) {

                    userDatabase.child(phoneSignUp);
                    User user = new User(name, age, phoneSignUp, gender, email, passwordSignUp);
                    userDatabase.child(phoneSignUp).setValue(user);
                    Toast.makeText(SignUpActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class );
                    startActivity(intent);
                }

            }
        });

        iv_profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


    }


    public boolean validate(String name, String email, String age, String phoneSignUp, String gender) {
        flag=0;

        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return false;
        }

        if (phoneSignUp.isEmpty()) {
            et_phoneSignUp.setError("mobile number is required");
            et_phoneSignUp.requestFocus();
            return false;
        }

        if (name.isEmpty()) {
            et_name.setError("name is required");
            et_name.requestFocus();
            return false;
        }

        if (age.isEmpty()) {
            et_age.setError("age is requried");
            et_age.requestFocus();
            return false;
        }

        if (gender.isEmpty()) {
            et_gender.setError("gender is required");
            et_gender.requestFocus();
            return false;
        }

        if (passwordSignUp.isEmpty()) {
            et_passwordSignUp.setError("password is required");
            et_passwordSignUp.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please enter a valid email");
            et_email.requestFocus();
            return false;
        }

        if (passwordSignUp.length() < 6) {
            et_passwordSignUp.setError("Minimum length of the password should be 6");
            et_passwordSignUp.requestFocus();
            return false;
        }

        if (phoneSignUp.length() != 10) {
            et_phoneSignUp.setError("Please enter a valid mobile number");
            et_phoneSignUp.requestFocus();
            return false;
        }

        final DatabaseReference userReference = userDatabase.child(phoneSignUp).child("mobileNumber");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkMobileNumber = dataSnapshot.getValue(String.class);
                if(checkMobileNumber != null){
                    Log.d("FirebaseCheck", "onDataChange: " + "nooooo");
                    et_phoneSignUp.setError("An account with this number already exists");
                    et_phoneSignUp.requestFocus();
                    flag=1;
                }
                Log.d("FirebaseCheck", "onDataChange: " + checkMobileNumber + " " + userReference);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return flag == 0;
    }




    public void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options,new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int index) {

                if(options[index].equals("Take Photo"))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                }
                else if(options[index].equals("Choose from Gallery"))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    READ_EXTERNAL_STORAGE_CODE);
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_SELECT_IMAGE);
                        }
                    }

                }
                else if(options[index].equals("Cancel"))
                {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "reading external storage permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_SELECT_IMAGE);
            } else {
                Toast.makeText(this, "reading external storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            iv_profilePicture.setImageBitmap(bitmap);
        }
        else if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                bitmap = Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);


                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.iv_profilePicture);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
