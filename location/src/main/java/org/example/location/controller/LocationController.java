package org.example.location.controller;

import org.example.location.model.Location;
import org.example.location.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public Iterable<Location> findAll() {
        return locationService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Location> findById(@PathVariable int id) {
        return locationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Location> save(@RequestBody Location location) {
        return locationService.existsById(location.getId())
                ? ResponseEntity.badRequest().build()
                : new ResponseEntity<>(locationService.save(location), HttpStatus.CREATED);
    }
}
