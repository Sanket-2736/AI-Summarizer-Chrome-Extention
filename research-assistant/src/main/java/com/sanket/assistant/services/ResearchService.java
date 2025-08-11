package com.sanket.assistant.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanket.assistant.utils.GeminiResponse;
import com.sanket.assistant.utils.ResearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    private String extractTextFromResponse(String response){
        try {
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()){
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().isEmpty()){
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }

            return "No content found!";
        } catch (Exception e) {
            return "Error in parsing: " + e.getMessage();
        }
    }

    public String processContent(ResearchRequest req) {
        //        build the prompt
        String prompt = buildPrompt(req);

        //        query api
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );
        System.out.println(requestBody);
        //        parse response
        String response = webClient.post().uri(geminiApiUrl + geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    //        return response
        return extractTextFromResponse(response);
    }

    private String buildPrompt(ResearchRequest req){
        StringBuilder prompt = new StringBuilder();
        switch (req.getOperation()){
            case "summarize" :
                prompt.append("Provide a clear and concise summary of the following text in a few sentences \n\n");
                break;

            case "suggest" :
                prompt.append("Based on the following content, suggest related topics and further reading. \n\n");
                break;

            default:
                throw new IllegalArgumentException("Unknown operation: " + req.getOperation());
        }

        prompt.append(req.getContent());
        return prompt.toString();
    }
}
