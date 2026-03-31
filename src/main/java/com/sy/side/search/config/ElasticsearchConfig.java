package com.sy.side.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfigurationSupport {

    @Override
    protected boolean writeTypeHints() {
        return false;
    }
}
