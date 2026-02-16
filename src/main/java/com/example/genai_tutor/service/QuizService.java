package com.example.genai_tutor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.genai_tutor.model.DocumentChunk;

@Service
public class QuizService {

    private final VectorSearchService vectorSearchService;
    private final LlamaService llamaService;

    public QuizService(VectorSearchService vectorSearchService,
                       LlamaService llamaService) {
        this.vectorSearchService = vectorSearchService;
        this.llamaService = llamaService;
    }

    public String generateQuiz(String topic) {

        List<DocumentChunk> chunks =
                vectorSearchService.search(topic, 5);

        String context = chunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n\n"));

        return llamaService.generateQuiz(context);
    }
}
