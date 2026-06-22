package org.example.person.service;

import org.example.person.model.Geodata;
import org.example.person.model.Person;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Service
public class PersonService {

    private static final String LOCATION_URL = "http://localhost:8081/location?name={name}";
    private static final String WEATHER_URL = "http://localhost:8082/weather?lat={lat}&lon={lon}";

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public Iterable<Person> findAll() {
        return repository.findAll();
    }

    public Optional<Person> findByName(String name) {
        return repository.findByName(name);
    }

    public Optional<Person> create(Person person) {
        if (findByName(person.getName()).isPresent()) {
            return Optional.empty();
        }
        return Optional.of(repository.save(person));
    }

    public Optional<Person> update(String name, Person person) {
        if (findByName(name).isEmpty()) {
            return Optional.empty();
        }
        person.setName(name);
        return Optional.of(repository.save(person));
    }

    public boolean delete(String name) {
        return findByName(name)
                .map(p -> {
                    repository.delete(p);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Weather> getWeather(String name) {
        return findByName(name).flatMap(person -> {
            Geodata geodata = restTemplate.getForObject(
                    LOCATION_URL, Geodata.class, person.getLocation());
            if (geodata == null) {
                return Optional.empty();
            }
            JsonNode root = restTemplate.getForObject(
                    WEATHER_URL, JsonNode.class, geodata.getLatitude(), geodata.getLongitude());
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
