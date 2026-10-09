package io.tinylink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TinylinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinylinkApplication.class, args);
    }
}
