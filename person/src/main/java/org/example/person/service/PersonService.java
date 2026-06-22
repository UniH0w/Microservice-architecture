package org.example.person.service;

import org.example.person.model.User;
import org.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }
}
