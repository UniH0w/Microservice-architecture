package org.example.person.service;

import org.example.person.model.Person;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public Iterable<Person> findAll() {
        return repository.findAll();
    }

    public Optional<Person> findByName(String name) {
        return repository.findByName(name);
    }

    public Person save(Person person) {
        return repository.save(person);
    }

    public void delete(Person person) {
        repository.delete(person);
    }
}
