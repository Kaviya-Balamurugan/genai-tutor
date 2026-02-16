package com.example.genai_tutor.dto;

public class EmbeddingResponse {
    private float[] embedding;

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
