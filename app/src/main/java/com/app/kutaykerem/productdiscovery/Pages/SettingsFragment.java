package com.app.kutaykerem.productdiscovery.Pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.databinding.FragmentFeedbackBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class SettingsFragment extends Fragment {


    FragmentFeedbackBinding binding;



    String userId;
    String dil;

    String signState;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DatabaseReference databaseReference;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userLauncher();


        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if (dil.equals("türkce")) {
                    binding.bizeUlasText.setText("Bize Ulaşın");
                    binding.biziDegerlendirText.setText("Bizi Değerlendirin");
                    binding.feedbackFeedbackText.setText("Ayarlar");
                    binding.constraintGizlilikPolitikasiText.setText("Gizlilik Politikası");


                } else if (dil.equals("ingilizce")) {
                    binding.bizeUlasText.setText("Contact Us");
                    binding.biziDegerlendirText.setText("Rate Us");
                    binding.feedbackFeedbackText.setText("Settings");
                    binding.constraintGizlilikPolitikasiText.setText("Privacy Policy");
                }
            }
        });

        try {
        binding.constraintBizeUlas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dilTanı(new DilCallback() {
                    @Override
                    public void onDilCallback(String dil) {
                        if (dil.equals("türkce")) {
                            Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                            mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"evolvingsoftware2021@gmail.com"});
                            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Products Discovery Geri Bildirim");
                            mailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            mailIntent.setData(Uri.parse("mailto:"));
                            startActivity(mailIntent);
                        } else {
                            Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                            mailIntent.putExtra(Intent.EXTRA_EMAIL, "Products Discovery Feedback");

                            mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"evolvingsoftware2021@gmail.com"});
                            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Products Discovery Feedback");
                            mailIntent.putExtra(Intent.EXTRA_TEXT, "");
                            mailIntent.setData(Uri.parse("mailto:"));
                            startActivity(mailIntent);
                        }
                    }
                });


            }
        });
    }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }


        // https://play.google.com/store/apps/details?id=com.app.kutaykerem.productdiscovery

        binding.constraintBiziDegerlendirin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.app.kutaykerem.productdiscovery"));
                    startActivity(intent);
                }catch (android.content.ActivityNotFoundException e){
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.app.kutaykerem.productdiscovery"));
                    startActivity(intent);
                }


            }
        });







        try {

        if(getArguments() != null){

            String arananParcaAdi = SettingsFragmentArgs.fromBundle(getArguments()).getArananParca();



            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @SuppressLint("ResourceAsColor")
                @Override
                public void handleOnBackPressed() {
                    NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToHomeFragmentBottomNav().setPos(1).setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);
                }
            });

            binding.feedbackGeri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToHomeFragmentBottomNav().setPos(1).setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);
                }
            });

            binding.constraintGizlilikPolitikasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToGizlilikPolitikasiFragment(dil).setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);

                }
            });



        }else{
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @SuppressLint("ResourceAsColor")
                @Override
                public void handleOnBackPressed() {
                    NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToHomeFragmentBottomNav().setPos(1);
                    Navigation.findNavController(view).navigate(action);
                }
            });

            binding.feedbackGeri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToHomeFragmentBottomNav().setPos(1);
                    Navigation.findNavController(view).navigate(action);
                }
            });
            binding.constraintGizlilikPolitikasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                            NavDirections action = SettingsFragmentDirections.actionFeedbackFragmentToGizlilikPolitikasiFragment(dil);
                            Navigation.findNavController(view).navigate(action);


                }
            });

        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }









    }


    public void dilTanı(final DilCallback dilCallback){

        try {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);

                    dilCallback.onDilCallback(dil);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }

    }



    public interface DilCallback {
        void onDilCallback(String dil);
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

                registerUser("google",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail());

            }else if(signState.equals("facebook")){

                registerUser("facebook",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail());

            }else if(signState.equals("gmail_hotmail")){

                registerUser("gmail_hotmail",firebaseUser.getUid(),"user name",null,firebaseUser.getEmail());

            }



        } else {
            // Kullanıcı oturum açmamıştır

        }
    }



    private void registerUser(String signState, String uid, String userName, Uri userProfile, String email){

        if(signState.equals("google")){

            userId = uid;

        }else if(signState.equals("facebook")){

            userId = uid;
        }else if(signState.equals("gmail_hotmail")){
            userId = uid;
        }


        else{

        }

    }






}
