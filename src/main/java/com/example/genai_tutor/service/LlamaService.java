package com.example.genai_tutor.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class LlamaService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    // ⚡ Fast model for chat
    private static final String CHAT_MODEL = "llama3.2:1b";

    // 🧠 Stable model for structured quiz
    private static final String QUIZ_MODEL = "llama3.2:3b";

    private static final int CHAT_TOKENS = 200;
    private static final int QUIZ_TOKENS = 500;

    private static final double CHAT_TEMP = 0.4;
    private static final double QUIZ_TEMP = 0.1; // lower temp = more stable structure

    private final RestTemplate restTemplate = new RestTemplate();

    /* ================= CHAT ANSWER ================= */

    public String generateAnswer(String context, String question) {

    String prompt = """
You are a helpful and intelligent AI assistant.

Instructions:
- Answer naturally like ChatGPT.
- Adapt your response style based on the question.
- If the user asks for definition → give definition.
- If they ask for explanation → explain clearly.
- If they ask for example → provide example.
- Do NOT force definition/example unless needed.
- Choose the most common Computer Science meaning if multiple meanings exist.
- Never refuse normal academic questions.

QUESTION:
""" + question;

    String response = callOllama(prompt, CHAT_MODEL, CHAT_TOKENS, CHAT_TEMP);

    return response == null || response.isBlank()
            ? "Sorry, I couldn’t generate a response."
            : response.trim();
}


    /* ================= QUIZ ================= */

    public String generateQuiz(String topic) {

        String prompt = """
You are generating a STRICTLY FORMATTED quiz.

MANDATORY RULES (DO NOT BREAK):
- Generate EXACTLY 5 questions.
- Each question MUST have 4 options labeled A, B, C, D.
- NEVER put [CORRECT] or [EXPLANATION] inside option text.
- Put [CORRECT:X] on its OWN LINE.
- Put [EXPLANATION: short explanation] on its OWN LINE.
- Start directly with Q1.
- Do NOT add scores or summaries.
- If format is broken, regenerate internally before responding.

FORMAT:

Q1. Question?
A. Option
B. Option
C. Option
D. Option
[CORRECT:B]
[EXPLANATION: Short explanation.]

(repeat exactly 5 times)

TOPIC:
""" + topic;

        String raw = callOllama(prompt, QUIZ_MODEL, QUIZ_TOKENS, QUIZ_TEMP);

        // 🔁 Retry once automatically if invalid
        if (!isQuizValid(raw)) {
            raw = callOllama(prompt, QUIZ_MODEL, QUIZ_TOKENS, QUIZ_TEMP);
        }

        if (!isQuizValid(raw)) {
            return "⚠️ Unable to generate quiz. Please try again.";
        }

        return sanitizeQuiz(raw);
    }

    /* ================= VALIDATION ================= */

    private boolean isQuizValid(String text) {

        if (text == null) return false;

        return text.contains("Q1.")
                && text.contains("Q2.")
                && text.contains("Q3.")
                && text.contains("Q4.")
                && text.contains("Q5.")
                && text.split("\\[CORRECT:").length == 6
                && text.split("\\[EXPLANATION:").length == 6;
    }

    /* ================= SANITIZATION ================= */

    private String sanitizeQuiz(String raw) {

        if (raw == null) return "";

        // Normalize tags
        raw = raw.replaceAll("(?i)\\[correct", "[CORRECT");
        raw = raw.replaceAll("(?i)\\[explanation", "[EXPLANATION");

        // Remove [CORRECT] accidentally placed inside options
        raw = raw.replaceAll(
                "(?m)^([A-D]\\.\\s*)(\\[CORRECT:[^\\]]+\\])",
                "$1"
        );

        // Ensure quiz starts from Q1
        raw = raw.replaceAll("(?s)^.*?(Q1\\.)", "Q1.");

        return raw.trim();
    }

    /* ================= OLLAMA CALL ================= */

    private String callOllama(String prompt, String model, int tokens, double temperature) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("prompt", prompt);
            body.put("stream", false);
            body.put("options", Map.of(
                    "num_predict", tokens,
                    "temperature", temperature
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response =
                    restTemplate.postForObject(OLLAMA_URL, request, Map.class);

            return response == null || response.get("response") == null
                    ? ""
                    : response.get("response").toString().trim();

        } catch (Exception e) {
            return "";
        }
    }

    public void streamAnswer(String prompt,
                         String model,
                         int tokens,
                         double temperature,
                         java.io.OutputStream outputStream) {

    try {

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("stream", true);
        body.put("options", Map.of(
                "num_predict", tokens,
                "temperature", temperature
        ));

        restTemplate.execute(
                OLLAMA_URL,
                org.springframework.http.HttpMethod.POST,
                request -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getBody().write(
                            new com.fasterxml.jackson.databind.ObjectMapper()
                                    .writeValueAsBytes(body)
                    );
                },
                response -> {

                    java.io.BufferedReader reader =
                            new java.io.BufferedReader(
                                    new java.io.InputStreamReader(response.getBody()));

                    String line;

                    while ((line = reader.readLine()) != null) {

                        if (line.contains("\"response\"")) {

                            String chunk = line
                                    .replaceAll(".*\"response\":\"", "")
                                    .replaceAll("\".*", "");

                            outputStream.write(chunk.getBytes());
                            outputStream.flush();
                        }
                    }

                    return null;
                }
        );

    } catch (Exception e) {
        try {
            outputStream.write("❌ Error generating response.".getBytes());
            outputStream.flush();
        } catch (Exception ignored) {}
    }
}
}