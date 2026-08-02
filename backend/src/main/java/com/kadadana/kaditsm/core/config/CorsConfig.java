package com.kadadana.kaditsm.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Collections.singletonList("*"));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control", "Accept"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}