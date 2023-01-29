package com.ribarevic.antonio.weatherapp.ui.current_temperature;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ribarevic.antonio.weatherapp.R;
import com.ribarevic.antonio.weatherapp.databinding.FragmentCurrentTemperatureBinding;
import com.ribarevic.antonio.weatherapp.model.Temperature;
import com.ribarevic.antonio.weatherapp.ui.MainActivity;

public class CurrentTemperatureFragment extends Fragment implements MenuProvider {

    private FragmentCurrentTemperatureBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private CurrentTemperatureViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrentTemperatureViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCurrentTemperatureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        final Observer<String> locationObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String location) {
                if (location != null) {
                    ((MainActivity) requireActivity()).getSupportActionBar().setTitle(location);
                } else {
                    ((MainActivity) requireActivity()).getSupportActionBar().setTitle("");
                    // Before you perform the actual permission request, check whether your app
                    // already has the permissions, and whether your app needs to show a permission
                    // rationale dialog. For more details, see Request permissions.
                    locationPermissionRequest.launch(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });
                }
            }
        };

        final Observer<Temperature> temperatureObserver = new Observer<Temperature>() {
            @Override
            public void onChanged(Temperature temperature) {
                if (temperature != null) {
                    binding.tvCurrentTemperatureValue.setText(temperature.getTemp().toString() + "°C");
                } else {
                    binding.tvCurrentTemperatureValue.setText("");
                }
            }
        };

        viewModel.getActiveLocation().observe(getViewLifecycleOwner(), locationObserver);
        viewModel.getCurrentTemperature().observe(getViewLifecycleOwner(), temperatureObserver);
    }

    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            getCurrentLocation();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            getCurrentLocation();
                        } else {
                            // No location access granted.
                        }
                    }
            );

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(((MainActivity) requireActivity()), new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if (location != null) {
                            viewModel.getCurrentTemperature(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.checkIfThereIsActiveLocation();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_change_location) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_to_edit_location);
            return true;
        }
        return false;
    }

    @Override
    public void onPrepareMenu(@NonNull Menu menu) {
        MenuProvider.super.onPrepareMenu(menu);
    }

    @Override
    public void onMenuClosed(@NonNull Menu menu) {
        MenuProvider.super.onMenuClosed(menu);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}