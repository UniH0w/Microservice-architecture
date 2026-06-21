package org.example.weather.controller;

import org.example.weather.model.Weather;
import org.example.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherRepository repository;

    @GetMapping
    public Iterable<Weather> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Weather> findById(@PathVariable int id) {
        return repository.findById(id);
    }

    @GetMapping("/by-coordinates")
    public Optional<Weather> findByCoordinates(@RequestParam Double latitude, @RequestParam Double longitude) {
        return repository.findByLatitudeAndLongitude(latitude, longitude);
    }

    @PostMapping
    public ResponseEntity<Weather> save(@RequestBody Weather weather) {
        return repository.findById(weather.getId()).isPresent()
                ? ResponseEntity.badRequest().build()
                : new ResponseEntity<>(repository.save(weather), HttpStatus.CREATED);
    }
}
