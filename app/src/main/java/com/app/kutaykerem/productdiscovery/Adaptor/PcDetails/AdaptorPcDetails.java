package com.app.kutaykerem.productdiscovery.Adaptor.PcDetails;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.PcDetails;
import com.app.kutaykerem.productdiscovery.Parts.ProductsFragmentDirections;
import com.app.kutaykerem.productdiscovery.R;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;

public class AdaptorPcDetails extends RecyclerView.Adapter<AdaptorPcDetails.HolderAnakart> {



    String dil;
    String userId;
    FirebaseAuth userAuth;
    ArrayList<PcDetails> pcDetailsArrayList;
    private Context context;
    String signState;

    public AdaptorPcDetails(ArrayList<PcDetails> pcDetailsArrayList,Context context) {
        this.pcDetailsArrayList = pcDetailsArrayList;
        this.context = context;

    }
    public void setPcDetailsArrayList(ArrayList<PcDetails> pcDetailsArrayList) {
        this.pcDetailsArrayList = pcDetailsArrayList;
    }


    @NonNull
    @Override
    public HolderAnakart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pc_info,parent,false);
        return new HolderAnakart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAnakart holder, int position) {
        holder.parcaAdi.setText(pcDetailsArrayList.get(position).getName());
        holder.parcaModeli.setText(pcDetailsArrayList.get(position).getDetailsName());

        holder.parcaModeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String names = holder.parcaModeli.getText().toString();
                String parcaAdi = holder.parcaAdi.getText().toString();




                if (!parcaAdi.isEmpty()){
                    ProductsFragmentDirections.ActionProductsFragmentToHomeFragmentBottomNav action = ProductsFragmentDirections.actionProductsFragmentToHomeFragmentBottomNav().setPos(1);
                    action.setArananParca(names);
                    Navigation.findNavController(view).navigate(action);
                }




            }
        });


        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String names = holder.parcaModeli.getText().toString();
                String parcaAdi = holder.parcaAdi.getText().toString();


                if (!parcaAdi.isEmpty()) {
                    ProductsFragmentDirections.ActionProductsFragmentToHomeFragmentBottomNav action = ProductsFragmentDirections.actionProductsFragmentToHomeFragmentBottomNav().setPos(1);
                    action.setArananParca(names);
                    Navigation.findNavController(view).navigate(action);
                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return pcDetailsArrayList.size();
    }

    public class HolderAnakart extends RecyclerView.ViewHolder{

        TextView parcaAdi;
        TextView parcaModeli;
        ImageView ok;

        public HolderAnakart(@NonNull View itemView) {
            super(itemView);

             parcaAdi = itemView.findViewById(R.id.parcaAdi);
             parcaModeli = itemView.findViewById(R.id.parcaModeli);
             ok = itemView.findViewById(R.id.recycler_parca_ok);





            userLauncher();

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
