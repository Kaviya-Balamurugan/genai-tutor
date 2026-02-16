package com.example.genai_tutor.controller;

import com.example.genai_tutor.service.ChunkStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    private final ChunkStorageService storageService;

    public UploadController(ChunkStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        int count = storageService.processAndStore(file);
        return "Stored " + count + " chunks into database";
    }
}
