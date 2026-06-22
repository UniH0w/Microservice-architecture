package org.example.person.controller;

import org.example.person.model.User;
import org.example.person.model.Weather;
import org.example.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

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
        return personService.create(user)
                .map(created -> new ResponseEntity<>(created, HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        return personService.update(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return personService.delete(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<Weather> getWeather(@PathVariable int id) {
        return personService.getWeather(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
