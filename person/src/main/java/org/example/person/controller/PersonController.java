package org.example.person.controller;

import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Value("${location.url}")
    private String locationUrl;

    @Autowired
    private PersonService personService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        personService.findAll().forEach(users::add);
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable int id) {
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getId() != null && personService.existsById(user.getId())) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(personService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        if (personService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        return ResponseEntity.ok(personService.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return personService.findById(id)
                .map(user -> {
                    personService.delete(user);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<Weather> getWeather(@PathVariable int id) {
        return personService.findById(id)
                .map(user -> {
                    String url = String.format("http://%s/location/weather?name=%s",
                            locationUrl, user.getLocation());
                    Weather weather = restTemplate.getForObject(url, Weather.class);
                    if (weather == null) {
                        return ResponseEntity.notFound().<Weather>build();
                    }
                    return ResponseEntity.ok(weather);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
