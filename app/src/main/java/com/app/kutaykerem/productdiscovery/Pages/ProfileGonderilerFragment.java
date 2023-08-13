package com.app.kutaykerem.productdiscovery.Pages;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.kutaykerem.productdiscovery.Models.KesfetDetails;
import com.app.kutaykerem.productdiscovery.Adaptor.Profile.AdapterGonderiler;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentProfileGonderilerBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class ProfileGonderilerFragment extends Fragment {


    FragmentProfileGonderilerBinding binding;



    ArrayList<KesfetDetails> kesfetDetailsArrayList;
    AdapterGonderiler adapterGonderiler;
    FirebaseFirestore firebaseFirestore;
    String signState,userId,dil,ids;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileGonderilerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        kesfetDetailsArrayList = new ArrayList<>();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile");


        userLauncher();
        dilTani(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });



    }
    public void showText(String id) {
        ids = id;
        firebaseFirestore.collection("Kesfet")
                .document("Gonderi")
                .collection("Gonderiler")
                .whereEqualTo("gonderenId",id)
                .orderBy("tarih", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                        }
                        if (value != null) {
                            kesfetDetailsArrayList.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {


                                Map<String, Object> dates = snapshot.getData();
                                String downloadUrl = (String) dates.get("downloadUrl");
                                String aciklama = (String) dates.get("aciklama");
                                String parcaAdi = (String) dates.get("parcaAdi");
                                String parcaModeli = (String) dates.get("parcaModeli");
                                String puanDegeri = (String) dates.get("puanDegeri");
                                String gonderiId = (String) dates.get("gonderiId");
                                String gonderenId = (String) dates.get("gonderenId");
                                Timestamp tarih = (Timestamp) dates.get("tarih");
                                String ayrıParca = (String) dates.get("ayrıParca");
                                Long begeniSayisi = (Long) dates.get("begeniSayisi");
                                KesfetDetails kesfetDetails = new KesfetDetails(downloadUrl, parcaAdi, aciklama, puanDegeri, gonderiId, gonderenId ,parcaModeli,ayrıParca,tarih,begeniSayisi.intValue());

                                kesfetDetailsArrayList.add(kesfetDetails);

                            }

                            binding.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapterGonderiler = new AdapterGonderiler(requireContext(),kesfetDetailsArrayList);
                            binding.mRecyclerView.setAdapter(adapterGonderiler);
                            adapterGonderiler.notifyDataSetChanged();


                        }

                    }

                });




    }

    private void dilTani(final DilCallback dilCallback){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);



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



    @Override
    public void onResume() {
        super.onResume();

        try{
            binding.profilekesfetSwipeResfresh.setColorSchemeResources(R.color.secilmeyenRenk);
            binding.profilekesfetSwipeResfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    showText(ids);
                    binding.profilekesfetSwipeResfresh.setRefreshing(false);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }




    }
}