package com.carlosgarguz.routeme.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;
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

public class PopUpDisplayFavRoutesActivity extends FragmentActivity {

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
        favRoutesAdapter = new RouteDbAdapter(listFavRoutes, this);
        rvFavRoutes.setAdapter(favRoutesAdapter);



    }
}
