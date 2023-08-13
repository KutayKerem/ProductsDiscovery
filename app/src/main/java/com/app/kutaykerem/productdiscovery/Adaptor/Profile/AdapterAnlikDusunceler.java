package com.app.kutaykerem.productdiscovery.Adaptor.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.GetDateDetailEnglish;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailTurkish;
import com.app.kutaykerem.productdiscovery.Models.KesfetAnlıkDusuncelerDetails;
import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAnlikDusunceler extends RecyclerView.Adapter<AdapterAnlikDusunceler.AnlikDusuncelerHolder> {

    String userId;
    String dil;
    String signState;
    DatabaseReference databaseReference;
    Context context;
    ArrayList<KesfetAnlıkDusuncelerDetails> anlıkDusuncelerDetailsArrayList;

    public AdapterAnlikDusunceler(Context context, ArrayList<KesfetAnlıkDusuncelerDetails> anlıkDusuncelerDetailsArrayList) {
        this.context = context;
        this.anlıkDusuncelerDetailsArrayList = anlıkDusuncelerDetailsArrayList;
    }

    @NonNull
    @Override
    public AnlikDusuncelerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_profile_anlik_dusunceler,parent,false);
      return new AnlikDusuncelerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnlikDusuncelerHolder holder, @SuppressLint("RecyclerView") int position) {


        userLauncher();
        holder.kullanıcıAciklamasi.setText(anlıkDusuncelerDetailsArrayList.get(position).aciklama);


        String puan = anlıkDusuncelerDetailsArrayList.get(position).getPuan();

        if(puan.isEmpty()){
            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {

                    if (dil.equals("türkce")){
                        holder.puanText.setText("");
                        holder.yorumYapText.setText("Yorum yap");
                    }else if(dil.equals("ingilizce")){
                        holder.puanText.setText("");
                        holder.yorumYapText.setText("Comment");
                    }

                }
            });
        }else{
            holder.puan.setText(anlıkDusuncelerDetailsArrayList.get(position).puan);
            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {

                    if (dil.equals("türkce")){
                        holder.puanText.setText("puan");
                        holder.yorumYapText.setText("Yorum yap");
                    }else if(dil.equals("ingilizce")){
                        holder.puanText.setText("point");
                        holder.yorumYapText.setText("Comment");
                    }

                }
            });

        }


        Timestamp timestamp = anlıkDusuncelerDetailsArrayList.get(position).getTarih(); // Firebase'den gelen Timestamp değeri
        Date olusturulanTarih = timestamp.toDate();
        if(olusturulanTarih != null){
            Date endTime = new Date(); // Geçerli zamanı temsil eden endTime değişkeni

            dilTanı(new DilCallback() {
                @Override
                public void onDilCallback(String dil) {
                    if(dil.equals("türkce")){
                        String elapsedTime = GetDateDetailTurkish.calculateElapsedTime(olusturulanTarih, endTime);
                        holder.tarih.setText(elapsedTime);
                    }else if(dil.equals("ingilizce")){
                        String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                        holder.tarih.setText(elapsedTime);
                    }else{
                        String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                        holder.tarih.setText(elapsedTime);
                    }
                }
            });


        }

        String parcaAdi = anlıkDusuncelerDetailsArrayList.get(position).getParcaAdi();


        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            try {
                if (dil.equals("türkce"))
                {

                    if (parcaAdi.equals("Computer") || parcaAdi.equals("Bilgisayar") || parcaAdi.equals("Motherboards") || parcaAdi.equals("Safes") || parcaAdi.equals("Processor")
                            || parcaAdi.equals("İşlemci") || parcaAdi.equals("Anakart") || parcaAdi.equals("Kasa") || parcaAdi.equals("Ekran kartı") || parcaAdi.equals("Rams") || parcaAdi.equals("Ram")
                    )
                    {
                        holder.parcaAdi.setText("Bilgisayar");

                    }
                    else if (parcaAdi.equals("Mobile Phone") || parcaAdi.equals("Cep Telefonu"))
                    {
                        holder.parcaAdi.setText("Cep Telefonu");
                    }

                    else if (parcaAdi.equals("Television") || parcaAdi.equals("Televizyon"))
                    {
                        holder.parcaAdi.setText("Televizyon");
                    }

                    else if  (parcaAdi.equals("Game Console") || parcaAdi.equals("Oyun Konsolu") )
                    {
                        holder.parcaAdi.setText("Oyun Konsolu");
                    }

                    else if (parcaAdi.equals("Vehicle (Car-Suv-Motorcycle)") ||  parcaAdi.equals("Vasıta(Otomobil-Suv-Motosiklet)"))
                    {
                        holder.parcaAdi.setText("Vasıta");
                    }

                    else if (parcaAdi.equals("Book (Magazine-Newspaper)") || parcaAdi.equals("Kitap(Dergi-Gazete)"))
                    {
                        holder.parcaAdi.setText("Kitap");
                    }

                    else if (parcaAdi.equals("Diğer") || parcaAdi.equals("Other"))
                    {
                        holder.parcaAdi.setText("Diğer");
                    }








                } else if (dil.equals("ingilizce"))
                {

                    if (parcaAdi.equals("Computer") || parcaAdi.equals("Bilgisayar") || parcaAdi.equals("Motherboards") || parcaAdi.equals("Safes") || parcaAdi.equals("Processor")
                            || parcaAdi.equals("İşlemci") || parcaAdi.equals("Anakart") || parcaAdi.equals("Kasa") || parcaAdi.equals("Ekran kartı") || parcaAdi.equals("Rams") || parcaAdi.equals("Ram")
                    )
                    {
                        holder.parcaAdi.setText("Computer");

                    }
                    else if (parcaAdi.equals("Mobile Phone") || parcaAdi.equals("Cep Telefonu"))
                    {
                        holder.parcaAdi.setText("Mobile Phone");
                    }

                    else if (parcaAdi.equals("Television") || parcaAdi.equals("Televizyon"))
                    {
                        holder.parcaAdi.setText("Television");
                    }

                    else if (parcaAdi.equals("Game Console") || parcaAdi.equals("Oyun Konsolu") )
                    {
                        holder.parcaAdi.setText("Game Console");
                    }

                    else if (parcaAdi.equals("Vehicle (Car-Suv-Motorcycle)") ||  parcaAdi.equals("Vasıta(Otomobil-Suv-Motosiklet)"))
                    {
                        holder.parcaAdi.setText("Vehicle");
                    }

                    else if (parcaAdi.equals("Book (Magazine-Newspaper)") || parcaAdi.equals("Kitap(Dergi-Gazete)"))
                    {
                        holder.parcaAdi.setText("Book");
                    }

                    else if (parcaAdi.equals("Other")  || parcaAdi.equals("Diğer"))
                    {
                        holder.parcaAdi.setText("Other");
                    }




                }
            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }
        }
        });

        YorumlariSayisiniGör(holder,position);
        begen(anlıkDusuncelerDetailsArrayList.get(position).getGonderiId(),holder.begenImage,holder.begenText);
        begeniSayisi(anlıkDusuncelerDetailsArrayList.get(position).getGonderiId(),holder.begeniSayisiText);

        String parcaModeli = anlıkDusuncelerDetailsArrayList.get(position).parcaModeli;
        String ayrıParca = anlıkDusuncelerDetailsArrayList.get(position).ayrıParca;

        if(parcaModeli != null){
            holder.parcaModeli.setText(parcaModeli);
        }else if (parcaModeli == null){
            holder.ayrıParca.setText(ayrıParca);
        }





        String gonderen = anlıkDusuncelerDetailsArrayList.get(position).gonderenId.toString();

        DatabaseReference reference = holder.databaseReference.child(gonderen);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String kullanıcıAdı = snapshot.child("kullanıcıAdı").getValue().toString();
                holder.kullanıcıAdı.setText(kullanıcıAdı);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        FirebaseFirestore kullanıcıProfileFirestore = FirebaseFirestore.getInstance();

        kullanıcıProfileFirestore.collection("Profiles").document("Resimler").collection(gonderen).orderBy("time", Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {

                }

                if (value != null) {

                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();

                        if (data.get("ImageProfile") != null) {
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





        String gonderiId = anlıkDusuncelerDetailsArrayList.get(position).gonderiId;
        if (gonderiId != null) {



            holder.yorumyapLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeFragmentBottomNavDirections.ActionHomeFragmentBottomNavToYorumlarFragment action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToYorumlarFragment(gonderiId);
                    action.setGelenId(anlıkDusuncelerDetailsArrayList.get(position).getGonderenId().toString());
                    action.setFromWhere("profile_anlik_dusunceler");
                    Navigation.findNavController(view).navigate(action);

                }
            });
        }




        holder.begenLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.begenText.getText().equals("Beğen") || holder.begenText.getText().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("AnlikDusunce_Begeniler")
                            .child(anlıkDusuncelerDetailsArrayList.get(position).getGonderiId()).child(userId).setValue(true);








                    String gonderiId = anlıkDusuncelerDetailsArrayList.get(position).getGonderiId();

                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Kesfet").document("AnlıkDüsünce").collection("Gonderiler");

                    collectionReference.whereEqualTo("gonderiId", gonderiId).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                        documentSnapshot.getReference().update("begeniSayisi", FieldValue.increment(1));
                                    }
                                }
                            });







                }else{
                    FirebaseDatabase.getInstance().getReference().child("AnlikDusunce_Begeniler")
                            .child(anlıkDusuncelerDetailsArrayList.get(position).getGonderiId()).child(userId).removeValue();





                    String gonderiId = anlıkDusuncelerDetailsArrayList.get(position).getGonderiId();

                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Kesfet").document("AnlıkDüsünce").collection("Gonderiler");

                    collectionReference.whereEqualTo("gonderiId", gonderiId).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);


                                        documentSnapshot.getReference().update("begeniSayisi", FieldValue.increment(-1));
                                    }
                                }
                            });




                }


            }
        });









    }

    @Override
    public int getItemCount() {
        return anlıkDusuncelerDetailsArrayList.size();
    }



    private void begen(String gonderiId,ImageView begenImage,TextView begenText){

        DatabaseReference begeniDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("AnlikDusunce_Begeniler").child(gonderiId);


        begeniDatabaseYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(userId).exists()){

                    begenImage.setImageResource(R.drawable.like_full);

                    dilTanı(new DilCallback() {
                        @Override
                        public void onDilCallback(String dil) {

                            if (dil.equals("türkce")){
                                begenText.setText("Beğenildi");
                            }else if(dil.equals("ingilizce")){
                                begenText.setText("Liked");
                            }

                        }
                    });


                } else{
                    begenImage.setImageResource(R.drawable.like);

                    dilTanı(new DilCallback() {
                        @Override
                        public void onDilCallback(String dil) {

                            if (dil.equals("türkce")){
                                begenText.setText("Beğen");
                            }else if(dil.equals("ingilizce")){
                                begenText.setText("Like");
                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error:",error.getMessage());
            }
        });





    }

    private void begeniSayisi(String gonderiId,TextView begeniSayisiText){
        DatabaseReference begeniSayisiDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("AnlikDusunce_Begeniler").child(gonderiId);

        begeniSayisiDatabaseYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                begeniSayisiText.setText(String.valueOf(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error: ",error.getMessage());
            }
        });



    }


    private void YorumlariSayisiniGör(AnlikDusuncelerHolder holder, int position){


        dilTanı(new DilCallback() {

            @Override
            public void onDilCallback(String dil) {
                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Yorumlar").child(anlıkDusuncelerDetailsArrayList.get(position).getGonderiId()).child("Gonderiler");
                databaseReference2.addValueEventListener(new ValueEventListener() {
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
                            if(dil.equals("türkce")){
                                holder.textViewyorumlar.setText(yorumSayisi + " Yorum");
                            }else if(dil.equals("ingilizce")){
                                holder.textViewyorumlar.setText(yorumSayisi + " Comment");
                            }
                        }else{

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    public class AnlikDusuncelerHolder extends RecyclerView.ViewHolder{
        TextView kullanıcıAciklamasi,parcaAdi,puan,parcaModeli,kullanıcıAdı,tarih,ayrıParca,textViewyorumlar,begenText,yorumYapText,begeniSayisiText,puanText;
        CircleImageView profile;


        ConstraintLayout constraintLayout;
        LinearLayout begenLinear,yorumyapLinear;

        DatabaseReference databaseReference;


        ImageView secenekler,begenImage;
        public AnlikDusuncelerHolder(@NonNull View itemView) {
            super(itemView);

            AppCompatActivity activity = (AppCompatActivity) context;

            constraintLayout = itemView.findViewById(R.id.constrait_anlikdüsüncelerkesfet);
            secenekler = itemView.findViewById(R.id.secenekler_ücnokta_kesfetanlikdüsünceler);

            kullanıcıAciklamasi = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerAcıklama);
            kullanıcıAdı = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerKullanıcıAdı);
            parcaAdi = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerParcaAdi);
            parcaModeli = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerParcaModeli);
            ayrıParca = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerParcaModeli);
            puan = itemView.findViewById(R.id.recyclerrow_anlıkdusuncepuanSayisiText);
            puanText = itemView.findViewById(R.id.recyclerrow_anlıkdusuncepuan);
            tarih = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerTarih);
            profile = itemView.findViewById(R.id.recyclerrow_anlıkdüsüncelerProfile);

            textViewyorumlar = itemView.findViewById(R.id.recyclerrow_textViewAnlikDusunceler_yorumlar);
            begenText = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_begenText);
            begenImage = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_image);
            begeniSayisiText = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_begeni_sayisi);
            begenLinear = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_begen_linear);

            yorumYapText = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_yorumYapText);
            yorumyapLinear = itemView.findViewById(R.id.recyclerrow_anlikDusunceler_yorumlar_linear);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile");


            userLauncher();




        }




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
