package com.carlosgarguz.routeme.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.paths.ParserJsonPath;
import com.carlosgarguz.routeme.paths.Path;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback{

    //global variables
    final int PLAN_ROUTE_REQUEST = 100;
    final String GOOGLE_KEY = "AIzaSyC-bfmAZTXvjIFjpaDHDpICvPA3lXCP_QU";
    public static GoogleMap map;
    public static PolylineOptions polylineOptions = new PolylineOptions();
    public static Polyline polyline = null;
    public static TextView tvRouteTime = null;


    String bestProvider;
    static final int ACCESS_FINE_LOCATION_CODE = 100;
    private LatLng startingPoint;
    private LatLng endPoint;
    private String language;
    private String use;
    private boolean atDestination = false;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLatitude;
    double currentLongitude;
    String currentLocationName;
    Location loc;


    //global views
    private Button buttonProfile;
    private FloatingActionButton buttonStartTrip;
    private FloatingActionButton buttonCancelTrip;
    private FloatingActionButton buttonConfiguration;
    private Button buttonFinishStage;
    private PlacesClient placesClient;
    private Marker markerStartingPoint;
    private Marker markerEndingPoint;
    private BitmapDescriptor bitmapDescriptorEndFlag;
    //global views para manejar el buscador
    LinearLayout layoutAutocompleteStartingPoint;
    LinearLayout layoutAutocompleteEndingPoint;
    AutocompleteSupportFragment autocompleteStartingPointFragment;
    AutocompleteSupportFragment autocompleteEndingPointFragment;
    private LinearLayout layoutStartingPoint;
    private LinearLayout layoutEndingPoint;
    private TextView tvStartingPoint;
    private TextView tvEndingPoint;
    private TextView tvNumberStage;
    private ImageButton buttonDeleteStartingPoint;
    private ImageButton buttonDeleteEndingPoint;
    private View dividerSartingEndPoints;
    private View dividerEndingPointMap;






    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);






        use = getIntent().getStringExtra("use");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        language = prefs.getString("language", "es");



        SurfaceView mainSurface = new SurfaceView(this);
        mainSurface.setZOrderMediaOverlay(true);

        /*Button planRouteButton = (Button) findViewById(R.id.boton_crear_ruta);
        planRouteButton.setOnClickListener(this);
        buttonProfile = findViewById(R.id.boton_perfil);
        buttonProfile.setOnClickListener(this);*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        layoutAutocompleteStartingPoint = findViewById(R.id.layout_starting_point_autocomplete_fragment);
        layoutAutocompleteEndingPoint = findViewById(R.id.layout_end_point_autocomplete_fragment);
        autocompleteStartingPointFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.starting_point_autocomplete_fragment);
        autocompleteEndingPointFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.end_point_autocomplete_fragment);
        layoutStartingPoint = findViewById(R.id.layout_starting_point_text_view);
        layoutEndingPoint = findViewById(R.id.layout_ending_point_text_view);
        tvStartingPoint = findViewById(R.id.starting_point_text_view);
        tvEndingPoint = findViewById(R.id.ending_point_text_view);
        buttonDeleteStartingPoint = findViewById(R.id.delete_starting_point_button);
        buttonDeleteEndingPoint = findViewById(R.id.delete_ending_point_button);
        buttonConfiguration = findViewById(R.id.configuration_button);
        dividerSartingEndPoints = findViewById(R.id.divider_starting_end_point);
        dividerEndingPointMap = findViewById(R.id.divider_ending_point_map);

        bitmapDescriptorEndFlag = bitmapDescriptorFromVector(this, R.drawable.ic_finish_flag);

        tvRouteTime = (TextView) findViewById(R.id.texto_tiempo_ruta);
        tvNumberStage = findViewById(R.id.text_view_number_stage);
        buttonStartTrip = (FloatingActionButton) findViewById(R.id.start_trip_button);
        buttonCancelTrip = (FloatingActionButton) findViewById(R.id.cancel_trip_button);
        buttonStartTrip.setOnClickListener(this);
        buttonCancelTrip.setOnClickListener(this);
        buttonFinishStage = findViewById(R.id.finish_stage_button);

        getCurrentLocation();

        if(getIntent().getStringExtra("use").equals("0")){
            buttonDeleteStartingPoint.setOnClickListener(this);
            buttonDeleteStartingPoint.setVisibility(View.VISIBLE);
            buttonDeleteEndingPoint.setOnClickListener(this);
            buttonDeleteEndingPoint.setVisibility(View.VISIBLE);
            buttonConfiguration.setOnClickListener(this);
            buttonConfiguration.setVisibility(View.VISIBLE);
            tvNumberStage.setVisibility(View.GONE);
            buttonFinishStage.setVisibility(View.GONE);



            initializeAutocompleteFragment();

            CountDownTimer timer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    centerMapView(currentLatitude, currentLongitude);
                }
            };

            timer.start();

        }else{
            buttonDeleteStartingPoint.setVisibility(View.GONE);
            buttonDeleteEndingPoint.setVisibility(View.GONE);
            buttonConfiguration.setVisibility(View.GONE);
            tvNumberStage.setVisibility(View.VISIBLE);
            buttonFinishStage.setVisibility(View.VISIBLE);
            buttonFinishStage.setOnClickListener(this);

            initializeUseOne();
        }







    }



    public void initializeAutocompleteFragment(){
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


        autocompleteStartingPointFragment.setTypeFilter(TypeFilter.ADDRESS);
        /*autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);*/
        autocompleteStartingPointFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteStartingPointFragment.setCountry("ES");
        autocompleteStartingPointFragment.setHint("Introduce el punto de partida");


        autocompleteStartingPointFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                map.clear();
                Toast.makeText(getApplicationContext(), place.getName(), Toast.LENGTH_SHORT).show();
                startingPoint = place.getLatLng();


                autocompleteStartingPointFragment.setText("");
                layoutAutocompleteStartingPoint.setVisibility(View.GONE);
                layoutStartingPoint.setVisibility(View.VISIBLE);
                dividerSartingEndPoints.setVisibility(View.VISIBLE);
                tvStartingPoint.setText("Salida: " + place.getName());


                if(endPoint == null){
                    initializeDestinationAutocompleteFragment();
                    centerMapView(place.getLatLng());
                }else{
                    if(startingPoint!=null) {
                        clearPolyline();
                        drawPolyline();
                        buttonConfiguration.setVisibility(View.GONE);
                        buttonStartTrip.setVisibility(View.VISIBLE);
                        buttonCancelTrip.setVisibility(View.VISIBLE);
                    }
                }




            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeDestinationAutocompleteFragment() {
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }


        // Create a new Places client instance.
        placesClient = Places.createClient(this);


        autocompleteEndingPointFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteEndingPointFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteEndingPointFragment.setCountry("ES");
        autocompleteEndingPointFragment.setHint("Introduce el destino");

        autocompleteEndingPointFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                map.clear();
                endPoint = place.getLatLng();

                autocompleteEndingPointFragment.setText("");
                layoutAutocompleteEndingPoint.setVisibility(View.GONE);
                layoutEndingPoint.setVisibility(View.VISIBLE);
                dividerEndingPointMap.setVisibility(View.VISIBLE);
                tvEndingPoint.setText("Destino: " + place.getName());
                markerStartingPoint = map.addMarker(new MarkerOptions().position(startingPoint).title("Salida"));
                markerEndingPoint = map.addMarker(new MarkerOptions().position(endPoint).title("Destino").icon(bitmapDescriptorEndFlag));
                //markerEndingPoint.setVisible(true);
                if(endPoint!=null) {
                    if (startingPoint != null) {
                        clearPolyline();
                        drawPolyline();
                        buttonConfiguration.setVisibility(View.GONE);
                        buttonStartTrip.setVisibility(View.VISIBLE);
                        buttonCancelTrip.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        layoutAutocompleteEndingPoint.setVisibility(View.VISIBLE);
    }



    private void initializeUseOne() {



        startingPoint = new LatLng(getIntent().getDoubleExtra("starting_point_latitude", 0.0), getIntent().getDoubleExtra("starting_point_longitude", 0.0));
        endPoint = new LatLng(getIntent().getDoubleExtra("ending_point_latitude", 0.0), getIntent().getDoubleExtra("ending_point_longitude", 0.0));

        layoutAutocompleteStartingPoint.setVisibility(View.GONE);
        layoutStartingPoint.setVisibility(View.VISIBLE);
        dividerSartingEndPoints.setVisibility(View.VISIBLE);
        tvStartingPoint.setText("Salida: " + getIntent().getStringExtra("starting_point_name"));

        layoutAutocompleteEndingPoint.setVisibility(View.GONE);
        layoutEndingPoint.setVisibility(View.VISIBLE);
        dividerEndingPointMap.setVisibility(View.VISIBLE);
        tvEndingPoint.setText("Destino: " + getIntent().getStringExtra("ending_point_name"));
        tvNumberStage.setText("ETAPA\n" + String.valueOf(getIntent().getIntExtra("stage", 0)));

        /*markerStartingPoint = map.addMarker(new MarkerOptions().position(startingPoint).title("Salida"));
        markerEndingPoint = map.addMarker(new MarkerOptions().position(endPoint).title("Destino").icon(bitmapDescriptorEndFlag));
        drawPolyline();*/

        buttonStartTrip.setVisibility(View.VISIBLE);
        buttonCancelTrip.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.cancel_trip_button:

                if(use.equals("0")) {
                    map.clear();
                    clearPolyline();
                    buttonCancelTrip.setVisibility(View.GONE);
                    buttonStartTrip.setVisibility(View.GONE);
                    buttonConfiguration.setVisibility(View.VISIBLE);

                    //Eliminamos la vista del destino y el tiempo de ruta
                    layoutEndingPoint.setVisibility(View.GONE);
                    tvRouteTime.setVisibility(View.GONE);

                    //ponemos en pantalla el buscador de destino
                    layoutAutocompleteEndingPoint.setVisibility(View.VISIBLE);
                    autocompleteEndingPointFragment.setText("");
                    endPoint = null;
                    centerMapView(startingPoint);
                }else{
                    Intent i = new Intent();
                    setResult(RESULT_CANCELED, i);
                    finish();
                }


                break;

            case R.id.delete_starting_point_button:

                startingPoint = null;
                if(endPoint == null){
                    if(layoutAutocompleteEndingPoint.getVisibility() == View.VISIBLE)
                        layoutAutocompleteEndingPoint.setVisibility(View.GONE);
                    centerMapView(currentLatitude, currentLongitude);
                }else{
                    clearPolyline();
                    buttonCancelTrip.setVisibility(View.GONE);
                    buttonStartTrip.setVisibility(View.GONE);
                    buttonConfiguration.setVisibility(View.VISIBLE);
                    tvRouteTime.setVisibility(View.GONE);
                    centerMapView(endPoint.latitude, endPoint.longitude);
                }

                tvStartingPoint.setText("");
                layoutStartingPoint.setVisibility(View.GONE);
                layoutAutocompleteStartingPoint.setVisibility(View.VISIBLE);
                autocompleteStartingPointFragment.setText("");
                break;

            case R.id.delete_ending_point_button:

                endPoint = null;
                tvEndingPoint.setText("");
                layoutEndingPoint.setVisibility(View.GONE);

                if(startingPoint!=null) {
                    layoutAutocompleteEndingPoint.setVisibility(View.VISIBLE);
                    autocompleteEndingPointFragment.setText("");
                    clearPolyline();
                    buttonCancelTrip.setVisibility(View.GONE);
                    buttonStartTrip.setVisibility(View.GONE);
                    buttonConfiguration.setVisibility(View.VISIBLE);
                    tvRouteTime.setVisibility(View.GONE);
                    centerMapView(startingPoint);
                }else {
                    dividerEndingPointMap.setVisibility(View.GONE);
                    centerMapView(new LatLng(currentLatitude, currentLongitude));
                }

                break;

            case R.id.start_trip_button:

                checkProximity(startingPoint.latitude, startingPoint.longitude);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(atDestination){

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + endPoint.latitude + "," + endPoint.longitude + "&mode=d"));
                            intent.setPackage("com.google.android.apps.maps");

                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Descarga google maps");
                                builder.setMessage("Necesitas tener descargado google maps para entrar al modo navegación");
                                builder.setPositiveButton(android.R.string.ok, null);
                                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                    }
                                });
                                builder.show();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Estás lejos del punto de partida");
                            builder.setMessage("Para entrar al modo navegación tienes que estar cerca del punto de partida seleccionado");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }
                            });
                            builder.show();
                        }
                    }
                }, 500);

                break;

            case R.id.configuration_button:
                Intent i = new Intent(this, ConfigurationActivity.class);
                startActivity(i);

                break;

            case R.id.finish_stage_button:
                checkProximity(endPoint.latitude, endPoint.longitude);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(atDestination){
                            int stopTime = getIntent().getIntExtra("stop_time", 0);
                            if(stopTime>0) {
                                AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();
                                final View dialogView = getLayoutInflater().inflate(R.layout.dialog_accept_refuse, null);
                                builder.setView(dialogView);
                                TextView summary = dialogView.findViewById(R.id.description_dialog_accept_refuse);
                                summary.setText("Hay programada una para de " + String.valueOf(stopTime) + " minutos para el final de la etapa. ¿Quieres lanzar un temporizador?");
                                Button accept = (Button) dialogView.findViewById(R.id.button_accept_dialog);
                                accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(AlarmClock.ACTION_SET_TIMER);
                                        i.putExtra(AlarmClock.EXTRA_LENGTH, stopTime*60);
                                        i.putExtra(AlarmClock.EXTRA_MESSAGE, "Tiempo de parada de la etapa");
                                        i.putExtra(AlarmClock.EXTRA_SKIP_UI, false);
                                        startActivity(i);
                                        builder.dismiss();
                                        Intent i2 = new Intent();
                                        setResult(RESULT_OK, i2);
                                        finish();
                                    }
                                });
                                Button reject = (Button) dialogView.findViewById(R.id.button_refuse_dialog);
                                reject.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent();
                                        setResult(RESULT_OK, i);
                                        finish();
                                    }
                                });

                                builder.show();
                            }else{
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("No estás en el destino");
                            builder.setMessage("Para terminar etapa has de estar en el destino. Puedes marcarla como finalizada de forma manual en la pantalla de ruta.");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                }
                            });
                            builder.show();
                        }
                    }
                }, 500);

            default:
                return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Una vez obtenido el permiso, obtenemos el mejor proveedor de localización
            if(use.equals("0"))
                map.setMyLocationEnabled(true);
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            getBestProvider(locManager);
            //getCurrentLocation();
            /*Log.i("prueba localicazion", "La localización inicial es: " + currentLatitude + ", " + currentLongitude);
            centerMapView(currentLatitude, currentLongitude);*/
            //Ya tenemos el mejor proveedor, obtenemos la localización actual.
        }else{
            showLocationPermissionDialog();
        }



        // Vamos a iniciar la app en nuestra localización en caso de que la acepten.
        //Damos unos valores iniciales por si no nos aceptan los permisos de localización



        // Solicitamos permisos de localización. Esta parte podrás externalizarla a una clase en un futuro.


        if(use.equals("1")) {
            clearPolyline();
            markerStartingPoint = map.addMarker(new MarkerOptions().position(startingPoint).title("Salida"));
            markerEndingPoint = map.addMarker(new MarkerOptions().position(endPoint).title("Destino").icon(bitmapDescriptorEndFlag));
            drawPolyline();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Servicio de localización activado.", Toast.LENGTH_LONG);
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Una vez obtenido el permiso, obtenemos el mejor proveedor de localización
                        map.setMyLocationEnabled(true);
                        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        getBestProvider(locManager);
                        Location initLoc = locManager.getLastKnownLocation(bestProvider);
                        Log.i("Checkeo", "Lat: " + String.valueOf(initLoc.getLatitude()) + "Long: " + String.valueOf(initLoc.getLongitude()));
                        centerMapView(initLoc.getLatitude(), initLoc.getLongitude());
                        //Ya tenemos el mejor proveedor, obtenemos la localización actual.
                    }
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Funcionalidad limitada");
                    builder.setMessage("Debido a que no han sido concedidos los permisos de localización, no se podrá situar tu posición.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                    centerMapView(40.332632965813126, -3.765152917323647 );
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public String getBestProvider(LocationManager locManager) {

        Criteria criterios = new Criteria();
        criterios.setAccuracy(Criteria.ACCURACY_FINE);
        criterios.setPowerRequirement(Criteria.POWER_MEDIUM);
        criterios.setAltitudeRequired(false);
        criterios.setBearingRequired(false);
        criterios.setSpeedRequired(true);
        criterios.setCostAllowed(true);

        bestProvider = locManager.getBestProvider(criterios, true);
        Log.i("Checkeo", "Best provider is " + bestProvider);
        return bestProvider;
    }





    public void showLocationPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Esta aplicación necesita acceso a localización");
        builder.setMessage("Por favor, concede permisos de localicación para poder situar tu posición");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_CODE);
            }
        });
        builder.show();
    }



    public void centerMapView(double latitud, double longitud) {
        centerMapView(new LatLng(latitud, longitud));
        //Ponemos un marcador
    }

    public void centerMapView(LatLng coordinates){


        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(coordinates, 18);
        map.animateCamera(cu, 1500, null);
        markerStartingPoint = map.addMarker(new MarkerOptions().position(coordinates).title("Salida"));
    }



    public void drawPolyline(){
        String url = Path.getApiUrl(startingPoint, endPoint, "driving", GOOGLE_KEY);
        Log.i("url api", url);
        ParserJsonPath parserJsonPath = new ParserJsonPath();
        parserJsonPath.execute(url);
    }

    public boolean checkGeocoder(){
        if(Geocoder.isPresent()){
            return true;
        }else{
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Error");
            ad.setMessage("Tu dispositivo no tiene geocodificardor o no estás conectado a internet");
            ad.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            ad.show();
            return false;
        }
    }

    private void clearPolyline(){
        if(polyline!=null) {
            Log.i("debug polyline", "Limpiando polyline");
            polylineOptions.getPoints().clear();
            if(polyline.isVisible()) {
                polyline.remove();
            }
            /*if(markerStartingPoint.isVisible()) {
                markerStartingPoint.remove();
            }
            if(markerEndingPoint.isVisible()) {
                markerEndingPoint.remove();
            }*/
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getCurrentLocation() {


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    loc = task.getResult();

                    if (loc != null) {
                        // Log.i("prueba ruta compleja", "Loc no es null");
                        Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());

                        try {

                            // Log.i("prueba ruta compleja", "loc.getlatitude() is: " + loc.getLatitude() + ", loc.getlongitude() is: " + loc.getLongitude());
                            List<Address> addresses = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                            //Log.i("prueba ruta compleja", "addres latitude is: " + addresses.get(0).getLatitude() + ", address lonfitude is: " + addresses.get(0).getLongitude());
                            currentLatitude = addresses.get(0).getLatitude();
                            currentLongitude = addresses.get(0).getLongitude();
                            currentLocationName = addresses.get(0).getAddressLine(0);
                            Log.i("prueba localizacion", "loc es: " + currentLatitude + ", " + currentLongitude);

                        } catch (IOException e) {
                            // Log.i("prueba ruta compleja", "excepcion al hacer getfromlocation");
                            e.printStackTrace();
                        }
                    }else{
                        Log.i("prueba localizacion", "No se ha obtenido localización");
                        //Log.i("prueba ruta compleja", "No has obtenido la localización");
                    }
                }
            });
        }
    }

    private boolean checkProximity(double latitude, double longitude){


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            atDestination = false;
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    loc = task.getResult();

                    if (loc != null) {
                        // Log.i("prueba ruta compleja", "Loc no es null");
                        Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());

                        try {

                            // Log.i("prueba ruta compleja", "loc.getlatitude() is: " + loc.getLatitude() + ", loc.getlongitude() is: " + loc.getLongitude());
                            List<Address> addresses = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                            //Log.i("prueba ruta compleja", "addres latitude is: " + addresses.get(0).getLatitude() + ", address lonfitude is: " + addresses.get(0).getLongitude());
                            currentLatitude = addresses.get(0).getLatitude();
                            currentLongitude = addresses.get(0).getLongitude();
                            currentLocationName = addresses.get(0).getAddressLine(0);

                            Log.i("prueba localizacion", "loc es: " + currentLatitude + ", " + currentLongitude);
                            Location loc2 = new Location(bestProvider);
                            loc2.setLatitude(latitude);
                            loc2.setLongitude(longitude);

                            if(loc.distanceTo(loc2)<70.0) {
                                Log.i("prueba localizacion", "La distancia es "+ loc.distanceTo(loc2));
                                atDestination = true;
                            }


                        } catch (IOException e) {
                            // Log.i("prueba ruta compleja", "excepcion al hacer getfromlocation");
                            e.printStackTrace();
                        }
                    }else{
                        Log.i("prueba localizacion", "No se ha obtenido localización");
                        //Log.i("prueba ruta compleja", "No has obtenido la localización");
                    }
                }
            });
        }
        return atDestination;

    }
}