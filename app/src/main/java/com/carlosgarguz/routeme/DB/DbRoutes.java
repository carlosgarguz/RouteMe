package com.carlosgarguz.routeme.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.carlosgarguz.routeme.utils.DestinationCard;
import com.carlosgarguz.routeme.utils.RouteCard;
import com.carlosgarguz.routeme.utils.RouteCardDb;

import java.util.ArrayList;

public class DbRoutes extends DbAssistant {

    private Context context;

    public DbRoutes(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertRoute(String name){

        long id = 0;
        try {
            DbAssistant dbAssistant = new DbAssistant(context);
            SQLiteDatabase db = dbAssistant.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);

            id = db.insert(TABLE_ROUTES, null, values);
        }catch(Exception e){
            e.toString();
        }
        return id;
    }

    public ArrayList<RouteCardDb> showDestinations(){
        DbAssistant dbAssistant = new DbAssistant(context);
        SQLiteDatabase db = dbAssistant.getWritableDatabase();

        ArrayList<RouteCardDb> routesList = new ArrayList<>();
        RouteCardDb route = null;
        Cursor routesCursor = null;

        routesCursor = db.rawQuery("SELECT * FROM " + TABLE_ROUTES, null);

        if(routesCursor.moveToFirst()){
            do{
                route = new RouteCardDb();
                route.setId(routesCursor.getInt(0));
                route.setName(routesCursor.getString(1));

                routesList.add(route);
            }while (routesCursor.moveToNext());
        }

        routesCursor.close();

        return routesList;
    }


    public boolean deleteRouteByName(String nameRoute){
        boolean result = false;

        DbAssistant dbAssistant = new DbAssistant(context);
        SQLiteDatabase db = dbAssistant.getWritableDatabase();

        try{
            db.execSQL("DELETE FROM " + TABLE_ROUTES + " WHERE name = \"" + nameRoute + "\"");
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
