package com.app.kutaykerem.productdiscovery.Pages;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.Models.GetDateStart;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentDuzenleBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class DuzenleFragment extends Fragment {

    private FragmentDuzenleBinding binding;

    FirebaseAuth auth;
    DatabaseReference databaseReference = null;
    FirebaseDatabase database=null;

    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference kullanilanDillerReference;

    ActivityResultLauncher<Intent> activityResultLauncher;

    ActivityResultLauncher<String> permissionLauncher;


    ImageView dilsec,arkaplan;
    Button türkce,ingilizce;
    String düzenleAdi,cikisAdi,arkadasliktancikaradi,arkadasekleadi,arkadasistekiptaladi,sohbetadi,katilmatarihiadi;


    TextView eposata,kullanıcıAdı,biyografi;

    Uri imageData;
    Bitmap selectedimage;

    CircleImageView profile;
    ImageButton kaydet;


    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;


    String userId;
    String email;
    Boolean state;


    FirebaseUser userCurrentUser;
    String dil;
    String signState;
    Switch switchDuzenle;




    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public SharedPreferences selectedDilSharedPrefences= null ;

    public DuzenleFragment() {


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentDuzenleBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Profile");
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        kullanilanDillerReference = FirebaseDatabase.getInstance().getReference();



        eposata = view.findViewById(R.id.duzenleeposta);
        kullanıcıAdı = view.findViewById(R.id.duzenleEditTextKullaniciAdi);
        biyografi = view.findViewById(R.id.editTextBiyografi);
        profile = view.findViewById(R.id.imageProfile);
        kaydet = view.findViewById(R.id.kaydetButton);
        switchDuzenle = view.findViewById(R.id.switchDuzenle);






        userLauncher();
try {
        binding.duzenleeposta.setEnabled(false);


                DilTanı();

                profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {



                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        if (dataSnapshot.exists()) {
                                            dil = dataSnapshot.child("dil").getValue(String.class);

                                            if(dil.equals("türkce")){
                                                Snackbar.make(view, "Profil fotoğrafı seçmeniz için izne ihtiyacımız var", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                                    }
                                                }).show();

                                            }else if (dil.equals("ingilizce")){
                                                Snackbar.make(view, " We need permission to choose a profile photo", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                                    }
                                                }).show();



                                            }


                                        }else{

                                        }




                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {



                                    }
                                });















                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intent);
                        }
                    }
                });
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                view.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if(i == KeyEvent.KEYCODE_BACK){
                            NavDirections navDirections = DuzenleFragmentDirections.actionDuzenleFragmentToHomeFragmentBottomNav().setPos(4);
                            Navigation.findNavController(view).navigate(navDirections);

                        }

                        return true;
                    }
                });

                binding.duzenleGeri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavDirections navDirections =  DuzenleFragmentDirections.actionDuzenleFragmentToHomeFragmentBottomNav().setPos(4);
                        Navigation.findNavController(view).navigate(navDirections);
                    }
                });


                kaydet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        kaydetMethod(view);
                    }
                });
    }catch (Exception e){

    }






try {
        registerLauncher();



        türkce = view.findViewById(R.id.turkce);
        ingilizce = view.findViewById(R.id.ingilizce);


        dilsec = (ImageView) view.findViewById(R.id.seceneklerdil);
        final boolean dilSecildi = false; // Durum değişkeni

        dilsec.setOnClickListener(new View.OnClickListener() {
            boolean isDilSecildi = dilSecildi; // Final bir değişken olarak atama

            @Override
            public void onClick(View view) {
                if (!isDilSecildi) {
                    türkce.setVisibility(View.VISIBLE);
                    ingilizce.setVisibility(View.VISIBLE);
                } else {
                    türkce.setVisibility(View.GONE);
                    ingilizce.setVisibility(View.GONE);
                }
                isDilSecildi = !isDilSecildi; // Durum değişkenini güncelle
            }
        });



        türkce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dil = "türkce";
                DilSecildi(dil);
                DilTanı();
                NavDirections navDirections =DuzenleFragmentDirections.actionDuzenleFragmentSelf();
                Navigation.findNavController(view).navigate(navDirections);
            }
        });
        ingilizce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dil = "ingilizce";
                DilSecildi(dil);
                DilTanı();

               NavDirections navDirections = DuzenleFragmentDirections.actionDuzenleFragmentSelf();
                Navigation.findNavController(view).navigate(navDirections);

            }
        });

    }catch (Exception e){

    }




        DilTanı();


        try {
            sharedPreferences = requireActivity().getSharedPreferences("MODE",Context.MODE_PRIVATE);
            Boolean nightMode = sharedPreferences.getBoolean("night",false);

            if(nightMode){
                binding.switchDuzenle.setChecked(true);
                binding.switchDuzenle.setText("Light");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else{
                binding.switchDuzenle.setText("Dark");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            binding.switchDuzenle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(nightMode){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("night",false);
                    }else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("night",true);
                    }
                    editor.apply();
                    NavDirections action =DuzenleFragmentDirections.actionDuzenleFragmentSelf();
                    Navigation.findNavController(view).navigate(action);

                }
            });
        }catch (Exception e){
            Log.d("Error:", e.getMessage());
        }







/*
        compositeDisposable = new CompositeDisposable();
        RjDatabase rjDatabase = Room.databaseBuilder(requireContext(), RjDatabase.class, "database_theme")
                .allowMainThreadQueries()
                .build();
        rjDao = rjDatabase.rjDao();


        compositeDisposable.add(rjDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getTheme));

*/

       super.onViewCreated(view, savedInstanceState);
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
            email = email;
            binding.duzenleeposta.setText(email);
            userId = uid;
            userCurrentUser = currentUser;

        }else if(signState.equals("facebook")){
            email = email;
            binding.duzenleeposta.setText(email);
            userId = uid;
            userCurrentUser = currentUser;
        }else if(signState.equals("gmail_hotmail")){
            email = email;
            binding.duzenleeposta.setText(email);
            userId = uid;
            userCurrentUser = currentUser;
        }

        else{

        }

    }

    private void kaydetMethod(View view) {
try {
        String kullaniciAdi=kullanıcıAdı.getText().toString();
        String biyografı = biyografi.getText().toString();

        if(TextUtils.isEmpty(kullaniciAdi)){
            kullanıcıAdı.setError("User Name");
        } else if (kullanıcıAdı != null) {


            databaseReference.child(userId).child("kullanıcıAdı").setValue(kullaniciAdi);
            databaseReference.child(userId).child("biyografi").setValue(biyografı);

            NavDirections action =DuzenleFragmentDirections.actionDuzenleFragmentToHomeFragmentBottomNav().setPos(4);
            Navigation.findNavController(view).navigate(action);

        }
    }catch (Exception e){

    }

    }


    public void DilSecildi(String value){


        try {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        HashMap<String, Object> data = new HashMap<>();
        data.put("dil", value);
        Date tarih = new Date();
        data.put("tarih", GetDateStart.calculateElapsedTime(tarih));


            databaseReference.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    } else {
                        // Hata durumunda işlemler
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }






    }
    public void DilTanı(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                      try {
                          dil = dataSnapshot.child("dil").getValue(String.class);;

                          selectedDilSharedPrefences = requireActivity().getSharedPreferences("Dil",Context.MODE_PRIVATE);
                          selectedDilSharedPrefences.edit().putString("dil",dil).apply();

                      }catch (Exception e){
                          Log.d("Error:",e.getMessage());
                      }




                        if(dil.equals("türkce")){
                            binding.duzenleEditTextKullaniciAdi.setHint("kullanıcı adı");
                            binding.editTextBiyografi.setHint("biyografi");
                            binding.selectLanguageText.setHint("Dil Seç");

                            binding.duzenleUsernameText.setText("Kullanıı Adı");
                            binding.duzenleEmailText.setText("Email");
                            binding.duzenleBiyografiText.setText("Biyografi");
                            binding.profileText.setText("Profil Seç");
                            binding.editProfileText.setText("Profili Düzenle");
                            binding.darkText.setText("Uygulama Teması");
                        }else if (dil.equals("ingilizce")){
                            binding.duzenleEditTextKullaniciAdi.setHint("user name");
                            binding.editTextBiyografi.setHint("biography");
                            binding.selectLanguageText.setText("Select Language");

                            binding.duzenleUsernameText.setText("User Name");
                            binding.duzenleEmailText.setText("Email");
                            binding.duzenleBiyografiText.setText("Biography");
                            binding.profileText.setText("Select Profile");
                            binding.editProfileText.setText("Edit Profile");
                            binding.darkText.setText("App Theme");
                        }


                    }else{

                    }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });






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

                                profile.setImageBitmap(selectedimage);




                            } else {
                                selectedimage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData);
                                profile.setImageBitmap(selectedimage);
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }







                        final String name = "Kullanıcılar/" + userId + ".jpg";

                        try {
                            storageReference.child(name).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(name);
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String profileImage = uri.toString();

                                            HashMap<String, Object> data = new HashMap<>();
                                            data.put("ImageProfile", profileImage);
                                            Date tarih = new Date();
                                            data.put("time", GetDateStart.calculateElapsedTime(tarih));

                                            CollectionReference collectionReference = firebaseFirestore.collection("Profiles").document("Resimler").collection(userId);

                                            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {
                                                    if (querySnapshot.isEmpty()) {
                                                        // Veri yoksa yeni belge oluştur
                                                        collectionReference.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                // Başarılı olduğunda yapılacak işlemler
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Error:",e.getLocalizedMessage());
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
                                                                Log.d("Error:",e.getLocalizedMessage());
                                                            }
                                                        });
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Error:",e.getLocalizedMessage());
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }catch (Exception e){
                            Log.d("Error:", e.getMessage());
                        }







                    }
                }
            }



        });
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
            kaydet.setEnabled(true);
            binding.seceneklerdil.setEnabled(true);
        } else {
            kaydet.setEnabled(false);
            binding.seceneklerdil.setEnabled(false);

        }



    }

    @Override
    public void onResume() {
        super.onResume();
        networkController();







    }



}