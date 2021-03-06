package com.carlosgarguz.routeme.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlosgarguz.routeme.R;

import java.util.List;

public class RouteDbAdapter extends RecyclerView.Adapter<RouteDbAdapter.RouteDbViewHolder> {

    private List<RouteCardDb> routesDbList;
    private Context context;
    private OnRouteDbListener mOnRouteDbListener;



    public RouteDbAdapter() {
    }

    public RouteDbAdapter(List<RouteCardDb> routesDbList, Context context, OnRouteDbListener mOnRouteDbListener) {
        this.routesDbList = routesDbList;
        this.context = context;
        this.mOnRouteDbListener = mOnRouteDbListener;
    }

    @NonNull
    @Override
    public RouteDbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_db_card, parent, false);
        return new RouteDbViewHolder(v, mOnRouteDbListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RouteDbViewHolder holder, int position) {
        holder.tvName.setText(routesDbList.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return routesDbList.size();
    }

    public void addItem(RouteCardDb route){
        routesDbList.add(route);
        notifyDataSetChanged();
    }

    public void clear(){
        routesDbList.clear();
        notifyDataSetChanged();
    }

    public static class RouteDbViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        OnRouteDbListener onRouteDbListener;

        public RouteDbViewHolder(@NonNull View itemView, OnRouteDbListener onRouteDbListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name_of_route);
            this.onRouteDbListener = onRouteDbListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRouteDbListener.onRouteClick(getAdapterPosition(), v);
        }
    }

    public interface OnRouteDbListener{
        void onRouteClick(int position, View view);
    }
}
