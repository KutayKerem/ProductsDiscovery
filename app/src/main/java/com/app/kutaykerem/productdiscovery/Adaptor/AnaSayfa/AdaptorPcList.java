package com.app.kutaykerem.productdiscovery.Adaptor.AnaSayfa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.kutaykerem.productdiscovery.Models.BitmapGlideImageCorners;
import com.app.kutaykerem.productdiscovery.Models.PcNames;
import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;

import java.util.List;

public class AdaptorPcList extends RecyclerView.Adapter<AdaptorPcList.Holder> {





    List<PcNames> pcNamesArrayList;
    Context context;


    public AdaptorPcList(Context context,List<PcNames> pcNamesArrayList) {
        this.pcNamesArrayList = pcNamesArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pc_list,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(pcNamesArrayList.get(position).getName());

        Glide.with(context).load(pcNamesArrayList.get(position).getImage()).transform(new BitmapGlideImageCorners(30)).override(250,250).into(holder.image);

        String name = pcNamesArrayList.get(position).getName();



              if(name.equals("Televizyonlar")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Televizyonu gör");
              }else if(name.equals("Telefonlar")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Telefonu gör");
              }else if(name.equals("Bilgisayarlar")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Bilgisayar Bileşenlerini gör");
              }else if(name.equals("Oyun Konsolları")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Oyun Konsolunu gör");
              }else if(name.equals("Vasıtalar")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Vasıtayı gör");
              }else if(name.equals("Kitaplar")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " Kitabı gör");
              }else if(name.equals("Televisions")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Television");
              }else if(name.equals("Phones")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Mobile Phone");
              }else if(name.equals("Computers")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Computer Components");
              }else if(name.equals("Game Consoles")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Game Console");
              }else if(name.equals("Vehicles")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Vehicle");
              }else if(name.equals("Books")){
                  holder.sayi.setText(String.valueOf(pcNamesArrayList.get(position).getSayi()) + " See Book");
              }





        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View view) {
                   String names =  holder.name.getText().toString();


                   NavDirections navDirections = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavToProductsFragment(names);
                   Navigation.findNavController(view).navigate(navDirections);




               }
           });







    }








    @Override
    public int getItemCount() {
        return pcNamesArrayList.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name,sayi;

        LinearLayout linearLayout;


        public Holder(@NonNull View itemView) {
            super(itemView);


            linearLayout = itemView.findViewById(R.id.anasayfa_linerLayout);
            image = itemView.findViewById(R.id.parca_image);
            name = itemView.findViewById(R.id.name);
            sayi = itemView.findViewById(R.id.sayi);





        }



    }


}


