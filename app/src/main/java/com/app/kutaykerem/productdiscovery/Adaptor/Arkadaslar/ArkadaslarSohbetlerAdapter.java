package com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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

import com.app.kutaykerem.productdiscovery.Models.GetDateDetailEnglish;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailTurkish;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadaslarSohbetlerAdapter extends RecyclerView.Adapter<ArkadaslarSohbetlerAdapter.ArkadaslarSohbetlerHolder> {

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

    public ArkadaslarSohbetlerAdapter(List<Object> idList, Context context) {
        this.idList = idList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArkadaslarSohbetlerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerrow_arkadaslar_listesi_sohbetler,parent,false);
        return new ArkadaslarSohbetlerHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ArkadaslarSohbetlerHolder holder, int position) {
        String id = (String) idList.get(position);

        System.out.println("arkadaslar sohbetler "+id);

        userLauncher();

        String gonderen = id.toString();



        FirebaseFirestore kullanıcıProfileFirestore = FirebaseFirestore.getInstance();

        kullanıcıProfileFirestore.collection("Profiles").document("Resimler").collection(gonderen).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error !=null){

                }

                if(value != null){

                    for(DocumentSnapshot snapshot  : value.getDocuments()){

                        Map<String ,Object> data = snapshot.getData();

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
            };

        });



        databaseReference.child("ArkadaslarListesiMesajlar").child(userId).child(id.toString()).orderByChild("time" + Query.Direction.DESCENDING).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if(snapshot != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        HashMap<String, Object> dates = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (dates != null) {
                            String mesaj = dates.get("mesaj") != null ? dates.get("mesaj").toString() : "";
                            String gönderen = dates.get("from") != null ? dates.get("from").toString() : "";
                            String type = dates.get("type") != null ? dates.get("type").toString() : "";
                            String timestamp = dates.get("time") != null ? dates.get("time").toString() : "";


                            // FirebaseFirestore doğrudan Dateyi desteklediği için kesfetAdapterde stringe çevirmedik ama databaseReference desteklemediği için simpleDateFormat kullanak zorundayız.
                            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);

                            if (!timestamp.isEmpty()) {
                                // Tarih dizesi boş değilse ayrıştırma işlemini gerçekleştir
                                try {
                                    Date olusturulanTarih = format.parse(timestamp);
                                    if (olusturulanTarih != null) {
                                        Date endTime = new Date(); // Geçerli zamanı temsil eden endTime değişkeni

                                        dilTanı(new DilCallback() {
                                            @Override
                                            public void onDilCallback(String dil) {
                                                if (dil.equals("türkce")) {
                                                    String elapsedTime = GetDateDetailTurkish.calculateElapsedTime(olusturulanTarih, endTime);
                                                    holder.sonMesajTarih.setText(elapsedTime);
                                                } else if (dil.equals("ingilizce")) {
                                                    String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                                                    holder.sonMesajTarih.setText(elapsedTime);
                                                } else {
                                                    String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                                                    holder.sonMesajTarih.setText(elapsedTime);
                                                }
                                            }
                                        });


                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    // Ayrıştırma hatasıyla başa çıkma
                                }
                            } else {
                                // Tarih dizesi boşsa yapılacak işlem
                            }







                            holder.sonMesaj.setText(mesaj);




                            if (gönderen.equals(userId)) {

                                if (type.equals("resim")) {
                                    holder.sonMesaj.setText(holder.resimgönderildi);
                                } else {
                                    holder.sonMesaj.setText(mesaj);
                                    holder.sonMesajKarsi.setVisibility(View.GONE);
                                }

                            } else {
                                if (type.equals("resim")) {
                                    holder.sonMesaj.setText(holder.resimigör);
                                } else {
                                    holder.sonMesajKarsi.setText(mesaj);
                                    holder.sonMesaj.setVisibility(View.GONE);
                                }

                            }

                        }




                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        reference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    String kullanıcıAdı = snapshot.child("kullanıcıAdı").getValue().toString();
                    holder.kullanıcıAdı.setText(kullanıcıAdı);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(id);
                Navigation.findNavController(view).navigate(action);

            }
        });


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToSohbetlerFragment().setKullaniciId(id.toString());
                Navigation.findNavController(view).navigate(action);

            }
        });

    }
    @Override
    public int getItemCount() {
        return idList.size();
    }


    public class ArkadaslarSohbetlerHolder extends RecyclerView.ViewHolder{
        CircleImageView profile;
        TextView kullanıcıAdı, sonMesaj, sonMesajKarsi, sonMesajTarih;
        String resimigör, ilkmesajat,resimgönderildi;
        ConstraintLayout constraintLayout;

        public ArkadaslarSohbetlerHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.recyclerrow_arkadaslar_Profile);
            kullanıcıAdı = itemView.findViewById(R.id.recyclerrow_arkadaslar_kullanıcıAdı);
            sonMesaj = itemView.findViewById(R.id.recyclerrow_arkadaslar_sonMesaj);
            sonMesajKarsi = itemView.findViewById(R.id.recyclerrow_arkadaslar_sonMesajKarsi);
            sonMesajTarih = itemView.findViewById(R.id.recyclerrow_arkadaslar_sonMesajTarih);
            constraintLayout = itemView.findViewById(R.id.arkadaslarConstraintLayout);

            profile.setVisibility(View.VISIBLE);
            kullanıcıAdı.setVisibility(View.VISIBLE);




            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child("Profile");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            AppCompatActivity activity = (AppCompatActivity) context;


            userLauncher();






            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        dil = dataSnapshot.child("dil").getValue(String.class);


                        if (dil.equals("türkce")) {
                            resimigör = "Resimi gör";
                            resimgönderildi = "Resim gönderildi";
                            ilkmesajat = "İlk mesajı siz atın";

                            sonMesaj.setHint(ilkmesajat);
                        } else if (dil.equals("ingilizce")) {
                            resimigör = "see picture";
                            resimgönderildi = "picture sent";
                            ilkmesajat = "Send first message";

                            sonMesaj.setHint(ilkmesajat);
                        }



                    }else{

                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {



                }
            });







        }


    }
    public void dilTanı(DilCallback dilCallback){
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
