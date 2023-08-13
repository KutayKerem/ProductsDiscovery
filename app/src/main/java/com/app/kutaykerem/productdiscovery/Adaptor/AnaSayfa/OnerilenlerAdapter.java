package com.app.kutaykerem.productdiscovery.Adaptor.AnaSayfa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kutaykerem.productdiscovery.Models.BitmapGlideImageCorners;
import com.app.kutaykerem.productdiscovery.Models.PcNames;
import com.bumptech.glide.Glide;
import com.app.kutaykerem.productdiscovery.Pages.HomeFragmentBottomNavDirections;
import com.app.kutaykerem.productdiscovery.R;

import java.util.List;



public class OnerilenlerAdapter extends RecyclerView.Adapter<OnerilenlerAdapter.OnerilenlerHolder> {



    Context context;
    List<PcNames> pcNamesList;


    String dil;



    public OnerilenlerAdapter(Context context, List<PcNames> pcNamesList,String dil) {
        this.context = context;
        this.pcNamesList = pcNamesList;
        this.dil = dil;

     }

    @NonNull
    @Override
    public OnerilenlerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerrow_onerilenler,parent,false);
        return new OnerilenlerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnerilenlerHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(pcNamesList.get(position).getName());

        try {


        Glide.with(context).load(pcNamesList.get(position).getImage()).transform(new BitmapGlideImageCorners(30)).override(250,250).into(holder.image);

        }catch (Exception e){

        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = pcNamesList.get(position).getName();
                if(name.equals("En çok yorum yapılan gönderiler") || name.equals("Most commented posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("most_commented_posts");
                    Navigation.findNavController(v).navigate(action);
                }else if(name.equals("Yorum yapılmamış gönderiler") || name.equals("Posts with no comments")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_no_comments");
                    Navigation.findNavController(v).navigate(action);


                }else if(name.equals("Most liked posts") || name.equals("En çok beğenilen gönderiler")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("most_liked_posts");
                    Navigation.findNavController(v).navigate(action);

                } else if(name.equals("En yüksek puana sahip gönderiler") || name.equals("Posts with the highest score")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_the_highest_score");
                    Navigation.findNavController(v).navigate(action);

                }else if(name.equals("En düşük puana sahip gönderiler") || name.equals("Posts with the lowest score")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_the_lowest_score");
                    Navigation.findNavController(v).navigate(action);

                }else if(name.equals("En yeni gönderiler") || name.equals("Newest posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("newest_posts");
                    Navigation.findNavController(v).navigate(action);


                }else if(name.equals("En eski gönderiler") || name.equals("Oldest posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("oldest_posts");
                    Navigation.findNavController(v).navigate(action);
                }else if(name.equals("Benim gönderdiğim gönderiler") || name.equals("My submitted posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("my_submitted_posts");
                    Navigation.findNavController(v).navigate(action);
                }


            }
        });

        holder.browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = pcNamesList.get(position).getName();
                if(name.equals("En çok yorum yapılan gönderiler") || name.equals("Most commented posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("most_commented_posts");
                    Navigation.findNavController(v).navigate(action);
                }else if(name.equals("Yorum yapılmamış gönderiler") || name.equals("Posts with no comments")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_no_comments");
                    Navigation.findNavController(v).navigate(action);


                }else if(name.equals("Most liked posts") || name.equals("En çok beğenilen gönderiler")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("most_liked_posts");
                    Navigation.findNavController(v).navigate(action);

                } else if(name.equals("En yüksek puana sahip gönderiler") || name.equals("Posts with the highest score")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_the_highest_score");
                    Navigation.findNavController(v).navigate(action);

                }else if(name.equals("En düşük puana sahip gönderiler") || name.equals("Posts with the lowest score")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("posts_with_the_lowest_score");
                    Navigation.findNavController(v).navigate(action);

                }else if(name.equals("En yeni gönderiler") || name.equals("Newest posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("newest_posts");
                    Navigation.findNavController(v).navigate(action);


                }else if(name.equals("En eski gönderiler") || name.equals("Oldest posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("oldest_posts");
                    Navigation.findNavController(v).navigate(action);
                }else if(name.equals("Benim gönderdiğim gönderiler") || name.equals("My submitted posts")){
                    NavDirections action = HomeFragmentBottomNavDirections.actionHomeFragmentBottomNavSelf().setArananParca("my_submitted_posts");
                    Navigation.findNavController(v).navigate(action);
                }
            }
        });



    }



    @Override
    public int getItemCount() {
        return pcNamesList.size();
    }




    public class OnerilenlerHolder extends RecyclerView.ViewHolder{

       TextView name;
       ImageView image;
       ConstraintLayout constraintLayout;
       Button browseButton;


        public OnerilenlerHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.onerilenler_textview);
            image = itemView.findViewById(R.id.onerilenler_image);
            constraintLayout = itemView.findViewById(R.id.onerilenler_constraintLayout);
            browseButton = itemView.findViewById(R.id.recyclerrow_oneriler_button);
            browseButton.setText(dil);



        }



    }




}
