package com.example.analyticsservice.controllers;


import com.example.analyticsservice.services.AiServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ai")
public class AnalyticsController {

    private final AiServiceClient aiServiceClient;

    public AnalyticsController(AiServiceClient aiServiceClient) {
        this.aiServiceClient = aiServiceClient;
    }

    @PostMapping("/predict")
    public Mono<AiServiceClient.PredictResponse> predict(@RequestBody AiServiceClient.PredictRequest request) {
        return aiServiceClient.predictStudent(request)
                .onErrorResume(ex -> {
                    // Propagate FastAPI error properly
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage()));
                });
    }

    @GetMapping("/reco/{studentId}/{moduleCode}")
    public Mono<AiServiceClient.RecommendationResponse> getRecommendations(
            @PathVariable int studentId,
            @PathVariable String moduleCode) {
        return aiServiceClient.getRecommendations(studentId, moduleCode)
                .onErrorResume(ex -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("UP");
    }
}
