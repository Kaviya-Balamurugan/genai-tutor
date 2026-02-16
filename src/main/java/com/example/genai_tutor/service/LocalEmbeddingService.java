package com.example.genai_tutor.service;

import org.springframework.stereotype.Service;

@Service
public class LocalEmbeddingService extends EmbeddingService {

    @Override
    public float[] embed(String text) {
        int size = 384;
        float[] vector = new float[size];

        if (text == null || text.isBlank()) {
            return vector;
        }

        for (int i = 0; i < text.length(); i++) {
            vector[i % size] += text.charAt(i);
        }

        // normalize
        float norm = 0f;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }

        return vector;
    }
}
