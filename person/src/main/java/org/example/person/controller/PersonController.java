package org.example.person.controller;

import org.example.person.model.Person;
import org.example.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public Iterable<Person> findAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Person> findById(@PathVariable int id) {
        return personService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Person> save(@RequestBody Person person) {
        return personService.existsById(person.getId())
                ? new ResponseEntity(personService.findById(person.getId()), HttpStatus.BAD_REQUEST)
                : new ResponseEntity(personService.save(person), HttpStatus.CREATED);
    }
}
