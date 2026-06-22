package org.example.weather.controller;

import org.example.weather.model.Weather;
import org.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public Iterable<Weather> findAll() {
        return weatherService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Weather> findById(@PathVariable int id) {
        return weatherService.findById(id);
    }

    @GetMapping("/by-coordinates")
    public Optional<Weather> findByCoordinates(@RequestParam Double latitude, @RequestParam Double longitude) {
        return weatherService.findByCoordinates(latitude, longitude);
    }

    @PostMapping
    public ResponseEntity<Weather> save(@RequestBody Weather weather) {
        return weatherService.existsById(weather.getId())
                ? ResponseEntity.badRequest().build()
                : new ResponseEntity<>(weatherService.save(weather), HttpStatus.CREATED);
    }
}
