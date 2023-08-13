package com.app.kutaykerem.productdiscovery.Pages;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.app.kutaykerem.productdiscovery.Adaptor.Profile.ProfileAdapterViewPager;
import com.app.kutaykerem.productdiscovery.Models.GetDate;
import com.app.kutaykerem.productdiscovery.Models.GetDateStart;
import com.app.kutaykerem.productdiscovery.Models.KullanıcıBilgileri;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.Sign.UserLogin;
import com.app.kutaykerem.productdiscovery.databinding.FragmentKullaniciProfileBinding;
import com.app.kutaykerem.productdiscovery.Service.RefreshDatabase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class KullaniciProfileFragment extends Fragment {




    private FragmentKullaniciProfileBinding binding;


    ArrayList<KullanıcıBilgileri> kullanıcıBilgileriArrayList;




    List<String> arkList;
    Button düzenle,sohbetButton,cıkısButton;



    String düzenleAdi,cikisAdi,arkadasliktancikaradi,arkadasekleadi,arkadasistekiptaladi,sohbetadi;


    FirebaseAuth auth;
    DatabaseReference databaseReference = null;
    FirebaseDatabase database = null;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    FirebaseAuth userAuth;

    DatabaseReference arkreference;
    DatabaseReference arkadaslarreference,onlineKontrolReference,kullanilanDillerReference;


    String farklıKullanıcı;
    String arkistekKontrol = "";
    String userId;
    String dil;

    Button arkadasEkle,arkadasİstegiİptalEt,arkadastanCıkar;






    ArrayList<KullanıcıBilgileri> kullanıcıBilgileris;




    HashMap<String,Object> dataOnlineOfline;
    int butonKontrol = 0;
    int butonKontrol2 = 0;



    Boolean stateAdapter= false;


    TextView kullanıcıAdıText,biyografiText,katılmaTarihiText;


    CircleImageView circleImageProfile;


    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    String signState;

    Data refresh_data ;

    public KullaniciProfileFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    binding = FragmentKullaniciProfileBinding.inflate(inflater,container,false);
    return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        düzenle = (Button) view.findViewById(R.id.düzenle);
        sohbetButton = (Button) view.findViewById(R.id.sohbetButton);
        cıkısButton = (Button) view.findViewById(R.id.cıkısButton);
        arkadastanCıkar = view.findViewById(R.id.arkcikar);




        kullanıcıBilgileriArrayList = new ArrayList<>();
        kullanıcıBilgileris = new ArrayList<>();

        kullanıcıAdıText = view.findViewById(R.id.profilekullaniciadi);
        biyografiText = view.findViewById(R.id.profilebiyografi);

        katılmaTarihiText = view.findViewById(R.id.katilmaTarihi);



        circleImageProfile = view.findViewById(R.id.imageProfile);

        arkadasEkle = view.findViewById(R.id.profileArkadasEkle);
        arkadasİstegiİptalEt = view.findViewById(R.id.arkadasİstegiİptalEt);




        arkList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Profile");
        arkadaslarreference = FirebaseDatabase.getInstance().getReference();
        onlineKontrolReference = FirebaseDatabase.getInstance().getReference();
        kullanilanDillerReference = FirebaseDatabase.getInstance().getReference();












        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        arkreference = FirebaseDatabase.getInstance().getReference("Arkadasİstekleri");


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions);









        /*
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.linearLayoutPost.getLayoutParams();

        int leftMargin = 20; // Sol kenar boşluğu
        int topMargin = 30; // Üst kenar boşluğu
        int rightMargin = 20; // Sağ kenar boşluğu
        int bottomMargin = 30; // Alt kenar boşluğu

        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        binding.linearLayoutPost.setLayoutParams(params);
*/
















        if(getArguments() !=  null){

            farklıKullanıcı = KullaniciProfileFragmentArgs.fromBundle(getArguments()).getGonderen();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            userLauncher();




            if(farklıKullanıcı.equals(userId) || farklıKullanıcı.equals("null")){
                arkadastanCıkar.setVisibility(View.GONE);
                butonKontrol2 = 1;
                cıkısButton.setVisibility(View.VISIBLE);

                initProfileStateAdapter(userId);
                initProfileMovementSize(userId);


                dilTanı(new DilCallback() {
                    @Override
                    public void onDilCallback(String dil) {

                    }
                });
                getData();
                userkatılmaTarihi();


            } else{

                dilTanı(new DilCallback() {
                    @Override
                    public void onDilCallback(String dil) {

                    }
                });
                userLauncher();
                farklıK(farklıKullanıcı);
                farklıKullanıcıkatılmaTarihi(farklıKullanıcı);

                initProfileStateAdapter(farklıKullanıcı);
                initProfileMovementSize(farklıKullanıcı);

                onResume();


                arkreference.child(farklıKullanıcı).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userId)){

                            arkistekKontrol = "istek";
                            snapshot.child(userId).child("tip").getValue().toString();

                            arkadasEkle.setVisibility(View.GONE);
                            arkadasİstegiİptalEt.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                arkadaslarreference.child("Arkadaslar").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(farklıKullanıcı)){
                            arkistekKontrol = "arkadas";
                            arkadasEkle.setVisibility(View.GONE);
                            arkadasİstegiİptalEt.setVisibility(View.GONE);
                            arkadastanCıkar.setVisibility(View.VISIBLE);
                            butonKontrol = 4;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                arkadasEkle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(arkistekKontrol.equals("istek")){
                            arkadasİstegiİptalEt(farklıKullanıcı);
                        }else if (arkistekKontrol.equals("arkadas")){
                            arkadaslıktanCıkar(farklıKullanıcı);
                        }else{
                            arkadasEkle(farklıKullanıcı);
                        }


                    }
                });
                arkadasİstegiİptalEt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        arkadasİstegiİptalEt(farklıKullanıcı);
                    }
                });

                arkadastanCıkar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(arkistekKontrol.equals("istek")){
                            arkadasİstegiİptalEt(farklıKullanıcı);
                        }else if (arkistekKontrol.equals("arkadas")){
                            arkadaslıktanCıkar(farklıKullanıcı);
                        }else{
                            arkadasEkle(farklıKullanıcı);
                        }
                    }
                });




            }

            if(farklıKullanıcı.equals("null")){
                butonKontrol2 = 1;
                cıkısButton.setVisibility(View.VISIBLE);
                arkadastanCıkar.setVisibility(View.INVISIBLE);
                getData();
                dilTanı(new DilCallback() {
                   @Override
                   public void onDilCallback(String dil) {

                   }
               });
                userkatılmaTarihi();

                initProfileStateAdapter(userId);
                initProfileMovementSize(userId);
            }

        }





        if(butonKontrol2 != 1){



            if(butonKontrol == 0){
                arkadasEkle.setVisibility(View.VISIBLE);
                arkadasİstegiİptalEt.setVisibility(View.INVISIBLE);
                arkadastanCıkar.setVisibility(View.INVISIBLE);
            }else if(butonKontrol == 1){
                arkadasEkle.setVisibility(View.INVISIBLE);
                arkadasİstegiİptalEt.setVisibility(View.VISIBLE);
                arkadastanCıkar.setVisibility(View.INVISIBLE);
            }else if (butonKontrol == 2){
                arkadasEkle.setVisibility(View.VISIBLE);
                arkadasİstegiİptalEt.setVisibility(View.INVISIBLE);
                arkadastanCıkar.setVisibility(View.INVISIBLE);
            }else if (butonKontrol == 4){
                arkadasEkle.setVisibility(View.INVISIBLE);
                arkadasİstegiİptalEt.setVisibility(View.INVISIBLE);
                arkadastanCıkar.setVisibility(View.VISIBLE);
            }
        }


        else{

            initProfileStateAdapter(userId);
            initProfileMovementSize(userId);
            getData();
           dilTanı(new DilCallback() {
               @Override
               public void onDilCallback(String dil) {

               }
           });
        }


        düzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToDuzenleFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
        sohbetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action =HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToSohbetlerFragment().setKullaniciId(farklıKullanıcı);
                Navigation.findNavController(view).navigate(action);
            }
        });












        userLauncher();

        networkController();



    }



    // Gönderiler ve Anlık Düşünceler

    @SuppressLint("WrongConstant")
    private void initProfileStateAdapter(String currentId){


        /*
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(binding.linearSheet);
        bottomSheetBehavior.setPeekHeight(550);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
*/

        ProfileAdapterViewPager fragAdapter = new ProfileAdapterViewPager(requireActivity());
        binding.profileViewpager.setAdapter(fragAdapter);




        new TabLayoutMediator(binding.profileTablayout, binding.profileViewpager, (tab, position) -> {
            dilTanı(dil -> {
                if (position == 0) {
                    tab.setText(dil.equals("türkce") ? "Gönderiler" : "Posts");
                } else if (position == 1) {
                    tab.setText(dil.equals("türkce") ? "İfade Akışları" : "Expression Stream");
                }
            });
        }).attach();



        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE",Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("night",false);

        if(nightMode){
            binding.profileTablayout.setTabTextColors(ContextCompat.getColor(requireContext(),R.color.black),ContextCompat.getColor(requireContext(),R.color.darkeyGray));
        }else{
            binding.profileTablayout.setTabTextColors(ContextCompat.getColor(requireContext(),R.color.white),ContextCompat.getColor(requireContext(),R.color.darkeyGray));
        }
        binding.profileTablayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(),R.color.secilenAcikRenk));




        try {


        binding.profileViewpager.post(() -> {
            // İlk açıldığında "Gönderiler" sekmesini seçili hâle getir ve ilgili fragmenti kontrol et
            binding.profileViewpager.setCurrentItem(0,false);
            ProfileGonderilerFragment frag = (ProfileGonderilerFragment) getCurrentFrag();
            if (frag != null) {
                frag.showText(currentId);
            }



            binding.profileTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();

                    if(position == 0){

                    }else if(position == 1){
                        stateAdapter = true;
                        binding.profileViewpager.post(() -> {
                            binding.profileViewpager.setCurrentItem(1,false);
                            ProfileAnlikDusuncelerFragment profileAnlikDusuncelerFragment = (ProfileAnlikDusuncelerFragment) getCurrentFrag();
                            profileAnlikDusuncelerFragment.showText(currentId);
                            stateAdapter = false;
                        });
                    }


                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


            binding.profileViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    if(position == 0){
                        binding.profileViewpager.setCurrentItem(0,false);
                        ProfileGonderilerFragment frag = (ProfileGonderilerFragment) getCurrentFrag();
                        if (frag != null) {
                            frag.showText(currentId);
                        }
                    }else if(position == 1){
                        if(stateAdapter == false){
                            binding.profileViewpager.setCurrentItem(1,false);
                            ProfileAnlikDusuncelerFragment profileAnlikDusuncelerFragment = (ProfileAnlikDusuncelerFragment) getCurrentFrag();
                            profileAnlikDusuncelerFragment.showText(currentId);
                        }


                    }

                }
            });



        });

        }catch (Exception e){
                Log.d("Error:",e.getMessage());
        }







    }




    private void initProfileMovementSize(String currentId){



        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("gonderenId",currentId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
                }
                if (value != null) {
                    int postCount = value.size(); // Gönderi sayısını alın
                    String postCountSize = String.valueOf(postCount); // Metin dizesine dönüştürün
                    binding.profilePostSize.setText(postCountSize);

                }

            }

        });



        firebaseFirestore.collection("Kesfet").document("AnlıkDüsünce").collection("Gonderiler").whereEqualTo("gonderenId",currentId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
                }
                if (value != null) {
                    int postCount = value.size(); // Gönderi sayısını alın
                    String anlikDusunceCountText = String.valueOf(postCount); // Metin dizesine dönüştürün
                    binding.profileAnlikdSNceSize.setText(anlikDusunceCountText);

                }

            }

        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int yorumSayisiAnlikDusunceler = 0; // anlık düşüncelerdeki yorumların toplam sayısını tutmak için bir değişken
                int yorumSayisiGonderiler = 0; // gönderilerdeki yorumların toplam sayısını tutmak için bir değişken
                if (dataSnapshot.exists()) {

                    for (DataSnapshot gonderiSnapshot : dataSnapshot.getChildren()) {
                        String gonderiId = gonderiSnapshot.getKey();

                        if (gonderiSnapshot.child("AnlikDusunceler").exists()) {
                            for (DataSnapshot yorumSnapshot : gonderiSnapshot.child("AnlikDusunceler").getChildren()) {
                                // Gerekli diğer işlemleri gerçekleştirin
                                String yorumYapanId = yorumSnapshot.child("gonderenId").getValue(String.class);
                                if (yorumYapanId != null && yorumYapanId.equals(currentId)) {
                                    yorumSayisiAnlikDusunceler++; // anlık düşüncelerdeki yorumları say
                                }
                            }
                        }

                        if (gonderiSnapshot.child("Gonderiler").exists()) {
                            for (DataSnapshot yorumSnapshot : gonderiSnapshot.child("Gonderiler").getChildren()) {
                                // Gerekli diğer işlemleri gerçekleştirin
                                String yorumYapanId = yorumSnapshot.child("gonderenId").getValue(String.class);
                                if (yorumYapanId != null && yorumYapanId.equals(currentId)) {
                                    yorumSayisiGonderiler++; // gönderilerdeki yorumları say
                                }
                            }
                        }
                    }

                }

                // İlgili değerleri kullanarak gerekli işlemleri yapabilirsiniz
                binding.profileYorumSize.setText(String.valueOf(yorumSayisiGonderiler + yorumSayisiAnlikDusunceler));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR: ",error.getMessage());
            }
        });









    }



    private Fragment getCurrentFrag() {
        try {
            return requireActivity().getSupportFragmentManager().findFragmentByTag("f" + binding.profileViewpager.getCurrentItem());
        }catch (Exception  e){
            Log.d("ERROR: ",e.getMessage());
        }

        return null;
    }








    private void userLauncher(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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


                registerUser("google",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail(),firebaseAuth);

            }else if(signState.equals("facebook")){

                registerUser("facebook",firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl(),firebaseUser.getEmail(),firebaseAuth);

            }else if(signState.equals("gmail_hotmail")){

                registerUser("gmail_hotmail",firebaseUser.getUid(),"User name",null,firebaseUser.getEmail(),firebaseAuth);

            }



        } else {
            // Kullanıcı oturum açmamıştır

        }
    }




    private void registerUser(String signState, String uid,String userName, Uri userProfile,String email,FirebaseAuth auth){

        if(signState.equals("google")){

            userId = uid;
            userAuth = auth;


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
                        reference.push().setValue(data);
                    }
                }
            });


        }else if(signState.equals("facebook")){

            userId = uid;
            userAuth = auth;
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
                        reference.push().setValue(data);
                    }
                }
            });


        }else if(signState.equals("gmail_hotmail")){
            userId = uid;
            userAuth = auth;
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
                        reference.push().setValue(data);


                    }
                }
            });
        }


        else{

        }

    }










    private void goSıgnGoogle() {

        OnlineOflineDurum(false);
        userAuth.signOut();
        googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                OnlineOflineDurum(false);
                Intent intent = new Intent(getActivity(), UserLogin.class);
                startActivity(intent);
            }
        });

    }
    public void dilTanı(final DilCallback dilCallback){

       userLauncher();

       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);
                    dilCallback.onDilCallback(dil);




                    if(dil.equals("türkce")){



                        düzenleAdi = "DÜZENLE";
                        cikisAdi = "ÇIKIŞ";
                        arkadasekleadi = "Arkadaş İsteği Gönder";
                        arkadasistekiptaladi = "arkadaşlık isteğini iptal et";
                        arkadasliktancikaradi = "arkadaş listesinden çıkar";
                        sohbetadi = "SOHBET";
                        binding.biyografi.setText("   Biyografi");
                        binding.profilePostText.setText("Gönderi");



                        binding.profileAnlikdSNceText.setText("İfade Akışları");
                        binding.profileYorumText.setText("Yorum");
                        binding.profileProfileText.setText("Profil");

                        arkadasEkle.setText(arkadasekleadi);
                        arkadasİstegiİptalEt.setText(arkadasistekiptaladi);
                        arkadastanCıkar.setText(arkadasliktancikaradi);
                        sohbetButton.setText(sohbetadi);
                        cıkısButton.setText(cikisAdi);
                        düzenle.setText(düzenleAdi);



                        cıkısButton.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onClick(View view) {



                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("Çıkış");
                                alertDialog.setMessage("Çıkış yapmak istediğinizden emin misiniz?");
                                alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            OnlineOflineDurum(false);
                                            userAuth.signOut();
                                            OnlineOflineDurum(false);
                                            Intent intent = new Intent(getActivity(), UserLogin.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            OnlineOflineDurum(false);

                                            refresh_data =  new Data.Builder().putBoolean("durum",false).putString("userId",userId).build();
                                            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class).setInitialDelay(5,TimeUnit.MINUTES).setInputData(refresh_data).build();
                                            WorkManager.getInstance(requireContext()).enqueue(workRequest);
                                            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workRequest.getId()).observe(requireActivity(), new Observer<WorkInfo>() {
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
                                            Log.d("ERROR: ",e.getLocalizedMessage());
                                        }


                                    }

                                });
                                alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                AlertDialog customDialogs = alertDialog.create();
                                customDialogs.show();

                                customDialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.secilmeyenRenk);
                                customDialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.secilmeyenRenk);

                            }




                        });





                    }else if (dil.equals("ingilizce")){




                        düzenleAdi = "edıt";
                        cikisAdi = "EXIT";
                        arkadasekleadi = "Send frendship request";
                        arkadasistekiptaladi = "cancel friend request";
                        arkadasliktancikaradi = "remove from friend list";
                        sohbetadi = "CHAT";
                        binding.biyografi.setText("   Biography");
                        binding.profileProfileText.setText("Profile");
                        binding.profilePostText.setText("Post");

                        binding.profileAnlikdSNceText.setText("Expr Stream");


                        binding.profileYorumText.setText("Comment");


                        arkadasEkle.setText(arkadasekleadi);
                        arkadasİstegiİptalEt.setText(arkadasistekiptaladi);
                        arkadastanCıkar.setText(arkadasliktancikaradi);
                        sohbetButton.setText(sohbetadi);
                        cıkısButton.setText(cikisAdi);
                        düzenle.setText(düzenleAdi);



                        cıkısButton.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onClick(View view) {



                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle("Exit");
                                alertDialog.setMessage("Are you sure you want to log out?");
                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            OnlineOflineDurum(false);
                                            userAuth.signOut();
                                            OnlineOflineDurum(false);
                                            Intent intent = new Intent(getActivity(), UserLogin.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            OnlineOflineDurum(false);

                                            refresh_data =  new Data.Builder().putBoolean("durum",false).putString("userId",userId).build();
                                            WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class).setInitialDelay(5,TimeUnit.MINUTES).setInputData(refresh_data).build();
                                            WorkManager.getInstance(requireContext()).enqueue(workRequest);
                                            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workRequest.getId()).observe(requireActivity(), new Observer<WorkInfo>() {
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
                                            Log.d("ERROR: ",e.getLocalizedMessage());
                                        }

                                    }

                                });
                                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                AlertDialog customDialogs = alertDialog.create();
                                customDialogs.show();

                                customDialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.secilmeyenRenk);
                                customDialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.secilmeyenRenk);



                            }

                        });




                    }




                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR: ",error.getMessage());
            }
        });
    }

    public interface DilCallback {
        void onDilCallback(String dil);
    }




    public void getData() {





        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        userLauncher();





        arkadasEkle.setVisibility(View.INVISIBLE);
        arkadasİstegiİptalEt.setVisibility(View.INVISIBLE);
        sohbetButton.setVisibility(View.INVISIBLE);
        düzenle.setVisibility(View.VISIBLE);
        cıkısButton.setVisibility(View.VISIBLE);
        arkadastanCıkar.setVisibility(View.INVISIBLE);
        binding.removeProfile.setVisibility(View.VISIBLE);

        String yol = userId;


        if (firebaseFirestore.collection("Profiles").document("Resimler").collection(yol) != null) {

            firebaseFirestore.collection("Profiles").document("Resimler").collection(yol).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }

                    if (value != null && !value.isEmpty()) {
                        DocumentSnapshot snapshot = value.getDocuments().get(value.size() - 1);
                        String profile = (String) snapshot.get("ImageProfile");



                            try {


                                Uri uri = Uri.parse(profile);

                                RequestOptions requestOptions = new RequestOptions()
                                        .format(DecodeFormat.PREFER_RGB_565)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .encodeQuality(50);

                                Glide.with(requireContext())
                                        .load(uri)
                                        .apply(requestOptions)
                                        .into(binding.imageProfile);

                            }catch (Exception e){
                                Log.d("ERROR:",e.getMessage());
                            }




                    }else
                    {




                    }


                }

                ;

            });

        }


        DatabaseReference reference = databaseReference.child(userId);

        if (reference != null) {



            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.getValue() != null) {
                        KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);
                        kullanıcıBilgileris.add(kullanıcıBilgileri);

                        if (kullanıcıBilgileri == null) {
                            biyografiText.setText("...");
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                            layoutParams.setMargins(20,15,0,0);
                            binding.profilekullaniciadi.setLayoutParams(layoutParams);
                            kullanıcıAdıText.setText("User name");
                        } else if (kullanıcıBilgileri != null) {

                            if (kullanıcıBilgileri.kullanıcıAdı != null)
                            {
                                String kd = kullanıcıBilgileri.kullanıcıAdı;
                                int krs = kd.length();

                                if(krs <= 10){
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                                    layoutParams.startToStart = binding.linearProfile.getId();
                                    layoutParams.endToEnd = binding.linearProfile.getId();
                                    binding.profilekullaniciadi.setLayoutParams(layoutParams);

                                    kullanıcıAdıText.setText(kullanıcıBilgileri.kullanıcıAdı);
                                }else{
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                                    layoutParams.setMargins(20,15,0,0);
                                    binding.profilekullaniciadi.setLayoutParams(layoutParams);

                                    kullanıcıAdıText.setText(kullanıcıBilgileri.kullanıcıAdı);


                                }






                            } else if (kullanıcıBilgileri.kullanıcıAdı == null) {
                                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                                layoutParams.setMargins(20,15,0,0);
                                binding.profilekullaniciadi.setLayoutParams(layoutParams);
                                kullanıcıAdıText.setText("User name");
                            }

                            if (kullanıcıBilgileri.biyografi != null) {
                                biyografiText.setText(kullanıcıBilgileri.biyografi);
                            } else if (kullanıcıBilgileri.biyografi == null) {
                                biyografiText.setText("...");
                            }

                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR: ",error.getMessage());
                }
            });
        } else {

            databaseReference.child(userId).child("kullanıcıAdı").setValue("username");
            databaseReference.child(userId).child("biyografi").setValue(null);
        }



        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

                if(dil.equals("türkce")){
                    binding.removeProfile.setVisibility(View.GONE); // Varsayılan olarak gizle

                    firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Profil fotoğrafı yok
                                binding.removeProfile.setVisibility(View.GONE); // Gizle
                            } else {
                                firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                        if(value != null){

                                            for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                                                Boolean isDefault = (Boolean) documentSnapshot.get("default");

                                                if(isDefault != null){
                                                    if(isDefault.equals(true)){
                                                        binding.removeProfile.setVisibility(View.GONE);
                                                    }else{
                                                        binding.removeProfile.setVisibility(View.VISIBLE); // Göster
                                                    }
                                                }else{
                                                    binding.removeProfile.setVisibility(View.VISIBLE); // Göster
                                                }


                                            }

                                        }

                                    }
                                });




                            }
                        }
                    });





                    binding.removeProfile.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Profili Kaldır");
                            alertDialog.setMessage("Profil fotoğrafınızı kaldırmak istediğinizden emin misiniz?");
                            alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).get().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().delete();

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
                                                                                        Log.d("ERROR: ",e.getLocalizedMessage());
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
                                                                                        Log.d("ERROR: ",e.getLocalizedMessage());
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("ERROR: ",e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });



                                                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(userId);
                                                    Navigation.findNavController(getView()).navigate(action);

                                                }
                                            });



                                }

                            });
                            alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
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
                }else if(dil.equals("ingilizce")){
                    binding.removeProfile.setVisibility(View.GONE); // Varsayılan olarak gizle

                    firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Profil fotoğrafı yok
                                binding.removeProfile.setVisibility(View.GONE); // Gizle
                            } else {
                                // Profil fotoğrafı var
                                firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                        if(value != null){

                                            for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                                                Boolean isDefault = (Boolean) documentSnapshot.get("default");

                                                if(isDefault!= null){
                                                    if(isDefault.equals(true)){
                                                        binding.removeProfile.setVisibility(View.GONE);
                                                    }else{
                                                        binding.removeProfile.setVisibility(View.VISIBLE); // Göster
                                                    }
                                                }else{
                                                    binding.removeProfile.setVisibility(View.VISIBLE); // Gösterv
                                                }



                                            }

                                        }

                                    }
                                });
                            }
                        } else {
                            // Hata durumunda işlemler
                        }
                    });

                    binding.removeProfile.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onClick(View v) {


                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Remove Profile");
                            alertDialog.setMessage("Are you sure you want to remove your profile photo?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).get().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().delete();
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
                                                                                        Log.d("ERROR: ",e.getLocalizedMessage());
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
                                                                                        Log.d("ERROR: ",e.getLocalizedMessage());
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("ERROR: ",e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
















                                                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(userId);
                                                    Navigation.findNavController(getView()).navigate(action);
                                                }
                                            });

                                }

                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
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


                }
            }
        });









    }



    public void farklıK(String farklıKullanıcı){





        arkadasEkle.setVisibility(View.VISIBLE);
        arkadasİstegiİptalEt.setVisibility(View.INVISIBLE);
        sohbetButton.setVisibility(View.VISIBLE);
        düzenle.setVisibility(View.INVISIBLE);
        cıkısButton.setVisibility(View.INVISIBLE);
        binding.removeProfile.setVisibility(View.INVISIBLE);




        userId = farklıKullanıcı;



        if (firebaseFirestore.collection("Profiles").document("Resimler").collection(userId) != null) {

            firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }

                    if (value != null && !value.isEmpty()) {
                        DocumentSnapshot snapshot = value.getDocuments().get(value.size() - 1);
                        String profile = (String) snapshot.get("ImageProfile");

                        try {


                            Uri uri = Uri.parse(profile);

                            RequestOptions requestOptions = new RequestOptions()
                                    .format(DecodeFormat.PREFER_RGB_565)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .encodeQuality(50);

                            Glide.with(requireContext())
                                    .load(uri)
                                    .apply(requestOptions)
                                    .into(binding.imageProfile);

                        }catch (Exception e){
                            Log.d("ERROR:",e.getMessage());
                        }

                    }else{

                    }


                }

            });

        }











        DatabaseReference reference2 = databaseReference.child(userId);

        if(reference2 != null){


            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if(snapshot.getValue() != null){
                        KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);
                        kullanıcıBilgileris.add(kullanıcıBilgileri);

                        if(kullanıcıBilgileri == null){
                            biyografiText.setText("...");
                            kullanıcıAdıText.setText("");
                        }
                        else if (kullanıcıBilgileri != null ){

                            if(kullanıcıBilgileri.kullanıcıAdı != null){

                                String kd = kullanıcıBilgileri.kullanıcıAdı;
                                int krs = kd.length();
                                if(krs <= 10){
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                                    layoutParams.startToStart = binding.linearProfile.getId();
                                    layoutParams.endToEnd = binding.linearProfile.getId();
                                    binding.profilekullaniciadi.setLayoutParams(layoutParams);

                                    kullanıcıAdıText.setText(kullanıcıBilgileri.kullanıcıAdı);
                                }else{
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.profilekullaniciadi.getLayoutParams();
                                    layoutParams.setMargins(20,15,0,0);
                                    binding.profilekullaniciadi.setLayoutParams(layoutParams);

                                    kullanıcıAdıText.setText(kullanıcıBilgileri.kullanıcıAdı);

                                }


                            } else if (kullanıcıBilgileri.kullanıcıAdı == null ){
                                kullanıcıAdıText.setText("");
                            }

                            if(kullanıcıBilgileri.biyografi != null){
                                biyografiText.setText(kullanıcıBilgileri.biyografi);
                            } else if (kullanıcıBilgileri.biyografi == null){
                                biyografiText.setText("...");
                            }

                        }


                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR: ",error.getMessage());
                }
            });
        }


    }




    public void userkatılmaTarihi(){



        userLauncher();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(userId);

        databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String tarih = snapshot.child("tarih").getValue(String.class);


                                    if (tarih != null) {
                                        if (dil != null) {
                                            if (dil.equals("türkce")) {
                                                katılmaTarihiText.setText(tarih + "  " + "tarihinde katıldı");
                                            } else if (dil.equals("ingilizce")) {
                                                katılmaTarihiText.setText(tarih + "  " + "joined");
                                            }
                                        }
                                    }

                            }


                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ERROR: ",error.getMessage());
                        }
                    });



  }










    public void farklıKullanıcıkatılmaTarihi(String farklıKullanıcı){

        userLauncher();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("KatılmaTarihi").child(farklıKullanıcı);

        databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                String tarih = snapshot.child("tarih").getValue(String.class);


                                if (tarih != null) {
                                    if (dil != null) {
                                        if (dil.equals("türkce")) {
                                            katılmaTarihiText.setText(tarih + "  " + "tarihinde katıldı");
                                        } else if (dil.equals("ingilizce")) {
                                            katılmaTarihiText.setText(tarih + "  " + "joined");
                                        }
                                    }
                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ERROR: ",error.getMessage());
                        }
                    });





    }

    public void arkadasEkle(String farklıKullanıcı){
        userLauncher();

        arkadasEkle.setVisibility(View.VISIBLE);
        arkadasİstegiİptalEt.setVisibility(View.GONE);
        arkadastanCıkar.setVisibility(View.GONE);


        arkreference.child(userId).child(farklıKullanıcı).child("tip").setValue("gonderdi").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    arkreference.child(farklıKullanıcı).child(userId).child("tip").setValue("aldi").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                arkistekKontrol="aldi";

                              if (dil.equals("türkce")) {
                                  Toast.makeText(getActivity(),"Arkadaş isteği gönderildi",Toast.LENGTH_LONG).show();
                              } else if (dil.equals("ingilizce")) {
                                  Toast.makeText(getActivity(),"Friend request sent",Toast.LENGTH_LONG).show();

                              }

                              arkadasEkle.setVisibility(View.GONE);
                              arkadasİstegiİptalEt.setVisibility(View.VISIBLE);
                              butonKontrol=1;



                            }else{

                                if (dil.equals("türkce")) {
                                    Toast.makeText(getActivity(),"Arkadaş isteği gönderilemedi",Toast.LENGTH_LONG).show();
                                } else if (dil.equals("ingilizce")) {
                                    Toast.makeText(getActivity(),"Failed to send friend request",Toast.LENGTH_LONG).show();

                                }

                            }
                        }
                    });

                }else {
                    if (dil.equals("türkce")) {
                        Toast.makeText(getActivity(),"Arkadaş isteği gönderilemedi",Toast.LENGTH_LONG).show();
                    } else if (dil.equals("ingilizce")) {
                        Toast.makeText(getActivity(), "Failed to send friend request", Toast.LENGTH_LONG).show();

                    }
                }

            }
        });



    }


    public void arkadasİstegiİptalEt(String farklıKullanıcı){
        userLauncher();

        arkadasEkle.setVisibility(View.VISIBLE);
        arkadasİstegiİptalEt.setVisibility(View.GONE);
        arkadastanCıkar.setVisibility(View.GONE);

        arkreference.child(userId).child(farklıKullanıcı).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    arkreference.child(farklıKullanıcı).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            arkistekKontrol="";
                            arkadasEkle.setVisibility(View.VISIBLE);
                            arkadasİstegiİptalEt.setVisibility(View.GONE);
                            butonKontrol=2;


                                           if (dil.equals("türkce")) {
                                               Toast.makeText(getActivity(),"Arkadaşlık isteği iptal edildi",Toast.LENGTH_LONG).show();
                                           } else if (dil.equals("ingilizce")) {
                                               Toast.makeText(getActivity(),"Friend request canceled",Toast.LENGTH_LONG).show();

                                           }








                        }
                    });
                }

            }
        });


    }

    public void arkadaslıktanCıkar(String farklıKullanıcı){
        userLauncher();

        butonKontrol = 0;
        arkadasEkle.setVisibility(View.VISIBLE);
        arkadasİstegiİptalEt.setVisibility(View.GONE);

        arkadaslarreference.child("Arkadaslar").child(userId).child(farklıKullanıcı).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    arkadaslarreference.child("Arkadaslar").child(farklıKullanıcı).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            arkistekKontrol="";
                            arkadasEkle.setVisibility(View.VISIBLE);
                            arkadasİstegiİptalEt.setVisibility(View.GONE);
                            butonKontrol=1;
                            arkadastanCıkar.setVisibility(View.GONE);


                                            if (dil.equals("türkce")) {
                                                Toast.makeText(getActivity(),"Arkadaş listesinden kaldırıldı",Toast.LENGTH_LONG).show();
                                            } else if (dil.equals("ingilizce")) {
                                                Toast.makeText(getActivity(),"Removed from friend list",Toast.LENGTH_LONG).show();

                                            }



                        }
                    });
                }

            }
        });


    }



    public Uri getDrawableUri(int drawableId) {
        Resources res = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                res.getResourcePackageName(drawableId) + '/' +
                res.getResourceTypeName(drawableId) + '/' +
                res.getResourceEntryName(drawableId));
    }


    public void OnlineOflineDurum(Boolean durum) {

        try{

            userLauncher();

            DatabaseReference reference = databaseReference.child(userId);

        if(reference != null){


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.getValue() != null) {
                        KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);
                        String kulllaniciAdi = kullanıcıBilgileri.getKullanıcıAdı();
                        dataOnlineOfline = new HashMap<>();
                        dataOnlineOfline.put("durum", durum);
                        dataOnlineOfline.put("kullanıcıAdı",kulllaniciAdi);
                        if(durum == false){
                            Date tarih = new Date();
                            dataOnlineOfline.put("songörülme", GetDateStart.calculateElapsedTime(tarih));
                        }


                        onlineKontrolReference.child("Durum").child(userId).child("State").setValue(dataOnlineOfline);
                        onlineKontrolReference.child("Durum").child(userId).child("State").updateChildren(dataOnlineOfline);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR: ",error.getMessage());
                }
            });

        }

        }catch (Exception e){
            Log.d("ERROR: ",e.getLocalizedMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(farklıKullanıcı != null){
            userLauncher();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child("Arkadaslar").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isFriend = false;

                    for (DataSnapshot arkadasSnapshot : snapshot.getChildren()) {
                        String arkadasId = arkadasSnapshot.getKey();

                        if (arkadasId.equals(farklıKullanıcı)) {
                            isFriend = true;
                            break;
                        }

                    }
                    if (isFriend) {
                        binding.sohbetButton.setEnabled(true);
                    } else {
                        binding.sohbetButton.setEnabled(false);

                        binding.sohbetButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dil.equals("türkce")){
                                    Toast.makeText(getActivity(),"Kullanıcıya mesaj gönderebilmek için arkadaş olmalısınız",Toast.LENGTH_SHORT).show();
                                }else if(dil.equals("ingilizce")){
                                    Toast.makeText(getActivity(),"You must be a friend to send a message to the user",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Hata durumunda yapılacaklar
                    Log.d(TAG,"ArkadasKontrol:succes",error.toException());
                }
            });
        }
        networkController();

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
            binding.kullaniciProfileProgressBar.setVisibility(View.GONE);

            binding.imageProfile.setVisibility(View.VISIBLE);
            binding.profilekullaniciadi.setVisibility(View.VISIBLE);
            binding.constraininitProfile.setVisibility(View.VISIBLE);
            binding.constrainButtonLayout.setVisibility(View.VISIBLE);
            binding.linearBiograhyLayout.setVisibility(View.VISIBLE);
         //   binding.CoordinatorLayoutKullaniciProfile.setVisibility(View.VISIBLE);
            binding.profileProfileText.setVisibility(View.VISIBLE);
            binding.profileTablayout.setVisibility(View.VISIBLE);
            binding.profileViewpager.setVisibility(View.VISIBLE);

            if(farklıKullanıcı.equals(userId) || farklıKullanıcı.equals("null")){
                binding.removeProfile.setVisibility(View.VISIBLE);

            }else{
                binding.removeProfile.setVisibility(View.INVISIBLE);
            }



        } else {
            binding.kullaniciProfileProgressBar.setVisibility(View.VISIBLE);
            binding.imageProfile.setVisibility(View.INVISIBLE);
            binding.removeProfile.setVisibility(View.INVISIBLE);
            binding.profilekullaniciadi.setVisibility(View.INVISIBLE);
            binding.constraininitProfile.setVisibility(View.GONE);
            binding.constrainButtonLayout.setVisibility(View.INVISIBLE);
            binding.linearBiograhyLayout.setVisibility(View.INVISIBLE);
       //     binding.CoordinatorLayoutKullaniciProfile.setVisibility(View.INVISIBLE);
            binding.profileProfileText.setVisibility(View.INVISIBLE);
            binding.profileTablayout.setVisibility(View.GONE);
            binding.profileViewpager.setVisibility(View.GONE);



        }



    }



    @Override
    public void onStart() {
        super.onStart();
        networkController();
        binding.sohbetButton.setEnabled(false);
    }


}