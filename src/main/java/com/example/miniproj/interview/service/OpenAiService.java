package com.example.miniproj.interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class OpenAiService {

    @Value("${OPEN_AI_KEY}")
    private String apiKey;
    @Value("${OPEN_AI_MODEL}")
    private String model;
    @Value("${OPEN_AI_URL}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // [기능 1] 예상 질문 생성 (3회 재시도 로직 포함)
    // public List<String> generateQuestions(
    //     String jobCategory,
    //     String content) {
    //     String prompt = "직무: " + jobCategory + "\n자소서: " + content + 
    //                     "\n\n위 내용을 바탕으로 면접 질문 5개만 줄바꿈으로 구분해서 출력해." +
    //                     "\n반드시 질문 5개만 생성하고, 번호나 서두 없이 질문만 출력해.";

    //     for (int i = 1; i <= 3; i++) {
    //         try {
    //             // 1. API 호출 데이터 설정 (Map.put 사용)
    //             Map<String, Object> body = new HashMap<>();
    //             body.put("model", model);
    //             body.put("messages", List.of(
    //                     Map.of("role", "system", "content", "You are a helpful interviewer."),
    //                     Map.of("role", "user", "content", prompt)
    //             ));

    //             // 2. WebClient 호출
    //             String jsonResponse = webClientBuilder.build().post()
    //                     .uri(apiUrl + "/chat/completions")
    //                     .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .bodyValue(body)
    //                     .retrieve()
    //                     .bodyToMono(String.class)
    //                     .block();

    //             // 3. 응답 파싱
    //             JsonNode root = objectMapper.readTree(jsonResponse);
    //             String resultText = root.path("choices").get(0).path("message").path("content").asText();
                
    //             // return Arrays.asList(resultText.split("\n"));
    //             // 공백만 있는 줄은 버리고, 각 줄의 앞뒤 공백을 제거한 뒤 리스트로 만듭니다.
    //             return Arrays.stream(resultText.split("\n"))
    //                         .map(String::trim)          // 앞뒤 공백 제거
    //                         .filter(q -> !q.isEmpty())  // 빈 줄 제거
    //                         .toList();
                            
    //         } catch (Exception e) {
    //             System.err.println("질문 생성 실패 시도 " + i + ": " + e.getMessage());
    //             if (i == 3) break;
    //             try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    //         }
    //     }
    //     return List.of("질문 생성 실패");
    // }
public List<String> generateQuestions(String jobCategory, String content) {
    String prompt = "직무: " + jobCategory + "\n자소서: " + content + 
                    "\n\n위 내용을 바탕으로 면접 질문 5개만 줄바꿈으로 구분해서 출력해." +
                    "\n반드시 질문 5개만 생성하고, 번호나 서두 없이 질문만 출력해.";

    for (int i = 1; i <= 3; i++) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", "You are a helpful interviewer."),
                    Map.of("role", "user", "content", prompt)
            ));

            // 1. WebClient 호출 및 상세 에러 로깅
            String jsonResponse = webClientBuilder.build().post()
                    .uri(apiUrl + "/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> 
                        response.bodyToMono(String.class).flatMap(errorBody -> {
                            // API 서버에서 보낸 에러 메시지 확인용
                            System.err.println("API 에러 응답 코드: " + response.statusCode());
                            System.err.println("API 에러 메시지: " + errorBody);
                            return Mono.error(new RuntimeException("API 호출 실패: " + response.statusCode()));
                        })
                    )
                    .bodyToMono(String.class)
                    .block();

            // 2. 응답 데이터 로그 출력 (파싱 전 확인)
            // System.out.println("Raw JSON Response: " + jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);
            
            // 3. 응답 구조 검증 로깅
            JsonNode choices = root.path("choices");
            if (choices.isMissingNode() || !choices.isArray() || choices.isEmpty()) {
                System.err.println("응답 JSON 구조가 예상과 다릅니다: " + jsonResponse);
                throw new RuntimeException("Unexpected JSON structure");
            }

            String resultText = choices.get(0).path("message").path("content").asText();
            
            if (resultText == null || resultText.isBlank()) {
                System.err.println("GPT로부터 빈 답변을 받았습니다.");
                throw new RuntimeException("Empty content from GPT");
            }

            return Arrays.stream(resultText.split("\n"))
                    .map(String::trim)
                    .filter(q -> !q.isEmpty())
                    .toList();

        } catch (Exception e) {
            // 4. 상세 예외 로그 (스택 트레이스 포함)
            System.err.println("질문 생성 실패 시도 " + i + " 번째");
            e.printStackTrace(); // 어떤 라인에서 어떤 에러가 났는지 상세히 출력

            if (i == 3) break;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }
    return List.of("질문 생성 실패 - 로그를 확인하세요.");
}
    // [기능 2] 피드백 생성 (3회 재시도 로직 포함)
    public String generateFeedback(String question, String answer, String jobCategory, String resumeContent) {
        String prompt = String.format(
                "직무: %s\n자소서: %s\n\n질문: %s\n답변: %s\n\n위 답변을 면접관 입장에서 피드백해줘. 500자 이내 한국어.",
                jobCategory, resumeContent, question, answer);

        for (int i = 1; i <= 3; i++) {
            try {
                // 1. API 호출 데이터 설정
                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("messages", List.of(
                        Map.of("role", "system", "content", "You are a professional HR interviewer."),
                        Map.of("role", "user", "content", prompt)
                ));

                // 2. WebClient 호출
                // String jsonResponse = webClientBuilder.build().post()
                //         .uri(apiUrl + "/chat/completions")
                //         .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                //         .contentType(MediaType.APPLICATION_JSON)
                //         .bodyValue(body)
                //         .retrieve()
                //         .bodyToMono(String.class)
                //         .block();
                // WebClient 빌드 시 또는 호출 시 인코딩 설정을 명시적으로 추가
                String jsonResponse = webClientBuilder.build().post()
                        .uri(apiUrl + "/chat/completions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .acceptCharset(StandardCharsets.UTF_8) // 추가: UTF-8 응답 선호
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                // 3. 응답 파싱
                JsonNode root = objectMapper.readTree(jsonResponse);
                return root.path("choices").get(0).path("message").path("content").asText();

            } catch (Exception e) {
                System.err.println("피드백 생성 실패 시도 " + i + ": " + e.getMessage());
                if (i == 3) break;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        return "피드백 생성 실패";
    }
}