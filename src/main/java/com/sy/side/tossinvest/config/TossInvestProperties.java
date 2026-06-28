package com.sy.side.tossinvest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss-invest")
public class TossInvestProperties {

    private String baseUrl;
    private String clientId;
    private String clientSecret;
}
