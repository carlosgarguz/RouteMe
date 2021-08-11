package com.carlosgarguz.routeme.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.ICUUncheckedIOException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosgarguz.routeme.DB.DbAssistant;
import com.carlosgarguz.routeme.DB.DbDestinations;
import com.carlosgarguz.routeme.DB.DbRoutes;
import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.paths.ParserJsonRuta;
import com.carlosgarguz.routeme.paths.Path;
import com.carlosgarguz.routeme.paths.Route;
import com.carlosgarguz.routeme.paths.RouteTime;
import com.carlosgarguz.routeme.paths.TravelSalesmanProblem;
import com.carlosgarguz.routeme.utils.DestinationCard;
import com.carlosgarguz.routeme.utils.DestinationsAdapter;
import com.carlosgarguz.routeme.utils.SwipeDestinationCardCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PlanRouteActivity extends AppCompatActivity implements View.OnClickListener{

    final int FIRST_STAGE_CODE = 100;
    final String GOOGLE_KEY = "AIzaSyC-bfmAZTXvjIFjpaDHDpICvPA3lXCP_QU";
    /*ArrayList<DestinationCard> destinations = new ArrayList<>();
    DestinationsAdapter destinationsAdapter;
    RecyclerView rvDestinations;*/


    FragmentTransaction transaction;
    Fragment fragmentComputerRoute, fragmentDisplayRoute;
    int activeFragment = 0;


    List<DestinationCard> listDestinations = new ArrayList<>();
    RouteTime[][] matrix;
    Route route;
    Button buttonComputeRoute;

    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLatitude;
    double currentLongitude;
    String currentLocationName;

    public static View layout;

    int currentStage = 1;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("pruebas destinos", "on create called");


        setContentView(R.layout.activity_plan_route);
        Log.i("pruebas destinos", "on create called");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.titulo_actividad_planear_ruta);
        }





        //Instanciamos los fragments
        fragmentComputerRoute = new ComputeRouteFragment();
        fragmentDisplayRoute = new DisplayRouteFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_plan_route, fragmentComputerRoute).commit();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        layout = findViewById(R.id.layout_plan_route_activity);


        //Cogemos los datos recividos del main activity
        //Bundle b = this.getIntent().getExtras();


        //Incluimos el punto de partida
        //EditText etStartingPoint = (EditText) findViewById(R.id.punto_de_partida_ruta_compleja);
        //etStartingPoint.setText(b.getString("puntoDePartida"));

        buttonComputeRoute = findViewById(R.id.boton_calcular_ruta);
        buttonComputeRoute.setOnClickListener(this);

        /*ImageButton buttonAddDestination = (ImageButton) findViewById(R.id.boton_añadir_destino);
        buttonAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDestination();
            }
        });*/

        /*ImageButton buttonRemoveDestination = (ImageButton) findViewById(R.id.boton_quitar_destino);
        buttonRemoveDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDestination();
            }
        });*/

        getCurrentLocation();




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_plan_route_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, ConfigurationActivity.class);
                startActivity(i);
                return true;

            case R.id.action_fav_routes:
                String[] options = {"Ver rutas favoritas", "Añadir ruta a favoritas"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.pregunta_qué_hacer);
                builder.setItems(R.array.options_fav_routes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int option) {
                        switch (option){
                            case 0:
                                Intent i = new Intent(PlanRouteActivity.this, PopUpDisplayFavRoutesActivity.class);
                                startActivity(i);
                                break;

                            case 1:
                                if(listDestinations.size()>1){
                                    addToFavRoutes();
                                }else{
                                    Toast.makeText(PlanRouteActivity.this, "Las rutas deben tener más de un destino", Toast.LENGTH_LONG).show();
                                }

                                break;

                            default:
                                break;
                        }
                    }
                });
                builder.show();
                /*DbAssistant dbAssistant = new DbAssistant(PlanRouteActivity.this);
                SQLiteDatabase db = dbAssistant.getWritableDatabase();
                if(db != null){
                    Toast.makeText(PlanRouteActivity.this, "Se creó", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PlanRouteActivity.this, "No se creó", Toast.LENGTH_LONG).show();
                }*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FIRST_STAGE_CODE:
                if(resultCode==RESULT_OK) {
                    DisplayRouteFragment fragment = (DisplayRouteFragment) getSupportFragmentManager().findFragmentByTag("DISPLAY_ROUTE");
                    fragment.shadowRoute(currentStage - 1);
                    currentStage++;
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.boton_calcular_ruta:

                if(activeFragment == 0) {
                    transaction = getSupportFragmentManager().beginTransaction();
                    //Log.i("Prueba ruta compleja", "Has pinchado en empezar ruta");

                    if (listDestinations.size() > 1) {

                        ProgressDialog dialog = new ProgressDialog(PlanRouteActivity.this);
                        dialog.setMessage("Calculando ruta...");
                        //computeBestRoute();
                        dialog.setCancelable(true);
                        dialog.show();

                        computeBestRoute();
                        currentStage = 1;
                        dialog.dismiss();


                        transaction.replace(R.id.fragment_container_plan_route, fragmentDisplayRoute, "DISPLAY_ROUTE").commit();
                        activeFragment = 1;
                        transaction.addToBackStack(null);

                        buttonComputeRoute.setText(R.string.empezar_ruta);


                    } else {
                        Toast.makeText(this, "Debes seleccionar más de un destino", Toast.LENGTH_LONG).show();
                    }

                }else{
                    sendNextStage();
                }




                break;

            default:
                break;

        }
    }

    private void sendNextStage() {

        if(currentStage == route.getOrder().size()){
            AlertDialog.Builder builder = new AlertDialog.Builder(PlanRouteActivity.this);
            builder.setTitle("¡ENHORABUENA!");
            builder.setMessage("¡Has completado la ruta! Esperamos haberte ayudado :)");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            builder.show();
        }else{
            int startingPointID = route.getOrder().get(currentStage-1);
            int endPointID = route.getOrder().get(currentStage);



            Intent i = new Intent(this, MainActivity.class);

            if(currentStage==1){
                i.putExtra("starting_point_latitude", currentLatitude);
                i.putExtra("starting_point_longitude", currentLongitude);
                i.putExtra("starting_point_name", currentLocationName);
            }else{
                DestinationCard startingPoint = listDestinations.get(startingPointID-1);
                i.putExtra("starting_point_latitude", startingPoint.getLatitude());
                i.putExtra("starting_point_longitude", startingPoint.getLongitude());
                i.putExtra("starting_point_name", startingPoint.getDestinationName());
            }

            if(currentStage==(route.getOrder().size()-1)){
                i.putExtra("ending_point_latitude", currentLatitude);
                i.putExtra("ending_point_longitude", currentLongitude);
                i.putExtra("ending_point_name", currentLocationName);
                i.putExtra("stop_time", 0);
            }else{
                DestinationCard endingPoint = listDestinations.get(endPointID-1);
                i.putExtra("ending_point_latitude", endingPoint.getLatitude());
                i.putExtra("ending_point_longitude", endingPoint.getLongitude());
                i.putExtra("ending_point_name", endingPoint.getDestinationName());
                i.putExtra("stop_time", endingPoint.getNumberStopTime());
            }
            i.putExtra("use", "1");
            i.putExtra("duration", matrix[startingPointID][endPointID].getTimeInSeconds());
            i.putExtra("stage", currentStage);
            startActivityForResult(i, FIRST_STAGE_CODE);
        }
    }

    private void computeBestRoute() {

        /*LinearLayoutManager rvLayoutManager = (LinearLayoutManager) rvDestinations.getLayoutManager();
        for(int i = 0; i<listDestinations.size(); i++){
            listDestinations.get(i).setId(i+1);
            View v = rvLayoutManager.findViewByPosition(i);
            CheckBox isLast = v.findViewById(R.id.check_box_is_last_point);
            if(isLast.isChecked()){
                finalDestinationId = i+1;
            }
        }*/

        //Creamos la lista de coordenadas de los destinos
        ArrayList<LatLng> destinationCoordinates = new ArrayList<>();
        //Añadimos las coordenadas de nuestra posición
        destinationCoordinates.add(new LatLng(currentLatitude, currentLongitude));
        //Añadimos el resto de coordenadas
        for (int i = 0; i < listDestinations.size(); i++) {
            destinationCoordinates.add(new LatLng(listDestinations.get(i).getLatitude(), listDestinations.get(i).getLongitude()));
        }

        //Obtenemos el medio de transporte de los ajustes y si evitamos peajes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String mode = prefs.getString("mode", "driving");
        String language = prefs.getString("language", "es");
        boolean avoidTolls = prefs.getBoolean("avoid_tolls", true);
        //Creamos url
        String url = Path.getMatrixApiUrl(destinationCoordinates, mode, language, GOOGLE_KEY);
        Log.i("prueba ruta compleja", url);

        //Una vez tenemos la url creada, parseamos el json para obtener la matriz de tiempos entre destinos
        int side = destinationCoordinates.size();
        ParserJsonRuta parserJsonRuta = new ParserJsonRuta();
        int[][] costMatrix;


        try {
            //A partir de esta matriz creamos la matriz de costes que meteremos en el algoritmo de tsp
            matrix = parserJsonRuta.execute(url, String.valueOf(side)).get();
            for(int i = 0; i<listDestinations.size(); i++) {
                 String destinationName = listDestinations.get(i).getDestinationName();
                for (int j = 0; j < listDestinations.size()+1; j++) {
                    matrix[i+1][j].setStartPointName(destinationName);
                    matrix[j][i+1].setEndPointName(destinationName);
                }
            }
            costMatrix = new int[side][side];
            for (int i = 0; i < side; i++) {
                for (int j = 0; j < side; j++) {
                    costMatrix[i][j] = (int) matrix[i][j].getTimeInSeconds();
                }
            }

            //Para ver en pantalla la matriz
            for (int i = 0; i < side; i++) {
                for (int j = 0; j < side; j++) {
                    System.out.print(costMatrix[i][j] + ", ");
                }
                System.out.println();
            }


            int [] rest = generateRest(side, ComputeRouteFragment.finalDestinationId);

            TravelSalesmanProblem tsp = new TravelSalesmanProblem(0, rest, costMatrix, ComputeRouteFragment.finalDestinationId);
            route = tsp.execute().get();

            Collections.reverse(route.getOrder());

            System.out.println("El resultado es " + route.getTime() + " y el orden es: ");
            for(int i = 0; i<route.getOrder().size(); i++) {
                System.out.print(String.valueOf(route.getOrder().get(i)) + ", ");
                Log.i("prueba ruta compleja", String.valueOf(route.getOrder().get(i)));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //dialog.dismiss();

    }

    private int[] generateRest(int side, int finalDestinationId) {
        int[] rest;
        if (finalDestinationId == 0) {
            rest = new int[side - 1];
            for (int i = 0; i < (side - 1); i++) {
                rest[i] = i + 1;
            }
        } else {
            boolean coincide = false;
            rest = new int[side - 2];
            for (int i = 0; i < (side - 1); i++) {
                if ((i + 1) == finalDestinationId) {
                    coincide = true;
                } else {
                    if (!coincide) {
                        rest[i] = i + 1;
                    } else {
                        rest[i - 1] = i + 1;
                    }
                }
            }
        }
        return rest;
    }

    private void getCurrentLocation() {


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location loc = task.getResult();
                    if (loc != null) {
                       // Log.i("prueba ruta compleja", "Loc no es null");
                        Geocoder gc = new Geocoder(PlanRouteActivity.this, Locale.getDefault());

                        try {
                           // Log.i("prueba ruta compleja", "loc.getlatitude() is: " + loc.getLatitude() + ", loc.getlongitude() is: " + loc.getLongitude());
                            List<Address> addresses = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                            //Log.i("prueba ruta compleja", "addres latitude is: " + addresses.get(0).getLatitude() + ", address lonfitude is: " + addresses.get(0).getLongitude());
                            currentLatitude = addresses.get(0).getLatitude();
                            currentLongitude = addresses.get(0).getLongitude();
                            currentLocationName = addresses.get(0).getAddressLine(0);

                        } catch (IOException e) {
                           // Log.i("prueba ruta compleja", "excepcion al hacer getfromlocation");
                            e.printStackTrace();
                        }
                    }else{
                        //Log.i("prueba ruta compleja", "No has obtenido la localización");
                    }
                }
            });
        }else{
            ActivityCompat.requestPermissions(PlanRouteActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    private void addToFavRoutes() {

        AlertDialog builder = new AlertDialog.Builder(PlanRouteActivity.this).create();
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_fav_routes, null);
        builder.setView(dialogView);
        Button buttonAccept = dialogView.findViewById(R.id.button_accept_dialog_fav_routes);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tvNameRoute = dialogView.findViewById(R.id.edit_text_name_of_route);
                String name = tvNameRoute.getText().toString();
                if(name.equals("")){
                    Toast.makeText(PlanRouteActivity.this, "Debes escribir un nombre", Toast.LENGTH_LONG).show();
                }else {
                    //Toast.makeText(PlanRouteActivity.this, "Nombre: " + name, Toast.LENGTH_LONG).show();
                    DbRoutes dbRoutes = new DbRoutes(PlanRouteActivity.this);
                    long result = dbRoutes.insertRoute(name);
                    if(result>0){
                        //Toast.makeText(PlanRouteActivity.this, "Ruta creada con éxito", Toast.LENGTH_LONG).show();
                        DbDestinations dbDestinations = new DbDestinations(PlanRouteActivity.this);
                        DestinationCard destinationCard = null;
                        long result2 = 0;
                        for(int i = 0; i<listDestinations.size(); i++){
                            destinationCard = listDestinations.get(i);
                            result2 = dbDestinations.insertDestination(name, destinationCard.getDestinationName(), destinationCard.getNumberStopTime(), destinationCard.getLatitude(), destinationCard.getLongitude());
                            if(result2==0){
                                Toast.makeText(PlanRouteActivity.this, "No se ha podido crear la ruta", Toast.LENGTH_LONG).show();
                                //Aquí deberías borrar la ruta creada
                                break;
                            }
                        }
                        Toast.makeText(PlanRouteActivity.this, "Ruta creada con éxito", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(PlanRouteActivity.this, "No se ha podido crear la ruta", Toast.LENGTH_LONG).show();
                    }
                    builder.dismiss();
                }
            }
        });
        Button buttonCancel = dialogView.findViewById(R.id.button_refuse_dialog_fav_routes);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();

    }






    /*
    private ArrayList<Address> obtainDestinationsCoordinates() {
        ArrayList<Address> destinationsAddresses = new ArrayList<>();


        Geocoder gc = new Geocoder(this);

        for (int i = 0; i < destinationsAdapter.getItemCount(); i++) {
            ArrayList<Address> addresses;
            RecyclerView.ViewHolder holder = rvDestinations.findViewHolderForAdapterPosition(i);

            EditText etAddress = (EditText) holder.itemView.findViewById(R.id.destino_card);
            String address = etAddress.getText().toString();
            Log.i("Prueba ruta compleja", "Destino " + i + ": " + address);

            try {
                addresses = (ArrayList<Address>) gc.getFromLocationName(address, 1);
                if (!addresses.isEmpty()) {
                    destinationsAddresses.add(addresses.get(0));
                } else {
                    Toast.makeText(this, "El destino " + (i+1) + "es inválido", Toast.LENGTH_LONG).show();
                    break;
                }
            } catch (IOException e) {
                Log.i("Error", "Excepción al sacar coordenadas de un destino");
                Toast.makeText(this, "El destino " + (i + 1) + "es inválido", Toast.LENGTH_LONG).show();
            }
        }
        return destinationsAddresses;

    }

    /*
    public Address obtainStartingPointCoordinates() {

        Geocoder gc = new Geocoder(this);
        EditText etStartingPoint = (EditText) findViewById(R.id.punto_de_partida_ruta_compleja);
        String address = etStartingPoint.getText().toString();
        Log.i("Prueba ruta compleja", "Obteniendo coordenada de punto de partida");
        ArrayList<Address> addresses = null;
        try {
            addresses = (ArrayList<Address>) gc.getFromLocationName(address, 1);
        }catch(IOException e){
            Log.i("Prueba ruta compleja", "Excepcion al obtener corrdenadas del punto de partida de la ruta compleja");
        }
        if(!addresses.isEmpty()) {
            Log.i("Prueba ruta compleja", "El punto de partida es: " + addresses.get(0).getLatitude());
            return addresses.get(0);
        }else{
            Toast.makeText(this, "Localización de salida inválida", Toast.LENGTH_LONG ).show();
            return null;
        }
    }*/

}

