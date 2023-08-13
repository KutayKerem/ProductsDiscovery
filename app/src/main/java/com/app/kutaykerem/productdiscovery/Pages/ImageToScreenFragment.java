package com.app.kutaykerem.productdiscovery.Pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentImageToScreenBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;


public class ImageToScreenFragment extends Fragment {



    FragmentImageToScreenBinding binding;
    PhotoView photoView;
    String url,fromWhere,kullaniciİd;



    public ImageToScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentImageToScreenBinding.inflate(inflater,container,false);
        return  binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        photoView = view.findViewById(R.id.photoView);


        if(getArguments() != null){
            url = getArguments().getString("url").toString();



            Glide.with(this)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.imageToScreenProgresBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.imageToScreenProgresBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(photoView);




            fromWhere = getArguments().getString("from_where").toString();
            String arananParca = getArguments().getString("arananParca","null");

            if(fromWhere.equals("kesfet")){
                if(!arananParca.equals("null")){
                    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                           ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(1);
                            action.setArananParca(arananParca);
                            Navigation.findNavController(view).navigate(action);
                        }

                    });

                    binding.imageToScreenGeri.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(1);
                            action.setArananParca(arananParca);
                            Navigation.findNavController(view).navigate(action);
                        }
                    });
                }else{
                    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(1);
                            Navigation.findNavController(view).navigate(action);
                        }

                    });
                    binding.imageToScreenGeri.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(1);
                            Navigation.findNavController(view).navigate(action);
                        }
                    });

                }


            }else if(fromWhere.equals("sohbetler")){

               kullaniciİd = getArguments().getString("hedef_Id").toString();

                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        photoView.setVisibility(View.GONE);
                      ImageToScreenFragmentDirections.ActionImageToScreenFragmentToSohbetlerFragment action =ImageToScreenFragmentDirections.actionImageToScreenFragmentToSohbetlerFragment();
                        action.setKullaniciId(kullaniciİd);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
                binding.imageToScreenGeri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoView.setVisibility(View.GONE);
                        ImageToScreenFragmentDirections.ActionImageToScreenFragmentToSohbetlerFragment action =ImageToScreenFragmentDirections.actionImageToScreenFragmentToSohbetlerFragment();
                        action.setKullaniciId(kullaniciİd);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
            }else if(fromWhere.equals("profile_gonderiler")){
                kullaniciİd = getArguments().getString("hedef_Id").toString();

                requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        photoView.setVisibility(View.GONE);
                        ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(4);
                        action.setGonderen(kullaniciİd);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
                binding.imageToScreenGeri.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoView.setVisibility(View.GONE);
                       ImageToScreenFragmentDirections.ActionImageToScreenFragmentToHomeFragmentBottomNav action = ImageToScreenFragmentDirections.actionImageToScreenFragmentToHomeFragmentBottomNav().setPos(4);
                        action.setGonderen(kullaniciİd);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
            }


        }





    }
}