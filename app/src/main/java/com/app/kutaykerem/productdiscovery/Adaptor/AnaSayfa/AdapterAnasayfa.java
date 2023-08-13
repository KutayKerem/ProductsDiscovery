package com.app.kutaykerem.productdiscovery.Adaptor.AnaSayfa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.kutaykerem.productdiscovery.Models.AnasayfaDetails;
import com.app.kutaykerem.productdiscovery.Models.PcNames;
import com.app.kutaykerem.productdiscovery.R;

import java.util.List;

public class AdapterAnasayfa extends RecyclerView.Adapter<AdapterAnasayfa.AnasayfaHolder> {




    private Context context;
    private List<AnasayfaDetails> allCategoryList;

    String dil;

    public AdapterAnasayfa(Context context, List<AnasayfaDetails> allCategoryList,String dil) {
        this.context = context;
        this.allCategoryList = allCategoryList;
        this.dil = dil;

    }



    @NonNull
    @Override
    public AnasayfaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerrow_anasayfa,parent,false);
        return new AnasayfaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnasayfaHolder holder, int position) {


        holder.name.setText(allCategoryList.get(position).getName());

        if (allCategoryList.get(position).getName().equals("Genel Ürünler") || allCategoryList.get(position).getName().equals("General Products")) {
            setPartsItemRecycler(holder.categoryRescyclerView, allCategoryList.get(position).getCategoryItemList());
        } else if (allCategoryList.get(position).getName().equals("Öneriler") || allCategoryList.get(position).getName().equals("Suggestions")) {
            setOnerilerItemRecycler(holder.categoryRescyclerView, allCategoryList.get(position).getCategoryItemList());
        }

    }

    @Override
    public int getItemCount() {
        return allCategoryList.size();
    }

    public class AnasayfaHolder extends RecyclerView.ViewHolder{

        TextView name;
        RecyclerView categoryRescyclerView;
        public AnasayfaHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.recyclerrow_anasayfa_title);
            categoryRescyclerView = itemView.findViewById(R.id.recyclerrow_recyclerview);
        }
    }
    private void setPartsItemRecycler(RecyclerView recyclerView, List<PcNames> categoryItemList){

        AdaptorProductList itemRecyclerAdapter = new AdaptorProductList(context,categoryItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(itemRecyclerAdapter);


    }
    private void setOnerilerItemRecycler(RecyclerView recyclerView, List<PcNames> onerilenlerDetails){

        OnerilenlerAdapter itemRecyclerAdapter = new OnerilenlerAdapter(context,onerilenlerDetails,dil);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(itemRecyclerAdapter);


    }
}
