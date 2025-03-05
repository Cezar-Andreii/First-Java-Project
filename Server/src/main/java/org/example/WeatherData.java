package org.example;

import java.util.Objects;
import java.util.*;

class WeatherData {
    String location;
    double latitude;
    double longitude;
    List<WeatherForecast> forecast;

    public WeatherData(String location, double latitude, double longitude, List<WeatherForecast> forecast) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.forecast = forecast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(location, that.location) &&
                Objects.equals(forecast, that.forecast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, latitude, longitude, forecast);
    }
}
