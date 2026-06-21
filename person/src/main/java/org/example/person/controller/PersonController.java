package org.example.person.controller;

import org.example.person.model.Geodata;
import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final String LOCATION_URL = "http://location/location?name={name}";
    private static final String WEATHER_URL = "http://weather/weather?lat={lat}&lon={lon}";

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable int id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getId() != null && repository.findById(user.getId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(repository.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        if (repository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(repository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return repository.findById(id)
                .map(user -> {
                    repository.delete(user);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<Weather> getWeather(@PathVariable int id) {
        return repository.findById(id)
                .map(user -> {
                    Geodata geodata = restTemplate.getForObject(
                            LOCATION_URL, Geodata.class, user.getLocation());
                    if (geodata == null) {
                        return ResponseEntity.notFound().<Weather>build();
                    }
                    JsonNode root = restTemplate.getForObject(
                            WEATHER_URL, JsonNode.class, geodata.getLatitude(), geodata.getLongitude());
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
