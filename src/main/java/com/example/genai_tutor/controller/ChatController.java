package com.example.genai_tutor.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.genai_tutor.service.RagAnswerService;

import jakarta.servlet.http.HttpServletResponse; // ✅ IMPORTANT

@RestController
public class ChatController {

    private final RagAnswerService ragAnswerService;

    public ChatController(RagAnswerService ragAnswerService) {
        this.ragAnswerService = ragAnswerService;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public void chat(@RequestParam String question,
                     HttpServletResponse response) throws Exception {

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        java.io.OutputStream outputStream = response.getOutputStream();

        ragAnswerService.streamAnswer(question, outputStream);

        outputStream.flush();
    }
}
