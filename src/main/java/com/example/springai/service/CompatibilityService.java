package com.example.springai.service;

import com.example.springai.dto.CompatibilityRequest;
import com.example.springai.dto.CompatibilityResponse;
import com.example.springai.dto.SajuRequest;
import com.example.springai.dto.SajuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * 궁합 분석 서비스
 */
@Service
public class CompatibilityService {

    private final SajuService sajuService;
    private final ChatClient chatClient;

    public CompatibilityService(SajuService sajuService, ChatClient.Builder chatClientBuilder) {
        this.sajuService = sajuService;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 두 사람의 궁합 분석
     */
    public CompatibilityResponse analyzeCompatibility(CompatibilityRequest request) {
        // 각 사람의 사주 계산
        SajuRequest saju1Request = new SajuRequest();
        saju1Request.setName(request.getName1());
        saju1Request.setGender(request.getGender1());
        saju1Request.setBirthDate(request.getBirthDate1());
        SajuResponse saju1 = sajuService.analyzeSaju(saju1Request);

        SajuRequest saju2Request = new SajuRequest();
        saju2Request.setName(request.getName2());
        saju2Request.setGender(request.getGender2());
        saju2Request.setBirthDate(request.getBirthDate2());
        SajuResponse saju2 = sajuService.analyzeSaju(saju2Request);

        // 궁합 타입 결정
        String compatibilityType = request.getCompatibilityType() != null
            ? request.getCompatibilityType()
            : "general";

        // AI를 통한 궁합 분석
        String interpretation = getCompatibilityInterpretation(request, saju1, saju2, compatibilityType);

        // 궁합 점수 계산 (간단한 오행 기반 계산)
        int score = calculateCompatibilityScore(saju1.getSaju(), saju2.getSaju());

        // 추천 사항 생성
        String recommendation = getRecommendation(score, compatibilityType);

        return CompatibilityResponse.builder()
            .name1(request.getName1())
            .name2(request.getName2())
            .birthDate1(request.getBirthDate1())
            .birthDate2(request.getBirthDate2())
            .saju1(saju1.getSaju())
            .saju2(saju2.getSaju())
            .compatibilityType(getCompatibilityTypeKorean(compatibilityType))
            .compatibilityScore(score)
            .interpretation(interpretation)
            .recommendation(recommendation)
            .build();
    }

    /**
     * AI를 통한 궁합 해석
     */
    private String getCompatibilityInterpretation(CompatibilityRequest request,
                                                   SajuResponse saju1,
                                                   SajuResponse saju2,
                                                   String compatibilityType) {
        String typeDescription = switch (compatibilityType) {
            case "love" -> "연애 궁합";
            case "marriage" -> "결혼 궁합";
            case "business" -> "사업 파트너 궁합";
            case "friendship" -> "우정 궁합";
            default -> "전반적인 궁합";
        };

        String systemMessage = """
            당신은 전문 사주 명리학자입니다.
            두 사람의 사주팔자를 분석하여 궁합을 상세히 설명해주세요.

            다음 항목에 대해 분석해주세요:
            1. 오행 상생상극 분석
            2. 천간 지지 조화도
            3. %s 관점에서의 궁합 분석
            4. 장점과 보완점
            5. 주의사항
            6. 구체적인 조언

            친근하고 이해하기 쉬운 한국어로 설명해주세요.
            """.formatted(typeDescription);

        String userMessage = String.format("""
            첫 번째 사람:
            이름: %s
            성별: %s
            생년월일: %s
            사주팔자: %s

            두 번째 사람:
            이름: %s
            성별: %s
            생년월일: %s
            사주팔자: %s

            궁합 타입: %s

            위 두 사람의 궁합을 분석해주세요.
            """,
            request.getName1(),
            request.getGender1().equals("male") ? "남성" : "여성",
            request.getBirthDate1(),
            saju1.getSaju(),
            request.getName2(),
            request.getGender2().equals("male") ? "남성" : "여성",
            request.getBirthDate2(),
            saju2.getSaju(),
            typeDescription
        );

        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }

    /**
     * 궁합 점수 계산 (간단한 알고리즘)
     */
    private int calculateCompatibilityScore(String saju1, String saju2) {
        // 기본 점수
        int score = 50;

        // 오행 균형 분석 (간단한 버전)
        int woodCount1 = countElement(saju1, "목");
        int fireCount1 = countElement(saju1, "화");
        int earthCount1 = countElement(saju1, "토");
        int metalCount1 = countElement(saju1, "금");
        int waterCount1 = countElement(saju1, "수");

        int woodCount2 = countElement(saju2, "목");
        int fireCount2 = countElement(saju2, "화");
        int earthCount2 = countElement(saju2, "토");
        int metalCount2 = countElement(saju2, "금");
        int waterCount2 = countElement(saju2, "수");

        // 상생 관계 체크 (목생화, 화생토, 토생금, 금생수, 수생목)
        if (woodCount1 > 0 && fireCount2 > 0) score += 10;
        if (fireCount1 > 0 && earthCount2 > 0) score += 10;
        if (earthCount1 > 0 && metalCount2 > 0) score += 10;
        if (metalCount1 > 0 && waterCount2 > 0) score += 10;
        if (waterCount1 > 0 && woodCount2 > 0) score += 10;

        // 반대 방향도 체크
        if (woodCount2 > 0 && fireCount1 > 0) score += 10;
        if (fireCount2 > 0 && earthCount1 > 0) score += 10;
        if (earthCount2 > 0 && metalCount1 > 0) score += 10;
        if (metalCount2 > 0 && waterCount1 > 0) score += 10;
        if (waterCount2 > 0 && woodCount1 > 0) score += 10;

        // 음양 균형
        int yangCount1 = countYinYang(saju1, "양");
        int yangCount2 = countYinYang(saju2, "양");
        if (Math.abs(yangCount1 - yangCount2) <= 1) {
            score += 5;
        }

        // 점수 범위 조정 (0-100)
        return Math.min(100, Math.max(0, score));
    }

    private int countElement(String saju, String element) {
        return saju.split(element, -1).length - 1;
    }

    private int countYinYang(String saju, String yinYang) {
        return saju.split(yinYang, -1).length - 1;
    }

    private String getRecommendation(int score, String compatibilityType) {
        if (score >= 80) {
            return "매우 좋은 궁합입니다! 서로를 보완하며 발전할 수 있는 관계입니다.";
        } else if (score >= 60) {
            return "좋은 궁합입니다. 서로 이해하고 노력한다면 좋은 관계를 유지할 수 있습니다.";
        } else if (score >= 40) {
            return "보통 궁합입니다. 서로의 차이를 인정하고 존중하는 것이 중요합니다.";
        } else {
            return "조금 어려운 궁합이지만, 서로를 이해하려는 노력이 있다면 좋은 관계를 만들 수 있습니다.";
        }
    }

    private String getCompatibilityTypeKorean(String type) {
        return switch (type) {
            case "love" -> "연애 궁합";
            case "marriage" -> "결혼 궁합";
            case "business" -> "사업 파트너 궁합";
            case "friendship" -> "우정 궁합";
            default -> "전반적인 궁합";
        };
    }
}
