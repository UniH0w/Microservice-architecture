package org.example.location.controller;

import tools.jackson.databind.JsonNode;
import org.example.location.model.Location;
import org.example.location.model.Weather;
import org.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Value("${weather.url}")
    private String weatherUrl;

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) String name) {
        if (name == null) {
            List<Location> locations = new ArrayList<>();
            repository.findAll().forEach(locations::add);
            return ResponseEntity.ok(locations);
        }
        return repository.findByName(name)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location location) {
        if (repository.findByName(location.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(repository.save(location), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Location> update(@RequestParam String name, @RequestBody Location location) {
        if (repository.findByName(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        location.setName(name);
        return ResponseEntity.ok(repository.save(location));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String name) {
        return repository.findByName(name)
                .map(location -> {
                    repository.delete(location);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> getWeather(@RequestParam String name) {
        return repository.findByName(name)
                .map(location -> {
                    String url = String.format("http://%s/weather?lat=%s&lon=%s",
                            weatherUrl, location.getLatitude(), location.getLongitude());
                    JsonNode root = restTemplate.getForObject(url, JsonNode.class);
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
