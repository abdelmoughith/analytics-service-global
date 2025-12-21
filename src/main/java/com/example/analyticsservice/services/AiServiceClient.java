package com.example.analyticsservice.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AiServiceClient {

    private final WebClient webClient;

    public AiServiceClient(WebClient.Builder builder,
                           @Value("${ai-service.url}") String aiServiceUrl) {
        this.webClient = builder.baseUrl(aiServiceUrl).build();
    }

    // Predict endpoint
    public Mono<PredictResponse> predictStudent(PredictRequest request) {
        return webClient.post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(body))))
                .bodyToMono(PredictResponse.class);
    }

    // Recommendations endpoint
    public Mono<RecommendationResponse> getRecommendations(int studentId, String moduleCode) {
        return webClient.get()
                .uri("/reco/{studentId}/{moduleCode}", studentId, moduleCode)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(body))))
                .bodyToMono(RecommendationResponse.class);
    }

    // DTO classes (copy from your existing ones)
    public static class PredictRequest {
        public int student_id;
        public String module_code;
    }

    public static class PredictResponse {
        public int student_id;
        public String module_code;
        public double success_proba;
        public String risk_level;
        public String message;
    }

    public static class RecommendationResponse {
        public int student_id;
        public String module_code;
        public List<Recommendation> recommendations;

        public static class Recommendation {
            public String resource_id;
            public String title;
            public String url;
            public String type;
            public String reason;
        }
    }
}
