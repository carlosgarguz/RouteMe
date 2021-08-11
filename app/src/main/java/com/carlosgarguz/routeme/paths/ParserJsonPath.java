package com.carlosgarguz.routeme.paths;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.carlosgarguz.routeme.activities.MainActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ParserJsonPath extends AsyncTask<String, Void, Path> {




    @Override
    protected Path doInBackground(String... sUrl) {
        Path path;
        path = downloadUrlPath(sUrl[0]);
        return path;
    }

    @Override
    protected void onPostExecute(Path path) {
        Log.i("json parser", "El polylines tiene " + String.valueOf(path.getPolylines().size()) + "elementos");
        Log.i("json parser final", path.toString());
        for (int i=0; i<path.getPolylines().size(); i++){
            path.getPoints().addAll(path.decodePoly(path.getPolylines().get(i)));
        }


        drawPath(path);
        showCameraRoute(path);

        MainActivity.tvRouteTime.setVisibility(View.VISIBLE);
        MainActivity.tvRouteTime.setText(path.getDuration().getTextTime());





    }

    private void showCameraRoute(Path path) {
        LatLng salida = path.getPoints().get(0);
        LatLng destino = path.getPoints().get(path.getPoints().size()-1);


        LatLngBounds.Builder pathBounds = new LatLngBounds.Builder();
        pathBounds.include(salida).include(destino);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(pathBounds.build(), 100);
        MainActivity.map.animateCamera(cu, 1500, null);

        MarkerOptions optionsDestino = new MarkerOptions();
        optionsDestino.position(destino);



    }

    private void drawPath(Path path) {
        MainActivity.polylineOptions.addAll(path.getPoints());
        MainActivity.polylineOptions.width(7);
        MainActivity.polylineOptions.color(Color.BLUE);

        Log.i("debug polyline", "LOOL");
        for(int j=0; j<MainActivity.polylineOptions.getPoints().size(); j++){
            Log.i("debug polyline", "Puntox " + j + ": Lat = " + MainActivity.polylineOptions.getPoints().get(j).latitude + ", Long = " + MainActivity.polylineOptions.getPoints().get(j).longitude);
        }
        MainActivity.polyline = MainActivity.map.addPolyline(MainActivity.polylineOptions);
        MarkerOptions optionsDestino = new MarkerOptions();


    }


    private Path downloadUrlPath(String sUrl){

        Path path = null;
        URL url;
        BufferedInputStream inpstr = null;
        JsonReader reader;


        try{
            url = new URL(sUrl);
        }catch(Exception e){
            Log.i("Error", "URL para obtener el path mal formada");
            return null;
        }

        try{
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
            inpstr = new BufferedInputStream(httpsConnection.getInputStream());
        }catch (IOException e){
            Log.i("Error", "IO Exception al obtener el input stream de la conexión https a la api de Rutas de Google");
            return null;
        }

        if(inpstr!=null){
            try{
                reader = new JsonReader(new InputStreamReader(inpstr, "UTF-8"));
                path =  readPath(reader);
                inpstr.close();
            }catch(Exception e){
                Log.i("Error", "Exception al parsear el JSON del path");
            }
        }else{
            return null;
        }

        return path;
    }

    private Path readPath(JsonReader reader) throws IOException {
        Path path = new Path();
        String name = "";
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("routes")){
                path = readRoutesArray(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return path;
    }

    private Path readRoutesArray(JsonReader reader) throws IOException {
        Path path = new Path();
        reader.beginArray();
        while(reader.hasNext()){
            path = readRoutesObject(reader);
        }
        reader.endArray();
        return path;
    }

    private Path readRoutesObject(JsonReader reader) throws IOException {
        Path path = new Path();
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("legs")){
                path = readLegsArray(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return path;
    }

    private Path readLegsArray(JsonReader reader) throws IOException {
        Path path = new Path();
        reader.beginArray();
        while(reader.hasNext()){
            path = readLegsObject(reader);
        }
        reader.endArray();
        return path;
    }

    private Path readLegsObject(JsonReader reader) throws IOException {
        Path path = new Path();
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("duration")){
                path.setDuration(readDurationObject(reader));
            }else if(name.equals("end_address")){
                path.getEndingPoint().setTextAddress(reader.nextString());
            }else if(name.equals("end_location")){
                path.getEndingPoint().setCoordinates(readEndStartLocationObject(reader));
            }else if(name.equals("start_address")){
                path.getStartingPoint().setTextAddress(reader.nextString());
            }else if(name.equals("start_location")) {
                path.getStartingPoint().setCoordinates(readEndStartLocationObject(reader));
            }else if(name.equals("steps")){
                path.setPolylines(readStepsArray(reader));
            }else{
                reader.skipValue();
                ;
            }
        }
        reader.endObject();
        return path;
    }



    private LatLng readEndStartLocationObject(JsonReader reader) throws IOException {
        String name;
        double latitud = 0.0;
        double longitud = 0.0;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("lat")){
                latitud = reader.nextDouble();
            }else if(name.equals("long")){
                longitud = reader.nextDouble();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new LatLng(latitud, longitud);
    }


    private RouteTime readDurationObject(JsonReader reader) throws IOException {
        String name;
        RouteTime duracion = new RouteTime();
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("text")){
                duracion.setTextTime(reader.nextString());
            }else if(name.equals("value")){
                duracion.setTimeInSeconds(reader.nextLong());
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return duracion;
    }

    private ArrayList<String> readStepsArray(JsonReader reader) throws IOException {
        ArrayList<String> polylines = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()){
            polylines.add(readStepsObject(reader));
        }
        reader.endArray();
        return polylines;
    }

    private String readStepsObject(JsonReader reader) throws IOException {
        String polyline ="";
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("polyline")){
                polyline = readPolylineObject(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return polyline;
    }

    private String readPolylineObject(JsonReader reader) throws IOException {
        String polyline = "";
        String name;
        reader.beginObject();
        int i = 0;
        while(reader.hasNext()){
            name = reader.nextName();
            Log.i("debug json parser", name);
            if(name.equals("points")){
                polyline = reader.nextString();
                /*Log.i("json debuger", polylines.get(i));
                Log.i("json debuger", "tamaño de la lista es : " + polylines.size());
                i++;
                Log.i("json debuger", "i = " + i );*/
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return polyline;
    }
}