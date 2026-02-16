package com.example.genai_tutor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.genai_tutor.service.RagAnswerService;

@RestController
public class QuizController {

    private final RagAnswerService ragAnswerService;

    public QuizController(RagAnswerService ragAnswerService) {
        this.ragAnswerService = ragAnswerService;
    }

    @GetMapping("/quiz")
    public String generateQuiz() {
        // 🔥 DO NOT accept topic from frontend
        // 🔥 Always generate quiz from LAST topic
        return ragAnswerService.answer("generate quiz");
    }
}
