package com.example.genai_tutor.service;

import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentIngestService {

    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    public DocumentIngestService(JdbcTemplate jdbcTemplate,
                                 EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public void ingest(String content) {
        try {
            float[] embedding = embeddingService.embed(content);

            PGobject vector = new PGobject();
            vector.setType("vector");
            vector.setValue(toPgVector(embedding));

            jdbcTemplate.update(
                "INSERT INTO document_chunk (content, embedding) VALUES (?, ?)",
                content,
                vector
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to ingest document", e);
        }
    }

    private String toPgVector(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
