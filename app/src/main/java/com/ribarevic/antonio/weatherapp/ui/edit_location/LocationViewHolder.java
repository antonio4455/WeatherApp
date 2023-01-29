package com.ribarevic.antonio.weatherapp.ui.edit_location;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ribarevic.antonio.weatherapp.databinding.LocationItemBinding;
import com.ribarevic.antonio.weatherapp.model.Location;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    final LocationItemBinding binding;

    public LocationViewHolder(@NonNull LocationItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindItem(Location location) {
        binding.tvLocation.setText(location.getCityName());
    }
}
