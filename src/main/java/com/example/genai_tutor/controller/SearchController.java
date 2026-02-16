package com.example.genai_tutor.controller;

import com.example.genai_tutor.model.DocumentChunk;
import com.example.genai_tutor.service.VectorSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final VectorSearchService searchService;

    public SearchController(VectorSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public List<DocumentChunk> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int k
    ) {
        return searchService.search(query, k);
    }
}
