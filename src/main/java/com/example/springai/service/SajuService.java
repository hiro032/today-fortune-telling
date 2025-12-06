package com.example.springai.service;

import com.example.springai.dto.SajuRequest;
import com.example.springai.dto.SajuResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 사주팔자 분석 서비스
 */
@Service
public class SajuService {

    private final ChatClient chatClient;

    // 천간 (天干)
    private static final String[] HEAVENLY_STEMS = {
        "갑(甲)", "을(乙)", "병(丙)", "정(丁)", "무(戊)",
        "기(己)", "경(庚)", "신(辛)", "임(壬)", "계(癸)"
    };

    // 지지 (地支)
    private static final String[] EARTHLY_BRANCHES = {
        "자(子)", "축(丑)", "인(寅)", "묘(卯)", "진(辰)", "사(巳)",
        "오(午)", "미(未)", "신(申)", "유(酉)", "술(戌)", "해(亥)"
    };

    // 오행
    private static final String[] FIVE_ELEMENTS = {
        "목(木)", "목(木)", "화(火)", "화(火)", "토(土)",
        "토(土)", "금(金)", "금(金)", "수(水)", "수(水)"
    };

    // 음양
    private static final String[] YIN_YANG = {
        "양", "음", "양", "음", "양", "음", "양", "음", "양", "음"
    };

    public SajuService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 사주팔자 분석
     */
    public SajuResponse analyzeSaju(SajuRequest request) {
        LocalDateTime birthDateTime = parseBirthDateTime(request.getBirthDate(), request.getBirthTime());

        // 사주 계산
        String yearPillar = calculateYearPillar(birthDateTime);
        String monthPillar = calculateMonthPillar(birthDateTime);
        String dayPillar = calculateDayPillar(birthDateTime);
        String hourPillar = calculateHourPillar(birthDateTime);

        // 사주팔자 문자열
        String saju = String.format("%s %s %s %s", yearPillar, monthPillar, dayPillar, hourPillar);

        // AI 해석 요청
        String interpretation = getAIInterpretation(request, saju);

        return SajuResponse.builder()
            .name(request.getName())
            .birthDate(request.getBirthDate())
            .birthTime(request.getBirthTime())
            .yearPillar(yearPillar)
            .monthPillar(monthPillar)
            .dayPillar(dayPillar)
            .hourPillar(hourPillar)
            .saju(saju)
            .interpretation(interpretation)
            .build();
    }

    private LocalDateTime parseBirthDateTime(String birthDate, String birthTime) {
        LocalDate date = LocalDate.parse(birthDate);
        LocalTime time = birthTime != null && !birthTime.isEmpty()
            ? LocalTime.parse(birthTime)
            : LocalTime.of(12, 0); // 시간 미입력시 정오로 가정
        return LocalDateTime.of(date, time);
    }

    /**
     * 년주 계산
     */
    private String calculateYearPillar(LocalDateTime birthDateTime) {
        int year = birthDateTime.getYear();

        // 입춘 기준으로 년주 변경 (간단히 2월 4일로 근사)
        if (birthDateTime.getMonthValue() < 2 ||
            (birthDateTime.getMonthValue() == 2 && birthDateTime.getDayOfMonth() < 4)) {
            year--;
        }

        int stemIndex = (year - 4) % 10; // 갑자년 기준
        int branchIndex = (year - 4) % 12;

        String stem = HEAVENLY_STEMS[stemIndex];
        String branch = EARTHLY_BRANCHES[branchIndex];
        String element = FIVE_ELEMENTS[stemIndex];
        String yinYang = YIN_YANG[stemIndex];

        return String.format("%s%s(%s,%s)", stem, branch, element, yinYang);
    }

    /**
     * 월주 계산
     */
    private String calculateMonthPillar(LocalDateTime birthDateTime) {
        int month = birthDateTime.getMonthValue();
        int year = birthDateTime.getYear();

        // 월주는 년간에 따라 시작이 다름 (간단 계산)
        int yearStem = (year - 4) % 10;
        int monthStem = (yearStem * 2 + month) % 10;
        int monthBranch = (month + 1) % 12; // 인월부터 시작

        String stem = HEAVENLY_STEMS[monthStem];
        String branch = EARTHLY_BRANCHES[monthBranch];
        String element = FIVE_ELEMENTS[monthStem];
        String yinYang = YIN_YANG[monthStem];

        return String.format("%s%s(%s,%s)", stem, branch, element, yinYang);
    }

    /**
     * 일주 계산 (만세력 기준 간략 계산)
     */
    private String calculateDayPillar(LocalDateTime birthDateTime) {
        // 기준일(2000-01-01)부터의 일수 계산
        LocalDate baseDate = LocalDate.of(2000, 1, 1);
        long daysSince = java.time.temporal.ChronoUnit.DAYS.between(baseDate, birthDateTime.toLocalDate());

        // 2000-01-01은 경진일
        int baseStem = 6; // 경
        int baseBranch = 4; // 진

        int stemIndex = (int)((baseStem + daysSince) % 10);
        int branchIndex = (int)((baseBranch + daysSince) % 12);

        if (stemIndex < 0) stemIndex += 10;
        if (branchIndex < 0) branchIndex += 12;

        String stem = HEAVENLY_STEMS[stemIndex];
        String branch = EARTHLY_BRANCHES[branchIndex];
        String element = FIVE_ELEMENTS[stemIndex];
        String yinYang = YIN_YANG[stemIndex];

        return String.format("%s%s(%s,%s)", stem, branch, element, yinYang);
    }

    /**
     * 시주 계산
     */
    private String calculateHourPillar(LocalDateTime birthDateTime) {
        int hour = birthDateTime.getHour();

        // 시간을 지지로 변환 (자시: 23-01시, 축시: 01-03시, ...)
        int branchIndex = ((hour + 1) / 2) % 12;

        // 일간에 따라 시간 천간 결정 (간단 계산)
        int dayStem = Integer.parseInt(calculateDayPillar(birthDateTime).substring(0, 1)
            .replace("갑", "0").replace("을", "1").replace("병", "2")
            .replace("정", "3").replace("무", "4").replace("기", "5")
            .replace("경", "6").replace("신", "7").replace("임", "8")
            .replace("계", "9"));

        int stemIndex = (dayStem * 2 + branchIndex) % 10;

        String stem = HEAVENLY_STEMS[stemIndex];
        String branch = EARTHLY_BRANCHES[branchIndex];
        String element = FIVE_ELEMENTS[stemIndex];
        String yinYang = YIN_YANG[stemIndex];

        return String.format("%s%s(%s,%s)", stem, branch, element, yinYang);
    }

    /**
     * AI를 통한 사주 해석
     */
    private String getAIInterpretation(SajuRequest request, String saju) {
        String systemMessage = """
            당신은 전문 사주 명리학자입니다.
            사주팔자를 분석하여 다음 항목에 대해 상세히 설명해주세요:

            1. 사주 전체 분석 (오행 균형, 용신, 희신 등)
            2. 성격 및 기질
            3. 재물운
            4. 사업운/직업운
            5. 건강운
            6. 대인관계운
            7. 조언 및 주의사항

            친근하고 이해하기 쉬운 한국어로 설명해주세요.
            """;

        String userMessage = String.format("""
            이름: %s
            성별: %s
            생년월일: %s
            태어난 시간: %s

            사주팔자: %s

            위 사주팔자를 분석해주세요.
            """,
            request.getName(),
            request.getGender().equals("male") ? "남성" : "여성",
            request.getBirthDate(),
            request.getBirthTime() != null ? request.getBirthTime() : "시간 미상",
            saju
        );

        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }
}
