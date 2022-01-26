package com.carlosgarguz.routeme.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosgarguz.routeme.DB.DbDestinations;
import com.carlosgarguz.routeme.DB.DbRoutes;
import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.utils.DestinationsAdapter;
import com.carlosgarguz.routeme.utils.RouteCardDb;
import com.carlosgarguz.routeme.utils.RouteDbAdapter;

import java.util.ArrayList;

import static android.view.View.GONE;

public class PopUpDisplayFavRoutesActivity extends FragmentActivity implements RouteDbAdapter.OnRouteDbListener {

    RecyclerView rvFavRoutes;
    RouteDbAdapter favRoutesAdapter;
    ArrayList<RouteCardDb> listFavRoutes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_display_fav_routes);



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * .85), (int) (height * .6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        initializeRecyclerFavRoutes();
    }

    private void initializeRecyclerFavRoutes() {
        rvFavRoutes = findViewById(R.id.recycler_fav_routes);


        rvFavRoutes.setLayoutManager(new LinearLayoutManager(this));

        DbRoutes dbRoutes = new DbRoutes(this);
        listFavRoutes = dbRoutes.showDestinations();
        favRoutesAdapter = new RouteDbAdapter(listFavRoutes, this, this);
        rvFavRoutes.setAdapter(favRoutesAdapter);



    }

    @Override
    public void onRouteClick(int position, View view) {

        String nameFavRoute = listFavRoutes.get(position).getName();
        LinearLayout layout = view.findViewById(R.id.layout_destinations_fav_routes);
        if(layout.getVisibility()==View.GONE) {
            Button acceptButton = view.findViewById(R.id.button_launch_fav_route);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PopUpDisplayFavRoutesActivity.this, PlanRouteActivity.class);
                    intent.putExtra("fav_route_name", nameFavRoute);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            Button deleteButton = view.findViewById(R.id.button_delete_fav_route);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopUpDisplayFavRoutesActivity.this);
                    builder.setMessage("¿Desea eliminar esta ruta?")
                            .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DbRoutes dbRoutes = new DbRoutes(PopUpDisplayFavRoutesActivity.this);
                                    DbDestinations dbDestinations = new DbDestinations(PopUpDisplayFavRoutesActivity.this);
                                    Intent intent = new Intent(PopUpDisplayFavRoutesActivity.this, PlanRouteActivity.class);
                                    intent.putExtra("fav_route_name", nameFavRoute);
                                    if (dbRoutes.deleteRouteByName(nameFavRoute)) {
                                        if (dbDestinations.deleteDestinationsOfSpecificRoute(nameFavRoute)) {
                                            setResult(201, intent);
                                            finish();
                                        } else {
                                            dbRoutes.insertRoute(nameFavRoute);
                                            setResult(202, intent);
                                            finish();
                                        }

                                    } else {
                                        setResult(202, intent);
                                        finish();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                }
            });

            layout.setVisibility(View.VISIBLE);
        }else{
            layout.setVisibility(View.GONE);
        }

    }
}
