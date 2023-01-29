package com.ribarevic.antonio.weatherapp.model;

public class WeatherData {
    private Temperature main;
    private String name;

    public Temperature getMain() {
        return main;
    }

    public void setMain(Temperature main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

