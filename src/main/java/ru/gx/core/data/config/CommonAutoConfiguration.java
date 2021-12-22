package ru.gx.core.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gx.core.data.ActiveConnectionsContainer;
import ru.gx.core.data.ActiveSessionsContainer;

@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesService.class)
public class CommonAutoConfiguration {
    @Value("${service.name}")
    private String serviceName;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.active-connections-container.enabled", havingValue = "true")
    public ActiveConnectionsContainer activeConnectionsContainer() {
        return new ActiveConnectionsContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "service.active-sessions-container.enabled", havingValue = "true")
    public ActiveSessionsContainer activeSessionsContainer() {
        return new ActiveSessionsContainer();
    }
}
