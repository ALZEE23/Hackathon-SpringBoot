package com.niagapulse.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));
            
            // Masukkan .env ke urutan PERTAMA, supaya dia menimpa yang lain
            environment.getPropertySources().addFirst(new MapPropertySource("dotenv", envMap));
            
            System.out.println("✅ [DotenvInitializer] Environment variables loaded!");
        } catch (Exception e) {
            System.out.println("⚠️ [DotenvInitializer] .env not found, using system variables.");
        }
    }
}