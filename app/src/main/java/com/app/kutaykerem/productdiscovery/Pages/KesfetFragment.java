package com.app.kutaykerem.productdiscovery.Pages;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.kutaykerem.productdiscovery.Models.KesfetDetails;
import com.app.kutaykerem.productdiscovery.Adaptor.Kesfet.KesfetAdaptor;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentKesfetBinding;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;



public class KesfetFragment extends Fragment  {


    private FragmentKesfetBinding binding;





    ArrayList<KesfetDetails> kesfetDetailsArrayList;



    FirebaseFirestore firebaseFirestore;
    KesfetAdaptor kesfetAdaptor;


    private Toolbar toolbar;
    private MenuItem menuItem;
    private SearchView searchView;



    RecyclerView recyclerView;



    String dil;
    String userId;


    String signState;
    String saveArananParca = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKesfetBinding.inflate(inflater,container,false);
        return binding.getRoot();


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);









        userLauncher();
        recyclerView = view.findViewById(R.id.recycler_kesfet);
        recyclerView.requestFocus();
        kesfetDetailsArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();







    }




    public void dilTanı(final DilCallback dilCallback){

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
    }

    public interface DilCallback {
        void onDilCallback(String dil);
    }

    public void getArgumenData(String arananParcaAdı) {

        saveArananParca = arananParcaAdı;


        kesfetDetailsArrayList.clear();

            if (arananParcaAdı.equals("null")){

                getData();
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                kesfetAdaptor = new KesfetAdaptor(arananParcaAdı,kesfetDetailsArrayList,requireContext(),binding.kesfetView);
                recyclerView.setAdapter(kesfetAdaptor);



            }else if(arananParcaAdı.equals("Bilgisayar") || arananParcaAdı.equals("Cep Telefonu") ||  arananParcaAdı.equals("Vasıta") ||  arananParcaAdı.equals("Kitap")
                    ||  arananParcaAdı.equals("Computer") || arananParcaAdı.equals("Mobile Phone") || arananParcaAdı.equals("Vehicle")  || arananParcaAdı.equals("Book")) {




                ParcaAdiFiltre(arananParcaAdı);

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                kesfetAdaptor = new KesfetAdaptor(arananParcaAdı,kesfetDetailsArrayList,requireContext(),binding.kesfetView);
                recyclerView.setAdapter(kesfetAdaptor);



            }else if(arananParcaAdı.equals("most_commented_posts") ||arananParcaAdı.equals("posts_with_no_comments") || arananParcaAdı.equals("posts_with_the_highest_score") ||
                    arananParcaAdı.equals("posts_with_the_lowest_score") || arananParcaAdı.equals("newest_posts") ||arananParcaAdı.equals("oldest_posts") || arananParcaAdı.equals("my_submitted_posts") || arananParcaAdı.equals("most_liked_posts")) {

                if(arananParcaAdı.equals("most_commented_posts")){
                    MostCommentedPosts();

                }else if(arananParcaAdı.equals("posts_with_no_comments")){
                    PostsWithNoComments();
                } else if (arananParcaAdı.equals("most_liked_posts")){
                    MostLikedPost();
                }
                else if(arananParcaAdı.equals("posts_with_the_highest_score")){
                    PostsWithTheHighestScore();
                }else if(arananParcaAdı.equals("posts_with_the_lowest_score")){
                    PostsWithTheLowestScore();

                }else if(arananParcaAdı.equals("newest_posts")){
                    NewestPosts();

                }else if(arananParcaAdı.equals("oldest_posts")){
                    OldestPosts();

                }else if(arananParcaAdı.equals("my_submitted_posts")){
                    MySubmittedPosts();
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                kesfetAdaptor = new KesfetAdaptor(arananParcaAdı,kesfetDetailsArrayList,requireContext(),binding.kesfetView);
                recyclerView.setAdapter(kesfetAdaptor);




            }

            else{
                Parcafiltreleme(arananParcaAdı);

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                kesfetAdaptor = new KesfetAdaptor(arananParcaAdı,kesfetDetailsArrayList,requireContext(),binding.kesfetView);
                recyclerView.setAdapter(kesfetAdaptor);


            }

/*
        recyclerView.smoothScrollToPosition(0);
        recyclerView.requestFocus();
        */

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





    public void Parcafiltreleme(String arananParcaAdı){
        kesfetDetailsArrayList.clear();

        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaModeli",arananParcaAdı).orderBy("tarih", Query.Direction.DESCENDING) .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
                }
                if (value != null) {

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
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });

    }
    public void ParcaAdiFiltre(String arananParcaAdı){

        kesfetDetailsArrayList.clear();




        if(arananParcaAdı.equals("Bilgisayar") || arananParcaAdı.equals("Computer")){



            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Bilgisayar").orderBy("tarih",Query.Direction.DESCENDING) .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Computer").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });


        }else if(arananParcaAdı.equals("Cep Telefonu") || arananParcaAdı.equals("Mobile Phone")){

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Cep Telefonu").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {

                    }
                    if (value != null) {


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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Mobile Phone").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {

                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

        }else if(arananParcaAdı.equals("Vasıta") || arananParcaAdı.equals("Vehicle")){

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Vasıta(Otomobil-Suv-Motosiklet)").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Vehicle (Car-Suv-Motorcycle)").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

        }else if(arananParcaAdı.equals("Kitap") || arananParcaAdı.equals("Book")){
            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Kitap(Dergi-Gazete)").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });

            firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("parcaAdi","Book (Magazine-Newspaper)").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("ERROR: ",error.getLocalizedMessage());
                    }
                    if (value != null) {

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
                        kesfetAdaptor.notifyDataSetChanged();
                    }

                }

            });
        }




    }



    public void MostCommentedPosts(){

        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler")
                .orderBy("tarih",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId).child("Gonderiler");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int yorumSayisi = 0; // yorumların toplam sayısını tutmak için bir değişken
                                if (dataSnapshot != null) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        yorumSayisi++; // her yorumu aldığımızda yorum sayısını artırıyoruz
                                    }
                                }
                                if(yorumSayisi >= 1){
                                    // yorumların toplam sayısını yazdırma
                                    KesfetDetails kesfetDetails = new KesfetDetails(downloadUrl, parcaAdi, aciklama, puanDegeri, gonderiId, gonderenId ,parcaModeli,ayrıParca,tarih,begeniSayisi.intValue());
                                    kesfetDetailsArrayList.add(kesfetDetails);
                                     kesfetAdaptor.notifyDataSetChanged();

                                }else{

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                    }
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });

    }

    public void PostsWithNoComments(){
        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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



                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(gonderiId).child("Gonderiler");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int yorumSayisi = 0; // yorumların toplam sayısını tutmak için bir değişken
                                if (dataSnapshot != null) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        yorumSayisi++; // her yorumu aldığımızda yorum sayısını artırıyoruz
                                    }
                                }
                                if(yorumSayisi >= 1){
                                    // yorumların toplam sayısını yazdırma

                                }else{
                                    KesfetDetails kesfetDetails = new KesfetDetails(downloadUrl, parcaAdi, aciklama, puanDegeri, gonderiId, gonderenId ,parcaModeli,ayrıParca,tarih,begeniSayisi.intValue());
                                    kesfetDetailsArrayList.add(kesfetDetails);
                                    kesfetAdaptor.notifyDataSetChanged();
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                    }
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });
    }


    public void MostLikedPost(){

        firebaseFirestore.collection("Kesfet")
                .document("Gonderi")
                .collection("Gonderiler")
                .orderBy("begeniSayisi", Query.Direction.DESCENDING)
                .whereGreaterThan("begeniSayisi", 0)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ", error.getLocalizedMessage());
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


                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });


    }


    public void PostsWithTheHighestScore(){
        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler")
                .orderBy("puanDegeri", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("ERROR: ",error.getLocalizedMessage());
                        }
                        if (value != null) {
                            kesfetDetailsArrayList.clear();
                            // Sorgu sonucunda gelen tüm belgeleri işleme
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                // Belgeden gerekli alanları çıkarma
                                String downloadUrl = (String) data.get("downloadUrl");
                                String aciklama = (String) data.get("aciklama");
                                String parcaAdi = (String) data.get("parcaAdi");
                                String parcaModeli = (String) data.get("parcaModeli");
                                String puanDegeri = (String) data.get("puanDegeri");
                                String gonderiId = (String) data.get("gonderiId");
                                String gonderenId = (String) data.get("gonderenId");
                                Timestamp tarih = (Timestamp) data.get("tarih");
                                String ayrıParca = (String) data.get("ayrıParca");
                                Long begeniSayisi = (Long) data.get("begeniSayisi");
                                // KesfetDetails nesnesi oluşturma ve ArrayList'e ekleme
                                if (puanDegeri != null && !puanDegeri.isEmpty() && Double.valueOf(puanDegeri) > 0) {
                                    KesfetDetails kesfetDetails = new KesfetDetails(downloadUrl, parcaAdi, aciklama, puanDegeri, gonderiId, gonderenId ,parcaModeli,ayrıParca,tarih,begeniSayisi.intValue());
                                    kesfetDetailsArrayList.add(kesfetDetails);
                                }


                            }
                            // ArrayList'i puanDegeri'ne göre sıralama ve adapter'ı güncelleme
                            Collections.sort(kesfetDetailsArrayList, new Comparator<KesfetDetails>() {
                                @Override
                                public int compare(KesfetDetails o1, KesfetDetails o2) {
                                    return o2.getPuan().compareTo(o1.getPuan());
                                }
                            });
                            kesfetAdaptor.notifyDataSetChanged();
                        }
                    }
                });





    }

    public void PostsWithTheLowestScore(){
        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler")
                .orderBy("puanDegeri", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d("ERROR: ",error.getLocalizedMessage());
                        }
                        if (value != null) {
                            kesfetDetailsArrayList.clear();
                            // Sorgu sonucunda gelen tüm belgeleri işleme
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                // Belgeden gerekli alanları çıkarma
                                String downloadUrl = (String) data.get("downloadUrl");
                                String aciklama = (String) data.get("aciklama");
                                String parcaAdi = (String) data.get("parcaAdi");
                                String parcaModeli = (String) data.get("parcaModeli");
                                String puanDegeri = (String) data.get("puanDegeri");
                                String gonderiId = (String) data.get("gonderiId");
                                String gonderenId = (String) data.get("gonderenId");
                                Timestamp tarih = (Timestamp) data.get("tarih");
                                String ayrıParca = (String) data.get("ayrıParca");
                                Long begeniSayisi = (Long) data.get("begeniSayisi");

                                if (puanDegeri == null || puanDegeri.isEmpty() || Double.valueOf(puanDegeri) == 0) {

                                    KesfetDetails kesfetDetails = new KesfetDetails(downloadUrl, parcaAdi, aciklama, puanDegeri, gonderiId, gonderenId ,parcaModeli,ayrıParca,tarih,begeniSayisi.intValue());
                                    kesfetDetailsArrayList.add(kesfetDetails);
                                }


                            }
                            // ArrayList'i puanDegeri'ne göre sıralama ve adapter'ı güncelleme
                            Collections.sort(kesfetDetailsArrayList, new Comparator<KesfetDetails>() {
                                @Override
                                public int compare(KesfetDetails o1, KesfetDetails o2) {
                                    return o2.getPuan().compareTo(o1.getPuan());
                                }
                            });
                            kesfetAdaptor.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void NewestPosts(){
        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });
    }

    public void OldestPosts(){

        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").orderBy("tarih",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });
    }

    public void MySubmittedPosts(){

        firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler").whereEqualTo("gonderenId",userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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
                    kesfetAdaptor.notifyDataSetChanged();
                }

            }

        });



    }





    public void getData() {

        CollectionReference collectionReference = firebaseFirestore.collection("Kesfet").document("Gonderi").collection("Gonderiler");

        collectionReference.orderBy("tarih", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d("ERROR: ",error.getLocalizedMessage());
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
                    kesfetAdaptor.notifyDataSetChanged();


                }
            }




        });


    }






    @Override
    public void onResume() {
        super.onResume();



        binding.kesfetSwipeResfresh.setColorSchemeResources(R.color.secilmeyenRenk);

        binding.kesfetSwipeResfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getArgumenData(saveArananParca);
                binding.kesfetSwipeResfresh.setRefreshing(false);
            }
        });




    }
}
