package org.example.location.controller;

import tools.jackson.databind.JsonNode;
import org.example.location.model.Location;
import org.example.location.model.Weather;
import org.example.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    private static final String WEATHER_URL = "http://localhost:8082/weather?lat={lat}&lon={lon}";

    @Autowired
    private LocationService locationService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) String name) {
        if (name == null) {
            List<Location> locations = new ArrayList<>();
            locationService.findAll().forEach(locations::add);
            return ResponseEntity.ok(locations);
        }
        return locationService.findByName(name)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {
        if (locationService.findByName(location.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(locationService.save(location), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Location> update(@RequestParam String name, @RequestBody Location location) {
        if (locationService.findByName(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        location.setName(name);
        return ResponseEntity.ok(locationService.save(location));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String name) {
        return locationService.findByName(name)
                .map(location -> {
                    locationService.delete(location);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> getWeather(@RequestParam String name) {
        return locationService.findByName(name)
                .map(location -> {
                    JsonNode root = restTemplate.getForObject(
                            WEATHER_URL, JsonNode.class, location.getLatitude(), location.getLongitude());
                    if (root == null) {
                        return ResponseEntity.notFound().<Weather>build();
                    }
                    Weather weather = new Weather(
                            root.get("main").get("temp").asDouble(),
                            root.get("weather").get(0).get("description").asText(),
                            root.get("main").get("humidity").asInt());
                    return ResponseEntity.ok(weather);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
