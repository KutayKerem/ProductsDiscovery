package com.app.kutaykerem.productdiscovery.Pages;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Adaptor.AnaSayfa.AdapterAnasayfa;
import com.app.kutaykerem.productdiscovery.Models.AnasayfaDetails;
import com.app.kutaykerem.productdiscovery.Models.KullanıcıBilgileri;
import com.app.kutaykerem.productdiscovery.Models.PcNames;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentAnasayfaBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;



public class AnasayfaFragment extends Fragment  {

    public FragmentAnasayfaBinding binding;

    public ArrayList<PcNames> partsPcNamesArrayList,onerilenPcNamesArrayList;

    public List<AnasayfaDetails> allCategoryList,onerilenCategoryList = new ArrayList<>();

    public RecyclerView anasayfaRecyclerView;
    public AdapterAnasayfa adapterAnasayfa;

    public DatabaseReference databaseReference;

    public int Kontrol = 0;

    public String arkistek ;

    public FirebaseAuth userAuth;
    public String userId;
    public String dil;

    public GoogleSignInOptions googleSignInOptions;
    public GoogleSignInClient googleSignInClient;
    public FirebaseFirestore firebaseFirestore;
    public DatabaseReference getDatabaseReference = null;

    public StorageReference storageReference;

    public String signState;

    public SharedPreferences selectedDilSharedPrefences= null ;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arkistek = null;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAnasayfaBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        partsPcNamesArrayList = new ArrayList<>();
        onerilenPcNamesArrayList = new ArrayList<>();



        //networkController();


        firebaseFirestore = FirebaseFirestore.getInstance();
        getDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Profile");
        storageReference = FirebaseStorage.getInstance().getReference();







        anasayfaRecyclerView = view.findViewById(R.id.recycler_anasayfa);
        databaseReference = FirebaseDatabase.getInstance().getReference("Arkadasİstekleri");







        arkistek = null;








        userLauncher();

        getUserM(userId);

try {
        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                selectedDilSharedPrefences = requireActivity().getSharedPreferences("Dil",Context.MODE_PRIVATE);
                selectedDilSharedPrefences.edit().putString("dil",dil).apply();
            }
        });
    }catch (Exception e){

    }


        binding.anasayfaBildrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections navDirections2 = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(3);
                Navigation.findNavController(view).navigate(navDirections2);
            }
        });





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



        }else if(signState.equals("facebook")){

            userId = uid;



        }else if(signState.equals("gmail_hotmail")){
            userId = uid;


        }else{
            userId = uid;

        }



    }



    public void dilTanı(final DilCallback dilCallback){


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    try {


                    dil = dataSnapshot.child("dil").getValue(String.class);

                    dilCallback.onDilCallback(dil);
                    }catch (Exception e){

                    }

                    if(dil.equals("türkce")){


                        binding.popular.setText("Popüler");
                        binding.populer1Text.setText("Bilgisayarlar");
                        binding.populer2Text.setText("Cep Telefonları");
                        binding.populer3Text.setText("Vasıtalar");
                        binding.populer4Text.setText("Kitaplar");





                        binding.populer1Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                               NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Bilgisayar").setPos(1);
                               Navigation.findNavController(v).navigate(action);


                            }
                        });

                        binding.populer2Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                NavDirections action  = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Cep Telefonu").setPos(1);
                                Navigation.findNavController(v).navigate(action);


                            }
                        });
                        binding.populer3Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action  = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Vasıta").setPos(1);;
                                Navigation.findNavController(v).navigate(action);


                            }
                        });
                        binding.populer4Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action  = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Kitap").setPos(1);;
                                Navigation.findNavController(v).navigate(action);


                            }
                        });






                        // BottomNavigationView editing

                        binding.anasayfaWelcomeText.setText("Tekrar Hoşgeldiniz");



                        partsPcNamesArrayList.clear();
                        partsPcNamesArrayList = new ArrayList<>();
                        onerilenPcNamesArrayList = new ArrayList<>();

                        onerilenCategoryList = new ArrayList<>();
                        allCategoryList = new ArrayList<>();


                        onerilenPcNamesArrayList.add(new PcNames("En çok yorum yapılan gönderiler",R.drawable.onerilen_en_cok_yorum,0));
                        onerilenPcNamesArrayList.add(new PcNames("Yorum yapılmamış gönderiler",R.drawable.onerilen_en_az_yorum,0));
                        onerilenPcNamesArrayList.add( new PcNames("En çok beğenilen gönderiler",R.drawable.en_cok_begenilen2,0));
                        onerilenPcNamesArrayList.add( new PcNames("En yüksek puana sahip gönderiler",R.drawable.onerilen_en_cok_puan,0));
                        onerilenPcNamesArrayList.add(new PcNames("En düşük puana sahip gönderiler",R.drawable.onerilen_en_az_puan,0));
                        onerilenPcNamesArrayList.add( new PcNames("En yeni gönderiler",R.drawable.onerilen_en_yeni_gonderiler,0));
                        onerilenPcNamesArrayList.add(new PcNames("En eski gönderiler",R.drawable.onerilen_en_eski_gonderiler,0));
                        onerilenPcNamesArrayList.add(new PcNames("Benim gönderdiğim gönderiler",R.drawable.onerilen_benim_gonderilerim,0));

                        partsPcNamesArrayList.add(new PcNames("Bilgisayarlar",R.drawable.kasa,903));
                        partsPcNamesArrayList.add(new PcNames("Telefonlar",R.drawable.phone,1614));
                        partsPcNamesArrayList.add( new PcNames("Televizyonlar",R.drawable.television,43));
                        partsPcNamesArrayList.add(new PcNames("Oyun Konsolları",R.drawable.gameconsole,23));
                        partsPcNamesArrayList.add( new PcNames("Vasıtalar",R.drawable.vehicles,1500));
                        partsPcNamesArrayList.add(new PcNames("Kitaplar",R.drawable.books,42));

                        // RecyclerView içinde recyclerview
                        onerilenCategoryList.add(new AnasayfaDetails("Öneriler", onerilenPcNamesArrayList));
                        allCategoryList.add(new AnasayfaDetails("Genel Ürünler", partsPcNamesArrayList));

                        ArrayList<AnasayfaDetails> combinedList = new ArrayList<>();
                        combinedList.addAll(onerilenCategoryList);
                        combinedList.addAll(allCategoryList);

                        try{
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
                            anasayfaRecyclerView.setLayoutManager(layoutManager);
                            adapterAnasayfa = new AdapterAnasayfa(getActivity().getApplicationContext(), combinedList,"Göz at");
                            anasayfaRecyclerView.setAdapter(adapterAnasayfa);
                        }catch (Exception e){
                            e.printStackTrace();
                        }















                    }


                    else if (dil.equals("ingilizce")){

                        binding.popular.setText("Popular");
                        binding.populer1Text.setText("Computers");
                        binding.populer2Text.setText("Mobile Phones");
                        binding.populer3Text.setText("Vehicles");
                        binding.populer4Text.setText("Books");

                        binding.populer1Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Computer");
                                Navigation.findNavController(v).navigate(action);

                            }
                        });

                        binding.populer2Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action =HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Mobile Phone");
                                Navigation.findNavController(v).navigate(action);


                            }
                        });
                        binding.populer3Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action =HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Vehicle");
                                Navigation.findNavController(v).navigate(action);


                            }
                        });
                        binding.populer4Cons.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("Book");
                                Navigation.findNavController(v).navigate(action);

                            }
                        });



                        binding.anasayfaWelcomeText.setText("Welcome Back");









                        partsPcNamesArrayList.clear();
                        partsPcNamesArrayList = new ArrayList<>();
                        onerilenPcNamesArrayList = new ArrayList<>();

                        onerilenCategoryList = new ArrayList<>();
                        allCategoryList = new ArrayList<>();


                        onerilenPcNamesArrayList.add(new PcNames("Most commented posts",R.drawable.onerilen_en_cok_yorum,0));
                        onerilenPcNamesArrayList.add(new PcNames("Posts with no comments",R.drawable.onerilen_en_az_yorum,0));
                        onerilenPcNamesArrayList.add( new PcNames("Most liked posts",R.drawable.en_cok_begenilen,0));
                        onerilenPcNamesArrayList.add( new PcNames("Posts with the highest score",R.drawable.onerilen_en_cok_puan,0));
                        onerilenPcNamesArrayList.add(new PcNames("Posts with the lowest score",R.drawable.onerilen_en_az_puan,0));
                        onerilenPcNamesArrayList.add( new PcNames("Newest posts",R.drawable.onerilen_en_yeni_gonderiler,0));
                        onerilenPcNamesArrayList.add(new PcNames("Oldest posts",R.drawable.onerilen_en_eski_gonderiler,0));
                        onerilenPcNamesArrayList.add(new PcNames("My submitted posts",R.drawable.onerilen_benim_gonderilerim,0));

                        partsPcNamesArrayList.add(new PcNames("Computers",R.drawable.kasa,903));
                        partsPcNamesArrayList.add(new PcNames("Phones",R.drawable.phone,1614));
                        partsPcNamesArrayList.add( new PcNames("Televisions",R.drawable.television,43));
                        partsPcNamesArrayList.add(new PcNames("Game Consoles",R.drawable.gameconsole,23));
                        partsPcNamesArrayList.add( new PcNames("Vehicles",R.drawable.vehicles,1500));
                        partsPcNamesArrayList.add(new PcNames("Books",R.drawable.books,42));



                        // RecyclerView içinde recyclerview



                        onerilenCategoryList.add(new AnasayfaDetails("Suggestions", onerilenPcNamesArrayList));
                        allCategoryList.add(new AnasayfaDetails("General Products", partsPcNamesArrayList));
                        System.out.println(allCategoryList);

                        ArrayList<AnasayfaDetails> combinedList = new ArrayList<>();
                        combinedList.addAll(onerilenCategoryList);
                        combinedList.addAll(allCategoryList);

                        try{
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
                            anasayfaRecyclerView.setLayoutManager(layoutManager);
                            adapterAnasayfa = new AdapterAnasayfa(getActivity().getApplicationContext(), combinedList,"Browse");
                            anasayfaRecyclerView.setAdapter(adapterAnasayfa);
                        }catch (Exception e){
                        e.printStackTrace();
                        }




                        try{
                            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {




                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }



                    }else{

                    }


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






    private void getUserM(String userId){


        if (firebaseFirestore.collection("Profiles").document("Resimler").collection(userId) != null) {

            firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        Log.d("ERROR: ", error.getLocalizedMessage());
                    }

                    if (value != null && !value.isEmpty()) {

                        try {



                        DocumentSnapshot snapshot = value.getDocuments().get(value.size() - 1);
                        String profile = (String) snapshot.get("ImageProfile");



                        Uri uri = Uri.parse(profile);

                        RequestOptions requestOptions = new RequestOptions()
                                .format(DecodeFormat.PREFER_RGB_565)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .encodeQuality(50);

                        Glide.with(requireContext())
                                .load(uri)
                                .apply(requestOptions)
                                .into(binding.anayasayfaProfile);

                        }catch (Exception e){
                            Log.d("ERROR:",e.getMessage());
                        }


                    }


                }



            });

        }

try {
        DatabaseReference reference = getDatabaseReference.child(userId);

        if (reference != null) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.getValue() != null) {
                        KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);

                        if (kullanıcıBilgileri == null) {
                            binding.anasayfaUsernameText.setText("user name");
                        } else if (kullanıcıBilgileri != null) {

                            if (kullanıcıBilgileri.kullanıcıAdı != null) {
                                binding.anasayfaUsernameText.setText(kullanıcıBilgileri.kullanıcıAdı);
                            } else if (kullanıcıBilgileri.kullanıcıAdı == null) {
                                binding.anasayfaUsernameText.setText("user name");
                            }

                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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
                    binding.anasayfaBildrim.setImageResource(R.drawable.notification);

                } else {
                    binding.anasayfaBildrim.setImageResource(R.drawable.bildirim);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }






        if(Kontrol == 1){

        }else if (Kontrol == 2){

        }



    }

    public void networkController()  {


        if (isConnected()) {
            binding.anaSayfaProgresBar.setVisibility(View.INVISIBLE);
            binding.anasayfaLinerLayout.setVisibility(View.VISIBLE);
            binding.linearLayout.setVisibility(View.VISIBLE);
            binding.linearLayout2.setVisibility(View.VISIBLE);
            binding.recyclerAnasayfa.setVisibility(View.VISIBLE);
            binding.popular.setVisibility(View.VISIBLE);

            binding.populer1Cons.setVisibility(View.INVISIBLE);
            binding.populer2Cons.setVisibility(View.INVISIBLE);
            binding.populer3Cons.setVisibility(View.INVISIBLE);
            binding.populer4Cons.setVisibility(View.INVISIBLE);

            if(binding.popular.getVisibility() == View.VISIBLE){
                binding.populer1Cons.setVisibility(View.VISIBLE);
                binding.populer2Cons.setVisibility(View.VISIBLE);
                binding.populer3Cons.setVisibility(View.VISIBLE);
                binding.populer4Cons.setVisibility(View.VISIBLE);
            }

         /*
            CountDownTimer countDownTimer = new CountDownTimer(1000,900) {
                @Override
                public void onTick(long millisUntilFinished) {

                    binding.anaSayfaProgresBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    binding.anaSayfaProgresBar.setVisibility(View.INVISIBLE);
                    binding.anasayfaLinerLayout.setVisibility(View.VISIBLE);
                    binding.linearLayout.setVisibility(View.VISIBLE);
                    binding.linearLayout2.setVisibility(View.VISIBLE);
                    binding.recyclerAnasayfa.setVisibility(View.VISIBLE);
                    binding.popular.setVisibility(View.VISIBLE);

                }
            }.start();
            */

        } else {


            binding.anasayfaLinerLayout.setVisibility(View.INVISIBLE);
            binding.linearLayout.setVisibility(View.INVISIBLE);
            binding.linearLayout2.setVisibility(View.INVISIBLE);
            binding.popular.setVisibility(View.INVISIBLE);
            binding.recyclerAnasayfa.setVisibility(View.INVISIBLE);
            binding.populer1Cons.setVisibility(View.INVISIBLE);
            binding.populer2Cons.setVisibility(View.INVISIBLE);
            binding.populer3Cons.setVisibility(View.INVISIBLE);
            binding.populer4Cons.setVisibility(View.INVISIBLE);
            binding.anaSayfaProgresBar.setVisibility(View.VISIBLE);




        }



    }


    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onResume() {
        super.onResume();
        bell();

        networkController();
        userLauncher();
    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStart() {
        super.onStart();

    }
}

