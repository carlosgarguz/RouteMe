package com.carlosgarguz.routeme.DB;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestoreAssistant {

    private FirebaseFirestore mFirestore;
    private DatabaseReference dbReference;
    private int result = 0;

    public FirestoreAssistant() {
        mFirestore = FirebaseFirestore.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();
    }

    public DocumentReference readLocationData(double latitude, double longitude){
        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));
        /*
        DocumentReference doc = mFirestore.collection("locs").document(String.valueOf(hash.charAt(0)))
                .collection(String.valueOf(hash.charAt(1))).document(String.valueOf(hash.charAt(2)))
                .collection(String.valueOf(hash.charAt(3))).document(String.valueOf(hash.charAt(4)))
                .collection(String.valueOf(hash.charAt(5))).document(String.valueOf(hash.charAt(6)));*/

        DocumentReference doc = mFirestore.collection("esp").document(hash);

        return doc;
    }

    public int createBlankDocument(double latitude, double longitude, String streetName){

        Map<String, Object> map = new HashMap<>();
        map.put("nombre calle", streetName);
        map.put("fecha", "Sin información");
        map.put("zonaCargaDescarga", "Sin información");
        map.put("aparcamiento", "Sin información");
        map.put("dobleFila", "Sin información");
        map.put("trafico", "Sin información");

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));

        mFirestore.collection("esp").document(hash).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("debug", "Se ha creado un documento en blanco en firestore");
                result = 1;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("debug", "No ha sido posible crear un documento en blanco en firestore");
                result = -1;
            }
        });

        return result;

    }

    public int createDocumentWithInfo(double latitude, double longitude, Map<String, Object> map){



        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));

        mFirestore.collection("esp").document(hash).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("debug", "Se ha creado un documento en blanco en firestore");
                result = 1;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("debug", "No ha sido posible crear un documento en blanco en firestore");
                result = -1;
            }
        });

        return result;

    }


}
