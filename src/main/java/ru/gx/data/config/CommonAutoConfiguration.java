package ru.gx.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gx.data.ActiveConnectionsContainer;

@Configuration
public class CommonAutoConfiguration {
    @Value("${service.name}")
    private String serviceName;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.active-connections-container.enabled", havingValue = "true")
    public ActiveConnectionsContainer activeConnectionsContainer() {
        return new ActiveConnectionsContainer();
    }
}
