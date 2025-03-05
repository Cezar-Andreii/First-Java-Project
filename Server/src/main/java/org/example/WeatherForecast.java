package org.example;

import java.util.Objects;

class WeatherForecast {
    String condition;
    double temperature;

    public WeatherForecast(String condition, double temperature) {
        this.condition = condition;
        this.temperature = temperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherForecast that = (WeatherForecast) o;
        return Double.compare(that.temperature, temperature) == 0 &&
                Objects.equals(condition, that.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, temperature);
    }

    @Override
    public String toString() {
        return "Condition: " + condition + ", Temperature: " + temperature + "°C";
    }

}
