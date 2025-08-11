package com.sanket.assistant.controller;

import com.sanket.assistant.services.ResearchService;
import com.sanket.assistant.utils.ResearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ResearchController {
    private final ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest request){
        String res = researchService.processContent(request);
        return ResponseEntity.ok(res);
    }
}
