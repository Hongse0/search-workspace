package com.sy.side.stock.config;

import com.sy.side.stock.util.EtfPublicDataProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EtfPublicDataProperties.class)
public class StockPropertiesConfig {
}