package com.example.genai_tutor.service;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Service;

import com.example.genai_tutor.model.DocumentChunk;

@Service
public class VectorSearchService {

    private final EmbeddingService embeddingService;

    @PersistenceContext
    private EntityManager entityManager;

    public VectorSearchService(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunk> search(String question, int k) {

        // 1️⃣ Get REAL embedding (384-d)
        float[] embedding = embeddingService.embed(question);

        // 2️⃣ Convert to pgvector literal
        String vectorLiteral = toVectorLiteral(embedding);

        // 3️⃣ Native pgvector query
        String sql = """
            SELECT *
            FROM document_chunk
            ORDER BY embedding <-> CAST('%s' AS vector)
            LIMIT %d
        """.formatted(vectorLiteral, k);

        Query query = entityManager.createNativeQuery(sql, DocumentChunk.class);

        @SuppressWarnings("unchecked")
        List<DocumentChunk> results = query.getResultList();
        return results;
    }

    private String toVectorLiteral(float[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            sb.append(v[i]);
            if (i < v.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
