package com.carlosgarguz.routeme.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.paths.Route;
import com.carlosgarguz.routeme.paths.RouteTime;
import com.carlosgarguz.routeme.utils.DestinationCard;
import com.carlosgarguz.routeme.utils.DestinationsAdapter;
import com.carlosgarguz.routeme.utils.RouteAdapter;
import com.carlosgarguz.routeme.utils.RouteCard;
import com.carlosgarguz.routeme.utils.SwipeDestinationCardCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayRouteFragment extends Fragment implements RouteAdapter.OnRouteListener {

    FragmentTransaction transaction;
    Fragment fragmentComputerRoute;

    RecyclerView rvRoutes;
    RouteAdapter routesAdapter;
    ArrayList<RouteCard> listRoutes = new ArrayList<>();
    TextView tvStagesLeft;
    TextView tvTotalDuration;
    TextView tvTotalDistance;

    FloatingActionButton backButton;

    int totalStopTime = 0;
    String measurement;

    public DisplayRouteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayRouteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayRouteFragment newInstance(String param1, String param2) {
        DisplayRouteFragment fragment = new DisplayRouteFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           /* mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_display_route, container, false);

        fragmentComputerRoute = new ComputeRouteFragment();
        tvTotalDuration = rootView.findViewById(R.id.text_view_total_duration);
        tvTotalDistance = rootView.findViewById(R.id.text_view_total_distance);


        backButton = rootView.findViewById(R.id.return_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container_plan_route, fragmentComputerRoute, "COMPUTE_ROUTE").commit();
                ((PlanRouteActivity)getActivity()).activeFragment = 0;
                routesAdapter.clear();
                ((PlanRouteActivity)getActivity()).buttonComputeRoute.setText(R.string.calcular_ruta);


                //transaction.addToBackStack(null);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        measurement = prefs.getString("measurement", "tiempo");

        inicializarRecycler(rootView, measurement);
        fillRecycler();

        tvStagesLeft = rootView.findViewById(R.id.text_view_number_stages_left);
        tvStagesLeft.setText(String.valueOf(routesAdapter.getItemCount()));





        return rootView;
    }




    private void inicializarRecycler(View rootView, String measurement) {
        rvRoutes = rootView.findViewById(R.id.recycler_routes);

        rvRoutes.setLayoutManager(new LinearLayoutManager(getActivity()));





        routesAdapter = new RouteAdapter(listRoutes, getActivity(), this, measurement);
        rvRoutes.setAdapter(routesAdapter);

        /*ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDestinationCardCallback(des));
        itemTouchHelper.attachToRecyclerView(rvDestinations);*/
    }

    private void fillRecycler() {

        ArrayList<Integer> order = ((PlanRouteActivity)getActivity()).route.getOrder();
        order.add(ComputeRouteFragment.finalDestinationId);


        ArrayList<RouteTime> routeTimesList = new ArrayList<>();

        for(int i=0; i<(order.size()-1); i++){
            routeTimesList.add(((PlanRouteActivity)getActivity()).matrix[order.get(i)][order.get(i+1)]);
        }


        if(((PlanRouteActivity)getActivity()).startingPoint==0) {

            for (int i = 0; i < routeTimesList.size(); i++) {
                RouteCard routeCard = new RouteCard();
                routeCard.setStartingPointName(routeTimesList.get(i).getStartPointName());
                routeCard.setEndingPointName(routeTimesList.get(i).getEndPointName());
                routeCard.setDurationText(routeTimesList.get(i).getTextTime());
                routeCard.setDurationNumber(routeTimesList.get(i).getTimeInSeconds());
                routeCard.setDistanceText(routeTimesList.get(i).getTextDistance());
                routeCard.setDistanceNumber(routeTimesList.get(i).getDistanceInNumber());
                int destinationId = routeTimesList.get(i).getEndPointID();
                if (destinationId != 0)
                    routeCard.setStopTime(((PlanRouteActivity) getActivity()).listDestinations.get(destinationId - 1).getNumberStopTime());
                totalStopTime = totalStopTime + routeCard.getStopTime();

                routesAdapter.addItem(routeCard);
            }
        }else {

            for (int i = 0; i < routeTimesList.size(); i++) {
                RouteCard routeCard = new RouteCard();
                routeCard.setStartingPointName(routeTimesList.get(i).getStartPointName());
                routeCard.setEndingPointName(routeTimesList.get(i).getEndPointName());
                routeCard.setDurationText(routeTimesList.get(i).getTextTime());
                routeCard.setDurationNumber(routeTimesList.get(i).getTimeInSeconds());
                int destinationId = routeTimesList.get(i).getEndPointID();
                if (destinationId != (((PlanRouteActivity) getActivity()).startingPoint - 1))
                    routeCard.setStopTime(((PlanRouteActivity) getActivity()).listDestinations.get(destinationId).getNumberStopTime());
                totalStopTime = totalStopTime + routeCard.getStopTime();

                routesAdapter.addItem(routeCard);

            }
        }


        long totalDuration = 0;
        for(int i=0; i<(order.size()-1); i++){
            totalDuration = totalDuration + routeTimesList.get(i).getTimeInSeconds();
        }
        totalDuration = totalDuration + totalStopTime*60;
        String sTotalDuration = parseTotalDuration((int) totalDuration);
        tvTotalDuration.setText(sTotalDuration);


        if(measurement.equals("distancia")){
            long totalDistance = 0;
            for(int i=0; i<(order.size()-1); i++){
                totalDistance = totalDistance + routeTimesList.get(i).getDistanceInNumber();
            }
            String sTotalDistance = parseTotalDistance((int) totalDistance);
            tvTotalDistance.setText(sTotalDistance);
            tvTotalDistance.setVisibility(View.VISIBLE);
        }






    }

    private String parseTotalDistance(int distanceInMeters) {
        int km, m;
        String totalDistance = "";

        km = distanceInMeters/1000;
        m = distanceInMeters-km*1000;
        if(km>0){
            totalDistance = km + "km ";
        }
        if(m>0){
            totalDistance = totalDistance + m + "m";
        }

        return totalDistance;

    }


    private String parseTotalDuration(int durationInSeconds) {

        int days, hours, minuts;
        String totalDuration = "";

        //seconds = durationInSeconds%60;
        days = (durationInSeconds/86400);
        hours = (durationInSeconds-days*86400)/3600;
        minuts = (durationInSeconds-days*86400-hours*3600)/60;

        minuts++;
        if(minuts==60){
            minuts = 0;
            hours++;
            if(hours==24){
                hours = 0;
                days++;
            }
        }

        if(days>0){
            totalDuration = days + "d ";
        }
        if (hours>0){
            totalDuration = totalDuration + hours + "h ";
        }
        if (minuts>0){
            totalDuration = totalDuration + minuts + " min";
        }
        if(totalDuration.equals(""))
            totalDuration = "1 min";

        return totalDuration;
    }

    public void shadowRoute(int position){
        LinearLayout layout = rvRoutes.getLayoutManager().findViewByPosition(position).findViewById(R.id.layout_route_card);
        layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.card_destination_back_overshadowed));
        int stagesLeft = Integer.parseInt(tvStagesLeft.getText().toString());
        stagesLeft--;
        tvStagesLeft.setText(String.valueOf(stagesLeft));
    }

    @Override
    public void onRouteClick(int position, View view) {

        RouteCard route = listRoutes.get(position);
        Intent i = new Intent(getContext(), PopUpFirebaseInfoActivity.class);
        i.putExtra("street_name", route.getEndingPointName());


        int destinationId = ((PlanRouteActivity)getActivity()).route.getOrder().get(position+1);

        if(((PlanRouteActivity)getActivity()).startingPoint == 0) {
            if (destinationId == 0) {
                i.putExtra("latitude", ((PlanRouteActivity) getActivity()).currentLatitude);
                i.putExtra("longitude", ((PlanRouteActivity) getActivity()).currentLongitude);
            } else {
                DestinationCard destination = ((PlanRouteActivity) getActivity()).listDestinations.get(destinationId - 1);
                i.putExtra("latitude", destination.getLatitude());
                i.putExtra("longitude", destination.getLongitude());
            }
        }else{
            DestinationCard destination = ((PlanRouteActivity) getActivity()).listDestinations.get(destinationId);
            i.putExtra("latitude", destination.getLatitude());
            i.putExtra("longitude", destination.getLongitude());
        }



        startActivity(i);



    }
}