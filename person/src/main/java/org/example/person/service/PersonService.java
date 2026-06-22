package org.example.person.service;

import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class PersonService {

    @Value("${location.url}")
    private String locationUrl;

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
            String url = String.format("http://%s/location/weather?name=%s",
                    locationUrl, user.getLocation());
            Weather weather = restTemplate.getForObject(url, Weather.class);
            return weather == null ? Optional.empty() : Optional.of(weather);
        });
    }
}
