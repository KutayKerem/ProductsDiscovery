package com.app.kutaykerem.productdiscovery.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.kutaykerem.productdiscovery.Models.GetDateStart;
import com.app.kutaykerem.productdiscovery.Models.KullanıcıBilgileri;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class RefreshDatabase  extends Worker {

    public  HashMap<String, Object> data;
    DatabaseReference databaseReference,onlineKontrolReference;

    Context context;


    public RefreshDatabase(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;


    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        Boolean durum = data.getBoolean("durum",false);
        String userId = data.getString("userId");

        refreshDatabase(durum,userId);

        return Result.success();
    }

    private void refreshDatabase(Boolean durum,String userId){

        try {


            onlineKontrolReference = FirebaseDatabase.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Profile");

            DatabaseReference reference = databaseReference.child(userId);
            if(reference != null){

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null) {

                            KullanıcıBilgileri kullanıcıBilgileri = snapshot.getValue(KullanıcıBilgileri.class);
                            String kulllaniciAdi = kullanıcıBilgileri.getKullanıcıAdı();
                            data = new HashMap<>();
                            data.put("durum", durum);
                            data.put("kullanıcıAdı",kulllaniciAdi);
                            if(durum == false){
                                Date tarih = new Date();
                                data.put("songörülme", GetDateStart.calculateElapsedTime(tarih));
                            }


                            onlineKontrolReference.child("Durum").child(userId).child("State").setValue(data);
                            onlineKontrolReference.child("Durum").child(userId).child("State").updateChildren(data);



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }catch (Exception e){
            Log.d("Error:",e.getMessage());
        }


    }


}
