package org.example.location.repository;

import org.example.location.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, String> {
    Optional<Location> findByName(String name);
}
