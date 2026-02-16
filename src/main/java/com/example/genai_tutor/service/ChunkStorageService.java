package com.example.genai_tutor.service;

import com.example.genai_tutor.util.TextChunker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChunkStorageService {

    private final PdfService pdfService;
    private final EmbeddingService embeddingService;

    @PersistenceContext
    private EntityManager entityManager;

    public ChunkStorageService(PdfService pdfService,
                               EmbeddingService embeddingService) {
        this.pdfService = pdfService;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public int processAndStore(MultipartFile file) throws Exception {
        String text = pdfService.extractText(file);
        var chunks = TextChunker.chunk(text, 500);

        for (String chunk : chunks) {
            float[] embedding = embeddingService.embed(chunk);

            PGobject vector = new PGobject();
            vector.setType("vector");
            vector.setValue(toPgVector(embedding));

            entityManager.createNativeQuery(
                "INSERT INTO document_chunk (content, embedding) VALUES (?, ?)"
            )
            .setParameter(1, chunk)
            .setParameter(2, vector)
            .executeUpdate();
        }

        return chunks.size();
    }

    private String toPgVector(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
