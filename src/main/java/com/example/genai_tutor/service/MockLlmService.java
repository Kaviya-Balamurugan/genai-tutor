package com.example.genai_tutor.service;

import org.springframework.stereotype.Service;

@Service
public class MockLlmService implements LlmService {

    @Override
    public String generate(String prompt) {

        String text = prompt.toLowerCase();

        if (!text.contains("brain tumour") && !text.contains("brain tumor")) {
            return "I don't know.";
        }

        return "A brain tumour is an abnormal growth of cells in the brain.";
    }
}
