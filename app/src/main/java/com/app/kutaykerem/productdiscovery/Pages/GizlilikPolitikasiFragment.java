package com.app.kutaykerem.productdiscovery.Pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.app.kutaykerem.productdiscovery.R;
import com.app.kutaykerem.productdiscovery.databinding.FragmentGizlilikPolitikasiBinding;


public class GizlilikPolitikasiFragment extends Fragment {


    FragmentGizlilikPolitikasiBinding binding;


    String userId;
    String dil;






     String gizlilikPolitikasiAciklama = "";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGizlilikPolitikasiBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






try {


        if(getArguments() != null){
            String arananParcaAdi = GizlilikPolitikasiFragmentArgs.fromBundle(getArguments()).getArananParca();
            String dil = GizlilikPolitikasiFragmentArgs.fromBundle(getArguments()).getDil();


            getGizlilikPolitikasi(dil);



            if (dil.equals("türkce")){
                binding.gizlilikGizlilikText.setText("Gizlilik Politikası");




            }else{
                binding.gizlilikGizlilikText.setText("Privacy Policy");



            }


            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @SuppressLint("ResourceAsColor")
                @Override
                public void handleOnBackPressed() {

                    NavDirections action = GizlilikPolitikasiFragmentDirections.actionGizlilikPolitikasiFragmentToFeedbackFragment().setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);
                }
            });

            binding.gizlilikGeri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action =GizlilikPolitikasiFragmentDirections.actionGizlilikPolitikasiFragmentToFeedbackFragment().setArananParca(arananParcaAdi);
                    Navigation.findNavController(view).navigate(action);
                }
            });

        }else{

            getGizlilikPolitikasi("ingilizce");


            if (dil.equals("türkce")){
                binding.gizlilikGizlilikText.setText("Gizlilik Politikası");



            }else{
                binding.gizlilikGizlilikText.setText("Privacy Policy");


            }


            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @SuppressLint("ResourceAsColor")
                @Override
                public void handleOnBackPressed() {
                    NavDirections action = GizlilikPolitikasiFragmentDirections.actionGizlilikPolitikasiFragmentToFeedbackFragment();
                    Navigation.findNavController(view).navigate(action);
                }
            });

            binding.gizlilikGeri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavDirections action = GizlilikPolitikasiFragmentDirections.actionGizlilikPolitikasiFragmentToFeedbackFragment();
                    Navigation.findNavController(view).navigate(action);
                }
            });
        }
    }catch (Exception e){
        Log.d("Error :", e.getMessage());
    }



    }



    public void getGizlilikPolitikasi(String dil){
        if(dil.equals("türkce")){
            binding.gizlilikAciklamasiText.setText(R.string.gizlilik_politikasi_türkce);
        }else{
            binding.gizlilikAciklamasiText.setText(R.string.gizlilik_politikasi_ing);

        }
    }





}
