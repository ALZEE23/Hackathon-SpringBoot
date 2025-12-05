package com.niagapulse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niagapulse.dto.WeatherResponse;
import com.niagapulse.model.VendorLocationLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.StringUtils;

import org.json.JSONObject;
import java.util.Map;

@Service
public class IntelligenceService {

    private final Logger log = LoggerFactory.getLogger(IntelligenceService.class);

    @Value("${niagapulse.weather.key:}")
    private String weatherApiKey;

    @Value("${niagapulse.ai.key:}")
    private String aiApiKey;

    @Value("${niagapulse.ai.model:Claude Sonnet 4.5}")
    public String aiModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
    private final String GEOCODE_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s";
    private final String AI_ENDPOINT;

    public IntelligenceService(RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               @Value("${niagapulse.ai.endpoint:https://api.openai.com/v1/chat/completions}") 
                               String aiEndpoint) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.AI_ENDPOINT = aiEndpoint;
    }

    // =====================================================
    // GET WEATHER
    // =====================================================
    public String getWeather(Double lat, Double lon) {
        if (lat == null || lon == null) {
            log.warn("getWeather called with null coordinates");
            return "Unknown";
        }
        if (!StringUtils.hasText(weatherApiKey)) {
            log.warn("Weather API key not configured (niagapulse.weather.key)");
            return "Unknown";
        }

        try {
            String url = String.format(WEATHER_URL, lat, lon, weatherApiKey);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("OpenWeather returned non-2xx: {}", response.getStatusCode());
                return "Unknown";
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode weatherNode = root.path("weather");

            if (weatherNode.isArray() && weatherNode.size() > 0) {
                String main = weatherNode.get(0).path("main").asText("");
                String desc = weatherNode.get(0).path("description").asText("");
                return (main + (desc.isEmpty() ? "" : " - " + desc)).trim();
            }

        } catch (Exception e) {
            log.error("Failed to fetch weather: {}", e.getMessage());
        }

        return "Unknown";
    }

    // =====================================================
    // GET AI RECOMMENDATION (VERSI FULL)
    // =====================================================
    public String getRecommendation(VendorLocationLog currentLog, String historisPenjualan) {

        if (currentLog == null || currentLog.getLocation() == null) {
            return "Tidak ada rekomendasi (lokasi tidak tersedia)";
        }

        Double lat = currentLog.getLocation().getY();
        Double lon = currentLog.getLocation().getX();
        String cuaca = currentLog.getWeatherCondition();
        String area = getAddressName(lat, lon);

        String prompt = String.format(
                "Saya pedagang keliling di area %s. Lokasi: (%.4f, %.4f). Cuaca: %s. Riwayat penjualan: %s. " +
                "Berikan 1 kalimat (maks 15 kata) kemana saya harus pindah.",
                area, lat, lon,
                cuaca == null ? "Tidak diketahui" : cuaca,
                historisPenjualan == null ? "-" : historisPenjualan
        );

        return callAI(prompt);
    }

    // =====================================================
    // GET AI RECOMMENDATION (VERSI SIMPLE)
    // =====================================================
    public String getRecommendation(String area, String cuaca, String historySales) {

        String prompt = String.format(
                "Saya pedagang keliling di area %s. Cuaca: %s. Data penjualan: %s. " +
                "Berikan 1 kalimat saran rute terbaik.",
                area, cuaca, historySales
        );

        return callAI(prompt);
    }


    // =====================================================
    // AI CALLER — SINGLE PLACE
    // =====================================================
    private String callAI(String prompt) {
    if (!StringUtils.hasText(aiApiKey)) {
        return "AI tidak tersedia (API key tidak ditemukan)";
    }

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiApiKey);

        // Body sesuai Kolosal AI
        Map<String, Object> body = Map.of(
            "model", aiModel,  // contoh: "Claude Sonnet 4.5"
            "messages", new Object[]{
                Map.of("role", "system", "content", "Kamu adalah AI pembantu strategi UMKM."),
                Map.of("role", "user", "content", prompt)
            }
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(AI_ENDPOINT, entity, String.class);

        // Parse response (format Kolosal AI)
        JSONObject json = new JSONObject(resp.getBody());

        return json
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim();

    } catch (Exception e) {
        log.error("AI error: {}", e.getMessage());
        return "AI sedang sibuk. Coba lagi nanti.";
    }
}


    // =====================================================
    // REVERSE GEOCODING
    // =====================================================
    public String getAddressName(Double lat, Double lon) {
        if (lat == null || lon == null) return "Area Tidak Dikenal";

        try {
            String url = String.format(GEOCODE_URL, lat, lon);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "NiagaPulse-App");

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.has("display_name")) return root.get("display_name").asText();
            }

        } catch (Exception e) {
            log.error("Geocode error: {}", e.getMessage());
        }

        return "Area Tidak Dikenal";
    }
}
