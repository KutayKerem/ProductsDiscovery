package com.app.kutaykerem.productdiscovery.Pages;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.AddPostFirstStageBinding;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class AddPostFirstStage extends Fragment {


     AddPostFirstStageBinding binding;



    List<String> ekleparcaAdiArrayList;




    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;


    DatabaseReference databaseReference = null;
    FirebaseDatabase database=null;


    ArrayList<String> productDetailsArrayList;








    FirebaseUser userCurrentUser;
    String userId;
    String dil;

    String signState;


    AutoCompleteTextView autoCompleteTextViewParcaAdi,autoCompleteTextViewParcaModeli;
    ArrayAdapter<String> adapterParcaAdi,adapterParcaModeli;
    String stringParcaAdi;




    String stringParcaAdı;
    String stringParcaModeli;
    String stringAyriParca;
    String stringAciklama;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = AddPostFirstStageBinding.inflate(inflater,container,false);
       return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




       // listView = view.findViewById(R.id.listView);
        ekleparcaAdiArrayList = new ArrayList<>();
        productDetailsArrayList = new ArrayList<>();





        /// switch1 = view.findViewById(R.id.switch1);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Profile");
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();




        userLauncher();

        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });


        autoCompleteTextViewParcaAdi = view.findViewById(R.id.add_post_first_auto_complete_text_parcaAdi);
        autoCompleteTextViewParcaModeli = view.findViewById(R.id.add_post_first_auto_complete_text_parcaModeli);






        try {

        adapterParcaAdi = new ArrayAdapter<String>(requireContext(), R.layout.list_item, ekleparcaAdiArrayList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
                Boolean nightMode = sharedPreferences.getBoolean("night",false);

                if(nightMode){
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.secilmeyenRenk)); // İstenilen rengi burada ayarlayabilirsiniz
                    return textView;
                }else{
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // İstenilen rengi burada ayarlayabilirsiniz
                    return textView;

                }

            }
        };
        autoCompleteTextViewParcaAdi.setAdapter(adapterParcaAdi);
        autoCompleteTextViewParcaAdi.setKeyListener(null);





        autoCompleteTextViewParcaAdi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                stringParcaAdi = adapterView.getItemAtPosition(i).toString();
                stringParcaAdı = stringParcaAdi;

                if(stringParcaAdi.equals("Other") || stringParcaAdi.equals("Diğer")){
                    autoCompleteTextViewParcaModeli.setAdapter(adapterParcaModeli);
                    autoCompleteTextViewParcaModeli.setKeyListener(null);
                }else{
                    parcaModelleri(stringParcaAdi);

                    adapterParcaModeli = new ArrayAdapter<String>(requireContext(), R.layout.list_item, productDetailsArrayList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
                            Boolean nightMode = sharedPreferences.getBoolean("night",false);

                            if(nightMode){
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.secilmeyenRenk)); // İstenilen rengi burada ayarlayabilirsiniz
                                return textView;
                            }else{
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // İstenilen rengi burada ayarlayabilirsiniz
                                return textView;

                            }


                        }
                    };
                    autoCompleteTextViewParcaModeli.setAdapter(adapterParcaModeli);
                    autoCompleteTextViewParcaModeli.setKeyListener(null);

                }













                // Parça Modeli zaten doluyken birdaha parça adı seçe basılırsa parça modeli sıfırlanır.
                autoCompleteTextViewParcaAdi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            autoCompleteTextViewParcaModeli.setText("");
                            autoCompleteTextViewParcaModeli.getText().clear();
                            autoCompleteTextViewParcaModeli.clearListSelection();

                            stringParcaAdi = adapterView.getItemAtPosition(i).toString();
                            stringParcaAdı = stringParcaAdi;

                            productDetailsArrayList.clear();
                            parcaModelleri(stringParcaAdi);





                            adapterParcaModeli = new ArrayAdapter<String>(requireContext(), R.layout.list_item, productDetailsArrayList) {
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
                                    Boolean nightMode = sharedPreferences.getBoolean("night",false);

                                    if(nightMode){
                                        TextView textView = (TextView) super.getView(position, convertView, parent);
                                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.secilmeyenRenk)); // İstenilen rengi burada ayarlayabilirsiniz
                                        return textView;
                                    }else{
                                        TextView textView = (TextView) super.getView(position, convertView, parent);
                                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // İstenilen rengi burada ayarlayabilirsiniz
                                        return textView;

                                    }


                                }
                            };
                            autoCompleteTextViewParcaModeli.setAdapter(adapterParcaModeli);
                            autoCompleteTextViewParcaModeli.setKeyListener(null);



                        }
                    });
                }

        });

    }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }





        try {

        binding.addPostFirstAutoCompleteTextParcaModeli.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 stringParcaAdı = stringParcaAdi;
                 stringParcaModeli = adapterView.getItemAtPosition(i).toString();

            }
        });
        }catch (Exception e){
        Log.d("Error:",e.getLocalizedMessage());
    }
        autoCompleteTextViewParcaModeli.setKeyListener(null);


        binding.addPostFirstDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                if(!binding.addPostFirstAutoCompleteTextParcaAdi.getText().toString().isEmpty()){

                    if(!binding.addPostFirstAutoCompleteTextParcaModeli.getText().toString().isEmpty() || !binding.addPostFirstTextInputEditTextAyrParca.getText().toString().isEmpty()){
                        if(!binding.addPostFirstTextInputEditTextAciklama.getText().toString().isEmpty()){
                            nextPage(stringParcaAdı,stringParcaModeli,binding.addPostFirstTextInputEditTextAyrParca.getText().toString(),binding.addPostFirstTextInputEditTextAciklama.getText().toString());


                        }else{
                            dilTanı(new DilCallback() {
                                @Override
                                public void onDilCallback(String dil) {
                                    if(dil.equals("türkce")){
                                        Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }else{
                        dilTanı(new DilCallback() {
                            @Override
                            public void onDilCallback(String dil) {
                                if(dil.equals("türkce")){
                                    Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }


                }else{
                    dilTanı(new DilCallback() {
                        @Override
                        public void onDilCallback(String dil) {
                            if(dil.equals("türkce")){
                                Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }
            }
        });













        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
              try {
                NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToHomeFragmentBottomNav().setPos(1);
                Navigation.findNavController(view).navigate(action);
            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }
            }
        });


        binding.addPostFirstGeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {
                NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToHomeFragmentBottomNav().setPos(1);
                Navigation.findNavController(view).navigate(action);
            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }
            }
        });


    }

    public void nextPage(String parcaAdi, String parcaModeli, String ayriParca,String aciklama){


        try {

        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {


                if (dil.equals("türkce")){


                    if(parcaModeli == "Diğer" || parcaModeli == "Other"){

                        if(!parcaAdi.isEmpty()){

                            if (!aciklama.isEmpty()){
                                String digerParcalar ="";

                                if(ayriParca != null){
                                    digerParcalar = ayriParca;
                                }
                                else if (ayriParca == null){
                                    Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                                }

                                if(!digerParcalar.isEmpty()){
                                    binding.addPostFirstDevam.setEnabled(true);

                                    String finalSonParcaModeli = digerParcalar;
                                    try {
                                        //NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_right).setExitAnim(R.anim.slide_right).build();
                                        binding.addPostFirstView.setVisibility(View.GONE);
                                        NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToAddPostSecondStage(parcaAdi, finalSonParcaModeli,aciklama,binding.addPostTRSwitch.getText().toString(),"türkce");
                                        Navigation.findNavController(requireView()).navigate(action);
                                    }catch (Exception e){
                                        Log.d("Error:",e.getLocalizedMessage());
                                    }


                                }else{
                                    Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(!parcaAdi.isEmpty()){

                            if (!aciklama.isEmpty()){
                                String sonParcaModeli ="";

                                if(parcaModeli != null && !ayriParca.isEmpty()){
                                    sonParcaModeli = parcaModeli;
                                }else if(parcaModeli != null  && ayriParca.isEmpty()){
                                    sonParcaModeli = parcaModeli;
                                }else if(parcaModeli == null && !ayriParca.isEmpty()){
                                    sonParcaModeli = ayriParca;
                                }else if(parcaModeli == null && ayriParca.isEmpty()){
                                    Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                                }

                                if(!sonParcaModeli.isEmpty()){
                                    binding.addPostFirstDevam.setEnabled(true);

                                    String finalSonParcaModeli = sonParcaModeli;
                                    try {
                                        //NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_right).setExitAnim(R.anim.slide_right).build();
                                        binding.addPostFirstView.setVisibility(View.GONE);
                                        NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToAddPostSecondStage(parcaAdi, finalSonParcaModeli,aciklama,binding.addPostTRSwitch.getText().toString(),"türkce");
                                        Navigation.findNavController(requireView()).navigate(action);
                                    }catch (Exception e){
                                        Log.d("Error:",e.getLocalizedMessage());
                                    }


                                }else{
                                }
                            }else{
                                Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            Toast.makeText(requireContext(),"Bilgileri Doldurunuz...",Toast.LENGTH_SHORT).show();
                        }
                    }






                }



                else{


                    if(parcaModeli == "Diğer" || parcaModeli == "Other"){

                        if(!parcaAdi.isEmpty()){

                            if (!aciklama.isEmpty()){
                                String digerParcalar ="";

                                if(ayriParca != null){
                                    digerParcalar = ayriParca;
                                }
                                else if (ayriParca == null){
                                    Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                                }
                                if(!digerParcalar.isEmpty()){
                                    binding.addPostFirstDevam.setEnabled(true);

                                    String finalSonParcaModeli = digerParcalar;
                                    try {
                                        //NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_right).setExitAnim(R.anim.slide_right).build();
                                        binding.addPostFirstView.setVisibility(View.GONE);
                                        NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToAddPostSecondStage(parcaAdi, finalSonParcaModeli,aciklama,binding.addPostTRSwitch.getText().toString(),"ingilizce");
                                        Navigation.findNavController(requireView()).navigate(action);
                                    }catch (Exception e){
                                        Log.d("Error:",e.getLocalizedMessage());
                                    }


                                }else{
                                    Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                            }

                        }else{

                            Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        if(!parcaAdi.isEmpty()){

                            if (!aciklama.isEmpty()){
                                String sonParcaModeli ="";

                                if(parcaModeli != null && !ayriParca.isEmpty()){
                                    sonParcaModeli = parcaModeli;
                                }else if(parcaModeli != null&& ayriParca.isEmpty()){
                                    sonParcaModeli = parcaModeli;
                                }else if(parcaModeli == null && !ayriParca.isEmpty()){
                                    sonParcaModeli = ayriParca;
                                }else if(parcaModeli == null && ayriParca.isEmpty()){
                                    Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                                }

                                if(!sonParcaModeli.isEmpty()){
                                    binding.addPostFirstDevam.setEnabled(true);



                                    try {
                                        binding.addPostFirstView.setVisibility(View.GONE);
                                        String finalSonParcaModeli = sonParcaModeli;
                                        NavDirections action = AddPostFirstStageDirections.actionAddPostFirstStageToAddPostSecondStage(parcaAdi, finalSonParcaModeli,aciklama,binding.addPostTRSwitch.getText().toString(),"ingilizce");
                                        Navigation.findNavController(requireView()).navigate(action);
                                    }catch (Exception e){
                                        Log.d("Error:",e.getLocalizedMessage());
                                    }


                                }
                            }else{
                                Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(requireContext(),"Fill İn The İnformation......",Toast.LENGTH_SHORT).show();
                        }
                    }













                }






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

            userId = uid;
            userCurrentUser = currentUser;

        }else if(signState.equals("facebook")){
            userId = uid;
            userCurrentUser = currentUser;
        }else if(signState.equals("gmail_hotmail")){
            userId = uid;
            userCurrentUser = currentUser;
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

                    autoCompleteTextViewParcaAdi.clearListSelection();
                    ekleparcaAdiArrayList.clear();
                    parcaAdları(dil);
                    adapterParcaAdi = new ArrayAdapter<String>(requireContext(), R.layout.list_item, ekleparcaAdiArrayList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
                            Boolean nightMode = sharedPreferences.getBoolean("night",false);

                            if(nightMode){
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.secilmeyenRenk)); // İstenilen rengi burada ayarlayabilirsiniz
                                return textView;
                            }else{
                                TextView textView = (TextView) super.getView(position, convertView, parent);
                                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // İstenilen rengi burada ayarlayabilirsiniz
                                return textView;

                            }

                        }
                    };
                    autoCompleteTextViewParcaAdi.setAdapter(adapterParcaAdi);
                    autoCompleteTextViewParcaAdi.setKeyListener(null);


                    if(dil.equals("türkce")){

                        binding.addPostTRSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(compoundButton.isChecked()){
                                    binding.addPostTRSwitch.setText("İfade Akışı");
                                }else{
                                    binding.addPostTRSwitch.setText("Gönderi");

                                }
                            }
                        });

                        binding.addPostFirstFirstText.setText("Paylaş");
                        binding.addPostFirstTextInputLayoutParcaAdi.setHint("Genel Ürünler");
                        binding.addPostFirstTextInputLayoutParcaModeli.setHint("Modeller");
                        binding.addPostFirstTextInputLayoutAciklama.setHint("Açıklama");
                        binding.addPostFirstTextInputLayoutAyrParca.setHint("Farklı Bir Ürün");
                        binding.addPostFirstDevam.setText("Devam et");










                    }else if (dil.equals("ingilizce")){

                        binding.addPostTRSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                if(compoundButton.isChecked()){
                                    binding.addPostTRSwitch.setText("Expression Stream");

                                }else{
                                    binding.addPostTRSwitch.setText("Post");
                                }
                            }
                        });


                        binding.addPostFirstFirstText.setText("Share");
                        binding.addPostFirstTextInputLayoutParcaAdi.setHint("General Products");
                        binding.addPostFirstTextInputLayoutParcaModeli.setHint("Models");
                        binding.addPostFirstTextInputLayoutAciklama.setHint("Explanation");
                        binding.addPostFirstTextInputLayoutAyrParca.setHint("Different Product");
                        binding.addPostFirstDevam.setText("Next");




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
            binding.addPostFirstProgressBar.setVisibility(View.GONE);
            binding.addPostFirstTextInputLayoutAyrParca.setVisibility(View.VISIBLE);
            binding.addPostFirstGeri.setVisibility(View.VISIBLE);
            binding.addPostTRSwitch.setVisibility(View.VISIBLE);
            binding.addPostFirstTextInputLayoutParcaAdi.setVisibility(View.VISIBLE);
            binding.addPostFirstTextInputLayoutAciklama.setVisibility(View.VISIBLE);
            binding.addPostFirstTextInputLayoutParcaModeli.setVisibility(View.VISIBLE);
            binding.addPostFirstDevam.setVisibility(View.VISIBLE);
            binding.addPostFirstFirstText.setVisibility(View.VISIBLE);

        } else {
            binding.addPostFirstProgressBar.setVisibility(View.VISIBLE);
            binding.addPostFirstTextInputLayoutAyrParca.setVisibility(View.GONE);
            binding.addPostFirstGeri.setVisibility(View.GONE);
            binding.addPostTRSwitch.setVisibility(View.GONE);
            binding.addPostFirstTextInputLayoutParcaAdi.setVisibility(View.GONE);
            binding.addPostFirstTextInputLayoutAciklama.setVisibility(View.GONE);
            binding.addPostFirstTextInputLayoutParcaModeli.setVisibility(View.GONE);
            binding.addPostFirstDevam.setVisibility(View.GONE);
            binding.addPostFirstFirstText.setVisibility(View.GONE);


        }



    }




    @Override
    public void onResume() {
        super.onResume();
        networkController();
    }





    public void parcaAdları(String dil){




        if(dil.equals("türkce")){

            ekleparcaAdiArrayList.add("Bilgisayar");
            ekleparcaAdiArrayList.add("Cep Telefonu");
            ekleparcaAdiArrayList.add("Televizyon");
            ekleparcaAdiArrayList.add("Oyun Konsolu");
            ekleparcaAdiArrayList.add("Vasıta(Otomobil-Suv-Motosiklet)");
            ekleparcaAdiArrayList.add("Kitap(Dergi-Gazete)");
            ekleparcaAdiArrayList.add("Diğer");

        }
        else if (dil.equals("ingilizce")){
            ekleparcaAdiArrayList.add("Computer");
            ekleparcaAdiArrayList.add("Mobile Phone");
            ekleparcaAdiArrayList.add("Television");
            ekleparcaAdiArrayList.add("Game Console");
            ekleparcaAdiArrayList.add("Vehicle (Car-Suv-Motorcycle)");
            ekleparcaAdiArrayList.add("Book (Magazine-Newspaper)");
            ekleparcaAdiArrayList.add("Other");

        }




    }

    public void parcaModelleri(String parcaAdi) {
        productDetailsArrayList.clear();


        if (parcaAdi.equals("Bilgisayar") || parcaAdi.equals("Computer")){

            productDetailsArrayList.add("ASUS PRIME H510M-D Intel H510 Soket 1200 DDR4 3200MHz(OC) M.2");
            productDetailsArrayList.add("ASUS PRIME H510M-E Intel H510 Soket 1200 DDR4 3200MHz(OC) M.2");
            productDetailsArrayList.add("ASUS TUF GAMING B550M-PLUS AMD B550 Soket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME B450M-K II AMD B450 Soket AM4 DDR4 4400MHz(O.C.)");
            productDetailsArrayList.add("ASUS TUF GAMING X570-PLUS (WI-FI) AM4 AMD Ryzen™ DDR4");
            productDetailsArrayList.add("ASUS ROG MAXIMUS Z690 HERO EVA Intel Z690 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS PRIME B550M-A WIFI II AMD B550 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING B660M-PLUS D4 Intel B660 socket DDR4 5333MHz");
            productDetailsArrayList.add("ASUS ROG STRIX B660-G GAMING WIFI Intel B660 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS PRIME B550-PLUS AMD B550 socket AM4 Ryzen DDR4 4600MHz");
            productDetailsArrayList.add("ASUS PRIME H410M-A INTEL H410 socket 1200 DDR4 2933MHz(O.C)");
            productDetailsArrayList.add("ASUS PRIME Z690-P WIFI Intel Z690 socket 1700 DDR5 6000MHz (OC)");
            productDetailsArrayList.add("ASUS PRIME Z690-P Intel Z690 socket 1700 128GB DDR5");
            productDetailsArrayList.add("ASUS ROG STRIX GAMING WIFI Z690-E Intel socket 1700 DDR5");
            productDetailsArrayList.add("ASUS PRIME H610M-E D4 Intel H610 socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS ROG CROSSHAIR VIII DARK HERO X570 AM4 AMD Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME A520M-E A520 socket AM4 AMD Ryzen DDR4 4600MHz");
            productDetailsArrayList.add("ASUS PRIME H610M-K D4 Intel H610 socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS TUF GAMING Z690-PLUS WF D4 Intel Z690 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING Z690-PLUS D4 Intel Z690 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS ROGSTRIX Z690-A GAMING WIFI D4 Intel Z690 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS B450M-DRAGON AMD B450 socket AM4 4400MHz(O.C.) M.2");
            productDetailsArrayList.add("ASUS PRIME H510M-A Intel H510 socket 1200 DDR4 3200MHz(OC) M.2");
            productDetailsArrayList.add("ASUS TUF GAMING B550-PLUS WF AMD B550 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME B550-PLUS AMD B550 socket AM4 Ryzen DDR4 4600MHz");
            productDetailsArrayList.add("ASUS PRIME H410M-A INTEL H410 socket 1200 DDR4 2933MHz(O.C)");
            productDetailsArrayList.add("ASUS PRIME Z690-P WIFI Intel Z690 socket 1700 DDR5 6000MHz (OC)");
            productDetailsArrayList.add("ASUS ROG STRIX GAMING WIFI Z690-E Intel socket 1700 DDR5");
            productDetailsArrayList.add("ASUS ROGSTRIX Z690-A GAMING WIFI Intel Z690  socket 1700 DDR5");
            productDetailsArrayList.add("ASUS TUF GAMING Z690-PLUS WIFI Intel Z690 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS TUF GAMING Z690-PLUS Intel Z690 socket 1700 DDR5 6000MHz");
            productDetailsArrayList.add("ASUS ROG MAXIMUS Z690 FORMULA Intel Z690 socket 1700");
            productDetailsArrayList.add("ASUS TUF GAMING B660M-PLUS WIFI Intel B660 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS ROG STRIX B660-A GAMING WF Intel socket 1700 DDR5 6000MHz");
            productDetailsArrayList.add("ASUS TUF GAMING B660M-PLUS WIFI D4 Intel B660 socket DDR4");
            productDetailsArrayList.add("ASUS PRIME B660-PLUS D4 Intel B660 socket 1700 DDR4 5066MHz");
            productDetailsArrayList.add("ASUS PRIME B660M-A WIFI D4 Intel B660 socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS ROG STRIX B660-A GAMING WF Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS PRIME Z690-P D4 Intel Z690 socket 1700128GB DDR4 5333MHz");
            productDetailsArrayList.add("ASUS TUF GAMING H670-PRO WIFI D4 Intel socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS TUF GAMING B660-PLUS WIFI D4 Intel socket 1700 DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING B660M-E D4 Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS PROART B660-CREATOR D4 Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("ASUS PRIME H670-PLUS D4 Intel H570 socket 1700 DDR4 2933MHz");
            productDetailsArrayList.add("ASUS PRIME H610M-D D4 Intel H610 socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS PRIME H610M-A D4 Intel H610 socket 1700 DDR4 3200MHz");
            productDetailsArrayList.add("ASUS PRIME B660M-A D4 Intel B660 socket 1700 DDR4 3200MHz (OC)");
            productDetailsArrayList.add("ASUS ROG CROSSHAIR VIII FORMULA X570 AMD socket AM4");
            productDetailsArrayList.add("ASUS ROG STRIX Z690-G GAMING WIFI Intel Z690 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS PRIME Z690-P WIFI D4 Intel PRIME Z690-P socket 1700 DDR4");
            productDetailsArrayList.add("ASUS ROG STRIX Z690-F GAMING WF Intel Z690 socket 1700 DDR5");
            productDetailsArrayList.add("ASUS PRIME Z690M-PLUS D4 Intel Z690 socket 1700 DDR4 5333MHz");
            productDetailsArrayList.add("ASUS PRIME Z690-A Intel Z690 socket 1700 DDR5 6000MHz (OC)");
            productDetailsArrayList.add("ASUS ProArt X570-CREATOR WIFI AMD X570 AM4 socket DDR4");
            productDetailsArrayList.add("ASUS PRIME B460I-PLUS INTEL B460 socket 1200 DDR4");
            productDetailsArrayList.add("ASUS EX-H510M-V3 INTEL H510 socket 1200 DDR4 3200MHz(O.C)");
            productDetailsArrayList.add("ASUS TUF GAMING A520M PLUS II socket AM4 AMD Ryzen DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING A520M PLUS WIFI socket AM4 AMD Ryzen DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING B560M-E Intel B560 socket 1200 DDR4 5000MHz");
            productDetailsArrayList.add("ASUS PRIME A520M-A II A520 socket AM4 AMD Ryzen DDR4 4800MHz4");
            productDetailsArrayList.add("ASUS PRIME B560M-K Intel B560 socket 1200 DDR4 4800MHz(OC) M.2");
            productDetailsArrayList.add("ASUS PRIME B560M-A Intel B560 socket 1200 DDR4 5000MHz(OC) M.2");
            productDetailsArrayList.add("ASUS PRIME H570M-PLUS Intel H570 socket 1200 DDR4 4600MHz");
            productDetailsArrayList.add("ASUS PRIME Z590-P Intel Z590 socket 1200 DDR4 5133MHz(OC) M.2");
            productDetailsArrayList.add("ASUS TUF GAMING Z590-PLUS Intel Z590 socket 1200 DDR4");
            productDetailsArrayList.add("ASUS ROG MAXIMUS XIII EXTREME Intel Z590 socket 1200");
            productDetailsArrayList.add("ASUS ROG STRIX B550 - XE GAMING WF AM4 socket AMD Ryzen");
            productDetailsArrayList.add("ASUS TUF GAMING B450-PLUS II AMD B450 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS ROG CROSSHAIR VIII HERO WF AMD X570 AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME B450M-A II AMD B450 socket AM4 DDR4 4400MHz(O.C.)");
            productDetailsArrayList.add("ASUS TUF GAMING B450M-PLUS II AMD B450 socket AM4 DDR4");
            productDetailsArrayList.add("ASUS TUF GAMING X570-PRO (WI-FI) AM4 X570 AM4 DDR4 5100MHz");
            productDetailsArrayList.add("ASUS TUF GAMING A520M-PLUS A520 socket AM4 AMD Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME A520M-K A520 socket AM4 AMD Ryzen DDR4 4600MHz");
            productDetailsArrayList.add("ASUS TUF GAMING B450M-PRO S Amd B450 AM4 socket DDR4");
            productDetailsArrayList.add("ASUS PRIME B550M-A (WI-FI) AMD B550 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME B460M-K INTEL B460 socket 1200 DDR4 2933MHz(O.C)");
            productDetailsArrayList.add("ASUS TUF H310M PLUS R2.0 Intel H310M socket 1151 DDR4");
            productDetailsArrayList.add("ASUS PRIME H310M-F R2.0 Intel H310 socket 1151 DDR4");
            productDetailsArrayList.add("ASUS ROG CROSSHAIR VIII IMPACT AMD X570 AM4 Ryzen");
            productDetailsArrayList.add("ASUS PRIME A320M-F A320 AM4 AMD Ryzen™ DDR4 3200(O.C.) MHz");
            productDetailsArrayList.add("ASUS PRIME X570-P AMD X570 AM4 Ryzen DDR4 4400MHz (O.C.)");
            productDetailsArrayList.add("ASUS ROG STRIX X570-F GAMING AMD X570 AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS PRIME H310M-K R2.0 Intel H310 socket 1151 DDR4");
            productDetailsArrayList.add("ASUS PRIME B365M-K Intel B365 socket 1151 DDR4");
            productDetailsArrayList.add("ASUS TUF B450M-PRO GAMING AMD B450 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS TUF B450-PRO GAMING AMD B450 socket AM4 Ryzen DDR4");
            productDetailsArrayList.add("ASUS TUF B450M-PLUS GAMING AMD B450 AM4 Ryzen™ DDR4");
            productDetailsArrayList.add("ASUS TUF B450-PLUS GAMING AMD B450 socket AM4 Ryzen™");
            productDetailsArrayList.add("ASUS PRIME A320M-E A320 AM4 AMD Ryzen™ DDR4 3200(O.C.) MHz");
            productDetailsArrayList.add("ASUS EX-A320M-GAMING A320  socket AM4 AMD Ryzen™DDR4");
            productDetailsArrayList.add("ASUS PRIME A320M-K A320 AM4 AMD Ryzen™ DDR4 3200(O.C.) MHz");
            productDetailsArrayList.add("GIGABYTE H510M H UD Intel H510 socket 1200 DDR4 3200MHz M.2");
            productDetailsArrayList.add("GIGABYTE H610M H 3200MHz DDR4 socket1700 M.2 HDMI D-Sub");
            productDetailsArrayList.add("GIGABYTE H610M H 3200MHz DDR4 socket1700 M.2 HDMI D-Sub");
            productDetailsArrayList.add("GIGABYTE B560M H 3200MHz (O.C) DDR4 socket  1200 M.2 HDMI D-Sub");
            productDetailsArrayList.add("GIGABYTE Z690 ULTRA DURABLE Intel socket1700 DDR5 6000MHz (O.C) M.2");
            productDetailsArrayList.add("GIGABYTE B450M H UD AMD B450 socket AM4 DDR4 3600MHZ (O.C)");
            productDetailsArrayList.add("GIGABYTE H310M H Intel H310 socket 8.-9.  1151 DDR4");
            productDetailsArrayList.add("GIGABYTE Z590 GAMING X Intel Z590 socket 1200 DDR4 5333(O.C)");
            productDetailsArrayList.add("GIGABYTE B460M GAMING HD Intel B460 socket 1200 DDR4 2933MHz");
            productDetailsArrayList.add("GIGABYTE Z590 AORUS MASTER Intel Z590 socket 1200 DDR4");
            productDetailsArrayList.add("GIGABYTE B450M S2H V2 AMD B450 socket AM4 DDR4");
            productDetailsArrayList.add("GIGABYTE GA-A520M-S2H AMD A520 socket AM4 DDR4 5100MHz");
            productDetailsArrayList.add("GIGABYTE B460M D2V Intel B460M socket 1200 DDR4 2933MHz M.2");
            productDetailsArrayList.add("GIGABYTE Z490I AORUS ULTRA Intel Z490 socket 1200 DDR4");
            productDetailsArrayList.add("GIGABYTE B550 GAMING X Amd B550 socket AM4 DDR4 4000MHz");
            productDetailsArrayList.add("GIGABYTE H310M H 2.0 Intel H310 socket  8.-9. 1151 DDR4");
            productDetailsArrayList.add("GIGABYTE GA-A320M-S2H A320 Socket AM4 AMD Ryzen™ DDR4");
            productDetailsArrayList.add("MSI PRO H610M-B DDR4 Intel H610 socket 1700 3200MHz (OC) M.2");
            productDetailsArrayList.add("MSI B450M-A PRO MAX Amd B450 socket AM4 DDR4 3466(OC) M.2");
            productDetailsArrayList.add("MSI H510M-A PRO Intel H510 socket 1200 DDR4 3200MHz (OC) M.2");
            productDetailsArrayList.add("MSI H310M PRO-VDH PLUS Intel H310 socket 1151 DDR4 2666");
            productDetailsArrayList.add("MSI PRO H410M-B Intel H410 socket 1200 DDR4 2933MHz M.2 ");
            productDetailsArrayList.add("MSI MAG B460M BAZOOKA Intel B460 socket 1200 DDR4 2933MHz");
            productDetailsArrayList.add("MSI B550M-A PRO Amd B550 socket AM4 DDR4 4600(O.C.) M.2 ");
            productDetailsArrayList.add("MSI PRO Z690-A Intel Z690 socket 1700 DDR4 5200MHz (OC) M.2");
            productDetailsArrayList.add("MSI MEG Z590 UNIFY Intel Z590 socket 1200 DDR4 5600(O.C.) M.2");
            productDetailsArrayList.add("MSI MEG Z590 ACE Intel Z590 socket1200 DDR4 5600(O.C.) M.2 ");
            productDetailsArrayList.add("MSI B550-A PRO Amd B550  socket AM4 DDR4 4400MHz M.2");
            productDetailsArrayList.add("MSI MAG B660 TOMAHAWK WIFI DDR4 Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("MSI PRO Z690-A WIFI Intel Z690 socket 1700 DDR5 6400MHz (OC)");
            productDetailsArrayList.add("MSI MEG Z590 ACE GOLD EDITION Intel Z590  1200 DDR4");
            productDetailsArrayList.add("MSI MPG Z590 GAMING PLUS Intel Z590 socket 1200 DDR4 5333MHz");
            productDetailsArrayList.add("MSI H310M PRO-M2 PLUS Intel H310  socket 1151 DDR4 2666 M.2");
            productDetailsArrayList.add("MSI MPG B550 GAMING PLUS Amd B550 socket AM4 DDR4 4400(OC)");
            productDetailsArrayList.add("MSI MPG Z690 EDGE WIFI Intel Z690 socket 1700 DDR5 6400MHz");
            productDetailsArrayList.add("MSI PRO H610M-G Intel H610 socket 1700 DDR4 3200MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B660M-E Intel B660 socket 1700 DDR4 4600MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B660M-B Intel B660 socket 1700 DDR4 4600MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B660M-A WIFI Intel B660  socket 1700 DDR4 4800MHz (OC)");
            productDetailsArrayList.add("MSI MAG B660M MORTAR WIFI Intel B660socket 1700 DDR5 6200MHz");
            productDetailsArrayList.add("MSI MPG Z690 EDGE WIFI Intel Z690 socket 1700 DDR4 5200MHz");
            productDetailsArrayList.add("MSI H510M PRO Intel H510 socket 1200 DDR4 3200MHz (OC) M.2");
            productDetailsArrayList.add("MSI MPG Z590 GAMING EDGE WIFI Intel Z590 socket 1200 DDR4");
            productDetailsArrayList.add("MSI B460M PRO-VDH Intel B460 socket 1200 DDR4 2933(O.C.) M.2");
            productDetailsArrayList.add("MSI MAG B460 TORPEDO socket 1200 DDR4 2933(O.C.) M.2 ");
            productDetailsArrayList.add("MSI MPG B550 GAMING CARBON WIFI Amd B550 socket AM4 DDR4 5100(O.C.) M.2 ");
            productDetailsArrayList.add("MSI B460M-A PRO Intel B460 socket 1200 DDR4 2933(OC) M.2 ");
            productDetailsArrayList.add("MSI B450M PRO-M2 MAX Amd B450 socket AM4 DDR4 3466(OC) M.2");
            productDetailsArrayList.add("MSI A320M-A PRO Amd A320 socket AM4 DDR4 3200(OC) ");
            productDetailsArrayList.add("MSI H610M BOMBER DDR4 3200MHz(OC) socket 1700 M.2 HDMI");
            productDetailsArrayList.add("MSI PRO B660M-P WIFI DDR4 Intel B660 socket 1700 DDR4 4600MHz");
            productDetailsArrayList.add("MSI B660 BOMBER DDR4 4600MHz (OC) DDR4 socket 1700 M.2 HDMI");
            productDetailsArrayList.add("MSI PRO B660M-P DDR4 Intel B660 socket 1700 DDR4 4600MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B550M-P GEN3 B550 socket AM4 DDR4 4400MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B550-P GEN3 B550 socket AM4 DDR4 4400MHz (OC) M.2");
            productDetailsArrayList.add("MSI H510I PRO WIFI 3200MHz DDR4 socket 1200 M.2 HDMI mITX");
            productDetailsArrayList.add("MSI MAG B660M BAZOOKA 4800MHz(OC) DDR4 socket 1700 M.2");
            productDetailsArrayList.add("MSI PRO B660-A Intel B660 socket 1700 DDR4 4800MHz (OC) M.2");
            productDetailsArrayList.add("MSI PRO B660M-A WIFI Intel B660 socket 1700 DDR5 6200MHz (OC)");
            productDetailsArrayList.add("MSI MAG H670 TOMAHAWK WIFI Intel H670 socket 1700 DDR4");
            productDetailsArrayList.add("MSI PRO B660M-A DDR4 Intel B660 socket 1700 DDR4 4600MHz (OC)");
            productDetailsArrayList.add("MSI PRO B660M-A DDR4 Intel B660 socket 1700 DDR4 4600MHz (OC)");
            productDetailsArrayList.add("MSI MAG Z690 TOMAHAWK WIFI Intel Z690 socket 1700 DDR5");
            productDetailsArrayList.add("MSI PRO B660M-G Intel B660 socket 1700 DDR4 4600MHz (OC) M.2");
            productDetailsArrayList.add("MSI MAG B660M MORTAR Intel B660 socket 1700 DDR5 6200MHz");
            productDetailsArrayList.add("MSI MAG B660 TOMAHAWK WIFI Intel B660 socket 1700 DDR5");
            productDetailsArrayList.add("MSI MAG Z690M MORTAR WIFI Intel Z690 socket 1700 DDR5 6200MHz");
            productDetailsArrayList.add("MSI MAG B660M MORTAR DDR4 Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("MSI MAG B660M MORTAR WIFI DDR4 Intel B660 socket 1700 DDR4");
            productDetailsArrayList.add("MSI H510M PRO-E Intel H510 socket 1200 DDR4 3200MHz (OC)");
            productDetailsArrayList.add("MSI PRO Z690-P DDR4 Intel Z690  socket 1700 DDR4 5200MHz(OC) M.2");
            productDetailsArrayList.add("MSI MAG Z690 TORPEDO Intel Z690 socket 1700 DDR5 6400MHz(OC)");
            productDetailsArrayList.add("MSI MAG Z690 TOMAHAWK WIFI DDR4 Intel Z690 socket 1700 DDR4");
            productDetailsArrayList.add("MSI MEG X570S UNIFY-X MAX Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI PRO Z690-A WIFI Intel Z690 socket 1700 DDR4 5200MHz (OC)");
            productDetailsArrayList.add("MSI PRO Z690-A Intel Z690 socket 1700 DDR5 6400MHz (OC) M.2");
            productDetailsArrayList.add("MSI MPG Z690 FORCE WIFI Intel Z690 socket 1700 DDR5 6666MHz");
            productDetailsArrayList.add("MSI MPG Z690 CARBON WIFI Intel Z690 socket 1700 DDR5 6666MHz");
            productDetailsArrayList.add("MSI B560-A PRO Intel B560 socket 1200 DDR4 5066MHz (OC) M.2");
            productDetailsArrayList.add("MSI MPG X570S CARBON MAX WIFI Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI MPG X570S EDGE MAX WIFI Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI MEG X570S ACE MAX Amd X570 socket AM4 DDR4 5300MHz");
            productDetailsArrayList.add("MSI MAG X570S TORPEDO MAX Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI MAG X570S TOMAHAWK MAX WIFI Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI A320M PRO-VH Amd A320 socket AM4 DDR4 3200(OC) M.2");
            productDetailsArrayList.add("MSI MAG B560 TORPEDO Intel B560 socket 1200 DDR4 5066MHz (OC)");
            productDetailsArrayList.add("MSI MAG B560M MORTAR Intel B560 socket 1200 DDR4 5066MHz");
            productDetailsArrayList.add("MSI MAG B560 TOMAHAWK WIFI Intel B560 socket 1200 DDR4");
            productDetailsArrayList.add("MSI Z590 PLUS Intel Z590 socket 1200 DDR4 5333MHz (OC) M.2");
            productDetailsArrayList.add("MSI MAG Z590 TOMAHAWK WIFI Intel Z590 socket 1200 DDR4");
            productDetailsArrayList.add("MSI B450 TOMAHAWK MAX II Amd B450 socket AM4 DDR4 4333MHz");
            productDetailsArrayList.add("MSI B560M PRO WIFI Intel B560 socket 1200 DDR4 5200MHz (OC)");
            productDetailsArrayList.add("MSI MAG B560M MORTAR WIFI Intel B560 socket 1200 DDR4 5066MHz");
            productDetailsArrayList.add("MSI B560M-A PRO Intel B560 socket 1200 DDR4 5200MHz (OC) M.2");
            productDetailsArrayList.add("MSI B560M PRO-E Intel B560 socket 1200 DDR4 4800MHz (OC) M.2 ");
            productDetailsArrayList.add("MSI B560M PRO Intel B560 socket 1200 DDR4 5200MHz (OC) M.2");
            productDetailsArrayList.add("MSI MAG B560M BAZOOKA Intel B560 socket 1200 DDR4 5066MHz");
            productDetailsArrayList.add("MSI MPG Z590 GAMING FORCE Intel Z590 socket 1200 DDR4");
            productDetailsArrayList.add("MSI MAG Z590 TORPEDO Intel Z590 socket 1200 DDR4 5333(O.C.) M.2");
            productDetailsArrayList.add("MSI B560M PRO-VDH Intel B560M socket 1200 DDR4 5066(O.C.) M.2");
            productDetailsArrayList.add("MSI B560M PRO-VDH WIFI Intel B560M socket 1200 DDR4 5066(O.C.) M.2");
            productDetailsArrayList.add("MSI MEG B550 UNIFY Amd B550 socket AM4 DDR4 5600MHz (OC) M.2");
            productDetailsArrayList.add("MSI MPG Z590-A PRO Intel Z590 socket 1200 DDR4 5333MHz (OC)");
            productDetailsArrayList.add("MSI MPG Z590 PRO WIFI Intel Z590 socket 1200 DDR4 5333MHz (OC)");
            productDetailsArrayList.add("MSI MPG Z590 GAMING CARBON WIFI Intel Z590  socket 1200 DDR4");
            productDetailsArrayList.add("MSI B550M PRO Amd B550 socketAM4 DDR4 4600(O.C.) M.2");
            productDetailsArrayList.add("MSI MEG B550 UNIFY-X Amd B550 socket AM4 DDR4 5800MHz(OC) M.2");
            productDetailsArrayList.add("MSI B550M PRO-VDH Amd B550 socket AM4 DDR4 4400(O.C.) M.2");
            productDetailsArrayList.add("MSI MAG A520M VECTOR WIFI Amd A520 socket AM4 DDR4 4600");
            productDetailsArrayList.add("MSI A520M PRO Amd A520  socket AM4 DDR4 4600 MHz(OC) M.2");
            productDetailsArrayList.add("MSI A520M-A PRO Amd A520 socket AM4 DDR4 4600 MHz(OC) M.2");
            productDetailsArrayList.add("MSI MAG X570 TOMAHAWK WIFI Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI H410M PRO Intel H410 socket 1200 DDR4 2933MHz M.2 ");
            productDetailsArrayList.add("MSI B550M PRO-VDH WIFI Amd B550 socket AM4 DDR4 4400(O.C.) M.2");
            productDetailsArrayList.add("MSI MAG B460 TOMAHAWK Intel B460 socket 1200 DDR4 2933MHz");
            productDetailsArrayList.add("MSI MPG B550 GAMING EDGE WIFI Amd B550 socket AM4 DDR4");
            productDetailsArrayList.add("MSI MAG B550M MORTAR WIFI Amd B550M socket AM4 DDR4 4400(OC)");
            productDetailsArrayList.add("MSI MAG B550M MORTAR Amd B550M socket AM4 DDR4 4400(OC)");
            productDetailsArrayList.add("MSI MAG B550 TOMAHAWK Amd B550 socket AM4 DDR4 5100(OC)");
            productDetailsArrayList.add("MSI B460M PRO-VDH WIFI Intel B460 socket 1200 DDR4 2933(OC)");
            productDetailsArrayList.add("MSI MAG B460M MORTAR WIFI Intel B460 socket 1200 DDR4 2933(OC) M.2 ");
            productDetailsArrayList.add("MSI MAG B460M MORTAR Intel B460 socket 1200 DDR4 2933(OC)");
            productDetailsArrayList.add("MSI Z490-A PRO Intel Z490 socket 1200 DDR4 4800(OC) M.2 ");
            productDetailsArrayList.add("MSI MAG Z490 TOMAHAWK Intel Z490 socket 1200 DDR4 4800(OC)");
            productDetailsArrayList.add("MSI X299 PRO Intel X299 socket 2066 DDR4 4200(OC) M.2 ");
            productDetailsArrayList.add("MSI MEG X570 UNIFY Amd X570 socket AM4 DDR4 4600(OC) M.2");
            productDetailsArrayList.add("MSI A320M-A PRO MAX Amd A320 socket AM4 DDR4 3200(OC) M.2");
            productDetailsArrayList.add("MSI B450-A PRO MAX Amd B450 socket AM4 DDR4 3466(OC) M.2");
            productDetailsArrayList.add("MSI B450 GAMING PLUS MAX Amd B450 socket AM4 DDR4 3466(OC)");
            productDetailsArrayList.add("MSI B450 TOMAHAWK MAX Amd B450 socket AM4 DDR4 3466(OC)");
            productDetailsArrayList.add("MSI X570-A PRO Amd X570 socket AM4 DDR4 4000(OC) M.2 ");
            productDetailsArrayList.add("MSI MPG X570 GAMING PLUS Amd X570 socket AM4 DDR4 4400(OC)");
            productDetailsArrayList.add("MSI MPG X570 GAMING EDGE WIFI Amd X570 socket AM4 DDR4");
            productDetailsArrayList.add("MSI MEG X570 ACE Amd X570 socket AM4 DDR4 4600(OC) M.2 ");
            productDetailsArrayList.add("MSI B365M PRO-VH Intel B365 socket 1151 DDR4 2666 M.2");
            productDetailsArrayList.add("MSI Z390-A PRO Intel Z390 socket 1151 DDR4 4400(OC) M.2 ");
            productDetailsArrayList.add("ASUS TUF GeForce GTX 1660 TI EVO OC 6GB GDDR6 192Bit DX12");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3060 GAMING V2 OC 12GB GDDR6");
            productDetailsArrayList.add("ASUS GeForce GT710 1GB GDDR5 32Bit Nvidia DX12 ");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1650 GAMING OC 4GB GDDR6 128Bit");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3080 GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 3060 OC V2 12GB GDDR6 192Bit NVIDIA");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3080 Ti GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1650 4GD6 GAMING 4GB GDDR6 128Bit Nvidia");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1660 SUPER GAMING OC 6GB GDDR6");
            productDetailsArrayList.add("ASUS GeForce GTX 1660 SUPER EVO OC 6GB GDDR6 192Bit DX12");
            productDetailsArrayList.add("ASUS GeForce GTX 1050 Ti CERBERUS OC 4GB GDDR5 128Bit");
            productDetailsArrayList.add("Asus GeForce GT1030 GDDR5 OC 2GB 64Bit NVIDIA DX12 ");
            productDetailsArrayList.add("Asus GeForce GT730 2GB GDDR5 64Bit NVIDIA DX12 ");
            productDetailsArrayList.add("ASUS GeForce GTX 1050 TI CERBERUS 4GB GDDR5 128Bit");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1650 4GD6-P GAMING 4GB GDDR6 128Bit Nvidia DX12");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3090 GAMING OC 24GB GDDR6X 384Bit");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1650 SUPER GAMING OC 4GB GDDR6");
            productDetailsArrayList.add("Asus GeForce GT1030 GDDR5 2GB 64Bit NVIDIA DX12 ");
            productDetailsArrayList.add("ASUS GeForce GTX1050 Ti GDDR5 4GB 128Bit NVIDIA ");
            productDetailsArrayList.add("ASUS GeForce TUF-RTX3050-O8G-GAMING 8GB GDDR6 128Bit");
            productDetailsArrayList.add("ASUS GeForce RTX 3080 NOCTUA 10GB OC GDDR6X 320Bit DX12");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 3060 V2 12GB GDDR6 192Bit NVIDIA");
            productDetailsArrayList.add("Asus GeForce GTX 1650 4GB GDDR5 128Bit DX12 Nvidia");
            productDetailsArrayList.add("Asus GeForce GT730 SL-2GD5-BRK-E DDR5 2GB 64Bit NVIDIA DX12");
            productDetailsArrayList.add("ASUS GeForce RTX 3070 8GB GDDR6 256Bit DX12 Nvidia ");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1650 4GD6 GAMING OC 4GB GDDR6 128Bit");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3090 TI GAMING OC 24GB GDDR6X 384Bit");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 2060 EVO 12GB GDDR6 192Bit Nvidia ");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 3050 8GB GDDR6 128Bit Nvidia ");
            productDetailsArrayList.add("ASUS GeForce RTX 3050 Phoenix 8GB GDDR6 128Bit Nvidia");
            productDetailsArrayList.add("ASUS GeForce RTX 3050 Phoenix 8GB GDDR6 128Bit Nvidia");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1660 TI EVO 6GB GDDR6 192Bit DX12");
            productDetailsArrayList.add("ASUS GeForce GT1030 DDR4 2GB 64Bit NVIDIA ");
            productDetailsArrayList.add("ASUS GeForce ROG STRIX RTX 3070 GAMING V2 OC 8GB GDDR6");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3070 GAMING OC 8GB GDDR6 256Bit");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 3060 Ti MIN OC 8GB GDDR6 256Bit DX12");
            productDetailsArrayList.add("Asus GeForce GT730 DDR5 2GB 64Bit NVIDIA DX12 ");
            productDetailsArrayList.add("ASUS GeForce DUAL RTX 3060 Ti OC 8GB GDDR6 256Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce RTX 3060 Phoenix 12GB GDDR6 192Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3070 Ti GAMING OC 8GB GDDR6X 256Bit");
            productDetailsArrayList.add("ASUS GeForce ROG STRIX RTX 3070 Ti GAMING OC 8GB GDDR6X");
            productDetailsArrayList.add("ASUS GeForce RTX 3060 Phoenix 12GB GDDR6 192Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce GTX 1650 Phoenix OC 4GB GDDR6 128Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce ROG STRIX RTX 3090 GAMING OC 24GB GDDR6X");
            productDetailsArrayList.add("ASUS GeForce TUF RTX 3090 GAMING 24GB GDDR6X 384Bit DX12 Nvidia ");
            productDetailsArrayList.add("ASUS GeForce TUF GTX 1660 SUPER GAMING 6GB GDDR6 192Bit");
            productDetailsArrayList.add("ASUS GeForce GT710 1GB GDDR5 32Bit Nvidia DX12");
            productDetailsArrayList.add("ASUS GeForce GT710 2GB DDR5 64Bit Nvidia DX12 ");
            productDetailsArrayList.add("ASUS GeForce GTX 1660 SUPER Phoenix OC 6GB DDR6 192Bit DX12");
            productDetailsArrayList.add("ASUS GeForce GTX 1650 Phoenix OC 4GB GDDR5 128Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce DUAL GTX1650 4GB GDDR5 128Bit DX12 Nvidia ");
            productDetailsArrayList.add("ASUS GeForce DUAL GTX 1650 OC 4GB GDDR5 128Bit DX12 Nvidia");
            productDetailsArrayList.add("ASUS GeForce GT1030 GDDR5 2GB 64Bit NVIDIA ");
            productDetailsArrayList.add("EVGA GeForce RTX 3090 FTW3 ULTRA GAMING 24GB GDDR6X");
            productDetailsArrayList.add("EVGA RTX 3080 XC3 ULTRA GAMING 12GB GDDR6X RGB LED");
            productDetailsArrayList.add("EVGA GeForce RTX 3070 FTW3 ULTRA GAMING 8GB GDDR6 256Bit");
            productDetailsArrayList.add("EVGA GeForce GTX 1660 SC ULTRA GAMING 6GB GDDR5 192Bit Nvidia");
            productDetailsArrayList.add("EVGA GeForce RTX 3050 XC GAMING 8GB GDDR6 128Bit Nvidia");
            productDetailsArrayList.add("EVGA GeForce RTX 3090 Ti FTW3 ULTRA GAMING 24GB GDDR6X");
            productDetailsArrayList.add("EVGA GeForce RTX 3080 Ti FTW3 ULTRA GAMING 12GB GDDR6X");
            productDetailsArrayList.add("EVGA GeForce GTX 1660 SUPER SC ULTRA GAMING 6GB GDDR6");
            productDetailsArrayList.add("EVGA RTX 3090 Ti FTW3 BLACK GAMING 24GB GDDR6X ARGB ");
            productDetailsArrayList.add("EVGA RTX 3080 FTW3 ULTRA GAMING 12GB GDDR6X ARGB");
            productDetailsArrayList.add("EVGA GeForce RTX 2060 SC OVERCLOCKED 6GB GDDR6 192 bit Nvidia");
            productDetailsArrayList.add("EVGA GeForce RTX 3090 Ti FTW3 GAMING 24GB GDDR6X 384Bit");
            productDetailsArrayList.add("EVGA GeForce RTX 3090 XC3 ULTRA GAMING 24GB GDDR6X 384Bit ARGB ");
            productDetailsArrayList.add("EVGA GeForce RTX 3080 Ti XC3 ULTRA GAMING 12GB GDDR6X");
            productDetailsArrayList.add("EVGA GeForce RTX 3080 FTW3 ULTRA GAMING 10GB GDDR6X");
            productDetailsArrayList.add("GIGABYTE GeForce GTX 1660 TI OC 6GB GDDR6 192Bit DX12 Nvidia");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3090 Ti GAMING 24G 24GB GDDR6X 384 Bit");
            productDetailsArrayList.add("GIGABYTE GEFORCE RTX 2060 GAMING OC 12GB GDDR6 192Bit");
            productDetailsArrayList.add("GIGABYTE GEFORCE RTX 3080 GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3070 Ti MASTER 8GB GDDR6X 256Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 Ti EAGLE OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3070 Ti GAMING OC 8GB GDDR6X 256Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 Ti GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3090 GAMING OC 24GB GDDR6X 384Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3070 AORUS MASTER 8GB GDDR6");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 GAMING OC 10GB GDDR6X 320Bit");
            productDetailsArrayList.add("GIGABYTE GeForce GTX 1660 SUPER OC 6GB GDDR6 192Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 Ti GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3060 TWIN EDGE GAMING OC 12GB GDDR6");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3080 GAMING TRINTY OC 10GB GDDR6X");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3070 TWIN EDGE GAMINGOC 8GB GDDR6");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3060 Ti TWINEDGE GAMING 8GB GDDR6");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3080 Ti GAMING AMP HOLO 12GB GDDR6X");
            productDetailsArrayList.add("ZOTAC GeForce RTX 3080 Ti GAMING TRINTY OC 12GB GDDR6X");
            productDetailsArrayList.add("ZOTAC GTX 1660 Super GDDR6 6GB 192Bit Nvidia GeForce DX12 ");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 GAMING OC 10GB GDDR6X 320Bit");
            productDetailsArrayList.add("GIGABYTE GeForce GTX 1660 SUPER OC 6GB GDDR6 192Bit");
            productDetailsArrayList.add("GIGABYTE GeForce RTX 3080 Ti GAMING OC 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6600-8G 8GB 128Bit AMD ");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6400 4GB GDDR6 64Bit AMD  ");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6500 XT OC 4GB GDDR6 64Bit AMD ");
            productDetailsArrayList.add("ASUS Radeon ROG STRIX RX560 GAMING 4GB GDDR5 128 Bit AMD");
            productDetailsArrayList.add("ASUS ROG STRIX Radeon RX 6650 XT OC ARGB 8GB GDDR6 128 Bit ");
            productDetailsArrayList.add("ASUS TUF GAMING Radeon RX 6950 XT OC Edition 16GB GDDR6");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6650 XT OC 8GB GDRR6 128Bit AMD ");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6750 XT OC 12GB GDDR6 192Bit AMD");
            productDetailsArrayList.add("ASUS RADEON ROG STRIX RX 6750 XT OC GAMING 12GB GDDR6");
            productDetailsArrayList.add("ASUS RADEON RX560 DUAL 4GB GDDR5 128 Bit AMD");
            productDetailsArrayList.add("ASUS RADEON RX 6400 4GB GDDR6 64Bit AMD ");
            productDetailsArrayList.add("ASUS RADEON 550 Phoenix 2GB GDDR5 64Bit AMD ");
            productDetailsArrayList.add("ASUS RADEON TUF RX 6500 XT GAMING OC 4GB GDDR6 64Bit AMD");
            productDetailsArrayList.add("ASUS RADEON ROG STRIX RX 6600 XT OC GAMING 8GB GDDR6");
            productDetailsArrayList.add("ASUS RADEON DUAL RX 6600 XT OC 8GB GDDR6 128Bit AMD ");
            productDetailsArrayList.add("ASUS RADEON TUF RX 6800 GAMING OC 16GB GDDR6 256Bit");
            productDetailsArrayList.add("ASUS RADEON TUF RX 6800 XT OC 16GB GAMING GDDR6 256Bit DX12");
            productDetailsArrayList.add("ASUS RADEON RX 550 4G EVO Phoenix 4G GDD5 128bit DX12 AMD");
            productDetailsArrayList.add("MSI RADEON RX 6800 XT GAMING Z TRIO 16GB GDDR6 256bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6600 ARMOR 8G 8GB GDDR6 128Bit AMD ");
            productDetailsArrayList.add("MSI RADEON RX 6700 XT MECH 2X 12GB GDDR6 192Bit AMD ");
            productDetailsArrayList.add("MSI RADEON RX 6500 XT MECH 2X 4G OC GDDR6 64bit DX12 AMD");
            productDetailsArrayList.add("MSI RADEON RX 6900 XT GAMING Z TRIO 16GB GDDR6 256bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6800 GAMING X TRIO 16GB GDDR6 256bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6750 XT GAMING X TRIO 12G GDDR6 192Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6750 XT MECH 2X 12G OC GDDR6 192Bit AMD ");
            productDetailsArrayList.add("MSI RADEON RX 6650 XT MECH 2X OC 8GB GDDR6 128 Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6400 AERO ITX 4GB GDDR6 64 Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6950 XT GAMING TRIO 16GB GDDR6 256 Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6600 XT MECH 2X 8G OC GDDR6 128Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6600 XT GAMING X 8GB GDDR6 128Bit AMD ");
            productDetailsArrayList.add("MSI RADEON RX 6700 XT MECH 2X 12G OC GDDR6 192Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6700 XT GAMING X 12GB GDDR6 192Bit AMD");
            productDetailsArrayList.add("MSI RADEON RX 6800 XT GAMING X TRIO 16GB GDDR6 256bit AMD");
            productDetailsArrayList.add("GIGABYTE RADEON RX 6600 XT EAGLE 8GB GDDR6 128Bit AMD");
            productDetailsArrayList.add("GIGABYTE RADEON RX 6800 GAMING OC 16GB GDDR6 256Bit");
            productDetailsArrayList.add("GIGABYTE RADEON RX 6700 XT GAMING OC 12GB GDDR6 192Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 VENTUS 2X OC 12GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3090 TI SUPRIM X 24GB GDDR6X 384bit");
            productDetailsArrayList.add("MSI GEFORCE GTX 1650 4GT LP OC 4GB GDDR5 128bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 2060 VENTUS 12G OC 12GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 SUPER GAMING X 6GB GDDR6 192bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3050 GAMING X 8G GDDR6 128Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3050 VENTUS 2X 8GB GDDR6 128Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 TI GAMING Z TRIO 8GB LHR GDDR6 256bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 2060 VENTUS GP OC 6GB GDDR6 192Bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GTX 1650 VENTUS XS 4G OC 4GB GDDR5 128bit");
            productDetailsArrayList.add("MSI GEFORCE GTX 1050 TI 4GT LP 4GB GDDR5 128bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 AERO ITX 12G OC 12GB GDDR6 192Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3090 TI GAMING X TRIO 24G 24GB GDDR6X 384Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 GAMING Z TRIO 12GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 TI GAMING X TRIO 12GB GDDR6X");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 SUPER VENTUS XS OC 6GB GDDR6 192bit");
            productDetailsArrayList.add("MSI GEFORCE GT 710 1GD3H LP 1GB DDR3 64bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 2060 VENTUS GP 6GB GDDR6 192Bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 VENTUS 3X PLUS 10G LHR GDDR6X 320Bit");
            productDetailsArrayList.add("MSI GeForce RTX 3070 SUPRIM 8GB GDDR6 256 Bit LHR ");
            productDetailsArrayList.add("MSI GeForce GTX 1650 D6 AERO ITX OCV1 4GB GDDR6 128 Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3090 TI BLACK TRIO 24GB GDDR6X 384 Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE GTX 1650 D6 AERO ITX 4G OC GDDR6 128bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GT 730 2GD3 LP 2GB DDR3 64Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3050 VENTUS 2X 8GB OC GDDR6 128Bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 GAMING Z TRIO 12G LHR GDDR6X 384Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 VENTUS 3X PLUS 12G OC LHR GDDR6X");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 VENTUS 3X PLUS 10G OC LHR GDDR6X");
            productDetailsArrayList.add("MSI GEFORCE GT 730 2GD3H LP 2GB DDR3 64bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3050 AERO ITX 8G OC GDDR6 128Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 VENTUS 3X OC LHR 8GB GDDR6 256Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3050 AERO ITX 8GB GDDR6 128Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 TI VENTUS 3X OC LHR 8GB GDDR6");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 GAMING Z TRIO 8G LHR 8GB GDDR6 256bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 VENTUS 2X 8G OC LHR 8GB GDDR6 256bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 SUPRIM X LHR 10GB GDDR6X 320bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 TI VENTUS 3X 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 SUPRIM X 8G LHR GDDR6 256bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 TI GAMING X LHR 8GB GDDR6 256Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 TI VENTUS 2X 8G OCV1 LHR GDDR6");
            productDetailsArrayList.add("MSI GEFORCE GT 730 4GB DDR3 64Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 GAMING Z TRIO 10G LHR GDDR6X 320Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 TI GAMING X TRIO 8GB GDDR6X");
            productDetailsArrayList.add("MSI GEFORCE RTX 3070 TI SUPRIM X 8GB GDDR6X 256bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 TI VENTUS 3X 12G OC GDDR6X");
            productDetailsArrayList.add("MSI GEFORCE RTX 3080 TI SUPRIM X 12GB GDDR6X 384Bit");
            productDetailsArrayList.add("MSI GEFORCE GT 710 2GD3H H2D 2GB DDR3 64Bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 VENTUS 3X 12G OC 12GB GDDR6 192Bit");
            productDetailsArrayList.add("MSI GEFORCE RTX 3060 GAMING X 12GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE RTX 3090 GAMING X TRIO 24G 24GB GDDR6X 384Bit");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 SUPER GAMING 6GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 VENTUS XS 6G OC 6GB GDDR5 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 TI VENTUS XS 6G OC 6GB GDDR6");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 TI VENTUS XS 6G 6GB GDDR6 192bit");
            productDetailsArrayList.add("MSI GEFORCE GTX 1660 TI ARMOR 6G OC 6GB GDDR6 192bit NVIDIA");
            productDetailsArrayList.add("MSI GEFORCE GT 1030 2GHD4 LP OC 2GB DDR4 64bit NVIDIA ");
            productDetailsArrayList.add("MSI GEFORCE GTX 1050 TI AERO ITX 4G OCV1 4GB GDDR5 128bit");
            productDetailsArrayList.add("MSI N730-2GD3V2 2GB DDR3 128bit NVIDIA");
            productDetailsArrayList.add("Intel Core i3 12100F socket 1700 12.generation 3.30GHz 12MB 10nm");
            productDetailsArrayList.add("Intel Core i5 11400F socket 1200 11.generation 2.60GHz 12MB  14nm");
            productDetailsArrayList.add("Intel Core i3 10100F socket 1200 10.generation 3.60GHz 6MB ");
            productDetailsArrayList.add("Intel Core i7 12700F socket 1700 12.generation 25MB 10nm ");
            productDetailsArrayList.add("Intel Core i5 12400 socket 1700 12.generation 2.50GHz 18MB  10nm");
            productDetailsArrayList.add("Intel Core i5 12600K socket 1700 12.generation 3.70GHz 20MB  10nm");
            productDetailsArrayList.add("Intel Core i5 9400 socket 1151 - 9.generation 2.9GHz 9MB  14nm");
            productDetailsArrayList.add("Intel Core i9 11900KF socket 1200 11.generation 3.50GHz 16MB  14nm");
            productDetailsArrayList.add("Intel Core i5 11600K socket 1200 11.generation 3.90GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i5 12400F socket 1700 12.generation 2.50GHz 18MB 10nm");
            productDetailsArrayList.add("Intel Core i7 12700K socket 1700 12.generation 2.70GHz 25MB 10nm");
            productDetailsArrayList.add("Intel Core i9 12900KS socket 1700 3.4GHz 12. 30MB");
            productDetailsArrayList.add("Intel Core i9 12900 socket 1700 12.generation  3.20GHz 30MB 10nm");
            productDetailsArrayList.add("Intel Core i3 10105F socket 1200 10.generation  3.70GHz 6MB 14nm");
            productDetailsArrayList.add("Intel Core i9 12900F socket 1700 12.generation  2.40GHz 30MB 10nm");
            productDetailsArrayList.add("Intel Core i7 12700  socket 1700 12.generation  2.10GHz 25MB  10nm");
            productDetailsArrayList.add("Intel Core i5 10600KF socket 1200 10generation. 4.10GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10900F socket 1200 10.generation  2.80 GHz 20MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10850K socket 1200 10.generation  3.60 GHz 20MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10900  socket 1200 10.generation  2.80 GHz 20MB 14nm");
            productDetailsArrayList.add("Intel Core i5 10400F socket 1200 10.generation  2.9GHz 12MBb 14nm");
            productDetailsArrayList.add("Intel Core i3 12100  socket 1700 12.generation  3.30GHz 12MB 10nm");
            productDetailsArrayList.add("Intel Core i7 12700KF socket 1700 12generation. 2.70GHz 25MB 10nm");
            productDetailsArrayList.add("Intel Core i9 11900  socket 1200 11.generation  2.50GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i5 11600KF socket 1200 11generation  3.90GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i5 10400  socket 1200 10.generation  2.9GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i3 10320  socket 1200 10.generation  3.80GHz 8MB ");
            productDetailsArrayList.add("Intel Core i5 12500  socket 1700 12.generation  3 GHz 18MB 10nm");
            productDetailsArrayList.add("Intel Core i9 12900KF socket 1700 12generation. 3.20GHz 30MB 10nm");
            productDetailsArrayList.add("Intel Core i9 12900K socket 1700 12.generation  3.20GHz 30MB 10nm");
            productDetailsArrayList.add("Intel Core i5 12600KF socket 1700 12generation. 3.70GHz 20MB 10nm");
            productDetailsArrayList.add("Intel Core i3 10105  socket 1200 10.generation  3.70GHz 6MB 14nm");
            productDetailsArrayList.add("Intel Core i9 11900K socket 1200 11.generation  3.50GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i7 11700KF socket 1200 11generation. 3.60GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i5 11600 socket 1200 11.generation  2.80GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i5 11500  socket 1200 11.generation  2.70GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i7 11700F socket 1200 11.generation  2.50GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i7 11700  socket 1200 11.generation  2.50GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i5 11400  socket 1200 11.generation  2.60GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i7 11700K socket 1200 11.generation  3.60GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10850K socket 1200 10.generation  3.60 GHz 20MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10940X socket 2066 10.generation  3.30 GHz 19.25M");
            productDetailsArrayList.add("Intel Core i9 10980XE socket 2066 3.0GHz 24.75MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10900K socket 1200 10.generation 3.70 GHz 20MB  14nm");
            productDetailsArrayList.add("Intel Core i9 10900KF socket 1200 10.generation 3.70 GHz 20MB 14nm");
            productDetailsArrayList.add("Intel Core i7 10700K socket 1200 10.generation 3.80 GHz 16MB  14nm");
            productDetailsArrayList.add("Intel Core i7 10700KF socket 1200 10.generation 3.80 GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i7 10700 socket 1200 10.generation 2.90GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i7 10700F socket 1200 10.generation 2.90GHz 16MB 14nm");
            productDetailsArrayList.add("Intel Core i5 10600K socket 1200 10.generation 4.10GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i5 10600 socket 1200 10.generation 3.30GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i5 10500 socket 1200 10.generation 3.10GHz 12MB 14nm");
            productDetailsArrayList.add("Intel Core i3 10100 socket 1200 10.generation 3.6GHz 6MB 14nm");
            productDetailsArrayList.add("Intel Core i9 10900X socket 2066 3.70GHz 19.25MB 14nm");
            productDetailsArrayList.add("AMD Ryzen™5 5500   AM4 4.2 GHz 19MB 65W 7nm");
            productDetailsArrayList.add("AMD Ryzen™5 5600X  AM4 Wraith Stealth 3.7GHz 32MB 65W");
            productDetailsArrayList.add("AMD Ryzen™5 5600   AM4 3.5GHz 32MB 65W 7nm");
            productDetailsArrayList.add("AMD Ryzen™7 5700X  AM4 3.4GHz 32MB 65W 7nm");
            productDetailsArrayList.add("AMD Ryzen™5 4500   AM4 4.1GHz 11MB 165W 7nm");
            productDetailsArrayList.add("AMD Ryzen™7 5800X  AM4 3.8GHz 32MB 105W 7nm");
            productDetailsArrayList.add("AMD Ryzen™7 5700G  AM4 3.8 GHz 20MB 65W 7nm");
            productDetailsArrayList.add("AMD Ryzen™9 5950X  AM4 3.4GHz 64MB 105W 7nm");
            productDetailsArrayList.add("AMD Ryzen™5 5600G  AM4 3.9 GHz 19MB 65W 7nm");
            productDetailsArrayList.add("AMD Ryzen™7 3800XT AM4 4.7 GHz 36MB 105W 7nm");
            productDetailsArrayList.add("AMD Ryzen™7 3800X  AM4+WraithPrism(RGB) 3.9 GHz");
            productDetailsArrayList.add("AMD Ryzen™7 3700X  AM4+Wraith Prism(RGB) 3.6 GHz");
            productDetailsArrayList.add("POWERBOOST VK-P1900B 500W USB 3.0 MESH FIXED 4x120mm");
            productDetailsArrayList.add("POWERBOOST VK-P15B 600W 80 PLUS USB 3.0 MESH 4x120mm");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX TD500 650W 80PLUS BRONZE");
            productDetailsArrayList.add("MSI MAG FORGE 100M TEMPERED GLASS 2x120mm RGB  MidT");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX TD500 MESH 4x120mm ARGB");
            productDetailsArrayList.add("RAMPAGE REACTION 4x120mm Rainbow 600W 80 PLUS");
            productDetailsArrayList.add("POWERBOOST VK-P06B 550W USB 3.0 DOUBLE RING MESH 4x120mm");
            productDetailsArrayList.add("POWERBOOST VK-P06B 550W USB 3.0 DOUBLE RING MESH 4x120mm");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX TD500 MESH 4x120mm ARGB ");
            productDetailsArrayList.add("COOLER MASTER MASTEBOX TD500 750W 80PLUS BRONZE");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX K501L V4 600W 80PLUS MESH ");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX MB311L 600W 3x120mm RGB MINI TOWER");
            productDetailsArrayList.add("POWERBOOST VK-M202B USB 3.0 MESH 4x120mm RGB FAN ATX");
            productDetailsArrayList.add("CORSAIR İCUE 4000X RGB 750W 80PLUS BRONZE TEMPERED");
            productDetailsArrayList.add("FRISBY 4x120mm RGB  650W 80 PLUS MESH MidT ATX GAMING ");
            productDetailsArrayList.add("CORSAIR 4000D TEMPERED GLASS 2x120mm MidT ");
            productDetailsArrayList.add("RAMPAGE SAILOR 4x120mm Rainbow 600W 80Plus");
            productDetailsArrayList.add("THERMALTAKE V200TG 600W 80PLUS 3x120mm RGB  MidT");
            productDetailsArrayList.add("POWERBOOST VK-D501M 650W 80PLUS USB 3.0 MESH 4x120mm");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 500 TG  3x120mm ARGB  MidT");
            productDetailsArrayList.add("MSI MAG FORGE 100R TEMPERED GLASS 2x120mm ARGB  MidT");
            productDetailsArrayList.add("RAMPAGE FUSION 4x120mm RAINBOW  USB3.0 MidT ATX");
            productDetailsArrayList.add("XIGMATEK DIAMOND 600W 80 PLUS POWER 4X120mm RAINBOW");
            productDetailsArrayList.add("THERMALTAKE COMMANDER G33 TG 750W 80PLUS 2x200mm ARGB");
            productDetailsArrayList.add("COUGAR PANZER EVO RGB GAMING USB3.0 FullT ATX ");
            productDetailsArrayList.add("COUGAR PURITAS GAMING USB3.0 MidT ATX ");
            productDetailsArrayList.add("THERMALTAKE TOWER 500  3X TEMPERED GLASS");
            productDetailsArrayList.add("MSI MAG FORGE M100R TEMPERED GLASS ARGB FAN USB");
            productDetailsArrayList.add("MSI MAG FORGE M100A ARGB FAN USB 3.2");
            productDetailsArrayList.add("POWERBOOST VK-P3301B 500W USB 3.0 MESH FIXED 4x120mm");
            productDetailsArrayList.add("POWERBOOST VK-C12B USB 3.0 TEMPERED GLASS 4x120mm RGB");
            productDetailsArrayList.add("COOLER MASTER HAF 700 EVO 2x120mm-2x200mm ARGB");
            productDetailsArrayList.add("MAG SHIELD 110R USB3.2 MidT ATX ");
            productDetailsArrayList.add("COOLER MASTER HAF 500 TG MESH ARGB 1x120cm 2x200cm");
            productDetailsArrayList.add("THERMALTAKE AH T200  Tempered Glass ");
            productDetailsArrayList.add("MSI MAG FORGE 100 RB65CAM 650W BRONZ");
            productDetailsArrayList.add("THERMALTAKE Core P6  3xTempered Glass ");
            productDetailsArrayList.add("MSI MPG VELOX 100P AIRFLOW 4x120mm  MidT ATX GAMING");
            productDetailsArrayList.add("THERMALTAKE PCI-e 4.0 X16 300mm 90°  RISER");
            productDetailsArrayList.add("MSI MAG SHIELD M300 USB3.2 MidT ATX");
            productDetailsArrayList.add("MSI MAG FORGE 101M TEMPERED GLASS 4x120mm RGB  MidT");
            productDetailsArrayList.add("COUGAR MX660 MESH RGB 4x120mm RGB MESH GEX");
            productDetailsArrayList.add("MSI MPG GUNGNIR 110R TEMPERED GLASS 4x120mm ARGB");
            productDetailsArrayList.add("COOLER MASTER MASTERCASE H500 MESH 2x200mm +1x120mm");
            productDetailsArrayList.add("CORSAIR 5000D TEMPERED GLASS YAN PANEL 2x120mm ");
            productDetailsArrayList.add("ZALMAN M3 PLUS 4x120mm RGB  MINI TOWER GAMING ");
            productDetailsArrayList.add("ZALMAN S4 PLUS 3x120mm RGB  MEGAMAX 600W 80PLUS MidT ATX GAMING ");
            productDetailsArrayList.add("THERMALTAKE VIEW 51 TEMPERED GLASS 2x200mm ARGB");
            productDetailsArrayList.add("THERMALTAKE VIEW 51 TEMPERED GLASS 2x200mm ARGB");
            productDetailsArrayList.add("RAMPAGE HACKER 4x120mm RGB 600W 80PLUS BRONZE MidT");
            productDetailsArrayList.add("COUGAR DARK BLADER-G RGB  USB3.0 FullT ATX");
            productDetailsArrayList.add("THERMALTAKE VERSA J23 650W 3x120mm RGB ");
            productDetailsArrayList.add("EVEREST X-MESH 3x120mm RAINBOW USB 3.0 Tower");
            productDetailsArrayList.add("CORSAIR CARBIDE  SPEC-DELTA 550W 80PLUS RGB ");
            productDetailsArrayList.add("COUGAR GEMINI S GAMING USB3.0 MidT ");
            productDetailsArrayList.add("COUGAR GEMINI T RGB GAMING USB3.0 MidT ATX ");
            productDetailsArrayList.add("XIGMATEK ASTRO 650W 80PLUS POWER 4x120mm RGB ");
            productDetailsArrayList.add("RAMPAGE THE KING 4x120mm RGB FAN USB 3.0 MidT ATX");
            productDetailsArrayList.add("POWER BOOST VK-G3403S 650W 80 PLUS MESH PANEL USB 3.0");
            productDetailsArrayList.add("POWER BOOST VK-T01B RGB 650W 80+ Mesh Panel USB 3.0 Mid");
            productDetailsArrayList.add("FRISBY FC-9410G WOLF 500W RGB 3X FAN USB 3.0 Mid ATX");
            productDetailsArrayList.add("FRISBY FC-9320G MESH 4XRGB 600W USB 3.0 80 PLUS Mid ATX");
            productDetailsArrayList.add("THERMALTAKE CORE P6 3X TEMPERED GLASS");
            productDetailsArrayList.add("THARMALTAKE AH T200  TEMPERED GLASS ");
            productDetailsArrayList.add("MSI MAG SHIELD M301 MESH mATX ");
            productDetailsArrayList.add("ASUS TUF GAMING GT301 3x120mm ARGB  CAM MidT ATX");
            productDetailsArrayList.add("RAMPAGE REDSKY 4x120mm RGB 700W 80 Plus Bronze");
            productDetailsArrayList.add("XIGMATEK GAMING M 4*X20C RGB  X-Power 500W Mesh Panel Tempered ");
            productDetailsArrayList.add("EVEREST BUMPY 4x120mm ARGB  TEMPERED ");
            productDetailsArrayList.add("EVEREST 2*Sata Mesh 720R Peak-250W");
            productDetailsArrayList.add("MSI MAG FORGE 112R ARGB Tempered ATX Mid Tower");
            productDetailsArrayList.add("POWERBOOST X59RGB 650W 80 PLUS USB 3.0 TEMPERED");
            productDetailsArrayList.add("POWERBOOST VK-G3701B 550W 80 PLUS USB 3.0 MESH 4x120mm");
            productDetailsArrayList.add("MSI MPG GUNGNIR 110R WHITE Tempered Glass RGB USB 3.2 ATX");
            productDetailsArrayList.add("MSI MAG FORGE 110R ARGB USB 3.2  ATX Mid Tower ");
            productDetailsArrayList.add("MSI MAG FORGE 111R USB 3.2 ARGB Tempered Glass  ATX");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX MB520 600W 80 PLUS 3x120mm");
            productDetailsArrayList.add("MSI MAG SHIELD 110A USB3.2 MidT ATX ");
            productDetailsArrayList.add("S-LINK SLX-F201 10cm 4 Pin 2'li PWM ");
            productDetailsArrayList.add("COOLER MASTER TD300 TG MESH 2x120mm ARGB ");
            productDetailsArrayList.add("COOLER MASTER HAF 500 TG MESH ARGB 1x120cm 2x200cm");
            productDetailsArrayList.add("THERMALTAKE AH T200  Tempered Glass  Micro ATX");
            productDetailsArrayList.add("THERMALTAKE Core P6  3xTempered Glass  MidT");
            productDetailsArrayList.add("THERMALTAKE Divider 200 TG Air  1x200mm Mesh MiniT");
            productDetailsArrayList.add("THERMALTAKE Divider 200 TG Air  1x200mm Mesh MiniT");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 500 TG AIR  2x120mm MESH");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 500 TG AIR  2x120mm MESH");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 500 TG  4x120mm ARGB MidT");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 300 TG AIR  2x120mm  MESH");
            productDetailsArrayList.add("THERMALTAKE DIVIDER 300 TG AIR  2x120mm  MESH");
            productDetailsArrayList.add("THERMALTAKE The Tower 100 Racing Green 3xTempered Glass");
            productDetailsArrayList.add("THERMALTAKE THE TOWER 100  3xTEMPERED GLASS");
            productDetailsArrayList.add("THERMALTAKE The Tower 100 3xTempered Glass");
            productDetailsArrayList.add("THERMALTAKE THE TOWER 100 3xTEMPERED GLASS");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 300R PACIFIC BLUE TEMPERED GLASS 1x120mm");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 300R MIDNIGHT GREEN TEMPERED");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 100L TEMPERED GLASS MidT ARGB ATX");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX NR200P TEMPERED GLASS Mini");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX NR200P TEMPERED GLASS Mini-");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX NR200P TEMPERED GLASS Mini-");
            productDetailsArrayList.add("MSI MPG VELOX 100R 4x120mm ARGB  MidT ATX GAMING");
            productDetailsArrayList.add("MSI MPG VELOX 100R 4x120mm ARGB  MidT ATX GAMING");
            productDetailsArrayList.add("MSI MPG QUIETUDE 100S 1x120mm  MidT ATX GAMING");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 300R TEMPERED GLASS 1x120mm ARGB");
            productDetailsArrayList.add("MSI MPG GUNGNIR 111R TEMPERED GLASS 4x120mm ARGB");
            productDetailsArrayList.add("COUGAR GEMINI T PRO ARGB LED MidT ATX GAMING");
            productDetailsArrayList.add("COUGAR GEMINI S SILVER XTC 600W 80 PLUS MidT ATX GAMING");
            productDetailsArrayList.add("COUGAR QBX USB3.0 Mini-ITX");
            productDetailsArrayList.add("COUGAR BLAZER ESSENCE TEMPER  MidT ATX GAMIN");
            productDetailsArrayList.add("MSI MPG SEKIRA 500G TEMPERED GLASS 3x200mm  MidT ATX ");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX TD500 650W 80PLUS BRONZE");
            productDetailsArrayList.add("COUGAR MX440 MESH RGB 4x120mm RGB VTE X2 750W");
            productDetailsArrayList.add("ZALMAN N5 MF (SE) 4x120mm RGB  MegaMax 600W 80PLUS MidT");
            productDetailsArrayList.add("COUGAR PANZER 1x120mm MidT ATX USB3.0 GAMING ");
            productDetailsArrayList.add("COUGAR DARK BLADER X7 1x120mm ARGB  GAMING");
            productDetailsArrayList.add("COUGAR DARK BLADER X5-RGB 3x120mm ARGBGAMING");
            productDetailsArrayList.add("XIGMATEK MASTER X 4x120mm ARGB  X-Power 650W TEMPER MESH PANEL");
            productDetailsArrayList.add("XIGMATEK X7 7x120mm ARGB TEMPERED");
            productDetailsArrayList.add("XIGMATEK GAMING X 4x120mm RGB  X-Power 500W TEMPER");
            productDetailsArrayList.add("MSI MPG GUNGNIR 100P TEMPERED GLASS 1x120mm ");
            productDetailsArrayList.add("CORSAIR 470T RGB  CAM YAN PANEL MidT ATX ");
            productDetailsArrayList.add("RAMPAGE DEEPFORCE 4x120mm RGB  600W 80PLUS BRONZE");
            productDetailsArrayList.add("MSI MPG SEKIRA 500X TEMPERED GLASS 3x200mm ARGB  MidT");
            productDetailsArrayList.add("THERMALTAKE H330 650W 2x120mm  TEMPERED");
            productDetailsArrayList.add("THERMALTAKE VERSA T25 550W 80+ TEMPERED GLASS RGB");
            productDetailsArrayList.add("ASUS TUF GAMING GT501 WHITE EDITION 3x120mm RGB");
            productDetailsArrayList.add("FRISBY 4x120mm RAINBOW 600W 80PLUS BRONZE USB3.0");
            productDetailsArrayList.add("EVEREST SPECTRUM 4x120mm  500W MidT ATX GAMING");
            productDetailsArrayList.add("RAMPAGE PHANTOM X2 600W 80PLUS BRONZE 1x120mm RGB");
            productDetailsArrayList.add("RAMPAGE PHANTOM X1 600W 80PLUS BRONZE 1x120mm RGB");
            productDetailsArrayList.add("CORSAIR CARBIDE SPEC-05 650W 80PLUS BRONZE MidT");
            productDetailsArrayList.add("CORSAIR CARBIDE SPEC-05 550W 80PLUS BRONZE MidT");
            productDetailsArrayList.add("COUGAR MX410 MESH-G RGB 4x120mm ARGB  650W 80 PLUS USB3.0 GAMING ");
            productDetailsArrayList.add("ASUS ROG Z11 3x120mm USB3.2 MINI TOWER ");
            productDetailsArrayList.add("MSI MPG SEKIRA 100R TEMPERED GLASS 4x120mm ARGB  MidT");
            productDetailsArrayList.add("MSI MPG GUNGNIR 110M TEMPERED GLASS 3x120mm RGB");
            productDetailsArrayList.add("MSI MPG GUNGNIR 100D TEMPERED GLASS 2x120mm ");
            productDetailsArrayList.add("MSI MPG GUNGNIR 100 TEMPERED GLASS 1x120mm ARGB");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 100R TEMPERED GALSS 1x120mm ARGB");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 011C TEMPERED GLASS 1x120mm ARGB");
            productDetailsArrayList.add("MSI MAG VAMPIRIC 010X TEMPERED GLASS 1x120mm RGB");
            productDetailsArrayList.add("CORSAIR 5000D TEMPERED GLASS YAN PANEL 2x120mm  MidT ");
            productDetailsArrayList.add("FRISBY 4x120mm RGB 650W 80 PLUS USB3.0 MidT ATX GAMING");
            productDetailsArrayList.add("FRISBY 4x120mm RGB 600W 80 PLUS USB3.0 MidT ATX GAMING");
            productDetailsArrayList.add("FRISBY 4x120mm RGB 650W 80 PLUS USB3.0 MidT ATX GAMING");
            productDetailsArrayList.add("XIGMATEK CYCLOPS M 4x120mm RGB  X-POWER 650W");
            productDetailsArrayList.add("XIGMATEK TRIDENT PLUS 4x120mm RGB  X-POWER");
            productDetailsArrayList.add("COOLER MASTER MASTERCASE H500 750W 80PLUS BRONZE ARGB");
            productDetailsArrayList.add("XIGMATEK VERA M 2x200mm ARGB  MESH PANEL SUPER");
            productDetailsArrayList.add("XIGMATEK VERA 2x200mm ARGB  CAM PANEL SUPER TOWER");
            productDetailsArrayList.add("RAMPAGE HECTORA GLASS 4x140mm ARGB  700W");
            productDetailsArrayList.add("RAMPAGE AMAZE 4x120mm RGB 700W 80PLUS BRONZE MidT");
            productDetailsArrayList.add("RAMPAGE HECTORA XL 4x140mm ARGB 700W 80PLUS");
            productDetailsArrayList.add("THERMALTAKE VERSA T25 650W 80PLUS ARGB 3x120mm  MidT");
            productDetailsArrayList.add("THERMALTAKE H330 650W ARGB 3x120mm TEMPERED GLASS");
            productDetailsArrayList.add("RAMPAGE PLATINO 4x120mm RAINBOW 600W 80PLUS");
            productDetailsArrayList.add("RAMPAGE ESPECTRO 4x120mm RAINBOW  600W 80PLUS");
            productDetailsArrayList.add("RAMPAGE GALAXY 4x120mm RGB  600W 80PLUS BRONZE MidT");
            productDetailsArrayList.add("RAMPAGE X-HORSE 650W 80 PLUS BRONZE 4x120mm RAINBOW FAN");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX MB520  4x120mm");
            productDetailsArrayList.add("CORSAIR İCUE 4000X RGB TEMPERED GLASS 3x120mm ");
            productDetailsArrayList.add("CORSAIR 4000D TEMPERED GLASS 2x120mm MidT");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX MB511 650W 80PLUS BRONZE");
            productDetailsArrayList.add("XIGMATEK ZEUS SPECTRUM EDITION 5x120mm RGB ");
            productDetailsArrayList.add("XIGMATEK PERSEUS 5x120mm RGB  USB3.0 TEMPER ");
            productDetailsArrayList.add("RAMPAGE  MESH 4x120mm RAINBOW  650W");
            productDetailsArrayList.add("RAMPAGE VICTORY 1x12mm  600W 80 PLUS BRONZE MidT ATX");
            productDetailsArrayList.add("XIGMATEK GRIP 4x120mm RAINBOW  X-POWER 600W");
            productDetailsArrayList.add("THERMALTAKE AH T600 2 x Tempered Glass ARGB");
            productDetailsArrayList.add("ASUS TUF GAMING GT301 3x120mm ARGB");
            productDetailsArrayList.add("RAMPAGE SHAKE 4x120mm RGB 600W 80Plus BRONZE MidT");
            productDetailsArrayList.add("RAMPAGE TRAPPER 3x140mm ARGB  650W 80PLUS");
            productDetailsArrayList.add("FRISBY FC-9325G INFINITY 4x120mm RGB 650W 80PLUS");
            productDetailsArrayList.add("XIGMATEK SIROCON III X-POWER 600W 80 PLUS RAINBOW FAN");
            productDetailsArrayList.add("RAMPAGE IMPOSING PRO 2x200mm ARGB  700W");
            productDetailsArrayList.add("FRISBY 4x120mm DUAL RING RGB  600W 80 PLUS BRONZE MidT");
            productDetailsArrayList.add("FRISBY 4x120mm DUAL RING 600W 80 PLUS BRONZE MidT");
            productDetailsArrayList.add("CORSAIR iCUE 220T 3x120mm RGB TEMPER MidT ATX");
            productDetailsArrayList.add("CORSAIR iCUE 220T 3x120mm RGB  TEMPER MidT ATX");
            productDetailsArrayList.add("COUGAR BLAZER TEMPER USB3.0 MidT ATX GAMING");
            productDetailsArrayList.add("COUGAR GEMINI S IRON GRAY XTC 600W 80 PLUS USB3.0 MidT");
            productDetailsArrayList.add("COUGAR DARK BLADER-S RGB  PANEL USB3.0 FullT ATX GAMING ");
            productDetailsArrayList.add("ASUS ROG STRIX GX601 HELIOS 4x140mm  USB3.1 MidT ATX");
            productDetailsArrayList.add("ASUS TUF GAMING GT501 3x120mm RGB ");
            productDetailsArrayList.add("XIGMATEK LAMIYA 4x120mm  X-POWER 650W 80PLUS MidT ATX");
            productDetailsArrayList.add("XIGMATEK COCKPIT MESH PANEL 4x120mm  X-POWER 700W");
            productDetailsArrayList.add("XIGMATEK CYCLOPS 4x120mm FAN X-POWER 650W 80PLUS MidT");
            productDetailsArrayList.add("XIGMATEK CYCLOPS BLACK 4x120mm FAN X-POWER 650W");
            productDetailsArrayList.add("COUGAR DARK BLADER-S RGB USB3.0 FullT ATX");
            productDetailsArrayList.add("ASUS ROG STRIX GX601 HELIOS 4x140mm  USB3.1 MidT ATX");
            productDetailsArrayList.add("ASUS TUF GAMING GT501 3x120mm RGB ");
            productDetailsArrayList.add("XIGMATEK LAMIYA 4x120mm  X-POWER 650W 80PLUS MidT ATX");
            productDetailsArrayList.add("XIGMATEK COCKPIT MESH PANEL 4x120mm  X-POWER 700W 80PLUS MidT ATX");
            productDetailsArrayList.add("XIGMATEK CYCLOPS 4x120mm FAN X-POWER 650W 80PLUS MidT ATX ");
            productDetailsArrayList.add("XIGMATEK CYCLOPS BLACK 4x120mm FAN X-POWER 650W");
            productDetailsArrayList.add("XIGMATEK HELIOS RAINBOW USB 3.0 MidT ATX GAMING");
            productDetailsArrayList.add("FRISBY FC-8940G 650W 80 PLUS 2x200mm+1x120mm RGB");
            productDetailsArrayList.add("FRISBY FC-8935G 650W 80 PLUS 4x120mm RAINBOW  MidT");
            productDetailsArrayList.add("FRISBY FC-8930G 650W 80 PLUS 3x120mm RAINBOW  MESH");
            productDetailsArrayList.add("XIGMATEK AQUARIUS PLUS 7x120mm  USB3.0 TEMPER");
            productDetailsArrayList.add("COUGAR PURITAS RGB 3x120mm RGB  USB3.0 MidT ATX");
            productDetailsArrayList.add("CORSAIR CARBIDE  SPEC-05 550W 80PLUS MidT ATX");
            productDetailsArrayList.add("THERMALTAKE VERSA J25 650W 80PLUS RGB 3x120mm MidT");
            productDetailsArrayList.add("THERMALTAKE VERSA J24 650W 80PLUS RGB 3x120mm MidT");
            productDetailsArrayList.add("XIGMATEK BEAST 4 x RGB  X-POWER 650W 80PLUS USB 3.0");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX MB520 650W 80PLUS 3x120mm");
            productDetailsArrayList.add("COUGAR CGR-5NM1B-C MX350 700W 80 PLUS MidT ATX GAMING");
            productDetailsArrayList.add("THERMALTAKE PCI-e X16 Riser Cable");
            productDetailsArrayList.add("THERMALTAKE VIEW 27 600W 80PLUS ARGB 3x120mm  MidT");
            productDetailsArrayList.add("NZXT S340MB-GB Mid TOWER ATX");
            productDetailsArrayList.add("NZXT S340MB-GR Mid TOWER ATX");
            productDetailsArrayList.add("NZXT S340W-TH ELITE SPECIAL EDITION Mid TOWER ATX ");
            productDetailsArrayList.add("NZXT S340W-B5 ELITE Mid TOWER ATX ");
            productDetailsArrayList.add("NZXT S340W-B3 ELITE Mid TOWER ATX ");
            productDetailsArrayList.add("COUGAR PANZER-G GAMING USB3.0 MidT ATX ");
            productDetailsArrayList.add("COUGAR PANZER MAX GAMING USB3.0 FullT ATX");
            productDetailsArrayList.add("COUGAR CONQUER GAMING USB3.0 MidT ATX ");
            productDetailsArrayList.add("RAMPAGE TEMPER PRO V5 4x120mm RGB FAN 6xRGB ");
            productDetailsArrayList.add("XIGMATEK ZEST RAINBOW LED BAR USB3.0 TEMPER ");
            productDetailsArrayList.add("COUGAR TURRET V2 700W 80 PLUS GAMING USB3.0 MidT ATX");
            productDetailsArrayList.add("THERMALTAKE LEVEL 20 MT ARGB 3x120mm FANLI MidT ");
            productDetailsArrayList.add("THERMALTAKE V200TG 3x120MM RGB FANLI MidT ATX GAMING");
            productDetailsArrayList.add("FRISBY GC-9250G GAMEMAX RAPTOR 650W USB 3.0MidT ATX");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX K500L 600W 80PLUS MidT ATX");
            productDetailsArrayList.add("FRISBY FC-8860G-650 650W USB 3.0 MidT GAMING ");
            productDetailsArrayList.add("FRISBY FC-8865G-650 650W MidT ATX");
            productDetailsArrayList.add("RAMPAGE CARBON 4x120mm RGB FAN USB 3.0 MidT ATX");
            productDetailsArrayList.add("COOLER MASTER MASTERBOX LITE 5 600W 80PLUS MidT ATX");
            productDetailsArrayList.add("FRISBY FC-8870G 400W USB 3.0 MidT GAMING ");
            productDetailsArrayList.add("RAMPAGE RGB 600W 80 PLUS BRONZE 120mm RGB ");
            productDetailsArrayList.add("POWERBOOST FURY 550W 80 PLUS 120mm");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS BRONZE 750W 2xEPS");
            productDetailsArrayList.add("EVGA SUPERNOVA 850 GA 850W 80 PLUS GOLD FULL MODULER");
            productDetailsArrayList.add("COOLER MASTER MWE V2 850W 80PLUS GOLD 2xEPS 120MM ");
            productDetailsArrayList.add("MSI MPG A850GF 850W 80 PLUS GOLD ");
            productDetailsArrayList.add("POWERBOOST FURY 650W 80 PLUS 120mm ");
            productDetailsArrayList.add("COOLER MASTER ELITE V4 80PLUS 500W PFC 120mm");
            productDetailsArrayList.add("POWERBOOST FURY 750W 80 PLUS 120mm ");
            productDetailsArrayList.add("CORSAIR CX CX750F RGB 750W 80PLUS BRONZE ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF1 80+ GOLD 750W 140mm ");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS BRONZE 600W 2xEPS, PFC");
            productDetailsArrayList.add("THERMALTAKE LİTEPOWER RGB 650W APFC 12cm  PSU");
            productDetailsArrayList.add("COOLER MASTER M2000 2000W 80PLUS PLATINUM 135MM ");
            productDetailsArrayList.add("MSI MPG A1000G 1000W 80+ GOLD PSU Full Modüler");
            productDetailsArrayList.add("THERMALTAKE SMART BX1 80PLUS BRONZE 750W ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF 850W 80PLUS GOLD");
            productDetailsArrayList.add("CORSAIR CX  CX750F RGB 750W 80PLUS BRONZE ");
            productDetailsArrayList.add("MSI MPG A650GF 650W 80 PLUS GOLD");
            productDetailsArrayList.add("XIGMATEK X-POWER 350W ");
            productDetailsArrayList.add("ZALMAN ZM600-TXII 600W 80 PLUS ");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS BRONZE 700W 2xEPS");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS BRONZE 650W 2xEPS");
            productDetailsArrayList.add("ASUS ROG STRIX 850W 80 PLUS GOLD ");
            productDetailsArrayList.add("THERMALTAKE SMART BX1 80PLUS BRONZE 650W GÜÇ");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS 650W 2xEPS");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GRAND 80+ PLATINUM 1200W FUL");
            productDetailsArrayList.add("RAMPAGE RMP-500-80P 500W 80PLUS 120MM ");
            productDetailsArrayList.add("THERMALTAKE LITEPOWER 650W ");
            productDetailsArrayList.add("ASUS ROG-THOR GAMING 1200W 80PLUS PLATINIUM AURO SYNC &");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF1 1000W 80PLUS GOLD FULL");
            productDetailsArrayList.add("POWERBOOST 2000W 140mm");
            productDetailsArrayList.add("POWERBOOST WARRIOR 850W 80 PLUS GOLD FULL MODÜLER ");
            productDetailsArrayList.add("CORSAIR CP-9020234-EU RM750 750W TAM MODULER 80PLUS ");
            productDetailsArrayList.add("CORSAIR RM850 850W TAM MODULER 80PLUS GOLD ");
            productDetailsArrayList.add("CORSAIR RM750 750W TAM MODULER 80PLUS GOLD ");
            productDetailsArrayList.add("COOLER MASTER XG PLUS 850W 80+ PLATINYUM RGB FULL");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF2 80 PLUS 750W GOLD ARGB");
            productDetailsArrayList.add("EVGA 500 W2 500W 80 PLUSI");
            productDetailsArrayList.add("EVGA SUPERNOVA 750 GA 750W 80 PLUS GOLD FULL MODULER");
            productDetailsArrayList.add("EVGA 1000 GQ 1000W 80 PLUS GOLD SEMI MODULER");
            productDetailsArrayList.add("EVGA 750 GQ 750W 80 PLUS GOLD SEMI MODULER");
            productDetailsArrayList.add("MSI MAG A550BN 550W 80 PLUS BRONZE ");
            productDetailsArrayList.add("COOLER MASTER V750W SFX 80PLUS GOLD 750W");
            productDetailsArrayList.add("COOLER MASTER MWE V2 80PLUS GOLD 1250W FULL MODÜLER 140MM");
            productDetailsArrayList.add("COOLER MASTER MWE V2 80PLUS GOLD 1050W FULL MODÜLER");
            productDetailsArrayList.add("ZALMAN ZM1200-EBTII WATTTERA 80PLUS GOLD 1200W ");
            productDetailsArrayList.add("RAMPAGE BTC-1650 1650W 140MM FANLI ");
            productDetailsArrayList.add("COUGAR GEX1050 1050W 80PLUS GOLD");
            productDetailsArrayList.add("COUGAR GEX850 850W 80PLUS GOLD ");
            productDetailsArrayList.add("COUGAR VTE X2 750 750W 80PLUS BRONZE ");
            productDetailsArrayList.add("COUGAR GX-S450 450W 80PLUS GOLD ");
            productDetailsArrayList.add("COOLER MASTER MWE V2 750W 80PLUS GOLD 2xEPS 120MM ");
            productDetailsArrayList.add("RAMPAGE RGB 500W 80 PLUS BRONZE 120mm RGB ");
            productDetailsArrayList.add("CORSAIR RMx  RM750X 750W 80PLUS GOLD TAM");
            productDetailsArrayList.add("XIGMATEK MINOTOUR 750W 80PLUS GOLD FULL");
            productDetailsArrayList.add("XIGMATEK MINOTOUR 850W 80PLUS GOLD FULL");
            productDetailsArrayList.add("FRISBY FR-PS50F12B 500W 120mm ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF 750W 80PLUS GOLD FULL");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GRAND 80PLUS PLATINUM 1050W");
            productDetailsArrayList.add("ASUS ROG THOR 850W 80 PLUS PLATINUM AURO SYNC & OLED");
            productDetailsArrayList.add("CORSAIR RMx  RM750X V2 750W 80PLUS GOLD TAM");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF2 80 PLUS 850W GOLD ARGB");
            productDetailsArrayList.add("CORSAIR CX  CX650F RGB 650W 80PLUS BRONZE TAM");
            productDetailsArrayList.add("ASUS TUF GAMING 450W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS TUF GAMING 750W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS ROG STRIX 1000W 80 PLUS GOLD");
            productDetailsArrayList.add("MSI MPG A750GF 750W 80 PLUS GOLD");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER PF1 850W 80+ PLATINUM FULL");
            productDetailsArrayList.add("ASUS TUF GAMING 650W 80PLUS BRONZE ");
            productDetailsArrayList.add("XIGMATEK HYDRA M 550W 80PLUS BRONZE FULL MODULER");
            productDetailsArrayList.add("XIGMATEK HYDRA M 650W 80PLUS BRONZE FULL MODULER");
            productDetailsArrayList.add("ZALMAN ZM700-TXII 700W 80 PLUS ");
            productDetailsArrayList.add("ZALMAN ZM1200-EBTII WATTTERA 80PLUS GOLD 1200W ");
            productDetailsArrayList.add("RAMPAGE BTC-1650 1650W 140MM ");
            productDetailsArrayList.add("COUGAR GEX1050 1050W 80PLUS GOLD ");
            productDetailsArrayList.add("COUGAR GEX850 850W 80PLUS GOLD ");
            productDetailsArrayList.add("COUGAR VTE X2 750 750W 80PLUS BRONZE");
            productDetailsArrayList.add("COUGAR GX-S450 450W 80PLUS GOLD ");
            productDetailsArrayList.add("COOLER MASTER MWE V2 750W 80PLUS GOLD 2xEPS 120MM");
            productDetailsArrayList.add("RAMPAGE RGB 500W 80 PLUS BRONZE 120mm RGB");
            productDetailsArrayList.add("CORSAIR RMx  RM750X 750W 80PLUS GOLD ");
            productDetailsArrayList.add("XIGMATEK MINOTOUR  750W 80PLUS GOLD");
            productDetailsArrayList.add("XIGMATEK MINOTOUR  850W 80PLUS GOLD");
            productDetailsArrayList.add("FRISBY FR-PS50F12B 500W 120mm ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF 750W 80PLUS GOLD ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GRAND 80PLUS PLATINUM 1050W");
            productDetailsArrayList.add("ASUS ROG THOR 850W 80 PLUS PLATINUM AURO SYNC & OLED");
            productDetailsArrayList.add("CORSAIR RMx RM750X V2 750W 80PLUS GOLD TAM");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF2 80 PLUS 850W GOLD ARGB");
            productDetailsArrayList.add("CORSAIR CX  CX650F RGB 650W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS TUF GAMING 450W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS TUF GAMING 750W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS ROG STRIX 1000W 80 PLUS GOLD ");
            productDetailsArrayList.add("MSI MPG A750GF 750W 80 PLUS GOLD");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER PF1 850W 80+ PLATINUM L");
            productDetailsArrayList.add("ASUS TUF GAMING 650W 80PLUS BRONZE ");
            productDetailsArrayList.add("ASUS TUF GAMING 650W 80PLUS BRONZE ");
            productDetailsArrayList.add("XIGMATEK HYDRA M 550W 80PLUS BRONZE FULL MODULER ");
            productDetailsArrayList.add("XIGMATEK HYDRA M 650W 80PLUS BRONZE FULL MODULER ");
            productDetailsArrayList.add("ZALMAN ZM700-TXII 700W 80 PLUS ");
            productDetailsArrayList.add("ZALMAN ZM500-TXII 500W 80 PLUS ");
            productDetailsArrayList.add("ASUS ROG STRIX WHITE 850W 80 PLUS GOLD ");
            productDetailsArrayList.add("CORSAIR CV  CV650 650W 80PLUS BRONZE ");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS BRONZE 750W 2xEPS");
            productDetailsArrayList.add("CORSAIR CV  CV550 550W 80PLUS BRONZE");
            productDetailsArrayList.add("CORSAIR RM  650W 80PLUS GOLD MODÜLER");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER PF1 ARGB 80PLUS PLATINUM 1200W");
            productDetailsArrayList.add("COUGAR BXM-850 850W 80 PLUS BRONZE ");
            productDetailsArrayList.add("COUGAR BXM-700 700W 80 PLUS BRONZE ");
            productDetailsArrayList.add("COUGAR XTC 500 500W 80 PLUS ");
            productDetailsArrayList.add("FRISBY FR-PS6580P-RGB 80 PLUS 650W");
            productDetailsArrayList.add("FRISBY FR-PS6080P 80 PLUS BRONZE 600W ");
            productDetailsArrayList.add("ASUS ROG STRIX 750W 80 PLUS GOLD");
            productDetailsArrayList.add("ASUS ROG STRIX 650W 80 PLUS GOLD ");
            productDetailsArrayList.add("ASUS ROG STRIX 550W 80 PLUS GOLD ");
            productDetailsArrayList.add("THERMALTAKE TR2 S 750W 80 PLUS 120mm ");
            productDetailsArrayList.add("THERMALTAKE TR2 S 650W 80 PLUS 120mm ");
            productDetailsArrayList.add("THERMALTAKE TR2 S 550W 80 PLUS 120mm ");
            productDetailsArrayList.add("FRISBY FR-PS6580P 650W 80 PLUS 120mm ");
            productDetailsArrayList.add("COUGAR CGR-GS-750 GX-S 750W 80PLUS GOLD");
            productDetailsArrayList.add("COUGAR CGR-BX-700 CMX 700W 80PLUS BRONZE ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER PF1 ARGB 80PLUS PLATINUM 1050W");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF1 80 PLUS 850W GOLD ");
            productDetailsArrayList.add("THERMALTAKE SMART RGB BX1 80PLUS BRONZE 750W ");
            productDetailsArrayList.add("COOLER MASTER MWE 80PLUS 600W 2xEPS");
            productDetailsArrayList.add("XIGMATEK MINOTAUR FULL RANGE 80PLUS GOLD 650W");
            productDetailsArrayList.add("COUGAR CGR-STX-700 700W 80 PLUS ");
            productDetailsArrayList.add("XIGMATEK X-POWER 650W 80PLUS");
            productDetailsArrayList.add("THERMALTAKE LITEPOWER RGB 550W APFC 12cm PSU");
            productDetailsArrayList.add("XIGMATEK X-POWER 600W 80PLUS ");
            productDetailsArrayList.add("XIGMATEK X-POWER 500W 80PLUS ");
            productDetailsArrayList.add("THERMALTAKE SMART RGB 80 PLUS 700W ");
            productDetailsArrayList.add("THERMALTAKE SMART RGB 80PLUS 600W ");
            productDetailsArrayList.add("THERMALTAKE SMART RGB 80PLUS 500W ");
            productDetailsArrayList.add("RAMPAGE RMP-750-80PB 750W 80 PLUS BRONZE 140MM ");
            productDetailsArrayList.add("RAMPAGE RMP-700-80P 700W 80PLUS 120MM ");
            productDetailsArrayList.add("RAMPAGE RMP-650-80PB 650W 80PLUS BRONZE 140MM ");
            productDetailsArrayList.add("THERMALTAKE TOUGHPOWER GF1 80 PLUS 850W GOLD FULL");
            productDetailsArrayList.add("RAMPAGE RMP-750-80PB 750W 80 PLUS BRONZE 140MM");
            productDetailsArrayList.add("RAMPAGE RMP-700-80P 700W 80PLUS 120MM ");
            productDetailsArrayList.add("RAMPAGE RMP-650-80PB 650W 80PLUS BRONZE 140MM ");
            productDetailsArrayList.add("RAMPAGE RMP-600-80P 600W 80PLUS 120MM ");
            productDetailsArrayList.add("XIGMATEK X MINER 1800W 80 PLUS GOLD ");
            productDetailsArrayList.add("RAMPAGE RGB-550 80PLUS GOLD 550W FULL MODÜLER RGB ");
            productDetailsArrayList.add("COOLER MASTER WATT 80PLUS 600W  PFC 120MM ");
            productDetailsArrayList.add("THERMALTAKE LITEPOWER 550W");
            productDetailsArrayList.add("EVEREST BTX-600 600W 80PLUS BRONZE");
            productDetailsArrayList.add("EVEREST ETX-750 750W 80PLUS GOLD ");
            productDetailsArrayList.add("EVEREST EPS-4900B 350W ");
            productDetailsArrayList.add("GSKILL 8GB (1x8GB) RipjawsV DDR4 3200Mhz CL16 1.35V ");
            productDetailsArrayList.add("GSKILL 8GB (1x8GB) RipjawsV  DDR4 3000MHz CL16 1.35V");
            productDetailsArrayList.add("GSKILL 16GB (1x16) RipjawsV  DDR4 3200Mhz CL16 1.35V ");
            productDetailsArrayList.add("GSKILL 8GB (1x8GB) RipjawsV  DDR4 3600MHz CL18 1.35V");
            productDetailsArrayList.add("GSKILL 16GB (1x16GB) RipjawsV Red DDR4 3000MHz CL16 1.35V");
            productDetailsArrayList.add("GSKILL 8GB Value DDR3 1600MHz CL11");
            productDetailsArrayList.add("Crucial 8GB (1x8GB) Ballistix  DDR4 3000MHz CL15 1.35V RGB PC ");
            productDetailsArrayList.add("Kingston 8GB (1x8GB) Fury Renegade  DDR4 3600MHz CL16");
            productDetailsArrayList.add("GSKILL 4GB Value DDR3 1333MHz CL9 PC3 ");
            productDetailsArrayList.add("Crucial 16GB (1x16GB) Ballistix  DDR4 2666MHz CL16 1.2V");
            productDetailsArrayList.add("Kingston 8GB (1x8GB) DDR4 3200MHz CL22");
            productDetailsArrayList.add("Crucial 8GB (1x8GB) Ballistix DDR4 3200MHz CL16 1.35V RGB");
            productDetailsArrayList.add("Crucial 8GB (1x8GB) DDR5 4800MHz CL40 1.1V");
            productDetailsArrayList.add("CORSAIR 8GB Vengeance RGB RS 3200mhz CL16 DDR4");
            productDetailsArrayList.add("CORSAIR 16GB Vengeance RGB RS 3200mhz CL16 DDR4");
            productDetailsArrayList.add("Kingston 16GB (1x16GB) Fury Beast RGB DDR4 3200MHz CL16");
            productDetailsArrayList.add("Kingston 16GB (1x16GB) Fury Beast  DDR4 3200MHz CL16 PC");
            productDetailsArrayList.add("Kıngston 8GB (1x8GB) Fury Beast RGB DDR4 3600MHz CL17");
            productDetailsArrayList.add("Kıngston 8GB (1x8GB) Fury Beast RGB DDR4 3200MHz CL16 PC");
            productDetailsArrayList.add("Kingston 32GB (1x32GB) DDR4 3200MHz CL22");
            productDetailsArrayList.add("Kingston 32GB (1x32GB) DDR4 2666MHz CL19");
            productDetailsArrayList.add("Kingston 16GB (1x16GB) DDR4 3200MHz CL22");
            productDetailsArrayList.add("Kingston 16GB (1x16GB) DDR4 2666MHz CL19");
            productDetailsArrayList.add("Crucial 16GB (1x16GB) DDR5 4800MHz CL40 1.1V");
            productDetailsArrayList.add("Crucial 8GB (1x8GB) Ballistix DDR4 3000MHz CL15 1.35V RGB");
            productDetailsArrayList.add("Kingston 32GB (1x32GB) Fury Beast DDR4 3600MHz CL17 P");
            productDetailsArrayList.add("GSKILL 16GB (1x16GB) Aegis DDR4 2666Mhz CL19 1.2V");
            productDetailsArrayList.add("Crucial 16GB (1x16GB) DDR4 3200MHz CL22 1.2V");
            productDetailsArrayList.add("Crucial 8GB (1x8GB) Ballistix  DDR4 3200MHz CL16 RGB");
            productDetailsArrayList.add("Kingston 8GB (1x8GB) Fruy Beast DDR4 3200MHz CL16 PC");
            productDetailsArrayList.add("Kingston 8GB (1x8GB) Fury Beast DDR4 3600MHz CL17");
            productDetailsArrayList.add("Kingston 8GB (1x8GB) DDR4 2666MHz CL19");
            productDetailsArrayList.add("CORSAIR 8GB (1x8GB) Vengeance LPX  DDR4 3200MHz CL16");
            productDetailsArrayList.add("CORSAIR 16GB (1x16GB) Vengeance LPX  DDR4");
            productDetailsArrayList.add("CORSAIR 8GB (1x8GB) Vengeance RGB PRO  DDR4 3600MHz CL18 Single ");
            productDetailsArrayList.add("CORSAIR 8GB (1x8GB) Vengeance RGB PRO  DDR4 3200MHz");
            productDetailsArrayList.add("CORSAIR 8GB (1x8GB) Vengeance LPX  DDR4 3600MHz CL18");
            productDetailsArrayList.add("CORSAIR 32GB (1x32GB) Vengeance LPX DDR4");
            productDetailsArrayList.add("CORSAIR 16GB (1x16) Vengeance  DDR4 3200Mhz CL16");
            productDetailsArrayList.add("CORSAIR 8GB Vengeance  DDR4 3200Mhz CL16 Single ");
            productDetailsArrayList.add("CORSAIR 8GB (1x8GB) Vengeance  DDR4 3000MHz CL16");
            productDetailsArrayList.add("CORSAIR 8GB Vengeance DDR4 2666MHz CL16");
            productDetailsArrayList.add("GSKILL 4GB Value DDR3 1600MHz CL11");
            productDetailsArrayList.add("GSKILL 4GB Value DDR3 1600MHz CL11");

            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                    if (dil.equals("türkce")){
                        productDetailsArrayList.add("Diğer");
                    }else{
                        productDetailsArrayList.add("Other");
                    }
                }
            });





        }

        else if(parcaAdi.equals("Cep Telefonu") || parcaAdi.equals("Mobile Phone")){



            productDetailsArrayList.add("Acer S100 Liquid");
            productDetailsArrayList.add("Alcatel 1");
            productDetailsArrayList.add("Alcatel 1)");
            productDetailsArrayList.add("Alcatel 3 (5052Y)");
            productDetailsArrayList.add("Alcatel 3v");
            productDetailsArrayList.add("Alcatel 3X (2019)");
            productDetailsArrayList.add("Alcatel 3X (2020)");
            productDetailsArrayList.add("Alcatel 5");
            productDetailsArrayList.add("Alcatel 1066D");
            productDetailsArrayList.add("Alcatel 2003D");
            productDetailsArrayList.add("Alcatel 2020 X");
            productDetailsArrayList.add("Alcatel 3088 ");
            productDetailsArrayList.add("Alcatel A3 )");
            productDetailsArrayList.add("Alcatel Idol 4");
            productDetailsArrayList.add("Alcatel Idol 4s ");
            productDetailsArrayList.add("Alcatel One Touch 2007 ");
            productDetailsArrayList.add("Alcatel One Touch 2012 ");
            productDetailsArrayList.add("Alcatel One Touch 535 ");
            productDetailsArrayList.add("Alcatel One Touch Idol 5 ");
            productDetailsArrayList.add("Alcatel One Touch M'Pop ");
            productDetailsArrayList.add("Alcatel One Touch Pocket ");
            productDetailsArrayList.add("Alcatel One Touch S'Pop ");
            productDetailsArrayList.add("Alcatel Pop 4S");
            productDetailsArrayList.add("Alcatel Shine Lite ");
            productDetailsArrayList.add("Apple iPhone 2G ");
            productDetailsArrayList.add("Apple iPhone 3G )");
            productDetailsArrayList.add("Apple iPhone 3GS ");
            productDetailsArrayList.add("Apple iPhone 4 ");
            productDetailsArrayList.add("Apple iPhone 4S");
            productDetailsArrayList.add("Apple iPhone 5 ");
            productDetailsArrayList.add("Apple iPhone 5S ");
            productDetailsArrayList.add("Apple iPhone 5C ");
            productDetailsArrayList.add("Apple iPhone 6 ");
            productDetailsArrayList.add("Apple iPhone 6 Plus ");
            productDetailsArrayList.add("Apple iPhone 6S");
            productDetailsArrayList.add("Apple iPhone 6S Plus");
            productDetailsArrayList.add("Apple iPhone SE 1.");
            productDetailsArrayList.add("Apple iPhone SE 2.");
            productDetailsArrayList.add("Apple iPhone SE 3.");
            productDetailsArrayList.add("Apple iPhone 7 ");
            productDetailsArrayList.add("Apple iPhone 7 Plus");
            productDetailsArrayList.add("Apple iPhone 8 )");
            productDetailsArrayList.add("Apple iPhone 8 Plus ");
            productDetailsArrayList.add("Apple iPhone X ");
            productDetailsArrayList.add("Apple iPhone XS");
            productDetailsArrayList.add("Apple iPhone XS Max ");
            productDetailsArrayList.add("Apple iPhone XR ");
            productDetailsArrayList.add("Apple iPhone 11 ");
            productDetailsArrayList.add("Apple iPhone 11 Pro");
            productDetailsArrayList.add("Apple iPhone 11 Pro Max ");
            productDetailsArrayList.add("Apple iPhone 12");
            productDetailsArrayList.add("Apple iPhone 12 Mini");
            productDetailsArrayList.add("Apple iPhone 12 Pro ");
            productDetailsArrayList.add("Apple iPhone 12 Pro Max ");
            productDetailsArrayList.add("Apple iPhone 13 ");
            productDetailsArrayList.add("Apple iPhone 13 Mini ");
            productDetailsArrayList.add("Apple iPhone 13 Pro ");
            productDetailsArrayList.add("Apple iPhone 13 Pro Max");
            productDetailsArrayList.add("Apple iPhone 14");
            productDetailsArrayList.add("Apple iPhone 14 Plus ");
            productDetailsArrayList.add("Apple iPhone 14 Pro ");
            productDetailsArrayList.add("Apple iPhone 14 Pro Max");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus ");
            productDetailsArrayList.add("Asus Fonepad Note FHD6 ME560CG ");
            productDetailsArrayList.add("Asus Garmin-Asus nuvifone M20 ");
            productDetailsArrayList.add("Asus J202 ");
            productDetailsArrayList.add("Asus P320 ");
            productDetailsArrayList.add("Asus P526 ");
            productDetailsArrayList.add("Asus P535 ");
            productDetailsArrayList.add("Asus PadFone ");
            productDetailsArrayList.add("Asus ROG Phone ZS600KL ");
            productDetailsArrayList.add("Asus ROG Phone II ZS660KL ");
            productDetailsArrayList.add("Asus ROG Phone 3 Strix ");
            productDetailsArrayList.add("Asus ROG Phone 3 ZS661KS ");
            productDetailsArrayList.add("Asus ROG Phone 5 ");
            productDetailsArrayList.add("Asus ROG Phone 5s");
            productDetailsArrayList.add("Asus ROG Phone 6 ");
            productDetailsArrayList.add("Asus ROG Phone 6D");
            productDetailsArrayList.add("Asus ROG Phone 6D Ultimate");
            productDetailsArrayList.add("Asus ROG Phone 6 Pro ");
            productDetailsArrayList.add("Asus ROG Phone 7 ");
            productDetailsArrayList.add("Asus ROG Phone 7 Ultimate");
            productDetailsArrayList.add("Asus ZenFone 2 ");
            productDetailsArrayList.add("Asus ZenFone 2 Laser ");
            productDetailsArrayList.add("Asus Zenfone 3 Deluxe 5.5");
            productDetailsArrayList.add("Asus Zenfone 3 Laser ZC551KL ");
            productDetailsArrayList.add("Asus Zenfone 3 Max ZC520TL ");
            productDetailsArrayList.add("Asus Zenfone 3 ZE520KL");
            productDetailsArrayList.add("Asus Zenfone 3 ZE552KL");
            productDetailsArrayList.add("Asus Zenfone 3 Zoom ZE553KL");
            productDetailsArrayList.add("Asus ZenFone 4 ");
            productDetailsArrayList.add("Asus Zenfone 4 Max Pro ZC554KL ");
            productDetailsArrayList.add("Asus Zenfone 4 Max ZC554KL ");
            productDetailsArrayList.add("Asus ZenFone 5 ");
            productDetailsArrayList.add("Asus ZenFone 5 Lite A502CG ");
            productDetailsArrayList.add("Asus Zenfone 5 Lite ZC600KL ");
            productDetailsArrayList.add("Asus Zenfone 5 ZE620KL ");
            productDetailsArrayList.add("Asus Zenfone 5z ZS620KL");
            productDetailsArrayList.add("Asus ZenFone 6 ");
            productDetailsArrayList.add("Asus Zenfone 8 ");
            productDetailsArrayList.add("Asus Zenfone 8 Flip ");
            productDetailsArrayList.add("Asus Zenfone 9 ");
            productDetailsArrayList.add("Asus Zenfone 10 ");
            productDetailsArrayList.add("Asus Zenfone Go ZB551KL ");
            productDetailsArrayList.add("Asus ZenFone Go ZC500TG ");
            productDetailsArrayList.add("Asus Zenfone Live ZB501KL ");
            productDetailsArrayList.add("Asus ZenFone Max ZC550KL ");
            productDetailsArrayList.add("Asus Zenfone Max Plus (M1) ZB570TL");
            productDetailsArrayList.add("Asus Zenfone Max Pro (M1) ZB601KL ");
            productDetailsArrayList.add("Asus ZenFone Selfie");
            productDetailsArrayList.add("BlackBerry 7130G");
            productDetailsArrayList.add("BlackBerry 7290 ");
            productDetailsArrayList.add("BlackBerry 8100 ");
            productDetailsArrayList.add("BlackBerry 8320 Curve ");
            productDetailsArrayList.add("BlackBerry 8520 Curve ");
            productDetailsArrayList.add("BlackBerry 8700G");
            productDetailsArrayList.add("BlackBerry 8820 ");
            productDetailsArrayList.add("BlackBerry 9000 Bold ");
            productDetailsArrayList.add("BlackBerry 9220 Curve ");
            productDetailsArrayList.add("BlackBerry 9300 Curve ");
            productDetailsArrayList.add("BlackBerry 9320 Curve ");
            productDetailsArrayList.add("BlackBerry 9360 Curve ");
            productDetailsArrayList.add("BlackBerry 9500 Storm ");
            productDetailsArrayList.add("BlackBerry 9550 Storm 2 ");
            productDetailsArrayList.add("BlackBerry 9700 Bold ");
            productDetailsArrayList.add("BlackBerry 9780 Bold ");
            productDetailsArrayList.add("BlackBerry 9790 Bold ");
            productDetailsArrayList.add("BlackBerry 9800 Torch ");
            productDetailsArrayList.add("BlackBerry 9900 Bold ");
            productDetailsArrayList.add("BlackBerry Classic Q20 ");
            productDetailsArrayList.add("BlackBerry Keyone ");
            productDetailsArrayList.add("BlackBerry Key2 ");
            productDetailsArrayList.add("BlackBerry Key2 LE )");
            productDetailsArrayList.add("BlackBerry P9981 Porsche Design ");
            productDetailsArrayList.add("BlackBerry P9982 Porsche Design ");
            productDetailsArrayList.add("BlackBerry Passport ");
            productDetailsArrayList.add("BlackBerry Priv ");
            productDetailsArrayList.add("BlackBerry Q5 ");
            productDetailsArrayList.add("BlackBerry Q10");
            productDetailsArrayList.add("Casper VIA A1 ");
            productDetailsArrayList.add("Casper VIA A1 Plus ");
            productDetailsArrayList.add("Casper VIA A2 ");
            productDetailsArrayList.add("Casper VIA A3 ");
            productDetailsArrayList.add("Casper VIA A3316 ");
            productDetailsArrayList.add("Casper VIA A3 Plus ");
            productDetailsArrayList.add("Casper VIA A4 ");
            productDetailsArrayList.add("Casper VIA A6108 ");
            productDetailsArrayList.add("Casper VIA E1 ");
            productDetailsArrayList.add("Casper VIA E2 ");
            productDetailsArrayList.add("Casper VIA E3 ");
            productDetailsArrayList.add("Casper VIA E30");
            productDetailsArrayList.add("Casper VIA E30 Plus ");
            productDetailsArrayList.add("Casper VIA E4 ");
            productDetailsArrayList.add("Casper VIA F1 ");
            productDetailsArrayList.add("Casper VIA F2 ");
            productDetailsArrayList.add("Casper VIA F20 ");
            productDetailsArrayList.add("Casper VIA F3 ");
            productDetailsArrayList.add("Casper VIA F30 ");
            productDetailsArrayList.add("Casper VIA G1 )");
            productDetailsArrayList.add("Casper VIA G1 Plus ");
            productDetailsArrayList.add("Casper VIA G3 ");
            productDetailsArrayList.add("Casper VIA G4 ");
            productDetailsArrayList.add("Casper VIA G5 ");
            productDetailsArrayList.add("Casper VIA M1 ");
            productDetailsArrayList.add("Casper VIA M2 ");
            productDetailsArrayList.add("Casper VIA M3 ");
            productDetailsArrayList.add("Casper VIA M4 ");
            productDetailsArrayList.add("Casper VIA P1 ");
            productDetailsArrayList.add("Casper VIA P2");
            productDetailsArrayList.add("Casper VIA P3");
            productDetailsArrayList.add("Casper VIA S ");
            productDetailsArrayList.add("Casper VIA V3");
            productDetailsArrayList.add("Casper VIA V4");
            productDetailsArrayList.add("Casper VIA V5");
            productDetailsArrayList.add("Casper VIA V8");
            productDetailsArrayList.add("Casper VIA V8C ");
            productDetailsArrayList.add("Casper VIA V9 ");
            productDetailsArrayList.add("Casper VIA X20 ");
            productDetailsArrayList.add("Casper M500 ");
            productDetailsArrayList.add("General Mobile 4G Android One  ");
            productDetailsArrayList.add("General Mobile 4G Android One Dual  ");
            productDetailsArrayList.add("General Mobile Discovery  ");
            productDetailsArrayList.add("General Mobile Discovery 2 ");
            productDetailsArrayList.add("General Mobile Discovery 2 Mini  ");
            productDetailsArrayList.add("General Mobile Discovery Air  ");
            productDetailsArrayList.add("General Mobile Discovery Air 2GB  ");
            productDetailsArrayList.add("General Mobile Discovery Elite ");
            productDetailsArrayList.add("General Mobile DST 01 ");
            productDetailsArrayList.add("General Mobile DST 33 ");
            productDetailsArrayList.add("General Mobile DST 350 ");
            productDetailsArrayList.add("General Mobile DST 450 ");
            productDetailsArrayList.add("General Mobile DST 1907 Fenerbahçe  ");
            productDetailsArrayList.add("General Mobile DST 3G Smart  ");
            productDetailsArrayList.add("General Mobile DST Business  ");
            productDetailsArrayList.add("General Mobile DST Diamond  ");
            productDetailsArrayList.add("General Mobile DST Diamond Bar  ");
            productDetailsArrayList.add("General Mobile DST Surf  ");
            productDetailsArrayList.add("General Mobile DST Q100  ");
            productDetailsArrayList.add("General Mobile DST Q300  ");
            productDetailsArrayList.add("General Mobile G 333  ");
            productDetailsArrayList.add("General Mobile GM 5  ");
            productDetailsArrayList.add("General Mobile GM 5 Plus  ");
            productDetailsArrayList.add("General Mobile GM 5 Plus D  ");
            productDetailsArrayList.add("General Mobile GM 6  ");
            productDetailsArrayList.add("General Mobile GM 6 D  ");
            productDetailsArrayList.add("General Mobile GM 8  ");
            productDetailsArrayList.add("General Mobile GM 8 Go  ");
            productDetailsArrayList.add("General Mobile GM 9 Go  ");
            productDetailsArrayList.add("General Mobile GM 9 Plus ");
            productDetailsArrayList.add("General Mobile GM 9 Pro  ");
            productDetailsArrayList.add("General Mobile GM 10 ");
            productDetailsArrayList.add("General Mobile GM 20  ");
            productDetailsArrayList.add("General Mobile GM 20 Pro ");
            productDetailsArrayList.add("General Mobile GM 21  ");
            productDetailsArrayList.add("General Mobile GM 21 Plus  ");
            productDetailsArrayList.add("General Mobile GM 21 Pro  ");
            productDetailsArrayList.add("General Mobile GM 22  ");
            productDetailsArrayList.add("General Mobile GM 22 Plus  ");
            productDetailsArrayList.add("General Mobile GM 22 Pro ");
            productDetailsArrayList.add("General Mobile GM 22S  ");
            productDetailsArrayList.add("General Mobile GM 23  ");
            productDetailsArrayList.add("General Mobile GM 23 SE ");
            productDetailsArrayList.add("General Mobile Planet  ");
            productDetailsArrayList.add("Hiking A15 ");
            productDetailsArrayList.add("Hiking A20 ");
            productDetailsArrayList.add("Hiking A21 ");
            productDetailsArrayList.add("Hiking A22");
            productDetailsArrayList.add("Hiking A23");
            productDetailsArrayList.add("Hiking A26");
            productDetailsArrayList.add("Hiking A27");
            productDetailsArrayList.add("Hiking X6 ");
            productDetailsArrayList.add("Hiking X9 ");
            productDetailsArrayList.add("Hiking X11");
            productDetailsArrayList.add("Honor 5X (GR5)  ");
            productDetailsArrayList.add("Honor 6  ");
            productDetailsArrayList.add("Honor 6A (Pro)");
            productDetailsArrayList.add("Honor 6X  ");
            productDetailsArrayList.add("Honor 7  ");
            productDetailsArrayList.add("Honor 7A  ");
            productDetailsArrayList.add("Honor 7C  ");
            productDetailsArrayList.add("Honor 7S ");
            productDetailsArrayList.add("Honor 7X ");
            productDetailsArrayList.add("Honor 8  ");
            productDetailsArrayList.add("Honor 8A");
            productDetailsArrayList.add("Honor 8C");
            productDetailsArrayList.add("Honor 8S");
            productDetailsArrayList.add("Honor 8X");
            productDetailsArrayList.add("Honor 9  ");
            productDetailsArrayList.add("Honor 9 Lite  ");
            productDetailsArrayList.add("Honor 9X  ");
            productDetailsArrayList.add("Honor 9X Pro ");
            productDetailsArrayList.add("Honor X10 Max ");
            productDetailsArrayList.add("Honor 10  ");
            productDetailsArrayList.add("Honor 10 Lite");
            productDetailsArrayList.add("Honor 20  ");
            productDetailsArrayList.add("Honor 20 Lite  ");
            productDetailsArrayList.add("Honor 30 Pro  ");
            productDetailsArrayList.add("Honor 50  ");
            productDetailsArrayList.add("Honor 70  ");
            productDetailsArrayList.add("Honor Magic  ");
            productDetailsArrayList.add("Honor Magic 3 Pro ");
            productDetailsArrayList.add("Honor Magic 4 Pro ");
            productDetailsArrayList.add("Honor Magic 4 Ultimate ");
            productDetailsArrayList.add("Honor Magic 5 Lite ");
            productDetailsArrayList.add("Honor Magic 5 Pro  ");
            productDetailsArrayList.add("Honor Magic 5 Ultimate  ");
            productDetailsArrayList.add("Honor Magic VS  ");
            productDetailsArrayList.add("Honor Play ");
            productDetailsArrayList.add("Honor View 20");
            productDetailsArrayList.add("Honor X9A");
            productDetailsArrayList.add("Huawai Ascend G7 ");
            productDetailsArrayList.add("Huawai Ascend P6 ");
            productDetailsArrayList.add("Huawai Ascend P7 ");
            productDetailsArrayList.add("Huawai Ascend G510 ");
            productDetailsArrayList.add("Huawai Ascend G610 ");
            productDetailsArrayList.add("Huawai Ascend Mate 7 ");
            productDetailsArrayList.add("Huawai Ascend Mate 8 ");
            productDetailsArrayList.add("Huawai Ascend Y530 ");
            productDetailsArrayList.add("Huawai Enjoy 6s ");
            productDetailsArrayList.add("Huawai G8 ");
            productDetailsArrayList.add("Huawai GR3 ");
            productDetailsArrayList.add("Huawai GR5 2017  ");
            productDetailsArrayList.add("Huawai GR5 Mini ");
            productDetailsArrayList.add("Huawai GT3 Honor 5c  ");
            productDetailsArrayList.add("Huawai Mate 9 ");
            productDetailsArrayList.add("Huawai Mate 9 Porsche Design ");
            productDetailsArrayList.add("Huawai Mate 10 ");
            productDetailsArrayList.add("Huawai Mate 10 Lite  ");
            productDetailsArrayList.add("Huawai Mate 10 Pro  ");
            productDetailsArrayList.add("Huawai Mate 20 ");
            productDetailsArrayList.add("Huawai Mate 20 Lite ");
            productDetailsArrayList.add("Huawai Mate 20 Pro  ");
            productDetailsArrayList.add("Huawai Mate 20 RS Porsche Design ");
            productDetailsArrayList.add("Huawai Mate 20 X  ");
            productDetailsArrayList.add("Huawai Mate 30 Pro  ");
            productDetailsArrayList.add("Huawai Mate 30 RS Porsche Design ");
            productDetailsArrayList.add("Huawai Mate 40 Pro  ");
            productDetailsArrayList.add("Huawai Mate 40 RS Porsche Design ");
            productDetailsArrayList.add("Huawai Mate 50 Pro  ");
            productDetailsArrayList.add("Huawai Mate 50 RS Porsche Design ");
            productDetailsArrayList.add("Huawai Mate RS Porsche Design ");
            productDetailsArrayList.add("Huawai Mate S ");
            productDetailsArrayList.add("Huawai Mate X3  ");
            productDetailsArrayList.add("Huawai Mate XS  ");
            productDetailsArrayList.add("Huawai Mate XS 2 ");
            productDetailsArrayList.add("Huawai Nova  ");
            productDetailsArrayList.add("Huawai Nova 3 ");
            productDetailsArrayList.add("Huawai Nova 3i (P Smart+) ");
            productDetailsArrayList.add("Huawai Nova 5T  ");
            productDetailsArrayList.add("Huawai Nova 7i ");
            productDetailsArrayList.add("Huawai Nova 8i  ");
            productDetailsArrayList.add("Huawai Nova 9  ");
            productDetailsArrayList.add("Huawai Nova 9 SE  ");
            productDetailsArrayList.add("Huawai Nova Y70  ");
            productDetailsArrayList.add("Huawai Nova Y70 Plus ");
            productDetailsArrayList.add("Huawai Nova Y90  ");
            productDetailsArrayList.add("Huawai Nova Y91 ");
            productDetailsArrayList.add("Huawai Nova 10  ");
            productDetailsArrayList.add("Huawai P8 ");
            productDetailsArrayList.add("Huawai P8 Lite  ");
            productDetailsArrayList.add("Huawai P8 Lite (2017) ");
            productDetailsArrayList.add("Huawai P9  ");
            productDetailsArrayList.add("Huawai P9 Lite  ");
            productDetailsArrayList.add("Huawai P9 Lite (2017) ");
            productDetailsArrayList.add("Lenovo A316i  ");
            productDetailsArrayList.add("Lenovo A319  ");
            productDetailsArrayList.add("Lenovo A328  ");
            productDetailsArrayList.add("Lenovo A369i  ");
            productDetailsArrayList.add("Lenovo A806 Golden Warrior A8  ");
            productDetailsArrayList.add("Lenovo A2010  ");
            productDetailsArrayList.add("Lenovo A5000  ");
            productDetailsArrayList.add("Lenovo A6010  ");
            productDetailsArrayList.add("Lenovo A7000  ");
            productDetailsArrayList.add("Lenovo A7010 Vibe K4 Note  ");
            productDetailsArrayList.add("Lenovo K3 Note  ");
            productDetailsArrayList.add("Lenovo K5  ");
            productDetailsArrayList.add("Lenovo K5 Note  ");
            productDetailsArrayList.add("Lenovo K5 Plus  ");
            productDetailsArrayList.add("Lenovo K6  ");
            productDetailsArrayList.add("Lenovo K6 Note  ");
            productDetailsArrayList.add("Lenovo K6 Power  ");
            productDetailsArrayList.add("Lenovo K8 Note  ");
            productDetailsArrayList.add("Lenovo Legion 2 Pro  ");
            productDetailsArrayList.add("Lenovo Legion Duel  ");
            productDetailsArrayList.add("Lenovo Legion Pro  ");
            productDetailsArrayList.add("Lenovo Legion Y90  ");
            productDetailsArrayList.add("Lenovo K10 Note  ");
            productDetailsArrayList.add("Lenovo P2  ");
            productDetailsArrayList.add("Lenovo P70  ");
            productDetailsArrayList.add("Lenovo S60  ");
            productDetailsArrayList.add("Lenovo S90  ");
            productDetailsArrayList.add("Lenovo S580  ");
            productDetailsArrayList.add("Lenovo Vibe C A2020  ");
            productDetailsArrayList.add("Lg AKA ");
            productDetailsArrayList.add("Lg Chocolate Platinum KE800 ");
            productDetailsArrayList.add("Lg Cookie KP500 ");
            productDetailsArrayList.add("Lg Crystal GD900 ");
            productDetailsArrayList.add("Lg G Flex D958 ");
            productDetailsArrayList.add("Lg G Flex 2 H955 ");
            productDetailsArrayList.add("Lg G2 D802  ");
            productDetailsArrayList.add("Lg G2 Mini D610 ");
            productDetailsArrayList.add("Lg G3 D855  ");
            productDetailsArrayList.add("Lg G3 Beat  ");
            productDetailsArrayList.add("Lg G3 Stylus D690  ");
            productDetailsArrayList.add("Lg G4 Beat  ");
            productDetailsArrayList.add("Lg G4 H815  ");
            productDetailsArrayList.add("Lg G4 H818 ");
            productDetailsArrayList.add("Lg G4c H525n ");
            productDetailsArrayList.add("Lg G4 Stylus H540  ");
            productDetailsArrayList.add("Lg G5 H850  ");
            productDetailsArrayList.add("Lg G6 H870  ");
            productDetailsArrayList.add("Lg G7 ThinQ G710  ");
            productDetailsArrayList.add("Lg G8 ThinQ ");
            productDetailsArrayList.add("Lg K8 (2017)  ");
            productDetailsArrayList.add("Lg K8 K350N  ");
            productDetailsArrayList.add("Lg K9 LMX210 ");
            productDetailsArrayList.add("Lg K10 (2017)  ");
            productDetailsArrayList.add("Lg K10 K420N ");
            productDetailsArrayList.add("Lg K10 LTE  ");
            productDetailsArrayList.add("Lg K11 ");
            productDetailsArrayList.add("Lg K20  ");
            productDetailsArrayList.add("Lg K40S  ");
            productDetailsArrayList.add("Lg K41S ");
            productDetailsArrayList.add("Lg K50 ");
            productDetailsArrayList.add("Lg K61  ");
            productDetailsArrayList.add("Lg K62 ");
            productDetailsArrayList.add("Lg L40 D160 ");
            productDetailsArrayList.add("Lg L70 D320 ");
            productDetailsArrayList.add("Lg L80 D380 ");
            productDetailsArrayList.add("Lg L90 D405 ");
            productDetailsArrayList.add("Lg L Bello D331 ");
            productDetailsArrayList.add("Nokia 1 ");
            productDetailsArrayList.add("Nokia 2  ");
            productDetailsArrayList.add("Nokia 2.1 ");
            productDetailsArrayList.add("Nokia 2.2 ");
            productDetailsArrayList.add("Nokia 3 ");
            productDetailsArrayList.add("Nokia 3.1 ");
            productDetailsArrayList.add("Nokia 3.1 Plus ");
            productDetailsArrayList.add("Nokia 5  ");
            productDetailsArrayList.add("Nokia 5.1 Plus (X5) ");
            productDetailsArrayList.add("Nokia 6  ");
            productDetailsArrayList.add("Nokia 6 (2018) ");
            productDetailsArrayList.add("Nokia 8 ");
            productDetailsArrayList.add("Nokia 8 Sirocco ");
            productDetailsArrayList.add("Nokia 100  ");
            productDetailsArrayList.add("Nokia 101 ");
            productDetailsArrayList.add("Nokia 105 ");
            productDetailsArrayList.add("Nokia 106 ");
            productDetailsArrayList.add("Nokia 106 (2018) ");
            productDetailsArrayList.add("Nokia 112  ");
            productDetailsArrayList.add("Nokia 113  ");
            productDetailsArrayList.add("Nokia 206  ");
            productDetailsArrayList.add("Nokia 208 ");
            productDetailsArrayList.add("Nokia 215 ");
            productDetailsArrayList.add("Nokia 215 Dual SIM ");
            productDetailsArrayList.add("Nokia 216 ");
            productDetailsArrayList.add("Nokia 220  ");
            productDetailsArrayList.add("Nokia 222 ");
            productDetailsArrayList.add("Nokia 225  ");
            productDetailsArrayList.add("Nokia 230 ");
            productDetailsArrayList.add("Nokia 301  ");
            productDetailsArrayList.add("Nokia 500  ");
            productDetailsArrayList.add("Nokia 515  ");
            productDetailsArrayList.add("Nokia 603  ");
            productDetailsArrayList.add("Nokia 700  ");
            productDetailsArrayList.add("Nokia 701 ");
            productDetailsArrayList.add("Nokia 1100  ");
            productDetailsArrayList.add("Nokia 1110  ");
            productDetailsArrayList.add("Nokia 1110i  ");
            productDetailsArrayList.add("Nokia 1112  ");
            productDetailsArrayList.add("Nokia 1200  ");
            productDetailsArrayList.add("Nokia 1203  ");
            productDetailsArrayList.add("Nokia 1208  ");
            productDetailsArrayList.add("Nokia 1209  ");
            productDetailsArrayList.add("Nokia 1280  ");
            productDetailsArrayList.add("Nokia 1600  ");
            productDetailsArrayList.add("Nokia 1616  ");
            productDetailsArrayList.add("Nokia 1650  ");
            productDetailsArrayList.add("Nokia 1661  ");
            productDetailsArrayList.add("Nokia 1680 Classic  ");
            productDetailsArrayList.add("Nokia 1800  ");
            productDetailsArrayList.add("Nokia 2100  ");
            productDetailsArrayList.add("Nokia 2220 Slide  ");
            productDetailsArrayList.add("Nokia 2300  ");
            productDetailsArrayList.add("Nokia 2310 ");
            productDetailsArrayList.add("Nokia 2323 Classic  ");
            productDetailsArrayList.add("Nokia 2330 Classic  ");
            productDetailsArrayList.add("Nokia 2600  ");
            productDetailsArrayList.add("Nokia 2600 Classic  ");
            productDetailsArrayList.add("Nokia 2610  ");
            productDetailsArrayList.add("Nokia 2630  ");
            productDetailsArrayList.add("Nokia 2650 ");
            productDetailsArrayList.add("Nokia 2652 ");
            productDetailsArrayList.add("Nokia 2660 ");
            productDetailsArrayList.add("Nokia 2680 Slide  ");
            productDetailsArrayList.add("Nokia 2690  ");
            productDetailsArrayList.add("Nokia 2700 Classic  ");
            productDetailsArrayList.add("Nokia 2710 Navigation Edition ");
            productDetailsArrayList.add("Nokia 2720 Fold ");
            productDetailsArrayList.add("Nokia 2730 Classic  ");
            productDetailsArrayList.add("Nokia 2760  ");
            productDetailsArrayList.add("Nokia 3100  ");
            productDetailsArrayList.add("Nokia 3110 Classic  ");
            productDetailsArrayList.add("Nokia 3110 Evolve ");
            productDetailsArrayList.add("Nokia 3120  ");
            productDetailsArrayList.add("Nokia 3120 Classic  ");
            productDetailsArrayList.add("Nokia 3200  ");
            productDetailsArrayList.add("Nokia 3210  ");
            productDetailsArrayList.add("Nokia 3220  ");
            productDetailsArrayList.add("Nokia 3230 ");
            productDetailsArrayList.add("Nokia 3250 ");
            productDetailsArrayList.add("Nokia 3250 XpressMusic ");
            productDetailsArrayList.add("Nokia 3300 ");
            productDetailsArrayList.add("Nokia 3310  ");
            productDetailsArrayList.add("Nokia 3310 (2017) ");
            productDetailsArrayList.add("Nokia 3315  ");
            productDetailsArrayList.add("Nokia 3410  ");
            productDetailsArrayList.add("Nokia 3500 Classic  ");
            productDetailsArrayList.add("Nokia 3510  ");
            productDetailsArrayList.add("Nokia 3510i  ");
            productDetailsArrayList.add("Nokia 3600 Slide  ");
            productDetailsArrayList.add("Nokia 3610 ");
            productDetailsArrayList.add("Nokia 3650 ");
            productDetailsArrayList.add("Nokia 3710 Fold  ");
            productDetailsArrayList.add("Nokia 3720 Classic ");
            productDetailsArrayList.add("Nokia 5000  ");
            productDetailsArrayList.add("Nokia 5030 XpressRadio ");
            productDetailsArrayList.add("Nokia 5070  ");
            productDetailsArrayList.add("Nokia 5100 ");
            productDetailsArrayList.add("Nokia 5110  ");
            productDetailsArrayList.add("Nokia 5130 XpressMusic  ");
            productDetailsArrayList.add("Nokia 5140  ");
            productDetailsArrayList.add("Nokia 5200  ");
            productDetailsArrayList.add("Nokia 5210  ");
            productDetailsArrayList.add("Nokia 5220 XpressMusic ");
            productDetailsArrayList.add("Nokia 5228 ");
            productDetailsArrayList.add("Nokia 5230 ");
            productDetailsArrayList.add("Nokia 5250 ");
            productDetailsArrayList.add("Nokia 5300 XpressMusic ");
            productDetailsArrayList.add("Nokia 5310 XpressMusic  ");
            productDetailsArrayList.add("Nokia 5320 XpressMusic  ");
            productDetailsArrayList.add("Nokia 5500 Sport ");
            productDetailsArrayList.add("Nokia 5510 ");
            productDetailsArrayList.add("Nokia 5610 XpressMusic ");
            productDetailsArrayList.add("Nokia 5700 XpressMusic ");
            productDetailsArrayList.add("Nokia 5730 XpressMusic ");
            productDetailsArrayList.add("Nokia 5800 XpressMusic  ");
            productDetailsArrayList.add("Nokia 6020  ");
            productDetailsArrayList.add("Nokia 6030  ");
            productDetailsArrayList.add("Nokia 6060 ");
            productDetailsArrayList.add("Nokia 6070  ");
            productDetailsArrayList.add("Nokia 6080  ");
            productDetailsArrayList.add("Nokia 6085 ");
            productDetailsArrayList.add("Nokia 6100  ");
            productDetailsArrayList.add("Nokia 6101  ");
            productDetailsArrayList.add("Nokia 6103  ");
            productDetailsArrayList.add("Nokia 6110 ");
            productDetailsArrayList.add("Nokia 6111 ");
            productDetailsArrayList.add("Nokia 6120 Classic  ");
            productDetailsArrayList.add("Nokia 6125 ");
            productDetailsArrayList.add("Nokia 6131  ");
            productDetailsArrayList.add("Nokia 6150 ");
            productDetailsArrayList.add("Nokia 6151  ");
            productDetailsArrayList.add("Nokia 6170 ");
            productDetailsArrayList.add("Nokia 6210 ");
            productDetailsArrayList.add("Nokia 6210 Navigator ");
            productDetailsArrayList.add("Nokia 6220  ");
            productDetailsArrayList.add("Nokia 6220 Classic  ");
            productDetailsArrayList.add("Nokia 6230  ");
            productDetailsArrayList.add("Nokia 6230i  ");
            productDetailsArrayList.add("Nokia 6233  ");
            productDetailsArrayList.add("Nokia 6234 ");
            productDetailsArrayList.add("Nokia 6250 ");
            productDetailsArrayList.add("Nokia 6260  ");
            productDetailsArrayList.add("Nokia 6267 ");
            productDetailsArrayList.add("Nokia 6270 ");
            productDetailsArrayList.add("Nokia 6280 ");
            productDetailsArrayList.add("Nokia 6288 ");
            productDetailsArrayList.add("Nokia 6300  ");
            productDetailsArrayList.add("Nokia 6303i Classic  ");
            productDetailsArrayList.add("Nokia 6310  ");
            productDetailsArrayList.add("Nokia 6310i  ");
            productDetailsArrayList.add("Nokia 6500 Classic  ");
            productDetailsArrayList.add("Nokia 6500 Slide  ");
            productDetailsArrayList.add("Nokia 6510  ");
            productDetailsArrayList.add("Nokia 6555 ");
            productDetailsArrayList.add("Nokia 6600  ");
            productDetailsArrayList.add("Nokia 6600i Slide ");
            productDetailsArrayList.add("Nokia 6600 Slide  ");
            productDetailsArrayList.add("Nokia 6610  ");
            productDetailsArrayList.add("Nokia 6610i  ");
            productDetailsArrayList.add("Nokia 6630  ");
            productDetailsArrayList.add("Nokia 6650 T-Mobile ");
            productDetailsArrayList.add("Nokia 6670  ");
            productDetailsArrayList.add("Nokia 6680  ");
            productDetailsArrayList.add("Nokia 6681 ");
            productDetailsArrayList.add("Nokia 6700 Classic  ");
            productDetailsArrayList.add("Nokia 6700 Gold ");
            productDetailsArrayList.add("Nokia 6700 Slide  ");
            productDetailsArrayList.add("Nokia 6710 Navigator ");
            productDetailsArrayList.add("Nokia 6720 Classic ");
            productDetailsArrayList.add("Nokia 6730 Classic  ");
            productDetailsArrayList.add("Nokia 6760 Slide ");
            productDetailsArrayList.add("Nokia 6810 ");
            productDetailsArrayList.add("Nokia 6820 ");
            productDetailsArrayList.add("Nokia 6822 ");
            productDetailsArrayList.add("Nokia 7020  ");
            productDetailsArrayList.add("Nokia 7070 Prism ");
            productDetailsArrayList.add("Nokia 7100 Supernova  ");
            productDetailsArrayList.add("Nokia 7110  ");
            productDetailsArrayList.add("Nokia 7200 ");
            productDetailsArrayList.add("Nokia 7210  ");
            productDetailsArrayList.add("Nokia 7230  ");
            productDetailsArrayList.add("Nokia 7250  ");
            productDetailsArrayList.add("Nokia 7250i ");
            productDetailsArrayList.add("Nokia 7260  ");
            productDetailsArrayList.add("Nokia 7280 ");
            productDetailsArrayList.add("Nokia 7310 Supernova  ");
            productDetailsArrayList.add("Nokia 7360  ");
            productDetailsArrayList.add("Nokia 7370 ");
            productDetailsArrayList.add("Nokia 7373 ");
            productDetailsArrayList.add("Nokia 7380 ");
            productDetailsArrayList.add("Nokia 7390 ");
            productDetailsArrayList.add("Nokia 7500 Prism  ");
            productDetailsArrayList.add("Nokia 7510 Supernova  ");
            productDetailsArrayList.add("Nokia 7600 ");
            productDetailsArrayList.add("Nokia 7610  ");
            productDetailsArrayList.add("Nokia 7610 Supernova ");
            productDetailsArrayList.add("Nokia 7650 ");
            productDetailsArrayList.add("Nokia 7710 ");
            productDetailsArrayList.add("Nokia 808 PureView ");
            productDetailsArrayList.add("Nokia 8110 ");
            productDetailsArrayList.add("Nokia 8210  ");
            productDetailsArrayList.add("Nokia 8250 ");
            productDetailsArrayList.add("Nokia 8310  ");
            productDetailsArrayList.add("Nokia 8600 Luna ");
            productDetailsArrayList.add("Nokia 8800  ");
            productDetailsArrayList.add("Nokia 8800 Arte  ");
            productDetailsArrayList.add("Nokia 8800 Carbon Arte ");
            productDetailsArrayList.add("Nokia 8800 Sapphire  ");
            productDetailsArrayList.add("Nokia 8800 Sirocco  ");
            productDetailsArrayList.add("Nokia 8810 ");
            productDetailsArrayList.add("Nokia 8850  ");
            productDetailsArrayList.add("Nokia 8890 ");
            productDetailsArrayList.add("Nokia 8910  ");
            productDetailsArrayList.add("Nokia 8910i ");
            productDetailsArrayList.add("Nokia 9000 ");
            productDetailsArrayList.add("Nokia 9110 ");
            productDetailsArrayList.add("Nokia 9210 ");
            productDetailsArrayList.add("Nokia 9210i ");
            productDetailsArrayList.add("Nokia 9300 ");
            productDetailsArrayList.add("Nokia 9300i ");
            productDetailsArrayList.add("Nokia 9500 ");
            productDetailsArrayList.add("Nokia Asha 200  ");
            productDetailsArrayList.add("Nokia Asha 201  ");
            productDetailsArrayList.add("Nokia Asha 203  ");
            productDetailsArrayList.add("Nokia Asha 210  ");
            productDetailsArrayList.add("Nokia Asha 210 Dual SIM ");
            productDetailsArrayList.add("Nokia Asha 300  ");
            productDetailsArrayList.add("Nokia Asha 301 ");
            productDetailsArrayList.add("Nokia Asha 302  ");
            productDetailsArrayList.add("Nokia Asha 303  ");
            productDetailsArrayList.add("Nokia Asha 306 ");
            productDetailsArrayList.add("Nokia Asha 309 ");
            productDetailsArrayList.add("Nokia Asha 311  ");
            productDetailsArrayList.add("Nokia Asha 500 ");
            productDetailsArrayList.add("Nokia Asha 501 ");
            productDetailsArrayList.add("Nokia Asha 503 ");
            productDetailsArrayList.add("Nokia C1-01 ");
            productDetailsArrayList.add("Nokia C1-02 ");
            productDetailsArrayList.add("Nokia C2-00  ");
            productDetailsArrayList.add("Nokia C2-01  ");
            productDetailsArrayList.add("Nokia C2-02  ");
            productDetailsArrayList.add("Nokia C2-03 ");
            productDetailsArrayList.add("Nokia C2-05 ");
            productDetailsArrayList.add("Nokia C2-06  ");
            productDetailsArrayList.add("Nokia C3-00  ");
            productDetailsArrayList.add("Nokia C3-01 Touch and Type  ");
            productDetailsArrayList.add("Nokia C5-00  ");
            productDetailsArrayList.add("Nokia C5-03  ");
            productDetailsArrayList.add("Nokia C6-00 ");
            productDetailsArrayList.add("Nokia C6-01  ");
            productDetailsArrayList.add("Nokia C7-00  ");
            productDetailsArrayList.add("Nokia E5  ");
            productDetailsArrayList.add("Nokia E50 ");
            productDetailsArrayList.add("Nokia E51 ");
            productDetailsArrayList.add("Nokia E52 ");
            productDetailsArrayList.add("Nokia E55 ");
            productDetailsArrayList.add("Nokia E60 ");
            productDetailsArrayList.add("Nokia E6-00 ");
            productDetailsArrayList.add("Nokia E61");
            productDetailsArrayList.add("Nokia E61i");
            productDetailsArrayList.add("Nokia E63 ");
            productDetailsArrayList.add("Nokia E65 ");
            productDetailsArrayList.add("Nokia E66 ");
            productDetailsArrayList.add("Nokia E70");
            productDetailsArrayList.add("Nokia E7-00");
            productDetailsArrayList.add("Nokia E71 ");
            productDetailsArrayList.add("Nokia E72 ");
            productDetailsArrayList.add("Nokia Lumia 520 ");
            productDetailsArrayList.add("Nokia Lumia 525");
            productDetailsArrayList.add("Nokia Lumia 535");
            productDetailsArrayList.add("Nokia Lumia 620");
            productDetailsArrayList.add("Nokia Lumia 625");
            productDetailsArrayList.add("Nokia Lumia 630");
            productDetailsArrayList.add("Nokia Lumia 720");
            productDetailsArrayList.add("Nokia Lumia 735");
            productDetailsArrayList.add("Nokia Lumia 820");
            productDetailsArrayList.add("Nokia Lumia 830");
            productDetailsArrayList.add("Nokia Lumia 920");
            productDetailsArrayList.add("Nokia Lumia 925");
            productDetailsArrayList.add("Nokia Lumia 1020");
            productDetailsArrayList.add("Nokia Lumia 1320");
            productDetailsArrayList.add("Nokia Lumia 1520");
            productDetailsArrayList.add("Nokia N8 ");
            productDetailsArrayList.add("Nokia N-Gage ");
            productDetailsArrayList.add("Nokia N-Gage QD");
            productDetailsArrayList.add("Nokia N70 ");
            productDetailsArrayList.add("Nokia N71");
            productDetailsArrayList.add("Nokia N72 ");
            productDetailsArrayList.add("Nokia N73 ");
            productDetailsArrayList.add("Nokia N76");
            productDetailsArrayList.add("Nokia N78 ");
            productDetailsArrayList.add("Nokia N79 ");
            productDetailsArrayList.add("Nokia N80");
            productDetailsArrayList.add("Nokia N81");
            productDetailsArrayList.add("Nokia N82");
            productDetailsArrayList.add("Nokia N85");
            productDetailsArrayList.add("Nokia N86 8MP");
            productDetailsArrayList.add("Nokia N90");
            productDetailsArrayList.add("Nokia N91");
            productDetailsArrayList.add("Nokia N92");
            productDetailsArrayList.add("Nokia N93");
            productDetailsArrayList.add("Nokia N93i");
            productDetailsArrayList.add("Nokia N95 ");
            productDetailsArrayList.add("Nokia N95 8GB ");
            productDetailsArrayList.add("Nokia N96 ");
            productDetailsArrayList.add("Nokia N97 ");
            productDetailsArrayList.add("Nokia N97 Mini");
            productDetailsArrayList.add("Nokia N900");
            productDetailsArrayList.add("Nokia X Dual SIM");
            productDetailsArrayList.add("Nokia X1-00 ");
            productDetailsArrayList.add("Nokia X1-01");
            productDetailsArrayList.add("Nokia X2 ");
            productDetailsArrayList.add("Nokia X2-01");
            productDetailsArrayList.add("Nokia X2-02 ");
            productDetailsArrayList.add("Nokia X3-00 ");
            productDetailsArrayList.add("Nokia X3-02 Touch and Type ");
            productDetailsArrayList.add("Nokia X6 ");
            productDetailsArrayList.add("OnePLus 3 ");
            productDetailsArrayList.add("OnePLus 3T ");
            productDetailsArrayList.add("OnePLus 5 ");
            productDetailsArrayList.add("OnePLus 5T  ");
            productDetailsArrayList.add("OnePLus 6  ");
            productDetailsArrayList.add("OnePLus 6T ");
            productDetailsArrayList.add("OnePLus 7  ");
            productDetailsArrayList.add("OnePLus 7 Pro  ");
            productDetailsArrayList.add("OnePLus 7T  ");
            productDetailsArrayList.add("OnePLus 7T Pro  ");
            productDetailsArrayList.add("OnePLus 8  ");
            productDetailsArrayList.add("OnePLus 8 Pro  ");
            productDetailsArrayList.add("OnePLus 8T  ");
            productDetailsArrayList.add("OnePLus 9  ");
            productDetailsArrayList.add("OnePLus 9 Pro ( ");
            productDetailsArrayList.add("OnePLus 9R  ");
            productDetailsArrayList.add("OnePLus 9RT ");
            productDetailsArrayList.add("OnePLus 10 Pro  ");
            productDetailsArrayList.add("OnePLus 10R ");
            productDetailsArrayList.add("OnePLus 10T  ");
            productDetailsArrayList.add("OnePLus 11  ");
            productDetailsArrayList.add("OnePLus 11R ");
            productDetailsArrayList.add("OnePLus Nord  ");
            productDetailsArrayList.add("OnePLus Nord CE 3 Lite ");
            productDetailsArrayList.add("OnePLus Nord N10  ");
            productDetailsArrayList.add("Oppoo 3000  ");
            productDetailsArrayList.add("Oppoo A5 (2020)  ");
            productDetailsArrayList.add("Oppoo A5S   ");
            productDetailsArrayList.add("Oppoo A9 2020   ");
            productDetailsArrayList.add("Oppoo A12 ");
            productDetailsArrayList.add("Oppoo A15 ");
            productDetailsArrayList.add("Oppoo A15s ");
            productDetailsArrayList.add("Oppoo A16 ");
            productDetailsArrayList.add("Oppoo A31 ");
            productDetailsArrayList.add("Oppoo A52 ");
            productDetailsArrayList.add("Oppoo A53 ");
            productDetailsArrayList.add("Oppoo A54 ");
            productDetailsArrayList.add("Oppoo A55  ");
            productDetailsArrayList.add("Oppoo A57 ");
            productDetailsArrayList.add("Oppoo A72 ");
            productDetailsArrayList.add("Oppoo A73 ");
            productDetailsArrayList.add("Oppoo A74 ");
            productDetailsArrayList.add("Oppoo A77 ");
            productDetailsArrayList.add("Oppoo A91 ");
            productDetailsArrayList.add("Oppoo A96 ");
            productDetailsArrayList.add("Oppoo AX7 ");
            productDetailsArrayList.add("Oppoo F1s  ");
            productDetailsArrayList.add("Oppoo F9  ");
            productDetailsArrayList.add("Oppoo F11 Pro  ");
            productDetailsArrayList.add("Oppoo Find N  ");
            productDetailsArrayList.add("Oppoo Find N2  ");
            productDetailsArrayList.add("Oppoo Find N2 Flip   ");
            productDetailsArrayList.add("Oppoo Find X2  ");
            productDetailsArrayList.add("Oppoo Find X2 Pro  ");
            productDetailsArrayList.add("Oppoo Find X3 Pro  ");
            productDetailsArrayList.add("Oppoo Find X5 Pro  ");
            productDetailsArrayList.add("Oppoo Find X6 Pro  ");
            productDetailsArrayList.add("Oppoo Neo 3  ");
            productDetailsArrayList.add("Oppoo RX17 Neo   ");
            productDetailsArrayList.add("Oppoo RX17 Pro  ");
            productDetailsArrayList.add("Oppoo R5  ");
            productDetailsArrayList.add("Oppoo R15  ");
            productDetailsArrayList.add("Oppoo R17 Pro  ");
            productDetailsArrayList.add("Oppoo R817 Real  ");
            productDetailsArrayList.add("Oppoo Reno   ");
            productDetailsArrayList.add("Oppoo Reno 10x Zoom  ");
            productDetailsArrayList.add("Oppoo Reno2  ");
            productDetailsArrayList.add("Oppoo Reno2 Z (  ");
            productDetailsArrayList.add("Oppoo Reno3   ");
            productDetailsArrayList.add("Oppoo Reno3 Pro   ");
            productDetailsArrayList.add("Oppoo Reno Z   ");
            productDetailsArrayList.add("Oppoo Reno4   ");
            productDetailsArrayList.add("Oppoo Reno4 Lite   ");
            productDetailsArrayList.add("Oppoo Reno4 Pro  ");
            productDetailsArrayList.add("Oppoo Reno5   ");
            productDetailsArrayList.add("Oppoo Reno5 Lite (  ");
            productDetailsArrayList.add("Oppoo Reno6   ");
            productDetailsArrayList.add("Oppoo Reno6 Pro  ");
            productDetailsArrayList.add("Oppoo Reno7   ");
            productDetailsArrayList.add("Oppoo Reno7 Pro  ");
            productDetailsArrayList.add("Oppoo Reno7 SE  ");
            productDetailsArrayList.add("Oppoo Reno8  ");
            productDetailsArrayList.add("Oppoo Reno8 Pro  ");
            productDetailsArrayList.add("Philips 160  ");
            productDetailsArrayList.add("Philips 180  ");
            productDetailsArrayList.add("Philips 191  ");
            productDetailsArrayList.add("Philips 192  ");
            productDetailsArrayList.add("Philips 290  ");
            productDetailsArrayList.add("Philips 588  ");
            productDetailsArrayList.add("Philips 650/Xenium 9@9c  ");
            productDetailsArrayList.add("Philips 760 ");
            productDetailsArrayList.add("Philips Fizz ");
            productDetailsArrayList.add("Philips S 200  ");
            productDetailsArrayList.add("Philips S 220  ");
            productDetailsArrayList.add("Philips S 880  ");
            productDetailsArrayList.add("Philips Xenium 9@9++ ");
            productDetailsArrayList.add("Philips Xenium 9@9d  ");
            productDetailsArrayList.add("Philips Xenium 9@9e  ");
            productDetailsArrayList.add("Philips Xenium 9@9h  ");
            productDetailsArrayList.add("Philips Xenium 9@9k  ");
            productDetailsArrayList.add("Philips Xenium 9@9u  ");
            productDetailsArrayList.add("Philips Xenium 9@9z  ");
            productDetailsArrayList.add("Philips Xenium X800  ");
            productDetailsArrayList.add("Realme 1 ");
            productDetailsArrayList.add("Realme 2 ");
            productDetailsArrayList.add("Realme 2 Pro ");
            productDetailsArrayList.add("Realme 3 Pro ");
            productDetailsArrayList.add("Realme 5i  ");
            productDetailsArrayList.add("Realme 5 Pro  ");
            productDetailsArrayList.add("Realme 6  ");
            productDetailsArrayList.add("Realme 6i ");
            productDetailsArrayList.add("Realme 6 Pro  ");
            productDetailsArrayList.add("Realme 7  ");
            productDetailsArrayList.add("Realme 7 Pro  ");
            productDetailsArrayList.add("Realme 8 ");
            productDetailsArrayList.add("Realme 8 Pro  ");
            productDetailsArrayList.add("Realme 9 Pro  ");
            productDetailsArrayList.add("Realme 10  ");
            productDetailsArrayList.add("Realme 10 Pro ");
            productDetailsArrayList.add("Realme 10 Pro Plus  ");
            productDetailsArrayList.add("Realme C2  ");
            productDetailsArrayList.add("Realme C3  ");
            productDetailsArrayList.add("Realme C11 (");
            productDetailsArrayList.add("Realme C15  ");
            productDetailsArrayList.add("Realme C21  ");
            productDetailsArrayList.add("Realme C21Y  ");
            productDetailsArrayList.add("Realme C25  ");
            productDetailsArrayList.add("Realme C25S  ");
            productDetailsArrayList.add("Realme C25Y  ");
            productDetailsArrayList.add("Realme C55  ");
            productDetailsArrayList.add("Realme C3i  ");
            productDetailsArrayList.add("Realme GT  ");
            productDetailsArrayList.add("Realme GT 2  ");
            productDetailsArrayList.add("Realme GT 2 Pro  ");
            productDetailsArrayList.add("Realme GT Master Edition  ");
            productDetailsArrayList.add("Realme GT Neo ");
            productDetailsArrayList.add("Realme GT Neo 2 ");
            productDetailsArrayList.add("Realme GT Neo 3 ");
            productDetailsArrayList.add("Realme GT Neo 5 ");
            productDetailsArrayList.add("Realme X2 ");
            productDetailsArrayList.add("Realme X2 Pro ");
            productDetailsArrayList.add("Reeder S19 Max ");
            productDetailsArrayList.add("Reeder S19 Max Pro");
            productDetailsArrayList.add("Reeder S19 Max Pro S");
            productDetailsArrayList.add("Reeder P1");
            productDetailsArrayList.add("Reeder P10");
            productDetailsArrayList.add("Reeder P10S");
            productDetailsArrayList.add("Reeder P1");
            productDetailsArrayList.add("Reeder P11");
            productDetailsArrayList.add("Reeder P11S");
            productDetailsArrayList.add("Reeder P12 ");
            productDetailsArrayList.add("Reeder P13 Blue ");
            productDetailsArrayList.add("Reeder P13 Blue 2021");
            productDetailsArrayList.add("Reeder P13 Blue Max ");
            productDetailsArrayList.add("Reeder P13 Blue Max 2021");
            productDetailsArrayList.add("Reeder P13 Blue Max L 2021");
            productDetailsArrayList.add("Reeder P13 Blue Max L 2022");
            productDetailsArrayList.add("Reeder P13 Blue Max Lite ");
            productDetailsArrayList.add("Reeder P13 Blue Max Pro ");
            productDetailsArrayList.add("Reeder P13 Blue Max Pro Lite 2022");
            productDetailsArrayList.add("Reeder P13 Blue Plus");
            productDetailsArrayList.add("Samsung Galaxy 3 i5800");
            productDetailsArrayList.add("Samsung Galaxy 5 i5500");
            productDetailsArrayList.add("Samsung Galaxy A01 ");
            productDetailsArrayList.add("Samsung Galaxy A01 Core ");
            productDetailsArrayList.add("Samsung Galaxy A02 ");
            productDetailsArrayList.add("Samsung Galaxy A02s ");
            productDetailsArrayList.add("Samsung Galaxy A03 ");
            productDetailsArrayList.add("Samsung Galaxy A03s ");
            productDetailsArrayList.add("Samsung Galaxy A04 ");
            productDetailsArrayList.add("Samsung Galaxy A04e ");
            productDetailsArrayList.add("Samsung Galaxy A04s ");
            productDetailsArrayList.add("Samsung Galaxy A2 Core ");
            productDetailsArrayList.add("Samsung Galaxy A3 (2015)");
            productDetailsArrayList.add("Samsung Galaxy A3 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy A3 (2017) ");
            productDetailsArrayList.add("Samsung Galaxy A5 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy A5 (2017) ");
            productDetailsArrayList.add("Samsung Galaxy A5 A500 ");
            productDetailsArrayList.add("Samsung Galaxy A6 (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A6+ (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A7 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy A7 (2017) ");
            productDetailsArrayList.add("Samsung Galaxy A7 (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A7 A700 ");
            productDetailsArrayList.add("Samsung Galaxy A8 (2015) ");
            productDetailsArrayList.add("Samsung Galaxy A8 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy A8 (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A8+ (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A8 Duos");
            productDetailsArrayList.add("Samsung Galaxy A8s");
            productDetailsArrayList.add("Samsung Galaxy A9 (2018) ");
            productDetailsArrayList.add("Samsung Galaxy A9 Pro (2016)");
            productDetailsArrayList.add("Samsung Galaxy A10 ");
            productDetailsArrayList.add("Samsung Galaxy A10e");
            productDetailsArrayList.add("Samsung Galaxy A10s ");
            productDetailsArrayList.add("Samsung Galaxy A11 ");
            productDetailsArrayList.add("Samsung Galaxy A12 ");
            productDetailsArrayList.add("Samsung Galaxy A13 ");
            productDetailsArrayList.add("Samsung Galaxy A14 ");
            productDetailsArrayList.add("Samsung Galaxy A20 ");
            productDetailsArrayList.add("Samsung Galaxy A20e");
            productDetailsArrayList.add("Samsung Galaxy A20s ");
            productDetailsArrayList.add("Samsung Galaxy A21");
            productDetailsArrayList.add("Samsung Galaxy A21s ");
            productDetailsArrayList.add("Samsung Galaxy A22 ");
            productDetailsArrayList.add("Samsung Galaxy A23 ");
            productDetailsArrayList.add("Samsung Galaxy A24 ");
            productDetailsArrayList.add("Samsung Galaxy A30 ");
            productDetailsArrayList.add("Samsung Galaxy A30s ");
            productDetailsArrayList.add("Samsung Galaxy A31 ");
            productDetailsArrayList.add("Samsung Galaxy A32 ");
            productDetailsArrayList.add("Samsung Galaxy A33 ");
            productDetailsArrayList.add("Samsung Galaxy A34 ");
            productDetailsArrayList.add("Samsung Galaxy A40 ");
            productDetailsArrayList.add("Samsung Galaxy A41");
            productDetailsArrayList.add("Samsung Galaxy A50 ");
            productDetailsArrayList.add("Samsung Galaxy A51 ");
            productDetailsArrayList.add("Samsung Galaxy A52 ");
            productDetailsArrayList.add("Samsung Galaxy A52s ");
            productDetailsArrayList.add("Samsung Galaxy A53 ");
            productDetailsArrayList.add("Samsung Galaxy A54 ");
            productDetailsArrayList.add("Samsung Galaxy A70 ");
            productDetailsArrayList.add("Samsung Galaxy A71 ");
            productDetailsArrayList.add("Samsung Galaxy A72 ");
            productDetailsArrayList.add("Samsung Galaxy A73 ");
            productDetailsArrayList.add("Samsung Galaxy A80 ");
            productDetailsArrayList.add("Samsung Galaxy Ace S5830");
            productDetailsArrayList.add("Samsung Galaxy Ace S5830i ");
            productDetailsArrayList.add("Samsung Galaxy Ace 2 i8160 ");
            productDetailsArrayList.add("Samsung Galaxy Ace 4 G313");
            productDetailsArrayList.add("Samsung Galaxy Alpha G850 ");
            productDetailsArrayList.add("Samsung Galaxy Beam i8530");
            productDetailsArrayList.add("Samsung Galaxy C5 ");
            productDetailsArrayList.add("Samsung Galaxy C5 Pro ");
            productDetailsArrayList.add("Samsung Galaxy C7 ");
            productDetailsArrayList.add("Samsung Galaxy C7 Pro ");
            productDetailsArrayList.add("Samsung Galaxy C8 ");
            productDetailsArrayList.add("Samsung Galaxy C9 Pro ");
            productDetailsArrayList.add("Samsung Galaxy Core i8262");
            productDetailsArrayList.add("Samsung Galaxy Core Prime G360 ");
            productDetailsArrayList.add("Samsung Galaxy Core 2 G355H");
            productDetailsArrayList.add("Samsung Galaxy E5 E500 ");
            productDetailsArrayList.add("Samsung Galaxy E7 E700 ");
            productDetailsArrayList.add("Samsung Galaxy 551 i5510");
            productDetailsArrayList.add("Samsung Galaxy Fame S6810");
            productDetailsArrayList.add("Samsung Galaxy Fit S5670");
            productDetailsArrayList.add("Samsung Galaxy Fold ");
            productDetailsArrayList.add("Samsung Galaxy Gio S5660");
            productDetailsArrayList.add("Samsung Galaxy Grand i9082");
            productDetailsArrayList.add("Samsung Galaxy Grand Max G720");
            productDetailsArrayList.add("Samsung Galaxy Grand Neo Plus i9060 ");
            productDetailsArrayList.add("Samsung Galaxy Grand Prime G530 ");
            productDetailsArrayList.add("Samsung Galaxy Grand Prime G532 ");
            productDetailsArrayList.add("Samsung Galaxy Grand Prime Plus ");
            productDetailsArrayList.add("Samsung Galaxy Grand Prime Pro ");
            productDetailsArrayList.add("Samsung Galaxy Grand 2 G710 ");
            productDetailsArrayList.add("Samsung Galaxy i7500");
            productDetailsArrayList.add("Samsung Galaxy J Max");
            productDetailsArrayList.add("Samsung Galaxy J1 Ace ");
            productDetailsArrayList.add("Samsung Galaxy J1 J100 ");
            productDetailsArrayList.add("Samsung Galaxy J1 J120 ");
            productDetailsArrayList.add("Samsung Galaxy J1 Mini Prime ");
            productDetailsArrayList.add("Samsung Galaxy J2 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy J2 (2017)");
            productDetailsArrayList.add("Samsung Galaxy J2 (2018) ");
            productDetailsArrayList.add("Samsung Galaxy J2 Core (2020) ");
            productDetailsArrayList.add("Samsung Galaxy J2 J200 ");
            productDetailsArrayList.add("Samsung Galaxy J2 Prime ");
            productDetailsArrayList.add("Samsung Galaxy J2 Pro (2016)");
            productDetailsArrayList.add("Samsung Galaxy J3 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy J3 (2017)");
            productDetailsArrayList.add("Samsung Galaxy J3 (2018)");
            productDetailsArrayList.add("Samsung Galaxy J3 Emerge");
            productDetailsArrayList.add("Samsung Galaxy J3 Pro ");
            productDetailsArrayList.add("Samsung Galaxy J4+ ");
            productDetailsArrayList.add("Samsung Galaxy J4 Core ");
            productDetailsArrayList.add("Samsung Galaxy J4 J400 ");
            productDetailsArrayList.add("Samsung Galaxy J5 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy J5 (2017)");
            productDetailsArrayList.add("Samsung Galaxy J5 J500 ");
            productDetailsArrayList.add("Samsung Galaxy J5 Prime G570 ");
            productDetailsArrayList.add("Samsung Galaxy J5 Pro ");
            productDetailsArrayList.add("Samsung Galaxy J6 ");
            productDetailsArrayList.add("Samsung Galaxy J6+ ");
            productDetailsArrayList.add("Samsung Galaxy J7 (2016) ");
            productDetailsArrayList.add("Samsung Galaxy J7 (2017) ");
            productDetailsArrayList.add("Samsung Galaxy J7 Core ");
            productDetailsArrayList.add("Samsung Galaxy J7 Duo ");
            productDetailsArrayList.add("Samsung Galaxy J7 J700 (");
            productDetailsArrayList.add("Samsung Galaxy J7 Max ");
            productDetailsArrayList.add("Samsung Galaxy J7 Prime ");
            productDetailsArrayList.add("Samsung Galaxy J7 Prime 2 ");
            productDetailsArrayList.add("Samsung Galaxy J7 Pro");
            productDetailsArrayList.add("Samsung Galaxy J8 J810 ");
            productDetailsArrayList.add("Samsung Galaxy K zoom C115");
            productDetailsArrayList.add("Samsung Galaxy M10 ");
            productDetailsArrayList.add("Samsung Galaxy M10s");
            productDetailsArrayList.add("Samsung Galaxy M11 ");
            productDetailsArrayList.add("Samsung Galaxy M12 ");
            productDetailsArrayList.add("Samsung Galaxy M13 ");
            productDetailsArrayList.add("Samsung Galaxy M14 ");
            productDetailsArrayList.add("Samsung Galaxy M20 ");
            productDetailsArrayList.add("Samsung Galaxy M21 ");
            productDetailsArrayList.add("Samsung Galaxy M22 ");
            productDetailsArrayList.add("Samsung Galaxy M23 ");
            productDetailsArrayList.add("Samsung Galaxy M30");
            productDetailsArrayList.add("Samsung Galaxy M30s ");
            productDetailsArrayList.add("Samsung Galaxy M31 ");
            productDetailsArrayList.add("Samsung Galaxy M31s ");
            productDetailsArrayList.add("Samsung Galaxy M32 ");
            productDetailsArrayList.add("Samsung Galaxy M33 5G ");
            productDetailsArrayList.add("Samsung Galaxy M40");
            productDetailsArrayList.add("Samsung Galaxy M51 ");
            productDetailsArrayList.add("Samsung Galaxy M52 ");
            productDetailsArrayList.add("Samsung Galaxy Mega 5.8 i9152");
            productDetailsArrayList.add("Samsung Galaxy Mega 6.3 i9200");
            productDetailsArrayList.add("Samsung Galaxy Mini S5570");
            productDetailsArrayList.add("Samsung Galaxy Mini 2 S6500");
            productDetailsArrayList.add("Samsung Galaxy Note Fan Edition ");
            productDetailsArrayList.add("Samsung Galaxy Note N7000");
            productDetailsArrayList.add("Samsung Galaxy Note Edge N915 ");
            productDetailsArrayList.add("Samsung Galaxy Note 2 N7100 ");
            productDetailsArrayList.add("Samsung Galaxy Note 3 N9000 ");
            productDetailsArrayList.add("Samsung Galaxy Note 3 N9000Q (");
            productDetailsArrayList.add("Samsung Galaxy Note 3 N9005");
            productDetailsArrayList.add("Samsung Galaxy Note 3 Neo N750 ");
            productDetailsArrayList.add("Samsung Galaxy Note 4 N910 ");
            productDetailsArrayList.add("Samsung Galaxy Note 5 N920 ");
            productDetailsArrayList.add("Samsung Galaxy Note 5 Duos");
            productDetailsArrayList.add("Samsung Galaxy Note 7 N930");
            productDetailsArrayList.add("Samsung Galaxy Note 8 N950 ");
            productDetailsArrayList.add("Samsung Galaxy Note 8 Duos");
            productDetailsArrayList.add("Samsung Galaxy Note 9 N960 ");
            productDetailsArrayList.add("Samsung Galaxy Note 10 ");
            productDetailsArrayList.add("Samsung Galaxy Note 10+ ");
            productDetailsArrayList.add("Samsung Galaxy Note 10+ 5G ");
            productDetailsArrayList.add("Samsung Galaxy Note10 Lite ");
            productDetailsArrayList.add("Samsung Galaxy Note 20 ");
            productDetailsArrayList.add("Samsung Galaxy Note 20 5G");
            productDetailsArrayList.add("Samsung Galaxy Note 20 Ultra ");
            productDetailsArrayList.add("Samsung Galaxy Note 20 Ultra 5G ");
            productDetailsArrayList.add("Samsung Galaxy On5");
            productDetailsArrayList.add("Samsung Galaxy On7 ");
            productDetailsArrayList.add("Samsung Galaxy On7 Prime");
            productDetailsArrayList.add("Samsung Galaxy Pocket S5300");
            productDetailsArrayList.add("Samsung Galaxy Pocket Plus S5301");
            productDetailsArrayList.add("Samsung Galaxy Pro B7510");
            productDetailsArrayList.add("Samsung Galaxy S Advance i9070");
            productDetailsArrayList.add("Samsung Galaxy S Duos S7562 ");
            productDetailsArrayList.add("Samsung Galaxy S i9000");
            productDetailsArrayList.add("Samsung Galaxy S Plus i9001");
            productDetailsArrayList.add("Samsung Galaxy S2 i9100");
            productDetailsArrayList.add("Samsung Galaxy S3 i9300 (");
            productDetailsArrayList.add("Samsung Galaxy S3 Mini i8190 (");
            productDetailsArrayList.add("Samsung Galaxy S3 Mini Value Edition i8200 ");
            productDetailsArrayList.add("Samsung Galaxy S3 Neo i9301 ");
            productDetailsArrayList.add("Samsung Galaxy S4 Active i9295");
            productDetailsArrayList.add("Samsung Galaxy S4 i9500 ");
            productDetailsArrayList.add("Samsung Galaxy S4 Mini i9190 ");
            productDetailsArrayList.add("Samsung Galaxy S4 Zoom C101");
            productDetailsArrayList.add("Samsung Galaxy S5 G900 ");
            productDetailsArrayList.add("Samsung Galaxy S5 mini G800F ");
            productDetailsArrayList.add("Samsung Galaxy S5 mini Duos G800");
            productDetailsArrayList.add("Samsung Galaxy S5 Neo G903");
            productDetailsArrayList.add("Samsung Galaxy S6 G920 ");
            productDetailsArrayList.add("Samsung Galaxy S6 Edge G925 ");
            productDetailsArrayList.add("Samsung Galaxy S6 edge+ G928 ");
            productDetailsArrayList.add("Samsung Galaxy S7 Active");
            productDetailsArrayList.add("Samsung Galaxy S7 G930 ");
            productDetailsArrayList.add("Samsung Galaxy S7 Edge G935 ");
            productDetailsArrayList.add("Samsung Galaxy S8+ G955 ");
            productDetailsArrayList.add("Samsung Galaxy S8 G950 ");
            productDetailsArrayList.add("Samsung Galaxy S9+ G965 ");
            productDetailsArrayList.add("Samsung Galaxy S9 G960 ");
            productDetailsArrayList.add("Samsung Galaxy S10 ");
            productDetailsArrayList.add("Samsung Galaxy S10 5G ");
            productDetailsArrayList.add("Samsung Galaxy S10e ");
            productDetailsArrayList.add("Samsung Galaxy S10 Lite ");
            productDetailsArrayList.add("Samsung Galaxy S10 Plus ");
            productDetailsArrayList.add("Samsung Galaxy S20 ");
            productDetailsArrayList.add("Samsung Galaxy S20+ ");
            productDetailsArrayList.add("Samsung Galaxy S20+ BTS Edition");
            productDetailsArrayList.add("Samsung Galaxy S20 FE ");
            productDetailsArrayList.add("Samsung Galaxy S20 Ultra ");
            productDetailsArrayList.add("Samsung Galaxy S21 ");
            productDetailsArrayList.add("Samsung Galaxy S21+ ");
            productDetailsArrayList.add("Samsung Galaxy S21 FE (");
            productDetailsArrayList.add("Samsung Galaxy S21 Ultra (");
            productDetailsArrayList.add("Samsung Galaxy S22 ");
            productDetailsArrayList.add("Samsung Galaxy S22+ ");
            productDetailsArrayList.add("Samsung Galaxy S22 Ultra (");
            productDetailsArrayList.add("Samsung Galaxy S23 ");
            productDetailsArrayList.add("Samsung Galaxy S23+ ");
            productDetailsArrayList.add("Samsung Galaxy S23 Ultra (");
            productDetailsArrayList.add("Samsung Galaxy Spica i5700");
            productDetailsArrayList.add("Samsung Galaxy Star S5280");
            productDetailsArrayList.add("Samsung Galaxy Trend S7560");
            productDetailsArrayList.add("Samsung Galaxy Trend Plus S7580 ");
            productDetailsArrayList.add("Samsung Galaxy Win i8552 ");
            productDetailsArrayList.add("Samsung Galaxy Wonder i8150 ");
            productDetailsArrayList.add("Samsung Galaxy Xcover 3 G388");
            productDetailsArrayList.add("Samsung Galaxy Xcover 4");
            productDetailsArrayList.add("Samsung Galaxy Xcover Pro");
            productDetailsArrayList.add("Samsung Galaxy Y S5360 ");
            productDetailsArrayList.add("Samsung Galaxy Y Duos S6102");
            productDetailsArrayList.add("Samsung Galaxy Y Pro B5510");
            productDetailsArrayList.add("Samsung Galaxy Young S6310");
            productDetailsArrayList.add("Samsung Galaxy Z Flip ");
            productDetailsArrayList.add("Samsung Galaxy Z Flip 3 ");
            productDetailsArrayList.add("Samsung Galaxy Z Flip 4 ");
            productDetailsArrayList.add("Samsung Galaxy Z Fold 2 ");
            productDetailsArrayList.add("Samsung Galaxy Z Fold 3 ");
            productDetailsArrayList.add("Samsung Galaxy Z Fold 4 ");
            productDetailsArrayList.add("Samsung 600");
            productDetailsArrayList.add("Samsung A837 Rugby");
            productDetailsArrayList.add("Samsung B130");
            productDetailsArrayList.add("Samsung B210");
            productDetailsArrayList.add("Samsung B2700");
            productDetailsArrayList.add("Samsung B300");
            productDetailsArrayList.add("Samsung B310 ");
            productDetailsArrayList.add("Samsung B312 Metro");
            productDetailsArrayList.add("Samsung B3210 CorbyTXT");
            productDetailsArrayList.add("Samsung B3310");
            productDetailsArrayList.add("Samsung B3410 Corby Plus");
            productDetailsArrayList.add("Samsung B350E ");
            productDetailsArrayList.add("Samsung B5310 Corby Pro");
            productDetailsArrayList.add("Samsung B5702");
            productDetailsArrayList.add("Samsung B5722");
            productDetailsArrayList.add("Samsung B7300 Omnia Lite");
            productDetailsArrayList.add("Samsung B7320 OmniaPRO");
            productDetailsArrayList.add("Samsung B7722");
            productDetailsArrayList.add("Samsung C100");
            productDetailsArrayList.add("Samsung C260 ");
            productDetailsArrayList.add("Samsung C270");
            productDetailsArrayList.add("Samsung C300");
            productDetailsArrayList.add("Samsung C3010");
            productDetailsArrayList.add("Samsung C3010S");
            productDetailsArrayList.add("Samsung C3011");
            productDetailsArrayList.add("Samsung C3050");
            productDetailsArrayList.add("Samsung C3053");
            productDetailsArrayList.add("Samsung C3200 Monte Bar");
            productDetailsArrayList.add("Samsung C3212");
            productDetailsArrayList.add("Samsung C322 Chat");
            productDetailsArrayList.add("Samsung C3300 Champ");
            productDetailsArrayList.add("Samsung C3303K Champ");
            productDetailsArrayList.add("Samsung C3310 Champ Deluxe");
            productDetailsArrayList.add("Samsung C3310R Rex 60");
            productDetailsArrayList.add("Samsung C3322 Metro Duos");
            productDetailsArrayList.add("Samsung C3500");
            productDetailsArrayList.add("Samsung C3510 Genoa ");
            productDetailsArrayList.add("Samsung C3520");
            productDetailsArrayList.add("Samsung C3530 ");
            productDetailsArrayList.add("Samsung C3560");
            productDetailsArrayList.add("Samsung C3780");
            productDetailsArrayList.add("Samsung C450");
            productDetailsArrayList.add("Samsung C5130");
            productDetailsArrayList.add("Samsung C5212");
            productDetailsArrayList.add("Samsung C5510");
            productDetailsArrayList.add("Samsung C6112");
            productDetailsArrayList.add("Samsung C6712 Star II Duos");
            productDetailsArrayList.add("Samsung D500");
            productDetailsArrayList.add("Samsung D600");
            productDetailsArrayList.add("Samsung D720");
            productDetailsArrayList.add("Samsung D780");
            productDetailsArrayList.add("Samsung D800");
            productDetailsArrayList.add("Samsung D880");
            productDetailsArrayList.add("Samsung D900");
            productDetailsArrayList.add("Samsung D900e");
            productDetailsArrayList.add("Samsung D900i");
            productDetailsArrayList.add("Samsung D980");
            productDetailsArrayList.add("Samsung E1050 ");
            productDetailsArrayList.add("Samsung E1075");
            productDetailsArrayList.add("Samsung E1080T");
            productDetailsArrayList.add("Samsung E1081T");
            productDetailsArrayList.add("Samsung E1085");
            productDetailsArrayList.add("Samsung E1107 Crest Solar");
            productDetailsArrayList.add("Samsung E1150 ");
            productDetailsArrayList.add("Samsung E1180");
            productDetailsArrayList.add("Samsung E1182");
            productDetailsArrayList.add("Samsung E1190 ");
            productDetailsArrayList.add("Samsung E1200i");
            productDetailsArrayList.add("Samsung E1205 ");
            productDetailsArrayList.add("Samsung E1207");
            productDetailsArrayList.add("Samsung E1210");
            productDetailsArrayList.add("Samsung E1270 ");
            productDetailsArrayList.add("Samsung E1310");
            productDetailsArrayList.add("Samsung E2121 ");
            productDetailsArrayList.add("Samsung E2152");
            productDetailsArrayList.add("Samsung E2200");
            productDetailsArrayList.add("Samsung E2202");
            productDetailsArrayList.add("Samsung E2220");
            productDetailsArrayList.add("Samsung E2250 ");
            productDetailsArrayList.add("Samsung E2330");
            productDetailsArrayList.add("Samsung E250 ");
            productDetailsArrayList.add("Samsung E251");
            productDetailsArrayList.add("Samsung E2550 Monte Slider");
            productDetailsArrayList.add("Samsung E2652W Champ Duos");
            productDetailsArrayList.add("Samsung E3210");
            productDetailsArrayList.add("Samsung E390");
            productDetailsArrayList.add("Samsung E490");
            productDetailsArrayList.add("Samsung E730");
            productDetailsArrayList.add("Samsung E740");
            productDetailsArrayList.add("Samsung E840");
            productDetailsArrayList.add("Samsung E900");
            productDetailsArrayList.add("Samsung F250");
            productDetailsArrayList.add("Samsung F300");
            productDetailsArrayList.add("Samsung F330");
            productDetailsArrayList.add("Samsung F400");
            productDetailsArrayList.add("Samsung F480i");
            productDetailsArrayList.add("Samsung G600");
            productDetailsArrayList.add("Samsung G810");
            productDetailsArrayList.add("Samsung i5500 Corby");
            productDetailsArrayList.add("Samsung i560");
            productDetailsArrayList.add("Samsung i700");
            productDetailsArrayList.add("Samsung i8000 Omnia II");
            productDetailsArrayList.add("Samsung i8510 innov8");
            productDetailsArrayList.add("Samsung i8750 Ativ S");
            productDetailsArrayList.add("Samsung i8910 Omnia HD");
            productDetailsArrayList.add("Samsung i900 Omnia");
            productDetailsArrayList.add("Samsung J150");
            productDetailsArrayList.add("Samsung J750");
            productDetailsArrayList.add("Samsung L600");
            productDetailsArrayList.add("Samsung L700 ");
            productDetailsArrayList.add("Samsung L700i");
            productDetailsArrayList.add("Samsung L760");
            productDetailsArrayList.add("Samsung L770");
            productDetailsArrayList.add("Samsung L811");
            productDetailsArrayList.add("Samsung M100");
            productDetailsArrayList.add("Samsung M140");
            productDetailsArrayList.add("Samsung M150");
            productDetailsArrayList.add("Samsung M300");
            productDetailsArrayList.add("Samsung M3200 Beat S");
            productDetailsArrayList.add("Samsung M5650 Lindy");
            productDetailsArrayList.add("Samsung M600");
            productDetailsArrayList.add("Samsung M620");
            productDetailsArrayList.add("Samsung M7600 Beat Dj");
            productDetailsArrayList.add("Samsung M7603 Beat Dj");
            productDetailsArrayList.add("Samsung M8800");
            productDetailsArrayList.add("Samsung M8910 Pixon 12");
            productDetailsArrayList.add("Samsung N400");
            productDetailsArrayList.add("Samsung N500DA");
            productDetailsArrayList.add("Samsung N710");
            productDetailsArrayList.add("Samsung P300");
            productDetailsArrayList.add("Samsung P310");
            productDetailsArrayList.add("Samsung P520 Giorgio Armani");
            productDetailsArrayList.add("Samsung P850");
            productDetailsArrayList.add("Samsung R220");
            productDetailsArrayList.add("Samsung S3350 Chat");
            productDetailsArrayList.add("Samsung S3353 Trevi ");
            productDetailsArrayList.add("Samsung S3500");
            productDetailsArrayList.add("Samsung S3570 Chat");
            productDetailsArrayList.add("Samsung S3600 ");
            productDetailsArrayList.add("Samsung S3653 Corby");
            productDetailsArrayList.add("Samsung S3653W Corby Wi-Fi");
            productDetailsArrayList.add("Samsung S3770 Champ 3.5G");
            productDetailsArrayList.add("Samsung S3850 Corby II ");
            productDetailsArrayList.add("Samsung S501i");
            productDetailsArrayList.add("Samsung S5200");
            productDetailsArrayList.add("Samsung S5220 Star 3");
            productDetailsArrayList.add("Samsung S5233");
            productDetailsArrayList.add("Samsung S5233T");
            productDetailsArrayList.add("Samsung S5233W");
            productDetailsArrayList.add("Samsung S5250 Wave 2");
            productDetailsArrayList.add("Samsung S5253 Wave");
            productDetailsArrayList.add("Samsung S5260 Star II");
            productDetailsArrayList.add("Samsung S5270 Chat");
            productDetailsArrayList.add("Samsung S5320");
            productDetailsArrayList.add("Samsung S5330 Wave 2 Pro");
            productDetailsArrayList.add("Samsung S5350 Shark");
            productDetailsArrayList.add("Samsung S5503");
            productDetailsArrayList.add("Samsung S5510");
            productDetailsArrayList.add("Samsung S5603");
            productDetailsArrayList.add("Samsung S5610K ");
            productDetailsArrayList.add("Samsung S5620 Monte ");
            productDetailsArrayList.add("Samsung S7233E Wave");
            productDetailsArrayList.add("Samsung S7550 Blue Earth");
            productDetailsArrayList.add("Samsung S8000 Jet");
            productDetailsArrayList.add("Samsung S8003");
            productDetailsArrayList.add("Samsung S8300 UltraTouch");
            productDetailsArrayList.add("Samsung S8500 Wave");
            productDetailsArrayList.add("Samsung S8530 Wave 2");
            productDetailsArrayList.add("Samsung S8600 Wave 3");
            productDetailsArrayList.add("Samsung T109");
            productDetailsArrayList.add("Samsung T500");
            productDetailsArrayList.add("Samsung T519");
            productDetailsArrayList.add("Samsung U600");
            productDetailsArrayList.add("Samsung U700");
            productDetailsArrayList.add("Samsung U800");
            productDetailsArrayList.add("Samsung U900 Soul");
            productDetailsArrayList.add("Samsung V200");
            productDetailsArrayList.add("Samsung X150");
            productDetailsArrayList.add("Samsung X160");
            productDetailsArrayList.add("Samsung X200");
            productDetailsArrayList.add("Samsung X210");
            productDetailsArrayList.add("Samsung X300");
            productDetailsArrayList.add("Samsung X430");
            productDetailsArrayList.add("Samsung X510");
            productDetailsArrayList.add("Samsung X540");
            productDetailsArrayList.add("Samsung X600");
            productDetailsArrayList.add("Samsung X640");
            productDetailsArrayList.add("Samsung X700");
            productDetailsArrayList.add("Samsung Xcover 550");
            productDetailsArrayList.add("Samsung Z400");
            productDetailsArrayList.add("Samsung Z500");
            productDetailsArrayList.add("Samsung 3053");
            productDetailsArrayList.add("Sony Xperia 1");
            productDetailsArrayList.add("Sony Xperia 10 II");
            productDetailsArrayList.add("Sony Xperia 10 III");
            productDetailsArrayList.add("Sony Xperia 1 II");
            productDetailsArrayList.add("Sony Xperia 1 III");
            productDetailsArrayList.add("Sony Xperia 1 IV");
            productDetailsArrayList.add("Sony Xperia 1 V");
            productDetailsArrayList.add("Sony Xperia 5 II");
            productDetailsArrayList.add("Sony Xperia 5 III");
            productDetailsArrayList.add("Sony Xperia 5 IV");
            productDetailsArrayList.add("Sony Xperia C3");
            productDetailsArrayList.add("Sony Xperia C4");
            productDetailsArrayList.add("Sony Xperia C5 Ultra ");
            productDetailsArrayList.add("Sony Xperia E");
            productDetailsArrayList.add("Sony Xperia E3");
            productDetailsArrayList.add("Sony Xperia E4");
            productDetailsArrayList.add("Sony Xperia E4 Dual");
            productDetailsArrayList.add("Sony Xperia E4 G");
            productDetailsArrayList.add("Sony Xperia E5");
            productDetailsArrayList.add("Sony Xperia ion HSPA");
            productDetailsArrayList.add("Sony Xperia J");
            productDetailsArrayList.add("Sony Xperia L");
            productDetailsArrayList.add("Sony Xperia L1 ");
            productDetailsArrayList.add("Sony Xperia M");
            productDetailsArrayList.add("Sony Xperia M2");
            productDetailsArrayList.add("Sony Xperia M2 Dual");
            productDetailsArrayList.add("Sony Xperia M4 Aqua ");
            productDetailsArrayList.add("Sony Xperia M5 ");
            productDetailsArrayList.add("Sony Xperia Miro");
            productDetailsArrayList.add("Sony Xperia Neo L");
            productDetailsArrayList.add("Sony Xperia P");
            productDetailsArrayList.add("Sony Xperia Pro-I");
            productDetailsArrayList.add("Sony Xperia S");
            productDetailsArrayList.add("Sony Xperia Sola");
            productDetailsArrayList.add("Sony Xperia SP");
            productDetailsArrayList.add("Sony Xperia T2 Ultra");
            productDetailsArrayList.add("Sony Xperia T3");
            productDetailsArrayList.add("Sony Xperia Tipo");
            productDetailsArrayList.add("Sony Xperia U");
            productDetailsArrayList.add("Sony Xperia X");
            productDetailsArrayList.add("Sony Xperia XA ");
            productDetailsArrayList.add("Sony Xperia XA1 ");
            productDetailsArrayList.add("Sony Xperia XA1 Plus");
            productDetailsArrayList.add("Sony Xperia XA1 Ultra ");
            productDetailsArrayList.add("Sony Xperia XA2 Ultra");
            productDetailsArrayList.add("Sony Xperia XA Ultra ");
            productDetailsArrayList.add("Sony Xperia X Compact");
            productDetailsArrayList.add("Sony Xperia XZ");
            productDetailsArrayList.add("Sony Xperia XZ1 ");
            productDetailsArrayList.add("Sony Xperia XZ3");
            productDetailsArrayList.add("Sony Xperia XZ Premium");
            productDetailsArrayList.add("Sony Xperia Z ");
            productDetailsArrayList.add("Sony Xperia Z1 ");
            productDetailsArrayList.add("Sony Xperia Z2 ");
            productDetailsArrayList.add("Sony Xperia Z3 ");
            productDetailsArrayList.add("Sony Xperia Z3 Compact");
            productDetailsArrayList.add("Sony Xperia Z3 Plus");
            productDetailsArrayList.add("Sony Xperia Z5 ");
            productDetailsArrayList.add("Sony Xperia Z5 Dual");
            productDetailsArrayList.add("Sony Xperia Z5 Premium");
            productDetailsArrayList.add("Tecno Camon 16");
            productDetailsArrayList.add("Tecno Camon 17 Pr");
            productDetailsArrayList.add("Tecno Camon 18 ");
            productDetailsArrayList.add("Tecno Camon 18P");
            productDetailsArrayList.add("Tecno Camon 18 Premier");
            productDetailsArrayList.add("Tecno Camon 19 Neo");
            productDetailsArrayList.add("Tecno Camon 19 Pro");
            productDetailsArrayList.add("Tecno Phantom V Fol");
            productDetailsArrayList.add("Tecno Phantom ");
            productDetailsArrayList.add("Tecno Phantom X2");
            productDetailsArrayList.add("Tecno Phantom X2 Pro");
            productDetailsArrayList.add("Tecno Pova ");
            productDetailsArrayList.add("Tecno Pova 3");
            productDetailsArrayList.add("Tecno Pova 4");
            productDetailsArrayList.add("Tecno Pova Neo 2");
            productDetailsArrayList.add("Tecno Spark 6");
            productDetailsArrayList.add("Tecno Spark 6 Go");
            productDetailsArrayList.add("Tecno Spark 7 Pro");
            productDetailsArrayList.add("Tecno Spark 7T");
            productDetailsArrayList.add("Tecno Spark ");
            productDetailsArrayList.add("Tecno Spark 8C ");
            productDetailsArrayList.add("Tecno Spark 8P");
            productDetailsArrayList.add("Tecno Spark 8T");
            productDetailsArrayList.add("Tecno Spark 9 Pro");
            productDetailsArrayList.add("Tecno Spark 10 P");
            productDetailsArrayList.add("Vestel Venus 4.5");
            productDetailsArrayList.add("Vestel Venus 5.0 ");
            productDetailsArrayList.add("Vestel Venus 5.5");
            productDetailsArrayList.add("Vestel Venus 5000 ");
            productDetailsArrayList.add("Vestel Venus E2");
            productDetailsArrayList.add("Vestel Venus E3 ");
            productDetailsArrayList.add("Vestel Venus E4 ");
            productDetailsArrayList.add("Vestel Venus E5 ");
            productDetailsArrayList.add("Vestel Venus GO ");
            productDetailsArrayList.add("Vestel Venus V3 5010 ");
            productDetailsArrayList.add("Vestel Venus V3 5020");
            productDetailsArrayList.add("Vestel Venus V3 5040  ");
            productDetailsArrayList.add("Vestel Venus V3 5070 ");
            productDetailsArrayList.add("Vestel Venus V3 5570  ");
            productDetailsArrayList.add("Vestel Venus V3 5580  ");
            productDetailsArrayList.add("Vestel Venus V4  ");
            productDetailsArrayList.add("Vestel Venus V5");
            productDetailsArrayList.add("Vestel Venus V6 ");
            productDetailsArrayList.add("Vestel Venus V7 ");
            productDetailsArrayList.add("Vestel Venus Z10 ");
            productDetailsArrayList.add("Vestel Venus Z20 ");
            productDetailsArrayList.add("Vestel Venus Z30");
            productDetailsArrayList.add("Vestel Venus Z40 ");
            productDetailsArrayList.add("Vestel Venüs 5530");
            productDetailsArrayList.add("Xiaomi 12 ");
            productDetailsArrayList.add("Xiaomi 12 Lite ");
            productDetailsArrayList.add("Xiaomi 12 Pro ");
            productDetailsArrayList.add("Xiaomi 12S Pro");
            productDetailsArrayList.add("Xiaomi 12S Ultra ");
            productDetailsArrayList.add("Xiaomi 12T ");
            productDetailsArrayList.add("Xiaomi 12T Pro ");
            productDetailsArrayList.add("Xiaomi 12X");
            productDetailsArrayList.add("Xiaomi 13 ");
            productDetailsArrayList.add("Xiaomi 13 Lite ");
            productDetailsArrayList.add("Xiaomi 13 Pro ");
            productDetailsArrayList.add("Xiaomi 13 Ultra ");
            productDetailsArrayList.add("Xiaomi Black Shar");
            productDetailsArrayList.add("Xiaomi Black Shark ");
            productDetailsArrayList.add("Xiaomi Black Shark 2 Pro");
            productDetailsArrayList.add("Xiaomi Black Shark ");
            productDetailsArrayList.add("Xiaomi Black Shark ");
            productDetailsArrayList.add("Xiaomi Black Shark 4 Pro");
            productDetailsArrayList.add("Xiaomi Black Shark 4S Pro");
            productDetailsArrayList.add("Xiaomi Black Shark 5 Pro");
            productDetailsArrayList.add("Xiaomi Mi 1");
            productDetailsArrayList.add("Xiaomi Mi 2");
            productDetailsArrayList.add("Xiaomi Mi 3");
            productDetailsArrayList.add("Xiaomi Mi 4");
            productDetailsArrayList.add("Xiaomi Mi 5");
            productDetailsArrayList.add("Xiaomi Mi 5s");
            productDetailsArrayList.add("Xiaomi Mi 5s Plus");
            productDetailsArrayList.add("Xiaomi Mi 6");
            productDetailsArrayList.add("Xiaomi Mi 8");
            productDetailsArrayList.add("Xiaomi Mi 8 Explore");
            productDetailsArrayList.add("Xiaomi Mi 8 Lite ");
            productDetailsArrayList.add("Xiaomi Mi 8 Pro ");
            productDetailsArrayList.add("Xiaomi Mi 8 SE");
            productDetailsArrayList.add("Xiaomi Mi 9");
            productDetailsArrayList.add("Xiaomi Mi 9 Explore");
            productDetailsArrayList.add("Xiaomi Mi 9 Lite");
            productDetailsArrayList.add("Xiaomi Mi 9 Pr");
            productDetailsArrayList.add("Xiaomi Mi 9 SE");
            productDetailsArrayList.add("Xiaomi Mi 9T ");
            productDetailsArrayList.add("Xiaomi Mi 9T Pro");
            productDetailsArrayList.add("Xiaomi Mi 10");
            productDetailsArrayList.add("Xiaomi Mi 10i 5G");
            productDetailsArrayList.add("Xiaomi Mi 10 Lite");
            productDetailsArrayList.add("Xiaomi Mi 10 Pro");
            productDetailsArrayList.add("Xiaomi Mi 10S");
            productDetailsArrayList.add("Xiaomi Mi 10 T ");
            productDetailsArrayList.add("Xiaomi Mi 10 T Lite");
            productDetailsArrayList.add("Xiaomi Mi 10 T Pro ");
            productDetailsArrayList.add("Xiaomi Mi 10 Ultra");
            productDetailsArrayList.add("Xiaomi Mi 10 Yout");
            productDetailsArrayList.add("Xiaomi Mi 11 ");
            productDetailsArrayList.add("Xiaomi Mi 11");
            productDetailsArrayList.add("Xiaomi Mi 11 Lite ");
            productDetailsArrayList.add("Xiaomi Mi 11 Lite 5G");
            productDetailsArrayList.add("Xiaomi Mi 11 Lite 5G NE");
            productDetailsArrayList.add("Xiaomi Mi 11 Pro");
            productDetailsArrayList.add("Xiaomi Mi 11T ");
            productDetailsArrayList.add("Xiaomi Mi 11T Pro ");
            productDetailsArrayList.add("Xiaomi Mi 11 Ultra ");
            productDetailsArrayList.add("Xiaomi Mi 11X Pr");
            productDetailsArrayList.add("Xiaomi Mi A1 (5X)");
            productDetailsArrayList.add("Xiaomi Mi A2 (6X)");
            productDetailsArrayList.add("Xiaomi Mi A2 Lite (Redmi 6 Pro)");
            productDetailsArrayList.add("Xiaomi Mi A3");
            productDetailsArrayList.add("Xiaomi Mi Ma");
            productDetailsArrayList.add("Xiaomi Mi Max 2");
            productDetailsArrayList.add("Xiaomi Mi Max 3");
            productDetailsArrayList.add("Xiaomi Mi Mix 3");
            productDetailsArrayList.add("Xiaomi Mi Mix 2");
            productDetailsArrayList.add("Xiaomi Mi Mix 2");
            productDetailsArrayList.add("Xiaomi Mi Mix 3");
            productDetailsArrayList.add("Xiaomi Mi Mix ");
            productDetailsArrayList.add("Xiaomi Mi Mix Fol");
            productDetailsArrayList.add("Xiaomi Mi Mix Fold 2");
            productDetailsArrayList.add("Xiaomi Mi Note 3");
            productDetailsArrayList.add("Xiaomi Mi Note Pr");
            productDetailsArrayList.add("Xiaomi Mi Note 10 ");
            productDetailsArrayList.add("Xiaomi Mi Note 10 Lite ");
            productDetailsArrayList.add("Xiaomi Mi Note 10 Pro");
            productDetailsArrayList.add("Xiaomi Mi Pla");
            productDetailsArrayList.add("Xiaomi Poco C40 ");
            productDetailsArrayList.add("Xiaomi Poco C5");
            productDetailsArrayList.add("Xiaomi Poco F2 Pro");
            productDetailsArrayList.add("Xiaomi Poco F3");
            productDetailsArrayList.add("Xiaomi Poco F4");
            productDetailsArrayList.add("Xiaomi Poco F4 GT");
            productDetailsArrayList.add("Xiaomi Poco F5");
            productDetailsArrayList.add("Xiaomi Poco F5 Pro");
            productDetailsArrayList.add("Xiaomi Poco M3 ");
            productDetailsArrayList.add("Xiaomi Poco M3 Pro");
            productDetailsArrayList.add("Xiaomi Poco M4 Pro ");
            productDetailsArrayList.add("Xiaomi Poco M5");
            productDetailsArrayList.add("Xiaomi Pocophone F1");
            productDetailsArrayList.add("Xiaomi Poco X2");
            productDetailsArrayList.add("Xiaomi Poco X3 ");
            productDetailsArrayList.add("Xiaomi Poco X3 GT");
            productDetailsArrayList.add("Xiaomi Poco X3 Pro (1");
            productDetailsArrayList.add("Xiaomi Poco X4 GT");
            productDetailsArrayList.add("Xiaomi Poco X4 Pro 5G ");
            productDetailsArrayList.add("Xiaomi Poco X5");
            productDetailsArrayList.add("Xiaomi Poco X5 Pro ");
            productDetailsArrayList.add("Xiaomi Qin 1s");
            productDetailsArrayList.add("Xiaomi Redmi ");
            productDetailsArrayList.add("Xiaomi Redmi ");
            productDetailsArrayList.add("Xiaomi Redmi 3 Pr");
            productDetailsArrayList.add("Xiaomi Redmi 3");
            productDetailsArrayList.add("Xiaomi Redmi ");
            productDetailsArrayList.add("Xiaomi Redmi 4a");
            productDetailsArrayList.add("Xiaomi Redmi 4 Prim");
            productDetailsArrayList.add("Xiaomi Redmi 4");
            productDetailsArrayList.add("Xiaomi Redmi ");
            productDetailsArrayList.add("Xiaomi Redmi 5");
            productDetailsArrayList.add("Xiaomi Redmi 5 Plus");
            productDetailsArrayList.add("Xiaomi Redmi 6");
            productDetailsArrayList.add("Xiaomi Redmi 6A");
            productDetailsArrayList.add("Xiaomi Redmi 7");
            productDetailsArrayList.add("Xiaomi Redmi 7A");
            productDetailsArrayList.add("Xiaomi Redmi 8 ");
            productDetailsArrayList.add("Xiaomi Redmi 8A ");
            productDetailsArrayList.add("Xiaomi Redmi 9 ");
            productDetailsArrayList.add("Xiaomi Redmi 9A ");
            productDetailsArrayList.add("Xiaomi Redmi 9C ");
            productDetailsArrayList.add("Xiaomi Redmi 9 Prim");
            productDetailsArrayList.add("Xiaomi Redmi 9T ");
            productDetailsArrayList.add("Xiaomi Redmi 10 ");
            productDetailsArrayList.add("Xiaomi Redmi 10 2022 ");
            productDetailsArrayList.add("Xiaomi Redmi 10A ");
            productDetailsArrayList.add("Xiaomi Redmi 10C ");
            productDetailsArrayList.add("Xiaomi Redmi 10");
            productDetailsArrayList.add("Xiaomi Redmi 10X Pr");
            productDetailsArrayList.add("Xiaomi Redmi 12C ");
            productDetailsArrayList.add("Xiaomi Redmi Go");
            productDetailsArrayList.add("Xiaomi Redmi K2");
            productDetailsArrayList.add("Xiaomi Redmi K20 Pro");
            productDetailsArrayList.add("Xiaomi Redmi K30");
            productDetailsArrayList.add("Xiaomi Redmi K30 Pr");
            productDetailsArrayList.add("Xiaomi Redmi K4");
            productDetailsArrayList.add("Xiaomi Redmi K40 Gaming ");
            productDetailsArrayList.add("Xiaomi Redmi K50");
            productDetailsArrayList.add("Xiaomi Redmi K50 Pr");
            productDetailsArrayList.add("Xiaomi Redmi K50 Gaming");
            productDetailsArrayList.add("Xiaomi Redmi K50 Ultra");
            productDetailsArrayList.add("Xiaomi Redmi K60");
            productDetailsArrayList.add("Xiaomi Redmi K60 Pro");
            productDetailsArrayList.add("Xiaomi Redmi Note ");
            productDetailsArrayList.add("Xiaomi Redmi Note 3 (Pro");
            productDetailsArrayList.add("Xiaomi Redmi Note 4");
            productDetailsArrayList.add("Xiaomi Redmi Note 4");
            productDetailsArrayList.add("Xiaomi Redmi Note 5");
            productDetailsArrayList.add("Xiaomi Redmi Note 5A Prim");
            productDetailsArrayList.add("Xiaomi Redmi Note 5 Pro");
            productDetailsArrayList.add("Xiaomi Redmi Note 6 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 7 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 7 Pr");
            productDetailsArrayList.add("Xiaomi Redmi Note 8 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 8 2021");
            productDetailsArrayList.add("Xiaomi Redmi Note 8 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 8");
            productDetailsArrayList.add("Xiaomi Redmi Note 9 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 9 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 9 Pro Max");
            productDetailsArrayList.add("Xiaomi Redmi Note 9S ");
            productDetailsArrayList.add("Xiaomi Redmi Note 9T");
            productDetailsArrayList.add("Xiaomi Redmi Note 10 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 10 5G ");
            productDetailsArrayList.add("Xiaomi Redmi Note 10 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 10 Pro Max");
            productDetailsArrayList.add("Xiaomi Redmi Note 10S ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11E ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11 Pro Plus ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11S ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11SE ");
            productDetailsArrayList.add("Xiaomi Redmi Note 11T Pr");
            productDetailsArrayList.add("Xiaomi Redmi Note 12 ");
            productDetailsArrayList.add("Xiaomi Redmi Note 12 Explore");
            productDetailsArrayList.add("Xiaomi Redmi Note 12 Pro ");
            productDetailsArrayList.add("Xiaomi Redmi Note 12 Pro Plus ");
            productDetailsArrayList.add("Xiaomi Redmi Note 12S ");
            productDetailsArrayList.add("Xiaomi Redmi Note 12 Turbo");
            productDetailsArrayList.add("Xiaomi Redmi Pr");
            productDetailsArrayList.add("Xiaomi Redmi S2");


            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                    if (dil.equals("türkce")){
                        productDetailsArrayList.add("Diğer");
                    }else{
                        productDetailsArrayList.add("Other");
                    }
                }
            });








        }

        else if(parcaAdi.equals("Televizyon") || parcaAdi.equals("Television")){


            productDetailsArrayList.add("LG CX ");
            productDetailsArrayList.add("LG GX ");
            productDetailsArrayList.add("LG GX ");
            productDetailsArrayList.add("LG GX ");
            productDetailsArrayList.add("Samsung Q90T ");
            productDetailsArrayList.add("Samsung Q800T ");
            productDetailsArrayList.add("Samsung Q70T  ");
            productDetailsArrayList.add("Samsung TU8500");
            productDetailsArrayList.add("Sony A8H OLED");
            productDetailsArrayList.add("Sony X950H");
            productDetailsArrayList.add("Sony Bravia");
            productDetailsArrayList.add("Sony A9G OLED");
            productDetailsArrayList.add("Panasonic HZ2000");
            productDetailsArrayList.add("Panasonic GX800");
            productDetailsArrayList.add("Panasonic HX940");
            productDetailsArrayList.add("TCL 6-Series");
            productDetailsArrayList.add("TCL 5-Series");
            productDetailsArrayList.add("TCL 8-Series");
            productDetailsArrayList.add("Philips 9006 OLED TV");
            productDetailsArrayList.add("Philips 8804 OLED TV");
            productDetailsArrayList.add("Philips 7504 LED TV");
            productDetailsArrayList.add("Toshiba 55QA");
            productDetailsArrayList.add("Toshiba 32W");
            productDetailsArrayList.add("Toshiba Z870");
            productDetailsArrayList.add("Vestel 43UA");
            productDetailsArrayList.add("Vestel 43UA");

            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                    if (dil.equals("türkce")){
                        productDetailsArrayList.add("Diğer");
                    }else{
                        productDetailsArrayList.add("Other");
                    }
                }
            });


        }
        else if(parcaAdi.equals("Oyun Konsolu") || parcaAdi.equals("Game Console")){

            productDetailsArrayList.add("Nintendo Switch ");
            productDetailsArrayList.add("Nintendo Wii ");
            productDetailsArrayList.add("Nintendo Wii U");
            productDetailsArrayList.add("Nintendo DS");
            productDetailsArrayList.add("Nintendo 2DS & 3DS");
            productDetailsArrayList.add("Nintendo Gameboy");
            productDetailsArrayList.add("Nintendo Game Cube");
            productDetailsArrayList.add("PlayStation 1 ");
            productDetailsArrayList.add("PlayStation 2 ");
            productDetailsArrayList.add("PlayStation 3 ");
            productDetailsArrayList.add("PlayStation 4 ");
            productDetailsArrayList.add("PlayStation 4 Pro ");
            productDetailsArrayList.add("PlayStation 5 ");
            productDetailsArrayList.add("Xbox");
            productDetailsArrayList.add("Xbox 360 ");
            productDetailsArrayList.add("Xbox One ");
            productDetailsArrayList.add("Xbox One S ");
            productDetailsArrayList.add("Xbox One X ");
            productDetailsArrayList.add("Xbox Series S ");
            productDetailsArrayList.add("Xbox Series X ");
            productDetailsArrayList.add("PSP ");
            productDetailsArrayList.add("PS Vita ");
            productDetailsArrayList.add("Steam Deck ");
            productDetailsArrayList.add("Asus Rog Ally");
            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                    if (dil.equals("türkce")){
                        productDetailsArrayList.add("Diğer");
                    }else{
                        productDetailsArrayList.add("Other");
                    }
                }
            });






        }

        else if(parcaAdi.equals("Vasıta(Otomobil-Suv-Motosiklet)") || parcaAdi.equals("Vehicle (Car-Suv-Motorcycle)")){


        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if (dil.equals("türkce")){
                    productDetailsArrayList.add("Alfa Romeo Giulia");
                    productDetailsArrayList.add("Alfa Romeo Giulia Quadrifoglio");
                    productDetailsArrayList.add("Alfa Romeo Giulietta ");
                    productDetailsArrayList.add("Alfa Romeo 145 ");
                    productDetailsArrayList.add("Alfa Romeo 146 ");
                    productDetailsArrayList.add("Alfa Romeo 147 ");
                    productDetailsArrayList.add("Alfa Romeo 155");
                    productDetailsArrayList.add("Alfa Romeo 156 ");
                    productDetailsArrayList.add("Alfa Romeo 159 ");
                    productDetailsArrayList.add("Alfa Romeo 164");
                    productDetailsArrayList.add("Alfa Romeo 166 ");
                    productDetailsArrayList.add("Alfa Romeo 33");
                    productDetailsArrayList.add("Alfa Romeo 75");
                    productDetailsArrayList.add("Alfa Romeo Brera");
                    productDetailsArrayList.add("Alfa Romeo GT");
                    productDetailsArrayList.add("Alfa Romeo GTV");
                    productDetailsArrayList.add("Alfa Romeo MiTo ");
                    productDetailsArrayList.add("Aston Martin DB11");
                    productDetailsArrayList.add("Aston Martin DB7 ");
                    productDetailsArrayList.add("Aston Martin DB9 ");
                    productDetailsArrayList.add("Aston Martin DBS ");
                    productDetailsArrayList.add("Aston Martin Rapide ");
                    productDetailsArrayList.add("Aston Martin Vanquish ");
                    productDetailsArrayList.add("Aston Martin Vantage ");
                    productDetailsArrayList.add("Aston Martin Virage ");
                    productDetailsArrayList.add("Audi A1 ");
                    productDetailsArrayList.add("Audi A2");
                    productDetailsArrayList.add("Audi A3");
                    productDetailsArrayList.add("Audi A4");
                    productDetailsArrayList.add("Audi A5");
                    productDetailsArrayList.add("Audi A6");
                    productDetailsArrayList.add("Audi A7 ");
                    productDetailsArrayList.add("Audi A8 ");
                    productDetailsArrayList.add("Audi E-Tron GT ");
                    productDetailsArrayList.add("Audi R8");
                    productDetailsArrayList.add("Audi RS ");
                    productDetailsArrayList.add("Audi S  Series ");
                    productDetailsArrayList.add("Audi TT ");
                    productDetailsArrayList.add("Audi TTS ");
                    productDetailsArrayList.add("Audi 80  Series ");
                    productDetailsArrayList.add("Audi 90  Series");
                    productDetailsArrayList.add("Audi 100 Series ");
                    productDetailsArrayList.add("Audi 200 Series");
                    productDetailsArrayList.add("Audi Cabrio");
                    productDetailsArrayList.add("BMW 1 Serisi ");
                    productDetailsArrayList.add("BMW 2 Serisi ");
                    productDetailsArrayList.add("BMW 3 Serisi ");
                    productDetailsArrayList.add("BMW 4 Serisi ");
                    productDetailsArrayList.add("BMW 5 Serisi ");
                    productDetailsArrayList.add("BMW 6 Serisi ");
                    productDetailsArrayList.add("BMW 7 Serisi ");
                    productDetailsArrayList.add("BMW 8 Serisi ");
                    productDetailsArrayList.add("BMW i Serisi ");
                    productDetailsArrayList.add("BMW M Serisi ");
                    productDetailsArrayList.add("BMW Z Serisi ");
                    productDetailsArrayList.add("Chevrolet Aveo ");
                    productDetailsArrayList.add("Chevrolet Camaro ");
                    productDetailsArrayList.add("Chevrolet Caprice ");
                    productDetailsArrayList.add("Chevrolet Celebrity ");
                    productDetailsArrayList.add("Chevrolet Corvette ");
                    productDetailsArrayList.add("Chevrolet Cruze ");
                    productDetailsArrayList.add("Chevrolet Epica ");
                    productDetailsArrayList.add("Chevrolet Evanda ");
                    productDetailsArrayList.add("Chevrolet Impala");
                    productDetailsArrayList.add("Chevrolet Kalos ");
                    productDetailsArrayList.add("Chevrolet Lacetti ");
                    productDetailsArrayList.add("Chevrolet Lumina ");
                    productDetailsArrayList.add("Chevrolet Malibu ");
                    productDetailsArrayList.add("Chevrolet Monte Carlo ");
                    productDetailsArrayList.add("Chevrolet Nubira ");
                    productDetailsArrayList.add("Chevrolet Rezzo ");
                    productDetailsArrayList.add("Chevrolet Spark ");
                    productDetailsArrayList.add("Citroën AMı ");
                    productDetailsArrayList.add("Citroën C-Elysée ");
                    productDetailsArrayList.add("Citroën C ");
                    productDetailsArrayList.add("Citroën C2 ");
                    productDetailsArrayList.add("Citroën C3  ");
                    productDetailsArrayList.add("Citroën C3 Picasso ");
                    productDetailsArrayList.add("Citroën C4  ");
                    productDetailsArrayList.add("Citroën C4 Grand Picasso ");
                    productDetailsArrayList.add("Citroën C4 Picasso ");
                    productDetailsArrayList.add("Citroën C4  ");
                    productDetailsArrayList.add("Citroën e-C4X ");
                    productDetailsArrayList.add("Citroën e-C4  ");
                    productDetailsArrayList.add("Citroën C5 ");
                    productDetailsArrayList.add("Citroën Saxo ");
                    productDetailsArrayList.add("Cupra Leon ");
                    productDetailsArrayList.add("Dacia Jogger");
                    productDetailsArrayList.add("Dacia Lodgy ");
                    productDetailsArrayList.add("Dacia Logan ");
                    productDetailsArrayList.add("Dacia Sandero ");
                    productDetailsArrayList.add("Dacia Nova");
                    productDetailsArrayList.add("Dacia Solenza ");
                    productDetailsArrayList.add("Fiat 124 Spider");
                    productDetailsArrayList.add("Fiat Albea ");
                    productDetailsArrayList.add("Fiat Brava");
                    productDetailsArrayList.add("Fiat Bravo");
                    productDetailsArrayList.add("Fiat 126 Bis");
                    productDetailsArrayList.add("Fiat Coupe");
                    productDetailsArrayList.add("Fiat Croroma");
                    productDetailsArrayList.add("Fiat 500");
                    productDetailsArrayList.add("Fiat Egea ");
                    productDetailsArrayList.add("Fiat Idea");
                    productDetailsArrayList.add("Fiat Linea ");
                    productDetailsArrayList.add("Fiat Marea");
                    productDetailsArrayList.add("Fiat Mirafio");
                    productDetailsArrayList.add("Fiat Multip");
                    productDetailsArrayList.add("Fiat Palio ");
                    productDetailsArrayList.add("Fiat Panda");
                    productDetailsArrayList.add("Fiat Punto ");
                    productDetailsArrayList.add("Fiat Rega");
                    productDetailsArrayList.add("Fiat Siena");
                    productDetailsArrayList.add("Fiat Stilo");
                    productDetailsArrayList.add("Fiat Tempra ");
                    productDetailsArrayList.add("Fiat Tipo ");
                    productDetailsArrayList.add("Fiat Ulysse");
                    productDetailsArrayList.add("Fiat Uno");
                    productDetailsArrayList.add("Ford B-Max ");
                    productDetailsArrayList.add("Ford C-Max ");
                    productDetailsArrayList.add("Ford Escort ");
                    productDetailsArrayList.add("Ford Fiesta ");
                    productDetailsArrayList.add("Ford Focus ");
                    productDetailsArrayList.add("Ford Fusion ");
                    productDetailsArrayList.add("Ford Galaxy");
                    productDetailsArrayList.add("Ford Grand C-Max");
                    productDetailsArrayList.add("Ford Ka ");
                    productDetailsArrayList.add("Ford Mondeo ");
                    productDetailsArrayList.add("Ford Mustang ");
                    productDetailsArrayList.add("Ford S-Max");
                    productDetailsArrayList.add("Ford Tauru");
                    productDetailsArrayList.add("Ford Couga");
                    productDetailsArrayList.add("Ford Crown Victori");
                    productDetailsArrayList.add("Ford Festiva");
                    productDetailsArrayList.add("Ford Granada");
                    productDetailsArrayList.add("Ford Orion");
                    productDetailsArrayList.add("Ford Probe");
                    productDetailsArrayList.add("Ford Puma");
                    productDetailsArrayList.add("Ford Scorpio");
                    productDetailsArrayList.add("Ford Sierra");
                    productDetailsArrayList.add("Ford Taunus ");
                    productDetailsArrayList.add("Honda Accord ");
                    productDetailsArrayList.add("Honda City ");
                    productDetailsArrayList.add("Honda Civic ");
                    productDetailsArrayList.add("Honda Concerto ");
                    productDetailsArrayList.add("Honda CR-Z (");
                    productDetailsArrayList.add("Honda CRX ");
                    productDetailsArrayList.add("Honda E ");
                    productDetailsArrayList.add("Honda Integra (");
                    productDetailsArrayList.add("Honda Jazz ");
                    productDetailsArrayList.add("Honda Legend ");
                    productDetailsArrayList.add("Honda Prelude (");
                    productDetailsArrayList.add("Honda S-MX ");
                    productDetailsArrayList.add("Honda S2000 ");
                    productDetailsArrayList.add("Honda Shuttle ");
                    productDetailsArrayList.add("Honda Stream ");
                    productDetailsArrayList.add("Hyundai Accent ");
                    productDetailsArrayList.add("Hyundai Accent Blue ");
                    productDetailsArrayList.add("Hyundai Accent Era ");
                    productDetailsArrayList.add("Hyundai Atos");
                    productDetailsArrayList.add("Hyundai Coupe");
                    productDetailsArrayList.add("Hyundai Elantra ");
                    productDetailsArrayList.add("Hyundai Excel ");
                    productDetailsArrayList.add("Hyundai Genesis");
                    productDetailsArrayList.add("Hyundai Getz ");
                    productDetailsArrayList.add("Hyundai Grandeu");
                    productDetailsArrayList.add("Hyundai i10 ");
                    productDetailsArrayList.add("Hyundai i20 (5");
                    productDetailsArrayList.add("Hyundai i20 Active");
                    productDetailsArrayList.add("Hyundai i20 N");
                    productDetailsArrayList.add("Hyundai i20 Troy ");
                    productDetailsArrayList.add("Hyundai i30 ");
                    productDetailsArrayList.add("Hyundai i4");
                    productDetailsArrayList.add("Hyundai Ioniq");
                    productDetailsArrayList.add("Hyundai iX20");
                    productDetailsArrayList.add("Hyundai Matrix ");
                    productDetailsArrayList.add("Hyundai S-Coup");
                    productDetailsArrayList.add("Hyundai Sonata ");
                    productDetailsArrayList.add("Hyundai Traje");
                    productDetailsArrayList.add("Jaguar Daimler  ");
                    productDetailsArrayList.add("Jaguar F-Type ");
                    productDetailsArrayList.add("Jaguar S-Type  ");
                    productDetailsArrayList.add("Jaguar Sovereign ");
                    productDetailsArrayList.add("Jaguar X-Type  ");
                    productDetailsArrayList.add("Jaguar XE  ");
                    productDetailsArrayList.add("Jaguar XF  ");
                    productDetailsArrayList.add("Jaguar XJ  ");
                    productDetailsArrayList.add("Jaguar XJ6 ");
                    productDetailsArrayList.add("Jaguar XJR ");
                    productDetailsArrayList.add("Jaguar XK8 ");
                    productDetailsArrayList.add("Jaguar XKR ");
                    productDetailsArrayList.add("Kia Capital  ");
                    productDetailsArrayList.add("Kia Carens ");
                    productDetailsArrayList.add("Kia Carnival  ");
                    productDetailsArrayList.add("Kia Ceed  ");
                    productDetailsArrayList.add("Kia Cerato  ");
                    productDetailsArrayList.add("Kia Clarus ");
                    productDetailsArrayList.add("Kia Magentis  ");
                    productDetailsArrayList.add("Kia Opirus ");
                    productDetailsArrayList.add("Kia Optima  ");
                    productDetailsArrayList.add("Kia Picanto  ");
                    productDetailsArrayList.add("Kia Pride  ");
                    productDetailsArrayList.add("Kia Pro Ceed  ");
                    productDetailsArrayList.add("Kia Rio  ");
                    productDetailsArrayList.add("Kia Sephia  ");
                    productDetailsArrayList.add("Kia Shuma  ");
                    productDetailsArrayList.add("Kia Stinger ");
                    productDetailsArrayList.add("Kia Venga  ");
                    productDetailsArrayList.add("Mazda 2 ");
                    productDetailsArrayList.add("Mazda 3  ");
                    productDetailsArrayList.add("Mazda 5 ");
                    productDetailsArrayList.add("Mazda 6 ");
                    productDetailsArrayList.add("Mazda MP ");
                    productDetailsArrayList.add("Mazda MX ");
                    productDetailsArrayList.add("Mazda Premac ");
                    productDetailsArrayList.add("Mazda 12 ");
                    productDetailsArrayList.add("Mazda 323  ");
                    productDetailsArrayList.add("Mazda 626  ");
                    productDetailsArrayList.add("Mazda 92 ");
                    productDetailsArrayList.add("Mazda Lantis ");
                    productDetailsArrayList.add("Mazda RX ");
                    productDetailsArrayList.add("Mazda Xedo ");
                    productDetailsArrayList.add("Mercedes Benz A Series ");
                    productDetailsArrayList.add("Mercedes Benz AMG GT ");
                    productDetailsArrayList.add("Mercedes Benz B Serisi ");
                    productDetailsArrayList.add("Mercedes Benz C Serisi ");
                    productDetailsArrayList.add("Mercedes Benz CL ");
                    productDetailsArrayList.add("Mercedes Benz CLA ");
                    productDetailsArrayList.add("Mercedes Benz CLC ");
                    productDetailsArrayList.add("Mercedes Benz CLK ");
                    productDetailsArrayList.add("Mercedes Benz CLS ");
                    productDetailsArrayList.add("Mercedes Benz E Series ");
                    productDetailsArrayList.add("Mercedes Benz EQE ");
                    productDetailsArrayList.add("Mercedes Benz EQS ");
                    productDetailsArrayList.add("Mercedes Benz Maybach S ");
                    productDetailsArrayList.add("Mercedes Benz R Serisi ");
                    productDetailsArrayList.add("Mercedes Benz S Serisi ");
                    productDetailsArrayList.add("Mercedes Benz SL ");
                    productDetailsArrayList.add("Mercedes Benz SLC ");
                    productDetailsArrayList.add("Mercedes Benz SLK ");
                    productDetailsArrayList.add("Mercedes Benz SLS AMG ");
                    productDetailsArrayList.add("Mercedes Benz 190 ");
                    productDetailsArrayList.add("Mercedes Benz 200 ");
                    productDetailsArrayList.add("Mercedes Benz 220 ");
                    productDetailsArrayList.add("Mercedes Benz 230 ");
                    productDetailsArrayList.add("Mercedes Benz 240 ");
                    productDetailsArrayList.add("Mercedes Benz 250 ");
                    productDetailsArrayList.add("Mercedes Benz 260 ");
                    productDetailsArrayList.add("Mercedes Benz 280 ");
                    productDetailsArrayList.add("Mercedes Benz 300 ");
                    productDetailsArrayList.add("Mercedes Benz 380 ");
                    productDetailsArrayList.add("Mercedes Benz 420 ");
                    productDetailsArrayList.add("Mercedes Benz 500 ");
                    productDetailsArrayList.add("Nissan 200 SX ");
                    productDetailsArrayList.add("Nissan 300 ZX");
                    productDetailsArrayList.add("Nissan 350 Z");
                    productDetailsArrayList.add("Nissan Almera (");
                    productDetailsArrayList.add("Nissan Altima");
                    productDetailsArrayList.add("Nissan Cedric");
                    productDetailsArrayList.add("Nissan GT-R ");
                    productDetailsArrayList.add("Nissan Laurel Altima ");
                    productDetailsArrayList.add("Nissan Maxima ");
                    productDetailsArrayList.add("Nissan Micra .");
                    productDetailsArrayList.add("Nissan Note ");
                    productDetailsArrayList.add("Nissan NX Coupe ");
                    productDetailsArrayList.add("Nissan Primera ");
                    productDetailsArrayList.add("Nissan Pulsar ");
                    productDetailsArrayList.add("Nissan Sunny ");
                    productDetailsArrayList.add("Nissan Teana");
                    productDetailsArrayList.add("Nissan Tino");
                    productDetailsArrayList.add("Opel Adam");
                    productDetailsArrayList.add("Opel Agila ");
                    productDetailsArrayList.add("Opel Ascona ");
                    productDetailsArrayList.add("Opel Astra ");
                    productDetailsArrayList.add("Opel Calibra");
                    productDetailsArrayList.add("Opel Cascada");
                    productDetailsArrayList.add("Opel Corsa ");
                    productDetailsArrayList.add("Opel Corsa-e ");
                    productDetailsArrayList.add("Opel GT (Roadster)");
                    productDetailsArrayList.add("Opel Insignia ");
                    productDetailsArrayList.add("Opel Kadett ");
                    productDetailsArrayList.add("Opel Karl");
                    productDetailsArrayList.add("Opel Manta");
                    productDetailsArrayList.add("Opel Meriva ");
                    productDetailsArrayList.add("Opel Omega ");
                    productDetailsArrayList.add("Opel Rekord");
                    productDetailsArrayList.add("Opel Senator");
                    productDetailsArrayList.add("Opel Signum");
                    productDetailsArrayList.add("Opel Tigra ");
                    productDetailsArrayList.add("Opel Vectra ");
                    productDetailsArrayList.add("Opel Zafira ");
                    productDetailsArrayList.add("Peugot 106 ");
                    productDetailsArrayList.add("Peugot 107");
                    productDetailsArrayList.add("Peugot 205");
                    productDetailsArrayList.add("Peugot 206 ");
                    productDetailsArrayList.add("Peugot 206 + ");
                    productDetailsArrayList.add("Peugot 207 ");
                    productDetailsArrayList.add("Peugot 208 ");
                    productDetailsArrayList.add("Peugot 301 ");
                    productDetailsArrayList.add("Peugot 30");
                    productDetailsArrayList.add("Peugot 306 ");
                    productDetailsArrayList.add("Peugot 307 ");
                    productDetailsArrayList.add("Peugot 308 ");
                    productDetailsArrayList.add("Peugot 39");
                    productDetailsArrayList.add("Peugot 405");
                    productDetailsArrayList.add("Peugot 406 ");
                    productDetailsArrayList.add("Peugot 407 ");
                    productDetailsArrayList.add("Peugot 508 ");
                    productDetailsArrayList.add("Peugot 605");
                    productDetailsArrayList.add("Peugot 607");
                    productDetailsArrayList.add("Peugot 80");
                    productDetailsArrayList.add("Peugot 80");
                    productDetailsArrayList.add("Peugot RCZ ");
                    productDetailsArrayList.add("Porsche 718  ");
                    productDetailsArrayList.add("Porsche 911  ");
                    productDetailsArrayList.add("Porsche 928 ");
                    productDetailsArrayList.add("Porsche Boxster  ");
                    productDetailsArrayList.add("Porsche Cayman  ");
                    productDetailsArrayList.add("Porsche Panamera ");
                    productDetailsArrayList.add("Porsche Taycan  ");
                    productDetailsArrayList.add("Renault Clio ");
                    productDetailsArrayList.add("Renault Espace");
                    productDetailsArrayList.add("Renault Fluence ");
                    productDetailsArrayList.add("Renault Fluence Z.E");
                    productDetailsArrayList.add("Renault Grand Espac");
                    productDetailsArrayList.add("Renault Grand Scenic ");
                    productDetailsArrayList.add("Renault Laguna ");
                    productDetailsArrayList.add("Renault Latitude ");
                    productDetailsArrayList.add("Renault Megane ");
                    productDetailsArrayList.add("Renault Modus");
                    productDetailsArrayList.add("Renault Safrane");
                    productDetailsArrayList.add("Renault Scenic ");
                    productDetailsArrayList.add("Renault Symbol ");
                    productDetailsArrayList.add("Renault Taliant ");
                    productDetailsArrayList.add("Renault Talisman ");
                    productDetailsArrayList.add("Renault Twingo ");
                    productDetailsArrayList.add("Renault Twizy");
                    productDetailsArrayList.add("Renault Vel Satis");
                    productDetailsArrayList.add("Renault ZOE ");
                    productDetailsArrayList.add("Renault R 5");
                    productDetailsArrayList.add("Renault R 9 ");
                    productDetailsArrayList.add("Renault R 11 ");
                    productDetailsArrayList.add("Renault R 12 ");
                    productDetailsArrayList.add("Renault R 19 ");
                    productDetailsArrayList.add("Seat Alhambra ");
                    productDetailsArrayList.add("Seat Altea ");
                    productDetailsArrayList.add("Seat Arosa");
                    productDetailsArrayList.add("Seat Cordoba ");
                    productDetailsArrayList.add("Seat Exeo ");
                    productDetailsArrayList.add("Seat Ibiza ");
                    productDetailsArrayList.add("Seat Leon ");
                    productDetailsArrayList.add("Seat Marbella");
                    productDetailsArrayList.add("Seat Toledo ");
                    productDetailsArrayList.add("Skoda Citigo");
                    productDetailsArrayList.add("Skoda Fabia ");
                    productDetailsArrayList.add("Skoda Favorit ");
                    productDetailsArrayList.add("Skoda Felicia ");
                    productDetailsArrayList.add("Skoda Forman ");
                    productDetailsArrayList.add("Skoda Octavia ");
                    productDetailsArrayList.add("Skoda Rapid ");
                    productDetailsArrayList.add("Skoda Roomster");
                    productDetailsArrayList.add("Skoda Scala ");
                    productDetailsArrayList.add("Skoda Superb ");
                    productDetailsArrayList.add("Subaru BRZ ");
                    productDetailsArrayList.add("Subaru Impreza ");
                    productDetailsArrayList.add("Subaru Legacy ");
                    productDetailsArrayList.add("Subaru Levorg ");
                    productDetailsArrayList.add("Subaru Justy ");
                    productDetailsArrayList.add("Subaru Vivio ");
                    productDetailsArrayList.add("Suzuki Alto ");
                    productDetailsArrayList.add("Suzuki Baleno ");
                    productDetailsArrayList.add("Suzuki Splash ");
                    productDetailsArrayList.add("Suzuki Swift (");
                    productDetailsArrayList.add("Suzuki SX4 ");
                    productDetailsArrayList.add("Suzuki Wagon R");
                    productDetailsArrayList.add("Suzuki Liana");
                    productDetailsArrayList.add("Suzuki Maruti ");
                    productDetailsArrayList.add("Tesla Model 3 ");
                    productDetailsArrayList.add("Tesla Model S ");
                    productDetailsArrayList.add("Tesla Model X ");
                    productDetailsArrayList.add("Tesla Model Y ");
                    productDetailsArrayList.add("Tofas Doğan ");
                    productDetailsArrayList.add("Tofas Kartal ");
                    productDetailsArrayList.add("Tofas Murat");
                    productDetailsArrayList.add("Tofas Şahin ");
                    productDetailsArrayList.add("Tofas Serçe");
                    productDetailsArrayList.add("Toyata Auris ");
                    productDetailsArrayList.add("Toyata Avensis ");
                    productDetailsArrayList.add("Toyata Aygo ");
                    productDetailsArrayList.add("Toyata Camry ");
                    productDetailsArrayList.add("Toyata Carina ");
                    productDetailsArrayList.add("Toyata Celica ");
                    productDetailsArrayList.add("Toyata Corolla ");
                    productDetailsArrayList.add("Toyata Corona ");
                    productDetailsArrayList.add("Toyata Cressida ");
                    productDetailsArrayList.add("Toyata Estima ");
                    productDetailsArrayList.add("Toyata GT86 ");
                    productDetailsArrayList.add("Toyata Mark 2");
                    productDetailsArrayList.add("Toyata MR2 ");
                    productDetailsArrayList.add("Toyata Picnic ");
                    productDetailsArrayList.add("Toyata Previa ");
                    productDetailsArrayList.add("Toyata Prius ");
                    productDetailsArrayList.add("Toyata Starlet (");
                    productDetailsArrayList.add("Toyata Supra ");
                    productDetailsArrayList.add("Toyata Urban Cruiser ");
                    productDetailsArrayList.add("Toyata Verso ");
                    productDetailsArrayList.add("Toyata Yaris ");
                    productDetailsArrayList.add("Volkswagen Beetle ");
                    productDetailsArrayList.add("Volkswagen Bora ");
                    productDetailsArrayList.add("Volkswagen EOS ");
                    productDetailsArrayList.add("Volkswagen Golf ");
                    productDetailsArrayList.add("Volkswagen ID.3");
                    productDetailsArrayList.add("Volkswagen Jetta ");
                    productDetailsArrayList.add("Volkswagen Lupo ");
                    productDetailsArrayList.add("Volkswagen Passat ");
                    productDetailsArrayList.add("Volkswagen Passat Alltrack ");
                    productDetailsArrayList.add("Volkswagen Passat Variant ");
                    productDetailsArrayList.add("Volkswagen Phaeton");
                    productDetailsArrayList.add("Volkswagen Polo ");
                    productDetailsArrayList.add("Volkswagen Routan");
                    productDetailsArrayList.add("Volkswagen Santana");
                    productDetailsArrayList.add("Volkswagen Scirocco ");
                    productDetailsArrayList.add("Volkswagen Sharan ");
                    productDetailsArrayList.add("Volkswagen Touran ");
                    productDetailsArrayList.add("Volkswagen Up Club");
                    productDetailsArrayList.add("Volkswagen VW CC");
                    productDetailsArrayList.add("Volvo C30 ");
                    productDetailsArrayList.add("Volvo C70 ");
                    productDetailsArrayList.add("Volvo S40 ");
                    productDetailsArrayList.add("Volvo S60 ");
                    productDetailsArrayList.add("Volvo S70 ");
                    productDetailsArrayList.add("Volvo S80 ");
                    productDetailsArrayList.add("Volvo S90 ");
                    productDetailsArrayList.add("Volvo V40 ");
                    productDetailsArrayList.add("Volvo V40 Cross Country ");
                    productDetailsArrayList.add("Volvo V50 ");
                    productDetailsArrayList.add("Volvo V60 ");
                    productDetailsArrayList.add("Volvo V60 Cross Country ");
                    productDetailsArrayList.add("Volvo V70 ");
                    productDetailsArrayList.add("Volvo V90 Cross Country ");
                    productDetailsArrayList.add("Volvo 440");
                    productDetailsArrayList.add("Volvo 460");
                    productDetailsArrayList.add("Volvo 480");
                    productDetailsArrayList.add("Volvo 740");
                    productDetailsArrayList.add("Volvo 850 ");
                    productDetailsArrayList.add("Volvo 940 ");
                    productDetailsArrayList.add("Volvo 960");
                    productDetailsArrayList.add("Alfa Romeo Stelvio ");
                    productDetailsArrayList.add("Alfa Romeo Tonale ");
                    productDetailsArrayList.add("Audi E-Tron ");
                    productDetailsArrayList.add("Audi E-Tron Sportback ");
                    productDetailsArrayList.add("Audi Q2 ");
                    productDetailsArrayList.add("Audi Q3 ");
                    productDetailsArrayList.add("Audi Q3 Sportback ");
                    productDetailsArrayList.add("Audi Q4 ");
                    productDetailsArrayList.add("Audi Q5 3");
                    productDetailsArrayList.add("Audi Q5 Sportback ");
                    productDetailsArrayList.add("Audi Q7 ");
                    productDetailsArrayList.add("Audi Q8 ");
                    productDetailsArrayList.add("Audi Q8 e-tron ");
                    productDetailsArrayList.add("BMW iX ");
                    productDetailsArrayList.add("BMW iX1");
                    productDetailsArrayList.add("BMW iX3 ");
                    productDetailsArrayList.add("BMW X1 ");
                    productDetailsArrayList.add("BMW X2");
                    productDetailsArrayList.add("BMW X3 ");
                    productDetailsArrayList.add("BMW X4");
                    productDetailsArrayList.add("BMW X5 ");
                    productDetailsArrayList.add("BMW X6 ");
                    productDetailsArrayList.add("BMW X");
                    productDetailsArrayList.add("Chevrolet Avalanche");
                    productDetailsArrayList.add("Chevrolet Blazer ");
                    productDetailsArrayList.add("Chevrolet Captiva ");
                    productDetailsArrayList.add("Chevrolet Equinox");
                    productDetailsArrayList.add("Chevrolet Silverado ");
                    productDetailsArrayList.add("Chevrolet Suburban");
                    productDetailsArrayList.add("Chevrolet Tahoe ");
                    productDetailsArrayList.add("Chevrolet Tracker");
                    productDetailsArrayList.add("Chevrolet Trax ");
                    productDetailsArrayList.add("Chevrolet Menlo");
                    productDetailsArrayList.add("Chevrolet HHR");
                    productDetailsArrayList.add("Citroen C3 AirCross ");
                    productDetailsArrayList.add("Citroen C4 Cactus ");
                    productDetailsArrayList.add("Citroen C5 AirCross ");
                    productDetailsArrayList.add("Cupra Formentor");
                    productDetailsArrayList.add("Dacia Duster ");
                    productDetailsArrayList.add("Dacia Logan PickuP");
                    productDetailsArrayList.add("Dacia Sandero Stepway ");
                    productDetailsArrayList.add("Fiat Egea Cross ");
                    productDetailsArrayList.add("Fiat Freemont ");
                    productDetailsArrayList.add("Fiat Fullback ");
                    productDetailsArrayList.add("Fiat Sedici ");
                    productDetailsArrayList.add("Fiat 500 X ");
                    productDetailsArrayList.add("Ford EcoSport ");
                    productDetailsArrayList.add("Ford Edge");
                    productDetailsArrayList.add("Ford Expedition");
                    productDetailsArrayList.add("Ford Explorer ");
                    productDetailsArrayList.add("Ford F ");
                    productDetailsArrayList.add("Ford Flex");
                    productDetailsArrayList.add("Ford Kuga ");
                    productDetailsArrayList.add("Ford Mustang Mach-E");
                    productDetailsArrayList.add("Ford Puma ");
                    productDetailsArrayList.add("Ford Ranger ");
                    productDetailsArrayList.add("Ford Ranger Raptor ");
                    productDetailsArrayList.add("Ford Bronco");
                    productDetailsArrayList.add("Ford Freestyle");
                    productDetailsArrayList.add("Ford Maverick");
                    productDetailsArrayList.add("Honda CR-V ");
                    productDetailsArrayList.add("Honda HR-V");
                    productDetailsArrayList.add("Hyundai Bayon ");
                    productDetailsArrayList.add("Hyundai Ioniq ");
                    productDetailsArrayList.add("Hyundai ix35 ");
                    productDetailsArrayList.add("Hyundai ix55");
                    productDetailsArrayList.add("Hyundai Galloper ");
                    productDetailsArrayList.add("Hyundai Kona ");
                    productDetailsArrayList.add("Hyundai Santa Fe (");
                    productDetailsArrayList.add("Hyundai Terracan");
                    productDetailsArrayList.add("Hyundai Tucson ");
                    productDetailsArrayList.add("Jaguar E-Pace");
                    productDetailsArrayList.add("Jaguar F-Pace ");
                    productDetailsArrayList.add("Jaguar I-Pace");
                    productDetailsArrayList.add("Jeep Cherokee ");
                    productDetailsArrayList.add("Jeep Commander");
                    productDetailsArrayList.add("Jeep Compass ");
                    productDetailsArrayList.add("Jeep Grand Cherokee ");
                    productDetailsArrayList.add("Jeep Patriot");
                    productDetailsArrayList.add("Jeep Renegade ");
                    productDetailsArrayList.add("Jeep Wrangler ");
                    productDetailsArrayList.add("Jeep CJ");
                    productDetailsArrayList.add("Land Rover Defender");
                    productDetailsArrayList.add("Land Rover Discovery");
                    productDetailsArrayList.add("Land Rover Discovery Sport");
                    productDetailsArrayList.add("Land Rover Range Rover ");
                    productDetailsArrayList.add("Land Rover Range Rover Evoque");
                    productDetailsArrayList.add("Land Rover Range Rover Sport");
                    productDetailsArrayList.add("Land Rover Range Rover Velar");
                    productDetailsArrayList.add("Land Rover Freelander");
                    productDetailsArrayList.add("Mazda CX-3 ");
                    productDetailsArrayList.add("Mazda CX-5 ");
                    productDetailsArrayList.add("Mazda CX-9 ");
                    productDetailsArrayList.add("Mazda B Series ");
                    productDetailsArrayList.add("Mazda Tribute ");
                    productDetailsArrayList.add("Mercedes Benz EQA");
                    productDetailsArrayList.add("Mercedes Benz EQB ");
                    productDetailsArrayList.add("Mercedes Benz EQC ");
                    productDetailsArrayList.add("Mercedes Benz EQS SUV");
                    productDetailsArrayList.add("Mercedes Benz G Series ");
                    productDetailsArrayList.add("Mercedes Benz GL ");
                    productDetailsArrayList.add("Mercedes Benz GLA ");
                    productDetailsArrayList.add("Mercedes Benz GLB ");
                    productDetailsArrayList.add("Mercedes Benz GLC ");
                    productDetailsArrayList.add("Mercedes Benz GLC Coupe ");
                    productDetailsArrayList.add("Mercedes Benz GLE ");
                    productDetailsArrayList.add("Mercedes Benz GLE Coupe ");
                    productDetailsArrayList.add("Mercedes Benz GLK ");
                    productDetailsArrayList.add("Mercedes Benz GLS ");
                    productDetailsArrayList.add("Mercedes Benz ML ");
                    productDetailsArrayList.add("Mercedes Benz X ");
                    productDetailsArrayList.add("Mitsubishi ASX ");
                    productDetailsArrayList.add("Mitsubishi Eclipse Cross ");
                    productDetailsArrayList.add("Mitsubishi L 200 ");
                    productDetailsArrayList.add("Mitsubishi Outlander ");
                    productDetailsArrayList.add("Mitsubishi Pajero ");
                    productDetailsArrayList.add("Nissan Juke ");
                    productDetailsArrayList.add("Nissan Navara ");
                    productDetailsArrayList.add("Nissan Pathfinder");
                    productDetailsArrayList.add("Nissan Patrol");
                    productDetailsArrayList.add("Nissan Qashqai ");
                    productDetailsArrayList.add("Nissan Qashqai+2");
                    productDetailsArrayList.add("Nissan X-Trail ");
                    productDetailsArrayList.add("Nissan Country");
                    productDetailsArrayList.add("Nissan Muran");
                    productDetailsArrayList.add("Nissan Rally Raid");
                    productDetailsArrayList.add("Nissan Skystar ");
                    productDetailsArrayList.add("Nissan Terrano");
                    productDetailsArrayList.add("Opel Antara");
                    productDetailsArrayList.add("Opel Crossland ");
                    productDetailsArrayList.add("Opel Crossland X ");
                    productDetailsArrayList.add("Opel Grandland ");
                    productDetailsArrayList.add("Opel Grandland X ");
                    productDetailsArrayList.add("Opel Mokka ");
                    productDetailsArrayList.add("Opel Mokka-e");
                    productDetailsArrayList.add("Opel Mokka X ");
                    productDetailsArrayList.add("Opel Frontera");
                    productDetailsArrayList.add("Opel Montere");
                    productDetailsArrayList.add("Peugeot 408 ");
                    productDetailsArrayList.add("Peugeot e-2008");
                    productDetailsArrayList.add("Peugeot 2008 ");
                    productDetailsArrayList.add("Peugeot 3008 ");
                    productDetailsArrayList.add("Peugeot 5008 ");
                    productDetailsArrayList.add("Peugeot 4007");
                    productDetailsArrayList.add("Porsche Cayenne ");
                    productDetailsArrayList.add("Porsche Cayenne Coupe ");
                    productDetailsArrayList.add("Porsche Macan ");
                    productDetailsArrayList.add("Renault Austral ");
                    productDetailsArrayList.add("Renault Captur ");
                    productDetailsArrayList.add("Renault Kadjar ");
                    productDetailsArrayList.add("Renault Koleos ");
                    productDetailsArrayList.add("Renault Scenic RX4 ");
                    productDetailsArrayList.add("Seat Tarraco ");
                    productDetailsArrayList.add("Seat Arona ");
                    productDetailsArrayList.add("Seat Ateca ");
                    productDetailsArrayList.add("Skoda Kamiq ");
                    productDetailsArrayList.add("Skoda Karoq ");
                    productDetailsArrayList.add("Skoda Kodiaq ");
                    productDetailsArrayList.add("Skoda Yeti ");
                    productDetailsArrayList.add("Skoda Felicia Pickup");
                    productDetailsArrayList.add("Ssang Yong Actyon");
                    productDetailsArrayList.add("Ssang Yong Actyon Sports ");
                    productDetailsArrayList.add("Ssang Yong Korando ");
                    productDetailsArrayList.add("Ssang Yong Korando Sports ");
                    productDetailsArrayList.add("Ssang Yong Kyron");
                    productDetailsArrayList.add("Ssang Yong Musso");
                    productDetailsArrayList.add("Ssang Yong Musso Grand");
                    productDetailsArrayList.add("Ssang Yong Rexton");
                    productDetailsArrayList.add("Ssang Yong Tivoli");
                    productDetailsArrayList.add("Ssang Yong XLV");
                    productDetailsArrayList.add("Ssang Yong Rodius");
                    productDetailsArrayList.add("Subaru Forester ");
                    productDetailsArrayList.add("Subaru Outback");
                    productDetailsArrayList.add("Subaru Solterr");
                    productDetailsArrayList.add("Subaru XV ");
                    productDetailsArrayList.add("Subaru Tribeca");
                    productDetailsArrayList.add("Suzuki Grand Vitara (");
                    productDetailsArrayList.add("Suzuki Jimny ");
                    productDetailsArrayList.add("Suzuki SJ ");
                    productDetailsArrayList.add("Suzuki S-Cross ");
                    productDetailsArrayList.add("Suzuki Samurai ");
                    productDetailsArrayList.add("Suzuki Vitara ");
                    productDetailsArrayList.add("Suzuki X-90");
                    productDetailsArrayList.add("TOGG T10X");
                    productDetailsArrayList.add("Toyata C-HR");
                    productDetailsArrayList.add("Toyata Corolla Cross ");
                    productDetailsArrayList.add("Toyata FJ Cruiser ");
                    productDetailsArrayList.add("Toyata Hilux ");
                    productDetailsArrayList.add("Toyata Land Cruiser ");
                    productDetailsArrayList.add("Toyata RAV4 ");
                    productDetailsArrayList.add("Toyata Yaris Cross ");
                    productDetailsArrayList.add("Toyata 4Runner");
                    productDetailsArrayList.add("Toyata Fortuner");
                    productDetailsArrayList.add("Toyata Sequoia");
                    productDetailsArrayList.add("Toyata Tacoma");
                    productDetailsArrayList.add("Volkswagen Amarok ");
                    productDetailsArrayList.add("Volkswagen ID.4");
                    productDetailsArrayList.add("Volkswagen ID.6");
                    productDetailsArrayList.add("Volkswagen T-Cross ");
                    productDetailsArrayList.add("Volkswagen T-Roc ");
                    productDetailsArrayList.add("Volkswagen Taigo ");
                    productDetailsArrayList.add("Volkswagen Tiguan ");
                    productDetailsArrayList.add("Volkswagen Tiguan AllSpace");
                    productDetailsArrayList.add("Volkswagen Touareg ");
                    productDetailsArrayList.add("Volvo Amarok ");
                    productDetailsArrayList.add("Volvo ID.4");
                    productDetailsArrayList.add("Volvo ID.6");
                    productDetailsArrayList.add("Volvo T-Cross ");
                    productDetailsArrayList.add("Volvo T-Roc ");
                    productDetailsArrayList.add("Volvo Taigo ");
                    productDetailsArrayList.add("Volvo Tiguan ");
                    productDetailsArrayList.add("Volvo Tiguan AllSpace");
                    productDetailsArrayList.add("Altai Carrier 110 Pro ");
                    productDetailsArrayList.add("Altai Carrier 125 ");
                    productDetailsArrayList.add("Altai F1Max 50 ");
                    productDetailsArrayList.add("Altai F1Max Pro 50 ");
                    productDetailsArrayList.add("Altai F1Max Pro 125 ");
                    productDetailsArrayList.add("Altai Misk 50 ");
                    productDetailsArrayList.add("Altai Tank 50 ");
                    productDetailsArrayList.add("Altai Tank S50");
                    productDetailsArrayList.add("Apachi 4V Cross");
                    productDetailsArrayList.add("Apachi Alfa 50 ");
                    productDetailsArrayList.add("Apachi Joy 125 ");
                    productDetailsArrayList.add("Apachi Myra ");
                    productDetailsArrayList.add("Apachi Nova 125 ");
                    productDetailsArrayList.add("Apachi Pusat ");
                    productDetailsArrayList.add("Apachi True 50 ");
                    productDetailsArrayList.add("Apachi True 125 ");
                    productDetailsArrayList.add("Apachi XRS ");
                    productDetailsArrayList.add("Apachi XZ 250R");
                    productDetailsArrayList.add("BMW C 600 Sport");
                    productDetailsArrayList.add("BMW C 650 GT ");
                    productDetailsArrayList.add("BMW C 650 Sport");
                    productDetailsArrayList.add("BMW C1");
                    productDetailsArrayList.add("BMW F 650 ");
                    productDetailsArrayList.add("BMW F 650 CS");
                    productDetailsArrayList.add("BMW F 650 GS ");
                    productDetailsArrayList.add("BMW F 650 GS Dakar");
                    productDetailsArrayList.add("BMW F 650ST");
                    productDetailsArrayList.add("BMW F 700 GS ");
                    productDetailsArrayList.add("BMW F 750 GS ");
                    productDetailsArrayList.add("BMW F 800 GS ");
                    productDetailsArrayList.add("BMW F 800 GS Adventure");
                    productDetailsArrayList.add("BMW F 800 GT");
                    productDetailsArrayList.add("BMW F 800 R");
                    productDetailsArrayList.add("BMW F 800S");
                    productDetailsArrayList.add("BMW F 850 GS ");
                    productDetailsArrayList.add("BMW F 850 GS Adventure");
                    productDetailsArrayList.add("BMW F 900 R");
                    productDetailsArrayList.add("BMW F 900 XR ");
                    productDetailsArrayList.add("BMW G 310 GS ");
                    productDetailsArrayList.add("BMW G 310 R ");
                    productDetailsArrayList.add("BMW G 650 GS");
                    productDetailsArrayList.add("BMW G 650 X Country");
                    productDetailsArrayList.add("BMW K 1100 LT");
                    productDetailsArrayList.add("BMW K 1200 GT");
                    productDetailsArrayList.add("BMW K 1200 LT");
                    productDetailsArrayList.add("BMW K 1200 R ");
                    productDetailsArrayList.add("BMW K 1200 RS");
                    productDetailsArrayList.add("BMW K 1200 S ");
                    productDetailsArrayList.add("BMW K 1300 GT");
                    productDetailsArrayList.add("BMW K 1300 R");
                    productDetailsArrayList.add("BMW K 1300 S");
                    productDetailsArrayList.add("BMW K 1600 B");
                    productDetailsArrayList.add("BMW K 1600 Bagger");
                    productDetailsArrayList.add("BMW K 1600 Grand America");
                    productDetailsArrayList.add("BMW K 1600 GT ");
                    productDetailsArrayList.add("BMW K 1600 GTL ");
                    productDetailsArrayList.add("Falcon Attack 50 ");
                    productDetailsArrayList.add("Falcon Attack 100 (");
                    productDetailsArrayList.add("Falcon Breeze 125");
                    productDetailsArrayList.add("Falcon Comfort 150 ");
                    productDetailsArrayList.add("Falcon Comfort 180");
                    productDetailsArrayList.add("Falcon Cooper 125 EFI ");
                    productDetailsArrayList.add("Falcon Cooper 50 ");
                    productDetailsArrayList.add("Falcon Cooper 50 EFI ");
                    productDetailsArrayList.add("Falcon Crazy 125");
                    productDetailsArrayList.add("Falcon Crown 150 ");
                    productDetailsArrayList.add("Falcon Custom 150");
                    productDetailsArrayList.add("Falcon Desert 277");
                    productDetailsArrayList.add("Falcon Dolphin 100 ");
                    productDetailsArrayList.add("Falcon Dolphin 100 EFI ");
                    productDetailsArrayList.add("Falcon Dolphin 125 EFI ");
                    productDetailsArrayList.add("Falcon Flash 100");
                    productDetailsArrayList.add("Falcon Freedom 250 ");
                    productDetailsArrayList.add("Falcon Leopar 222 ");
                    productDetailsArrayList.add("Falcon Lion 150");
                    productDetailsArrayList.add("Falcon Magic 50 ");
                    productDetailsArrayList.add("Falcon Martini 125 ");
                    productDetailsArrayList.add("Falcon Martini 50");
                    productDetailsArrayList.add("Falcon Magic 100 ");
                    productDetailsArrayList.add("Falcon Master 50 ");
                    productDetailsArrayList.add("Falcon Mexico 150 ");
                    productDetailsArrayList.add("Falcon Mocco 125 ");
                    productDetailsArrayList.add("Falcon Mocco 50 ");
                    productDetailsArrayList.add("Falcon Nitro 50 ");
                    productDetailsArrayList.add("Falcon Shark 188 ");
                    productDetailsArrayList.add("Falcon Sharp 150");
                    productDetailsArrayList.add("Falcon Sharp 170");
                    productDetailsArrayList.add("Honda Activa 100 ");
                    productDetailsArrayList.add("Honda Activa 110 ");
                    productDetailsArrayList.add("Honda Activa S ");
                    productDetailsArrayList.add("Honda ADV350 ");
                    productDetailsArrayList.add("Honda Bali");
                    productDetailsArrayList.add("Honda Beat ");
                    productDetailsArrayList.add("Honda C100 BIZ");
                    productDetailsArrayList.add("Honda C125 Super Cub ");
                    productDetailsArrayList.add("Honda C70 Super Cub");
                    productDetailsArrayList.add("Honda C90 Super Cub ");
                    productDetailsArrayList.add("Honda CB 125 ");
                    productDetailsArrayList.add("Honda CB 125 ACE ");
                    productDetailsArrayList.add("Honda CB 125E ");
                    productDetailsArrayList.add("Honda CB 125 F ");
                    productDetailsArrayList.add("Honda CB 125 R ");
                    productDetailsArrayList.add("Honda CB 250 R ");
                    productDetailsArrayList.add("Honda CB 500 F ");
                    productDetailsArrayList.add("Honda CB 600 F Hornet");
                    productDetailsArrayList.add("Honda CB 650 F ");
                    productDetailsArrayList.add("Honda CB 650 R ");
                    productDetailsArrayList.add("Honda CB 750 Hornet ");
                    productDetailsArrayList.add("Honda CB 900 Hornet");
                    productDetailsArrayList.add("Honda CR 85");
                    productDetailsArrayList.add("Honda CB 1000 R");
                    productDetailsArrayList.add("Honda CBF 150 (");
                    productDetailsArrayList.add("Honda CBF 250 ");
                    productDetailsArrayList.add("Honda CBF 500 ");
                    productDetailsArrayList.add("Honda CBF 600 ");
                    productDetailsArrayList.add("Honda CBF 1000 ");
                    productDetailsArrayList.add("Honda CBR 125 R ");
                    productDetailsArrayList.add("Honda CBR 250 R ");
                    productDetailsArrayList.add("Honda CBR 500R ");
                    productDetailsArrayList.add("Honda CBR 600 F ");
                    productDetailsArrayList.add("Honda CBR 600 FA");
                    productDetailsArrayList.add("Honda CBR 600 F Sport");
                    productDetailsArrayList.add("Honda CBR 600 RR ");
                    productDetailsArrayList.add("Honda CBR 650 F ");
                    productDetailsArrayList.add("Honda CBR 650 FA");
                    productDetailsArrayList.add("Honda CBR 650 R ");
                    productDetailsArrayList.add("Honda CBR 900 RR");
                    productDetailsArrayList.add("Honda CBR 929 RR");
                    productDetailsArrayList.add("Honda CBR 954 RR");
                    productDetailsArrayList.add("Honda CBR 1000 RR ");
                    productDetailsArrayList.add("Honda CBR 1000 RR-R Fireblade SP");
                    productDetailsArrayList.add("Honda CBR 1000 RR SP ");
                    productDetailsArrayList.add("Honda CBR 1100 XX ");
                    productDetailsArrayList.add("Honda CBX 250 Twister");
                    productDetailsArrayList.add("Honda CBX 550 F");
                    productDetailsArrayList.add("Honda CBX 750");
                    productDetailsArrayList.add("Honda CG 125 ");
                    productDetailsArrayList.add("Honda CGL 125 ");
                    productDetailsArrayList.add("Honda CL 250");
                    productDetailsArrayList.add("Honda CMX 250 Rebel");
                    productDetailsArrayList.add("Honda CR 250");
                    productDetailsArrayList.add("Honda CRF 150 R");
                    productDetailsArrayList.add("Honda CRF 250 L ");
                    productDetailsArrayList.add("Honda CRF 250 R");
                    productDetailsArrayList.add("Honda CRF 250 Rally ");
                    productDetailsArrayList.add("Honda CRF 250 X");
                    productDetailsArrayList.add("Honda CRF 450 R");
                    productDetailsArrayList.add("Honda CRF 450 X");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin ");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin Adventure Sports");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin Adventure Sports DCT");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin DCT ");
                    productDetailsArrayList.add("Kanuni BD100");
                    productDetailsArrayList.add("Kanuni Bobcat 150 ");
                    productDetailsArrayList.add("Kanuni Bora 125");
                    productDetailsArrayList.add("Kanuni Breton 125 ");
                    productDetailsArrayList.add("Kanuni Breton S ");
                    productDetailsArrayList.add("Kanuni Breton SL 125");
                    productDetailsArrayList.add("Kanuni BS 100 ");
                    productDetailsArrayList.add("Kanuni BS 125");
                    productDetailsArrayList.add("Kanuni Caracal 200 ");
                    productDetailsArrayList.add("Kanuni Cheetah 125");
                    productDetailsArrayList.add("Kanuni Classic 125 ");
                    productDetailsArrayList.add("Kanuni Cross 150");
                    productDetailsArrayList.add("Kanuni Cross 250");
                    productDetailsArrayList.add("Kanuni Cup 100 ");
                    productDetailsArrayList.add("Kanuni Cup 100 S ");
                    productDetailsArrayList.add("Kanuni Deer 152");
                    productDetailsArrayList.add("Kanuni Dolphin 100");
                    productDetailsArrayList.add("Kanuni Elite 100");
                    productDetailsArrayList.add("Kanuni Enduro 200");
                    productDetailsArrayList.add("Kanuni Explorer");
                    productDetailsArrayList.add("Kanuni FOX");
                    productDetailsArrayList.add("Kanuni Freedom 200");
                    productDetailsArrayList.add("Kanuni GT 170 ");
                    productDetailsArrayList.add("Kanuni GT 250");
                    productDetailsArrayList.add("Kanuni GV 170");
                    productDetailsArrayList.add("Kanuni GV 250");
                    productDetailsArrayList.add("Kanuni Hussar 125 ");
                    productDetailsArrayList.add("Kanuni Leopard");
                    productDetailsArrayList.add("Kanuni Mati 125 (");
                    productDetailsArrayList.add("Kanuni Merlin S ");
                    productDetailsArrayList.add("Kanuni Mini 50");
                    productDetailsArrayList.add("Kanuni Moped Turbo Sport");
                    productDetailsArrayList.add("Kanuni Motocar 200");
                    productDetailsArrayList.add("Kanuni Motorum 150 ");
                    productDetailsArrayList.add("Kanuni Nev 50 ");
                    productDetailsArrayList.add("Kanuni Phantom 180 ");
                    productDetailsArrayList.add("Kanuni Popcorn 90 ");
                    productDetailsArrayList.add("Kanuni Power GXL");
                    productDetailsArrayList.add("Kanuni Puma 150");
                    productDetailsArrayList.add("Kanuni Q100");
                    productDetailsArrayList.add("Kanuni Racer 200");
                    productDetailsArrayList.add("Kanuni Racer 200 S");
                    productDetailsArrayList.add("Kanuni Reha 250");
                    productDetailsArrayList.add("Kanuni Resa 125 (");
                    productDetailsArrayList.add("Kanuni Rokko 150");
                    productDetailsArrayList.add("Kanuni Ronny S ");
                    productDetailsArrayList.add("Kanuni RS 125 ");
                    productDetailsArrayList.add("Kanuni Ruby 100");
                    productDetailsArrayList.add("Kanuni S125T");
                    productDetailsArrayList.add("Kanuni S170T");
                    productDetailsArrayList.add("Kanuni Seha 150 ");
                    productDetailsArrayList.add("Kymco Agility 16+ 125i ");
                    productDetailsArrayList.add("Kymco Agility 16+ 150");
                    productDetailsArrayList.add("Kymco Agility 50");
                    productDetailsArrayList.add("Kymco Agility 125 (");
                    productDetailsArrayList.add("Kymco Agility Carry 125i");
                    productDetailsArrayList.add("Kymco Agility Carry 50i 4T");
                    productDetailsArrayList.add("Kymco Agility City 125 ");
                    productDetailsArrayList.add("Kymco Agility City 200 i ");
                    productDetailsArrayList.add("Kymco Agility S 125");
                    productDetailsArrayList.add("Kymco Ak 550");
                    productDetailsArrayList.add("Kymco Activ 100");
                    productDetailsArrayList.add("Kymco Aktiv 110");
                    productDetailsArrayList.add("Kymco CK 125 Pulsar ");
                    productDetailsArrayList.add("Kymco CV3");
                    productDetailsArrayList.add("Kymco Dink 200i ");
                    productDetailsArrayList.add("Kymco Dink R 150 ");
                    productDetailsArrayList.add("Kymco Downtown 250i ");
                    productDetailsArrayList.add("Kymco Downtown 300i ");
                    productDetailsArrayList.add("Kymco Downtown 350i ABS");
                    productDetailsArrayList.add("Kymco DT X360 ");
                    productDetailsArrayList.add("Kymco Grand Dink 150");
                    productDetailsArrayList.add("Kymco Grand Dink 250");
                    productDetailsArrayList.add("Kymco Grand Dink 250i");
                    productDetailsArrayList.add("Kymco Heroism 125");
                    productDetailsArrayList.add("Kymco KRV 200 ");
                    productDetailsArrayList.add("Kymco Like 125 ");
                    productDetailsArrayList.add("Kymco Like 200i");
                    productDetailsArrayList.add("Kymco Like 50 ");
                    productDetailsArrayList.add("Kymco Like S 125");
                    productDetailsArrayList.add("Kymco Like S 50");
                    productDetailsArrayList.add("Kymco Movie XL 125");
                    productDetailsArrayList.add("Kymco Movie XL 150");
                    productDetailsArrayList.add("Kymco People 125");
                    productDetailsArrayList.add("Kymco People S 125i");
                    productDetailsArrayList.add("Kuba Ajax 150");
                    productDetailsArrayList.add("Kuba Apricot 125");
                    productDetailsArrayList.add("Kuba Black Cat ");
                    productDetailsArrayList.add("Kuba Blueberry ");
                    productDetailsArrayList.add("Kuba Bluebird ");
                    productDetailsArrayList.add("Kuba Brilliant 125 ");
                    productDetailsArrayList.add("Kuba Brilliant 50 ");
                    productDetailsArrayList.add("Kuba Cargo");
                    productDetailsArrayList.add("Kuba CG 100 ");
                    productDetailsArrayList.add("Kuba CG 100/KM125-6 ");
                    productDetailsArrayList.add("Kuba CG150 ");
                    productDetailsArrayList.add("Kuba CG 50 ");
                    productDetailsArrayList.add("Kuba City Go ");
                    productDetailsArrayList.add("Kuba CR1 ");
                    productDetailsArrayList.add("Kuba CR1-S ");
                    productDetailsArrayList.add("Kuba Çita 100 ");
                    productDetailsArrayList.add("Kuba Çita 100R ");
                    productDetailsArrayList.add("Kuba Çita 100R Gold ");
                    productDetailsArrayList.add("Kuba Çita 100R Max Gold");
                    productDetailsArrayList.add("Kuba Çita 125 ");
                    productDetailsArrayList.add("Kuba Çita 125R Max");
                    productDetailsArrayList.add("Kuba Çita 125R Max Gold");
                    productDetailsArrayList.add("Kuba Çita 150R ");
                    productDetailsArrayList.add("Kuba Çita 150R Gold ");
                    productDetailsArrayList.add("Kuba Çita 170F ");
                    productDetailsArrayList.add("Kuba Çita 180-FC ");
                    productDetailsArrayList.add("Kuba Çita 180R ");
                    productDetailsArrayList.add("Kuba Çita 180R Gold ");
                    productDetailsArrayList.add("Kuba Çita 50R Gold (");
                    productDetailsArrayList.add("Kuba DB705");
                    productDetailsArrayList.add("Kuba Dragon 50 ");
                    productDetailsArrayList.add("Kuba Ege 100 ");
                    productDetailsArrayList.add("Kuba Ege 50 ");
                    productDetailsArrayList.add("Kuba Faswind 200R");
                    productDetailsArrayList.add("Kuba Fighter 50 ");
                    productDetailsArrayList.add("Kuba Fighter 80 ");
                    productDetailsArrayList.add("Kuba Filinta 100");
                    productDetailsArrayList.add("Kuba Filinta 200 ");
                    productDetailsArrayList.add("Kuba Gelli 125");
                    productDetailsArrayList.add("Kuba Golf 100");
                    productDetailsArrayList.add("Kuba Hasat 100 ");
                    productDetailsArrayList.add("Kuba Kargo 180 ");
                    productDetailsArrayList.add("Kuba KB100-6");
                    productDetailsArrayList.add("Kuba KB150-25 ");
                    productDetailsArrayList.add("Kuba KB150-25 Max");
                    productDetailsArrayList.add("Kuba KB150-9");
                    productDetailsArrayList.add("Kuba KB150T-B");
                    productDetailsArrayList.add("Kuba KEE 100 ");
                    productDetailsArrayList.add("Kuba KH100");
                    productDetailsArrayList.add("Kuba KH125-12D");
                    productDetailsArrayList.add("Kuba KH150-12D");
                    productDetailsArrayList.add("Kuba KM 100T-9");
                    productDetailsArrayList.add("Kuba KM125-6 ");
                    productDetailsArrayList.add("Kuba KR 139");
                    productDetailsArrayList.add("Mondial Air Time ");
                    productDetailsArrayList.add("Mondial Fury 110i ");
                    productDetailsArrayList.add("Mondial Virago 50 ");
                    productDetailsArrayList.add("Mondial 50 BeeStreet ");
                    productDetailsArrayList.add("Mondial 50 Eagle ");
                    productDetailsArrayList.add("Mondial 50 HC ");
                    productDetailsArrayList.add("Mondial 50 Loyal ");
                    productDetailsArrayList.add("Mondial 50 Revival ");
                    productDetailsArrayList.add("Mondial 50 SFC ");
                    productDetailsArrayList.add("Mondial 50 TAB ");
                    productDetailsArrayList.add("Mondial 50 TT");
                    productDetailsArrayList.add("Mondial 50 Turismo ");
                    productDetailsArrayList.add("Mondial 50 Wing ");
                    productDetailsArrayList.add("Mondial 50 ZNU ");
                    productDetailsArrayList.add("Mondial 50 ZNU ec ");
                    productDetailsArrayList.add("Mondial 100 Ardour");
                    productDetailsArrayList.add("Mondial 100 Hyena ");
                    productDetailsArrayList.add("Mondial 100 KM ");
                    productDetailsArrayList.add("Mondial 100 Loyal ");
                    productDetailsArrayList.add("Mondial 100 Masti ");
                    productDetailsArrayList.add("Mondial 100 Masti X");
                    productDetailsArrayList.add("Mondial 100 MFH ");
                    productDetailsArrayList.add("Mondial 100 MFM");
                    productDetailsArrayList.add("Mondial 100 MG Prince ");
                    productDetailsArrayList.add("Mondial 100 MG Sport");
                    productDetailsArrayList.add("Mondial 100 MG Superboy (");
                    productDetailsArrayList.add("Mondial 100 NT Turkuaz ");
                    productDetailsArrayList.add("Mondial 100 RT ");
                    productDetailsArrayList.add("Mondial 100 SFC Automatic X ");
                    productDetailsArrayList.add("Mondial 100 SFC Basic X");
                    productDetailsArrayList.add("Mondial 100 SFC Exclusive");
                    productDetailsArrayList.add("Mondial 100 SFC Snappy X ");
                    productDetailsArrayList.add("Mondial 100 SFC Snappy Xi ");
                    productDetailsArrayList.add("Mondial 100 SFS Sport");
                    productDetailsArrayList.add("Mondial 100 Superboy i ");
                    productDetailsArrayList.add("Mondial 100 UAG ");
                    productDetailsArrayList.add("Mondial 100 UGK");
                    productDetailsArrayList.add("Mondial 100 UKH ");
                    productDetailsArrayList.add("Mondial 100 URT");
                    productDetailsArrayList.add("Mondial 110 FT ");
                    productDetailsArrayList.add("Mondial 110 KF");
                    productDetailsArrayList.add("Mondial 125 Aggressive");
                    productDetailsArrayList.add("Mondial 125 AGK ");
                    productDetailsArrayList.add("Mondial 125 Ardour");
                    productDetailsArrayList.add("Mondial 125 Drift L ");
                    productDetailsArrayList.add("Mondial 125 Drift L CBS ");
                    productDetailsArrayList.add("Mondial 125 Elegante ");
                    productDetailsArrayList.add("Mondial 125 KT ");
                    productDetailsArrayList.add("Mondial 125 KV");
                    productDetailsArrayList.add("Mondial 125 Lavinia ");
                    productDetailsArrayList.add("Mondial 125 Mash ");
                    productDetailsArrayList.add("Mondial 125 MC Roadracer");
                    productDetailsArrayList.add("Mondial 125 MG Classic ");
                    productDetailsArrayList.add("Mondial 125 MG Deluxe");
                    productDetailsArrayList.add("Mondial 125 MG Sport ");
                    productDetailsArrayList.add("Mondial 125 MH ");
                    productDetailsArrayList.add("Mondial 125 MH Drift (");
                    productDetailsArrayList.add("Mondial 125 MT ");
                    productDetailsArrayList.add("Mondial 125 MX Grumble ");
                    productDetailsArrayList.add("Mondial 125 NT Turkuaz ");
                    productDetailsArrayList.add("Mondial 125 Prostreet ");
                    productDetailsArrayList.add("Mondial 125 Road Boy ");
                    productDetailsArrayList.add("Mondial 125 RR");
                    productDetailsArrayList.add("Mondial 125 RT");
                    productDetailsArrayList.add("Mondial 125 RT Akik");
                    productDetailsArrayList.add("Mondial 125 SFS");
                    productDetailsArrayList.add("Mondial 125 Strada ");
                    productDetailsArrayList.add("Mondial 125 Superboy i ");
                    productDetailsArrayList.add("Motolux Africa King ");
                    productDetailsArrayList.add("Motolux Africa Wolf ");
                    productDetailsArrayList.add("Motolux CEO 110 ");
                    productDetailsArrayList.add("Motolux Efsane 50 ");
                    productDetailsArrayList.add("Motolux Efsane X ");
                    productDetailsArrayList.add("Motolux Macchiato 125 ");
                    productDetailsArrayList.add("Motolux Macchiato 50 ");
                    productDetailsArrayList.add("Motolux MTX 125 ");
                    productDetailsArrayList.add("Motolux MW46 ");
                    productDetailsArrayList.add("Motolux Nirvana 50 ");
                    productDetailsArrayList.add("Motolux Nirvana Pro ");
                    productDetailsArrayList.add("Motolux Pitton 50");
                    productDetailsArrayList.add("Motolux Rossi 125 ");
                    productDetailsArrayList.add("Motolux Rossi 50 ");
                    productDetailsArrayList.add("Motolux Rossi Rs ");
                    productDetailsArrayList.add("Motolux Rossi RS 50 ");
                    productDetailsArrayList.add("Motolux Rüzgar 50 ");
                    productDetailsArrayList.add("Motolux Transit 125");
                    productDetailsArrayList.add("Motolux Vintage 50 ");
                    productDetailsArrayList.add("Motolux W46 ");
                    productDetailsArrayList.add("RKS 125R ");
                    productDetailsArrayList.add("RKS 125-S");
                    productDetailsArrayList.add("RKS Arome 125 ");
                    productDetailsArrayList.add("RKS Azure 50");
                    productDetailsArrayList.add("RKS Azure 50 Pro (");
                    productDetailsArrayList.add("RKS Bitter 125 ");
                    productDetailsArrayList.add("RKS Bitter 50 ");
                    productDetailsArrayList.add("RKS Blackster 250i");
                    productDetailsArrayList.add("RKS Blade 250 ");
                    productDetailsArrayList.add("RKS Blade 350 ");
                    productDetailsArrayList.add("RKS Blazer 50 ");
                    productDetailsArrayList.add("RKS Blazer 50 XR ");
                    productDetailsArrayList.add("RKS Blazer 50 XR Max ");
                    productDetailsArrayList.add("RKS Bolero 50 ");
                    productDetailsArrayList.add("RKS C1002 V");
                    productDetailsArrayList.add("RKS Cafe 152 ");
                    productDetailsArrayList.add("RKS Cityblade ");
                    productDetailsArrayList.add("RKS Cruiser 250 ");
                    productDetailsArrayList.add("RKS Dark Blue 125 ");
                    productDetailsArrayList.add("RKS Dark Blue 50 ");
                    productDetailsArrayList.add("RKS Easy Pro 50 ");
                    productDetailsArrayList.add("RKS Freccia 150 ");
                    productDetailsArrayList.add("RKS Galaxy Gold 125 ");
                    productDetailsArrayList.add("RKS Grace 202 ");
                    productDetailsArrayList.add("RKS Grace 202 Pro ");
                    productDetailsArrayList.add("RKS Jaguar 100 ");
                    productDetailsArrayList.add("RKS K-Light 202 ");
                    productDetailsArrayList.add("RKS K-Light 250 ");
                    productDetailsArrayList.add("RKS M502N ");
                    productDetailsArrayList.add("RKS Newlight ");
                    productDetailsArrayList.add("RKS Newlight 125 Pro ");
                    productDetailsArrayList.add("RKS Next 100 ");
                    productDetailsArrayList.add("RKS Next 50 ");
                    productDetailsArrayList.add("RKS NR200 ");
                    productDetailsArrayList.add("Kawasaki EL 250");
                    productDetailsArrayList.add("Kawasaki EN 500 ");
                    productDetailsArrayList.add("Kawasaki ER-5 ");
                    productDetailsArrayList.add("Kawasaki ER-6 F ");
                    productDetailsArrayList.add("Kawasaki ER-6 N ");
                    productDetailsArrayList.add("Kawasaki GTR 1400");
                    productDetailsArrayList.add("Kawasaki J 300");
                    productDetailsArrayList.add("Kawasaki KLE 500 ");
                    productDetailsArrayList.add("Kawasaki KLE 650 Versys ");
                    productDetailsArrayList.add("Kawasaki KLR 250");
                    productDetailsArrayList.add("Kawasaki KLR 650");
                    productDetailsArrayList.add("Kawasaki KLV 1000");
                    productDetailsArrayList.add("Kawasaki KLX 150 L");
                    productDetailsArrayList.add("Kawasaki KLX 250 ");
                    productDetailsArrayList.add("Kawasaki KLX 650");
                    productDetailsArrayList.add("Kawasaki KLZ 1000 Versys");
                    productDetailsArrayList.add("Kawasaki KX 125");
                    productDetailsArrayList.add("Kawasaki KX 250");
                    productDetailsArrayList.add("Kawasaki KX 250F");
                    productDetailsArrayList.add("Kawasaki KX 450F");
                    productDetailsArrayList.add("Kawasaki KX 65");
                    productDetailsArrayList.add("Kawasaki KX 85");
                    productDetailsArrayList.add("Kawasaki Ninja 250R ");
                    productDetailsArrayList.add("Kawasaki Ninja 250SL ");
                    productDetailsArrayList.add("Kawasaki Ninja 300 ");
                    productDetailsArrayList.add("Kawasaki Ninja 400 ");
                    productDetailsArrayList.add("Kawasaki Ninja 650 ");
                    productDetailsArrayList.add("Kawasaki Ninja 650 KRT Edition");
                    productDetailsArrayList.add("Kawasaki Ninja H2");
                    productDetailsArrayList.add("Kawasaki Ninja H2 SX");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-6R ");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10R ");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10RR");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10R SE");
                    productDetailsArrayList.add("Kawasaki Versys 1000 SE");
                    productDetailsArrayList.add("Kawasaki Versys X300");
                    productDetailsArrayList.add("Kawasaki VN 750 Vulcan");
                    productDetailsArrayList.add("Suzuki Address");
                    productDetailsArrayList.add("Suzuki Address 110 ");
                    productDetailsArrayList.add("Suzuki AN 125 HK ");
                    productDetailsArrayList.add("Suzuki Best 110");
                    productDetailsArrayList.add("Suzuki Burgman 200");
                    productDetailsArrayList.add("Suzuki Burgman UH 200 ");
                    productDetailsArrayList.add("Suzuki Burgman AN 400 ");
                    productDetailsArrayList.add("Suzuki Burgman AN 650 ABS ");
                    productDetailsArrayList.add("Suzuki DR350");
                    productDetailsArrayList.add("Suzuki DR600");
                    productDetailsArrayList.add("Suzuki DR650");
                    productDetailsArrayList.add("Suzuki DR650 SE");
                    productDetailsArrayList.add("Suzuki DR800 Big");
                    productDetailsArrayList.add("Suzuki DRZ400 SM");
                    productDetailsArrayList.add("Suzuki Freewind XF650");
                    productDetailsArrayList.add("Suzuki GN 125");
                    productDetailsArrayList.add("Suzuki GN 250");
                    productDetailsArrayList.add("Suzuki GS 500");
                    productDetailsArrayList.add("Suzuki GS 1000E");
                    productDetailsArrayList.add("Suzuki GSF 1200 Bandit S");
                    productDetailsArrayList.add("Suzuki GSF 600 Bandit");
                    productDetailsArrayList.add("Suzuki GSF 600 Bandit S");
                    productDetailsArrayList.add("Suzuki GSF 650 Bandit S ");
                    productDetailsArrayList.add("Suzuki GW 250F ");
                    productDetailsArrayList.add("Suzuki GW250 Inazuma ");
                    productDetailsArrayList.add("Suzuki GSR 600 ");
                    productDetailsArrayList.add("Suzuki GSR 750");
                    productDetailsArrayList.add("Suzuki GSX 8S");
                    productDetailsArrayList.add("Suzuki GSX 600 F");
                    productDetailsArrayList.add("Suzuki GSX 650 F");
                    productDetailsArrayList.add("Suzuki GSX 750 F");
                    productDetailsArrayList.add("Suzuki GSX 1400");
                    productDetailsArrayList.add("Suzuki GSX-R 125 ");
                    productDetailsArrayList.add("Suzuki GSX-R 250 ");
                    productDetailsArrayList.add("Suzuki GSX-R 600 Srad ");
                    productDetailsArrayList.add("Suzuki GSX-R 750 Srad");
                    productDetailsArrayList.add("Suzuki GSX-R 1000 ");
                    productDetailsArrayList.add("Suzuki GSX-R 1300 Hayabusa ");
                    productDetailsArrayList.add("Suzuki GSX-S 1000 ");
                    productDetailsArrayList.add("Suzuki GSX-S 1000 GT");
                    productDetailsArrayList.add("Suzuki GSX-S 125 ");
                    productDetailsArrayList.add("Suzuki GSX-S 750 ABS");
                    productDetailsArrayList.add("Suzuki Marauder 250");
                    productDetailsArrayList.add("Suzuki Marauder 800");
                    productDetailsArrayList.add("Suzuki LS 650 Savage");
                    productDetailsArrayList.add("SYN Allo 125");
                    productDetailsArrayList.add("SYN Crox 125");
                    productDetailsArrayList.add("SYN Cruisym 250i ");
                    productDetailsArrayList.add("SYN DRG 160 ");
                    productDetailsArrayList.add("SYN Fiddle II 125 ");
                    productDetailsArrayList.add("SYN Fiddle III");
                    productDetailsArrayList.add("SYN Fiddle III 125 ");
                    productDetailsArrayList.add("SYN Fiddle III 200i ");
                    productDetailsArrayList.add("SYN Fiddle IV 125 ");
                    productDetailsArrayList.add("SYN GTS 250 ");
                    productDetailsArrayList.add("SYN GTS 250i EVO ");
                    productDetailsArrayList.add("SYN HD2 200i");
                    productDetailsArrayList.add("SYN HD2 200i EVO ");
                    productDetailsArrayList.add("SYN HD Evo 200");
                    productDetailsArrayList.add("SYN Jet 14 ");
                    productDetailsArrayList.add("SYN Jet 14 200i");
                    productDetailsArrayList.add("SYN Jet 14 200i ABS (");
                    productDetailsArrayList.add("SYN Jet 4 125");
                    productDetailsArrayList.add("SYN Jet X 125 ");
                    productDetailsArrayList.add("SYN Joymax 250i ");
                    productDetailsArrayList.add("SYN Joymax 250i ABS ");
                    productDetailsArrayList.add("SYN Joymax Z 250");
                    productDetailsArrayList.add("SYN Joymax Z Plus 250 ");
                    productDetailsArrayList.add("SYN Joyride 300 ");
                    productDetailsArrayList.add("SYN Joyride Evo 200");
                    productDetailsArrayList.add("SYN Joyride Evo 200i ");
                    productDetailsArrayList.add("SYN Joyride S 200i ABS");
                    productDetailsArrayList.add("SYN Maxi 400i ");
                    productDetailsArrayList.add("SYN Maxi 400i ABS");
                    productDetailsArrayList.add("SYN Maxsym 600i ABS");
                    productDetailsArrayList.add("SYN Maxsym TL 508 ");
                    productDetailsArrayList.add("SYN Mio 100 ");
                    productDetailsArrayList.add("SYN Mio 50 ");
                    productDetailsArrayList.add("SYN Orbit II 125 ");
                    productDetailsArrayList.add("SYN Orbit II 125i ");
                    productDetailsArrayList.add("SYN Orbit II 50 ");
                    productDetailsArrayList.add("SYN Shark");
                    productDetailsArrayList.add("SYN Symnh X 125");
                    productDetailsArrayList.add("SYN Symphony SR 125 ");
                    productDetailsArrayList.add("SYN Symphony ST 125 ");
                    productDetailsArrayList.add("SYN Symphony ST 200i ");
                    productDetailsArrayList.add("SYN Symphony ST 200i ABS ");
                    productDetailsArrayList.add("SYN VS 150");
                    productDetailsArrayList.add("TVS Apache RR310");
                    productDetailsArrayList.add("TVS Apache RTR 150 ");
                    productDetailsArrayList.add("TVS Apache RTR 180");
                    productDetailsArrayList.add("TVS Apache RTR 200 (");
                    productDetailsArrayList.add("TVS Jupiter ");
                    productDetailsArrayList.add("TVS Jupiter 125 (");
                    productDetailsArrayList.add("TVS Neo X3i ");
                    productDetailsArrayList.add("TVS Ntorq 125");
                    productDetailsArrayList.add("TVS Scooty Pep Plus ");
                    productDetailsArrayList.add("TVS Scooty Zest 110");
                    productDetailsArrayList.add("TVS Wego ");
                    productDetailsArrayList.add("Vespa 946 125 i.e.");
                    productDetailsArrayList.add("Vespa Cosa 200");
                    productDetailsArrayList.add("Vespa ET 4 ");
                    productDetailsArrayList.add("Vespa GT 200 ");
                    productDetailsArrayList.add("Vespa GT 250");
                    productDetailsArrayList.add("Vespa GTS 125");
                    productDetailsArrayList.add("Vespa GTS 125 ABS ");
                    productDetailsArrayList.add("Vespa GTS 125 Supertech ");
                    productDetailsArrayList.add("Vespa GTS 150");
                    productDetailsArrayList.add("Vespa GTS 150 ABS ");
                    productDetailsArrayList.add("Vespa GTS 250 ");
                    productDetailsArrayList.add("Vespa GTS 250 ABS");
                    productDetailsArrayList.add("Vespa GTS 300 ");
                    productDetailsArrayList.add("Vespa GTS 300 Super ");
                    productDetailsArrayList.add("Vespa GTS 300 Super Sport ");
                    productDetailsArrayList.add("Vespa GTS 300 Super Sport S ");
                    productDetailsArrayList.add("Vespa GTS 300 Supertech ");
                    productDetailsArrayList.add("Vespa GTV 250 ie");
                    productDetailsArrayList.add("Vespa GTV 300 ie ");
                    productDetailsArrayList.add("Vespa LX 125 3V i.e ");
                    productDetailsArrayList.add("Vespa LX 150 ");
                    productDetailsArrayList.add("Vespa LX 150 3V i.e ");
                    productDetailsArrayList.add("Vespa LX 150 i.e ");
                    productDetailsArrayList.add("Vespa LXV 125");
                    productDetailsArrayList.add("Vespa Primavera 50 ");
                    productDetailsArrayList.add("Vespa Primavera 125 ");
                    productDetailsArrayList.add("Vespa Primavera 150 ");
                    productDetailsArrayList.add("Vespa PX 150 ");
                    productDetailsArrayList.add("Vespa PX 200 ");
                    productDetailsArrayList.add("Vespa S 125");
                    productDetailsArrayList.add("Yamaha Aerox 100");
                    productDetailsArrayList.add("Yamaha R7 ");
                    productDetailsArrayList.add("Yamaha BW's 50");
                    productDetailsArrayList.add("Yamaha BW's 100 (");
                    productDetailsArrayList.add("Yamaha BW's 125 ");
                    productDetailsArrayList.add("Yamaha Crypton ");
                    productDetailsArrayList.add("Yamaha Cygnus L ");
                    productDetailsArrayList.add("Yamaha Cygnus RS ");
                    productDetailsArrayList.add("Yamaha Cygnus X ");
                    productDetailsArrayList.add("Yamaha D'elight ");
                    productDetailsArrayList.add("Yamaha DT 80");
                    productDetailsArrayList.add("Yamaha DT 125 R");
                    productDetailsArrayList.add("Yamaha Fazer 8 ");
                    productDetailsArrayList.add("Yamaha Fazer 8 ABS ");
                    productDetailsArrayList.add("Yamaha FJR 1300 ");
                    productDetailsArrayList.add("Yamaha FZ 1");
                    productDetailsArrayList.add("Yamaha FZ1 Fazer");
                    productDetailsArrayList.add("Yamaha FZ6 ");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer ");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer ABS");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer S2");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer S2 ABS");
                    productDetailsArrayList.add("Yamaha FZ8 ");
                    productDetailsArrayList.add("Yamaha FZ8 ABS");
                    productDetailsArrayList.add("Yamaha FZR 1000");
                    productDetailsArrayList.add("Yamaha FZR 600");
                    productDetailsArrayList.add("Yamaha FZS1000");
                    productDetailsArrayList.add("Yamaha FZS600");
                    productDetailsArrayList.add("Yamaha Galaxy");
                    productDetailsArrayList.add("Yamaha LB 70 Chappy");
                    productDetailsArrayList.add("Yamaha Majesty 180");
                    productDetailsArrayList.add("Yamaha Majesty 250");
                    productDetailsArrayList.add("Yamaha Majesty 400 ");
                    productDetailsArrayList.add("Yamaha MT-01");
                    productDetailsArrayList.add("Yamaha MT-03 ");
                    productDetailsArrayList.add("Yamaha MT-07 ");
                    productDetailsArrayList.add("Yamaha MT-07 ABS ");
                    productDetailsArrayList.add("Yamaha MT-07 Moto Cage");
                    productDetailsArrayList.add("Yamaha MT-09 ");
                    productDetailsArrayList.add("Yamaha MT-09 SP ");
                    productDetailsArrayList.add("Yamaha MT-10 ");
                    productDetailsArrayList.add("Yamaha MT-10 SP ");
                    productDetailsArrayList.add("Yamaha MT-125 ");
                    productDetailsArrayList.add("Yamaha MT-25 ");
                    productDetailsArrayList.add("Yamaha MT-25 ABS ");
                    productDetailsArrayList.add("Yamaha Neos ");
                    productDetailsArrayList.add("Yamaha Niken ");
                    productDetailsArrayList.add("Yamaha Niken GT");
                    productDetailsArrayList.add("Yamaha NMax 125 ");
                    productDetailsArrayList.add("Yamaha NMax 155 ");
                    productDetailsArrayList.add("Yamaha Nouvo ");
                    productDetailsArrayList.add("Yamaha PW 50");
                    productDetailsArrayList.add("Yamaha PW 80");
                    productDetailsArrayList.add("Yamaha Royal Star XVZ 1300");
                    productDetailsArrayList.add("Yamaha RX 1");
                    productDetailsArrayList.add("Yamaha RX 115 ");
                    productDetailsArrayList.add("Yamaha RX 135");
                    productDetailsArrayList.add("Yamaha RX-Z");
                    productDetailsArrayList.add("Yamaha SCR950");
                    productDetailsArrayList.add("Yamaha SR125");
                    productDetailsArrayList.add("Yamaha SR 400");
                    productDetailsArrayList.add("Yamaha TDM 850");
                    productDetailsArrayList.add("Yamaha TDM 900");
                    productDetailsArrayList.add("Yamaha Tenere 700 ");
                    productDetailsArrayList.add("Yamaha Tmax 500");
                    productDetailsArrayList.add("Yamaha Tmax 530 ");
                    productDetailsArrayList.add("Yamaha Tmax 560 ");
                    productDetailsArrayList.add("Yamaha Tracer 700 (");
                    productDetailsArrayList.add("Yamaha Tracer 900 ");
                    productDetailsArrayList.add("Yamaha Tracer 900 GT ");
                    productDetailsArrayList.add("Yamaha Tricity 125 ");
                    productDetailsArrayList.add("Yamaha Tricity 155 ");
                    productDetailsArrayList.add("Yamaha Tricity 300");
                    productDetailsArrayList.add("Yamaha TT 600 E");
                    productDetailsArrayList.add("Yamaha TTR 110 E");
                    productDetailsArrayList.add("Yamaha Venture Royal");
                    productDetailsArrayList.add("Yamaha Vercity 300");
                    productDetailsArrayList.add("Yamaha Virago XV 1100");
                    productDetailsArrayList.add("Yamaha Virago XV 250");
                    productDetailsArrayList.add("Yamaha Virago XV 535 ");
                    productDetailsArrayList.add("Yamaha Virago XV 750");
                    productDetailsArrayList.add("Yamaha Vmax");
                    productDetailsArrayList.add("Yamaha Wild Star XV 1600");
                    productDetailsArrayList.add("Yamaha WR 125 R ");
                    productDetailsArrayList.add("Yamaha WR 125 X ");
                    productDetailsArrayList.add("Yamaha WR 250 F");
                    productDetailsArrayList.add("Yamaha WR 250 R ");
                    productDetailsArrayList.add("Yamaha WR 250 X");
                    productDetailsArrayList.add("Yamaha WR 400");
                    productDetailsArrayList.add("Yamaha WR 450 F");
                    productDetailsArrayList.add("Yamaha X-City 250 ");
                    productDetailsArrayList.add("Yamaha Xenter 150 ");
                    productDetailsArrayList.add("Yamaha XJ 6 ");
                    productDetailsArrayList.add("Yamaha XJ 600 Diversion");
                    productDetailsArrayList.add("Yamaha XJ 600 N");
                    productDetailsArrayList.add("Yamaha XJ 6 Diversion F ");
                    productDetailsArrayList.add("Yamaha XJ 900 Diversion");
                    productDetailsArrayList.add("Yamaha XJR 1200");
                    productDetailsArrayList.add("Yamaha XJR 1300 SP");
                    productDetailsArrayList.add("Yamaha X-Max 125 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 125 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 ");
                    productDetailsArrayList.add("Yamaha X-Max 250 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 MomoDesign ");
                    productDetailsArrayList.add("Yamaha X-Max 250 Tech Max ");
                    productDetailsArrayList.add("Yamaha X-Max 300 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 300 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 400 ");
                    productDetailsArrayList.add("Yamaha X-Max 400 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 400 Iron Max ");
                    productDetailsArrayList.add("Yamaha X-Max 400 Tech Max ");
                    productDetailsArrayList.add("Yamaha XSR 125 ");
                    productDetailsArrayList.add("Yamaha XSR 700 ");
                    productDetailsArrayList.add("Yamaha XSR 900 ");
                    productDetailsArrayList.add("Yamaha XT 125 R");
                    productDetailsArrayList.add("Yamaha XT 125 X");
                    productDetailsArrayList.add("Yamaha XT 600");
                    productDetailsArrayList.add("Yamaha XT 600 E");
                    productDetailsArrayList.add("Yamaha XT 660 R ");
                    productDetailsArrayList.add("Yamaha XT 660 X ");
                    productDetailsArrayList.add("Yamaha XT 1200Z Super Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 660ZA Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 660Z Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 750 Super Tenere");
                    productDetailsArrayList.add("Yamaha XV950 ");
                    productDetailsArrayList.add("Yamaha XV950R");
                    productDetailsArrayList.add("Yamaha XV950 Racer");
                    productDetailsArrayList.add("Yamaha XVS 1100 Drag Star ");
                    productDetailsArrayList.add("Yamaha XVS 1300 A");
                    productDetailsArrayList.add("Yamaha XVS 250 Drag Star");
                    productDetailsArrayList.add("Yamaha XVS 650 A");
                    productDetailsArrayList.add("Yamaha XVS 650 Drag Star ");
                    productDetailsArrayList.add("Yamaha XVS 950 A");
                    productDetailsArrayList.add("Yamaha XVS 950 CU");
                    productDetailsArrayList.add("Yamaha YB 100");
                    productDetailsArrayList.add("Yamaha YBR 125 ");
                    productDetailsArrayList.add("Yamaha YBR 125 ESD ");
                    productDetailsArrayList.add("Yamaha YBR 250");
                    productDetailsArrayList.add("Yamaha YP 250 R x max");
                    productDetailsArrayList.add("Yamaha YS 125 ");
                    productDetailsArrayList.add("Yamaha YZ 65");
                    productDetailsArrayList.add("Yamaha YZ 85");
                    productDetailsArrayList.add("Yamaha YZ 125");
                    productDetailsArrayList.add("Yamaha YZ 250");
                    productDetailsArrayList.add("Yamaha YZ 250 F");
                    productDetailsArrayList.add("Yamaha YZ 400 F");
                    productDetailsArrayList.add("Yamaha YZ 450 F");
                    productDetailsArrayList.add("Yamaha YZF 1000 Thunderace");
                    productDetailsArrayList.add("Yamaha YZF 600 R Thundercat");
                    productDetailsArrayList.add("Yamaha YZF R1 ");
                    productDetailsArrayList.add("Yamaha YZF R125 ");
                    productDetailsArrayList.add("Yamaha YZF R1M");
                    productDetailsArrayList.add("Yamaha YZF R25 ");
                    productDetailsArrayList.add("Yamaha YZF R25 ABS ");
                    productDetailsArrayList.add("Yamaha YZF R6 ");
                    productDetailsArrayList.add("Yamaha YZF R6s");
                    productDetailsArrayList.add("Yamaha Zoom ");
                    productDetailsArrayList.add("Yuki Active 125 ");
                    productDetailsArrayList.add("Yuki Active 50 ");
                    productDetailsArrayList.add("Yuki Afşin 250 ");
                    productDetailsArrayList.add("Yuki Attact 100 ");
                    productDetailsArrayList.add("Yuki Casper S 50 ");
                    productDetailsArrayList.add("Yuki Crypto 125 ");
                    productDetailsArrayList.add("Yuki Crypto 50 ");
                    productDetailsArrayList.add("Yuki Defender Maxi ADV ");
                    productDetailsArrayList.add("Yuki Dirty Paws Offroad ");
                    productDetailsArrayList.add("Yuki Drag 200 ");
                    productDetailsArrayList.add("Yuki Enzo 50 ");
                    productDetailsArrayList.add("Yuki Fifty 50 ");
                    productDetailsArrayList.add("Yuki Forza 100");
                    productDetailsArrayList.add("Yuki Forza 170");
                    productDetailsArrayList.add("Yuki Funrider 125 ");
                    productDetailsArrayList.add("Yuki Gelato 150 ");
                    productDetailsArrayList.add("Yuki Gusto 50 ");
                    productDetailsArrayList.add("Yuki Hammer 50 ");
                    productDetailsArrayList.add("Yuki Huracan TR250T ");
                    productDetailsArrayList.add("Yuki Imola 125 ");
                    productDetailsArrayList.add("Yuki JJ125T-15 Active ");
                    productDetailsArrayList.add("Yuki JJ50QT Picasso 50 ");
                    productDetailsArrayList.add("Yuki LB150T-8");
                    productDetailsArrayList.add("Yuki Legend 50 ");
                    productDetailsArrayList.add("Yuki Lupo 125 ");
                    productDetailsArrayList.add("Yuki Margherita 50 ");
                    productDetailsArrayList.add("Yuki Mojito 125 ");
                    productDetailsArrayList.add("Yuki Mojito 50 ");
                    productDetailsArrayList.add("Yuki QM50QT-6E Snoopy ");
                    productDetailsArrayList.add("Yuki Risotto 125 ");
                    productDetailsArrayList.add("Yuki Risotto 50 ");
                    productDetailsArrayList.add("Yuki Route 110 ");
                    productDetailsArrayList.add("Yuki Scram 170 ");
                    productDetailsArrayList.add("Yuki T11 Explorer ");
                    productDetailsArrayList.add("Yuki T9 Strom 125 ");
                    productDetailsArrayList.add("Yuki Taro 250R ");
                    productDetailsArrayList.add("Yuki TB100T3E Picasso 100");
                    productDetailsArrayList.add("Yuki Tekken 125 ");
                    productDetailsArrayList.add("Yuki TK50Q3 Picasso 50 ");
                    productDetailsArrayList.add("Yuki TN150-3A Driver ");
                    productDetailsArrayList.add("Yuki YB 100 Jumbo");
                    productDetailsArrayList.add("Yuki YB 150 Jumbo ");
                    productDetailsArrayList.add("Yuki YB250ZKT Optimus");
                    productDetailsArrayList.add("Yuki YB50QT-3 Casper ");
                    productDetailsArrayList.add("Yuki Yıldız 100");
                    productDetailsArrayList.add("Yuki Yıldız 130");
                    productDetailsArrayList.add("Yuki YK 100-2A Apollo");
                    productDetailsArrayList.add("Yuki YK100-2H");
                    productDetailsArrayList.add("Yuki YK100-5");
                    productDetailsArrayList.add("Yuki YK-100-7A Gusto ");
                    productDetailsArrayList.add("Yuki YK100-7 Paşa");
                    productDetailsArrayList.add("Yuki YK 100-B");
                    productDetailsArrayList.add("Yuki YK-100M Modify");
                    productDetailsArrayList.add("Yuki YK125-15");
                    productDetailsArrayList.add("Yuki YK125-28H");
                    productDetailsArrayList.add("Yuki YK 125-7D Apollo");
                    productDetailsArrayList.add("Yuki YK 125-7 Rex");
                    productDetailsArrayList.add("Yuki YK 150-9 Goldfox");
                    productDetailsArrayList.add("Yuki YK 150-9 Goldfox-S");
                    productDetailsArrayList.add("Yuki YK150-G");
                    productDetailsArrayList.add("Yuki YK 150-G Üstad ");
                    productDetailsArrayList.add("Yuki YK150T-20");
                    productDetailsArrayList.add("Yuki YK 150 T-3");
                    productDetailsArrayList.add("Yuki YK-162 Goldfox");
                    productDetailsArrayList.add("Yuki YK-162 Goldfox-S");
                    productDetailsArrayList.add("Yuki YK-180M Modify");
                    productDetailsArrayList.add("Yuki YK-19 Midilli Cargo");
                    productDetailsArrayList.add("Yuki YK200GY-2");
                    productDetailsArrayList.add("Yuki YK-22 Rüzgar");
                    productDetailsArrayList.add("Yuki YK-24 Aydos ");
                    productDetailsArrayList.add("Yuki YK250");
                    productDetailsArrayList.add("Yuki YK250-21 R-Samurai ");
                    productDetailsArrayList.add("Yuki YK250-4 ");
                    productDetailsArrayList.add("Yuki YK250GY-7 İzci ");
                    productDetailsArrayList.add("Yuki YK- 250 ZH Ayder ");
                    productDetailsArrayList.add("Yuki YK-25 Midilli");
                    productDetailsArrayList.add("Yuki YK50QT-2A");
                    productDetailsArrayList.add("Yuki YX 200");
                    productDetailsArrayList.add("Yuki ZN-100T-E5 Legend ");
                    productDetailsArrayList.add("Yuki ZN-150T-E5 Legend");
                    productDetailsArrayList.add("Yuki ZY125-15A Scrambler ");
                    productDetailsArrayList.add("Diğer");

                }else {
                    productDetailsArrayList.add("Alfa Romeo Giulia");
                    productDetailsArrayList.add("Alfa Romeo Giulia Quadrifoglio");
                    productDetailsArrayList.add("Alfa Romeo Giulietta ");
                    productDetailsArrayList.add("Alfa Romeo 145 ");
                    productDetailsArrayList.add("Alfa Romeo 146 ");
                    productDetailsArrayList.add("Alfa Romeo 147 ");
                    productDetailsArrayList.add("Alfa Romeo 155");
                    productDetailsArrayList.add("Alfa Romeo 156 (");
                    productDetailsArrayList.add("Alfa Romeo 159 ");
                    productDetailsArrayList.add("Alfa Romeo 164");
                    productDetailsArrayList.add("Alfa Romeo 166 ");
                    productDetailsArrayList.add("Alfa Romeo 33");
                    productDetailsArrayList.add("Alfa Romeo 75");
                    productDetailsArrayList.add("Alfa Romeo Brera");
                    productDetailsArrayList.add("Alfa Romeo GT");
                    productDetailsArrayList.add("Alfa Romeo GTV");
                    productDetailsArrayList.add("Alfa Romeo MiTo ");
                    productDetailsArrayList.add("Aston Martin DB11");
                    productDetailsArrayList.add("Aston Martin DB7 ");
                    productDetailsArrayList.add("Aston Martin DB9 ");
                    productDetailsArrayList.add("Aston Martin DBS ");
                    productDetailsArrayList.add("Aston Martin Rapide ");
                    productDetailsArrayList.add("Aston Martin Vanquish ");
                    productDetailsArrayList.add("Aston Martin Vantage ");
                    productDetailsArrayList.add("Aston Martin Virage ");
                    productDetailsArrayList.add("Audi A1 ");
                    productDetailsArrayList.add("Audi A2");
                    productDetailsArrayList.add("Audi A3");
                    productDetailsArrayList.add("Audi A4");
                    productDetailsArrayList.add("Audi A5");
                    productDetailsArrayList.add("Audi A6");
                    productDetailsArrayList.add("Audi A7 ");
                    productDetailsArrayList.add("Audi A8 ");
                    productDetailsArrayList.add("Audi E-Tron GT ");
                    productDetailsArrayList.add("Audi R8");
                    productDetailsArrayList.add("Audi RS ");
                    productDetailsArrayList.add("Audi S  Series ");
                    productDetailsArrayList.add("Audi TT ");
                    productDetailsArrayList.add("Audi TTS ");
                    productDetailsArrayList.add("Audi 80  Series ");
                    productDetailsArrayList.add("Audi 90  Series");
                    productDetailsArrayList.add("Audi 100 Series ");
                    productDetailsArrayList.add("Audi 200 Series");
                    productDetailsArrayList.add("Audi Cabrio");
                    productDetailsArrayList.add("BMW 1 Series ");
                    productDetailsArrayList.add("BMW 2 Series ");
                    productDetailsArrayList.add("BMW 3 Series ");
                    productDetailsArrayList.add("BMW 4 Series ");
                    productDetailsArrayList.add("BMW 5 Series ");
                    productDetailsArrayList.add("BMW 6 Series ");
                    productDetailsArrayList.add("BMW 7 Series ");
                    productDetailsArrayList.add("BMW 8 Series ");
                    productDetailsArrayList.add("BMW i Series ");
                    productDetailsArrayList.add("BMW M Series ");
                    productDetailsArrayList.add("BMW Z Series ");
                    productDetailsArrayList.add("Chevrolet Aveo ");
                    productDetailsArrayList.add("Chevrolet Camaro ");
                    productDetailsArrayList.add("Chevrolet Caprice ");
                    productDetailsArrayList.add("Chevrolet Celebrity ");
                    productDetailsArrayList.add("Chevrolet Corvette ");
                    productDetailsArrayList.add("Chevrolet Cruze ");
                    productDetailsArrayList.add("Chevrolet Epica ");
                    productDetailsArrayList.add("Chevrolet Evanda ");
                    productDetailsArrayList.add("Chevrolet Impala ");
                    productDetailsArrayList.add("Chevrolet Kalos ");
                    productDetailsArrayList.add("Chevrolet Lacetti");
                    productDetailsArrayList.add("Chevrolet Lumina ");
                    productDetailsArrayList.add("Chevrolet Malibu ");
                    productDetailsArrayList.add("Chevrolet Monte Carlo");
                    productDetailsArrayList.add("Chevrolet Nubira");
                    productDetailsArrayList.add("Chevrolet Rezzo");
                    productDetailsArrayList.add("Chevrolet Spark ");
                    productDetailsArrayList.add("Citroën AMı ");
                    productDetailsArrayList.add("Citroën C-Elysée ");
                    productDetailsArrayList.add("Citroën C ");
                    productDetailsArrayList.add("Citroën C2 ");
                    productDetailsArrayList.add("Citroën C3  ");
                    productDetailsArrayList.add("Citroën C3 Picasso ");
                    productDetailsArrayList.add("Citroën C4  ");
                    productDetailsArrayList.add("Citroën C4 Grand Picasso ");
                    productDetailsArrayList.add("Citroën C4 Picasso ");
                    productDetailsArrayList.add("Citroën C4  ");
                    productDetailsArrayList.add("Citroën e-C4X ");
                    productDetailsArrayList.add("Citroën e-C4  ");
                    productDetailsArrayList.add("Citroën C5 ");
                    productDetailsArrayList.add("Citroën Saxo ");
                    productDetailsArrayList.add("Cupra Leon ");
                    productDetailsArrayList.add("Dacia Jogger");
                    productDetailsArrayList.add("Dacia Lodgy ");
                    productDetailsArrayList.add("Dacia Logan ");
                    productDetailsArrayList.add("Dacia Sandero ");
                    productDetailsArrayList.add("Dacia Nova");
                    productDetailsArrayList.add("Dacia Solenza ");
                    productDetailsArrayList.add("Fiat 124 Spider");
                    productDetailsArrayList.add("Fiat Albea (");
                    productDetailsArrayList.add("Fiat Brava");
                    productDetailsArrayList.add("Fiat Bravo");
                    productDetailsArrayList.add("Fiat 126 Bis");
                    productDetailsArrayList.add("Fiat Coupe");
                    productDetailsArrayList.add("Fiat Croroma");
                    productDetailsArrayList.add("Fiat 500");
                    productDetailsArrayList.add("Fiat Egea ");
                    productDetailsArrayList.add("Fiat Idea");
                    productDetailsArrayList.add("Fiat Linea ");
                    productDetailsArrayList.add("Fiat Marea");
                    productDetailsArrayList.add("Fiat Mirafio");
                    productDetailsArrayList.add("Fiat Multip");
                    productDetailsArrayList.add("Fiat Palio ");
                    productDetailsArrayList.add("Fiat Panda");
                    productDetailsArrayList.add("Fiat Punto ");
                    productDetailsArrayList.add("Fiat Rega");
                    productDetailsArrayList.add("Fiat Siena");
                    productDetailsArrayList.add("Fiat Stilo");
                    productDetailsArrayList.add("Fiat Tempra ");
                    productDetailsArrayList.add("Fiat Tipo ");
                    productDetailsArrayList.add("Fiat Ulysse");
                    productDetailsArrayList.add("Fiat Uno");
                    productDetailsArrayList.add("Ford B-Max ");
                    productDetailsArrayList.add("Ford C-Max ");
                    productDetailsArrayList.add("Ford Escort ");
                    productDetailsArrayList.add("Ford Fiesta ");
                    productDetailsArrayList.add("Ford Focus ");
                    productDetailsArrayList.add("Ford Fusion ");
                    productDetailsArrayList.add("Ford Galaxy");
                    productDetailsArrayList.add("Ford Grand C-Max");
                    productDetailsArrayList.add("Ford Ka ");
                    productDetailsArrayList.add("Ford Mondeo ");
                    productDetailsArrayList.add("Ford Mustang ");
                    productDetailsArrayList.add("Ford S-Max");
                    productDetailsArrayList.add("Ford Tauru");
                    productDetailsArrayList.add("Ford Couga");
                    productDetailsArrayList.add("Ford Crown Victori");
                    productDetailsArrayList.add("Ford Festiva");
                    productDetailsArrayList.add("Ford Granada");
                    productDetailsArrayList.add("Ford Orion");
                    productDetailsArrayList.add("Ford Probe");
                    productDetailsArrayList.add("Ford Puma");
                    productDetailsArrayList.add("Ford Scorpio");
                    productDetailsArrayList.add("Ford Sierra");
                    productDetailsArrayList.add("Ford Taunus ");
                    productDetailsArrayList.add("Honda Accord ");
                    productDetailsArrayList.add("Honda City ");
                    productDetailsArrayList.add("Honda Civic ");
                    productDetailsArrayList.add("Honda Concerto ");
                    productDetailsArrayList.add("Honda CR-Z ");
                    productDetailsArrayList.add("Honda CRX ");
                    productDetailsArrayList.add("Honda E ");
                    productDetailsArrayList.add("Honda Integra (");
                    productDetailsArrayList.add("Honda Jazz ");
                    productDetailsArrayList.add("Honda Legend ");
                    productDetailsArrayList.add("Honda Prelude (");
                    productDetailsArrayList.add("Honda S-MX ");
                    productDetailsArrayList.add("Honda S2000 ");
                    productDetailsArrayList.add("Honda Shuttle ");
                    productDetailsArrayList.add("Honda Stream ");
                    productDetailsArrayList.add("Hyundai Accent ");
                    productDetailsArrayList.add("Hyundai Accent Blue ");
                    productDetailsArrayList.add("Hyundai Accent Era ");
                    productDetailsArrayList.add("Hyundai Atos");
                    productDetailsArrayList.add("Hyundai Coupe");
                    productDetailsArrayList.add("Hyundai Elantra ");
                    productDetailsArrayList.add("Hyundai Excel ");
                    productDetailsArrayList.add("Hyundai Genesis");
                    productDetailsArrayList.add("Hyundai Getz ");
                    productDetailsArrayList.add("Hyundai Grandeu");
                    productDetailsArrayList.add("Hyundai i10 ");
                    productDetailsArrayList.add("Hyundai i20 (5");
                    productDetailsArrayList.add("Hyundai i20 Active");
                    productDetailsArrayList.add("Hyundai i20 N");
                    productDetailsArrayList.add("Hyundai i20 Troy ");
                    productDetailsArrayList.add("Hyundai i30 ");
                    productDetailsArrayList.add("Hyundai i4");
                    productDetailsArrayList.add("Hyundai Ioniq");
                    productDetailsArrayList.add("Hyundai iX20");
                    productDetailsArrayList.add("Hyundai Matrix ");
                    productDetailsArrayList.add("Hyundai S-Coup");
                    productDetailsArrayList.add("Hyundai Sonata ");
                    productDetailsArrayList.add("Hyundai Traje");
                    productDetailsArrayList.add("Jaguar Daimler  ");
                    productDetailsArrayList.add("Jaguar F-Type ");
                    productDetailsArrayList.add("Jaguar S-Type  ");
                    productDetailsArrayList.add("Jaguar Sovereign ");
                    productDetailsArrayList.add("Jaguar X-Type  ");
                    productDetailsArrayList.add("Jaguar XE  ");
                    productDetailsArrayList.add("Jaguar XF  ");
                    productDetailsArrayList.add("Jaguar XJ  ");
                    productDetailsArrayList.add("Jaguar XJ6 ");
                    productDetailsArrayList.add("Jaguar XJR ");
                    productDetailsArrayList.add("Jaguar XK8 ");
                    productDetailsArrayList.add("Jaguar XKR ");
                    productDetailsArrayList.add("Kia Capital  ");
                    productDetailsArrayList.add("Kia Carens ");
                    productDetailsArrayList.add("Kia Carnival  ");
                    productDetailsArrayList.add("Kia Ceed  ");
                    productDetailsArrayList.add("Kia Cerato  ");
                    productDetailsArrayList.add("Kia Clarus ");
                    productDetailsArrayList.add("Kia Magentis  ");
                    productDetailsArrayList.add("Kia Opirus ");
                    productDetailsArrayList.add("Kia Optima  ");
                    productDetailsArrayList.add("Kia Picanto  ");
                    productDetailsArrayList.add("Kia Pride  ");
                    productDetailsArrayList.add("Kia Pro Ceed  ");
                    productDetailsArrayList.add("Kia Rio  ");
                    productDetailsArrayList.add("Kia Sephia  ");
                    productDetailsArrayList.add("Kia Shuma  ");
                    productDetailsArrayList.add("Kia Stinger ");
                    productDetailsArrayList.add("Kia Venga  ");
                    productDetailsArrayList.add("Mazda 2 ");
                    productDetailsArrayList.add("Mazda 3  ");
                    productDetailsArrayList.add("Mazda 5 ");
                    productDetailsArrayList.add("Mazda 6 ");
                    productDetailsArrayList.add("Mazda MP ");
                    productDetailsArrayList.add("Mazda MX ");
                    productDetailsArrayList.add("Mazda Premac ");
                    productDetailsArrayList.add("Mazda 12 ");
                    productDetailsArrayList.add("Mazda 323  ");
                    productDetailsArrayList.add("Mazda 626  ");
                    productDetailsArrayList.add("Mazda 92 ");
                    productDetailsArrayList.add("Mazda Lantis ");
                    productDetailsArrayList.add("Mazda RX ");
                    productDetailsArrayList.add("Mazda Xedo ");
                    productDetailsArrayList.add("Mercedes Benz A Series ");
                    productDetailsArrayList.add("Mercedes Benz AMG GT ");
                    productDetailsArrayList.add("Mercedes Benz B Series ");
                    productDetailsArrayList.add("Mercedes Benz C Series ");
                    productDetailsArrayList.add("Mercedes Benz CL ");
                    productDetailsArrayList.add("Mercedes Benz CLA ");
                    productDetailsArrayList.add("Mercedes Benz CLC ");
                    productDetailsArrayList.add("Mercedes Benz CLK ");
                    productDetailsArrayList.add("Mercedes Benz CLS ");
                    productDetailsArrayList.add("Mercedes Benz E Series ");
                    productDetailsArrayList.add("Mercedes Benz EQE ");
                    productDetailsArrayList.add("Mercedes Benz EQS ");
                    productDetailsArrayList.add("Mercedes Benz Maybach S ");
                    productDetailsArrayList.add("Mercedes Benz R Series ");
                    productDetailsArrayList.add("Mercedes Benz S Series ");
                    productDetailsArrayList.add("Mercedes Benz SL ");
                    productDetailsArrayList.add("Mercedes Benz SLC ");
                    productDetailsArrayList.add("Mercedes Benz SLK ");
                    productDetailsArrayList.add("Mercedes Benz SLS AMG ");
                    productDetailsArrayList.add("Mercedes Benz 190 ");
                    productDetailsArrayList.add("Mercedes Benz 200 ");
                    productDetailsArrayList.add("Mercedes Benz 220 ");
                    productDetailsArrayList.add("Mercedes Benz 230 ");
                    productDetailsArrayList.add("Mercedes Benz 240 ");
                    productDetailsArrayList.add("Mercedes Benz 250 ");
                    productDetailsArrayList.add("Mercedes Benz 260 ");
                    productDetailsArrayList.add("Mercedes Benz 280 ");
                    productDetailsArrayList.add("Mercedes Benz 300 ");
                    productDetailsArrayList.add("Mercedes Benz 380 ");
                    productDetailsArrayList.add("Mercedes Benz 420 ");
                    productDetailsArrayList.add("Mercedes Benz 500 ");
                    productDetailsArrayList.add("200 SX ");
                    productDetailsArrayList.add("300 ZX");
                    productDetailsArrayList.add("350 Z");
                    productDetailsArrayList.add("Almera ");
                    productDetailsArrayList.add("Altima");
                    productDetailsArrayList.add("Cedric");
                    productDetailsArrayList.add("GT-R ");
                    productDetailsArrayList.add("Laurel Altima ");
                    productDetailsArrayList.add("Maxima ");
                    productDetailsArrayList.add("Micra .");
                    productDetailsArrayList.add("Note ");
                    productDetailsArrayList.add("NX Coupe ");
                    productDetailsArrayList.add("Primera ");
                    productDetailsArrayList.add("Pulsar ");
                    productDetailsArrayList.add("Sunny ");
                    productDetailsArrayList.add("Teana");
                    productDetailsArrayList.add("Tino");
                    productDetailsArrayList.add("Opel Adam");
                    productDetailsArrayList.add("Opel Agila ");
                    productDetailsArrayList.add("Opel Ascona ");
                    productDetailsArrayList.add("Opel Astra ");
                    productDetailsArrayList.add("Opel Calibra");
                    productDetailsArrayList.add("Opel Cascada");
                    productDetailsArrayList.add("Opel Corsa ");
                    productDetailsArrayList.add("Opel Corsa-e ");
                    productDetailsArrayList.add("Opel GT (Roadster)");
                    productDetailsArrayList.add("Opel Insignia ");
                    productDetailsArrayList.add("Opel Kadett ");
                    productDetailsArrayList.add("Opel Karl");
                    productDetailsArrayList.add("Opel Manta");
                    productDetailsArrayList.add("Opel Meriva ");
                    productDetailsArrayList.add("Opel Omega ");
                    productDetailsArrayList.add("Opel Rekord");
                    productDetailsArrayList.add("Opel Senator");
                    productDetailsArrayList.add("Opel Signum");
                    productDetailsArrayList.add("Opel Tigra ");
                    productDetailsArrayList.add("Opel Vectra ");
                    productDetailsArrayList.add("Opel Zafira ");
                    productDetailsArrayList.add("Peugot 106 ");
                    productDetailsArrayList.add("Peugot 107");
                    productDetailsArrayList.add("Peugot 205");
                    productDetailsArrayList.add("Peugot 206 ");
                    productDetailsArrayList.add("Peugot 206 + ");
                    productDetailsArrayList.add("Peugot 207 ");
                    productDetailsArrayList.add("Peugot 208 ");
                    productDetailsArrayList.add("Peugot 301 ");
                    productDetailsArrayList.add("Peugot 30");
                    productDetailsArrayList.add("Peugot 306 ");
                    productDetailsArrayList.add("Peugot 307 ");
                    productDetailsArrayList.add("Peugot 308 ");
                    productDetailsArrayList.add("Peugot 39");
                    productDetailsArrayList.add("Peugot 405");
                    productDetailsArrayList.add("Peugot 406 ");
                    productDetailsArrayList.add("Peugot 407 ");
                    productDetailsArrayList.add("Peugot 508 ");
                    productDetailsArrayList.add("Peugot 605");
                    productDetailsArrayList.add("Peugot 607");
                    productDetailsArrayList.add("Peugot 80");
                    productDetailsArrayList.add("Peugot 80");
                    productDetailsArrayList.add("Peugot RCZ ");
                    productDetailsArrayList.add("Porsche 718  ");
                    productDetailsArrayList.add("Porsche 911  ");
                    productDetailsArrayList.add("Porsche 928 ");
                    productDetailsArrayList.add("Porsche Boxster  ");
                    productDetailsArrayList.add("Porsche Cayman  ");
                    productDetailsArrayList.add("Porsche Panamera ");
                    productDetailsArrayList.add("Porsche Taycan  ");
                    productDetailsArrayList.add("Renault Clio ");
                    productDetailsArrayList.add("Renault Espace");
                    productDetailsArrayList.add("Renault Fluence ");
                    productDetailsArrayList.add("Renault Fluence Z.E");
                    productDetailsArrayList.add("Renault Grand Espac");
                    productDetailsArrayList.add("Renault Grand Scenic ");
                    productDetailsArrayList.add("Renault Laguna ");
                    productDetailsArrayList.add("Renault Latitude ");
                    productDetailsArrayList.add("Renault Megane ");
                    productDetailsArrayList.add("Renault Modus");
                    productDetailsArrayList.add("Renault Safrane");
                    productDetailsArrayList.add("Renault Scenic ");
                    productDetailsArrayList.add("Renault Symbol (6");
                    productDetailsArrayList.add("Renault Taliant ");
                    productDetailsArrayList.add("Renault Talisman ");
                    productDetailsArrayList.add("Renault Twingo ");
                    productDetailsArrayList.add("Renault Twizy");
                    productDetailsArrayList.add("Renault Vel Satis");
                    productDetailsArrayList.add("Renault ZOE ");
                    productDetailsArrayList.add("Renault R 5");
                    productDetailsArrayList.add("Renault R 9 ");
                    productDetailsArrayList.add("Renault R 11 ");
                    productDetailsArrayList.add("Renault R 12 ");
                    productDetailsArrayList.add("Renault R 19 ");
                    productDetailsArrayList.add("Seat Alhambra ");
                    productDetailsArrayList.add("Seat Altea ");
                    productDetailsArrayList.add("Seat Arosa");
                    productDetailsArrayList.add("Seat Cordoba ");
                    productDetailsArrayList.add("Seat Exeo ");
                    productDetailsArrayList.add("Seat Ibiza ");
                    productDetailsArrayList.add("Seat Leon ");
                    productDetailsArrayList.add("Seat Marbella");
                    productDetailsArrayList.add("Seat Toledo ");
                    productDetailsArrayList.add("Skoda Citigo");
                    productDetailsArrayList.add("Skoda Fabia ");
                    productDetailsArrayList.add("Skoda Favorit ");
                    productDetailsArrayList.add("Skoda Felicia ");
                    productDetailsArrayList.add("Skoda Forman ");
                    productDetailsArrayList.add("Skoda Octavia ");
                    productDetailsArrayList.add("Skoda Rapid ");
                    productDetailsArrayList.add("Skoda Roomster");
                    productDetailsArrayList.add("Skoda Scala ");
                    productDetailsArrayList.add("Skoda Superb ");
                    productDetailsArrayList.add("Subaru BRZ ");
                    productDetailsArrayList.add("Subaru Impreza ");
                    productDetailsArrayList.add("Subaru Legacy ");
                    productDetailsArrayList.add("Subaru Levorg ");
                    productDetailsArrayList.add("Subaru Justy ");
                    productDetailsArrayList.add("Subaru Vivio ");
                    productDetailsArrayList.add("Suzuki Alto ");
                    productDetailsArrayList.add("Suzuki Baleno ");
                    productDetailsArrayList.add("Suzuki Splash ");
                    productDetailsArrayList.add("Suzuki Swift ");
                    productDetailsArrayList.add("Suzuki SX4 ");
                    productDetailsArrayList.add("Suzuki Wagon R");
                    productDetailsArrayList.add("Suzuki Liana");
                    productDetailsArrayList.add("Suzuki Maruti ");
                    productDetailsArrayList.add("Tesla Model 3 ");
                    productDetailsArrayList.add("Tesla Model S ");
                    productDetailsArrayList.add("Tesla Model X ");
                    productDetailsArrayList.add("Tesla Model Y ");
                    productDetailsArrayList.add("Tofas Doğan ");
                    productDetailsArrayList.add("Tofas Kartal ");
                    productDetailsArrayList.add("Tofas Murat");
                    productDetailsArrayList.add("Tofas Şahin ");
                    productDetailsArrayList.add("Tofas Serçe");
                    productDetailsArrayList.add("Toyata Auris ");
                    productDetailsArrayList.add("Toyata Avensis ");
                    productDetailsArrayList.add("Toyata Aygo ");
                    productDetailsArrayList.add("Toyata Camry ");
                    productDetailsArrayList.add("Toyata Carina ");
                    productDetailsArrayList.add("Toyata Celica ");
                    productDetailsArrayList.add("Toyata Corolla ");
                    productDetailsArrayList.add("Toyata Corona ");
                    productDetailsArrayList.add("Toyata Cressida ");
                    productDetailsArrayList.add("Toyata Estima ");
                    productDetailsArrayList.add("Toyata GT86 ");
                    productDetailsArrayList.add("Toyata Mark 2 ");
                    productDetailsArrayList.add("Toyata MR2 ");
                    productDetailsArrayList.add("Toyata Picnic ");
                    productDetailsArrayList.add("Toyata Previa ");
                    productDetailsArrayList.add("Toyata Prius ");
                    productDetailsArrayList.add("Toyata Starlet (");
                    productDetailsArrayList.add("Toyata Supra ");
                    productDetailsArrayList.add("Toyata Urban Cruiser ");
                    productDetailsArrayList.add("Toyata Verso ");
                    productDetailsArrayList.add("Toyata Yaris ");
                    productDetailsArrayList.add("Volkswagen Beetle ");
                    productDetailsArrayList.add("Volkswagen Bora ");
                    productDetailsArrayList.add("Volkswagen EOS ");
                    productDetailsArrayList.add("Volkswagen Golf ");
                    productDetailsArrayList.add("Volkswagen ID.3");
                    productDetailsArrayList.add("Volkswagen Jetta ");
                    productDetailsArrayList.add("Volkswagen Lupo ");
                    productDetailsArrayList.add("Volkswagen Passat ");
                    productDetailsArrayList.add("Volkswagen Passat Alltrack ");
                    productDetailsArrayList.add("Volkswagen Passat Variant ");
                    productDetailsArrayList.add("Volkswagen Phaeton");
                    productDetailsArrayList.add("Volkswagen Polo ");
                    productDetailsArrayList.add("Volkswagen Routan");
                    productDetailsArrayList.add("Volkswagen Santana");
                    productDetailsArrayList.add("Volkswagen Scirocco ");
                    productDetailsArrayList.add("Volkswagen Sharan ");
                    productDetailsArrayList.add("Volkswagen Touran ");
                    productDetailsArrayList.add("Volkswagen Up Club");
                    productDetailsArrayList.add("Volkswagen VW CC");
                    productDetailsArrayList.add("Volvo C30 ");
                    productDetailsArrayList.add("Volvo C70 ");
                    productDetailsArrayList.add("Volvo S40 ");
                    productDetailsArrayList.add("Volvo S60 ");
                    productDetailsArrayList.add("Volvo S70 ");
                    productDetailsArrayList.add("Volvo S80 ");
                    productDetailsArrayList.add("Volvo S90 ");
                    productDetailsArrayList.add("Volvo V40 ");
                    productDetailsArrayList.add("Volvo V40 Cross Country ");
                    productDetailsArrayList.add("Volvo V50 ");
                    productDetailsArrayList.add("Volvo V60 ");
                    productDetailsArrayList.add("Volvo V60 Cross Country ");
                    productDetailsArrayList.add("Volvo V70 ");
                    productDetailsArrayList.add("Volvo V90 Cross Country ");
                    productDetailsArrayList.add("Volvo 440");
                    productDetailsArrayList.add("Volvo 460");
                    productDetailsArrayList.add("Volvo 480");
                    productDetailsArrayList.add("Volvo 740");
                    productDetailsArrayList.add("Volvo 850 ");
                    productDetailsArrayList.add("Volvo 940 ");
                    productDetailsArrayList.add("Volvo 960");
                    productDetailsArrayList.add("Alfa Romeo Stelvio ");
                    productDetailsArrayList.add("Alfa Romeo Tonale ");
                    productDetailsArrayList.add("Audi E-Tron ");
                    productDetailsArrayList.add("Audi E-Tron Sportback ");
                    productDetailsArrayList.add("Audi Q2 ");
                    productDetailsArrayList.add("Audi Q3 ");
                    productDetailsArrayList.add("Audi Q3 Sportback ");
                    productDetailsArrayList.add("Audi Q4 ");
                    productDetailsArrayList.add("Audi Q5 3");
                    productDetailsArrayList.add("Audi Q5 Sportback ");
                    productDetailsArrayList.add("Audi Q7 ");
                    productDetailsArrayList.add("Audi Q8 ");
                    productDetailsArrayList.add("Audi Q8 e-tron ");
                    productDetailsArrayList.add("BMW iX ");
                    productDetailsArrayList.add("BMW iX1");
                    productDetailsArrayList.add("BMW iX3 ");
                    productDetailsArrayList.add("BMW X1 ");
                    productDetailsArrayList.add("BMW X2");
                    productDetailsArrayList.add("BMW X3 ");
                    productDetailsArrayList.add("BMW X4");
                    productDetailsArrayList.add("BMW X5 ");
                    productDetailsArrayList.add("BMW X6 ");
                    productDetailsArrayList.add("BMW X");
                    productDetailsArrayList.add("Chevrolet Avalanche");
                    productDetailsArrayList.add("Chevrolet Blazer ");
                    productDetailsArrayList.add("Chevrolet Captiva ");
                    productDetailsArrayList.add("Chevrolet Equinox");
                    productDetailsArrayList.add("Chevrolet Silverado ");
                    productDetailsArrayList.add("Chevrolet Suburban");
                    productDetailsArrayList.add("Chevrolet Tahoe ");
                    productDetailsArrayList.add("Chevrolet Tracker");
                    productDetailsArrayList.add("Chevrolet Trax ");
                    productDetailsArrayList.add("Chevrolet Menlo");
                    productDetailsArrayList.add("Chevrolet HHR");
                    productDetailsArrayList.add("Citroen C3 AirCross ");
                    productDetailsArrayList.add("Citroen C4 Cactus ");
                    productDetailsArrayList.add("Citroen C5 AirCross ");
                    productDetailsArrayList.add("Cupra Formentor");
                    productDetailsArrayList.add("Dacia Duster ");
                    productDetailsArrayList.add("Dacia Logan PickuP");
                    productDetailsArrayList.add("Dacia Sandero Stepway ");
                    productDetailsArrayList.add("Fiat Egea Cross ");
                    productDetailsArrayList.add("Fiat Freemont ");
                    productDetailsArrayList.add("Fiat Fullback ");
                    productDetailsArrayList.add("Fiat Sedici ");
                    productDetailsArrayList.add("Fiat 500 X ");
                    productDetailsArrayList.add("Ford EcoSport ");
                    productDetailsArrayList.add("Ford Edge");
                    productDetailsArrayList.add("Ford Expedition");
                    productDetailsArrayList.add("Ford Explorer ");
                    productDetailsArrayList.add("Ford F ");
                    productDetailsArrayList.add("Ford Flex");
                    productDetailsArrayList.add("Ford Kuga ");
                    productDetailsArrayList.add("Ford Mustang Mach-E");
                    productDetailsArrayList.add("Ford Puma ");
                    productDetailsArrayList.add("Ford Ranger ");
                    productDetailsArrayList.add("Ford Ranger Raptor ");
                    productDetailsArrayList.add("Ford Bronco");
                    productDetailsArrayList.add("Ford Freestyle");
                    productDetailsArrayList.add("Ford Maverick");
                    productDetailsArrayList.add("Honda CR-V ");
                    productDetailsArrayList.add("Honda HR-V");
                    productDetailsArrayList.add("Hyundai Bayon ");
                    productDetailsArrayList.add("Hyundai Ioniq ");
                    productDetailsArrayList.add("Hyundai ix35 ");
                    productDetailsArrayList.add("Hyundai ix55");
                    productDetailsArrayList.add("Hyundai Galloper ");
                    productDetailsArrayList.add("Hyundai Kona ");
                    productDetailsArrayList.add("Hyundai Santa Fe (");
                    productDetailsArrayList.add("Hyundai Terracan");
                    productDetailsArrayList.add("Hyundai Tucson ");
                    productDetailsArrayList.add("Jaguar E-Pace");
                    productDetailsArrayList.add("Jaguar F-Pace ");
                    productDetailsArrayList.add("Jaguar I-Pace");
                    productDetailsArrayList.add("Jeep Cherokee ");
                    productDetailsArrayList.add("Jeep Commander");
                    productDetailsArrayList.add("Jeep Compass ");
                    productDetailsArrayList.add("Jeep Grand Cherokee ");
                    productDetailsArrayList.add("Jeep Patriot");
                    productDetailsArrayList.add("Jeep Renegade ");
                    productDetailsArrayList.add("Jeep Wrangler ");
                    productDetailsArrayList.add("Jeep CJ");
                    productDetailsArrayList.add("Land Rover Defender");
                    productDetailsArrayList.add("Land Rover Discovery");
                    productDetailsArrayList.add("Land Rover Discovery Sport");
                    productDetailsArrayList.add("Land Rover Range Rover ");
                    productDetailsArrayList.add("Land Rover Range Rover Evoque");
                    productDetailsArrayList.add("Land Rover Range Rover Sport");
                    productDetailsArrayList.add("Land Rover Range Rover Velar");
                    productDetailsArrayList.add("Land Rover Freelander");
                    productDetailsArrayList.add("Mazda CX-3 ");
                    productDetailsArrayList.add("Mazda CX-5 ");
                    productDetailsArrayList.add("Mazda CX-9 ");
                    productDetailsArrayList.add("Mazda B Series ");
                    productDetailsArrayList.add("Mazda Tribute ");
                    productDetailsArrayList.add("Mercedes Benz EQA");
                    productDetailsArrayList.add("Mercedes Benz EQB ");
                    productDetailsArrayList.add("Mercedes Benz EQC ");
                    productDetailsArrayList.add("Mercedes Benz EQS SUV");
                    productDetailsArrayList.add("Mercedes Benz G Series ");
                    productDetailsArrayList.add("Mercedes Benz GL ");
                    productDetailsArrayList.add("Mercedes Benz GLA ");
                    productDetailsArrayList.add("Mercedes Benz GLB ");
                    productDetailsArrayList.add("Mercedes Benz GLC ");
                    productDetailsArrayList.add("Mercedes Benz GLC Coupe ");
                    productDetailsArrayList.add("Mercedes Benz GLE ");
                    productDetailsArrayList.add("Mercedes Benz GLE Coupe ");
                    productDetailsArrayList.add("Mercedes Benz GLK ");
                    productDetailsArrayList.add("Mercedes Benz GLS ");
                    productDetailsArrayList.add("Mercedes Benz ML ");
                    productDetailsArrayList.add("Mercedes Benz X ");
                    productDetailsArrayList.add("Mitsubishi ASX ");
                    productDetailsArrayList.add("Mitsubishi Eclipse Cross ");
                    productDetailsArrayList.add("Mitsubishi L 200 ");
                    productDetailsArrayList.add("Mitsubishi Outlander ");
                    productDetailsArrayList.add("Mitsubishi Pajero ");
                    productDetailsArrayList.add("Nissan Juke ");
                    productDetailsArrayList.add("Nissan Navara ");
                    productDetailsArrayList.add("Nissan Pathfinder");
                    productDetailsArrayList.add("Nissan Patrol");
                    productDetailsArrayList.add("Nissan Qashqai ");
                    productDetailsArrayList.add("Nissan Qashqai+2");
                    productDetailsArrayList.add("Nissan X-Trail ");
                    productDetailsArrayList.add("Nissan Country");
                    productDetailsArrayList.add("Nissan Muran");
                    productDetailsArrayList.add("Nissan Rally Raid");
                    productDetailsArrayList.add("Nissan Skystar ");
                    productDetailsArrayList.add("Nissan Terrano");
                    productDetailsArrayList.add("Opel Antara");
                    productDetailsArrayList.add("Opel Crossland ");
                    productDetailsArrayList.add("Opel Crossland X ");
                    productDetailsArrayList.add("Opel Grandland ");
                    productDetailsArrayList.add("Opel Grandland X ");
                    productDetailsArrayList.add("Opel Mokka ");
                    productDetailsArrayList.add("Opel Mokka-e");
                    productDetailsArrayList.add("Opel Mokka X ");
                    productDetailsArrayList.add("Opel Frontera");
                    productDetailsArrayList.add("Opel Montere");
                    productDetailsArrayList.add("Peugeot 408 ");
                    productDetailsArrayList.add("Peugeot e-2008");
                    productDetailsArrayList.add("Peugeot 2008 ");
                    productDetailsArrayList.add("Peugeot 3008 ");
                    productDetailsArrayList.add("Peugeot 5008 ");
                    productDetailsArrayList.add("Peugeot 4007");
                    productDetailsArrayList.add("Porsche Cayenne ");
                    productDetailsArrayList.add("Porsche Cayenne Coupe ");
                    productDetailsArrayList.add("Porsche Macan ");
                    productDetailsArrayList.add("Renault Austral ");
                    productDetailsArrayList.add("Renault Captur ");
                    productDetailsArrayList.add("Renault Kadjar ");
                    productDetailsArrayList.add("Renault Koleos ");
                    productDetailsArrayList.add("Renault Scenic RX4 ");
                    productDetailsArrayList.add("Seat Tarraco ");
                    productDetailsArrayList.add("Seat Arona ");
                    productDetailsArrayList.add("Seat Ateca ");
                    productDetailsArrayList.add("Skoda Kamiq ");
                    productDetailsArrayList.add("Skoda Karoq ");
                    productDetailsArrayList.add("Skoda Kodiaq ");
                    productDetailsArrayList.add("Skoda Yeti ");
                    productDetailsArrayList.add("Skoda Felicia Pickup");
                    productDetailsArrayList.add("Ssang Yong Actyon");
                    productDetailsArrayList.add("Ssang Yong Actyon Sports ");
                    productDetailsArrayList.add("Ssang Yong Korando ");
                    productDetailsArrayList.add("Ssang Yong Korando Sports ");
                    productDetailsArrayList.add("Ssang Yong Kyron");
                    productDetailsArrayList.add("Ssang Yong Musso");
                    productDetailsArrayList.add("Ssang Yong Musso Grand");
                    productDetailsArrayList.add("Ssang Yong Rexton");
                    productDetailsArrayList.add("Ssang Yong Tivoli");
                    productDetailsArrayList.add("Ssang Yong XLV");
                    productDetailsArrayList.add("Ssang Yong Rodius");
                    productDetailsArrayList.add("Subaru Forester ");
                    productDetailsArrayList.add("Subaru Outback");
                    productDetailsArrayList.add("Subaru Solterr");
                    productDetailsArrayList.add("Subaru XV ");
                    productDetailsArrayList.add("Subaru Tribeca");
                    productDetailsArrayList.add("Suzuki Grand Vitara ");
                    productDetailsArrayList.add("Suzuki Jimny ");
                    productDetailsArrayList.add("Suzuki SJ ");
                    productDetailsArrayList.add("Suzuki S-Cross ");
                    productDetailsArrayList.add("Suzuki Samurai ");
                    productDetailsArrayList.add("Suzuki Vitara ");
                    productDetailsArrayList.add("Suzuki X-90");
                    productDetailsArrayList.add("TOGG T10X");
                    productDetailsArrayList.add("Toyata C-HR");
                    productDetailsArrayList.add("Toyata Corolla Cross ");
                    productDetailsArrayList.add("Toyata FJ Cruiser ");
                    productDetailsArrayList.add("Toyata Hilux ");
                    productDetailsArrayList.add("Toyata Land Cruiser ");
                    productDetailsArrayList.add("Toyata RAV4 ");
                    productDetailsArrayList.add("Toyata Yaris Cross ");
                    productDetailsArrayList.add("Toyata 4Runner");
                    productDetailsArrayList.add("Toyata Fortuner");
                    productDetailsArrayList.add("Toyata Sequoia");
                    productDetailsArrayList.add("Toyata Tacoma");
                    productDetailsArrayList.add("Volkswagen Amarok ");
                    productDetailsArrayList.add("Volkswagen ID.4");
                    productDetailsArrayList.add("Volkswagen ID.6");
                    productDetailsArrayList.add("Volkswagen T-Cross ");
                    productDetailsArrayList.add("Volkswagen T-Roc ");
                    productDetailsArrayList.add("Volkswagen Taigo ");
                    productDetailsArrayList.add("Volkswagen Tiguan ");
                    productDetailsArrayList.add("Volkswagen Tiguan AllSpace");
                    productDetailsArrayList.add("Volkswagen Touareg ");
                    productDetailsArrayList.add("Volvo Amarok ");
                    productDetailsArrayList.add("Volvo ID.4");
                    productDetailsArrayList.add("Volvo ID.6");
                    productDetailsArrayList.add("Volvo T-Cross ");
                    productDetailsArrayList.add("Volvo T-Roc ");
                    productDetailsArrayList.add("Volvo Taigo ");
                    productDetailsArrayList.add("Volvo Tiguan ");
                    productDetailsArrayList.add("Volvo Tiguan AllSpace");
                    productDetailsArrayList.add("Altai Carrier 110 Pro ");
                    productDetailsArrayList.add("Altai Carrier 125 ");
                    productDetailsArrayList.add("Altai F1Max 50 ");
                    productDetailsArrayList.add("Altai F1Max Pro 50 ");
                    productDetailsArrayList.add("Altai F1Max Pro 125 ");
                    productDetailsArrayList.add("Altai Misk 50 ");
                    productDetailsArrayList.add("Altai Tank 50 ");
                    productDetailsArrayList.add("Altai Tank S50");
                    productDetailsArrayList.add("Apachi 4V Cross");
                    productDetailsArrayList.add("Apachi Alfa 50 ");
                    productDetailsArrayList.add("Apachi Joy 125 ");
                    productDetailsArrayList.add("Apachi Myra ");
                    productDetailsArrayList.add("Apachi Nova 125 ");
                    productDetailsArrayList.add("Apachi Pusat ");
                    productDetailsArrayList.add("Apachi True 50 ");
                    productDetailsArrayList.add("Apachi True 125 ");
                    productDetailsArrayList.add("Apachi XRS ");
                    productDetailsArrayList.add("Apachi XZ 250R");
                    productDetailsArrayList.add("BMW C 600 Sport");
                    productDetailsArrayList.add("BMW C 650 GT ");
                    productDetailsArrayList.add("BMW C 650 Sport");
                    productDetailsArrayList.add("BMW C1");
                    productDetailsArrayList.add("BMW F 650 ");
                    productDetailsArrayList.add("BMW F 650 CS");
                    productDetailsArrayList.add("BMW F 650 GS ");
                    productDetailsArrayList.add("BMW F 650 GS Dakar");
                    productDetailsArrayList.add("BMW F 650ST");
                    productDetailsArrayList.add("BMW F 700 GS ");
                    productDetailsArrayList.add("BMW F 750 GS ");
                    productDetailsArrayList.add("BMW F 800 GS ");
                    productDetailsArrayList.add("BMW F 800 GS Adventure");
                    productDetailsArrayList.add("BMW F 800 GT");
                    productDetailsArrayList.add("BMW F 800 R");
                    productDetailsArrayList.add("BMW F 800S");
                    productDetailsArrayList.add("BMW F 850 GS ");
                    productDetailsArrayList.add("BMW F 850 GS Adventure");
                    productDetailsArrayList.add("BMW F 900 R");
                    productDetailsArrayList.add("BMW F 900 XR ");
                    productDetailsArrayList.add("BMW G 310 GS ");
                    productDetailsArrayList.add("BMW G 310 R ");
                    productDetailsArrayList.add("BMW G 650 GS");
                    productDetailsArrayList.add("BMW G 650 X Country");
                    productDetailsArrayList.add("BMW K 1100 LT");
                    productDetailsArrayList.add("BMW K 1200 GT");
                    productDetailsArrayList.add("BMW K 1200 LT");
                    productDetailsArrayList.add("BMW K 1200 R ");
                    productDetailsArrayList.add("BMW K 1200 RS");
                    productDetailsArrayList.add("BMW K 1200 S ");
                    productDetailsArrayList.add("BMW K 1300 GT");
                    productDetailsArrayList.add("BMW K 1300 R");
                    productDetailsArrayList.add("BMW K 1300 S");
                    productDetailsArrayList.add("BMW K 1600 B");
                    productDetailsArrayList.add("BMW K 1600 Bagger");
                    productDetailsArrayList.add("BMW K 1600 Grand America");
                    productDetailsArrayList.add("BMW K 1600 GT ");
                    productDetailsArrayList.add("BMW K 1600 GTL ");
                    productDetailsArrayList.add("Falcon Attack 50 ");
                    productDetailsArrayList.add("Falcon Attack 100 (");
                    productDetailsArrayList.add("Falcon Breeze 125");
                    productDetailsArrayList.add("Falcon Comfort 150 ");
                    productDetailsArrayList.add("Falcon Comfort 180");
                    productDetailsArrayList.add("Falcon Cooper 125 EFI ");
                    productDetailsArrayList.add("Falcon Cooper 50 ");
                    productDetailsArrayList.add("Falcon Cooper 50 EFI ");
                    productDetailsArrayList.add("Falcon Crazy 125");
                    productDetailsArrayList.add("Falcon Crown 150 ");
                    productDetailsArrayList.add("Falcon Custom 150");
                    productDetailsArrayList.add("Falcon Desert 277");
                    productDetailsArrayList.add("Falcon Dolphin 100 ");
                    productDetailsArrayList.add("Falcon Dolphin 100 EFI ");
                    productDetailsArrayList.add("Falcon Dolphin 125 EFI ");
                    productDetailsArrayList.add("Falcon Flash 100");
                    productDetailsArrayList.add("Falcon Freedom 250 ");
                    productDetailsArrayList.add("Falcon Leopar 222 ");
                    productDetailsArrayList.add("Falcon Lion 150");
                    productDetailsArrayList.add("Falcon Magic 50 ");
                    productDetailsArrayList.add("Falcon Martini 125 ");
                    productDetailsArrayList.add("Falcon Martini 50");
                    productDetailsArrayList.add("Falcon Magic 100 ");
                    productDetailsArrayList.add("Falcon Master 50 ");
                    productDetailsArrayList.add("Falcon Mexico 150 ");
                    productDetailsArrayList.add("Falcon Mocco 125 ");
                    productDetailsArrayList.add("Falcon Mocco 50 ");
                    productDetailsArrayList.add("Falcon Nitro 50 ");
                    productDetailsArrayList.add("Falcon Shark 188 ");
                    productDetailsArrayList.add("Falcon Sharp 150");
                    productDetailsArrayList.add("Falcon Sharp 170");
                    productDetailsArrayList.add("Honda Activa 100 ");
                    productDetailsArrayList.add("Honda Activa 110 ");
                    productDetailsArrayList.add("Honda Activa S ");
                    productDetailsArrayList.add("Honda ADV350 ");
                    productDetailsArrayList.add("Honda Bali");
                    productDetailsArrayList.add("Honda Beat ");
                    productDetailsArrayList.add("Honda C100 BIZ");
                    productDetailsArrayList.add("Honda C125 Super Cub ");
                    productDetailsArrayList.add("Honda C70 Super Cub");
                    productDetailsArrayList.add("Honda C90 Super Cub ");
                    productDetailsArrayList.add("Honda CB 125 ");
                    productDetailsArrayList.add("Honda CB 125 ACE ");
                    productDetailsArrayList.add("Honda CB 125E ");
                    productDetailsArrayList.add("Honda CB 125 F ");
                    productDetailsArrayList.add("Honda CB 125 R ");
                    productDetailsArrayList.add("Honda CB 250 R ");
                    productDetailsArrayList.add("Honda CB 500 F ");
                    productDetailsArrayList.add("Honda CB 600 F Hornet");
                    productDetailsArrayList.add("Honda CB 650 F ");
                    productDetailsArrayList.add("Honda CB 650 R ");
                    productDetailsArrayList.add("Honda CB 750 Hornet ");
                    productDetailsArrayList.add("Honda CB 900 Hornet");
                    productDetailsArrayList.add("Honda CR 85");
                    productDetailsArrayList.add("Honda CB 1000 R");
                    productDetailsArrayList.add("Honda CBF 150 (");
                    productDetailsArrayList.add("Honda CBF 250 ");
                    productDetailsArrayList.add("Honda CBF 500 ");
                    productDetailsArrayList.add("Honda CBF 600 ");
                    productDetailsArrayList.add("Honda CBF 1000 ");
                    productDetailsArrayList.add("Honda CBR 125 R ");
                    productDetailsArrayList.add("Honda CBR 250 R ");
                    productDetailsArrayList.add("Honda CBR 500R ");
                    productDetailsArrayList.add("Honda CBR 600 F ");
                    productDetailsArrayList.add("Honda CBR 600 FA");
                    productDetailsArrayList.add("Honda CBR 600 F Sport");
                    productDetailsArrayList.add("Honda CBR 600 RR ");
                    productDetailsArrayList.add("Honda CBR 650 F ");
                    productDetailsArrayList.add("Honda CBR 650 FA");
                    productDetailsArrayList.add("Honda CBR 650 R ");
                    productDetailsArrayList.add("Honda CBR 900 RR");
                    productDetailsArrayList.add("Honda CBR 929 RR");
                    productDetailsArrayList.add("Honda CBR 954 RR");
                    productDetailsArrayList.add("Honda CBR 1000 RR ");
                    productDetailsArrayList.add("Honda CBR 1000 RR-R Fireblade SP");
                    productDetailsArrayList.add("Honda CBR 1000 RR SP ");
                    productDetailsArrayList.add("Honda CBR 1100 XX ");
                    productDetailsArrayList.add("Honda CBX 250 Twister");
                    productDetailsArrayList.add("Honda CBX 550 F");
                    productDetailsArrayList.add("Honda CBX 750");
                    productDetailsArrayList.add("Honda CG 125 ");
                    productDetailsArrayList.add("Honda CGL 125 ");
                    productDetailsArrayList.add("Honda CL 250");
                    productDetailsArrayList.add("Honda CMX 250 Rebel");
                    productDetailsArrayList.add("Honda CR 250");
                    productDetailsArrayList.add("Honda CRF 150 R");
                    productDetailsArrayList.add("Honda CRF 250 L ");
                    productDetailsArrayList.add("Honda CRF 250 R");
                    productDetailsArrayList.add("Honda CRF 250 Rally ");
                    productDetailsArrayList.add("Honda CRF 250 X");
                    productDetailsArrayList.add("Honda CRF 450 R");
                    productDetailsArrayList.add("Honda CRF 450 X");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin ");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin Adventure Sports");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin Adventure Sports DCT");
                    productDetailsArrayList.add("Honda CRF1000L Africa Twin DCT ");
                    productDetailsArrayList.add("Kanuni BD100");
                    productDetailsArrayList.add("Kanuni Bobcat 150 ");
                    productDetailsArrayList.add("Kanuni Bora 125");
                    productDetailsArrayList.add("Kanuni Breton 125 ");
                    productDetailsArrayList.add("Kanuni Breton S ");
                    productDetailsArrayList.add("Kanuni Breton SL 125");
                    productDetailsArrayList.add("Kanuni BS 100 ");
                    productDetailsArrayList.add("Kanuni BS 125");
                    productDetailsArrayList.add("Kanuni Caracal 200 ");
                    productDetailsArrayList.add("Kanuni Cheetah 125");
                    productDetailsArrayList.add("Kanuni Classic 125 ");
                    productDetailsArrayList.add("Kanuni Cross 150");
                    productDetailsArrayList.add("Kanuni Cross 250");
                    productDetailsArrayList.add("Kanuni Cup 100 ");
                    productDetailsArrayList.add("Kanuni Cup 100 S ");
                    productDetailsArrayList.add("Kanuni Deer 152");
                    productDetailsArrayList.add("Kanuni Dolphin 100");
                    productDetailsArrayList.add("Kanuni Elite 100");
                    productDetailsArrayList.add("Kanuni Enduro 200");
                    productDetailsArrayList.add("Kanuni Explorer");
                    productDetailsArrayList.add("Kanuni FOX");
                    productDetailsArrayList.add("Kanuni Freedom 200");
                    productDetailsArrayList.add("Kanuni GT 170 ");
                    productDetailsArrayList.add("Kanuni GT 250");
                    productDetailsArrayList.add("Kanuni GV 170");
                    productDetailsArrayList.add("Kanuni GV 250");
                    productDetailsArrayList.add("Kanuni Hussar 125 ");
                    productDetailsArrayList.add("Kanuni Leopard");
                    productDetailsArrayList.add("Kanuni Mati 125 (");
                    productDetailsArrayList.add("Kanuni Merlin S ");
                    productDetailsArrayList.add("Kanuni Mini 50");
                    productDetailsArrayList.add("Kanuni Moped Turbo Sport");
                    productDetailsArrayList.add("Kanuni Motocar 200");
                    productDetailsArrayList.add("Kanuni Motorum 150 ");
                    productDetailsArrayList.add("Kanuni Nev 50 ");
                    productDetailsArrayList.add("Kanuni Phantom 180 ");
                    productDetailsArrayList.add("Kanuni Popcorn 90 ");
                    productDetailsArrayList.add("Kanuni Power GXL");
                    productDetailsArrayList.add("Kanuni Puma 150");
                    productDetailsArrayList.add("Kanuni Q100");
                    productDetailsArrayList.add("Kanuni Racer 200");
                    productDetailsArrayList.add("Kanuni Racer 200 S");
                    productDetailsArrayList.add("Kanuni Reha 250");
                    productDetailsArrayList.add("Kanuni Resa 125 (");
                    productDetailsArrayList.add("Kanuni Rokko 150");
                    productDetailsArrayList.add("Kanuni Ronny S ");
                    productDetailsArrayList.add("Kanuni RS 125 ");
                    productDetailsArrayList.add("Kanuni Ruby 100");
                    productDetailsArrayList.add("Kanuni S125T");
                    productDetailsArrayList.add("Kanuni S170T");
                    productDetailsArrayList.add("Kanuni Seha 150 ");
                    productDetailsArrayList.add("Kymco Agility 16+ 125i ");
                    productDetailsArrayList.add("Kymco Agility 16+ 150");
                    productDetailsArrayList.add("Kymco Agility 50");
                    productDetailsArrayList.add("Kymco Agility 125 (");
                    productDetailsArrayList.add("Kymco Agility Carry 125i");
                    productDetailsArrayList.add("Kymco Agility Carry 50i 4T");
                    productDetailsArrayList.add("Kymco Agility City 125 ");
                    productDetailsArrayList.add("Kymco Agility City 200 i ");
                    productDetailsArrayList.add("Kymco Agility S 125");
                    productDetailsArrayList.add("Kymco Ak 550");
                    productDetailsArrayList.add("Kymco Activ 100");
                    productDetailsArrayList.add("Kymco Aktiv 110");
                    productDetailsArrayList.add("Kymco CK 125 Pulsar ");
                    productDetailsArrayList.add("Kymco CV3");
                    productDetailsArrayList.add("Kymco Dink 200i ");
                    productDetailsArrayList.add("Kymco Dink R 150 ");
                    productDetailsArrayList.add("Kymco Downtown 250i ");
                    productDetailsArrayList.add("Kymco Downtown 300i ");
                    productDetailsArrayList.add("Kymco Downtown 350i ABS");
                    productDetailsArrayList.add("Kymco DT X360 ");
                    productDetailsArrayList.add("Kymco Grand Dink 150");
                    productDetailsArrayList.add("Kymco Grand Dink 250");
                    productDetailsArrayList.add("Kymco Grand Dink 250i");
                    productDetailsArrayList.add("Kymco Heroism 125");
                    productDetailsArrayList.add("Kymco KRV 200 ");
                    productDetailsArrayList.add("Kymco Like 125 ");
                    productDetailsArrayList.add("Kymco Like 200i");
                    productDetailsArrayList.add("Kymco Like 50 ");
                    productDetailsArrayList.add("Kymco Like S 125");
                    productDetailsArrayList.add("Kymco Like S 50");
                    productDetailsArrayList.add("Kymco Movie XL 125");
                    productDetailsArrayList.add("Kymco Movie XL 150");
                    productDetailsArrayList.add("Kymco People 125");
                    productDetailsArrayList.add("Kymco People S 125i");
                    productDetailsArrayList.add("Kuba Ajax 150");
                    productDetailsArrayList.add("Kuba Apricot 125");
                    productDetailsArrayList.add("Kuba Black Cat ");
                    productDetailsArrayList.add("Kuba Blueberry ");
                    productDetailsArrayList.add("Kuba Bluebird ");
                    productDetailsArrayList.add("Kuba Brilliant 125 ");
                    productDetailsArrayList.add("Kuba Brilliant 50 ");
                    productDetailsArrayList.add("Kuba Cargo");
                    productDetailsArrayList.add("Kuba CG 100 ");
                    productDetailsArrayList.add("Kuba CG 100/KM125-6 ");
                    productDetailsArrayList.add("Kuba CG150 ");
                    productDetailsArrayList.add("Kuba CG 50 ");
                    productDetailsArrayList.add("Kuba City Go ");
                    productDetailsArrayList.add("Kuba CR1 ");
                    productDetailsArrayList.add("Kuba CR1-S ");
                    productDetailsArrayList.add("Kuba Çita 100 ");
                    productDetailsArrayList.add("Kuba Çita 100R ");
                    productDetailsArrayList.add("Kuba Çita 100R Gold ");
                    productDetailsArrayList.add("Kuba Çita 100R Max Gold");
                    productDetailsArrayList.add("Kuba Çita 125 ");
                    productDetailsArrayList.add("Kuba Çita 125R Max");
                    productDetailsArrayList.add("Kuba Çita 125R Max Gold");
                    productDetailsArrayList.add("Kuba Çita 150R ");
                    productDetailsArrayList.add("Kuba Çita 150R Gold ");
                    productDetailsArrayList.add("Kuba Çita 170F ");
                    productDetailsArrayList.add("Kuba Çita 180-FC ");
                    productDetailsArrayList.add("Kuba Çita 180R ");
                    productDetailsArrayList.add("Kuba Çita 180R Gold ");
                    productDetailsArrayList.add("Kuba Çita 50R Gold (");
                    productDetailsArrayList.add("Kuba DB705");
                    productDetailsArrayList.add("Kuba Dragon 50 ");
                    productDetailsArrayList.add("Kuba Ege 100 ");
                    productDetailsArrayList.add("Kuba Ege 50 ");
                    productDetailsArrayList.add("Kuba Faswind 200R");
                    productDetailsArrayList.add("Kuba Fighter 50 ");
                    productDetailsArrayList.add("Kuba Fighter 80 ");
                    productDetailsArrayList.add("Kuba Filinta 100");
                    productDetailsArrayList.add("Kuba Filinta 200 ");
                    productDetailsArrayList.add("Kuba Gelli 125");
                    productDetailsArrayList.add("Kuba Golf 100");
                    productDetailsArrayList.add("Kuba Hasat 100 ");
                    productDetailsArrayList.add("Kuba Kargo 180 ");
                    productDetailsArrayList.add("Kuba KB100-6");
                    productDetailsArrayList.add("Kuba KB150-25 ");
                    productDetailsArrayList.add("Kuba KB150-25 Max");
                    productDetailsArrayList.add("Kuba KB150-9");
                    productDetailsArrayList.add("Kuba KB150T-B");
                    productDetailsArrayList.add("Kuba KEE 100 ");
                    productDetailsArrayList.add("Kuba KH100");
                    productDetailsArrayList.add("Kuba KH125-12D");
                    productDetailsArrayList.add("Kuba KH150-12D");
                    productDetailsArrayList.add("Kuba KM 100T-9");
                    productDetailsArrayList.add("Kuba KM125-6 ");
                    productDetailsArrayList.add("Kuba KR 139");
                    productDetailsArrayList.add("Mondial Air Time ");
                    productDetailsArrayList.add("Mondial Fury 110i ");
                    productDetailsArrayList.add("Mondial Virago 50 ");
                    productDetailsArrayList.add("Mondial 50 BeeStreet ");
                    productDetailsArrayList.add("Mondial 50 Eagle ");
                    productDetailsArrayList.add("Mondial 50 HC ");
                    productDetailsArrayList.add("Mondial 50 Loyal ");
                    productDetailsArrayList.add("Mondial 50 Revival ");
                    productDetailsArrayList.add("Mondial 50 SFC ");
                    productDetailsArrayList.add("Mondial 50 TAB ");
                    productDetailsArrayList.add("Mondial 50 TT");
                    productDetailsArrayList.add("Mondial 50 Turismo ");
                    productDetailsArrayList.add("Mondial 50 Wing ");
                    productDetailsArrayList.add("Mondial 50 ZNU ");
                    productDetailsArrayList.add("Mondial 50 ZNU ec ");
                    productDetailsArrayList.add("Mondial 100 Ardour");
                    productDetailsArrayList.add("Mondial 100 Hyena ");
                    productDetailsArrayList.add("Mondial 100 KM ");
                    productDetailsArrayList.add("Mondial 100 Loyal ");
                    productDetailsArrayList.add("Mondial 100 Masti ");
                    productDetailsArrayList.add("Mondial 100 Masti X");
                    productDetailsArrayList.add("Mondial 100 MFH ");
                    productDetailsArrayList.add("Mondial 100 MFM");
                    productDetailsArrayList.add("Mondial 100 MG Prince ");
                    productDetailsArrayList.add("Mondial 100 MG Sport");
                    productDetailsArrayList.add("Mondial 100 MG Superboy (");
                    productDetailsArrayList.add("Mondial 100 NT Turkuaz ");
                    productDetailsArrayList.add("Mondial 100 RT ");
                    productDetailsArrayList.add("Mondial 100 SFC Automatic X ");
                    productDetailsArrayList.add("Mondial 100 SFC Basic X");
                    productDetailsArrayList.add("Mondial 100 SFC Exclusive");
                    productDetailsArrayList.add("Mondial 100 SFC Snappy X ");
                    productDetailsArrayList.add("Mondial 100 SFC Snappy Xi ");
                    productDetailsArrayList.add("Mondial 100 SFS Sport");
                    productDetailsArrayList.add("Mondial 100 Superboy i ");
                    productDetailsArrayList.add("Mondial 100 UAG ");
                    productDetailsArrayList.add("Mondial 100 UGK");
                    productDetailsArrayList.add("Mondial 100 UKH ");
                    productDetailsArrayList.add("Mondial 100 URT");
                    productDetailsArrayList.add("Mondial 110 FT ");
                    productDetailsArrayList.add("Mondial 110 KF");
                    productDetailsArrayList.add("Mondial 125 Aggressive");
                    productDetailsArrayList.add("Mondial 125 AGK ");
                    productDetailsArrayList.add("Mondial 125 Ardour");
                    productDetailsArrayList.add("Mondial 125 Drift L ");
                    productDetailsArrayList.add("Mondial 125 Drift L CBS ");
                    productDetailsArrayList.add("Mondial 125 Elegante ");
                    productDetailsArrayList.add("Mondial 125 KT ");
                    productDetailsArrayList.add("Mondial 125 KV");
                    productDetailsArrayList.add("Mondial 125 Lavinia ");
                    productDetailsArrayList.add("Mondial 125 Mash ");
                    productDetailsArrayList.add("Mondial 125 MC Roadracer");
                    productDetailsArrayList.add("Mondial 125 MG Classic ");
                    productDetailsArrayList.add("Mondial 125 MG Deluxe");
                    productDetailsArrayList.add("Mondial 125 MG Sport ");
                    productDetailsArrayList.add("Mondial 125 MH ");
                    productDetailsArrayList.add("Mondial 125 MH Drift (");
                    productDetailsArrayList.add("Mondial 125 MT ");
                    productDetailsArrayList.add("Mondial 125 MX Grumble ");
                    productDetailsArrayList.add("Mondial 125 NT Turkuaz ");
                    productDetailsArrayList.add("Mondial 125 Prostreet ");
                    productDetailsArrayList.add("Mondial 125 Road Boy (");
                    productDetailsArrayList.add("Mondial 125 RR");
                    productDetailsArrayList.add("Mondial 125 RT");
                    productDetailsArrayList.add("Mondial 125 RT Akik");
                    productDetailsArrayList.add("Mondial 125 SFS");
                    productDetailsArrayList.add("Mondial 125 Strada ");
                    productDetailsArrayList.add("Mondial 125 Superboy i ");
                    productDetailsArrayList.add("Motolux Africa King ");
                    productDetailsArrayList.add("Motolux Africa Wolf ");
                    productDetailsArrayList.add("Motolux CEO 110 ");
                    productDetailsArrayList.add("Motolux Efsane 50 ");
                    productDetailsArrayList.add("Motolux Efsane X ");
                    productDetailsArrayList.add("Motolux Macchiato 125 ");
                    productDetailsArrayList.add("Motolux Macchiato 50 ");
                    productDetailsArrayList.add("Motolux MTX 125 ");
                    productDetailsArrayList.add("Motolux MW46 ");
                    productDetailsArrayList.add("Motolux Nirvana 50 ");
                    productDetailsArrayList.add("Motolux Nirvana Pro ");
                    productDetailsArrayList.add("Motolux Pitton 50");
                    productDetailsArrayList.add("Motolux Rossi 125 ");
                    productDetailsArrayList.add("Motolux Rossi 50 ");
                    productDetailsArrayList.add("Motolux Rossi Rs ");
                    productDetailsArrayList.add("Motolux Rossi RS 50 ");
                    productDetailsArrayList.add("Motolux Rüzgar 50 ");
                    productDetailsArrayList.add("Motolux Transit 125");
                    productDetailsArrayList.add("Motolux Vintage 50 ");
                    productDetailsArrayList.add("Motolux W46 ");
                    productDetailsArrayList.add("RKS 125R ");
                    productDetailsArrayList.add("RKS 125-S");
                    productDetailsArrayList.add("RKS Arome 125 ");
                    productDetailsArrayList.add("RKS Azure 50");
                    productDetailsArrayList.add("RKS Azure 50 Pro (");
                    productDetailsArrayList.add("RKS Bitter 125 ");
                    productDetailsArrayList.add("RKS Bitter 50 ");
                    productDetailsArrayList.add("RKS Blackster 250i");
                    productDetailsArrayList.add("RKS Blade 250 ");
                    productDetailsArrayList.add("RKS Blade 350 ");
                    productDetailsArrayList.add("RKS Blazer 50 ");
                    productDetailsArrayList.add("RKS Blazer 50 XR ");
                    productDetailsArrayList.add("RKS Blazer 50 XR Max ");
                    productDetailsArrayList.add("RKS Bolero 50 ");
                    productDetailsArrayList.add("RKS C1002 V");
                    productDetailsArrayList.add("RKS Cafe 152 ");
                    productDetailsArrayList.add("RKS Cityblade ");
                    productDetailsArrayList.add("RKS Cruiser 250 ");
                    productDetailsArrayList.add("RKS Dark Blue 125 ");
                    productDetailsArrayList.add("RKS Dark Blue 50 ");
                    productDetailsArrayList.add("RKS Easy Pro 50 ");
                    productDetailsArrayList.add("RKS Freccia 150 ");
                    productDetailsArrayList.add("RKS Galaxy Gold 125 ");
                    productDetailsArrayList.add("RKS Grace 202 ");
                    productDetailsArrayList.add("RKS Grace 202 Pro ");
                    productDetailsArrayList.add("RKS Jaguar 100 ");
                    productDetailsArrayList.add("RKS K-Light 202 ");
                    productDetailsArrayList.add("RKS K-Light 250 ");
                    productDetailsArrayList.add("RKS M502N ");
                    productDetailsArrayList.add("RKS Newlight ");
                    productDetailsArrayList.add("RKS Newlight 125 Pro ");
                    productDetailsArrayList.add("RKS Next 100 ");
                    productDetailsArrayList.add("RKS Next 50 ");
                    productDetailsArrayList.add("RKS NR200 ");
                    productDetailsArrayList.add("Kawasaki EL 250");
                    productDetailsArrayList.add("Kawasaki EN 500 ");
                    productDetailsArrayList.add("Kawasaki ER-5 ");
                    productDetailsArrayList.add("Kawasaki ER-6 F ");
                    productDetailsArrayList.add("Kawasaki ER-6 N ");
                    productDetailsArrayList.add("Kawasaki GTR 1400");
                    productDetailsArrayList.add("Kawasaki J 300");
                    productDetailsArrayList.add("Kawasaki KLE 500 ");
                    productDetailsArrayList.add("Kawasaki KLE 650 Versys ");
                    productDetailsArrayList.add("Kawasaki KLR 250");
                    productDetailsArrayList.add("Kawasaki KLR 650");
                    productDetailsArrayList.add("Kawasaki KLV 1000");
                    productDetailsArrayList.add("Kawasaki KLX 150 L");
                    productDetailsArrayList.add("Kawasaki KLX 250 ");
                    productDetailsArrayList.add("Kawasaki KLX 650");
                    productDetailsArrayList.add("Kawasaki KLZ 1000 Versys");
                    productDetailsArrayList.add("Kawasaki KX 125");
                    productDetailsArrayList.add("Kawasaki KX 250");
                    productDetailsArrayList.add("Kawasaki KX 250F");
                    productDetailsArrayList.add("Kawasaki KX 450F");
                    productDetailsArrayList.add("Kawasaki KX 65");
                    productDetailsArrayList.add("Kawasaki KX 85");
                    productDetailsArrayList.add("Kawasaki Ninja 250R ");
                    productDetailsArrayList.add("Kawasaki Ninja 250SL ");
                    productDetailsArrayList.add("Kawasaki Ninja 300 ");
                    productDetailsArrayList.add("Kawasaki Ninja 400 ");
                    productDetailsArrayList.add("Kawasaki Ninja 650 ");
                    productDetailsArrayList.add("Kawasaki Ninja 650 KRT Edition");
                    productDetailsArrayList.add("Kawasaki Ninja H2");
                    productDetailsArrayList.add("Kawasaki Ninja H2 SX");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-6R ");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10R ");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10RR");
                    productDetailsArrayList.add("Kawasaki Ninja ZX-10R SE");
                    productDetailsArrayList.add("Kawasaki Versys 1000 SE");
                    productDetailsArrayList.add("Kawasaki Versys X300");
                    productDetailsArrayList.add("Kawasaki VN 750 Vulcan");
                    productDetailsArrayList.add("Suzuki Address");
                    productDetailsArrayList.add("Suzuki Address 110 ");
                    productDetailsArrayList.add("Suzuki AN 125 HK ");
                    productDetailsArrayList.add("Suzuki Best 110");
                    productDetailsArrayList.add("Suzuki Burgman 200");
                    productDetailsArrayList.add("Suzuki Burgman UH 200 ");
                    productDetailsArrayList.add("Suzuki Burgman AN 400 ");
                    productDetailsArrayList.add("Suzuki Burgman AN 650 ABS ");
                    productDetailsArrayList.add("Suzuki DR350");
                    productDetailsArrayList.add("Suzuki DR600");
                    productDetailsArrayList.add("Suzuki DR650");
                    productDetailsArrayList.add("Suzuki DR650 SE");
                    productDetailsArrayList.add("Suzuki DR800 Big");
                    productDetailsArrayList.add("Suzuki DRZ400 SM");
                    productDetailsArrayList.add("Suzuki Freewind XF650");
                    productDetailsArrayList.add("Suzuki GN 125");
                    productDetailsArrayList.add("Suzuki GN 250");
                    productDetailsArrayList.add("Suzuki GS 500");
                    productDetailsArrayList.add("Suzuki GS 1000E");
                    productDetailsArrayList.add("Suzuki GSF 1200 Bandit S");
                    productDetailsArrayList.add("Suzuki GSF 600 Bandit");
                    productDetailsArrayList.add("Suzuki GSF 600 Bandit S");
                    productDetailsArrayList.add("Suzuki GSF 650 Bandit S ");
                    productDetailsArrayList.add("Suzuki GW 250F ");
                    productDetailsArrayList.add("Suzuki GW250 Inazuma ");
                    productDetailsArrayList.add("Suzuki GSR 600 ");
                    productDetailsArrayList.add("Suzuki GSR 750");
                    productDetailsArrayList.add("Suzuki GSX 8S");
                    productDetailsArrayList.add("Suzuki GSX 600 F");
                    productDetailsArrayList.add("Suzuki GSX 650 F");
                    productDetailsArrayList.add("Suzuki GSX 750 F");
                    productDetailsArrayList.add("Suzuki GSX 1400");
                    productDetailsArrayList.add("Suzuki GSX-R 125 ");
                    productDetailsArrayList.add("Suzuki GSX-R 250 ");
                    productDetailsArrayList.add("Suzuki GSX-R 600 Srad ");
                    productDetailsArrayList.add("Suzuki GSX-R 750 Srad");
                    productDetailsArrayList.add("Suzuki GSX-R 1000 ");
                    productDetailsArrayList.add("Suzuki GSX-R 1300 Hayabusa ");
                    productDetailsArrayList.add("Suzuki GSX-S 1000 ");
                    productDetailsArrayList.add("Suzuki GSX-S 1000 GT");
                    productDetailsArrayList.add("Suzuki GSX-S 125 ");
                    productDetailsArrayList.add("Suzuki GSX-S 750 ABS");
                    productDetailsArrayList.add("Suzuki Marauder 250");
                    productDetailsArrayList.add("Suzuki Marauder 800");
                    productDetailsArrayList.add("Suzuki LS 650 Savage");
                    productDetailsArrayList.add("SYN Allo 125");
                    productDetailsArrayList.add("SYN Crox 125");
                    productDetailsArrayList.add("SYN Cruisym 250i ");
                    productDetailsArrayList.add("SYN DRG 160 ");
                    productDetailsArrayList.add("SYN Fiddle II 125 ");
                    productDetailsArrayList.add("SYN Fiddle III");
                    productDetailsArrayList.add("SYN Fiddle III 125 ");
                    productDetailsArrayList.add("SYN Fiddle III 200i ");
                    productDetailsArrayList.add("SYN Fiddle IV 125 ");
                    productDetailsArrayList.add("SYN GTS 250 ");
                    productDetailsArrayList.add("SYN GTS 250i EVO ");
                    productDetailsArrayList.add("SYN HD2 200i");
                    productDetailsArrayList.add("SYN HD2 200i EVO ");
                    productDetailsArrayList.add("SYN HD Evo 200");
                    productDetailsArrayList.add("SYN Jet 14 ");
                    productDetailsArrayList.add("SYN Jet 14 200i");
                    productDetailsArrayList.add("SYN Jet 14 200i ABS (");
                    productDetailsArrayList.add("SYN Jet 4 125");
                    productDetailsArrayList.add("SYN Jet X 125 ");
                    productDetailsArrayList.add("SYN Joymax 250i ");
                    productDetailsArrayList.add("SYN Joymax 250i ABS ");
                    productDetailsArrayList.add("SYN Joymax Z 250");
                    productDetailsArrayList.add("SYN Joymax Z Plus 250 ");
                    productDetailsArrayList.add("SYN Joyride 300 ");
                    productDetailsArrayList.add("SYN Joyride Evo 200");
                    productDetailsArrayList.add("SYN Joyride Evo 200i ");
                    productDetailsArrayList.add("SYN Joyride S 200i ABS");
                    productDetailsArrayList.add("SYN Maxi 400i ");
                    productDetailsArrayList.add("SYN Maxi 400i ABS");
                    productDetailsArrayList.add("SYN Maxsym 600i ABS");
                    productDetailsArrayList.add("SYN Maxsym TL 508 ");
                    productDetailsArrayList.add("SYN Mio 100 ");
                    productDetailsArrayList.add("SYN Mio 50 ");
                    productDetailsArrayList.add("SYN Orbit II 125 ");
                    productDetailsArrayList.add("SYN Orbit II 125i ");
                    productDetailsArrayList.add("SYN Orbit II 50 ");
                    productDetailsArrayList.add("SYN Shark");
                    productDetailsArrayList.add("SYN Symnh X 125");
                    productDetailsArrayList.add("SYN Symphony SR 125 ");
                    productDetailsArrayList.add("SYN Symphony ST 125 ");
                    productDetailsArrayList.add("SYN Symphony ST 200i ");
                    productDetailsArrayList.add("SYN Symphony ST 200i ABS ");
                    productDetailsArrayList.add("SYN VS 150");
                    productDetailsArrayList.add("TVS Apache RR310");
                    productDetailsArrayList.add("TVS Apache RTR 150 ");
                    productDetailsArrayList.add("TVS Apache RTR 180");
                    productDetailsArrayList.add("TVS Apache RTR 200 (");
                    productDetailsArrayList.add("TVS Jupiter ");
                    productDetailsArrayList.add("TVS Jupiter 125 ");
                    productDetailsArrayList.add("TVS Neo X3i ");
                    productDetailsArrayList.add("TVS Ntorq 125");
                    productDetailsArrayList.add("TVS Scooty Pep Plus ");
                    productDetailsArrayList.add("TVS Scooty Zest 110");
                    productDetailsArrayList.add("TVS Wego ");
                    productDetailsArrayList.add("Vespa 946 125 i.e.");
                    productDetailsArrayList.add("Vespa Cosa 200");
                    productDetailsArrayList.add("Vespa ET 4 ");
                    productDetailsArrayList.add("Vespa GT 200 ");
                    productDetailsArrayList.add("Vespa GT 250");
                    productDetailsArrayList.add("Vespa GTS 125");
                    productDetailsArrayList.add("Vespa GTS 125 ABS ");
                    productDetailsArrayList.add("Vespa GTS 125 Supertech ");
                    productDetailsArrayList.add("Vespa GTS 150");
                    productDetailsArrayList.add("Vespa GTS 150 ABS ");
                    productDetailsArrayList.add("Vespa GTS 250 ");
                    productDetailsArrayList.add("Vespa GTS 250 ABS");
                    productDetailsArrayList.add("Vespa GTS 300 ");
                    productDetailsArrayList.add("Vespa GTS 300 Super ");
                    productDetailsArrayList.add("Vespa GTS 300 Super Sport ");
                    productDetailsArrayList.add("Vespa GTS 300 Super Sport S ");
                    productDetailsArrayList.add("Vespa GTS 300 Supertech ");
                    productDetailsArrayList.add("Vespa GTV 250 ie");
                    productDetailsArrayList.add("Vespa GTV 300 ie ");
                    productDetailsArrayList.add("Vespa LX 125 3V i.e ");
                    productDetailsArrayList.add("Vespa LX 150 ");
                    productDetailsArrayList.add("Vespa LX 150 3V i.e ");
                    productDetailsArrayList.add("Vespa LX 150 i.e ");
                    productDetailsArrayList.add("Vespa LXV 125");
                    productDetailsArrayList.add("Vespa Primavera 50 ");
                    productDetailsArrayList.add("Vespa Primavera 125 ");
                    productDetailsArrayList.add("Vespa Primavera 150 ");
                    productDetailsArrayList.add("Vespa PX 150 ");
                    productDetailsArrayList.add("Vespa PX 200 ");
                    productDetailsArrayList.add("Vespa S 125");
                    productDetailsArrayList.add("Yamaha Aerox 100");
                    productDetailsArrayList.add("Yamaha R7 ");
                    productDetailsArrayList.add("Yamaha BW's 50");
                    productDetailsArrayList.add("Yamaha BW's 100 (");
                    productDetailsArrayList.add("Yamaha BW's 125 ");
                    productDetailsArrayList.add("Yamaha Crypton ");
                    productDetailsArrayList.add("Yamaha Cygnus L ");
                    productDetailsArrayList.add("Yamaha Cygnus RS ");
                    productDetailsArrayList.add("Yamaha Cygnus X ");
                    productDetailsArrayList.add("Yamaha D'elight ");
                    productDetailsArrayList.add("Yamaha DT 80");
                    productDetailsArrayList.add("Yamaha DT 125 R");
                    productDetailsArrayList.add("Yamaha Fazer 8 ");
                    productDetailsArrayList.add("Yamaha Fazer 8 ABS ");
                    productDetailsArrayList.add("Yamaha FJR 1300 ");
                    productDetailsArrayList.add("Yamaha FZ 1");
                    productDetailsArrayList.add("Yamaha FZ1 Fazer");
                    productDetailsArrayList.add("Yamaha FZ6 ");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer ");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer ABS");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer S2");
                    productDetailsArrayList.add("Yamaha FZ6 Fazer S2 ABS");
                    productDetailsArrayList.add("Yamaha FZ8 ");
                    productDetailsArrayList.add("Yamaha FZ8 ABS");
                    productDetailsArrayList.add("Yamaha FZR 1000");
                    productDetailsArrayList.add("Yamaha FZR 600");
                    productDetailsArrayList.add("Yamaha FZS1000");
                    productDetailsArrayList.add("Yamaha FZS600");
                    productDetailsArrayList.add("Yamaha Galaxy");
                    productDetailsArrayList.add("Yamaha LB 70 Chappy");
                    productDetailsArrayList.add("Yamaha Majesty 180");
                    productDetailsArrayList.add("Yamaha Majesty 250");
                    productDetailsArrayList.add("Yamaha Majesty 400 ");
                    productDetailsArrayList.add("Yamaha MT-01");
                    productDetailsArrayList.add("Yamaha MT-03 ");
                    productDetailsArrayList.add("Yamaha MT-07 ");
                    productDetailsArrayList.add("Yamaha MT-07 ABS ");
                    productDetailsArrayList.add("Yamaha MT-07 Moto Cage");
                    productDetailsArrayList.add("Yamaha MT-09 ");
                    productDetailsArrayList.add("Yamaha MT-09 SP ");
                    productDetailsArrayList.add("Yamaha MT-10 ");
                    productDetailsArrayList.add("Yamaha MT-10 SP ");
                    productDetailsArrayList.add("Yamaha MT-125 ");
                    productDetailsArrayList.add("Yamaha MT-25 ");
                    productDetailsArrayList.add("Yamaha MT-25 ABS ");
                    productDetailsArrayList.add("Yamaha Neos ");
                    productDetailsArrayList.add("Yamaha Niken ");
                    productDetailsArrayList.add("Yamaha Niken GT");
                    productDetailsArrayList.add("Yamaha NMax 125 ");
                    productDetailsArrayList.add("Yamaha NMax 155 ");
                    productDetailsArrayList.add("Yamaha Nouvo ");
                    productDetailsArrayList.add("Yamaha PW 50");
                    productDetailsArrayList.add("Yamaha PW 80");
                    productDetailsArrayList.add("Yamaha Royal Star XVZ 1300");
                    productDetailsArrayList.add("Yamaha RX 1");
                    productDetailsArrayList.add("Yamaha RX 115 ");
                    productDetailsArrayList.add("Yamaha RX 135");
                    productDetailsArrayList.add("Yamaha RX-Z");
                    productDetailsArrayList.add("Yamaha SCR950");
                    productDetailsArrayList.add("Yamaha SR125");
                    productDetailsArrayList.add("Yamaha SR 400");
                    productDetailsArrayList.add("Yamaha TDM 850");
                    productDetailsArrayList.add("Yamaha TDM 900");
                    productDetailsArrayList.add("Yamaha Tenere 700 ");
                    productDetailsArrayList.add("Yamaha Tmax 500");
                    productDetailsArrayList.add("Yamaha Tmax 530 ");
                    productDetailsArrayList.add("Yamaha Tmax 560 ");
                    productDetailsArrayList.add("Yamaha Tracer 700 ");
                    productDetailsArrayList.add("Yamaha Tracer 900 ");
                    productDetailsArrayList.add("Yamaha Tracer 900 GT ");
                    productDetailsArrayList.add("Yamaha Tricity 125 ");
                    productDetailsArrayList.add("Yamaha Tricity 155 ");
                    productDetailsArrayList.add("Yamaha Tricity 300");
                    productDetailsArrayList.add("Yamaha TT 600 E");
                    productDetailsArrayList.add("Yamaha TTR 110 E");
                    productDetailsArrayList.add("Yamaha Venture Royal");
                    productDetailsArrayList.add("Yamaha Vercity 300");
                    productDetailsArrayList.add("Yamaha Virago XV 1100");
                    productDetailsArrayList.add("Yamaha Virago XV 250");
                    productDetailsArrayList.add("Yamaha Virago XV 535 ");
                    productDetailsArrayList.add("Yamaha Virago XV 750");
                    productDetailsArrayList.add("Yamaha Vmax");
                    productDetailsArrayList.add("Yamaha Wild Star XV 1600");
                    productDetailsArrayList.add("Yamaha WR 125 R ");
                    productDetailsArrayList.add("Yamaha WR 125 X ");
                    productDetailsArrayList.add("Yamaha WR 250 F");
                    productDetailsArrayList.add("Yamaha WR 250 R ");
                    productDetailsArrayList.add("Yamaha WR 250 X");
                    productDetailsArrayList.add("Yamaha WR 400");
                    productDetailsArrayList.add("Yamaha WR 450 F");
                    productDetailsArrayList.add("Yamaha X-City 250 ");
                    productDetailsArrayList.add("Yamaha Xenter 150 ");
                    productDetailsArrayList.add("Yamaha XJ 6 ");
                    productDetailsArrayList.add("Yamaha XJ 600 Diversion");
                    productDetailsArrayList.add("Yamaha XJ 600 N");
                    productDetailsArrayList.add("Yamaha XJ 6 Diversion F ");
                    productDetailsArrayList.add("Yamaha XJ 900 Diversion");
                    productDetailsArrayList.add("Yamaha XJR 1200");
                    productDetailsArrayList.add("Yamaha XJR 1300 SP");
                    productDetailsArrayList.add("Yamaha X-Max 125 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 125 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 ");
                    productDetailsArrayList.add("Yamaha X-Max 250 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 250 MomoDesign ");
                    productDetailsArrayList.add("Yamaha X-Max 250 Tech Max ");
                    productDetailsArrayList.add("Yamaha X-Max 300 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 300 Iron Max ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 400 ");
                    productDetailsArrayList.add("Yamaha X-Max 400 ABS ");
                    productDetailsArrayList.add("Yamaha X-Max 400 Iron Max ");
                    productDetailsArrayList.add("Yamaha X-Max 400 Tech Max ");
                    productDetailsArrayList.add("Yamaha XSR 125 ");
                    productDetailsArrayList.add("Yamaha XSR 700 ");
                    productDetailsArrayList.add("Yamaha XSR 900 ");
                    productDetailsArrayList.add("Yamaha XT 125 R");
                    productDetailsArrayList.add("Yamaha XT 125 X");
                    productDetailsArrayList.add("Yamaha XT 600");
                    productDetailsArrayList.add("Yamaha XT 600 E");
                    productDetailsArrayList.add("Yamaha XT 660 R ");
                    productDetailsArrayList.add("Yamaha XT 660 X ");
                    productDetailsArrayList.add("Yamaha XT 1200Z Super Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 660ZA Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 660Z Tenere ");
                    productDetailsArrayList.add("Yamaha XTZ 750 Super Tenere");
                    productDetailsArrayList.add("Yamaha XV950 ");
                    productDetailsArrayList.add("Yamaha XV950R");
                    productDetailsArrayList.add("Yamaha XV950 Racer");
                    productDetailsArrayList.add("Yamaha XVS 1100 Drag Star ");
                    productDetailsArrayList.add("Yamaha XVS 1300 A");
                    productDetailsArrayList.add("Yamaha XVS 250 Drag Star");
                    productDetailsArrayList.add("Yamaha XVS 650 A");
                    productDetailsArrayList.add("Yamaha XVS 650 Drag Star ");
                    productDetailsArrayList.add("Yamaha XVS 950 A");
                    productDetailsArrayList.add("Yamaha XVS 950 CU");
                    productDetailsArrayList.add("Yamaha YB 100");
                    productDetailsArrayList.add("Yamaha YBR 125 ");
                    productDetailsArrayList.add("Yamaha YBR 125 ESD ");
                    productDetailsArrayList.add("Yamaha YBR 250");
                    productDetailsArrayList.add("Yamaha YP 250 R x max");
                    productDetailsArrayList.add("Yamaha YS 125 ");
                    productDetailsArrayList.add("Yamaha YZ 65");
                    productDetailsArrayList.add("Yamaha YZ 85");
                    productDetailsArrayList.add("Yamaha YZ 125");
                    productDetailsArrayList.add("Yamaha YZ 250");
                    productDetailsArrayList.add("Yamaha YZ 250 F");
                    productDetailsArrayList.add("Yamaha YZ 400 F");
                    productDetailsArrayList.add("Yamaha YZ 450 F");
                    productDetailsArrayList.add("Yamaha YZF 1000 Thunderace");
                    productDetailsArrayList.add("Yamaha YZF 600 R Thundercat");
                    productDetailsArrayList.add("Yamaha YZF R1 ");
                    productDetailsArrayList.add("Yamaha YZF R125 ");
                    productDetailsArrayList.add("Yamaha YZF R1M");
                    productDetailsArrayList.add("Yamaha YZF R25 ");
                    productDetailsArrayList.add("Yamaha YZF R25 ABS ");
                    productDetailsArrayList.add("Yamaha YZF R6 ");
                    productDetailsArrayList.add("Yamaha YZF R6s");
                    productDetailsArrayList.add("Yamaha Zoom ");
                    productDetailsArrayList.add("Yuki Active 125 ");
                    productDetailsArrayList.add("Yuki Active 50 ");
                    productDetailsArrayList.add("Yuki Afşin 250 ");
                    productDetailsArrayList.add("Yuki Attact 100 ");
                    productDetailsArrayList.add("Yuki Casper S 50 ");
                    productDetailsArrayList.add("Yuki Crypto 125 ");
                    productDetailsArrayList.add("Yuki Crypto 50 ");
                    productDetailsArrayList.add("Yuki Defender Maxi ADV ");
                    productDetailsArrayList.add("Yuki Dirty Paws Offroad ");
                    productDetailsArrayList.add("Yuki Drag 200 ");
                    productDetailsArrayList.add("Yuki Enzo 50 ");
                    productDetailsArrayList.add("Yuki Fifty 50 ");
                    productDetailsArrayList.add("Yuki Forza 100");
                    productDetailsArrayList.add("Yuki Forza 170");
                    productDetailsArrayList.add("Yuki Funrider 125 ");
                    productDetailsArrayList.add("Yuki Gelato 150 ");
                    productDetailsArrayList.add("Yuki Gusto 50 ");
                    productDetailsArrayList.add("Yuki Hammer 50 ");
                    productDetailsArrayList.add("Yuki Huracan TR250T ");
                    productDetailsArrayList.add("Yuki Imola 125 ");
                    productDetailsArrayList.add("Yuki JJ125T-15 Active ");
                    productDetailsArrayList.add("Yuki JJ50QT Picasso 50 ");
                    productDetailsArrayList.add("Yuki LB150T-8");
                    productDetailsArrayList.add("Yuki Legend 50 ");
                    productDetailsArrayList.add("Yuki Lupo 125 ");
                    productDetailsArrayList.add("Yuki Margherita 50 ");
                    productDetailsArrayList.add("Yuki Mojito 125 ");
                    productDetailsArrayList.add("Yuki Mojito 50 ");
                    productDetailsArrayList.add("Yuki QM50QT-6E Snoopy ");
                    productDetailsArrayList.add("Yuki Risotto 125 ");
                    productDetailsArrayList.add("Yuki Risotto 50 ");
                    productDetailsArrayList.add("Yuki Route 110 ");
                    productDetailsArrayList.add("Yuki Scram 170 ");
                    productDetailsArrayList.add("Yuki T11 Explorer ");
                    productDetailsArrayList.add("Yuki T9 Strom 125 ");
                    productDetailsArrayList.add("Yuki Taro 250R ");
                    productDetailsArrayList.add("Yuki TB100T3E Picasso 100");
                    productDetailsArrayList.add("Yuki Tekken 125 ");
                    productDetailsArrayList.add("Yuki TK50Q3 Picasso 50 ");
                    productDetailsArrayList.add("Yuki TN150-3A Driver ");
                    productDetailsArrayList.add("Yuki YB 100 Jumbo");
                    productDetailsArrayList.add("Yuki YB 150 Jumbo ");
                    productDetailsArrayList.add("Yuki YB250ZKT Optimus");
                    productDetailsArrayList.add("Yuki YB50QT-3 Casper ");
                    productDetailsArrayList.add("Yuki Yıldız 100");
                    productDetailsArrayList.add("Yuki Yıldız 130");
                    productDetailsArrayList.add("Yuki YK 100-2A Apollo");
                    productDetailsArrayList.add("Yuki YK100-2H");
                    productDetailsArrayList.add("Yuki YK100-5");
                    productDetailsArrayList.add("Yuki YK-100-7A Gusto ");
                    productDetailsArrayList.add("Yuki YK100-7 Paşa");
                    productDetailsArrayList.add("Yuki YK 100-B");
                    productDetailsArrayList.add("Yuki YK-100M Modify");
                    productDetailsArrayList.add("Yuki YK125-15");
                    productDetailsArrayList.add("Yuki YK125-28H");
                    productDetailsArrayList.add("Yuki YK 125-7D Apollo");
                    productDetailsArrayList.add("Yuki YK 125-7 Rex");
                    productDetailsArrayList.add("Yuki YK 150-9 Goldfox");
                    productDetailsArrayList.add("Yuki YK 150-9 Goldfox-S");
                    productDetailsArrayList.add("Yuki YK150-G");
                    productDetailsArrayList.add("Yuki YK 150-G Üstad ");
                    productDetailsArrayList.add("Yuki YK150T-20");
                    productDetailsArrayList.add("Yuki YK 150 T-3");
                    productDetailsArrayList.add("Yuki YK-162 Goldfox");
                    productDetailsArrayList.add("Yuki YK-162 Goldfox-S");
                    productDetailsArrayList.add("Yuki YK-180M Modify");
                    productDetailsArrayList.add("Yuki YK-19 Midilli Cargo");
                    productDetailsArrayList.add("Yuki YK200GY-2");
                    productDetailsArrayList.add("Yuki YK-22 Rüzgar");
                    productDetailsArrayList.add("Yuki YK-24 Aydos ");
                    productDetailsArrayList.add("Yuki YK250");
                    productDetailsArrayList.add("Yuki YK250-21 R-Samurai ");
                    productDetailsArrayList.add("Yuki YK250-4 ");
                    productDetailsArrayList.add("Yuki YK250GY-7 İzci ");
                    productDetailsArrayList.add("Yuki YK- 250 ZH Ayder ");
                    productDetailsArrayList.add("Yuki YK-25 Midilli");
                    productDetailsArrayList.add("Yuki YK50QT-2A");
                    productDetailsArrayList.add("Yuki YX 200");
                    productDetailsArrayList.add("Yuki ZN-100T-E5 Legend ");
                    productDetailsArrayList.add("Yuki ZN-150T-E5 Legend");
                    productDetailsArrayList.add("Yuki ZY125-15A Scrambler ");
                    productDetailsArrayList.add("Other");

























                }
            }
        });

        }
        else if(parcaAdi.equals("Kitap(Dergi-Gazete)") || parcaAdi.equals("Book (Magazine-Newspaper)")){


            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                if(dil.equals("türkce")){
                   productDetailsArrayList.add("Anı kitapları");
                   productDetailsArrayList.add("Romanlar");
                   productDetailsArrayList.add("Hikaye kitapları");
                   productDetailsArrayList.add("Gezi kitapları");
                   productDetailsArrayList.add("Şiir kitapları");
                   productDetailsArrayList.add("Biyografi kitapları");
                   productDetailsArrayList.add("Din kitapları");
                   productDetailsArrayList.add("Bilgi kitapları");
                   productDetailsArrayList.add("Çocuk kitapları");
                   productDetailsArrayList.add("Anı");
                   productDetailsArrayList.add("Anlatı");
                   productDetailsArrayList.add("Araştırma-İnceleme");
                   productDetailsArrayList.add("Bilim");
                   productDetailsArrayList.add("Biyografi");
                   productDetailsArrayList.add("Çizgi Roman");
                   productDetailsArrayList.add("Deneme");
                   productDetailsArrayList.add("Edebiyat");
                   productDetailsArrayList.add("Eğitim");
                   productDetailsArrayList.add("Felsefe");
                   productDetailsArrayList.add("Gençlik");
                   productDetailsArrayList.add("Gezi");
                   productDetailsArrayList.add("Hikaye");
                   productDetailsArrayList.add("Hobi");
                   productDetailsArrayList.add("İnceleme");
                   productDetailsArrayList.add("İş Ekonomi - Hukuk");
                   productDetailsArrayList.add("Kişisel Gelişim");
                   productDetailsArrayList.add("Konuşmalar");
                   productDetailsArrayList.add("Masal");
                   productDetailsArrayList.add("Mektup");
                   productDetailsArrayList.add("Mizah");
                   productDetailsArrayList.add("Öykü");
                   productDetailsArrayList.add("Polisiye");
                   productDetailsArrayList.add("Psikoloji");
                   productDetailsArrayList.add("Resimli Öykü");
                   productDetailsArrayList.add("Roman");
                   productDetailsArrayList.add("Sağlık");
                   productDetailsArrayList.add("Sanat - Tasarım");
                   productDetailsArrayList.add("Sanat- Müzik");
                   productDetailsArrayList.add("Sinema Tarihi");
                   productDetailsArrayList.add("Söyleşi");
                   productDetailsArrayList.add("Şiir");
                   productDetailsArrayList.add("Tarih");
                   productDetailsArrayList.add("Yemek Kitapları");
                   productDetailsArrayList.add("Diğer");
                }else{
                    productDetailsArrayList.add("Memoirs (Memoirs)");
                    productDetailsArrayList.add("Novels (Novels)");
                    productDetailsArrayList.add("Storybooks (Storybooks)");
                    productDetailsArrayList.add("Travel books (Travel books)");
                    productDetailsArrayList.add("Poetry books (Poetry books)");
                    productDetailsArrayList.add("Biography books (Biography books)");
                    productDetailsArrayList.add("Religious books (Religious books)");
                    productDetailsArrayList.add("Non-fiction books (Non-fiction books)");
                    productDetailsArrayList.add("Children's books (Children's books)");
                    productDetailsArrayList.add("Memoir (Memoir)");
                    productDetailsArrayList.add("Narrative (Narrative)");
                    productDetailsArrayList.add("Research and analysis (Research and analysis)");
                    productDetailsArrayList.add("Science (Science)");
                    productDetailsArrayList.add("Biography (Biography)");
                    productDetailsArrayList.add("Comic book (Comic book)");
                    productDetailsArrayList.add("Essay (Essay)");
                    productDetailsArrayList.add("Literature (Literature)");
                    productDetailsArrayList.add("Education (Education)");
                    productDetailsArrayList.add("Philosophy (Philosophy)");
                    productDetailsArrayList.add("Youth (Youth)");
                    productDetailsArrayList.add("Travel (Travel)");
                    productDetailsArrayList.add("Story (Story)");
                    productDetailsArrayList.add("Hobby (Hobby)");
                    productDetailsArrayList.add("Analysis (Analysis)");
                    productDetailsArrayList.add("Business, Economics, and Law (Business, Economics, and Law)");
                    productDetailsArrayList.add("Personal development (Personal development)");
                    productDetailsArrayList.add("Speeches (Speeches)");
                    productDetailsArrayList.add("Fairy tale (Fairy tale)");
                    productDetailsArrayList.add("Letter (Letter)");
                    productDetailsArrayList.add("Humor (Humor)");
                    productDetailsArrayList.add("Short story (Short story)");
                    productDetailsArrayList.add("Detective (Detective)");
                    productDetailsArrayList.add("Psychology (Psychology)");
                    productDetailsArrayList.add("Illustrated story (Illustrated story)");
                    productDetailsArrayList.add("Novel (Novel)");
                    productDetailsArrayList.add("Health (Health)");
                    productDetailsArrayList.add("Art and design (Art and design)");
                    productDetailsArrayList.add("Art and music (Art and music)");
                    productDetailsArrayList.add("Cinema history (Cinema history)");
                    productDetailsArrayList.add("Interview (Interview)");
                    productDetailsArrayList.add("Poetry (Poetry)");
                    productDetailsArrayList.add("History (History)");
                    productDetailsArrayList.add("Cookbooks (Cookbooks)");
                    productDetailsArrayList.add("Other");

                }
                }
            });

        }else if(parcaAdi.equals("Diğer") || parcaAdi.equals("Other")){
            productDetailsArrayList.clear();
        }




       //   arrayAdapter.notifyDataSetChanged();

        
    }



}