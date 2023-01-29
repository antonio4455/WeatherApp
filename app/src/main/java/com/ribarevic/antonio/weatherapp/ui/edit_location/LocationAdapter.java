package com.ribarevic.antonio.weatherapp.ui.edit_location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ribarevic.antonio.weatherapp.databinding.LocationItemBinding;
import com.ribarevic.antonio.weatherapp.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private List<Location> locations;
    private final LocationClickListener locationClickListener;
    private final DeleteLocationClickListener deleteLocationClickListener;

    public LocationAdapter(
            LocationClickListener locationClickListener,
            DeleteLocationClickListener deleteLocationClickListener
    ) {
        this.locationClickListener = locationClickListener;
        this.deleteLocationClickListener = deleteLocationClickListener;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LocationViewHolder holder = new LocationViewHolder(
                LocationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
        holder.binding.ibtnDeleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocationClickListener.onDeleteLocationClick(
                        locations.get(holder.getAdapterPosition())
                );
            }
        });
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClickListener.onLocationClick(
                        locations.get(holder.getAdapterPosition())
                );
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.bindItem(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}

