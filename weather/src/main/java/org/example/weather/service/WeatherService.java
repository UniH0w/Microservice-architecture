package org.example.weather.service;

import org.example.weather.model.Weather;
import org.example.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository repository;

    public Iterable<Weather> findAll() {
        return repository.findAll();
    }

    public Optional<Weather> findById(int id) {
        return repository.findById(id);
    }

    public Optional<Weather> findByCoordinates(Double latitude, Double longitude) {
        return repository.findByLatitudeAndLongitude(latitude, longitude);
    }

    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    public Weather save(Weather weather) {
        return repository.save(weather);
    }
}
