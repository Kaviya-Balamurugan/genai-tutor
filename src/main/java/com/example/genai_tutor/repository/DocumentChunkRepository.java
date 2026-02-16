package com.example.genai_tutor.repository;

import com.example.genai_tutor.model.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    @Query(value = """
        SELECT *
        FROM document_chunk
        ORDER BY embedding <-> CAST(:queryVector AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<DocumentChunk> searchSimilar(
            @Param("queryVector") String queryVector,
            @Param("limit") int limit
    );
}
