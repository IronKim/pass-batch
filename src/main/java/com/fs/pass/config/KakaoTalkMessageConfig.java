package com.fs.pass.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakaotalk") // application.yml의 kakaotalk 아래에 있는 속성들을 가져옴
public class KakaoTalkMessageConfig {
    private String host;
    private String token;
}
