package com.example.genai_tutor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.genai_tutor.service.DocumentIngestService;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(DocumentIngestService ingestService) {
        return args -> {

            ingestService.ingest("Spring Boot makes Java development easier.");
            ingestService.ingest("Java is an object-oriented programming language.");
            ingestService.ingest("Machine learning uses data to train models.");
            ingestService.ingest("PostgreSQL supports vector similarity search.");

            System.out.println("✅ Sample documents ingested");
        };
    }
}

