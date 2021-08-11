package com.carlosgarguz.routeme.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.carlosgarguz.routeme.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PopUpPlaceActivity extends FragmentActivity implements View.OnClickListener {


    //PopupWindow popUp;
    LinearLayout layoutAutoCompleteFragment;
    AutocompleteSupportFragment autoCompletePlaceFragment;
    //ImageView destinationImage;
    TextView destination;
    PlacesClient placesClient;
    TextView tvStopTime;
    ImageButton buttonIncreaseStopTime;
    ImageButton buttonDecreaseStopTime;
    Button buttonConfirmDestination;
    Button buttonResetDestination;
    LatLng destinationCoordinates;
    String language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_place);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        language = prefs.getString("language", "es");

        //popUp = new PopupWindow(this);
        layoutAutoCompleteFragment = findViewById(R.id.layout_place_autocomplete_fragment);
        autoCompletePlaceFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //destinationImage = (ImageView) findViewById(R.id.destination_image);
        destination = (TextView) findViewById(R.id.destination);
        tvStopTime = (TextView) findViewById(R.id.text_view_tiempo_parada);
        buttonIncreaseStopTime = (ImageButton) findViewById(R.id.increase_time_stop);
        buttonIncreaseStopTime.setOnClickListener(this);
        buttonDecreaseStopTime = (ImageButton) findViewById(R.id.decrease_time_stop);
        buttonDecreaseStopTime.setOnClickListener(this);
        buttonConfirmDestination = (Button) findViewById(R.id.button_confirm_place);
        buttonConfirmDestination.setOnClickListener(this);
        buttonResetDestination = (Button) findViewById(R.id.button_reset_place);
        buttonResetDestination.setOnClickListener(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * .85), (int) (height * .6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        //popUp.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        initializeAutocompleteFragment();

    }


    public void initializeAutocompleteFragment() {
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            if(language.equals("en")){
                Places.initialize(getApplicationContext(), apiKey, Locale.ENGLISH);
            }else{
                Places.initialize(getApplicationContext(), apiKey);
            }

        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this);


        autoCompletePlaceFragment.setTypeFilter(TypeFilter.ADDRESS);
        /*autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);*/
        autoCompletePlaceFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS));
        autoCompletePlaceFragment.setCountry("ES");

        autoCompletePlaceFragment.setHint("Introduce destino");
        autoCompletePlaceFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.

                autoCompletePlaceFragment.setText("");
                layoutAutoCompleteFragment.setVisibility(View.GONE);
                destination.setVisibility(View.VISIBLE);
                destination.setText("Destino: " + place.getAddress());
                destinationCoordinates = place.getLatLng();


            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.increase_time_stop:
                increaseStopTime();
                break;

            case R.id.decrease_time_stop:
                decreaseStopTime();
                break;

            case R.id.button_confirm_place:
                if(destinationCoordinates!=null) {
                    String destinationName = (String) destination.getText();
                    String stopTime = (String) tvStopTime.getText();
                    Intent i = new Intent();
                    i.putExtra("destination_name", destinationName.substring(9));
                    i.putExtra("stop_time", stopTime);
                    i.putExtra("destination_latitude", String.valueOf(destinationCoordinates.latitude));
                    i.putExtra("destination_longitude", String.valueOf(destinationCoordinates.longitude));
                    setResult(RESULT_OK, i);
                    finish();
                }else{
                    Toast.makeText(this, "Introduce un destino", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.button_reset_place:
                Log.i("prueba destino", "Has llegado");
                destination.setText("");
                destination.setVisibility(View.GONE);
                layoutAutoCompleteFragment.setVisibility(View.VISIBLE);
                autoCompletePlaceFragment.setText("");
                tvStopTime.setText("0");
                destinationCoordinates = null;

                break;

            default:
                break;
        }
    }

    private void decreaseStopTime() {
        String sStopTime = (String) tvStopTime.getText();
        int stopTime = Integer.parseInt(sStopTime);
        if(stopTime>10){
            stopTime = stopTime-5;
        }else if(stopTime>0){
            stopTime--;
        }else{
            Toast.makeText(this, "Tiempo no permitido", Toast.LENGTH_SHORT);
        }
        sStopTime = String.valueOf(stopTime);
        tvStopTime.setText(sStopTime);
    }

    private void increaseStopTime() {
        String sStopTime = (String) tvStopTime.getText();
        int stopTime = Integer.parseInt(sStopTime);
        if(stopTime>=10){
            stopTime = stopTime+5;
        }else{
            stopTime++;
        }
        sStopTime = String.valueOf(stopTime);
        tvStopTime.setText(sStopTime);
    }

    /*private int chargePhoto(Place place) {
        List<PhotoMetadata> metadata = place.getPhotoMetadatas();
        if (metadata == null || metadata.isEmpty()) {
            Log.i("debug", "No photo metadata of the selected place");
            return 0;
        }else{
            Log.i("debug", "there is a photo metadata of the selected place");
        }

        PhotoMetadata photoMetadata = metadata.get(0);


        // Create a FetchPhotoRequest.
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(destinationImage.getMaxWidth()) // Optional.
                .setMaxHeight(destinationImage.getMaxHeight()) // Optional.
                .build();



        placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            Log.i("debug", "número de bytes: " + bitmap.getByteCount());
            destinationImage.setImageBitmap(bitmap);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.i("debug", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });

        return 1;
    }*/
}
