package com.niagapulse.service;

import com.niagapulse.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IntelligenceService {

    @Value("${OPENWEATHER_KEY}")
    private String weatherApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={appid}";

    public String getWeather(Double lat, Double lon) {
        try {
            String url = String.format(weatherApiUrl, lat, lon, weatherApiKey);

            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

            if (response != null && response.getWeather().isEmpty()) {
                return response.getWeather().get(0).getMain();
            }
        } catch (Exception e) {
            System.out.println("Gagal cek cuaca: " + e.getMessage());
        }
        return "Unknown";
    }
}
