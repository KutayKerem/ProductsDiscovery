package com.app.kutaykerem.productdiscovery.Adaptor.Arkadaslar;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BildirimlerAdaptor  extends RecyclerView.Adapter<BildirimlerAdaptor.BildirimlerHolder> {

    Activity activity;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference2;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    List<String> isteklerList;
    String dil;
    String userId;
    FirebaseAuth userAuth;
    String signState;

    public BildirimlerAdaptor(List<String> isteklerList,Context context) {
        this.activity = activity;
        this.context = context;
        this.firebaseDatabase = firebaseDatabase;
        this.databaseReference = databaseReference;
        this.reference = reference;
        this.firebaseFirestore = firebaseFirestore;
        this.isteklerList = isteklerList;
    }

    @NonNull
    @Override
    public BildirimlerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerrow_bildirimlerlistesi,parent,false);
      return new BildirimlerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BildirimlerHolder holder, int position) {

        String id = isteklerList.get(position);

        databaseReference.child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                DatabaseReference databaseReference1 = reference.child(id);

                if(databaseReference1 != null){


                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if(snapshot != null){
                                String kullanıcıAdı = snapshot.child("kullanıcıAdı").getValue().toString();

                                if(kullanıcıAdı != null){
                                    holder.kullanıcıAdı.setText(kullanıcıAdı);
                                }else{
                                    holder.kullanıcıAdı.setText("User");
                                }

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    holder.kullanıcıAdı.setText("User");
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        String gonderen = id;


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



        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setPos(4).setGonderen(id);
                Navigation.findNavController(view).navigate(action);

            }
        });

        holder.reddet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                istegiReddet(userId,id);


            }
        });

        holder.kabulet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                istegiKabulEt(userId,id);

            }
        });

    }
    @Override
    public int getItemCount() {
        return isteklerList.size();
    }




    public class BildirimlerHolder extends RecyclerView.ViewHolder{
        TextView kullanıcıAdı;
        CircleImageView profile;
        Button kabulet,reddet;

        public BildirimlerHolder(@NonNull View itemView) {
            super(itemView);

            kullanıcıAdı = itemView.findViewById(R.id.recyclerrow_bildirimler_kullanıcıAdı);
            profile = itemView.findViewById(R.id.recyclerrow_bildirimer_Propfile);
            kabulet = itemView.findViewById(R.id.bildirimlerkabuletbutton);
            reddet = itemView.findViewById(R.id.bildirimlerredettbutton);





            profile.setVisibility(View.VISIBLE);
            kullanıcıAdı.setVisibility(View.VISIBLE);



            databaseReference = FirebaseDatabase.getInstance().getReference("Arkadasİstekleri");
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference().child("Profile");
            databaseReference2 = FirebaseDatabase.getInstance().getReference();





            userLauncher();




            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KullanilanDiller").child(userId).child("SecilenDil");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        dil = dataSnapshot.child("dil").getValue(String.class);

                        if (dil.equals("türkce")) {
                            kabulet.setText("Kabul et");
                            reddet.setText("Reddet");
                        } else if (dil.equals("ingilizce")) {
                            kabulet.setText("To accept");
                            reddet.setText("To reject");

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


    public void istegiReddet(String userId,String hedefId){

        databaseReference.child(userId).child(hedefId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
           databaseReference.child(hedefId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void unused) {
                 //  Toast.makeText(activity.getApplicationContext(),"İstek Reddedildi",Toast.LENGTH_SHORT).show();



               }

           });
            }
        });

    }


    public void istegiKabulEt(String userId,String hedefId){

      final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
      Date today = Calendar.getInstance().getTime();

      String date = df.format(today);
        databaseReference2.child("Arkadaslar").child(userId).child(hedefId).child("tarih").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    databaseReference2.child("Arkadaslar").child(hedefId).child(userId).child("tarih").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            istegiReddet(userId,hedefId);
                          //  Toast.makeText(activity.getApplicationContext(),"İstek Kabul Edildi",Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });


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
