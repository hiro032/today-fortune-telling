package com.example.springai.util;

import java.util.*;

/**
 * 순수 로직 기반 사주팔자 분석 유틸리티
 */
public class SajuAnalyzer {

    // 십성(十星) - 일간과 다른 천간의 관계
    private static final String[][] TEN_GODS = {
        // 갑, 을, 병, 정, 무, 기, 경, 신, 임, 계
        {"비견", "겁재", "식신", "상관", "편재", "정재", "편관", "정관", "편인", "정인"}, // 갑일간
        {"겁재", "비견", "상관", "식신", "정재", "편재", "정관", "편관", "정인", "편인"}, // 을일간
        {"비견", "겁재", "식신", "상관", "편재", "정재", "편관", "정관", "편인", "정인"}, // 병일간
        {"겁재", "비견", "상관", "식신", "정재", "편재", "정관", "편관", "정인", "편인"}, // 정일간
        {"비견", "겁재", "식신", "상관", "편재", "정재", "편관", "정관", "편인", "정인"}, // 무일간
        {"겁재", "비견", "상관", "식신", "정재", "편재", "정관", "편관", "정인", "편인"}, // 기일간
        {"비견", "겁재", "식신", "상관", "편재", "정재", "편관", "정관", "편인", "정인"}, // 경일간
        {"겁재", "비견", "상관", "식신", "정재", "편재", "정관", "편관", "정인", "편인"}, // 신일간
        {"비견", "겁재", "식신", "상관", "편재", "정재", "편관", "정관", "편인", "정인"}, // 임일간
        {"겁재", "비견", "상관", "식신", "정재", "편재", "정관", "편관", "정인", "편인"}  // 계일간
    };

    // 오행 속성
    private static final Map<String, String> ELEMENT_MAP = Map.of(
        "갑", "목", "을", "목",
        "병", "화", "정", "화",
        "무", "토", "기", "토",
        "경", "금", "신", "금",
        "임", "수", "계", "수"
    );

    /**
     * 사주 분석 메인 메서드
     */
    public static String analyzeSaju(String yearPillar, String monthPillar,
                                     String dayPillar, String hourPillar,
                                     String gender) {
        StringBuilder analysis = new StringBuilder();

        // 일간 추출 (첫 글자)
        String dayStem = extractStem(dayPillar);
        int dayStemIndex = getStemIndex(dayStem);

        // 사주의 모든 천간 추출
        List<String> stems = Arrays.asList(
            extractStem(yearPillar),
            extractStem(monthPillar),
            extractStem(dayPillar),
            extractStem(hourPillar)
        );

        // 오행 분석
        Map<String, Integer> elementCount = analyzeElements(stems);

        analysis.append("【 오행 분석 】\n");
        analysis.append(getElementAnalysis(elementCount, dayStem)).append("\n\n");

        // 십성 분석
        analysis.append("【 십성 분석 】\n");
        analysis.append(getTenGodsAnalysis(dayStemIndex, stems)).append("\n\n");

        // 성격 및 기질
        analysis.append("【 성격 및 기질 】\n");
        analysis.append(getPersonalityAnalysis(dayStem, elementCount)).append("\n\n");

        // 재물운
        analysis.append("【 재물운 】\n");
        analysis.append(getWealthAnalysis(dayStemIndex, stems, elementCount)).append("\n\n");

        // 사업운/직업운
        analysis.append("【 사업운 및 직업운 】\n");
        analysis.append(getCareerAnalysis(dayStem, elementCount, stems)).append("\n\n");

        // 대인관계운
        analysis.append("【 대인관계운 】\n");
        analysis.append(getRelationshipAnalysis(dayStemIndex, stems)).append("\n\n");

        // 조언
        analysis.append("【 조언 및 주의사항 】\n");
        analysis.append(getAdvice(elementCount, dayStem)).append("\n");

        return analysis.toString();
    }

    private static String extractStem(String pillar) {
        return pillar.substring(0, 1);
    }

    private static int getStemIndex(String stem) {
        String[] stems = {"갑", "을", "병", "정", "무", "기", "경", "신", "임", "계"};
        for (int i = 0; i < stems.length; i++) {
            if (stems[i].equals(stem)) return i;
        }
        return 0;
    }

    private static Map<String, Integer> analyzeElements(List<String> stems) {
        Map<String, Integer> count = new HashMap<>();
        count.put("목", 0);
        count.put("화", 0);
        count.put("토", 0);
        count.put("금", 0);
        count.put("수", 0);

        for (String stem : stems) {
            String element = ELEMENT_MAP.get(stem);
            count.put(element, count.get(element) + 1);
        }
        return count;
    }

    private static String getElementAnalysis(Map<String, Integer> elementCount, String dayStem) {
        StringBuilder sb = new StringBuilder();
        String dayElement = ELEMENT_MAP.get(dayStem);

        sb.append(String.format("일간: %s(%s)\n", dayStem, dayElement));
        sb.append(String.format("목:%d개, 화:%d개, 토:%d개, 금:%d개, 수:%d개\n\n",
            elementCount.get("목"), elementCount.get("화"), elementCount.get("토"),
            elementCount.get("금"), elementCount.get("수")));

        // 오행 균형 판단
        int max = Collections.max(elementCount.values());
        int min = Collections.min(elementCount.values());

        if (max - min > 2) {
            sb.append("오행의 균형이 다소 치우쳐 있습니다. ");
            String strongElement = elementCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
            sb.append(String.format("%s 기운이 강한 사주입니다.\n", strongElement));
        } else {
            sb.append("오행의 균형이 비교적 잘 맞는 사주입니다.\n");
        }

        // 용신 추천
        String weakElement = elementCount.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .get().getKey();
        sb.append(String.format("부족한 %s 기운을 보충하면 좋습니다.", weakElement));

        return sb.toString();
    }

    private static String getTenGodsAnalysis(int dayStemIndex, List<String> stems) {
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> tenGodsCount = new HashMap<>();

        for (int i = 0; i < stems.size(); i++) {
            if (i == 2) continue; // 일간 제외

            int stemIndex = getStemIndex(stems.get(i));
            String tenGod = TEN_GODS[dayStemIndex][stemIndex];
            tenGodsCount.put(tenGod, tenGodsCount.getOrDefault(tenGod, 0) + 1);
        }

        if (tenGodsCount.isEmpty()) {
            return "십성 분석 결과가 없습니다.";
        }

        for (Map.Entry<String, Integer> entry : tenGodsCount.entrySet()) {
            sb.append(String.format("%s %d개 - %s\n",
                entry.getKey(), entry.getValue(), getTenGodMeaning(entry.getKey())));
        }

        return sb.toString();
    }

    private static String getTenGodMeaning(String tenGod) {
        return switch (tenGod) {
            case "비견" -> "동료, 형제자매, 경쟁 관계";
            case "겁재" -> "동료, 경쟁, 투자";
            case "식신" -> "표현력, 재능, 여유";
            case "상관" -> "재능, 표현, 반항심";
            case "편재" -> "유동적 재물, 투자";
            case "정재" -> "안정적 재물, 근면";
            case "편관" -> "권력, 명예, 압박";
            case "정관" -> "명예, 책임, 규율";
            case "편인" -> "학문, 기술, 개성";
            case "정인" -> "학문, 지혜, 모성";
            default -> "";
        };
    }

    private static String getPersonalityAnalysis(String dayStem, Map<String, Integer> elementCount) {
        StringBuilder sb = new StringBuilder();
        String element = ELEMENT_MAP.get(dayStem);

        sb.append(switch (element) {
            case "목" -> "목 일간은 성장과 확장의 기운을 가집니다. 진취적이고 적극적인 성격이며, " +
                        "새로운 일에 도전하기를 좋아합니다. 인내심이 강하고 끈기가 있습니다.";
            case "화" -> "화 일간은 열정과 활동성을 가집니다. 밝고 긍정적이며 사교성이 뛰어납니다. " +
                        "표현력이 풍부하고 창의적인 면이 있습니다.";
            case "토" -> "토 일간은 안정과 신뢰의 기운을 가집니다. 성실하고 믿음직스러우며, " +
                        "중재자 역할을 잘합니다. 포용력이 크고 현실적입니다.";
            case "금" -> "금 일간은 강인함과 결단력을 가집니다. 원칙적이고 정의로우며, " +
                        "책임감이 강합니다. 명확한 판단력을 지니고 있습니다.";
            case "수" -> "수 일간은 지혜와 융통성을 가집니다. 사고가 유연하고 적응력이 뛰어나며, " +
                        "통찰력이 있습니다. 부드럽고 포용적인 성격입니다.";
            default -> "";
        });

        return sb.toString();
    }

    private static String getWealthAnalysis(int dayStemIndex, List<String> stems,
                                           Map<String, Integer> elementCount) {
        StringBuilder sb = new StringBuilder();

        long wealthCount = stems.stream()
            .filter(stem -> {
                String tenGod = TEN_GODS[dayStemIndex][getStemIndex(stem)];
                return tenGod.contains("재");
            })
            .count();

        if (wealthCount >= 2) {
            sb.append("재성이 충분한 사주입니다. 재물운이 좋으며, 경제적으로 안정될 가능성이 높습니다.\n");
            sb.append("다만 재물을 관리하는 능력이 중요합니다.");
        } else if (wealthCount == 1) {
            sb.append("적당한 재성을 가진 사주입니다. 꾸준한 노력으로 재물을 모을 수 있습니다.\n");
            sb.append("계획적인 재테크가 도움이 됩니다.");
        } else {
            sb.append("재성이 약한 사주입니다. 재물보다는 명예나 학문에 관심이 많을 수 있습니다.\n");
            sb.append("전문성을 키워 간접적으로 재물을 얻는 것이 좋습니다.");
        }

        return sb.toString();
    }

    private static String getCareerAnalysis(String dayStem, Map<String, Integer> elementCount,
                                           List<String> stems) {
        StringBuilder sb = new StringBuilder();
        String element = ELEMENT_MAP.get(dayStem);

        sb.append("적합한 분야: ");
        sb.append(switch (element) {
            case "목" -> "교육, 출판, 목재업, 패션, 환경 관련";
            case "화" -> "예술, 방송, 요식업, 전기/전자, IT";
            case "토" -> "부동산, 건설, 농업, 중개업, 서비스업";
            case "금" -> "금융, 법률, 기계, 의료, 제조업";
            case "수" -> "유통, 물류, 관광, 수산업, 컨설팅";
            default -> "";
        });

        sb.append("\n\n");

        // 관성 분석
        long officialCount = stems.stream()
            .filter(stem -> {
                String tenGod = TEN_GODS[getStemIndex(dayStem)][getStemIndex(stem)];
                return tenGod.contains("관");
            })
            .count();

        if (officialCount >= 2) {
            sb.append("관성이 강하여 조직 생활이나 공직에 적합합니다. 책임감이 강하고 리더십이 있습니다.");
        } else {
            sb.append("자유로운 업무 환경이나 전문직이 잘 맞을 수 있습니다.");
        }

        return sb.toString();
    }

    private static String getRelationshipAnalysis(int dayStemIndex, List<String> stems) {
        StringBuilder sb = new StringBuilder();

        long supportCount = stems.stream()
            .filter(stem -> {
                String tenGod = TEN_GODS[dayStemIndex][getStemIndex(stem)];
                return tenGod.equals("비견") || tenGod.equals("겁재");
            })
            .count();

        if (supportCount >= 2) {
            sb.append("형제자매나 친구들과의 인연이 깊습니다. 동료들과 협력이 잘 되며, ");
            sb.append("단체 활동에서 능력을 발휘합니다.\n");
            sb.append("다만 경쟁 구도가 생길 수 있으니 조화를 유지하는 것이 중요합니다.");
        } else {
            sb.append("독립적인 성향이 강합니다. 소수의 깊은 관계를 선호하며, ");
            sb.append("혼자서도 일을 잘 처리하는 능력이 있습니다.");
        }

        return sb.toString();
    }

    private static String getAdvice(Map<String, Integer> elementCount, String dayStem) {
        StringBuilder sb = new StringBuilder();
        String dayElement = ELEMENT_MAP.get(dayStem);

        // 약한 오행 찾기
        String weakElement = elementCount.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .get().getKey();

        sb.append(String.format("1. %s 기운을 보충하면 좋습니다.\n", weakElement));
        sb.append(getElementAdvice(weakElement)).append("\n\n");

        sb.append("2. 균형잡힌 생활이 중요합니다. 과도한 욕심보다는 꾸준함이 좋은 결과를 가져옵니다.\n\n");

        sb.append("3. 자신의 강점인 ").append(dayElement).append(" 기운을 ");
        sb.append("긍정적으로 발휘하도록 노력하세요.");

        return sb.toString();
    }

    private static String getElementAdvice(String element) {
        return switch (element) {
            case "목" -> "   - 식물 키우기, 독서, 산책 등이 도움됩니다.\n" +
                        "   - 녹색 계열 색상을 활용하세요.";
            case "화" -> "   - 밝고 따뜻한 환경을 조성하세요.\n" +
                        "   - 적극적인 사회활동이 좋습니다.";
            case "토" -> "   - 안정적인 환경 구축이 중요합니다.\n" +
                        "   - 황토색, 갈색 계열을 활용하세요.";
            case "금" -> "   - 규칙적인 생활 패턴을 유지하세요.\n" +
                        "   - 금속 관련 물건이나 흰색이 좋습니다.";
            case "수" -> "   - 충분한 휴식과 수분 섭취가 중요합니다.\n" +
                        "   - 검정색, 남색 계열을 활용하세요.";
            default -> "";
        };
    }
}
