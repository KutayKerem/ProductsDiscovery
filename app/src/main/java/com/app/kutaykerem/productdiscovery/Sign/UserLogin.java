package com.app.kutaykerem.productdiscovery.Sign;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background;
import com.app.kutaykerem.productdiscovery.Pages.AnaSayfa;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.ActivityUserLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.io.File;
import java.util.Arrays;

public class UserLogin extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseGmail;


   GoogleSignInOptions googleSignInOptions;
   GoogleSignInClient googleSignInClient;

   CallbackManager callbackManager;


    public ActivityUserLoginBinding binding;



    int state = 0;


    public FirebaseAuth mGoogleAuth,mFacebookAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTheme(R.style.AppTheme_Light);

        mGoogleAuth = FirebaseAuth.getInstance();
        mFacebookAuth = FirebaseAuth.getInstance();

        FacebookSdk.sdkInitialize(getApplicationContext());








        // Google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);







        direktSign(view);


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






        firebaseAuth = FirebaseAuth.getInstance();



        firebaseGmail = FirebaseAuth.getInstance().getCurrentUser();

        Intent cıkısIntent = getIntent();

        if(cıkısIntent.getStringExtra("cıkıs") != null){
            firebaseAuth.signOut();
        }


        binding.parolayenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserLogin.this, ForgetPassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_right);
            }
        });



    }

    private void direktSign(View view){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com")) {
                    // Kullanıcı Google hesabı ile giriş yapmıştır
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                    if (acct != null && acct.getIdToken() != null) {
                        // Google hesabı ile oturum açmış kullanıcılara özgü işlemler
                        googlegiris(view);

                    }
                    break;
                } else if (providerId.equals("facebook.com")) {
                    // Kullanıcı Facebook hesabı ile giriş yapmıştır
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    if (isLoggedIn) {
                        // Facebook hesabı ile oturum açmış kullanıcılara özgü işlemler

                        facebookgiris(view);

                    }
                    break;
                }else if (providerId.equals("password")) {
                    // Kullanıcı email ve şifre ile giriş yapmıştır

                    goToHome();
                    break;
                }
            }
        } else {
            // Kullanıcı oturum açmamıştır
            googleSignInClient.signOut();
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
            clearApplicationCache(getApplicationContext());

            FirebaseAuth.getInstance().signOut();


        }
    }

    public void goToHome() {
        Intent intent = new Intent(UserLogin.this, AnaSayfa.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }
    public static void clearApplicationCache(Context context) {

      try {

        File cacheDirectory = context.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }

    }

    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }



    public void giris(View view) {

        String email = binding.email.getText().toString();
        String password = binding.sifre.getText().toString();

        if (TextUtils.isEmpty(email)) {

            binding.email.setError("Email");
        } else if (TextUtils.isEmpty(password)) {
            binding.sifre.setError("Password");

        } else {
            try {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(UserLogin.this, AnaSayfa.class);
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
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            Log.d("Error :", e.getMessage());
        }

        }
    }


    public void kayıt (View view){
            Intent intent = new Intent(UserLogin.this, Kayıt.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right, R.anim.slide_right);
    }







    // Google Sign
    public void googlegiris (View view){

        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(signInIntent,1);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mGoogleAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "google:onSuccess:" + task);
                            goToHome();

                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Error:" + e.getMessage());
                    }
                });


    }




    // Facebook Sign
    public void facebookgiris (View view){



        callbackManager = CallbackManager.Factory.create();

        state = 1;


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        try {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFacebookAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mFacebookAuth.getCurrentUser();
                            facebookGiris(user);
                        } else {
                            Toast.makeText(getApplicationContext(), (CharSequence) task.getException(),Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        Log.d("Error :", e.getMessage());
    }

    }

    private void facebookGiris(FirebaseUser user) {
        if(user != null){
          Intent intent = new Intent(UserLogin.this,AnaSayfa.class);
          startActivity(intent);
        }

    }












    // Google and Facebook Sign ResultCode
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {


                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...

            }
        }
        if(state == 1){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }


        super.onActivityResult(requestCode, resultCode, data);

    }





    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        }

        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
