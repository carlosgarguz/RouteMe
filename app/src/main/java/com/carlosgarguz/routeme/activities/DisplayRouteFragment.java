package com.carlosgarguz.routeme.activities;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.paths.RouteTime;
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
public class DisplayRouteFragment extends Fragment {

    FragmentTransaction transaction;
    Fragment fragmentComputerRoute;

    RecyclerView rvRoutes;
    RouteAdapter routesAdapter;
    ArrayList<RouteCard> listRoutes = new ArrayList<>();
    TextView tvStagesLeft;
    TextView tvTotalDuration;

    FloatingActionButton backButton;

    int totalStopTime = 0;

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


        inicializarRecycler(rootView);
        fillRecycler();

        tvStagesLeft = rootView.findViewById(R.id.text_view_number_stages_left);
        tvStagesLeft.setText(String.valueOf(routesAdapter.getItemCount()));
        tvTotalDuration = rootView.findViewById(R.id.text_view_total_duration);
        computeTotalDuration();


        return rootView;
    }




    private void inicializarRecycler(View rootView) {
        rvRoutes = rootView.findViewById(R.id.recycler_routes);

        rvRoutes.setLayoutManager(new LinearLayoutManager(getActivity()));



        routesAdapter = new RouteAdapter(listRoutes, getActivity());
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



        for(int i = 0; i<routeTimesList.size(); i++){
            RouteCard routeCard = new RouteCard();
            routeCard.setStartingPointName(routeTimesList.get(i).getStartPointName());
            routeCard.setEndingPointName(routeTimesList.get(i).getEndPointName());
            routeCard.setDurationText(routeTimesList.get(i).getTextTime());
            routeCard.setDurationNumber(routeTimesList.get(i).getTimeInSeconds());
            int destinationId = routeTimesList.get(i).getEndPointID();
            if(destinationId!=0)
                routeCard.setStopTime(((PlanRouteActivity)getActivity()).listDestinations.get(destinationId-1).getNumberStopTime());
                totalStopTime = totalStopTime + routeCard.getStopTime();

            routesAdapter.addItem(routeCard);
        }


    }


    private void computeTotalDuration() {

        int days, hours, minuts;
        String totalDuration = "";

        int durationInSeconds = ((PlanRouteActivity)getActivity()).route.getTime();
        durationInSeconds = durationInSeconds + totalStopTime*60;
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

        tvTotalDuration.setText(totalDuration);
    }

    public void shadowRoute(int position){
        LinearLayout layout = rvRoutes.getLayoutManager().findViewByPosition(position).findViewById(R.id.layout_route_card);
        layout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.card_destination_back_overshadowed));
        int stagesLeft = Integer.parseInt(tvStagesLeft.getText().toString());
        stagesLeft--;
        tvStagesLeft.setText(String.valueOf(stagesLeft));
    }
}