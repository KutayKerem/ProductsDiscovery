package com.app.kutaykerem.productdiscovery.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.kutaykerem.productdiscovery.Models.GetDate;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentHomeBottomNavBinding;
import com.facebook.AccessToken;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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
import java.util.HashMap;
import java.util.Locale;


public class HomeFragmentBottomNav extends Fragment {


    private FragmentHomeBottomNavBinding binding;






    // ca-app-pub-1885716495742225/9321493922 : Admob homeFragmentBannar ID

    /*
    Tests ID:
    Banner : ca-app-pub-3940256099942544/6300978111
    Interstitial : ca-app-pub-3940256099942544/1033173712
    Interstitial Video : ca-app-pub-3940256099942544/8691691433
    Rewarded : ca-app-pub-3940256099942544/5224354917

    daha fazlası: https://developers.google.com/admob/android/test-ads

         */
    private AdView mAdView;





    public BottomNavigationView bottomNavigationView;

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }


    int Kontrol = 0;


    String arkistek ;

    FirebaseAuth userAuth;
    String userId;
    String dil;
    String signState;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;


    String arananParcaAdi,gonderenId;
    int pos,kesfetTabPos;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBottomNavBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.buttomNavigation);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Arkadasİstekleri");





        userLauncher();
        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });








        if (getArguments() != null) {
            arananParcaAdi = HomeFragmentBottomNavArgs.fromBundle(getArguments()).getArananParca();
            pos = HomeFragmentBottomNavArgs.fromBundle(getArguments()).getPos();
            kesfetTabPos =HomeFragmentBottomNavArgs.fromBundle(getArguments()).getDiscoveryTabPos();
            gonderenId = HomeFragmentBottomNavArgs.fromBundle(getArguments()).getGonderen();




            if(arananParcaAdi.equals("null") || arananParcaAdi.isEmpty()){
                if(gonderenId.equals("null") || gonderenId.isEmpty()){
                    if(pos == 0){
                        replaceFragment(new AnasayfaFragment(),"anasayfa","null","null",0);
                        bottomNavigationView.setSelectedItemId(R.id.anaSayfa);
                    }else if(pos == 1){
                        replaceFragment(new Discovery(),"discovery",arananParcaAdi,"null",kesfetTabPos);
                        bottomNavigationView.setSelectedItemId(R.id.Kesfet);
                    }else if(pos == 2){
                        replaceFragment(new ArkadaslarListesiFragment(),"arkadaslarListesi","null","null",0);
                        bottomNavigationView.setSelectedItemId(R.id.Arkadaslar);
                    }else if(pos == 3){
                        replaceFragment(new BildirimlerFragment(),"Bildirimler","null","null",0);
                        bottomNavigationView.setSelectedItemId(R.id.Bildirimler);
                    }else if(pos == 4){
                        replaceFragment(new KullaniciProfileFragment(),"Profile","null","null",0);
                     if(gonderenId == userId){
                         bottomNavigationView.setSelectedItemId(R.id.Profile);
                     }else{
                         bottomNavigationView.setSelectedItemId(R.id.anaSayfa);
                     }

                    }
                }else {
                    replaceFragment(new KullaniciProfileFragment(),"Profile","null",gonderenId,0);
                    if(gonderenId == userId){
                        bottomNavigationView.setSelectedItemId(R.id.Profile);
                    }else{

                    }
                }
            }else{
                replaceFragment(new Discovery(),"discovery",arananParcaAdi,"null",kesfetTabPos);
                bottomNavigationView.setSelectedItemId(R.id.Kesfet);
            }



        }

try {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.anaSayfa) {
                    replaceFragment(new AnasayfaFragment(), "anasayfa", "null", "null", 0);
                    return true;
                } else if (item.getItemId() == R.id.Kesfet) {
                    replaceFragment(new Discovery(), "discovery", "null", "null", 0);
                    return true;
                } else if (item.getItemId() == R.id.Arkadaslar) {
                    replaceFragment(new ArkadaslarListesiFragment(), "arkadaslarListesi", "null", "null", 0);
                    return true;
                } else if (item.getItemId() == R.id.Bildirimler) {
                    replaceFragment(new BildirimlerFragment(), "Bildirimler", "null", "null", 0);
                    return true;
                } else if (item.getItemId() == R.id.Profile) {
                    replaceFragment(new KullaniciProfileFragment(), "Profile", "null", "null", 0);
                    return true;
                } else {
                    replaceFragment(new AnasayfaFragment(), "anasayfa", "null", "null", 0);
                    return false;
                }


            }
        });
    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }




    private void replaceFragment(Fragment fragment,String yer,String arananParcaAdi,String gonderenId,int kesfetTabPos){

        try {
        if(yer.equals("anasayfa")){
            binding.buttomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary));
            binding.buttomNavigation.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bottomnavigation_background));

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.homeFragmeLayout,fragment);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();
        }else if(yer.equals("discovery")){
            binding.buttomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary));
            binding.buttomNavigation.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bottomnavigation_background));


            Bundle bundle = new Bundle();
            bundle.putString("arananParca", arananParcaAdi);
            bundle.putInt("position",kesfetTabPos);

            fragment.setArguments(bundle);


            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.homeFragmeLayout,fragment);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();


        }

        else if(yer.equals("Bildirimler")){
            binding.buttomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary));
            binding.buttomNavigation.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bottomnavigation_background));

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.homeFragmeLayout,fragment);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();
        }else if(yer.equals("Profile")){
            binding.buttomNavigation.setBackground(null);
            binding.buttomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary));


            Bundle bundle = new Bundle();
            bundle.putString("gonderen", gonderenId);
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.homeFragmeLayout,fragment);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();


        }else if(yer.equals("arkadaslarListesi")){
            binding.buttomNavigation.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary));
            binding.buttomNavigation.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bottomnavigation_background));

            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.homeFragmeLayout,fragment);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commit();
        }

    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }

    }
    private void userLauncher(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com")) {
                    // Kullanıcı Google hesabı ile giriş yapmıştır
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireContext());
                    if (acct != null && acct.getIdToken() != null) {
                        // Google hesabı ile oturum açmış kullanıcılara özgü işlemler

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



    private void registerUser(String signState, String uid, String userName, Uri userProfile, String email){

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
        }else{


            userId = uid;

            databaseReference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        databaseReference.child(userId).child("kullanıcıAdı").setValue("User name");
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
        }



    }

    public void dilTanı(final DilCallback dilCallback){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);


                    if(dil.equals("türkce")){
                        bottomNavigationView.getMenu().findItem(R.id.Kesfet).setTitle("Keşfet");
                        bottomNavigationView.getMenu().findItem(R.id.anaSayfa).setTitle("Ana Sayfa");
                        bottomNavigationView.getMenu().findItem(R.id.Arkadaslar).setTitle("Arkadaşlar");
                        bottomNavigationView.getMenu().findItem(R.id.Bildirimler).setTitle("Bildirimler");
                        bottomNavigationView.getMenu().findItem(R.id.Profile).setTitle("Profil");









                        try {
                            requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {

                                try {
                                    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                        @SuppressLint("ResourceAsColor")
                                        @Override
                                        public void handleOnBackPressed() {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                            alertDialog.setTitle("Çık");
                                            alertDialog.setMessage("Uygulamadan çıkmak istiyor musunuz?");

                                            alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent setIntent = new Intent(Intent.ACTION_MAIN);
                                                    setIntent.addCategory(Intent.CATEGORY_HOME);
                                                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(setIntent);
                                                }
                                            }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });


                                            AlertDialog customDialogs = alertDialog.create();
                                            customDialogs.show();

                                            customDialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.darkeyGray);
                                            customDialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.darkeyGray);


                                        }
                                    });
                                }catch (Exception e){

                                }



                            }
                        });

                        }catch (Exception e){
                            Log.d("Error:",e.getMessage());
                        }

                    }else if (dil.equals("ingilizce")){
                        bottomNavigationView.getMenu().findItem(R.id.Kesfet).setTitle("Discover");
                        bottomNavigationView.getMenu().findItem(R.id.anaSayfa).setTitle("Home Page");
                        bottomNavigationView.getMenu().findItem(R.id.Arkadaslar).setTitle("Friends");
                        bottomNavigationView.getMenu().findItem(R.id.Bildirimler).setTitle("Notifications");
                        bottomNavigationView.getMenu().findItem(R.id.Profile).setTitle("Profile");



                        try{
                        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {

                                try {

                                    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                        @SuppressLint("ResourceAsColor")
                                        @Override
                                        public void handleOnBackPressed() {

                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                            alertDialog.setTitle("Exit");
                                            alertDialog.setMessage("Do you want to exit the application?");

                                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent setIntent = new Intent(Intent.ACTION_MAIN);
                                                    setIntent.addCategory(Intent.CATEGORY_HOME);
                                                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(setIntent);
                                                }
                                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                            AlertDialog customDialogs = alertDialog.create();
                                            customDialogs.show();

                                            customDialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.darkeyGray);
                                            customDialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.darkeyGray);

                                        }
                                    });
                            }catch (Exception e){

                            }

                            }
                        });

                        }catch (Exception e){

                            Log.d("Error:",e.getMessage());
                        }












                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });


    }

    public interface DilCallback {
        void onDilCallback(String dil);
    }
    public void bell(){

        try {
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NewApi", "ResourceType"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean aldiVarMi = false;
                for(DataSnapshot childSnapshot: snapshot.getChildren()) {

                    String tip = childSnapshot.child("tip").getValue(String.class);

                    if(tip != null && tip.equals("aldi")) {
                        aldiVarMi = true;
                        break;
                    }
                }

                if (aldiVarMi) {
                    Menu menu = bottomNavigationView.getMenu();
                    MenuItem item = menu.findItem(R.id.Bildirimler);
                    item.setIcon(R.drawable.notification);


                } else {
                    Menu menu = bottomNavigationView.getMenu();
                    MenuItem item = menu.findItem(R.id.Bildirimler);
                    item.setIcon(R.drawable.bildirim);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        if(Kontrol == 1){

        }else if (Kontrol == 2){

        }


        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }

    }
    public Uri getDrawableUri(int drawableId) {
        try {
            Resources res = getResources();
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    res.getResourcePackageName(drawableId) + '/' +
                    res.getResourceTypeName(drawableId) + '/' +
                    res.getResourceEntryName(drawableId));


        } catch (Exception e) {
            Log.d("Error:", e.getMessage());
        }


        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    try {
        bell();
    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }

    }




}