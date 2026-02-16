package com.example.genai_tutor.service;

import com.example.genai_tutor.dto.EmbeddingRequest;
import com.example.genai_tutor.dto.EmbeddingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    private static final String EMBEDDING_API =
            "http://127.0.0.1:8001/embed";

    private final RestTemplate restTemplate = new RestTemplate();

    public float[] embed(String text) {
        EmbeddingRequest request = new EmbeddingRequest(text);

        EmbeddingResponse response =
                restTemplate.postForObject(
                        EMBEDDING_API,
                        request,
                        EmbeddingResponse.class
                );

        if (response == null || response.getEmbedding() == null) {
            throw new RuntimeException("Embedding service failed");
        }

        return response.getEmbedding();
    }
}
