package com.ribarevic.antonio.weatherapp.ui.edit_location;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.ribarevic.antonio.weatherapp.database.LocationDao;
import com.ribarevic.antonio.weatherapp.database.WeatherAppDatabase;
import com.ribarevic.antonio.weatherapp.model.Location;

import java.util.List;

public class EditLocationViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Location>> locations = new MutableLiveData<>();
    public LiveData<List<Location>> getLocations() {
        return locations;
    }

    private final LocationDao locationDao = Room
            .databaseBuilder(getApplication(), WeatherAppDatabase.class, "weather_db")
            .allowMainThreadQueries()
            .build()
            .locationDao();

    public EditLocationViewModel(@NonNull Application application) {
        super(application);
        locations.setValue(locationDao.getLocations());
    }

    public void deleteLocation(Location location) {
        locationDao.deleteLocation(location);
        locations.setValue(locationDao.getLocations());
    }
}
