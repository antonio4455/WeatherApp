package com.ribarevic.antonio.weatherapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ribarevic.antonio.weatherapp.model.Location;

import java.util.List;

@Dao
public interface LocationDao {

    @Query("SELECT * FROM Location WHERE isActive == 1 LIMIT 1")
    Location getActiveLocation();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLocation(Location location);

    @Query("SELECT * FROM Location ORDER BY cityName")
    List<Location> getLocations();

    @Delete
    void deleteLocation(Location location);
}
