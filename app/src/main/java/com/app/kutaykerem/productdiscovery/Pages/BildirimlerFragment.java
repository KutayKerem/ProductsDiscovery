package com.app.kutaykerem.productdiscovery.Pages;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar.BildirimlerAdaptor;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentBildirimlerBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BildirimlerFragment extends Fragment {


    private FragmentBildirimlerBinding binding;
    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;

    BildirimlerAdaptor bildirimlerAdaptor;
    RecyclerView recyclerView;
    int kontrol = 0;
    List<String> isteklerList;


    String dil;
    String userId;

    String signState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentBildirimlerBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        isteklerList = new ArrayList<>();

        userId = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Arkadasİstekleri");

        recyclerView = view.findViewById(R.id.recyclerBildirimler);


        userLauncher();


        istekler();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bildirimlerAdaptor = new BildirimlerAdaptor(isteklerList,requireContext());
        recyclerView.setAdapter(bildirimlerAdaptor);

        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });







    }
    public void dilTanı(final DilCallback dilCallback){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    try {
                        dil = dataSnapshot.child("dil").getValue(String.class);
                    }catch (Exception e){
                        Log.d("Error:",e.getMessage());
                    }


                    if(dil.equals("türkce")){

                        if(isteklerList.isEmpty()){
                            binding.bildirimler.setText("Bildirimler(" + bildirimlerAdaptor.getItemCount()+")");
                            binding.bildirimlerNullRecycler.setText("Şu an buralar sessiz...");
                            binding.bildirimlerNullRecycler.setVisibility(View.VISIBLE);
                        }else {
                            binding.bildirimlerNullRecycler.setVisibility(View.GONE);
                        }


                    }else if (dil.equals("ingilizce")){


                        if(isteklerList.isEmpty()){
                            binding.bildirimlerNullRecycler.setText("It's quiet around here at the moment...");
                            binding.bildirimlerNullRecycler.setVisibility(View.VISIBLE);
                        }else {
                            binding.bildirimlerNullRecycler.setVisibility(View.GONE);
                        }

                    }

                    dilCallback.onDilCallback(dil);
                }else{

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


    private void istekler() {

        userLauncher();

        databaseReference.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                try {
                    if (snapshot.child("tip").getValue() != null) {
                        String arkistekKontrol = snapshot.child("tip").getValue().toString();
                        if (arkistekKontrol.equals("aldi")) {
                            isteklerList.add(snapshot.getKey());
                            kontrol = 1;
                            bildirimlerAdaptor.notifyDataSetChanged();



                            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long isteklerinSayisi = snapshot.getChildrenCount();
                                    // İsteklerin sayısını kullanabilirsiniz

                                    dilTanı(new DilCallback() {
                                        @Override
                                        public void onDilCallback(String dil) {
                                            if (dil.equals("türkce")){
                                                binding.bildirimler.setText("Bildirimler(" + isteklerinSayisi +")");
                                            }else{
                                                binding.bildirimler.setText("Notifications(" +isteklerinSayisi +")");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("Error:", error.getMessage());
                                }
                            });







                        }
                    }
                }catch (Exception e){
                    Log.d("Error:",e.getMessage());
                }







            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                isteklerList.remove(snapshot.getKey());
                bildirimlerAdaptor.notifyDataSetChanged();
                kontrol=1;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
            binding.recyclerBildirimler.setVisibility(View.VISIBLE);
            binding.bildirimlerProgressBar.setVisibility(View.INVISIBLE);
            binding.bildirimlerNullRecycler.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerBildirimler.setVisibility(View.INVISIBLE);
            binding.bildirimlerProgressBar.setVisibility(View.VISIBLE);
            binding.bildirimlerNullRecycler.setVisibility(View.INVISIBLE);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        networkController();
    }


}