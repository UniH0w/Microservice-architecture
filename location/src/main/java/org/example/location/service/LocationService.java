package org.example.location.service;

import org.example.location.model.Location;
import org.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    public Iterable<Location> findAll() {
        return repository.findAll();
    }

    public Optional<Location> findByName(String name) {
        return repository.findByName(name);
    }

    public Location save(Location location) {
        return repository.save(location);
    }

    public void delete(Location location) {
        repository.delete(location);
    }
}
