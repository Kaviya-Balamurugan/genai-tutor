package com.example.genai_tutor.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.genai_tutor.model.DocumentChunk;

@Service
public class RagAnswerService {

    private final VectorSearchService vectorSearchService;
    private final LlamaService llamaService;

    // 🔥 Remember last topic
    private String resolvedTopic = null;

    // 🔥 Conversation memory (last 3 messages)
    private final Deque<String> conversationMemory = new ArrayDeque<>();

    public RagAnswerService(VectorSearchService vectorSearchService,
                            LlamaService llamaService) {
        this.vectorSearchService = vectorSearchService;
        this.llamaService = llamaService;
    }

    public String answer(String question) {

        String q = question.trim();
        String qLower = q.toLowerCase();

        /* ================= GREETING ================= */

        if (qLower.matches("^(hi|hello|hey|hai)$")) {
            return "Hello 👋 How can I help you today?";
        }

        /* ================= FOLLOW-UP DETECTION ================= */

        if (isFollowUp(qLower) && resolvedTopic != null) {
            q = "Continue explaining about " + resolvedTopic + ". " + question;
        }

        /* ================= TOPIC LOCK ================= */

        if (isDefinitionQuestion(qLower)) {
            resolvedTopic = extractTopicLabel(q);
        }

        /* ================= RAG ================= */

        List<DocumentChunk> chunks = vectorSearchService.search(q, 3);

        String context = (chunks == null || chunks.isEmpty())
                ? ""
                : chunks.stream()
                        .map(DocumentChunk::getContent)
                        .collect(Collectors.joining("\n\n"));

        String response = llamaService.generateAnswer(context, q);

        // 🔥 Save to memory (max 3)
        conversationMemory.addLast("User: " + question);
        conversationMemory.addLast("AI: " + response);

        if (conversationMemory.size() > 6) {
            conversationMemory.pollFirst();
        }

        return response;
    }

    /* ================= HELPERS ================= */

    private boolean isDefinitionQuestion(String q) {
        return q.startsWith("what is ")
                || q.startsWith("define ")
                || q.startsWith("explain ");
    }

    private boolean isFollowUp(String q) {
        return q.equals("tell me more")
                || q.equals("yes")
                || q.equals("explain more")
                || q.equals("more details");
    }

    private String extractTopicLabel(String q) {
        return q
                .replaceAll("(?i)what is", "")
                .replaceAll("(?i)define", "")
                .replaceAll("(?i)explain", "")
                .replaceAll("\\?", "")
                .trim();
    }

   public void streamAnswer(String question, java.io.OutputStream outputStream) {

    String prompt = """
You are a helpful AI assistant.
Answer naturally like ChatGPT.

QUESTION:
""" + question;

    llamaService.streamAnswer(
            prompt,
            "llama3.2:1b",
            200,
            0.4,
            outputStream
    );
}

}
