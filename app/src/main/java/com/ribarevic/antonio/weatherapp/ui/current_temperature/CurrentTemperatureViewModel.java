package com.ribarevic.antonio.weatherapp.ui.current_temperature;

import static com.android.volley.VolleyLog.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.ribarevic.antonio.weatherapp.database.LocationDao;
import com.ribarevic.antonio.weatherapp.database.WeatherAppDatabase;
import com.ribarevic.antonio.weatherapp.model.Location;
import com.ribarevic.antonio.weatherapp.model.Temperature;
import com.ribarevic.antonio.weatherapp.model.WeatherData;

import java.util.Objects;

public class CurrentTemperatureViewModel extends AndroidViewModel {

    private final String REQUEST_TAG = "CurrentTemperatureRequestTag";
    private final RequestQueue requestQueue = Volley.newRequestQueue(getApplication());
    private final Gson gson = new Gson();
    private final LocationDao locationDao = Room
            .databaseBuilder(getApplication(), WeatherAppDatabase.class, "weather_db")
            .allowMainThreadQueries()
            .build()
            .locationDao();

    private final MutableLiveData<String> activeLocation = new MutableLiveData<>(null);

    public LiveData<String> getActiveLocation() {
        return activeLocation;
    }

    public final MutableLiveData<Temperature> currentTemperature = new MutableLiveData<>();

    public LiveData<Temperature> getCurrentTemperature() {
        return currentTemperature;
    }

    public CurrentTemperatureViewModel(@NonNull Application application) {
        super(application);
        getStoredActiveLocation();
    }

    public void getStoredActiveLocation() {
        Location location = locationDao.getActiveLocation();
        if (location != null) {
            activeLocation.setValue(location.getCityName());
            getCurrentTemperature(location.getCityName());
        }
    }

    public void getCurrentTemperature(Double latitude, Double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=7f2d7bf41b905f6e4f6dc76b1178ae85" + "&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WeatherData weatherData = gson.fromJson(response, WeatherData.class);
                        activeLocation.setValue(weatherData.getName());
                        currentTemperature.setValue(weatherData.getMain());
                        Location location = new Location();
                        location.setActive(true);
                        location.setCityName(weatherData.getName());
                        locationDao.insertLocation(location);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        stringRequest.setTag(REQUEST_TAG);

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void getCurrentTemperature(String name) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + name + "&appid=7f2d7bf41b905f6e4f6dc76b1178ae85" + "&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WeatherData weatherData = gson.fromJson(response, WeatherData.class);
                        if (activeLocation.getValue() != null) {
                            if(!Objects.equals(activeLocation.getValue(), name)) {
                                Location oldActiveLocation = new Location();
                                oldActiveLocation.setCityName(activeLocation.getValue());
                                oldActiveLocation.setActive(false);
                                locationDao.insertLocation(oldActiveLocation);
                            }
                        }
                        Location location = new Location();
                        location.setActive(true);
                        location.setCityName(weatherData.getName());
                        locationDao.insertLocation(location);
                        currentTemperature.setValue(weatherData.getMain());
                        activeLocation.setValue(weatherData.getName());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        stringRequest.setTag(REQUEST_TAG);

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public void setLocation(String name) {
        getCurrentTemperature(name);
    }

    public void checkIfThereIsActiveLocation() {
        Location location = locationDao.getActiveLocation();
        if (location == null) {
            activeLocation.setValue(null);
            currentTemperature.setValue(null);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }
}
