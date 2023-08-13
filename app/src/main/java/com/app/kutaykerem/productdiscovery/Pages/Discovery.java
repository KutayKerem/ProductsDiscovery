package com.app.kutaykerem.productdiscovery.Pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.app.kutaykerem.productdiscovery.Adaptor.Kesfet.CustomPagerAdapter;
import com.app.kutaykerem.productdiscovery.Models.KesfetAnlıkDusuncelerDetails;
import com.app.kutaykerem.productdiscovery.Models.KesfetDetails;
import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentDiscoveryBinding;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Discovery extends Fragment {


    public FragmentDiscoveryBinding binding;
    public  ViewPager2 viewPager;
    public String dil;
    public String userId;

    public String signState, arananParcaAdi;
    public boolean isPageSelectionHandled = false;

    public  boolean changePosPage = false;

    public ColorStateList def;
    public TextView item1, item2, select;

    public TabLayoutMediator tabLayoutMediator;
    public MenuItem menuItem;
    public SearchView searchView;

    public KesfetFragment kesfetFragment;
    public KesfetAnlikDusuncelerFragment kesfetAnlikDusuncelerFragment;
    public int pos;


    public Discovery() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = FragmentDiscoveryBinding.inflate(inflater, container, false);


        return binding.getRoot();

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewPager = view.findViewById(R.id.viewPager);


        userLauncher();

        createPage();


        try {


        CustomPagerAdapter adapter = new CustomPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);


        tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull com.google.android.material.tabs.TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                dilTanı(dil -> {
                                    if (dil.equals("türkce")) {
                                        tab.setText("Gönderiler");
                                    } else if (dil.equals("ingilizce")) {
                                        tab.setText("Posts");
                                    }


                                });

                                break;
                            case 1:
                                dilTanı(dil -> {
                                    if (dil.equals("türkce")) {
                                        tab.setText("İfade Akışları");
                                    } else if (dil.equals("ingilizce")) {
                                        tab.setText("Expression Stream");
                                    }


                                });
                                break;
                        }

                    }


                });
        tabLayoutMediator.attach();




        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        Boolean nightMode = sharedPreferences.getBoolean("night", false);

        if (nightMode) {
            binding.tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.black), ContextCompat.getColor(requireContext(), R.color.darkeyGray));
        } else {
            binding.tabLayout.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.white), ContextCompat.getColor(requireContext(), R.color.darkeyGray));
        }
        binding.tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.secilenAcikRenk));


        // SearchEdit sebebini çözemediğim nedenlerden dolayı kesfetFragment sadece OnCreateOptıonsMenu de çalışıyor o yüzden bunu yazmak zorunda bırakıyor...
        Toolbar toolbar = view.findViewById(R.id.toolbar_discovery_search);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        }catch (Exception e){
            Log.d("Error:",e.getLocalizedMessage());
        }






    }


    public void createPage() {

        if (getArguments() != null) {
            arananParcaAdi = DiscoveryArgs.fromBundle(getArguments()).getArananParca();
            pos = DiscoveryArgs.fromBundle(getArguments()).getPosition();

            try {
            binding.discoverySettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    binding.DiscoveryView.setVisibility(View.GONE);
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToFeedbackFragment().setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);

                }
            });



            binding.ekleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.DiscoveryView.setVisibility(View.GONE);

                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToAddPostFirstStage().setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);



                }
            });



            ScroolPageSelection(pos);
        }catch (Exception e){
            Log.d("Error:",e.getLocalizedMessage());
        }



        }


    }

    private void CreateMore(int position) {

        try{
        viewPager.post(() -> {

            int currentFragmentIndex = position;

            pos = currentFragmentIndex;

            if (currentFragmentIndex == 0) {


                viewPager.setCurrentItem(0, false);
                KesfetFragment frag = (KesfetFragment) getCurrentFrag();
                if (frag != null) {
                    frag.kesfetDetailsArrayList.clear();
                    frag.getArgumenData(arananParcaAdi);
                }


                changePosPage = false;

            } else if (currentFragmentIndex == 1) {

                changePosPage = true;





                    viewPager.setCurrentItem(1, false);
                    KesfetAnlikDusuncelerFragment frag2 = (KesfetAnlikDusuncelerFragment) getCurrentFrag();


                    if (frag2 != null) {
                        frag2.kesfetAnlıkDusuncelerDetailsArrayList.clear();
                        frag2.getArgumenData(arananParcaAdi);
                    }

                    kesfetAnlikDusuncelerFragment = (KesfetAnlikDusuncelerFragment) getCurrentFrag();





            } else {

                changePosPage = false;

                viewPager.setCurrentItem(0, false);
                KesfetFragment frag = (KesfetFragment) getCurrentFrag();
                if (frag != null) {
                    frag.kesfetDetailsArrayList.clear();
                    frag.getArgumenData(arananParcaAdi);
                }


            }


        });

        }catch (Exception e){
            Log.d("Error:",e.getLocalizedMessage());
        }

    }


    private void ScroolPageSelection(int position) {

        if (position != -1) {

            handlePageSelected();


            int currentFragmentIndex = position;

            if (currentFragmentIndex == 0) {


                CreateMore(0);


            } else if (currentFragmentIndex == 1) {


                CreateMore(1);
            } else {


                CreateMore(0);
            }

            viewPager.setCurrentItem(position, true);

        } else {


            viewPager.post(() -> {
                viewPager.setCurrentItem(position, false);
                KesfetFragment frag = (KesfetFragment) getCurrentFrag();
                if (frag != null) {
                    frag.kesfetDetailsArrayList.clear();
                    frag.getArgumenData(arananParcaAdi);
                }

            });


            handlePageSelected();
        }


    }


    private void handlePageSelected() {
        viewPager.post(() -> {

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);


                    int currentFragmentIndex = position;

                    if (currentFragmentIndex == 0) {


                        CreateMore(0);


                    } else if (currentFragmentIndex == 1) {


                        CreateMore(1);


                    } else {


                        CreateMore(0);

                    }
                }
            });
        });
    }


    private Fragment getCurrentFrag() {
        try {
            return requireActivity().getSupportFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
        }catch (Exception e){
            Log.d("Error: ",e.getMessage());
        }
      return null;

    }

    private void userLauncher() {
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

                } else if (providerId.equals("facebook.com")) {
                    // Kullanıcı Facebook hesabı ile giriş yapmıştır
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    if (isLoggedIn) {
                        // Facebook hesabı ile oturum açmış kullanıcılara özgü işlemler

                    }

                    signState = "facebook";
                    break;

                } else if (providerId.equals("password")) {
                    // Kullanıcı email ve şifre ile giriş yapmıştır


                    signState = "gmail_hotmail";

                    break;
                }
            }

            if (signState.equals("google")) {

                registerUser("google", firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl(), firebaseUser.getEmail());

            } else if (signState.equals("facebook")) {

                registerUser("facebook", firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl(), firebaseUser.getEmail());

            } else if (signState.equals("gmail_hotmail")) {

                registerUser("gmail_hotmail", firebaseUser.getUid(), "user name", null, firebaseUser.getEmail());

            }


        } else {
            // Kullanıcı oturum açmamıştır

        }
    }


    private void registerUser(String signState, String uid, String userName, Uri userProfile, String email) {

        if (signState.equals("google")) {

            userId = uid;

        } else if (signState.equals("facebook")) {

            userId = uid;
        } else if (signState.equals("gmail_hotmail")) {
            userId = uid;
        } else {

        }

    }


    public void dilTanı(final DilCallback dilCallback) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);


                    dilCallback.onDilCallback(dil);
                } else {

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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.toolbar_search,menu);








        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if (dil.equals("türkce")) {
                    binding.searchEditText.setHint ("Model giriniz");
                } else if (dil.equals("ingilizce")) {
                    binding.searchEditText.setHint("Enter model");
                }
            }
        });





        viewPager.post(() -> {


            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {

                    int currentFragmentIndex = position;



                    if (currentFragmentIndex == 0) {


                        kesfetFragment = (KesfetFragment) getCurrentFrag();



                        binding.searchEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                                // Do something when search text changes


                                ArrayList<KesfetDetails> newKesfet = new ArrayList<>();
                                for (KesfetDetails kesfetDetails : kesfetFragment.kesfetDetailsArrayList) {
                                    if(kesfetDetails.getParcaModeli() != null){
                                        if(kesfetDetails.getParcaModeli().toLowerCase().contains(newText.toString().toLowerCase())){
                                            newKesfet.add(kesfetDetails);
                                        }
                                    } else{
                                        if(kesfetDetails.getAyrıParca().toLowerCase().contains(newText.toString().toLowerCase())){
                                            newKesfet.add(kesfetDetails);
                                        }
                                    }
                                }
                                kesfetFragment.kesfetAdaptor.setKesfetDetailsArrayList(newKesfet);
                                kesfetFragment.kesfetAdaptor.notifyDataSetChanged();


                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });





                    } else if (currentFragmentIndex == 1) {


                        binding.searchEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                                // Do something when search text changes

                                ArrayList<KesfetAnlıkDusuncelerDetails> newkesfetAnlıkDusunceler = new ArrayList<>();

                                for(KesfetAnlıkDusuncelerDetails kesfetAnlıkDusuncelerDetails : kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerDetailsArrayList ){
                                    if(kesfetAnlıkDusuncelerDetails.getParcaModeli() != null){
                                        if(kesfetAnlıkDusuncelerDetails.getParcaModeli().toLowerCase().contains(newText.toString() .toLowerCase())){
                                            newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                                        }

                                    }else{
                                        if(kesfetAnlıkDusuncelerDetails.getAyrıParca().toLowerCase().contains(newText.toString() .toLowerCase())){
                                            newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                                        }
                                    }

                                }
                                kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.setKesfetAnlıkDusuncelerDetailsArrayList(newkesfetAnlıkDusunceler);
                                kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.notifyDataSetChanged();



                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });





                    }else{



                        kesfetFragment = (KesfetFragment) getCurrentFrag();

                        binding.searchEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                                // Do something when search text changes


                                ArrayList<KesfetDetails> newKesfet = new ArrayList<>();
                                for (KesfetDetails kesfetDetails : kesfetFragment.kesfetDetailsArrayList) {
                                    if(kesfetDetails.getParcaModeli() != null){
                                        if(kesfetDetails.getParcaModeli().toLowerCase().contains(newText.toString().toLowerCase())){
                                            newKesfet.add(kesfetDetails);
                                        }
                                    } else{
                                        if(kesfetDetails.getAyrıParca().toLowerCase().contains(newText.toString().toLowerCase())){
                                            newKesfet.add(kesfetDetails);
                                        }
                                    }
                                }
                                kesfetFragment.kesfetAdaptor.setKesfetDetailsArrayList(newKesfet);
                                kesfetFragment.kesfetAdaptor.notifyDataSetChanged();


                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });







                    }



                }
            });



        });


        // İlk açıldığında ve onClick yaparak sayfa değiştirildiğinde çalışması için bu kodları yazdık.


        if(pos == 1){

            binding.searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence newText, int start, int before, int count) {
                    // Do something when search text changes

                    ArrayList<KesfetAnlıkDusuncelerDetails> newkesfetAnlıkDusunceler = new ArrayList<>();

                    for(KesfetAnlıkDusuncelerDetails kesfetAnlıkDusuncelerDetails : kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerDetailsArrayList ){
                        if(kesfetAnlıkDusuncelerDetails.getParcaModeli() != null){
                            if(kesfetAnlıkDusuncelerDetails.getParcaModeli().toLowerCase().contains(newText.toString() .toLowerCase())){
                                newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                            }

                        }else{
                            if(kesfetAnlıkDusuncelerDetails.getAyrıParca().toLowerCase().contains(newText.toString() .toLowerCase())){
                                newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                            }
                        }

                    }
                    kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.setKesfetAnlıkDusuncelerDetailsArrayList(newkesfetAnlıkDusunceler);
                    kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.notifyDataSetChanged();



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


        }else{



            // Profile fragmenti açıldıktan sonra bu fragment açıldığında çökme sebebi ile buradaki kodlar bu şekilde yazıldı.

            binding.searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence newText, int start, int before, int count) {
                    // Do something when search text changes



                    if(viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == -1){

                        kesfetFragment = (KesfetFragment) getCurrentFrag();

                        ArrayList<KesfetDetails> newKesfet = new ArrayList<>();
                        for (KesfetDetails kesfetDetails : kesfetFragment.kesfetDetailsArrayList) {
                            if(kesfetDetails.getParcaModeli() != null){
                                if(kesfetDetails.getParcaModeli().toLowerCase().contains(newText.toString().toLowerCase())){
                                    newKesfet.add(kesfetDetails);
                                }
                            } else{
                                if(kesfetDetails.getAyrıParca().toLowerCase().contains(newText.toString().toLowerCase())){
                                    newKesfet.add(kesfetDetails);
                                }
                            }
                        }
                        kesfetFragment.kesfetAdaptor.setKesfetDetailsArrayList(newKesfet);
                        kesfetFragment.kesfetAdaptor.notifyDataSetChanged();

                    }else{
                        ArrayList<KesfetAnlıkDusuncelerDetails> newkesfetAnlıkDusunceler = new ArrayList<>();

                        for(KesfetAnlıkDusuncelerDetails kesfetAnlıkDusuncelerDetails : kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerDetailsArrayList ){
                            if(kesfetAnlıkDusuncelerDetails.getParcaModeli() != null){
                                if(kesfetAnlıkDusuncelerDetails.getParcaModeli().toLowerCase().contains(newText.toString() .toLowerCase())){
                                    newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                                }

                            }else{
                                if(kesfetAnlıkDusuncelerDetails.getAyrıParca().toLowerCase().contains(newText.toString() .toLowerCase())){
                                    newkesfetAnlıkDusunceler.add(kesfetAnlıkDusuncelerDetails);
                                }
                            }

                        }
                        kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.setKesfetAnlıkDusuncelerDetailsArrayList(newkesfetAnlıkDusunceler);
                        kesfetAnlikDusuncelerFragment.kesfetAnlıkDusuncelerAdaptor.notifyDataSetChanged();


                    }



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            //kesfetFragment = (KesfetFragment) getCurrentFrag();





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
            binding.discoveryProgressBar.setVisibility(View.INVISIBLE);
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.ekleButton.setVisibility(View.VISIBLE);
            binding.searchEditText.setVisibility(View.VISIBLE);
            binding.tabLayout.setVisibility(View.VISIBLE);
            binding.discoverySettings.setVisibility(View.VISIBLE);

        } else {
            binding.discoveryProgressBar.setVisibility(View.VISIBLE);
            binding.viewPager.setVisibility(View.INVISIBLE);
            binding.ekleButton.setVisibility(View.INVISIBLE);
            binding.searchEditText.setVisibility(View.INVISIBLE);
            binding.tabLayout.setVisibility(View.INVISIBLE);
            binding.discoverySettings.setVisibility(View.INVISIBLE);
        }


    }




    @Override
    public void onResume() {
        super.onResume();

        networkController();

        viewPager.post(() -> {
           pos =  viewPager.getCurrentItem();


        });


    }
}