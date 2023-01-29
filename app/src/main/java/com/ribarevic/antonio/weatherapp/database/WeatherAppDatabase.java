package com.ribarevic.antonio.weatherapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ribarevic.antonio.weatherapp.model.Location;

@Database(entities = {Location.class}, version = 1)
public abstract class WeatherAppDatabase extends RoomDatabase {

    public abstract LocationDao locationDao();
}
