package com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadaslarProfilesAdapter extends RecyclerView.Adapter<ArkadaslarProfilesAdapter.ArkadaslarProfilesHolder> {


    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;



    String dil;
    String userId;

    String signState;

    List<Object> idList;


    public ArkadaslarProfilesAdapter(List<Object> idList,Context context) {
        this.idList = idList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArkadaslarProfilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_arkadaslar_profiles,parent,false);
        return new ArkadaslarProfilesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArkadaslarProfilesHolder holder, int position) {

        String id = idList.get(position).toString();


        System.out.println("arkadaslar profiller "+id);


        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                        String kullanıcıAdı = dataSnapshot.child("kullanıcıAdı").getValue().toString();
                        holder.kullaniciAdi.setText(kullanıcıAdı);



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error: ",error.getMessage());
            }
        });






        databaseReference.child("Durum").child(id).child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                if(dataSnapshot.getValue() != null){
                    Boolean state = (Boolean) dataSnapshot.child("durum").getValue();

                    if(state){
                        holder.state.setBackgroundResource(R.drawable.kullanici_online);
                    }else{
                        holder.state.setBackgroundResource(R.drawable.kullanici_offline);
                    }
                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error: ",error.getMessage());
            }
        });








        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Profiles").document("Resimler").collection(id).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.d("Error: ",error.getLocalizedMessage());
                }

                if(value != null){
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){

                        Map<String,Object> data = documentSnapshot.getData();

                        if(data.get("ImageProfile") != null){
                            String image = (String) data.get("ImageProfile");



                            try {
                                Uri uri = Uri.parse(image);

                                RequestOptions requestOptions = new RequestOptions()
                                        .format(DecodeFormat.PREFER_RGB_565)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .encodeQuality(50);

                                Glide.with(context)
                                        .load(uri)
                                        .apply(requestOptions)
                                        .into(holder.profile);
                            }catch (Exception e){

                            }
                        }

                    }

                }


            }
        });





        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(id);
                Navigation.findNavController(v).navigate(action);
            }
        });














    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public class ArkadaslarProfilesHolder extends RecyclerView.ViewHolder{

        ConstraintLayout constraintLayout;
        TextView kullaniciAdi;
        CircleImageView profile;
        View state;



        public ArkadaslarProfilesHolder(@NonNull View itemView) {
            super(itemView);



            constraintLayout = itemView.findViewById(R.id.recycler_row_arkadaslar_profiles_constraintLayout);
            kullaniciAdi = itemView.findViewById(R.id.recycler_row_arkadaslar_profiles_kullaniciAdi);
            profile = itemView.findViewById(R.id.recycler_row_arkadaslar_profiles_profile);
            state = itemView.findViewById(R.id.recycler_row_arkadaslar_profiles_state);










            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child("Profile");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            AppCompatActivity activity = (AppCompatActivity) context;


            userLauncher();









        }
















    }
    public void dilTanı(ArkadaslarSohbetlerAdapter.DilCallback dilCallback){
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
    private void userLauncher(){
        AppCompatActivity activity = (AppCompatActivity) context;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo profile : firebaseUser.getProviderData()) {
                String providerId = profile.getProviderId();
                if (providerId.equals("google.com")) {
                    // Kullanıcı Google hesabı ile giriş yapmıştır
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(activity);
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
}
