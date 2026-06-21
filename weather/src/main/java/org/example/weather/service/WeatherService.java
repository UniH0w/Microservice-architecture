package org.example.weather.service;

import org.example.weather.model.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final String API_KEY = "53be6b675c0f5b4b7ed6ce4dd7f34fe3";
    private static final String API_URL =
            "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}&units=metric";

    @Autowired
    private RestTemplate restTemplate;

    @Cacheable(value = "weather", key = "#lat + '-' + #lon")
    public Root getWeather(double lat, double lon) {
        return restTemplate.getForObject(API_URL, Root.class, lat, lon, API_KEY);
    }
}
