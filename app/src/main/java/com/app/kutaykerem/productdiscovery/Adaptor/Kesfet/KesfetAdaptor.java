package com.app.kutaykerem.productdiscovery.Adaptor.Kesfet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.BitmapGlideImageCorners;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailEnglish;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailTurkish;
import com.app.kutaykerem.productdiscovery.Models.KesfetDetails;
import com.bumptech.glide.Glide;
import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;
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

public class KesfetAdaptor extends RecyclerView.Adapter<KesfetAdaptor.KesfetHolder> {

FirebaseFirestore firebaseFirestore;

ArrayList<KesfetDetails> kesfetDetailsArrayList;


String userId;
String dil;
String signState;

private Context context;


int gonderiPosition = -1;

String arananParca;
ConstraintLayout kesfeView;



    public KesfetAdaptor(String arananParca,ArrayList<KesfetDetails> kesfetDetailsArrayList,Context context,ConstraintLayout kesfeView) {
        this.kesfetDetailsArrayList = kesfetDetailsArrayList;
        this.context = context;
        this.arananParca = arananParca;
        this.kesfeView = kesfeView;
    }





    public void setKesfetDetailsArrayList(ArrayList<KesfetDetails> kesfetDetailsArrayList) {
        this.kesfetDetailsArrayList = kesfetDetailsArrayList;

    }




    @NonNull
    @Override
    public KesfetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_kesfet, parent, false);
        return new KesfetHolder(view);



        }








    @Override
    public void onBindViewHolder(@NonNull KesfetHolder holder, @SuppressLint("RecyclerView") int position) {


        userLauncher();

        holder.kullanıcıAciklamasi.setText(kesfetDetailsArrayList.get(position).aciklama);

        String puan = kesfetDetailsArrayList.get(position).getPuan();

        try {

            Uri uri = Uri.parse(kesfetDetailsArrayList.get(position).downloadUrl);

            RequestOptions requestOptions = new RequestOptions()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .encodeQuality(50);

            Glide.with(context)
                    .load(uri)
                    .apply(requestOptions)
                    .transform(new BitmapGlideImageCorners(30))
                    .into(holder.paylastıgıResim);

        }catch (Exception e){
            Log.d("ERROR:",e.getMessage());
        }




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
            holder.puan.setText(kesfetDetailsArrayList.get(position).puan);
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





        Timestamp timestamp = kesfetDetailsArrayList.get(position).getTarih(); // Firebase'den gelen Timestamp değeri
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



        String parcaAdi = kesfetDetailsArrayList.get(position).getParcaAdi();









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

                        String parcaModeli = kesfetDetailsArrayList.get(position).getParcaModeli();
                        holder.parcaAdi.setText("Kitap");


                        if (parcaModeli.equals("Memoirs (Memoirs)")) {
                            holder.parcaModeli.setText("Anı kitapları");
                        } else if (parcaModeli.equals("Novels (Novels)")) {
                            holder.parcaModeli.setText("Romanlar");
                        }
                        else if (parcaModeli.equals("Storybooks (Storybooks)")) {
                            holder.parcaModeli.setText("Hikaye kitapları");
                        } else if (parcaModeli.equals("Travel books (Travel books)")) {
                            holder.parcaModeli.setText("Seyahat kitapları");
                        } else if (parcaModeli.equals("Poetry books (Poetry books)")) {
                            holder.parcaModeli.setText("Şiir kitapları");
                        } else if (parcaModeli.equals("Biography books (Biography books)")) {
                            holder.parcaModeli.setText("Biyografi kitapları");
                        } else if (parcaModeli.equals("Religious books (Religious books)")) {
                            holder.parcaModeli.setText("Din kitapları");
                        } else if (parcaModeli.equals("Non-fiction books (Non-fiction books)")) {
                            holder.parcaModeli.setText("Bilgi kitapları");
                        } else if (parcaModeli.equals("Children's books (Children's books)")) {
                            holder.parcaModeli.setText("Çocuk kitapları");
                        } else if (parcaModeli.equals("Memoir (Memoir)")) {
                            holder.parcaModeli.setText("Anı");
                        } else if (parcaModeli.equals("Narrative (Narrative)")) {
                            holder.parcaModeli.setText("Anlatı");
                        } else if (parcaModeli.equals("Research and analysis (Research and analysis)")) {
                            holder.parcaModeli.setText("Araştırma ve analiz");
                        } else if (parcaModeli.equals("Science (Science)")) {
                            holder.parcaModeli.setText("Bilim");
                        } else if (parcaModeli.equals("Biography (Biography)")) {
                            holder.parcaModeli.setText("Biyografi");
                        } else if (parcaModeli.equals("Comic book (Comic book)")) {
                            holder.parcaModeli.setText("Çizgi roman");
                        } else if (parcaModeli.equals("Essay (Essay)")) {
                            holder.parcaModeli.setText("Deneme");
                        } else if (parcaModeli.equals("Literature (Literature)")) {
                            holder.parcaModeli.setText("Edebiyat");
                        } else if (parcaModeli.equals("Education (Education)")) {
                            holder.parcaModeli.setText("Eğitim");
                        } else if (parcaModeli.equals("Philosophy (Philosophy)")) {
                            holder.parcaModeli.setText("Felsefe");
                        } else if (parcaModeli.equals("Youth (Youth)")) {
                            holder.parcaModeli.setText("Gençlik");
                        } else if (parcaModeli.equals("Travel (Travel)")) {
                            holder.parcaModeli.setText("Gezi");
                        } else if (parcaModeli.equals("Story (Story)")) {
                            holder.parcaModeli.setText("Hikaye");
                        } else if (parcaModeli.equals("Hobby (Hobby)")) {
                            holder.parcaModeli.setText("Hobi");
                        } else if (parcaModeli.equals("Analysis (Analysis)")) {
                            holder.parcaModeli.setText("Analiz");
                        } else if (parcaModeli.equals("Business, Economics, and Law (Business, Economics, and Law)")) {
                            holder.parcaModeli.setText("İşletme, Ekonomi ve Hukuk");
                        } else if (parcaModeli.equals("Personal development (Personal development)")) {
                            holder.parcaModeli.setText("Kişisel gelişim");
                        } else if (parcaModeli.equals("Speeches (Speeches)")) {
                            holder.parcaModeli.setText("Konuşmalar");
                        } else if (parcaModeli.equals("Fairy tale (Fairy tale)")) {
                            holder.parcaModeli.setText("Masal");
                        } else if (parcaModeli.equals("Letter (Letter)")) {
                            holder.parcaModeli.setText("Mektup");
                        } else if (parcaModeli.equals("Humor (Humor)")) {
                            holder.parcaModeli.setText("Mizah");
                        } else if (parcaModeli.equals("Short story (Short story)")) {
                            holder.parcaModeli.setText("Kısa hikaye");
                        } else if (parcaModeli.equals("Detective (Detective)")) {
                            holder.parcaModeli.setText("Polisiye");
                        } else if (parcaModeli.equals("Psychology (Psychology)")) {
                            holder.parcaModeli.setText("Psikoloji");
                        } else if (parcaModeli.equals("Illustrated story (Illustrated story)")) {
                            holder.parcaModeli.setText("Resimli hikaye");
                        } else if (parcaModeli.equals("Novel (Novel)")) {
                            holder.parcaModeli.setText("Roman");
                        } else if (parcaModeli.equals("Health (Health)")) {
                            holder.parcaModeli.setText("Sağlık");
                        } else if (parcaModeli.equals("Art and design (Art and design)")) {
                            holder.parcaModeli.setText("Sanat ve tasarım");
                        } else if (parcaModeli.equals("Art and music (Art and music)")) {
                            holder.parcaModeli.setText("Sanat ve müzik");
                        } else if (parcaModeli.equals("Cinema history (Cinema history)")) {
                            holder.parcaModeli.setText("Sinema tarihi");
                        } else if (parcaModeli.equals("Interview (Interview)")) {
                            holder.parcaModeli.setText("Röportaj");
                        } else if (parcaModeli.equals("Poetry (Poetry)")) {
                            holder.parcaModeli.setText("Şiir");
                        } else if (parcaModeli.equals("History (History)")) {
                            holder.parcaModeli.setText("Tarih");
                        } else if (parcaModeli.equals("Cookbooks (Cookbooks)")) {
                            holder.parcaModeli.setText("Yemek kitapları");
                        } else if (parcaModeli.equals("Other")) {
                            holder.parcaModeli.setText("Diğer");
                        }else{
                            holder.parcaModeli.setText(kesfetDetailsArrayList.get(position).getParcaModeli());
                        }





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
                        String parcaModeli = kesfetDetailsArrayList.get(position).getParcaModeli();


                        if (parcaModeli.equals("Anı kitapları")) {
                            holder.parcaModeli.setText("Memoirs (Memoirs)");
                        } else if (parcaModeli.equals("Romanlar")) {
                            holder.parcaModeli.setText("Novels (Novels)");
                        } else if (parcaModeli.equals("Hikaye kitapları")) {
                            holder.parcaModeli.setText("Storybooks (Storybooks)");
                        } else if (parcaModeli.equals("Gezi kitapları")) {
                            holder.parcaModeli.setText("Travel books (Travel books)");
                        } else if (parcaModeli.equals("Şiir kitapları")) {
                            holder.parcaModeli.setText("Poetry books (Poetry books)");
                        } else if (parcaModeli.equals("Biyografi kitapları")) {
                            holder.parcaModeli.setText("Biography books (Biography books)");
                        } else if (parcaModeli.equals("Din kitapları")) {
                            holder.parcaModeli.setText("Religious books (Religious books)");
                        } else if (parcaModeli.equals("Bilgi kitapları")) {
                            holder.parcaModeli.setText("Non-fiction books (Non-fiction books)");
                        } else if (parcaModeli.equals("Çocuk kitapları")) {
                            holder.parcaModeli.setText("Children's books (Children's books)");
                        } else if (parcaModeli.equals("Anı")) {
                            holder.parcaModeli.setText("Memoir (Memoir)");
                        } else if (parcaModeli.equals("Anlatı")) {
                            holder.parcaModeli.setText("Narrative (Narrative)");
                        } else if (parcaModeli.equals("Araştırma-İnceleme")) {
                            holder.parcaModeli.setText("Research and analysis (Research and analysis)");
                        } else if (parcaModeli.equals("Bilim")) {
                            holder.parcaModeli.setText("Science (Science)");
                        } else if (parcaModeli.equals("Biyografi")) {
                            holder.parcaModeli.setText("Biography (Biography)");
                        } else if (parcaModeli.equals("Çizgi roman")) {
                            holder.parcaModeli.setText("Comic book (Comic book)");
                        } else if (parcaModeli.equals("Deneme")) {
                            holder.parcaModeli.setText("Essay (Essay)");
                        } else if (parcaModeli.equals("Edebiyat")) {
                            holder.parcaModeli.setText("Literature (Literature)");
                        } else if (parcaModeli.equals("Eğitim")) {
                            holder.parcaModeli.setText("Education (Education)");
                        } else if (parcaModeli.equals("Felsefe")) {
                            holder.parcaModeli.setText("Philosophy (Philosophy)");
                        } else if (parcaModeli.equals("Gençlik")) {
                            holder.parcaModeli.setText("Youth (Youth)");
                        } else if (parcaModeli.equals("Gezi")) {
                            holder.parcaModeli.setText("Travel (Travel)");
                        } else if (parcaModeli.equals("Hikaye")) {
                            holder.parcaModeli.setText("Story (Story)");
                        } else if (parcaModeli.equals("Hobi")) {
                            holder.parcaModeli.setText("Hobby (Hobby)");
                        } else if (parcaModeli.equals("İnceleme")) {
                            holder.parcaModeli.setText("Analysis (Analysis)");
                        } else if (parcaModeli.equals("İş Ekonomi - Hukuk")) {
                            holder.parcaModeli.setText("Business, Economics, and Law (Business, Economics, and Law)");
                        } else if (parcaModeli.equals("Kişisel gelişim")) {
                            holder.parcaModeli.setText("Personal development (Personal development)");
                        } else if (parcaModeli.equals("Konuşmalar")) {
                            holder.parcaModeli.setText("Speeches (Speeches)");
                        } else if (parcaModeli.equals("Masal")) {
                            holder.parcaModeli.setText("Fairy tale (Fairy tale)");
                        } else if (parcaModeli.equals("Mektup")) {
                            holder.parcaModeli.setText("Letter (Letter)");
                        } else if (parcaModeli.equals("Mizah")) {
                            holder.parcaModeli.setText("Humor (Humor)");
                        } else if (parcaModeli.equals("Öykü")) {
                            holder.parcaModeli.setText("Short story (Short story)");
                        } else if (parcaModeli.equals("Polisiye")) {
                            holder.parcaModeli.setText("Detective (Detective)");
                        } else if (parcaModeli.equals("Psikoloji")) {
                            holder.parcaModeli.setText("Psychology (Psychology)");
                        } else if (parcaModeli.equals("Resimli Öykü")) {
                            holder.parcaModeli.setText("Illustrated story (Illustrated story)");
                        } else if (parcaModeli.equals("Roman")) {
                            holder.parcaModeli.setText("Novel (Novel)");
                        } else if (parcaModeli.equals("Sağlık")) {
                            holder.parcaModeli.setText("Health (Health)");
                        } else if (parcaModeli.equals("Sanat - Tasarım")) {
                            holder.parcaModeli.setText("Art and design (Art and design)");
                        } else if (parcaModeli.equals("Sanat- Müzik")) {
                            holder.parcaModeli.setText("Art and music (Art and music)");
                        } else if (parcaModeli.equals("Sinema Tarihi")) {
                            holder.parcaModeli.setText("Cinema history (Cinema history)");
                        } else if (parcaModeli.equals("Söyleşi")) {
                            holder.parcaModeli.setText("Interview (Interview)");
                        } else if (parcaModeli.equals("Şiir")) {
                            holder.parcaModeli.setText("Poetry (Poetry)");
                        } else if (parcaModeli.equals("Tarih")) {
                            holder.parcaModeli.setText("History (History)");
                        } else if (parcaModeli.equals("Yemek kitapları")) {
                            holder.parcaModeli.setText("Cookbooks (Cookbooks)");
                        } else if (parcaModeli.equals("Diğer")) {
                            holder.parcaModeli.setText("Other");
                        }else{
                            holder.parcaModeli.setText(kesfetDetailsArrayList.get(position).getParcaModeli());
                        }


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

        YorumlariSayisiniGör(holder,dil,position);
        begen(kesfetDetailsArrayList.get(position).getGonderiId(),holder.begenImage,holder.begenText);
        begeniSayisi(kesfetDetailsArrayList.get(position).getGonderiId(),holder.begeniSayisiText);


            String parcaModeli = kesfetDetailsArrayList.get(position).parcaModeli;
            String ayrıParca = kesfetDetailsArrayList.get(position).ayrıParca;

            if (parcaModeli != null) {
                holder.parcaModeli.setText(parcaModeli);
            } else if (parcaModeli == null) {
                holder.ayrıParca.setText(ayrıParca);
            }
        String gonderen = kesfetDetailsArrayList.get(position).gonderenId.toString();
            try{


            DatabaseReference reference = holder.databaseReference.child(kesfetDetailsArrayList.get(position).getGonderenId());

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

                            try {



                            if (data.get("ImageProfile") != null) {
                                String image = (String) data.get("ImageProfile");

                                Uri uri = Uri.parse(image);

                                    RequestOptions requestOptions = new RequestOptions()
                                            .format(DecodeFormat.PREFER_RGB_565)
                                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                            .encodeQuality(50);

                                    Glide.with(context)
                                            .load(uri)
                                            .apply(requestOptions)
                                            .into(holder.profile);







                            }

                            }catch (Exception e){
                                Log.d("Error:",e.getLocalizedMessage());
                            }

                        }
                    }
                }



            });
            }catch (Exception e){
                Log.d("Error:",e.getLocalizedMessage());
            }


            holder.itemView.findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    // Gezinti
                    String gonderen = kesfetDetailsArrayList.get(position).gonderenId.toString();
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(gonderen);
                    Navigation.findNavController(view).navigate(action);

                }


            });


            String gonderiId = kesfetDetailsArrayList.get(position).gonderiId;
            if (gonderiId != null) {



                holder.linearYorumYap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        kesfeView.setVisibility(View.GONE);
                        NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToYorumlarFragment(gonderiId)
                                .setFromWhere("kesfet")
                                .setArananParca(arananParca)
                                .setGelenId(kesfetDetailsArrayList.get(position).getGonderenId());
                        Navigation.findNavController(view).navigate(action);

                    }
                });


            }


            holder.paylastıgıResim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToImageToScreenFragment(kesfetDetailsArrayList.get(position).getDownloadUrl(),"kesfet").setArananParca(arananParca);
                    Navigation.findNavController(view).navigate(action);

                }
            });















        holder.secenekler.setVisibility(View.GONE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);
        ConstraintLayout constraintCopy = dialog.findViewById(R.id.linearLayoutCopyYorum);
        constraintCopy.setVisibility(View.GONE);

        ConstraintLayout constraintDelete = dialog.findViewById(R.id.linearLayoutDeleteYorum);

        TextView yorumlarText =  dialog.findViewById(R.id.bottom_sheet_yorumlarText);
        TextView deleteText =  dialog.findViewById(R.id.bottom_sheet_silText);

        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if(dil.equals("türkce")){
                    yorumlarText.setText("Yorumlar");
                    deleteText.setText("Kaldır");
                }else{
                    yorumlarText.setText("Comments");
                    deleteText.setText("Delete");
                }
            }
        });



        if(gonderen.equals(userId)){
            constraintDelete.setVisibility(View.VISIBLE);
        }else{
            constraintDelete.setVisibility(View.GONE);
        }



            if (kesfetDetailsArrayList.get(position).getGonderenId().equals(userId)) {

                holder.secenekler.setVisibility(View.VISIBLE);

                holder.secenekler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        gonderiPosition = position;
                       // notifyItemChanged(position);


                    }
                });

                if (gonderiPosition == position) {


                    holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });


                }else{
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }



                constraintDelete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        GonderiSil(position, view);

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });


            }else{
                holder.secenekler.setVisibility(View.GONE);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }




            holder.linearBegen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.begenText.getText().equals("Beğen") || holder.begenText.getText().equals("Like")){

                        FirebaseDatabase.getInstance().getReference().child("Gonderi_Begeniler")
                                .child(kesfetDetailsArrayList.get(position).getGonderiId()).child(userId).setValue(true);


                        String gonderiId = kesfetDetailsArrayList.get(position).getGonderiId();

                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Kesfet").document("Gonderi").collection("Gonderiler");

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
                        FirebaseDatabase.getInstance().getReference().child("Gonderi_Begeniler")
                                .child(kesfetDetailsArrayList.get(position).getGonderiId()).child(userId).removeValue();



                        String gonderiId = kesfetDetailsArrayList.get(position).getGonderiId();

                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Kesfet").document("Gonderi").collection("Gonderiler");

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
    private void begen(String gonderiId,ImageView begenImage,TextView begenText){

        DatabaseReference begeniDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("Gonderi_Begeniler").child(gonderiId);


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
        DatabaseReference begeniSayisiDatabaseYolu = FirebaseDatabase.getInstance().getReference().child("Gonderi_Begeniler").child(gonderiId);

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
    private void YorumlariSayisiniGör(KesfetHolder holder,String dil, int position){


        dilTanı(new DilCallback() {

            @Override
            public void onDilCallback(String dil) {

                if (!kesfetDetailsArrayList.isEmpty()){
                    if (!kesfetDetailsArrayList.get(position).getGonderiId().isEmpty()){
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Yorumlar").child(kesfetDetailsArrayList.get(position).getGonderiId()).child("Gonderiler");
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
                                        holder.textViewyorumlar.setText(yorumSayisi + " Comments");
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






            }
        });

    }




    private void GonderiSil(int position, View view) {
        String bilgi = kesfetDetailsArrayList.get(position).getGonderiId();
        CollectionReference collectionReference = firebaseFirestore.getInstance().collection("Kesfet").document("Gonderi").collection("Gonderiler");
        Query query = collectionReference.whereEqualTo("gonderiId",bilgi);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){

                }

                if(value != null){

                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        documentSnapshot.getReference().delete();

                    }



            if (dil.equals("türkce")) {
                Toast.makeText(view.getContext(),"Gönderi silindi",Toast.LENGTH_LONG).show();
            } else if (dil.equals("ingilizce")) {
                Toast.makeText(view.getContext(),"post deleted",Toast.LENGTH_LONG).show();

            }

        }
            }

        });


        kesfetDetailsArrayList.clear();
        notifyDataSetChanged();




    }




    @Override
    public int getItemCount() {

        return kesfetDetailsArrayList.size();
    }

    public class KesfetHolder extends RecyclerView.ViewHolder{


        TextView kullanıcıAciklamasi,parcaAdi,puan,parcaModeli,kullanıcıAdı,tarih,ayrıParca,textViewyorumlar,puanText,begenText,yorumYapText,begeniSayisiText;
        CircleImageView profile;
        ImageView  paylastıgıResim,secenekler,begenImage;
        LinearLayout linearBegen,linearYorumYap ;

        ConstraintLayout constraintLayout;
        FirebaseDatabase database;
        DatabaseReference databaseReference;




        @SuppressLint("WrongViewCast")
        public KesfetHolder(View itemView) {
            super(itemView);
            AppCompatActivity activity = (AppCompatActivity) context;

            constraintLayout = itemView.findViewById(R.id.constrait_kesfet);
            secenekler = itemView.findViewById(R.id.secenekler_ücnokta_kesfet);
            kullanıcıAciklamasi = itemView.findViewById(R.id.kullaniciAciklamasiText);
            parcaAdi = itemView.findViewById(R.id.parcaAdiText);
            puan = itemView.findViewById(R.id.puanSayisiText);
            puanText = itemView.findViewById(R.id.puanText);
            yorumYapText = itemView.findViewById(R.id.recyclerrow_kesfet_yorumYapText);
            begenText = itemView.findViewById(R.id.recyclerrow_kesfet_begenText);
            begeniSayisiText = itemView.findViewById(R.id.recyclerrow_kesfet_begeni_sayisi);
            linearBegen = itemView.findViewById(R.id.recyclerrow_kesfet_begen);
            begenImage = itemView.findViewById(R.id.recyclerrow_begen_image);
            linearYorumYap = itemView.findViewById(R.id.recyclerrow_kesfet_yorumlar);
            kullanıcıAdı = itemView.findViewById(R.id.kulllaniciAdiText);
            tarih   = itemView.findViewById(R.id.tarihText);
            paylastıgıResim = itemView.findViewById(R.id.kullaniciPaylastigiResim);
            profile =  (CircleImageView) itemView.findViewById(R.id.profile);
            parcaModeli = itemView.findViewById(R.id.parcaModeliText);
            ayrıParca = itemView.findViewById(R.id.parcaModeliText);
            textViewyorumlar = itemView.findViewById(R.id.textViewyorumlar);



            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference().child("Profile");





            firebaseFirestore = FirebaseFirestore.getInstance();

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
