package com.app.kutaykerem.productdiscovery.Adaptor.Kesfet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.GetDateDetailEnglish;
import com.app.kutaykerem.productdiscovery.Models.GetDateDetailTurkish;
import com.app.kutaykerem.productdiscovery.Models.YorumlarDetails;
import com.app.kutaykerem.productdiscovery.Pages.YorumlarFragmentDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.bumptech.glide.Glide;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class YorumlarAdaptor extends RecyclerView.Adapter<YorumlarAdaptor.YorumlarHolder> {




    String dil;
    String userId;
    Context context;
    FirebaseAuth userAuth;


    int yorumPosition = -1;

    ArrayList<YorumlarDetails> yorumlarDetailsArrayList;
    String signState;
    String fromWhere, arananParca, gelenId;
    View requireView;





    public YorumlarAdaptor(ArrayList<YorumlarDetails> yorumlarDetailsArrayList,Context context,String fromWhere,String arananParca,String gelenId,View requireView) {
        this.yorumlarDetailsArrayList = yorumlarDetailsArrayList;
        this.context = context;
        this.fromWhere = fromWhere;
        this.arananParca = arananParca;
        this.gelenId = gelenId;
        this.requireView = requireView;

    }



    @NonNull

    @Override
    public YorumlarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_yorumlar,parent,false);
        return new YorumlarHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull YorumlarHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.yorum.setText(yorumlarDetailsArrayList.get(position).getYorum());

        String gonderen = yorumlarDetailsArrayList.get(position).gonderenId.toString();




        userLauncher();


        holder.tarih.setVisibility(View.VISIBLE);


        for (int i = 0; i < yorumlarDetailsArrayList.size(); i++) {
            holder.tarih.setVisibility(View.VISIBLE);
                String timestamp = yorumlarDetailsArrayList.get(position).getTarih();

                // FirebaseFirestore doğrudan Dateyi desteklediği için kesfetAdapterde stringe çevirmedik ama databaseReference desteklemediği için simpleDateFormat kullanak zorundayız.
                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                try {
                    Date olusturulanTarih = format.parse(timestamp);

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

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }



        }









        userLauncher();
        dilTanı(new DilCallback() {
            @Override
            public void onDilCallback(String dil) {

            }
        });



        DatabaseReference reference = holder.databaseReference.child(gonderen);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)

            {
                String kullanıcıAdı = snapshot.child("kullanıcıAdı").getValue().toString();



                holder.kullanıcıAdı.setText(kullanıcıAdı);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseFirestore kullanıcıProfileFirestore = FirebaseFirestore.getInstance();
        kullanıcıProfileFirestore.collection("Profiles").document("Resimler").collection(gonderen).orderBy("time", com.google.firebase.firestore.Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {



                if(value != null)
                {

                    for(DocumentSnapshot snapshot  : value.getDocuments())
                    {
                        Map<String ,Object> data = snapshot.getData();

                        if(data.get("ImageProfile") != null)
                        {
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


                        }else{
                            /*
                            Glide.with(context).load(R.drawable.sh).transform(new BitmapGlideImageCorners(30)).override(200,200).into(holder.profile);
                   */

                        }

                    }


                }

            };

        });





        holder.itemView.findViewById(R.id.yorumlarKullanıcıProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {




                YorumlarFragmentDirections.ActionYorumlarFragmentToHomeFragmentBottomNav action = YorumlarFragmentDirections.actionYorumlarFragmentToHomeFragmentBottomNav().setPos(4);
                action.setGonderen(gonderen);
                Navigation.findNavController(view).navigate(action);


            }
        });


        holder.tarih.setVisibility(View.VISIBLE);





















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
                    yorumlarText.setText("Yorumlar");
                    copyText.setText("Kopyala");
                    deleteText.setText("Kaldır");
                }else{
                    yorumlarText.setText("Comments");
                    copyText.setText("Copy");
                    deleteText.setText("Delete");
                }
            }
        });


        if(gonderen.equals(userId)){
            constraintCopy.setVisibility(View.VISIBLE);
            constraintDelete.setVisibility(View.VISIBLE);
        }else{
            constraintCopy.setVisibility(View.VISIBLE);
            constraintDelete.setVisibility(View.GONE);
        }


        if(gonderen.equals(userId)) {

            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    yorumPosition = position;
                    notifyItemChanged(position); // notifyDataSetChanges() metodu çağırarak adapter'ı güncelleyin.
                    return true; // true değerini döndürerek onLongClick() işleminin tüketilmediğini belirtin.
                }
            });


            if(yorumPosition == position){
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);



                holder.linearLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });

            } else {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }





        }else {



        }




        if(gonderen != userId){


            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    yorumPosition = position;
                    notifyItemChanged(position);
                    return true;
                }
            });

            if(yorumPosition == position){
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);


                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                });

            }else {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }






        }else {



        }






        constraintDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                yorumSil(position,view);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }



            }
        });


        constraintCopy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                yorumCopy(yorumlarDetailsArrayList.get(position),view);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }


            }
        });










    }


    public void yorumSil(int position,View view ){

        String bilgi = yorumlarDetailsArrayList.get(position).getYorumId();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Yorumlar").child(yorumlarDetailsArrayList.get(position).getGonderiId()).child("Gonderiler");
        Query query = databaseReference.orderByChild("yorumId").equalTo(bilgi);

        query.addValueEventListener(new ValueEventListener() {
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


        YorumlarFragmentDirections.ActionYorumlarFragmentSelf action = YorumlarFragmentDirections.actionYorumlarFragmentSelf(yorumlarDetailsArrayList.get(position).getGonderiId());
        action.setGelenId(gelenId);
        action.setFromWhere(fromWhere);
        action.setArananParca(arananParca);
        action.setSilinen(true);
        Navigation.findNavController(requireView).navigate(action);




    }
    public void yorumCopy(YorumlarDetails yorumlarDetails,View view){


        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("",yorumlarDetails.getYorum());
        clipboardManager.setPrimaryClip(clipData);


        if (dil.equals("türkce")) {
            Toast.makeText(view.getContext(),"Kopyalandı",Toast.LENGTH_SHORT).show();
        } else if (dil.equals("ingilizce")) {
            Toast.makeText(view.getContext(),"Copied",Toast.LENGTH_SHORT).show();

        }





    }

    @Override
    public int getItemCount() {
        return yorumlarDetailsArrayList.size();

    }

    public class YorumlarHolder extends RecyclerView.ViewHolder{
        TextView yorum,kullanıcıAdı,tarih;
        ImageView profile;
        LinearLayout linearLayout;
        FirebaseDatabase database;
        DatabaseReference databaseReference;

        public YorumlarHolder(View itemView) {
           super(itemView);

            FirebaseFirestore firebaseFirestore;
            firebaseFirestore = FirebaseFirestore.getInstance();
            linearLayout = itemView.findViewById(R.id.yorumlar_linearLayout);

            yorum = itemView.findViewById(R.id.yorumlarKullanıcıYorum);
            kullanıcıAdı = itemView.findViewById(R.id.yorumlarKullanıcıAdı);
            profile = itemView.findViewById(R.id.yorumlarKullanıcıProfile);
            tarih = itemView.findViewById(R.id.yorumlarTarih);
            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference().child("Profile");


            userLauncher();



       }


    }
    public  void dilTanı(final DilCallback dilCallback){

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
