package com.app.kutaykerem.productdiscovery.Sign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        try {
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("night",false);

        if(nightMode){
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), true, false); // durum çubuğunu karanlık moda ayarlar
        }else{
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), false, false); // durum çubuğunu karanlık moda ayarlar
        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }





        auth = FirebaseAuth.getInstance();


        binding.forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });

        binding.backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, UserLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
            }
        });
        binding.backToLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this, UserLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
            }
        });










    }

    private void ResetPassword() {
     try {
        String email = binding.mail.getText().toString();
        if(email.isEmpty()){
            binding.mail.setError("Email required");
        }else{
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Intent intent = new Intent(ForgetPassword.this, UserLogin.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(ForgetPassword.this,"Check your email",Toast.LENGTH_LONG).show();
                        overridePendingTransition(R.anim.slide_left, R.anim.slide_left);
                        finish();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }

    }



}



