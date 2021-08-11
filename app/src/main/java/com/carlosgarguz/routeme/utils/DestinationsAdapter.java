package com.carlosgarguz.routeme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosgarguz.routeme.R;
import com.carlosgarguz.routeme.activities.ComputeRouteFragment;
import com.carlosgarguz.routeme.activities.PlanRouteActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.DestinationsViewHolder>{

    private List<DestinationCard> destinationsList;
    private Context context;
    private DestinationCard lastItemRemoved;
    private int lastItemRemovedPosition;

    public DestinationsAdapter(List<DestinationCard> destinationsList, Context context) {
        this.destinationsList = destinationsList;
        this.context = context;
    }

    @NonNull
    @Override
    public DestinationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.destination_card, parent, false);
        return new DestinationsViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationsViewHolder holder, int position) {
        //holder.destinationNumber.setText(destinationsList.get(position).getdestinationNumber());
        holder.destinationName.setText(destinationsList.get(position).getDestinationName());
        holder.stopTime.setText(destinationsList.get(position).getStopTime());
        //holder.isLastPoint.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return destinationsList.size();
    }

    public List<DestinationCard> getDestinationsList() {
        return destinationsList;
    }


    public void addItem(DestinationCard destino){
        if(getItemCount()==0){
            ComputeRouteFragment.buttonSimpleRoute.setVisibility(View.GONE);
        }
        destinationsList.add(destino);
        notifyDataSetChanged();
    }

    public void removeItem(int position){

        if(position == -1) {
            lastItemRemovedPosition = this.getItemCount()-1;
            lastItemRemoved = destinationsList.get(lastItemRemovedPosition);
            destinationsList.remove(lastItemRemovedPosition);
            //Cambiamos los IDs


            notifyDataSetChanged();

        }else{
            lastItemRemovedPosition = position;
            lastItemRemoved = destinationsList.get(position);
            destinationsList.remove(position);
            notifyDataSetChanged();
            ComputeRouteFragment.tvNumberDestinations.setText(String.valueOf(this.getItemCount()));
            for(int i = position; i<destinationsList.size(); i++){
                destinationsList.get(i).setId(destinationsList.get(i).getId()-1);
            }
            for(int i = 0; i<destinationsList.size(); i++){
                System.out.print(destinationsList.get(i).getId() + ", ");
            }
        }
        if(getItemCount()==0){
            ComputeRouteFragment.buttonSimpleRoute.setVisibility(View.VISIBLE);
        }
        showUndoSnackbar();
    }


    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(PlanRouteActivity.layout, R.string.texto_snack_bar, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.deshacer_snack_bar, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        if(getItemCount()==0){
            ComputeRouteFragment.buttonSimpleRoute.setVisibility(View.GONE);
        }
        destinationsList.add(lastItemRemovedPosition,
                lastItemRemoved);
        ComputeRouteFragment.tvNumberDestinations.setText(String.valueOf(getItemCount()));
        notifyDataSetChanged();
    }




    public static class DestinationsViewHolder extends RecyclerView.ViewHolder{

        //private TextView destinationNumber;
        private TextView destinationName;
        private TextView stopTime;
        private CheckBox isLastPoint;



        public DestinationsViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            //destinationNumber = itemView.findViewById(R.id.destination_number);
            destinationName = itemView.findViewById(R.id.destination_name);
            stopTime = itemView.findViewById(R.id.text_view_stop_time);
            isLastPoint = itemView.findViewById(R.id.check_box_is_last_point);




            isLastPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if (ComputeRouteFragment.finalDestinationSelected) {
                            isLastPoint.setChecked(false);
                            Toast.makeText(context, "Destino final ya seleccionado", Toast.LENGTH_LONG).show();
                        }else{
                            ComputeRouteFragment.finalDestinationSelected = true;
                            ComputeRouteFragment.finalDestinationId = getAdapterPosition()+1;
                        }
                    }else {
                        ComputeRouteFragment.finalDestinationSelected = false;
                    }
                }
            });

        }
    }
}
