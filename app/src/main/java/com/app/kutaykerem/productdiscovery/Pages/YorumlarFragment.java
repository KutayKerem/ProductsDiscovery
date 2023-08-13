package com.app.kutaykerem.productdiscovery.Pages;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Adaptor.Kesfet.YorumlarAdaptor;
import com.app.kutaykerem.productdiscovery.Models.YorumlarDetails;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentYorumlarBinding;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class YorumlarFragment extends Fragment {

    private FragmentYorumlarBinding binding;


    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    String gonderiId, gonderenId;
    String yorum;

    DatabaseReference databaseReference = null;
    ArrayList<YorumlarDetails> yorumlarDetailsArrayList;

    YorumlarAdaptor yorumlarAdaptor;

    TextView yorumEkle,yorumlar;
    String kullanıcıAdı, profile;
    String yrm;

    ImageView gonder;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;

    String userId;
    String dil;

    String signState;
    String fromWhere,arananParca,gelenİd;
    Boolean silinen;

    public YorumlarFragment() {
       
    }

   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentYorumlarBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
        appCompatActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        recyclerView = view.findViewById(R.id.recyclerYorumlar);


        yorumlarDetailsArrayList = new ArrayList<>();

        yorumEkle = view.findViewById(R.id.yorumEkle);
        gonder = view.findViewById(R.id.yorumGonder);
        yorumlar = view.findViewById(R.id.yorumlar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();





        userLauncher();

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("Profile");






        if(getArguments() != null){
            gonderiId = YorumlarFragmentArgs.fromBundle(getArguments()).getGonderiId();
            gonderenId = userId;



            fromWhere =YorumlarFragmentArgs.fromBundle(getArguments()).getFromWhere();
             gelenİd = YorumlarFragmentArgs.fromBundle(getArguments()).getGelenId();
            arananParca = YorumlarFragmentArgs.fromBundle(getArguments()).getArananParca();
            silinen = YorumlarFragmentArgs.fromBundle(getArguments()).getSilinen();

            linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setHasFixedSize(true);
            yorumlarAdaptor = new YorumlarAdaptor(yorumlarDetailsArrayList,requireContext(),fromWhere,arananParca,gelenİd,requireView());
            recyclerView.setAdapter(yorumlarAdaptor);





            if (fromWhere.equals("anlik_düsünce")){

                begeniSayisiAnlikDusunceler(gonderiId);



            }else if(fromWhere.equals("kesfet")){
                begeniSayisiGonderiler(gonderiId);



            }




        }







        gonder  = view.findViewById(R.id.yorumGonder);
        gonder.setBackground(null);


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                resumeBack(fromWhere,arananParca,gelenİd);

            }
        });
        binding.yorumlarGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeBack(fromWhere,arananParca,gelenİd);
            }
        });








        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Profile");
        DatabaseReference reference = databaseReference.child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    kullanıcıAdı = snapshot.child("kullanıcıAdı").getValue().toString();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                }

                if (value != null) {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();

                        profile = (String) data.get("ImageProfile");


                    }


                }
            }


        });

























        yorumEkle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               yrm = yorumEkle.getText().toString().trim();


            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

















        getYorumlar();











        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                yorumGonder(view);

            }
        });




        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });














    }



    public void resumeBack(String state,String arananParca,String gelenİd){
       
        if (state.equals("anlik_düsünce")){
            YorumlarFragmentDirections.ActionYorumlarFragmentToHomeFragmentBottomNav action = YorumlarFragmentDirections.actionYorumlarFragmentToHomeFragmentBottomNav().setPos(1);
            action.setArananParca(arananParca);
            action.setDiscoveryTabPos(1);
            Navigation.findNavController(requireView()).navigate(action);
        }
        else if (state.equals("kesfet")){
            YorumlarFragmentDirections.ActionYorumlarFragmentToHomeFragmentBottomNav action = YorumlarFragmentDirections.actionYorumlarFragmentToHomeFragmentBottomNav().setPos(1);
            action.setArananParca(arananParca);
            action.setDiscoveryTabPos(0);
            Navigation.findNavController(requireView()).navigate(action);
        }if (gelenİd != null){
               if(state.equals("profile_gonderiler")){
                YorumlarFragmentDirections.ActionYorumlarFragmentToHomeFragmentBottomNav action = YorumlarFragmentDirections.actionYorumlarFragmentToHomeFragmentBottomNav().setPos(4);
                action.setGonderen(gelenİd);
                Navigation.findNavController(requireView()).navigate(action);
            }else if (state.equals("profile_anlik_dusunceler")){
               YorumlarFragmentDirections.ActionYorumlarFragmentToHomeFragmentBottomNav action =YorumlarFragmentDirections.actionYorumlarFragmentToHomeFragmentBottomNav().setPos(4);
                action.setGonderen(gelenİd);
                Navigation.findNavController(requireView()).navigate(action);
            }
        }





    }
    private void begeniSayisiGonderiler(String gonderiId){
        try {
        DatabaseReference begeniSayisiDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("Gonderi_Begeniler").child(gonderiId);

        begeniSayisiDatabaseYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.yorumlarBegeniSayisi.setText(String.valueOf(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error: ",error.getMessage());
            }
        });
    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }


    }

    private void begeniSayisiAnlikDusunceler(String gonderiId){

        try {
        DatabaseReference begeniSayisiDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("AnlikDusunce_Begeniler").child(gonderiId);

        begeniSayisiDatabaseYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.yorumlarBegeniSayisi.setText(String.valueOf(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error: ",error.getMessage());
            }
        });

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
            System.out.println("Kullanıcı oturum açmamıştır");
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
    public void dilTanı(final DilCallback dilCallback){

        try {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);

                    if (dil.equals("türkce")) {
                        binding.yorumEkle.setHint("Yorum");
                        yorumlar.setText("Yorumlar");
                    } else if (dil.equals("ingilizce")) {
                        yorumlar.setText("Comments");
                        binding.yorumEkle.setHint("Comment");
                    }

                   dilCallback.onDilCallback(dil);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }

    }

    public interface DilCallback {
        void onDilCallback(String dil);
    }






    public void getYorumlar(){



        try {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId).child("Gonderiler");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                yorumlarDetailsArrayList.clear();

                if(dataSnapshot != null){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        YorumlarDetails yorumlarDetails = snapshot.getValue(YorumlarDetails.class);
                        yorumlarDetailsArrayList.add(yorumlarDetails);


                    }



                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(yorumlarAdaptor.getItemCount() - 1);
                        }
                    });


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }



    }


    public void yorumGonder(View view) {
        try {
        yorum = yorumEkle.getText().toString();

        if(yorum.isEmpty()){

        }

        else if(!yrm.isEmpty()){
            UUID uuid = UUID.randomUUID();
            String YorumId = System.currentTimeMillis() + uuid.toString();

            HashMap<String, Object> data = new HashMap<>();
            data.put("gonderiId", gonderiId);
            data.put("gonderenId", gonderenId);
            data.put("kullanıcıAdı", kullanıcıAdı);
            data.put("profile", profile);
            data.put("yorum", yorum);
            data.put("yorumId",YorumId);
            Date startTime = new Date();
            data.put("tarih", startTime.toString());



            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId).child("Gonderiler");

            databaseReference.push().setValue(data);
            binding.yorumEkle.setText("");


        }



    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
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
            binding.yorumGonder.setEnabled(true);

        } else {
            binding.yorumGonder.setEnabled(false);



        }



    }

    @Override
    public void onResume() {
        super.onResume();
        networkController();

/*
        binding.yorumlarSwipeResfresh.setColorSchemeResources(R.color.secilmeyenRenk);

        binding.yorumlarSwipeResfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getYorumlar();
                binding.yorumlarSwipeResfresh.setRefreshing(false);
            }
        });
        */

    }



}