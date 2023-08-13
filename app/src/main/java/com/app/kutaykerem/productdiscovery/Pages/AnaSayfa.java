package com.app.kutaykerem.productdiscovery.Pages;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.app.kutaykerem.productdiscovery.Models.GetDate;
import com.app.kutaykerem.productdiscovery.Models.GetDateStart;
import com.app.kutaykerem.productdiscovery.Models.KullanıcıBilgileri;
import com.app.kutaykerem.productdiscovery.Models.Status_Navigation_Background;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.ActivityAnaSayfaBinding;
import com.app.kutaykerem.productdiscovery.Service.RefreshDatabase;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class AnaSayfa extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
   public  HashMap<String, Object> data;
   public  DatabaseReference databaseReference,onlineKontrolReference;
   public  FirebaseFirestore firebaseFirestore;
   public  FirebaseAuth firebaseAuth;
   public  FirebaseStorage firebaseStorage;
   public  StorageReference storageReference;
   public  String userId;
   public  String dil;
   public  String signState;
   public ActivityAnaSayfaBinding binding;
   Boolean stateUs;
   Data refresh_data ;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnaSayfaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);




        databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile");
        onlineKontrolReference = FirebaseDatabase.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();









        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("night",false);

        if(nightMode){
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), true, false);

        }else{
            Status_Navigation_Background.setFullScreen(getWindow());
            Status_Navigation_Background.lightStatusBar(getWindow(), false, false);

        }

        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }







        userLauncher();


        networkController();





    }

    private void userLauncher(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com")) {
                    // Kullanıcı Google hesabı ile giriş yapmıştır
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                    if (acct != null && acct.getIdToken() != null) {
                        // Google hesabı ile oturum açmış kullanıcılara özgü işlemler


                        profile.getDisplayName();
                    }
                    signState = "google";
                    break;

                }
                else if (providerId.equals("facebook.com")) {
                    // Kullanıcı Facebook hesabı ile giriş yapmıştır
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    if (isLoggedIn) {
                        // Facebook hesabı ile oturum açmış kullanıcılara özgü işlemler
                    }

                    signState = "facebook";
                    break;

                }else if (providerId.equals("password")) {
                    // Kullanıcı email ve şifre ile giriş yapmıştır


                    signState = "gmail_hotmail";

                    break;
                }
            }

            if(signState.equals("google")){

                registerUser("google",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail());

            }else if(signState.equals("facebook")){

                registerUser("facebook",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail());

            }else if(signState.equals("gmail_hotmail")){

                registerUser("gmail_hotmail",firebaseUser.getUid(),"User name",null,firebaseUser.getEmail());

            }



        } else {
            // Kullanıcı oturum açmamıştır

        }
    }

    private void registerUser(String signState, String uid,String userName, Uri userProfile,String email){

        if(signState.equals("google")){

            userId = uid;
            databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        databaseReference.child(userId).child("kullanıcıAdı").setValue(userName);
                        databaseReference.child(userId).child("biyografi").setValue(null);

                        HashMap<String,Object> data = new HashMap<>();
                        data.put("kullanıcıAdı",userName);
                        data.put("tarih", GetDate.getDate());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(userId);
                        reference.updateChildren(data);

                        Locale currentLocale = Locale.getDefault();
                        String displayLanguage = currentLocale.getDisplayLanguage();



                        if (displayLanguage.equals("Türkçe"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "türkce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else if (displayLanguage.equals("English"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }





                        final String name = "Kullanıcılar/" + userId + ".jpg";
                        String photoUrlString = userProfile.toString();


                            try {
                                URL url = new URL(photoUrlString);
                                AsyncTask<URL, Void, InputStream> task = new AsyncTask<URL, Void, InputStream>() {
                                    @SuppressLint("StaticFieldLeak")
                                    @Override
                                    protected InputStream doInBackground(URL... urls) {
                                        try {
                                            URLConnection connection = urls[0].openConnection();
                                            return connection.getInputStream();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(InputStream inputStream) {
                                        if (inputStream != null) {

                                            storageReference.child(name).putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            String profileImage = uri.toString();

                                                            HashMap<String, Object> data = new HashMap<>();
                                                            data.put("ImageProfile", profileImage);
                                                            data.put("time", GetDate.getDate());



                                                            CollectionReference collectionReference = firebaseFirestore.collection("Profiles").document("Resimler").collection(userId);

                                                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot querySnapshot) {
                                                                    if (querySnapshot.isEmpty()){
                                                                        // Veri yoksa yeni belge oluştur
                                                                        collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("Error:", e.getLocalizedMessage());
                                                                            }
                                                                        });
                                                                    } else {
                                                                        // Veri varsa güncelle
                                                                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // İlk belgeyi al (varsayılan olarak)
                                                                        documentSnapshot.getReference().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // Başarılı olduğunda yapılacak işlemler
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("Error:", e.getLocalizedMessage());
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.d("Error:", e.getLocalizedMessage());
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Error:", e.getLocalizedMessage());
                                                        }
                                                    });;
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Error", e.getLocalizedMessage());
                                                }
                                            });;

                                        }
                                    }
                                };
                                task.execute(url);
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }

















                    }
                }
            });





        }else if(signState.equals("facebook")){

            userId = uid;
            System.out.println(userProfile);

            databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        databaseReference.child(userId).child("kullanıcıAdı").setValue(userName);
                        databaseReference.child(userId).child("biyografi").setValue(null);

                        HashMap<String,Object> data = new HashMap<>();
                        data.put("kullanıcıAdı",userName);
                        data.put("tarih",GetDate.getDate());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(userId);
                        reference.updateChildren(data);






                        Locale currentLocale = Locale.getDefault();
                        String displayLanguage = currentLocale.getDisplayLanguage();



                        if (displayLanguage.equals("Türkçe"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "türkce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else if (displayLanguage.equals("English"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }







                        final String name = "Kullanıcılar/" + userId + ".jpg";
                        String photoUrlString = userProfile.toString();


                        try {
                            URL url = new URL(photoUrlString);
                            AsyncTask<URL, Void, InputStream> task = new AsyncTask<URL, Void, InputStream>() {
                                @SuppressLint("StaticFieldLeak")
                                @Override
                                protected InputStream doInBackground(URL... urls) {
                                    try {
                                        URLConnection connection = urls[0].openConnection();
                                        return connection.getInputStream();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }

                                @SuppressLint("StaticFieldLeak")
                                @Override
                                protected void onPostExecute(InputStream inputStream) {
                                    if (inputStream != null) {

                                        storageReference.child(name).putStream(inputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        String profileImage = uri.toString();

                                                        HashMap<String, Object> data = new HashMap<>();
                                                        data.put("ImageProfile", profileImage);
                                                        data.put("time", GetDate.getDate());




                                                        CollectionReference collectionReference = firebaseFirestore.collection("Profiles").document("Resimler").collection(userId);

                                                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                                if (querySnapshot.isEmpty()){
                                                                    // Veri yoksa yeni belge oluştur
                                                                    collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("Error:", e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                } else {
                                                                    // Veri varsa güncelle
                                                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // İlk belgeyi al (varsayılan olarak)
                                                                    documentSnapshot.getReference().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            // Başarılı olduğunda yapılacak işlemler
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("Error:", e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Error:", e.getLocalizedMessage());
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Error:", e.getLocalizedMessage());
                                                    }
                                                });;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Error", e.getLocalizedMessage());
                                            }
                                        });;

                                    }
                                }
                            };
                            task.execute(url);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }






                    }
                }
            });



        }else if(signState.equals("gmail_hotmail")){
            userId = uid;

            databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        databaseReference.child(userId).child("kullanıcıAdı").setValue(userName);
                        databaseReference.child(userId).child("biyografi").setValue(null);



                        HashMap<String,Object> data = new HashMap<>();
                        data.put("kullanıcıAdı",userName);
                        data.put("tarih",GetDate.getDate());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(userId);
                        reference.updateChildren(data);


                        Locale currentLocale = Locale.getDefault();
                        String displayLanguage = currentLocale.getDisplayLanguage();



                        if (displayLanguage.equals("Türkçe"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "türkce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else if (displayLanguage.equals("English"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }











                        Uri imageUri = getDrawableUri(R.drawable.sh);


                        final String name = "Kullanıcılar/" + userId + ".jpg";

                        storageReference.child(name).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profileImage = uri.toString();

                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("ImageProfile", profileImage);
                                        data.put("default",true);
                                        data.put("time", GetDate.getDate());

                                        CollectionReference collectionReference = firebaseFirestore.collection("Profiles").document("Resimler").collection(userId);

                                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (querySnapshot.isEmpty()){
                                                    // Veri yoksa yeni belge oluştur
                                                    collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            // Başarılı olduğunda yapılacak işlemler
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Error:", e.getLocalizedMessage());
                                                        }
                                                    });
                                                } else {
                                                    // Veri varsa güncelle
                                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // İlk belgeyi al (varsayılan olarak)
                                                    documentSnapshot.getReference().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Başarılı olduğunda yapılacak işlemler
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Error:", e.getLocalizedMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Error:", e.getLocalizedMessage());
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                }
            });

        } else{
            userId = uid;

            databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        databaseReference.child(userId).child("kullanıcıAdı").setValue(userName);
                        databaseReference.child(userId).child("biyografi").setValue(null);


                        HashMap<String,Object> data = new HashMap<>();
                        data.put("kullanıcıAdı","User name");
                        data.put("tarih",GetDate.getDate());
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(userId);
                        reference.updateChildren(data);



                        Locale currentLocale = Locale.getDefault();
                        String displayLanguage = currentLocale.getDisplayLanguage();



                        if (displayLanguage.equals("Türkçe"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "türkce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else if (displayLanguage.equals("English"))
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("dil", "ingilizce");
                            hashMap.put("tarih", GetDate.getDate());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

                            try {
                                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        } else {
                                            // Hata durumunda işlemler
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }









                        Uri imageUri = getDrawableUri(R.drawable.sh);


                        final String name = "Kullanıcılar/" + userId + ".jpg";

                        storageReference.child(name).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String profileImage = uri.toString();

                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("ImageProfile", profileImage);
                                        data.put("default",true);
                                        data.put("time",GetDate.getDate());

                                        CollectionReference collectionReference = firebaseFirestore.collection("Profiles").document("Resimler").collection(userId);

                                        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (querySnapshot.isEmpty()){
                                                    // Veri yoksa yeni belge oluştur
                                                    collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            // Başarılı olduğunda yapılacak işlemler
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Error:", e.getLocalizedMessage());
                                                        }
                                                    });
                                                } else {
                                                    // Veri varsa güncelle
                                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // İlk belgeyi al (varsayılan olarak)
                                                    documentSnapshot.getReference().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Başarılı olduğunda yapılacak işlemler
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d("Error:", e.getLocalizedMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("Error:", e.getLocalizedMessage());
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                }
            });

        }

    }


    public void OnlineOflineDurum(Boolean durum) {


        try {
            DatabaseReference reference = databaseReference.child(userId);
            if(reference != null){

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null) {

                            KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);
                            String kulllaniciAdi = kullanıcıBilgileri.getKullanıcıAdı();
                            data = new HashMap<>();
                            data.put("durum", durum);
                            data.put("kullanıcıAdı",kulllaniciAdi);
                            if(durum == false){
                                Date tarih = new Date();
                                data.put("songörülme", GetDateStart.calculateElapsedTime(tarih));
                            }


                            onlineKontrolReference.child("Durum").child(userId).child("State").setValue(data);
                            onlineKontrolReference.child("Durum").child(userId).child("State").updateChildren(data);







                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }



    }


    public Uri getDrawableUri(int drawableId) {
        Resources res = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                res.getResourcePackageName(drawableId) + '/' +
                res.getResourceTypeName(drawableId) + '/' +
                res.getResourceEntryName(drawableId));
    }

    @Override
    protected void onStart() {
        super.onStart();
        WorkManager.getInstance(this).cancelAllWork();
        OnlineOflineDurum(true);
        networkController();
        WorkManager.getInstance(this).cancelAllWork();

    }

    @Override
    protected void onResume() {
        super.onResume();
        WorkManager.getInstance(this).cancelAllWork();
        OnlineOflineDurum(true);
        networkController();


    }

    @Override
    protected void onStop() {
        super.onStop();
        OnlineOflineDurum(false);

        // Kullanıcı uygulamadan çıktıntan sonra olurda durumu false olarak ayarlanmazsa 15 dakika sonra kullanıcı çıkış yapmış olsada false olarak ayarlanır.



        try {
            refresh_data =  new Data.Builder().putBoolean("durum",false).putString("userId",userId).build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class).setInitialDelay(5,TimeUnit.MINUTES).setInputData(refresh_data).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo.getState() == WorkInfo.State.RUNNING){
                        System.out.println("Running");
                    }else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                        System.out.println("Succeded");
                    }else if(workInfo.getState() == WorkInfo.State.FAILED){
                        System.out.println("Failed");
                    }
                }
            });

        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }


        /*
        Periyodik olarak çalıştırmak:

        WorkRequest workRequest = new PeriodicWorkRequest.Builder(RefreshDatabase.class,15,TimeUnit.MINUTES).setInputData(refresh_data).build();
       */

        //WorkManager.getInstance(this).cancelAllWork();

        //Chaining
        /*
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class)
                .setConstraints(constraints)
                .setInputData(data)
                //.setInitialDelay(5, TimeUnit.MINUTES)
                //.addTag("myTag")
                .build();

        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest)
                .then(oneTimeWorkRequest)
                .then(oneTimeWorkRequest)
                .enqueue();

         */


    }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineOflineDurum(false);

        try {
            refresh_data =  new Data.Builder().putBoolean("durum",false).putString("userId",userId).build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class).setInitialDelay(5,TimeUnit.MINUTES).setInputData(refresh_data).build();
            WorkManager.getInstance(this).enqueue(workRequest);

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo.getState() == WorkInfo.State.RUNNING){
                        System.out.println("Running");
                    }else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                        System.out.println("Succeded");
                    }else if(workInfo.getState() == WorkInfo.State.FAILED){
                        System.out.println("Failed");
                    }
                }
            });

        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OnlineOflineDurum(false);
        try {
            refresh_data =  new Data.Builder().putBoolean("durum",false).putString("userId",userId).build();
            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class).setInitialDelay(5,TimeUnit.MINUTES).setInputData(refresh_data).build();
            WorkManager.getInstance(this).enqueue(workRequest);

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
                @Override
                public void onChanged(WorkInfo workInfo) {
                    if (workInfo.getState() == WorkInfo.State.RUNNING){
                        System.out.println("Running");
                    }else if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
                        System.out.println("Succeded");
                    }else if(workInfo.getState() == WorkInfo.State.FAILED){
                        System.out.println("Failed");
                    }
                }
            });
        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }

    }

    public boolean isConnected() {

        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    public void networkController()  {



        if (isConnected()) {

        } else {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        dil = dataSnapshot.child("dil").getValue(String.class);


                        if(dil.equals("türkce")){
                            Toast.makeText(getApplicationContext(),"İnternet bağlantınızı kontrol edin",Toast.LENGTH_SHORT).show();

                        }else if (dil.equals("ingilizce")){
                            Toast.makeText(getApplicationContext(),"Check your internet connection",Toast.LENGTH_SHORT).show();

                        };

                    }else{

                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {



                }
            });



        }









        }




    }

















