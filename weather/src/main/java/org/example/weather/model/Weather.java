package org.example.weather.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Weather {
    @Id
    @GeneratedValue
    private int id;

    @NonNull private Double latitude;
    @NonNull private Double longitude;
    @NonNull private Double temperature;
    @NonNull private String description;
    @NonNull private Integer humidity;

    public Weather(@NonNull Double latitude,
                   @NonNull Double longitude,
                   @NonNull Double temperature,
                   @NonNull String description,
                   @NonNull Integer humidity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
    }
}
