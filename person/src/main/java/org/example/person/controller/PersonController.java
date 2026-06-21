package org.example.person.controller;

import tools.jackson.databind.JsonNode;
import org.example.person.model.Geodata;
import org.example.person.model.Person;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final String LOCATION_URL = "http://localhost:8081/location?name={name}";
    private static final String WEATHER_URL = "http://localhost:8082/weather?lat={lat}&lon={lon}";

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) String name) {
        if (name == null) {
            List<Person> persons = new ArrayList<>();
            repository.findAll().forEach(persons::add);
            return ResponseEntity.ok(persons);
        }
        return repository.findByName(name)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        if (repository.findByName(person.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(repository.save(person), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Person> update(@RequestParam String name, @RequestBody Person person) {
        if (repository.findByName(name).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        person.setName(name);
        return ResponseEntity.ok(repository.save(person));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String name) {
        return repository.findByName(name)
                .map(person -> {
                    repository.delete(person);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> getWeather(@RequestParam String name) {
        return repository.findByName(name)
                .map(person -> {
                    Geodata geodata = restTemplate.getForObject(
                            LOCATION_URL, Geodata.class, person.getLocation());
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
