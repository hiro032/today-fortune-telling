package com.example.springai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.time.LocalDate;
import java.util.function.Function;

/**
 * Fortune telling function
 */
public class FortuneFunction implements Function<FortuneFunction.Request, FortuneFunction.Response> {

    public record Request(
        @JsonProperty(required = true, value = "name")
        @JsonPropertyDescription("사용자의 이름")
        String name,

        @JsonProperty(required = true, value = "gender")
        @JsonPropertyDescription("성별: male 또는 female")
        String gender,

        @JsonProperty(required = true, value = "birthDate")
        @JsonPropertyDescription("생년월일 (YYYY-MM-DD 형식)")
        String birthDate
    ) {}

    public record Response(
        String name,
        String gender,
        String birthDate,
        String zodiacSign,
        String luckyNumber,
        String luckyColor,
        String fortuneSummary
    ) {}

    @Override
    public Response apply(Request request) {
        // 별자리 계산
        String zodiacSign = calculateZodiacSign(request.birthDate());

        // 행운의 숫자 (간단한 로직)
        int luckyNum = (request.name().length() + request.birthDate().hashCode()) % 100;
        String luckyNumber = String.valueOf(Math.abs(luckyNum));

        // 행운의 색상
        String[] colors = {"빨강", "파랑", "노랑", "초록", "보라", "주황", "분홍", "하늘색"};
        String luckyColor = colors[Math.abs(request.name().hashCode()) % colors.length];

        // 오늘의 운세 요약 (AI가 이를 기반으로 상세하게 풀어서 설명할 것)
        String fortuneSummary = String.format(
            "%s님의 별자리는 %s입니다. 오늘의 행운의 숫자는 %s, 행운의 색상은 %s입니다.",
            request.name(), zodiacSign, luckyNumber, luckyColor
        );

        return new Response(
            request.name(),
            request.gender(),
            request.birthDate(),
            zodiacSign,
            luckyNumber,
            luckyColor,
            fortuneSummary
        );
    }

    private String calculateZodiacSign(String birthDate) {
        try {
            LocalDate date = LocalDate.parse(birthDate);
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();

            if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "양자리";
            if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "황소자리";
            if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) return "쌍둥이자리";
            if ((month == 6 && day >= 22) || (month == 7 && day <= 22)) return "게자리";
            if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "사자자리";
            if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "처녀자리";
            if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "천칭자리";
            if ((month == 10 && day >= 23) || (month == 11 && day <= 22)) return "전갈자리";
            if ((month == 11 && day >= 23) || (month == 12 && day <= 21)) return "사수자리";
            if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) return "염소자리";
            if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "물병자리";
            if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "물고기자리";
        } catch (Exception e) {
            return "알 수 없음";
        }
        return "알 수 없음";
    }
}
