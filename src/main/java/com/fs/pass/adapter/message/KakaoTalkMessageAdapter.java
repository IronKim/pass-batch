package com.fs.pass.adapter.message;

import com.fs.pass.config.KakaoTalkMessageConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoTalkMessageAdapter {
    private final WebClient webClient;

    public KakaoTalkMessageAdapter(KakaoTalkMessageConfig config) {
        webClient = WebClient.builder()
                .baseUrl(config.getHost())
                .defaultHeaders(h -> {
                    h.setBearerAuth(config.getToken());
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                }).build();
    }

    public boolean sendKakaoTalkMessage(String uuid, String text) {
        KakaoTalkMessageResponse response = webClient.post().uri("/v1/api/talk/friends/message/default/send")
                .body(BodyInserters.fromValue(new KakaoTalkMessageRequest(uuid, text))) // 요청 바디를 설정한다.
                .retrieve() // 요청을 보내고 응답을 받는다.
                .bodyToMono(KakaoTalkMessageResponse.class) // 응답을 KakaoTalkMessageResponse 객체로 변환한다.
                .block(); // 응답을 기다린다.

        if (response == null || response.getSuccessfulReceiverUuids() == null) {
            return false;
        }
        return !response.getSuccessfulReceiverUuids().isEmpty();

    }
}
