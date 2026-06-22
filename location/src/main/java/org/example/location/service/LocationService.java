package org.example.location.service;

import org.example.location.model.Location;
import org.example.location.model.Weather;
import org.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Service
public class LocationService {

    private static final String WEATHER_URL = "http://weather/weather?lat={lat}&lon={lon}";

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public Iterable<Location> findAll() {
        return repository.findAll();
    }

    public Optional<Location> findByName(String name) {
        return repository.findByName(name);
    }

    public Optional<Location> create(Location location) {
        if (findByName(location.getName()).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(repository.save(location));
    }

    public Optional<Location> update(String name, Location location) {
        if (findByName(name).isEmpty()) {
            return Optional.empty();
        }
        location.setName(name);
        return Optional.of(repository.save(location));
    }

    public boolean delete(String name) {
        return findByName(name)
                .map(loc -> {
                    repository.delete(loc);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Weather> getWeather(String name) {
        return findByName(name).flatMap(location -> {
            JsonNode root = restTemplate.getForObject(
                    WEATHER_URL, JsonNode.class, location.getLatitude(), location.getLongitude());
            if (root == null) {
                return Optional.empty();
            }
            return Optional.of(parseWeather(root));
        });
    }

    private Weather parseWeather(JsonNode root) {
        return new Weather(
                root.get("main").get("temp").asDouble(),
                root.get("weather").get(0).get("description").asText(),
                root.get("main").get("humidity").asInt());
    }
}
