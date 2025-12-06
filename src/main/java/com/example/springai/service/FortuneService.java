package com.example.springai.service;

import com.example.springai.agent.FortuneFunction;
import com.example.springai.model.FortuneRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class FortuneService {

    private final ChatClient chatClient;
    private final FortuneFunction fortuneFunction;

    public FortuneService(ChatModel chatModel, FortuneFunction fortuneFunction) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.fortuneFunction = fortuneFunction;
    }

    /**
     * Get today's fortune based on user information
     */
    public String getTodaysFortune(FortuneRequest request) {
        String genderKorean = "male".equalsIgnoreCase(request.getGender()) ? "남성" : "여성";

        // FortuneFunction을 직접 호출하여 기본 정보 얻기
        FortuneFunction.Request funcRequest = new FortuneFunction.Request(
            request.getName(),
            request.getGender(),
            request.getBirthDate()
        );
        FortuneFunction.Response funcResponse = fortuneFunction.apply(funcRequest);

        // AI에게 운세 생성 요청
        String systemMessage = """
            당신은 친절하고 따뜻한 운세 전문가입니다.
            사용자의 정보를 바탕으로 오늘의 운세를 자세하고 긍정적으로 알려주세요.
            다음 내용을 포함해서 운세를 작성하세요:
            1. 오늘의 전반적인 운세
            2. 연애운
            3. 금전운
            4. 건강운
            5. 조언 및 주의사항

            친근하고 따뜻한 어조로 작성하되, 구체적이고 실용적인 조언을 포함하세요.
            운세는 한글로 작성하며, 3-4문단 정도로 작성하세요.
            """;

        String userMessage = String.format("""
            %s님(%s, 생년월일: %s)의 오늘의 운세를 알려주세요.

            별자리: %s
            행운의 숫자: %s
            행운의 색상: %s

            위 정보를 활용하여 구체적이고 긍정적인 운세를 작성해주세요.
            """,
            request.getName(),
            genderKorean,
            request.getBirthDate(),
            funcResponse.zodiacSign(),
            funcResponse.luckyNumber(),
            funcResponse.luckyColor()
        );

        return chatClient.prompt()
            .system(systemMessage)
            .user(userMessage)
            .call()
            .content();
    }
}
