package com.carlosgarguz.routeme.utils;

import android.content.Context;
import android.media.TimedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosgarguz.routeme.R;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder>{

    private List<RouteCard> routesList;
    private Context context;
    private OnRouteListener onRouteListener;
    private String measurement;

    public RouteAdapter(List<RouteCard> routesList, Context context, OnRouteListener onRouteListener, String measurement) {
        this.routesList = routesList;
        this.context = context;
        this.onRouteListener = onRouteListener;
        this.measurement = measurement;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card, parent, false);
        return new RouteViewHolder(v, onRouteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {

        holder.tvStartingPoint.setText(routesList.get(position).getStartingPointName());
        holder.tvEndingPoint.setText(routesList.get(position).getEndingPointName());
        if(measurement.equals("tiempo")) {
            holder.tvDuration.setText(routesList.get(position).getDurationText());
        }else{
            holder.tvDuration.setText(routesList.get(position).getDistanceText() + "  ->  " + routesList.get(position).getDurationText());
        }
        int stopTime = routesList.get(position).getStopTime();
        if(stopTime!=0) {
            holder.layoutStopTime.setVisibility(View.VISIBLE);
            holder.tvStopTime.setText(String.valueOf(stopTime) + " minutos de parada");

        }
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    public void addItem(RouteCard route){
        routesList.add(route);
        notifyDataSetChanged();
    }

    public void clear(){
        routesList.clear();
        notifyDataSetChanged();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvStartingPoint;
        private TextView tvEndingPoint;
        private TextView tvDuration;
        private TextView tvStopTime;
        private LinearLayout layoutStopTime;
        private LinearLayout layutCard;
        OnRouteListener onRouteListener;


        public RouteViewHolder(@NonNull View itemView, OnRouteListener onRouteListener) {
            super(itemView);

            layutCard = itemView.findViewById(R.id.layout_route_card);
            tvStartingPoint = itemView.findViewById(R.id.route_starting_point_name);
            tvEndingPoint = itemView.findViewById(R.id.route_ending_point_name);
            tvDuration = itemView.findViewById(R.id.route_duration);
            tvStopTime = itemView.findViewById((R.id.text_view_rest_time));
            layoutStopTime = itemView.findViewById(R.id.layout_stop_time);
            this.onRouteListener = onRouteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRouteListener.onRouteClick(getAdapterPosition(), v);
        }
    }

    public interface OnRouteListener{
        void onRouteClick(int position, View view);
    }
}
