package com.example.lakshya.readthefeed;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText et_phoneLogin, et_passwordLogin;
    private Button bv_login, bv_register;
    private TextView countdownTimerText;
    private String phoneLogin, passwordLogin;
    private DatabaseReference userDatabase;
    private String retrievedPassword;
    private String checkMobileNumber;
    private String key;
    private static int count = 0;
    private static int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_phoneLogin = findViewById(R.id.et_phoneLogin);
        et_passwordLogin = findViewById(R.id.et_passwordLogin);
        bv_login = findViewById(R.id.bv_login);
        bv_register = findViewById(R.id.bv_register);
        countdownTimerText = findViewById(R.id.tv_countdownTimer);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        if(flag==0) {

            bv_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    phoneLogin = et_phoneLogin.getText().toString();
                    passwordLogin = et_passwordLogin.getText().toString();

                    final DatabaseReference userReference = userDatabase.child(phoneLogin).child("mobileNumber");
                    //key = userReference.getKey();

                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkMobileNumber = dataSnapshot.getValue(String.class);
                            validateMobileNumber();
                            Log.d("FirebaseCheck", "onDataChange: " + checkMobileNumber + " " + userReference);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });
        }
        else{
            bv_login.setEnabled(false);
        }

        bv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class );
                startActivity(intent);
            }
        });

    }

    public void validateMobileNumber(){
        if(checkMobileNumber == null){
            Log.d("FirebaseCheck", "onDataChange: " + "nooooo");
            et_phoneLogin.setError("Please enter a valid mobile number");
            et_phoneLogin.requestFocus();
            count++;
            if(count==3){
                loginErrorTimer();
            }

        }
        else{
            DatabaseReference userPasswordReference = userDatabase.child(phoneLogin).child("password");

            userPasswordReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    retrievedPassword = dataSnapshot.getValue(String.class);
                    validatePassword();
                    Log.d("FirebaseCheck", "onDataChange: " + retrievedPassword);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void validatePassword() {

        if(!passwordLogin.equals(retrievedPassword)){
            et_passwordLogin.setError("Incorrect Password");
            et_passwordLogin.requestFocus();
            count++;
            if(count==3){
                loginErrorTimer();
            }
        }
        else{
            Intent intent = new Intent(LoginActivity.this, FeedActivity.class );
            startActivity(intent);
            finish();
        }

    }

    void loginErrorTimer(){
        flag=1;
        CountDownTimer  countDownTimer = new CountDownTimer(5*60*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("Blocked for %02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                countdownTimerText.setText(hms);//set text
            }
            public void onFinish() {
                count=0;
                flag=0;
                //countdownTimerText.setText("TIME'S UP!!"); //On finish change timer text
            }
        }.start();
    }
}
