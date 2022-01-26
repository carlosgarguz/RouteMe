package com.carlosgarguz.routeme.paths;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ParserJsonRuta extends AsyncTask<String, Void, RouteTime[][]> {

    private int rowNumber = -1;
    private int columnNumber = -1;


    @Override
    protected RouteTime[][] doInBackground(String... strings) {
        int lado = Integer.parseInt(strings[1]);
        //Log.i("debug json parser", "El lado es de" + lado);
        RouteTime[][] matrix = new RouteTime[lado][lado];

        for(int i=0; i<lado; i++){
            for(int j = 0; j<lado; j++){
                matrix[i][j] = new RouteTime();
            }
        }

        downloadUrlMatrix(strings[0], lado, matrix);

        /*
        for(int i=0; i<lado; i++){
            for(int j = 0; j<lado; j++){
                Log.i("debug json parser", "Del destino " + matrix[i][j].getStartPointID() + " al "+ matrix[i][j].getEndPointID() + " hay "  + matrix[i][j].getTextTime());

            }
        }*/





        return matrix;
    }

    private void downloadUrlMatrix(String sUrl, int lado, RouteTime[][] matrix) {

        URL url;
        BufferedInputStream inpstr = null;
        JsonReader reader;


        try{
            url = new URL(sUrl);
        }catch(Exception e){
            Log.i("Error", "URL para obtener la matriz mal formada");
            return ;
        }

        try{
            HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
            inpstr = new BufferedInputStream(httpsConnection.getInputStream());
        }catch (IOException e){
            Log.i("Error", "IO Exception al obtener el input stream de la conexión https a la api de Matriz de Google");
            return ;
        }

        if(inpstr!=null){
            try{
                reader = new JsonReader(new InputStreamReader(inpstr, "UTF-8"));
                readMatrix(reader, matrix, lado);
                /*for(int i=0; i<Integer.parseInt(destinationAmount); i++){
                    for(int j = 0; j<Integer.parseInt(destinationAmount); j++){
                        Log.i("debug json parser", "Fila " + i + ", columna " + j);

                    }
                }*/
                inpstr.close();
            }catch(Exception e){
                Log.i("Error", "Exception al parsear el JSON de la matriz");
            }
        }else{
            return;
        }

        return;
    }

    private RouteTime[][] readMatrix(JsonReader reader, RouteTime[][] matrix, int lado) throws IOException {
        rowNumber = -1;
        String name = "";
        reader.beginObject();

        while(reader.hasNext()){
            name = reader.nextName();
            //Log.i("debug json parser", name);
            if(name.equals("destination_addresses")){
                readDestinationAddressArray(reader, matrix, lado);
            }else if(name.equals("rows")){
                readRowsArray(reader, matrix);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return matrix;
    }

    private RouteTime[][] readDestinationAddressArray(JsonReader reader, RouteTime[][] matrix, int lado) throws IOException {
        ArrayList<String> destinos = null;

        reader.beginArray();
        int counter = 0;
        while(reader.hasNext()) {
            String name = reader.nextString();
            if(counter==0) {
                for (int i = 0; i < lado; i++) {
                    matrix[counter][i].setStartPointName(name);
                    matrix[i][counter].setEndPointName(name);
                }
            }
            counter++;
        }
        //reader.skipValue();
        reader.endArray();
        Log.i("debug json parser", "Has entrado 0");
        return matrix;
    }

    private RouteTime[][] readRowsArray(JsonReader reader, RouteTime[][] matrix) throws IOException {
        reader.beginArray();
        while(reader.hasNext()){
            matrix = readRowsObject(reader, matrix);
        }
        reader.endArray();
        return matrix;
    }

    private RouteTime[][] readRowsObject(JsonReader reader, RouteTime[][] matrix) throws IOException {
        //Log.i("debug json parser", "Has entrado");
        String name;
        reader.beginObject();
        int i = 0;
        while(reader.hasNext()){
            name = reader.nextName();
           Log.i("debug json parser", name);
            if(name.equals("elements")){
                columnNumber = -1;
                rowNumber++;
                readElementsArray(reader, matrix);
            }else{
                reader.skipValue();
            }
        }

        reader.endObject();
        return matrix;
    }

    private RouteTime[][] readElementsArray(JsonReader reader, RouteTime[][] matrix) throws IOException {
        //Log.i("debug json parser", "Has entrado en readElementsArray");

        reader.beginArray();
        while(reader.hasNext()){
            readElementsObject(reader, matrix);
        }
        reader.endArray();
        return matrix;
    }

    private RouteTime[][] readElementsObject(JsonReader reader, RouteTime[][] matrix) throws IOException {
        //Log.i("debug json parser", "Has entrado en readElementsObject");
        String name;
        reader.beginObject();

        while(reader.hasNext()){
            name = reader.nextName();
           // Log.i("debug json parser", name);
            if(name.equals("distance")){
                readDistanceObject(reader, matrix);
            }else if(name.equals("duration")) {
                readDurationObject(reader, matrix);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return matrix;
    }

    private void readDistanceObject(JsonReader reader, RouteTime[][] matrix) throws IOException {
        String name;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            //Log.i("debug json parser", name);
            if (name.equals("text")) {

                columnNumber++;
                matrix[rowNumber][columnNumber].setTextDistance(reader.nextString());

                //Log.i("debug json parser", "duracion en texto: " + reader.nextString());
            } else if (name.equals("value")) {

                matrix[rowNumber][columnNumber].setDistanceInNumber(reader.nextLong());
                //Log.i("debug json parser", "duracion en numero: " + reader.nextLong());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private RouteTime[][] readDurationObject(JsonReader reader, RouteTime[][] matrix) throws IOException {
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            //Log.i("debug json parser", name);
            if(name.equals("text")) {


              //  Log.i("debug json parser", "numero de fila: " + rowNumber + ", y numero de columna: " + columnNumber);
                matrix[rowNumber][columnNumber].setTextTime(reader.nextString());
                matrix[rowNumber][columnNumber].setStartPointID(rowNumber);
                matrix[rowNumber][columnNumber].setEndPointID(columnNumber);
                //Log.i("debug json parser", "duracion en texto: " + reader.nextString());
            }else if(name.equals("value")){

                matrix[rowNumber][columnNumber].setTimeInSeconds(reader.nextLong());
                //Log.i("debug json parser", "duracion en numero: " + reader.nextLong());
            }else{
                reader.skipValue();
            }
        }


        reader.endObject();
        return matrix;
    }


}
