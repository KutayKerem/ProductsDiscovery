package com.app.kutaykerem.productdiscovery.Pages;

import static androidx.fragment.app.FragmentManager.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.databinding.FragmentAddPostSecondStageBinding;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class AddPostSecondStage extends Fragment {



    public FragmentAddPostSecondStageBinding binding;

    //   ca-app-pub-1885716495742225/2048389001 : Addmob Interstitial ID

    /*
   Tests ID:
   Banner : ca-app-pub-3940256099942544/6300978111
   Interstitial : ca-app-pub-3940256099942544/1033173712
   Interstitial Video : ca-app-pub-3940256099942544/8691691433
   Rewarded : ca-app-pub-3940256099942544/5224354917

   daha fazlası: https://developers.google.com/admob/android/test-ads

        */
    private InterstitialAd mInterstitialAd;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;

    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference = null;
    FirebaseDatabase database=null;
    Bitmap selectedimage;



    HashMap<String, Object> data = new HashMap<>();





    FirebaseUser userCurrentUser;
    String userId;
    String dil;

    String signState;


    Boolean imageVısıble_Gone = false;
    Boolean gonderiliyor = false;
    ByteArrayOutputStream outputStream;
    byte[] resimByte;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostSecondStageBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Profile");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        userLauncher();
        registerLauncher();

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();


        InterstitialAd.load(requireContext(),"ca-app-pub-1885716495742225/2048389001", adRequest,
                new InterstitialAdLoadCallback() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


        if(getArguments() != null){
            String stringParcaAdi = AddPostSecondStageArgs.fromBundle(getArguments()).getParcaAdi();
            String stringSonParcaModeli = AddPostSecondStageArgs.fromBundle(getArguments()).getSonParcaModeli();
            String stringAciklama =  AddPostSecondStageArgs.fromBundle(getArguments()).getAciklama();
            String dil = AddPostSecondStageArgs.fromBundle(getArguments()).getDil();
            String tur =AddPostSecondStageArgs.fromBundle(getArguments()).getTur();


            try {




            binding.addPostSecondPuanDeger.setEnabled(false);
            binding.addPostSecondRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    binding.addPostSecondPuanDeger.setText(String.valueOf(v));
                    ratingBar.setEnabled(true);

                }
            });




            if(tur.equals("Gönderi") || tur.equals("Post")){
                binding.addPostSecondSecondText.setVisibility(View.VISIBLE);
                binding.addPostSecondSelectImage.setVisibility(View.VISIBLE);

                imageVısıble_Gone = true;

                if(dil.equals("türkce")){

                    binding.addPostSecondSecondText.setText("Fotoğraf ekle");
                    binding.addPostSecondPuanverText.setText("Puan ver");
                    binding.addPostSecondYukle.setText("Paylaş");
                }else if(dil.equals("ingilizce")){
                    binding.addPostSecondSecondText.setText("Add photo");
                    binding.addPostSecondPuanverText.setText("Rate it");
                    binding.addPostSecondYukle.setText("Share");

                }



                binding.addPostSecondSelectImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage(view);

                    }
                });

                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {

                        try {
                        binding.addPostSecondView.setVisibility(View.GONE);
                        //NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_left).setExitAnim(R.anim.slide_left).build();
                        NavDirections action = AddPostSecondStageDirections.actionAddPostSecondStageToAddPostFirstStage().setArananParca("null");
                        Navigation.findNavController(view).navigate(action);
                    }catch (Exception e){
                        Log.d("Error:",e.getLocalizedMessage());
                    }
                    }
                });




                binding.addPostSecondYukle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(imageData != null || !binding.addPostSecondPuanDeger.getText().toString().isEmpty()){
                            try {
                            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {

                                }
                            });
                        }catch (Exception e){
                            Log.d("Error:",e.getLocalizedMessage());
                        }
                            gonderiliyor = true;
                            Gonderi(view,stringParcaAdi,stringAciklama,stringSonParcaModeli,binding.addPostSecondPuanDeger.getText().toString());
                        }else {
                            if(imageData == null){
                                if (dil.equals("türkce")){
                                    Toast.makeText(requireContext(),"Gönderi için lütfen bir fotoğraf seçin...",Toast.LENGTH_SHORT).show();;
                                }else {
                                    Toast.makeText(requireContext(),"Please select a photo for the post...",Toast.LENGTH_SHORT).show();
                                }

                            }
                            if(binding.addPostSecondPuanDeger.getText().toString().isEmpty()){
                                if (dil.equals("türkce")){
                                    Toast.makeText(requireContext(),"Lütfen göderinize puan verin... ",Toast.LENGTH_SHORT).show();;
                                }else {
                                    Toast.makeText(requireContext(),"Please rate the post...",Toast.LENGTH_SHORT).show();
                                }

                            }

                        }



                    }
                });


            }else if (tur.equals("İfade Akışı")|| tur.equals("Expression Stream")){
                binding.addPostSecondSecondText.setVisibility(View.GONE);
                binding.addPostSecondSelectImage.setVisibility(View.GONE);
                imageVısıble_Gone = false;


                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.addPostSecondPuanverText.getLayoutParams();
                layoutParams.startToStart = ConstraintSet.PARENT_ID;
                layoutParams.endToEnd = ConstraintSet.PARENT_ID;
                layoutParams.setMarginStart(0);
                layoutParams.setMarginEnd(0);
                binding.addPostSecondPuanverText.setLayoutParams(layoutParams);


                if(dil.equals("türkce")){
                    binding.addPostSecondPuanverText.setTextSize(20);
                    binding.addPostSecondPuanverText.setText("Son Olarak Gönderinize Bir Puan Verin");
                    binding.addPostSecondYukle.setText("Paylaş");


                }else if(dil.equals("ingilizce")){
                    binding.addPostSecondPuanverText.setTextSize(20);
                    binding.addPostSecondPuanverText.setText("Please Rate Your Experience with the Response");
                    binding.addPostSecondYukle.setText("Share");


                }


                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                      try {
                        binding.addPostSecondView.setVisibility(View.GONE);
                        //NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_left).setExitAnim(R.anim.slide_left).build();
                        NavDirections action =AddPostSecondStageDirections.actionAddPostSecondStageToAddPostFirstStage().setArananParca("null");
                        Navigation.findNavController(view).navigate(action);
                    }catch (Exception e){
                        Log.d("Error:",e.getLocalizedMessage());
                    }
                    }
                });

                binding.addPostSecondYukle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(imageData == null && !binding.addPostSecondPuanDeger.getText().toString().isEmpty()){
                          try {
                            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {

                                }
                            });
                        }catch (Exception e){
                            Log.d("Error:",e.getLocalizedMessage());
                        }

                            anlıkDusunce(view,stringParcaAdi,stringAciklama,stringSonParcaModeli,binding.addPostSecondPuanDeger.getText().toString());
                        }else {
                            if(binding.addPostSecondPuanDeger.getText().toString().isEmpty()){
                                if (dil.equals("türkce")){
                                    Toast.makeText(requireContext(),"Lütfen göderinize puan verin... ",Toast.LENGTH_SHORT).show();;
                                }else {
                                    Toast.makeText(requireContext(),"Please rate the post...",Toast.LENGTH_SHORT).show();
                                }

                            }

                        }



                    }
                });







            }





        }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());

            }



        binding.addPostSecondGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                  binding.addPostSecondView.setVisibility(View.GONE);
                // NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_left).setExitAnim(R.anim.slide_left).build();
                NavDirections action = AddPostSecondStageDirections.actionAddPostSecondStageToAddPostFirstStage().setArananParca("null");
                Navigation.findNavController(view).navigate(action);

            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }
            }
        });




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

                registerUser("google",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser,firebaseUser.getEmail());

            }else if(signState.equals("facebook")){

                registerUser("facebook",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser,firebaseUser.getEmail());

            }else if(signState.equals("gmail_hotmail")){

                registerUser("gmail_hotmail",firebaseUser.getUid(),"user name",firebaseUser,firebaseUser.getEmail());

            }



        } else {
            // Kullanıcı oturum açmamıştır

        }
    }

    private void registerUser(String signState, String uid, String userName,FirebaseUser currentUser, String email){

        if(signState.equals("google")){

            userId = uid;
            userCurrentUser = currentUser;

        }else if(signState.equals("facebook")){
            userId = uid;
            userCurrentUser = currentUser;
        }else if(signState.equals("gmail_hotmail")){
            userId = uid;
            userCurrentUser = currentUser;
        }


        else{

        }

    }




    public void selectImage(View view) {


        try {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {


                if(dil.equals("türkce")){
                    Snackbar.make(view, "Fotoğraf yüklemeniz için izne ihtiyacımız var", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                }else if (dil.equals("ingilizce")){
                    Snackbar.make(view, "We need permission to choose a profile photo", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                }



            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        }

        }catch (Exception e){
        Log.d("Error :", e.getMessage());

        }


    }






    public void registerLauncher() {



        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        imageData = intent.getData();
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), imageData);
                                selectedimage = ImageDecoder.decodeBitmap(source);


                            } else {
                                selectedimage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData);
                            }

                            Glide.with(getView()).load(imageData).override(450,450).into(binding.addPostSecondSelectImage);


                            int bitmapSize = selectedimage.getAllocationByteCount();

                            double megabytes = bitmapSize / (1024.0 * 1024.0);

                            if (megabytes > 20) {

                                makeScalerUri(imageData);
                            } else {
                                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, 800, 800, true);


                                File tempFile = File.createTempFile("image", ".jpg", getActivity().getCacheDir());
                                FileOutputStream fos = new FileOutputStream(tempFile);
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                                fos.close();

                                imageData = Uri.fromFile(tempFile);

                            }





                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }


                    }
                }

            }
        });

        try {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                } else {

                    if(dil.equals("türkce")){
                        Toast.makeText(getActivity(), "İzin verilmedi", Toast.LENGTH_LONG).show();

                    }else if (dil.equals("ingilizce")){
                        Toast.makeText(getActivity(), "Not allowed", Toast.LENGTH_LONG).show();

                    }


                }

            }
        });

    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }

    }



    public void makeScalerUri(Uri image) {

            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, 600, 600, true);

                File tempFile = File.createTempFile("image", ".jpg", getActivity().getCacheDir());
                FileOutputStream fos = new FileOutputStream(tempFile);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                fos.close();

                imageData = Uri.fromFile(tempFile);

            } catch (IOException e) {
                e.printStackTrace();
            }






    }

    public void anlıkDusunce(View view,String parcaAdi,String aciklama,String sonParcaModeli,String puanDeger){
        try {

        Boolean isApproved = false;
        imageData = null;


        if(parcaAdi.isEmpty() || aciklama.isEmpty()) {


            if(dil.equals("türkce")){
                Toast.makeText(getActivity(),"Bilgileri Doldurunuz",Toast.LENGTH_LONG).show();

            }else if (dil.equals("ingilizce")){
                Toast.makeText(getActivity(), "Fill in Information", Toast.LENGTH_LONG).show();

            }


        }
        else{

            if(sonParcaModeli.isEmpty()) {


                    if(dil.equals("türkce")){
                        Toast.makeText(getActivity(), "Bilgileri Doldurunuz", Toast.LENGTH_LONG).show();
                    }else if (dil.equals("ingilizce")){
                        Toast.makeText(getActivity(), "Fill in Information", Toast.LENGTH_LONG).show();

                    }



            }
            else {
               isApproved = true;
            }


        }




        if(isApproved) {


            String gonderiId = UUID.randomUUID().toString();

            String gonderenId = userId;

            data.put("aciklama", aciklama);
            data.put("parcaAdi", parcaAdi);
            data.put("parcaModeli", sonParcaModeli);
            data.put("puanDegeri", puanDeger);
            data.put("email", userCurrentUser.getEmail());
            data.put("begeniSayisi", 0);
            data.put("gonderiId", gonderiId);
            data.put("gonderenId", gonderenId);
            Date startTime = new Date();
            data.put("tarih", startTime);




                firebaseFirestore.collection("Kesfet").document("AnlıkDüsünce").collection("Gonderiler").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                       AddPostSecondStageDirections.ActionAddPostSecondStageToHomeFragmentBottomNav action = AddPostSecondStageDirections.actionAddPostSecondStageToHomeFragmentBottomNav().setPos(1).setDiscoveryTabPos(1);
                        Navigation.findNavController(view).navigate(action);
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error: ", e.getLocalizedMessage());
                    }
                });

            }


        }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }


























    }





    public void Gonderi(View view,String parcaAdi,String aciklama,String sonParcaModeli,String puanDeger) {
        try {



        Boolean isApproved = false;
        if(parcaAdi.isEmpty() || aciklama.isEmpty()) {

            if(dil.equals("türkce")){
                Toast.makeText(getActivity(), "Bilgileri Doldurunuz", Toast.LENGTH_LONG).show();
                binding.addPostSecondYukle.setEnabled(true);
            }else if (dil.equals("ingilizce")){
                Toast.makeText(getActivity(), "Fill in Information", Toast.LENGTH_LONG).show();
                binding.addPostSecondYukle.setEnabled(true);

            }

        }
        else{

            if(sonParcaModeli.isEmpty()) {
                    if(dil.equals("türkce")){
                        Toast.makeText(getActivity(), "Bilgileri Doldurunuz", Toast.LENGTH_LONG).show();
                        binding.addPostSecondYukle.setEnabled(true);
                    }else if (dil.equals("ingilizce")){
                        Toast.makeText(getActivity(), "Fill in Information", Toast.LENGTH_LONG).show();
                        binding.addPostSecondYukle.setEnabled(true);

                    }
            }else{

                isApproved = true;
            }


        }





        if(isApproved){
            if(imageData != null) {



                UUID uuid = UUID.randomUUID();
                final String imagesName = "İmages/" + uuid + ".jpg";


                storageReference.child(imagesName).putFile(imageData).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {


                        binding.addPostSecondYukle.setEnabled(false);
                        binding.addPostSecondGeri.setEnabled(false);
                        binding.addPostSecondProgressBar.setVisibility(View.VISIBLE);


                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference storage = FirebaseStorage.getInstance().getReference(imagesName);
                        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();


                                String gonderiId = UUID.randomUUID().toString();

                                String gonderenId = userId;
                                data.put("downloadUrl", downloadUrl);
                                data.put("aciklama", aciklama);
                                data.put("parcaAdi", parcaAdi);
                                data.put("parcaModeli", sonParcaModeli);
                                data.put("puanDegeri", puanDeger);
                                data.put("begeniSayisi", 0);
                                data.put("email", userCurrentUser.getEmail());
                                data.put("gonderiId", gonderiId);
                                data.put("gonderenId", gonderenId);
                                Date startTime = new Date(); // Başlangıç zamanı
                                data.put("tarih", startTime);

                                firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        binding.addPostSecondYukle.setEnabled(true);
                                        binding.addPostSecondProgressBar.setVisibility(View.GONE);
                                        binding.addPostSecondGeri.setEnabled(true);

                                       AddPostSecondStageDirections.ActionAddPostSecondStageToHomeFragmentBottomNav action = AddPostSecondStageDirections.actionAddPostSecondStageToHomeFragmentBottomNav().setPos(1).setDiscoveryTabPos(0);
                                        Navigation.findNavController(view).navigate(action);
                                        if (mInterstitialAd != null) {
                                            mInterstitialAd.show(getActivity());
                                        } else {

                                        }


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.addPostSecondYukle.setEnabled(true);
                                        binding.addPostSecondProgressBar.setVisibility(View.GONE);
                                        Log.d("Error: ", e.getLocalizedMessage());
                                    }
                                });


                            }


                        });


                    }
                });
                }



            }



        }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }









    }


    public boolean isConnected() {

        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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


            binding.addPostSecondProgressBar.setVisibility(View.GONE);
            binding.addPostSecondYukle.setVisibility(View.VISIBLE);
            binding.addPostSecondPuanverText.setVisibility(View.VISIBLE);
            binding.addPostSecondRatingBar.setVisibility(View.VISIBLE);


            if(imageVısıble_Gone == true){
                binding.addPostSecondSelectImage.setVisibility(View.VISIBLE);
            }else{
                binding.addPostSecondSelectImage.setVisibility(View.GONE);
            }


            binding.addPostSecondSecondText.setVisibility(View.VISIBLE);

            binding.addPostSecondGeri.setVisibility(View.VISIBLE);
            binding.addPostSecondPuanDeger.setVisibility(View.VISIBLE);


        } else {
            binding.addPostSecondProgressBar.setVisibility(View.VISIBLE);
            binding.addPostSecondYukle.setVisibility(View.GONE);
            binding.addPostSecondPuanverText.setVisibility(View.GONE);
            binding.addPostSecondRatingBar.setVisibility(View.GONE);
            binding.addPostSecondSelectImage.setVisibility(View.GONE);
            binding.addPostSecondSecondText.setVisibility(View.GONE);
            binding.addPostSecondGeri.setVisibility(View.GONE);
            binding.addPostSecondPuanDeger.setVisibility(View.GONE);


        }



    }




    @Override
    public void onResume() {
        super.onResume();
        networkController();
    }



}