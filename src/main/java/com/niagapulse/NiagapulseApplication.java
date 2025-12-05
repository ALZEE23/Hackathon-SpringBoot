package com.niagapulse;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.niagapulse")
public class NiagapulseApplication {

    public static void main(String[] args) {
        // Use the ApplicationContext initializer to load .env into Spring Environment
        new SpringApplicationBuilder(NiagapulseApplication.class)
                .initializers(new com.niagapulse.config.DotenvInitializer())
                .run(args);
    }

}
