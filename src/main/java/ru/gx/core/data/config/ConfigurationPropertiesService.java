package ru.gx.core.data.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "service")
@Getter
@Setter
public class ConfigurationPropertiesService {

    @NestedConfigurationProperty
    private ActiveSessionsContainer activeSessionsContainer;

    @NestedConfigurationProperty
    private ActiveSessionsContainer activeConnectionsContainer;

    @Getter
    @Setter
    public static class ActiveSessionsContainer {
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class ActiveConnectionsContainer {
        private boolean enabled;
    }
}
