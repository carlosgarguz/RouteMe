package com.carlosgarguz.routeme.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.utils.DestinationCard;
import com.carlosgarguz.routeme.utils.DestinationsAdapter;
import com.carlosgarguz.routeme.utils.SwipeDestinationCardCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComputeRouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComputeRouteFragment extends Fragment  {

    final int PLACE_PICKER_REQUEST = 100;

    RecyclerView rvDestinations;

    DestinationsAdapter destinationsAdapter;
    public static TextView tvNumberDestinations;

    public static boolean finalDestinationSelected;
    public static int finalDestinationId;



    FloatingActionButton buttonAddDestination;
    FloatingActionButton buttonRemoveDestination;
    public static Button buttonSimpleRoute;

    public ComputeRouteFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ComputeRouteFragment newInstance(String param1, String param2) {
        ComputeRouteFragment fragment = new ComputeRouteFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_compute_route, container, false);

        buttonSimpleRoute = rootView.findViewById(R.id.button_simple_route);
        buttonSimpleRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MainActivity.class);
                i.putExtra("use", "0");
                startActivity(i);
            }
        });

        buttonAddDestination = rootView.findViewById(R.id.add_destination_button);
        buttonAddDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDestination();
            }
        });
        //buttonAddDestination.setOnClickListener(getActivity());
        buttonRemoveDestination = rootView.findViewById(R.id.remove_destination_button);
        buttonRemoveDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDestination();
            }
        });
        //buttonRemoveDestination.setOnClickListener(this);
        tvNumberDestinations = rootView.findViewById(R.id.text_view_number_destinations);
        finalDestinationSelected = false;
        finalDestinationId = 0;

        inicializarRecycler(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }


    private void inicializarRecycler(View rootView) {
        rvDestinations = rootView.findViewById(R.id.recycler_destinations);

        rvDestinations.setLayoutManager(new LinearLayoutManager(getActivity()));



        destinationsAdapter = new DestinationsAdapter(((PlanRouteActivity)getActivity()).listDestinations, getActivity());
        rvDestinations.setAdapter(destinationsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeDestinationCardCallback(destinationsAdapter));
        itemTouchHelper.attachToRecyclerView(rvDestinations);



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                DestinationCard destino = new DestinationCard();
                if(destinationsAdapter.getItemCount()==0){
                    buttonSimpleRoute.setVisibility(View.GONE);
                }
                //destino.setdestinationNumber("Destino " + (destinationsAdapter.getItemCount()+1) + ":");
                destino.setDestinationName(data.getStringExtra("destination_name"));
                destino.setNumberStopTime(Integer.parseInt(data.getStringExtra("stop_time")));
                destino.setStopTime("Se efectuará una parada de " + data.getStringExtra("stop_time") + " min");
                destino.setLatitude(Double.parseDouble(data.getStringExtra("destination_latitude")));
                destino.setLongitude(Double.parseDouble(data.getStringExtra("destination_longitude")));
                destino.setId(((PlanRouteActivity)getActivity()).listDestinations.size()+1);
                destinationsAdapter.addItem(destino);
                String numberDestinations =  String.valueOf(destinationsAdapter.getItemCount());
                tvNumberDestinations.setText(numberDestinations);

                //Toast.makeText(this, "Bien", Toast.LENGTH_LONG);
            }else{
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG);
                }
        }
    }


    private void removeDestination() {
        if(destinationsAdapter.getItemCount()==0){
            Toast.makeText(getActivity(), "No Hay destinos programados", Toast.LENGTH_LONG).show();
        }else {
            destinationsAdapter.removeItem(-1);
            String numberDestinations =  String.valueOf(destinationsAdapter.getItemCount());

            tvNumberDestinations.setText(numberDestinations);
        }
    }


    private void addDestination() {

        Intent i = new Intent(getActivity().getApplicationContext(), PopUpPlaceActivity.class);
        startActivityForResult(i, PLACE_PICKER_REQUEST);
        //rvDestinations.setAdapter(destinationsAdapter);
        //Log.i("prueba rv", "size: " + destinationsAdapter.getItemCount());


    }
}