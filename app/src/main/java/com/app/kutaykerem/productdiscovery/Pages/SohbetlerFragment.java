package com.app.kutaykerem.productdiscovery.Pages;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.kutaykerem.productdiscovery.Adaptor.Sohbetler.SohbetlerAdaptor;
import com.app.kutaykerem.productdiscovery.Models.Sohbetlerdetails;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentSohbetlerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;






public class SohbetlerFragment extends Fragment{

    private FragmentSohbetlerBinding binding;

    CircleImageView profile;
    TextView editmesaj;
    TextView kullanıcıAdı;
    ImageView geri;


    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    DatabaseReference reference2;
    StorageReference storageReference,yeniRef,sRef;
    ValueEventListener value;





    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;


    ArrayList<Sohbetlerdetails> sohbetlerDetailsArrayList;


    Uri imageData;
    Bitmap Bitmapresim;
    String kayıtYeri,indirmeLinkli;
    Boolean farklıState;
    String mesaj;
    String msj;

    SohbetlerAdaptor sohbetlerAdaptor;



    LinearLayoutManager linearLayoutManager;



    String gidenProfile,gidenKullanıcıAdı;





    ImageView geriGit;

    ImageView gonder;
    ImageView gonderImage;


    ByteArrayOutputStream outputStream;
    byte[] resimByte;


    Map<String ,Object> data;
    RecyclerView recyclerView;
    CircleImageView kullaniciState;
    TextView stateTextCevrimİci,stateTextCevrimDısı;

    FirebaseAuth userAuth;
    String gelenkullanıcıId, userId;
    String dil;
    String signState;
    ArrayList<String> arrayListGelenKullaniciId = new ArrayList<>();

    public SohbetlerFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSohbetlerBinding.inflate(inflater,container,false);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        recyclerView = view.findViewById(R.id.sohbetler_mesajlar_recyclerView);

        gonder = view.findViewById(R.id.gonderSend);
        gonderImage = view.findViewById(R.id.gonderImage);
        stateTextCevrimİci = view.findViewById(R.id.stateTextCevrimİci);
        stateTextCevrimDısı = view.findViewById(R.id.stateTextCevrimDısı);
        kullaniciState = view.findViewById(R.id.sohbetler_kullanıcı_state);



        userLauncher();


        binding.sohbetlerkisiprofile.setVisibility(View.VISIBLE);
        binding.sohbetlerKullanCAd.setVisibility(View.VISIBLE);










        sohbetlerDetailsArrayList = new ArrayList<>();

        geriGit = view.findViewById(R.id.sohbetler_geri);

        geriGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              NavDirections action = SohbetlerFragmentDirections.actionSohbetlerFragmentToHomeFragmentBottomNav().setPos(2);
              Navigation.findNavController(view).navigate(action);
            }
        });


        profile = view.findViewById(R.id.sohbetlerkisiprofile);
        kullanıcıAdı = view.findViewById(R.id.sohbetler_kullanıcıAdı);

        editmesaj = view.findViewById(R.id.sohbetlerEditmesaj);



        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Profile");
        reference = firebaseDatabase.getReference();
        reference2 = FirebaseDatabase.getInstance().getReference("Mesajlar");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();




        data = new HashMap<>();

        editmesaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                msj = editmesaj.getText().toString().trim();

            }
            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gonder();
            }
        });
        gonderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gönderImage(view);
            }
        });



        if(getArguments() != null){

            try {
            gelenkullanıcıId = SohbetlerFragmentArgs.fromBundle(getArguments()).getKullaniciId();
            direktKisiVerisiCekme();
            OnlineOflineKontrolFarklıK(gelenkullanıcıId);

            }catch (Exception e){
                Log.d("Error :", e.getMessage());
            }

            arrayListGelenKullaniciId.add(gelenkullanıcıId);
        }


        linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        sohbetlerAdaptor = new SohbetlerAdaptor(sohbetlerDetailsArrayList,arrayListGelenKullaniciId,requireContext());
        recyclerView.setAdapter(sohbetlerAdaptor);













        fotoGonder("resim");

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SohbetlerFragmentDirections.ActionSohbetlerFragmentToHomeFragmentBottomNav action = SohbetlerFragmentDirections.actionSohbetlerFragmentToHomeFragmentBottomNav().setPos(4);
                action.setGonderen(gelenkullanıcıId);
                Navigation.findNavController(view).navigate(action);
            }
        });


        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dils) {
                dil = dils;
            }
        });




        goruldu();




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



    public void dilTanı(final DilCallback dilCallback){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    try {
                    dil = dataSnapshot.child("dil").getValue(String.class);
                }catch (Exception e){
                    Log.d("Error :", e.getMessage());
                }
                    if (dil.equals("türkce")) {

                        binding.stateTextCevrimCi.setText("Çevrim içi");
                        binding.stateTextCevrimDS.setText("Çevrim dışı");
                        editmesaj.setHint("Mesaj");
                    } else if (dil.equals("ingilizce")) {
                        binding.stateTextCevrimCi.setText("Online");
                        binding.stateTextCevrimDS.setText("Offline");
                        editmesaj.setHint("Message");
                    }

                    dilCallback.onDilCallback(dil);
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error:", error.getMessage());
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavDirections action = SohbetlerFragmentDirections.actionSohbetlerFragmentToHomeFragmentBottomNav().setPos(2);
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

    }





    public interface DilCallback {
        void onDilCallback(String dil);
    }


    public void direktKisiVerisiCekme(){




        // KullanıcıAdı, KullanıcıId;

        try {

        DatabaseReference reference = databaseReference.child(gelenkullanıcıId);

        if(reference != null){


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String kd = snapshot.child("kullanıcıAdı").getValue().toString();
                    kullanıcıAdı.setText(kd);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }


        // Profile


        String yol = gelenkullanıcıId;

        try {
        if(firebaseFirestore.collection("Profiles").document("Resimler").collection(yol) != null){

            firebaseFirestore.collection("Profiles").document("Resimler").collection(yol).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    if(error !=null){
                        Log.d("Error:", error.getLocalizedMessage());
                    }

                    if(value != null){

                        for(DocumentSnapshot snapshot  : value.getDocuments()){

                            Map<String ,Object> data = snapshot.getData();

                            if(data.get("ImageProfile") != null){
                                String image = (String) data.get("ImageProfile");

                                if(image == null){

                                }else{

                                    try {


                                        Uri uri = Uri.parse(image);

                                        RequestOptions requestOptions = new RequestOptions()
                                                .format(DecodeFormat.PREFER_RGB_565)
                                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                .encodeQuality(50);

                                        Glide.with(requireContext())
                                                .load(uri)
                                                .apply(requestOptions)
                                                .into(profile);

                                    }catch (Exception e){
                                        Log.d("ERROR:",e.getMessage());
                                    }


                                }
                            }



                        }
                    }
                };

            });

        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }
        SohbetlerCek(userId,yol);







    }






    public void goruldu(){


        try {
        reference = FirebaseDatabase.getInstance().getReference("Mesajlar");

        value = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Sohbetlerdetails sohbetlerdetails = dataSnapshot.getValue(Sohbetlerdetails.class);

                    if(sohbetlerdetails.getHedefId().equals(userId) && sohbetlerdetails.getFrom().equals(gelenkullanıcıId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error:", error.getMessage());
            }
        });
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }




    }


    public void gonder(){

        mesaj = editmesaj.getText().toString();

        if(mesaj.isEmpty()){


        }else if(!msj.isEmpty()){


            // Karşıda gözükecek sohbet listesi

            // KullanıcıAdı


            // Karşıda gözükecek sohbet listesi

            // KullanıcıAdı

           try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Profile");
            DatabaseReference databaseReference2 = reference.child(userId);
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot != null){
                        gidenKullanıcıAdı =  snapshot.child("kullanıcıAdı").getValue().toString();


                        // Profile

                        firebaseFirestore.collection("Profiles").document("Resimler").collection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if(error !=null){
                                    Log.d("Error:", error.getLocalizedMessage());
                                }

                                if(value != null){

                                    for(DocumentSnapshot snapshot  : value.getDocuments()){

                                        Map<String ,Object> data = snapshot.getData();

                                        if(data.get("ImageProfile") != null){

                                            gidenProfile = (String) data.get("ImageProfile");

                                        }

                                    }

                                    mesajGonder(userId,gelenkullanıcıId,"text",false,mesaj);
                                    editmesaj.setText("");


                                }
                            };

                        });
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error:", error.getMessage());
                }



            });
        }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }

        }





    }





    public void mesajGonder(final String userId,final String hedefId,String textType,Boolean seen,String mesaj){

        final String  mesajId = reference.child("Mesajlar").child(userId).child(hedefId).push().getKey();


        UUID uuid = UUID.randomUUID();
        String MesajId = System.currentTimeMillis() + uuid.toString();

        try {

        Date startTime = new Date();
        HashMap<String,Object> data = new  HashMap<>();
        data.put("type",textType);
        data.put("seen",seen);
        data.put("time",startTime.toString());
        data.put("mesaj",mesaj);
        data.put("from",userId);
        data.put("hedefId",hedefId);
        data.put("mesajId",MesajId);
        data.put("kullanıcıAdı",gidenKullanıcıAdı);
        data.put("profile",gidenProfile);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Mesajlar").push().setValue(data);

        databaseReference.child("ArkadaslarListesiMesajlar").child(userId).child(gelenkullanıcıId).push().setValue(data);
        databaseReference.child("ArkadaslarListesiMesajlar").child(gelenkullanıcıId).child(userId).push().setValue(data);

    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }

    }
    public void SohbetlerCek(String userId , String hedefid){
        sohbetlerDetailsArrayList  = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Mesajlar");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sohbetlerDetailsArrayList.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Sohbetlerdetails sohbetlerdetails = dataSnapshot.getValue(Sohbetlerdetails.class);
                    String fromId = sohbetlerdetails.getFrom();
                    String hedefId = sohbetlerdetails.getHedefId();

                    if(hedefId != null || fromId != null ){
                        if(sohbetlerdetails.getHedefId().equals(userId) && sohbetlerdetails.getFrom().equals(hedefid)
                                || sohbetlerdetails.getHedefId().equals(hedefid) && sohbetlerdetails.getFrom().equals(userId)){
                            sohbetlerDetailsArrayList.add(sohbetlerdetails);
                        }
                    }


                }

                try{
                    sohbetlerAdaptor = new SohbetlerAdaptor(sohbetlerDetailsArrayList,arrayListGelenKullaniciId,requireContext());
                    recyclerView.setAdapter(sohbetlerAdaptor);

                }catch (Exception e){
                    Log.d("Error:", e.getLocalizedMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error:", error.getMessage());
            }
        });
        

    }






    public void gönderImage(View view){

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


    private void fotoGonder(String mesajTipi) {

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
                                Bitmapresim = ImageDecoder.decodeBitmap(source);


                            } else {
                                Bitmapresim  = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData);

                            }




                            int bitmapSize = Bitmapresim.getAllocationByteCount();

                            double megabytes = bitmapSize / (1024.0 * 1024.0);

                            if (megabytes > 20) {

                                makeScalerUri(imageData);
                            } else {
                                outputStream = new ByteArrayOutputStream();
                                Bitmapresim .compress(Bitmap.CompressFormat.PNG,75,outputStream);
                                resimByte  = outputStream.toByteArray();
                            }






                            UUID random = UUID.randomUUID();

                            kayıtYeri = "Sohbetler/" + "Resimler/" + userId + "/" +  gelenkullanıcıId + "/" + random + ".png";
                            sRef = storageReference.child(kayıtYeri);
                            sRef.putBytes(resimByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    yeniRef = FirebaseStorage.getInstance().getReference(kayıtYeri);
                                    yeniRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            indirmeLinkli = uri.toString();
                                            FotoGonderildi(indirmeLinkli,mesajTipi);

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
                            });

                        } catch (Exception e) {
                            Log.d("Error:", e.getLocalizedMessage());
                        }


                    }

                }else{


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

            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), image);
                Bitmapresim = ImageDecoder.decodeBitmap(source);
            } else {
                Bitmapresim = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
            }


            Bitmap scaledBitmap = Bitmap.createScaledBitmap(Bitmapresim, 600, 600, true);


            outputStream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            resimByte = outputStream.toByteArray();



        } catch (Exception e) {
            Log.d("Error:", e.getLocalizedMessage());
        }
    }

    private void FotoGonderildi(String mesajİcerigi, String mesajTipi) {


        final String  mesajId = reference.child("Mesajlar").child(userId).child(gelenkullanıcıId).push().getKey();

        try {



        UUID uuid = UUID.randomUUID();
        String MesajId = System.currentTimeMillis() + uuid.toString();


        Date startTime = new Date(); // Başlangıç zamanı

        HashMap<String,Object> data = new  HashMap<>();
        data.put("type",mesajTipi);
        data.put("seen",false);
        data.put("time",startTime.toString());
        data.put("mesaj",mesajİcerigi);
        data.put("mesajId",MesajId);
        data.put("from",userId);
        data.put("hedefId",gelenkullanıcıId);
        data.put("kullanıcıAdı",gidenKullanıcıAdı);
        data.put("profile",gidenProfile);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Mesajlar").push().setValue(data);


        databaseReference.child("ArkadaslarListesiMesajlar").child(userId).child(gelenkullanıcıId).push().setValue(data);
        databaseReference.child("ArkadaslarListesiMesajlar").child(gelenkullanıcıId).child(userId).push().setValue(data);
        }catch (Exception e){
            Log.d("Error :", e.getMessage());
        }

    }


    public void OnlineOflineKontrolFarklıK(String farklıKullanıcıId){
        try {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Durum").child(farklıKullanıcıId).child("State");

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                if(dataSnapshot != null){
                    farklıState = Boolean.parseBoolean(dataSnapshot.child("durum").getValue().toString());
                    if(farklıState == true){
                        binding.stateTextCevrimDS.setVisibility(View.INVISIBLE);
                        binding.stateTextCevrimCi.setVisibility(View.VISIBLE);
                        binding.sohbetlerKullanCState.setImageResource(R.drawable.kullanici_online);
                    }else{
                        binding.stateTextCevrimDS.setVisibility(View.VISIBLE);
                        binding.stateTextCevrimCi.setVisibility(View.INVISIBLE);
                        binding.sohbetlerKullanCState.setImageResource(R.drawable.kullanici_offline);

                    }
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
            binding.gonderSend.setEnabled(true);

        } else {
            binding.gonderSend.setEnabled(false);


        }



    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        networkController();
        binding.sohbetlerSwipeResfresh.setColorSchemeResources(R.color.secilmeyenRenk);

        binding.sohbetlerSwipeResfresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SohbetlerCek(userId,gelenkullanıcıId);
                binding.sohbetlerSwipeResfresh.setRefreshing(false);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        reference.removeEventListener(value);
    }


}