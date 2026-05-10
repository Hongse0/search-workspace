package com.sy.side.stock.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "external.public-data.etf")
public class EtfPublicDataProperties {
    private String baseUrl;
    private String serviceKey;
    private int numOfRows = 1000;
    private String resultType = "json";
}
