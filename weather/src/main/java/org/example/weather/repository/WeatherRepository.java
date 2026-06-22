package org.example.weather.repository;

import org.example.weather.model.Weather;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends CrudRepository<Weather, Integer> {
    Optional<Weather> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
