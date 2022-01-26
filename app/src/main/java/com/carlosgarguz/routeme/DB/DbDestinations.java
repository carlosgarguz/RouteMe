package com.carlosgarguz.routeme.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.carlosgarguz.routeme.utils.DestinationCard;

import java.security.spec.ECField;
import java.util.ArrayList;

public class DbDestinations extends DbAssistant{

    Context context;

    public DbDestinations(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertDestination(String routeName, String name, int stopTime, double latitude, double longitude){

        long id = 0;
        try {
            DbAssistant dbAssistant = new DbAssistant(context);
            SQLiteDatabase db = dbAssistant.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("routeName", routeName);
            values.put("name", name);
            values.put("stopTime", stopTime);
            values.put("latitude", latitude);
            values.put("longitude", longitude);

            id = db.insert(TABLE_DESTINATIONS, null, values);
        }catch(Exception e){
            e.toString();
        }
        return id;
    }


    public ArrayList<DestinationCard> showDestinations(){
        DbAssistant dbAssistant = new DbAssistant(context);
        SQLiteDatabase db = dbAssistant.getWritableDatabase();

        ArrayList<DestinationCard> destinationsList = new ArrayList<>();
        DestinationCard destination = null;
        Cursor destinationsCursor = null;

        destinationsCursor = db.rawQuery("SELECT * FROM " + TABLE_DESTINATIONS, null);

        if(destinationsCursor.moveToFirst()){
            do{
                destination = new DestinationCard();
                destination.setId(destinationsCursor.getInt(0));
                destination.setDestinationName(destinationsCursor.getString(2));
                destination.setNumberStopTime(destinationsCursor.getInt(3));
                destination.setStopTime("Se efectuará una parada de " + destinationsCursor.getInt(3) + " min");
                destination.setLatitude(destinationsCursor.getDouble(4));
                destination.setLongitude(destinationsCursor.getDouble(5));
                destinationsList.add(destination);
            }while (destinationsCursor.moveToNext());
        }

        destinationsCursor.close();

        return destinationsList;
    }

    public ArrayList<DestinationCard> showDestinationsOfSpecificRoute(String nameRoute){
        DbAssistant dbAssistant = new DbAssistant(context);
        SQLiteDatabase db = dbAssistant.getWritableDatabase();

        ArrayList<DestinationCard> destinationsList = new ArrayList<>();
        DestinationCard destination = null;
        Cursor destinationsCursor = null;

        destinationsCursor = db.rawQuery("SELECT * FROM " + TABLE_DESTINATIONS +
                " WHERE routeName = \"" + nameRoute + "\"", null);

        if(destinationsCursor.moveToFirst()){
            do{
                destination = new DestinationCard();
                destination.setId(destinationsCursor.getInt(0));
                destination.setDestinationName(destinationsCursor.getString(2));
                destination.setNumberStopTime(destinationsCursor.getInt(3));
                destination.setStopTime("Se efectuará una parada de " +
                        destinationsCursor.getInt(3) + " min");
                destination.setLatitude(destinationsCursor.getDouble(4));
                destination.setLongitude(destinationsCursor.getDouble(5));
                destinationsList.add(destination);
            }while (destinationsCursor.moveToNext());
        }

        destinationsCursor.close();

        return destinationsList;
    }

    public boolean deleteDestinationsOfSpecificRoute(String nameRoute){
        boolean result = false;

        DbAssistant dbAssistant = new DbAssistant(context);
        SQLiteDatabase db = dbAssistant.getWritableDatabase();

        try{
            db.execSQL("DELETE FROM " + TABLE_DESTINATIONS + " WHERE routeName = \"" + nameRoute + "\"");
            result = true;
        }catch (Exception e){
            e.toString();
            result = false;
        }finally {
            db.close();
        }

        return result;
    }

}
