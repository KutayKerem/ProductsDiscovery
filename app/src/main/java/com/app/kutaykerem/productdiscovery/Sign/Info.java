package com.app.kutaykerem.productdiscovery.Sign;


import static com.app.kutaykerem.productdiscovery.Sign.UserLogin.clearApplicationCache;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background;
import com.app.kutaykerem.productdiscovery.Pages.AnaSayfa;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.ActivityInfoBinding;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Info extends AppCompatActivity {


    ActivityInfoBinding binding;
    CountDownTimer countDownTimer;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);







        try {
        SharedPreferences themesSharedPrefences = getSharedPreferences("MODE", Context.MODE_PRIVATE);

        SharedPreferences dilSharedPrefences = getSharedPreferences("Dil", Context.MODE_PRIVATE);
        String dil = dilSharedPrefences.getString("dil","ingilizce");






            LocalTime aksamStart = LocalTime.of(18, 0);
            LocalTime aksamEnd = LocalTime.of(23, 0);

            LocalTime geceStart = LocalTime.of(23, 0);
            LocalTime geceEnd = LocalTime.of(5, 0);

            LocalTime sabahStart = LocalTime.of(5, 0);
            LocalTime sabahEnd = LocalTime.of(10, 0);

            LocalTime gunStart = LocalTime.of(10, 0);
            LocalTime gunEnd = LocalTime.of(18, 0);

            LocalTime currentTime = LocalDateTime.now().toLocalTime();



        if(dil.equals("türkce")){
            if (currentTime.isAfter(aksamStart) && currentTime.isBefore(aksamEnd)) {
                binding.stateInfoText.setText("İyi Akşamlar");

            } else if ((currentTime.isAfter(geceStart) && currentTime.isBefore(LocalTime.MAX)) ||
                    (currentTime.isAfter(LocalTime.MIN) && currentTime.isBefore(geceEnd))) {
                binding.stateInfoText.setText("İyi Geceler");
            } else if (currentTime.isAfter(sabahStart) && currentTime.isBefore(sabahEnd)) {
                binding.stateInfoText.setText("Günaydın");
            } else if (currentTime.isAfter(gunStart) && currentTime.isBefore(gunEnd)) {
                binding.stateInfoText.setText("İyi Günler");
            }

        } else{
            if (currentTime.isAfter(aksamStart) && currentTime.isBefore(aksamEnd)) {
                binding.stateInfoText.setText("Good Evening");

            } else if ((currentTime.isAfter(geceStart) && currentTime.isBefore(LocalTime.MAX)) ||
                    (currentTime.isAfter(LocalTime.MIN) && currentTime.isBefore(geceEnd))) {
                binding.stateInfoText.setText("Good Night");
            } else if (currentTime.isAfter(sabahStart) && currentTime.isBefore(sabahEnd)) {
                binding.stateInfoText.setText("Good Morning");
            } else if (currentTime.isAfter(gunStart) && currentTime.isBefore(gunEnd)) {
                binding.stateInfoText.setText("Good Day");
            }
        }

            Animation fadeInAnimation = new AlphaAnimation(0, 1);
            fadeInAnimation.setDuration(2000); // Duration in milliseconds (2 seconds in this example)

            // Set a listener to handle animation end
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Animation started
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Animation ended
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Animation repeated
                }
            });

            // Start the animation on the TextView
            binding.stateInfoText.startAnimation(fadeInAnimation);




        Boolean nightMode = themesSharedPrefences.getBoolean("night",false);



        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), true, false); // durum çubuğunu karanlık moda ayarlar
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), false, false); // durum çubuğunu karanlık moda ayarlar

        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }







        countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {


                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    for (UserInfo profile : firebaseUser.getProviderData()) {
                        String providerId = profile.getProviderId();
                        if (providerId.equals("google.com")) {
                            // Kullanıcı Google hesabı ile giriş yapmıştır
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            if (acct != null && acct.getIdToken() != null) {
                                // Google hesabı ile oturum açmış kullanıcılara özgü işlemler

                                Intent intent = new Intent(Info.this, AnaSayfa.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                            break;
                        } else if (providerId.equals("facebook.com")) {
                            // Kullanıcı Facebook hesabı ile giriş yapmıştır
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                            if (isLoggedIn) {
                                // Facebook hesabı ile oturum açmış kullanıcılara özgü işlemler

                                Intent intent = new Intent(Info.this, AnaSayfa.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                            break;
                        }else if (providerId.equals("password")) {
                            // Kullanıcı email ve şifre ile giriş yapmıştır

                            Intent intent = new Intent(Info.this, AnaSayfa.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                        }
                    }
                } else {
                    // Kullanıcı oturum açmamıştır

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    GoogleSignInClient googleSignInClient;
                    googleSignInClient = GoogleSignIn.getClient(getApplicationContext(),gso);

                    googleSignInClient.signOut();
                    LoginManager.getInstance().logOut();
                    AccessToken.setCurrentAccessToken(null);
                    Profile.setCurrentProfile(null);
                    clearApplicationCache(getApplicationContext());
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Info.this,UserLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        }.start();



    }






}
