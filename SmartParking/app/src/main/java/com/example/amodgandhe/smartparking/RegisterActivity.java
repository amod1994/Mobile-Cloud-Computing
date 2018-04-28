package com.example.amodgandhe.smartparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextView regEmail;
    private TextView regPass;
    private Button register;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        regEmail = (TextView) findViewById(R.id.rEmail);
        regPass = (TextView) findViewById(R.id.rPassword);
        register = (Button) findViewById(R.id.resgister);
    }

    public void registerUser(View view){
        String rEmail = regEmail.getText().toString().trim();
        String rPassword = regPass.getText().toString().trim();

        if(TextUtils.isEmpty(rEmail)){
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }if(TextUtils.isEmpty(rPassword)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(rEmail,rPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }else
                            Toast.makeText(RegisterActivity.this, "Could Not Register, Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
