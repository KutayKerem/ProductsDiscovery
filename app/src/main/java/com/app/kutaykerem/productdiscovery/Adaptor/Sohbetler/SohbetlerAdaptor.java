package com.app.kutaykerem.productdiscovery.Adaptor.Sohbetler;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.BitmapGlideImageCorners;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailEnglish;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailTurkish;
import com.app.kutaykerem.productdiscovery.Models.Sohbetlerdetails;
import com.app.kutaykerem.productdiscovery.Pages.SohbetlerFragmentDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SohbetlerAdaptor extends RecyclerView.Adapter<SohbetlerAdaptor.SohbetlerHolder> {



    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Boolean state =false;
    int mesajPosition = -1;
    int resimPosition= -1;




    Context context;

    String dil;
    String userId;


    String signState;




    private String gelenKullaniciId;

    ArrayList<String> arrayListGelenKullaniciId;



    int view_type_user=1;
    int view_type_karsı=2;


    private OnItemClickListener listener;




    public interface OnItemClickListener {
        void onItemClick(Sohbetlerdetails item , int position, Boolean state);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        this.state = state;

    }

    ArrayList<Sohbetlerdetails> sohbetlerdetailsArrayList;

    public SohbetlerAdaptor(ArrayList<Sohbetlerdetails> sohbetlerdetailsArrayList,Context context,ArrayList<String> arrayListGelenKullaniciId) {
        this.firebaseAuth = firebaseAuth;
        this.user = user;
        this.state = state;
        this.view_type_user = view_type_user;
        this.view_type_karsı = view_type_karsı;
        this.sohbetlerdetailsArrayList = sohbetlerdetailsArrayList;
        this.context = context;
        this.arrayListGelenKullaniciId = arrayListGelenKullaniciId;
    }


    public SohbetlerAdaptor(ArrayList<Sohbetlerdetails> sohbetlerDetailsArrayList, ArrayList<String> arrayListGelenKullaniciId, Context requireContext) {
        this.arrayListGelenKullaniciId = arrayListGelenKullaniciId;
        this.sohbetlerdetailsArrayList = sohbetlerDetailsArrayList;
        this.context = requireContext;
    }


    @Override
    public int getItemViewType(int position) {


        AppCompatActivity activity = (AppCompatActivity) context;


        userLauncher();




        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    dil = dataSnapshot.child("dil").getValue(String.class);
                }else{

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });


      String hedefId = String.valueOf(sohbetlerdetailsArrayList.get(position).getFrom());

        if(userId.equals(hedefId)){
            state = true;
            return view_type_user;
        }else{

            state = false;
            return view_type_karsı;
        }


    }


    @NonNull
    @Override
    public SohbetlerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == view_type_user){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerrow_sohbetler_user,parent,false);
            return new SohbetlerHolder(view);

        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerrow_sohbetler_karsi,parent,false);
            return new SohbetlerHolder(view);

        }



    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SohbetlerHolder holder, @SuppressLint("RecyclerView") int position) {


        Sohbetlerdetails sohbetlerdetails = sohbetlerdetailsArrayList.get(position);

        String id = sohbetlerdetails.getFarklıKullanici();






                holder.mesajTime.setVisibility(View.VISIBLE);
                String timestamp = sohbetlerdetails.getTime();

                // FirebaseFirestore doğrudan Dateyi desteklediği için kesfetAdapterde stringe çevirmedik ama databaseReference desteklemediği için simpleDateFormat kullanak zorundayız.
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);


                try {
                    Date olusturulanTarih = format.parse(timestamp);
                    if(olusturulanTarih != null){
                        Date endTime = new Date(); // Geçerli zamanı temsil eden endTime değişkeni

                        dilTanı(new DilCallback() {
                            @Override
                            public void onDilCallback(String dil) {
                                if(dil.equals("türkce")){
                                    String elapsedTime = GetDateDetailTurkish.calculateElapsedTime(olusturulanTarih, endTime);
                                    holder.mesajTime.setText(elapsedTime);
                                }else if(dil.equals("ingilizce")){
                                    String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                                    holder.mesajTime.setText(elapsedTime);
                                }else{
                                    String elapsedTime = GetDateDetailEnglish.calculateElapsedTime(olusturulanTarih, endTime);
                                    holder.mesajTime.setText(elapsedTime);
                                }
                            }
                        });


                    }

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }







        if(sohbetlerdetails.getType().equals("text")){
            holder.progressBar.setVisibility(View.GONE);
            holder.resim.setVisibility(View.GONE);
            holder.mesaj.setVisibility(View.VISIBLE);
            holder.mesaj.setText(sohbetlerdetailsArrayList.get(position).getMesaj());
        }else{

            holder.resim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gelenKullaniciId = "";
                    if (arrayListGelenKullaniciId.size() > 0) {
                        gelenKullaniciId = arrayListGelenKullaniciId.get(0);
                    }
                    printResim(position,view,gelenKullaniciId);
                }
            });

            holder.resim.setVisibility(View.VISIBLE);
            holder.mesaj.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);




            try {

                Uri uri = Uri.parse(sohbetlerdetailsArrayList.get(position).getMesaj());

                RequestOptions requestOptions = new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .encodeQuality(50);

                Glide.with(context)
                        .load(uri)
                        .apply(requestOptions)
                        .transform(new BitmapGlideImageCorners(30))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.resim);


            }catch (Exception e){
                Log.d("ERROR:",e.getMessage());
            }




        }





        Sohbetlerdetails chat = sohbetlerdetailsArrayList.get(position);

        if(chat.getFrom().equals(userId)){
            if(position == sohbetlerdetailsArrayList.size() -1){
                if(chat.getSeen()){

                    if(chat.getType().equals("text")){
                        holder.goruldu.setVisibility(View.VISIBLE);
                        holder.gorulduResim.setVisibility(View.GONE);
                    }else{
                        holder.gorulduResim.setVisibility(View.VISIBLE);
                        holder.goruldu.setVisibility(View.GONE);
                    }
                }else{
                    holder.goruldu.setVisibility(View.GONE);
                    holder.gorulduResim.setVisibility(View.GONE);
                }

            }else{
                holder.goruldu.setVisibility(View.GONE);
                holder.gorulduResim.setVisibility(View.GONE);
            }
        }








        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet);
        ConstraintLayout constraintCopy = dialog.findViewById(R.id.linearLayoutCopyYorum);
        ConstraintLayout constraintDelete = dialog.findViewById(R.id.linearLayoutDeleteYorum);

        TextView yorumlarText =  dialog.findViewById(R.id.bottom_sheet_yorumlarText);
        TextView copyText =  dialog.findViewById(R.id.bottom_sheet_kopyalaText);
        TextView deleteText =  dialog.findViewById(R.id.bottom_sheet_silText);
        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {
                if(dil.equals("türkce")){
                    yorumlarText.setText("Sohbet");
                    copyText.setText("Kopyala");
                    deleteText.setText("Kaldır");
                }else{
                    yorumlarText.setText("Chat");
                    copyText.setText("Copy");
                    deleteText.setText("Delete");
                }
            }
        });


        if(sohbetlerdetailsArrayList.get(position).getFrom().equals(userId)){
            if (sohbetlerdetailsArrayList.get(position).getType().equals("text")){
                constraintCopy.setVisibility(View.VISIBLE);
                constraintDelete.setVisibility(View.VISIBLE);
            }else{
                constraintCopy.setVisibility(View.GONE);
                constraintDelete.setVisibility(View.VISIBLE);
            }


        }else{
            constraintDelete.setVisibility(View.GONE);

            if (sohbetlerdetailsArrayList.get(position).getType().equals("text")){
                constraintCopy.setVisibility(View.VISIBLE);
            }else{
                constraintCopy.setVisibility(View.GONE);
            }


        }



        if(sohbetlerdetailsArrayList.get(position).getFrom().equals(userId)){




            holder.mesaj.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);


                    mesajPosition = position;
                    resimPosition = -1;
                   //  notifyItemChanged(position);
                    return true;
                }

            });

            holder.resim.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);


                    resimPosition = position;
                    mesajPosition = -1;
                    notifyItemChanged(position);
                    return true;
                }
            });


            if(resimPosition == position){




                holder.resim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });

                holder.mesaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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





            if (mesajPosition == position) {




                holder.linearMesaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });

                holder.mesaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });



            }





        }





        if (sohbetlerdetailsArrayList.get(position).getHedefId().equals(userId)){




            holder.mesaj.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.BOTTOM);


                    resimPosition = -1;
                    mesajPosition = position;
                    notifyItemChanged(position);
                    return true;
                }

            });



            if(mesajPosition == position){


                holder.cardViewSohbetler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                holder.mesajKarsi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });


            }

        }



        constraintCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mesajCopy(sohbetlerdetails,position,holder,dialog);


                }catch (Exception e){
                    Log.d("Error :", e.getMessage());
                }





            }
        });
        constraintDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    mesajDelete(sohbetlerdetails,position,view,dialog);
                }catch (Exception e){
                    Log.d("Error :", e.getMessage());
                }


            }
        });





    }

    public void mesajCopy(Sohbetlerdetails sohbetlerdetails,int position,SohbetlerHolder holder,Dialog dialog){

        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("",sohbetlerdetails.getMesaj());
        clipboardManager.setPrimaryClip(clipData);

        if (dil.equals("türkce")) {
            Toast.makeText(context,"Kopyalandı",Toast.LENGTH_LONG).show();
        } else if (dil.equals("ingilizce")) {
            Toast.makeText(context,"Copied",Toast.LENGTH_LONG).show();

        }


        notifyItemChanged(position);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }


    }

    public void mesajDelete(Sohbetlerdetails sohbetlerdetails,int position,View view,Dialog dialog){

        String bilgi = sohbetlerdetails.getMesajId();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Mesajlar");
        com.google.firebase.database.Query query = reference.orderByChild("mesajId").equalTo(bilgi);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Sohbetlerdetails sohbetlerdetails = dataSnapshot.getValue(Sohbetlerdetails.class);
                        if (sohbetlerdetails != null) {
                            String fromId = sohbetlerdetails.getFrom();
                            String hedefId = sohbetlerdetails.getHedefId();

                            if (hedefId != null && fromId != null) {
                                if ((sohbetlerdetails.getHedefId().equals(userId) && sohbetlerdetails.getFrom().equals(sohbetlerdetails.getHedefId()))
                                        || (sohbetlerdetails.getHedefId().equals(sohbetlerdetails.getHedefId()) && sohbetlerdetails.getFrom().equals(userId))) {

                                    dataSnapshot.getRef().removeValue();


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

        String bilgi2 = sohbetlerdetails.getMesajId();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Mesajlar");
        com.google.firebase.database.Query query2 = reference2.orderByChild("mesajId").equalTo(bilgi2);

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Sohbetlerdetails sohbetlerdetails = dataSnapshot.getValue(Sohbetlerdetails.class);
                        if (sohbetlerdetails != null) {
                            String fromId = sohbetlerdetails.getFrom();
                            String hedefId = sohbetlerdetails.getHedefId();

                            if (hedefId != null && fromId != null) {
                                if ((sohbetlerdetails.getHedefId().equals(userId) && sohbetlerdetails.getFrom().equals(sohbetlerdetails.getHedefId()))
                                        || (sohbetlerdetails.getHedefId().equals(sohbetlerdetails.getHedefId()) && sohbetlerdetails.getFrom().equals(userId))) {

                                    dataSnapshot.getRef().removeValue();


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



        // Arkadaslar listesinden kaldırma işlemleri

        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("ArkadaslarListesiMesajlar").child(userId).child(sohbetlerdetails.getHedefId());
        com.google.firebase.database.Query query3 = reference3.orderByChild("mesajId").equalTo(bilgi);

        query3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot != null){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference().child("ArkadaslarListesiMesajlar").child(sohbetlerdetails.getHedefId()).child(userId);
        com.google.firebase.database.Query query4 = reference4.orderByChild("mesajId").equalTo(bilgi2);

        query4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot != null) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (dil.equals("türkce")) {
            Toast.makeText(context,"Mesaj gönderimi iptal edildi",Toast.LENGTH_LONG).show();
        } else if (dil.equals("ingilizce")) {
            Toast.makeText(context,"Message canceled",Toast.LENGTH_LONG).show();

        }



        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if(sohbetlerdetailsArrayList.get(position).getFrom().equals(userId)){
            NavDirections action = SohbetlerFragmentDirections.actionSohbetlerFragmentSelf().setKullaniciId(sohbetlerdetailsArrayList.get(position).getHedefId());
            Navigation.findNavController(view).navigate(action);
        }

    }



    private void printResim(int position, View view , String id) {

        String resim = sohbetlerdetailsArrayList.get(position).getMesaj();

        SohbetlerFragmentDirections.ActionSohbetlerFragmentToImageToScreenFragment action = SohbetlerFragmentDirections.actionSohbetlerFragmentToImageToScreenFragment(resim,"sohbetler");
        action.setHedefId(id);
        Navigation.findNavController(view).navigate(action);

    }






    @Override
    public int getItemCount() {
        return sohbetlerdetailsArrayList.size();
    }

    public class SohbetlerHolder extends RecyclerView.ViewHolder{

        TextView mesaj,goruldu,gorulduResim ,mesajTime;
        TextView mesajKarsi;
        ImageView resim;
        ProgressBar progressBar;
        CardView  cardViewSohbetler;
        LinearLayout linearMesaj;

        ConstraintLayout  constraintLayoutUser;


        public SohbetlerHolder(@NonNull View itemView) {
            super(itemView);








            goruldu = itemView.findViewById(R.id.sohbetler_goruldu);
            resim = itemView.findViewById(R.id.sohbetler_resim);
            gorulduResim = itemView.findViewById(R.id.sohbetler_goruldu_resim);
            progressBar = itemView.findViewById(R.id.sohbetler_progressbar);
            mesajTime = itemView.findViewById(R.id.recyclerrow_mesaj_time);
            constraintLayoutUser = itemView.findViewById(R.id.arkaplan);
            mesajKarsi = itemView.findViewById(R.id.recyclerrow_mesaj_karsi);

            linearMesaj = itemView.findViewById(R.id.linearMesaj);

            cardViewSohbetler = itemView.findViewById(R.id.cardViewSohbetler);
            if(cardViewSohbetler != null){
                cardViewSohbetler.setBackground(null);
            }


            if(state == true){
                mesaj = itemView.findViewById(R.id.recyclerrow_mesaj_user);
            }else{
                mesaj = itemView.findViewById(R.id.recyclerrow_mesaj_karsi);
            }


            userLauncher();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        dil = dataSnapshot.child("dil").getValue(String.class);


                        if (dil.equals("türkce")) {
                            gorulduResim.setText("Görüldü");
                            goruldu.setText("Görüldü");

                        } else if (dil.equals("ingilizce")) {
                            goruldu.setText("Seen");
                            gorulduResim.setText("Seen");
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





}
