package com.example.miniproj.interview.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class OpenAiService {

    @Value("${OPEN_AI_KEY}") // .env 파일에서 API 키를 가져옵니다.
    private String apiKey;

    private final WebClient webClient;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }

    // 기능: 자소서를 주면 -> 예상 질문 5개를 리스트로 반환
    public List<String> generateQuestions(String jobCategory, String content) {
        
        // 1. 프롬프트 생성 (질문 5개만 딱 달라고 요청)
        String prompt = "직무: " + jobCategory + "\n" +
                        "자소서 내용: " + content + "\n\n" +
                        "위 내용을 바탕으로 면접관이 물어볼 날카로운 예상 질문 5가지를 한국어로 만들어줘. " +
                        "번호나 서론 없이 오직 질문 문장 5개만 줄바꿈으로 구분해서 출력해.";

        // 2. 요청 데이터 만들기 (JSON)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "${OPEN_AI_MODEL}"); // 모델 설정
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful interviewer."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7); // 창의성 조절

        // 3. API 호출 및 응답 받기
        String response = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 결과가 올 때까지 기다림 (동기 처리)

        // 4. 응답 파싱 (JSON -> List<String>)
        return parseResponse(response);
    }

    // 기능 2: 질문과 답변을 받아서 피드백 생성
    public String generateFeedback(String question, String answer) {
        
        // 1. 프롬프트 구성 (면접관 페르소나)
        String prompt = String.format(
                "면접 질문: \"%s\"\n" +
                "지원자 답변: \"%s\"\n\n" +
                "위 답변에 대해 면접관으로서 구체적인 피드백을 해줘. " +
                "잘한 점, 부족한 점, 그리고 보완할 수 있는 예시 답변을 포함해서 500자 이내로 한국어로 작성해줘.",
                question, answer
        );

        // 2. 요청 데이터 만들기
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a professional HR interviewer."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        // 3. API 호출
        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 4. 응답 파싱 (단일 문자열 반환)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "피드백 생성 중 오류가 발생했습니다.";
        }
    }

    private List<String> parseResponse(String jsonResponse) {
        List<String> questions = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            
            // 응답 내용 추출
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // 줄바꿈 기준으로 잘라서 리스트에 담기
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    questions.add(line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            questions.add("질문 생성 중 오류가 발생했습니다.");
        }
        return questions;
    }
}