package com.example.genai_tutor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "document_chunk")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "vector(384)")
    private Object embedding;

    public DocumentChunk() {}


    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Object embedding) {
        this.embedding = embedding;
    }
}
