package org.example.person.service;

import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class PersonService {

    private static final String LOCATION_WEATHER_URL = "http://location/location/weather?name={name}";

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
            Weather weather = restTemplate.getForObject(
                    LOCATION_WEATHER_URL, Weather.class, user.getLocation());
            return weather == null ? Optional.empty() : Optional.of(weather);
        });
    }
}
