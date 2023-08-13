package com.app.kutaykerem.productdiscovery.Sign;

import static com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background.lightStatusBar;
import static com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background.setFullScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.kutaykerem.productdiscovery.Models.GetDate;
import com.app.kutaykerem.productdiscovery.Pages.AnaSayfa;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.ActivityKayitBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Kayıt extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView kullanıcıAdi;
    public ActivityKayitBinding binding;

    DatabaseReference databaseReference = null;
    FirebaseDatabase database=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKayitBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();

        kullanıcıAdi = findViewById(R.id.kullaniciadi);

        database = FirebaseDatabase.getInstance();



        try {

        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("night",false);

        if(nightMode){
            setFullScreen(getWindow());
            lightStatusBar(getWindow(), true, false); // durum çubuğunu karanlık moda ayarlar
        }else{
            setFullScreen(getWindow());
            lightStatusBar(getWindow(), false, false); // durum çubuğunu karanlık moda ayarlar
        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }


    }




    public void kaydol(View view) {


        String email = binding.mail.getText().toString();

        String password = binding.sifre.getText().toString();

        String kullanıcıAdı = binding.kullaniciadi.getText().toString();

        if (TextUtils.isEmpty(email)) {

            binding.mail.setError("Email");
        } else if (TextUtils.isEmpty(password)) {
            binding.sifre.setError("Password");

        } else {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    databaseReference = database.getReference().child("Profile");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    databaseReference.child(user.getUid()).child("kullanıcıAdı").setValue(kullanıcıAdı);

                    HashMap<String,Object> data = new HashMap<>();
                    data.put("kullanıcıAdı",kullanıcıAdı);
                    data.put("tarih", GetDate.getDate());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(user.getUid());
                    reference.updateChildren(data);

                    Intent intent = new Intent(Kayıt.this, AnaSayfa.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);







                }


            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }


        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Kayıt.this, UserLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left, R.anim.slide_left);


    }
}