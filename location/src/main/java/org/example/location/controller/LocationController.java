package org.example.location.controller;

import org.example.location.model.Location;
import org.example.location.model.Weather;
import org.example.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

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
        return locationService.create(location)
                .map(created -> new ResponseEntity<>(created, HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping
    public ResponseEntity<Location> update(@RequestParam String name, @RequestBody Location location) {
        return locationService.update(name, location)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String name) {
        return locationService.delete(name)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> getWeather(@RequestParam String name) {
        return locationService.getWeather(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
