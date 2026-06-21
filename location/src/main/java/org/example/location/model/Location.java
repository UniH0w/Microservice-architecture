package org.example.location.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Location {
    @Id
    @GeneratedValue
    private int id;

    @NonNull private String city;
    @NonNull private Double latitude;
    @NonNull private Double longitude;

    public Location(@NonNull String city, @NonNull Double latitude, @NonNull Double longitude) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
