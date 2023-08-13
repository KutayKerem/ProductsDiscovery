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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.ArkListSingleton;
import com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar.ArkadaslarProfilesAdapter;
import com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar.ArkadaslarSohbetlerAdapter;
import com.app.kutaykerem.productdiscovery.databinding.FragmentArkadaslarListesiBinding;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class ArkadaslarListesiFragment extends Fragment {

   private FragmentArkadaslarListesiBinding binding;


    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DatabaseReference databaseReference;

    TextView arkSayısı;

    List<Object> arkListId;
    List<Object> arkadaslarListesiArrayListProfiles,arkadaslarListesiArrayListChats;
    List<Object> arkList = new ArrayList<>(); // En son listelerin toplandığı liste

    RecyclerView recyclerView;




    String arkadaslar;



    ImageView back;

    String userId;
    String dil;

    String signState;


    ArkadaslarProfilesAdapter arkadaslarProfilesAdapter;
    ArkadaslarSohbetlerAdapter arkadaslarSohbetlerAdapter;


    public ArkadaslarListesiFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentArkadaslarListesiBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        arkadaslarListesiArrayListChats = new ArrayList<>();
        arkadaslarListesiArrayListProfiles = new ArrayList<>();

        arkListId = new ArrayList<>();



        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();






        userLauncher();






        Arkadaslar();
        ArkadasSayısı();









        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if(dil.equals("türkce")){


                    databaseReference.child("Arkadaslar").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.getChildrenCount() >= 0){
                                binding.arkadaslarListesiProfilesText.setText("Arkadaşlar("  + snapshot.getChildrenCount() +")");
                            }else{

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {


                        }
                    });
                    binding.arkadaslarListesiSohbetlerText.setText("Sohbetler");


                }else if(dil.equals("ingilizce")){


                    databaseReference.child("Arkadaslar").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.getChildrenCount() >= 0){
                                binding.arkadaslarListesiProfilesText.setText("Friends("  + snapshot.getChildrenCount() +")");
                            }else{


                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {


                        }
                    });

                    binding.arkadaslarListesiSohbetlerText.setText("Chats");

                }



            }
        });





        binding.recyclerViewArkadaslarProfiles.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false));
        arkadaslarProfilesAdapter = new ArkadaslarProfilesAdapter(arkList,requireContext());
        binding.recyclerViewArkadaslarProfiles.setAdapter(arkadaslarProfilesAdapter);




        binding.recyclerViewArkadaslarSohbetler.setLayoutManager(new LinearLayoutManager(getActivity()));
        arkadaslarSohbetlerAdapter = new ArkadaslarSohbetlerAdapter(arkList,requireContext());
        binding.recyclerViewArkadaslarSohbetler .setAdapter(arkadaslarSohbetlerAdapter);

















    }



    public void Arkadaslar(){


        ArkListSingleton.getInstance().getArkList().clear();

        arkList.clear();




        reference.child("Arkadaslar").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot != null){
                    arkadaslar = snapshot.getKey();
                    arkList.add(arkadaslar);
                    arkadaslarProfilesAdapter.notifyDataSetChanged();
                    arkadaslarSohbetlerAdapter.notifyDataSetChanged();



                }





            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Değiştirilen arkadaşlar için yanıt verilebilir
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                arkList.remove(snapshot.getKey());
                arkadaslarProfilesAdapter.notifyDataSetChanged();
                arkadaslarSohbetlerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Arkadaşların yerini değiştiren hareketleri izleyebilirsiniz.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hata durumları için yanıt verilebilir
            }
        });






    }




    public void ArkadasSayısı() {

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





                    if (dil.equals("türkce")) {

                        reference.child("Arkadaslar").child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                               if(snapshot.getChildrenCount() > 0){
                                   binding.arkadaslarListesiNullRecycler.setVisibility(View.GONE);
                               }else{
                                   binding.arkadaslarListesiNullRecycler.setText("Henüz hiç arkadaşınız yok...");
                                   binding.arkadaslarListesiNullRecycler.setVisibility(View.VISIBLE);
                               }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });


                    } else if (dil.equals("ingilizce")) {

                        reference.child("Arkadaslar").child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                if(snapshot.getChildrenCount() > 0){
                                    binding.arkadaslarListesiNullRecycler.setVisibility(View.GONE);
                                }else{
                                    binding.arkadaslarListesiNullRecycler.setText("You don't have any friends yet...");
                                    binding.arkadaslarListesiNullRecycler.setVisibility(View.VISIBLE);

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });
                    }

                }else{

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



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
            binding.recyclerViewArkadaslarProfiles.setVisibility(View.VISIBLE);
            binding.recyclerViewArkadaslarSohbetler.setVisibility(View.VISIBLE);
            binding.arkadaslarListesiProgressBar.setVisibility(View.INVISIBLE);
            binding.arkadaslarListesiProfilesText.setVisibility(View.VISIBLE);
            binding.arkadaslarListesiSohbetlerText.setVisibility(View.VISIBLE);

        } else {
            binding.arkadaslarListesiProfilesText.setVisibility(View.INVISIBLE);
            binding.arkadaslarListesiSohbetlerText.setVisibility(View.INVISIBLE);
            binding.recyclerViewArkadaslarProfiles.setVisibility(View.INVISIBLE);
            binding.recyclerViewArkadaslarSohbetler.setVisibility(View.INVISIBLE);
            binding.arkadaslarListesiProgressBar.setVisibility(View.VISIBLE);


        }



    }

    @Override
    public void onResume() {
        super.onResume();
        networkController();
    }


}