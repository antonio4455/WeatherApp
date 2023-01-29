package com.ribarevic.antonio.weatherapp.ui.edit_location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.ribarevic.antonio.weatherapp.R;
import com.ribarevic.antonio.weatherapp.databinding.FragmentEditLocationBinding;
import com.ribarevic.antonio.weatherapp.model.Location;
import com.ribarevic.antonio.weatherapp.ui.current_temperature.CurrentTemperatureFragment;
import com.ribarevic.antonio.weatherapp.ui.current_temperature.CurrentTemperatureViewModel;

import java.util.List;

public class EditLocationFragment extends Fragment implements DeleteLocationClickListener, LocationClickListener {

    private FragmentEditLocationBinding binding;
    private EditLocationViewModel editLocationViewModel;
    private CurrentTemperatureViewModel currentTemperatureViewModel;
    private final LocationAdapter locationAdapter = new LocationAdapter(this, this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editLocationViewModel = new ViewModelProvider(this).get(EditLocationViewModel.class);
        currentTemperatureViewModel = new ViewModelProvider(requireActivity()).get(CurrentTemperatureViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Observer<List<Location>> locationsObserver = new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable final List<Location> locations) {
                if (locations != null) {
                   locationAdapter.setLocations(locations);
                }
            }
        };

        editLocationViewModel.getLocations().observe(getViewLifecycleOwner(), locationsObserver);

        binding.textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTemperatureViewModel.setLocation(binding.tietCityName.getText().toString());
                goBack();
            }
        });

        binding.rvRecentLocation.setAdapter(locationAdapter);
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDeleteLocationClick(Location location) {
        editLocationViewModel.deleteLocation(location);
    }

    @Override
    public void onLocationClick(Location location) {
        currentTemperatureViewModel.setLocation(location.getCityName());
        goBack();
    }
}