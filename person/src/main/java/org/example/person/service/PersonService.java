package org.example.person.service;

import org.example.person.model.Geodata;
import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

@Service
public class PersonService {

    private static final String LOCATION_URL = "http://location/location?name={name}";
    private static final String WEATHER_URL = "http://weather/weather?lat={lat}&lon={lon}";

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    public Optional<User> create(User user) {
        if (user.getId() != null && existsById(user.getId())) {
            return Optional.empty();
        }
        return Optional.of(repository.save(user));
    }

    public Optional<User> update(int id, User user) {
        if (findById(id).isEmpty()) {
            return Optional.empty();
        }
        user.setId(id);
        return Optional.of(repository.save(user));
    }

    public boolean delete(int id) {
        return findById(id)
                .map(user -> {
                    repository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Weather> getWeather(int id) {
        return findById(id).flatMap(user -> {
            Geodata geodata = restTemplate.getForObject(
                    LOCATION_URL, Geodata.class, user.getLocation());
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
