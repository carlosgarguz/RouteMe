package com.carlosgarguz.routeme.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.carlosgarguz.routeme.DB.FirestoreAssistant;
import com.carlosgarguz.routeme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PopUpFirebaseInfoActivity extends FragmentActivity implements View.OnClickListener {

    private TextView tvStreetName;
    private TextView tvCargaDescarga;
    private TextView tvParking;
    private TextView tvDoblefila;
    private TextView tvTraffic;
    private TextView tvDate;
    //private TextView tvExtraInfo;
    private ImageButton buttonAddInfoFirebase;
    private DocumentSnapshot document;
    private FirestoreAssistant firestoreAssistant;

    String cargaDescarga = "";
    String parking = "";
    String dobleFila = "";
    String traffic = "";




    private double latitude;
    private double longitude;
    private String streetName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_info_firestore);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * .85), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;



        tvStreetName = findViewById(R.id.text_view_street_name_firebase_pop_up);
        tvCargaDescarga = findViewById(R.id.text_view_info_carga_y_descarga);
        tvParking = findViewById(R.id.text_view_info_parking);
        tvDoblefila = findViewById(R.id.text_view_doble_fila);
        tvTraffic = findViewById(R.id.text_view_traffic);
        //tvExtraInfo = findViewById(R.id.text_view_extra_info);
        buttonAddInfoFirebase = findViewById(R.id.button_add_info_firebase);
        buttonAddInfoFirebase.setOnClickListener(this);
        tvDate = findViewById(R.id.text_view_fecha);

        Intent i = getIntent();
        latitude = i.getDoubleExtra("latitude", 0.0);
        longitude = i.getDoubleExtra("longitude", 0.0);
        streetName = i.getStringExtra("street_name");
        fillLayout();





    }

    private void fillLayout() {

        Intent i = getIntent();

        firestoreAssistant = new FirestoreAssistant();
        DocumentReference doc = firestoreAssistant.readLocationData(latitude, longitude);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        Log.i("prueba firebase", "DocumentSnapshot data: " + document.getData().toString());
                        Map<String, Object> result = document.getData();

                        //Checkeamos fecha
                        String fecha = result.get("fecha").toString();
                        int indexT = fecha.indexOf("T");
                        //Log.i("prueba firebase", fecha);


                        tvDate.setText("Información reportada el " + fecha.substring(0, indexT) + " a las " + fecha.substring(indexT + 1));

                        //Checkeamos zonaCargaDescarga
                        String zonaCargaDescarga = result.get("zonaCargaDescarga").toString();
                        if(!zonaCargaDescarga.equals("Sin información")){
                            if(zonaCargaDescarga.equals("si")){
                                tvCargaDescarga.setText("Hay una zona de carga y descarga ");
                            }else{
                                tvCargaDescarga.setText("No hay zona de carga y descarga");
                            }
                        }

                        String parking = result.get("aparcamiento").toString();
                        if(!parking.equals("Sin información")){
                            if(parking.equals("si")){
                                tvParking.setText("Es probable que sea fácil aparcar en la zona");
                            }else{
                                tvParking.setText("Es probable que sea complicado aparcar en la zona");
                            }
                        }

                        String dobleFila = result.get("dobleFila").toString();
                        if(!dobleFila.equals("Sin información")){
                            if(dobleFila.equals("si")){
                                tvDoblefila.setText("Es probable que sea fácil parar en doble fila");
                            }else{
                                tvDoblefila.setText("Es probable que sea complicado parar en doble fila");
                            }
                        }

                        String trafico = result.get("trafico").toString();
                        if(!trafico.equals("Sin información")){
                            if(trafico.equals("si")){
                                tvTraffic.setText("Es probable que haya tráfico en la zona");
                            }else{
                                tvTraffic.setText("Es poco probable que haya tráfico en la zona");
                            }
                        }




                    } else {
                        Log.i("prueba firebase", "No such document");
                    }
                } else {
                    Log.i("prueba firebase", "get failed with ", task.getException());
                }
            }
        });


        tvStreetName.setText(streetName);






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_info_firebase:

                firestoreAssistant.createBlankDocument(latitude, longitude, streetName);
                launchDialog();

                break;
            default:
                break;
        }
    }

    private void launchDialog() {
        Dialog dialog = new Dialog(PopUpFirebaseInfoActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.pop_up_add_info_firestore, null);
        dialog.setContentView(dialogView);


        ImageButton positiveCargaDescarga = dialogView.findViewById(R.id.button_pos_carga_descarga);
        ImageButton negativeCargaDescarga = dialogView.findViewById(R.id.button_neg_carga_descarga);

        ImageButton positiveParking = dialogView.findViewById(R.id.button_pos_parking);
        ImageButton negativeParking = dialogView.findViewById(R.id.button_neg_parking);

        ImageButton positiveDobleFila = dialogView.findViewById(R.id.button_pos_doble_fila);
        ImageButton negativeDobleFila = dialogView.findViewById(R.id.button_neg_doble_fila);

        ImageButton positiveTraffic = dialogView.findViewById(R.id.button_pos_traffic);
        ImageButton negativeTraffic = dialogView.findViewById(R.id.button_neg_traffic);

        Button buttonSendIndo = dialogView.findViewById(R.id.button_send_info);

        positiveCargaDescarga.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                negativeCargaDescarga.setBackground(null);

                cargaDescarga = "si";
                positiveCargaDescarga.setBackgroundColor(R.color.grey_pressed);
            }
        });
        negativeCargaDescarga.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    positiveCargaDescarga.setBackground(null);
                cargaDescarga = "no";
                negativeCargaDescarga.setBackgroundColor(R.color.grey_pressed);
            }
        });
        positiveParking.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    negativeParking.setBackground(null);

                parking = "si";
                positiveParking.setBackgroundColor(R.color.grey_pressed);
            }
        });
        negativeParking.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    positiveParking.setBackground(null);

                parking = "no";
                negativeParking.setBackgroundColor(R.color.grey_pressed);
            }
        });
        positiveDobleFila.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    negativeDobleFila.setBackground(null);

                dobleFila = "si";
                positiveDobleFila.setBackgroundColor(R.color.grey_pressed);
            }
        });
        negativeDobleFila.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    positiveDobleFila.setBackground(null);

                dobleFila = "no";
                negativeDobleFila.setBackgroundColor(R.color.grey_pressed);
            }
        });
        positiveTraffic.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    negativeTraffic.setBackground(null);

                traffic = "si";
                positiveTraffic.setBackgroundColor(R.color.grey_pressed);
            }
        });
        negativeTraffic.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                    positiveTraffic.setBackground(null);

                traffic = "no";
                negativeTraffic.setBackgroundColor(R.color.grey_pressed);
            }
        });

        buttonSendIndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                if(!cargaDescarga.equals("")){
                    map.put("zonaCargaDescarga", cargaDescarga);
                }
                if(!parking.equals("")){
                    map.put("aparcamiento", parking);
                }
                if(!dobleFila.equals("")){
                    map.put("dobleFila", dobleFila);
                }
                if(!traffic.equals("")){
                    map.put("trafico", traffic);
                }

                Date currentTime = Calendar.getInstance().getTime();


                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy'T'HH:mm:ss");
                String formattedDate = df.format(currentTime);
                map.put("fecha", formattedDate);

                firestoreAssistant.createDocumentWithInfo(latitude, longitude, map);
                final Runnable r = new Runnable() {
                    public void run() {
                        finish();
                    }
                };
                final Handler handler = new Handler();
                handler.postDelayed(r, 150);


            }
        });





        final Runnable r = new Runnable() {
            public void run() {
                dialog.show();
            }
        };

        final Handler handler = new Handler();
        handler.postDelayed(r, 150);


    }
}
