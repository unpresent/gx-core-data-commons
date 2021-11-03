package ru.gx.data.config;

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

    @NestedConfigurationProperty
    private EntitiesDtoLinks entitiesDtoLinks;

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

    @Getter
    @Setter
    public static class EntitiesDtoLinks {
        @NestedConfigurationProperty
        private SimpleConfiguration simpleConfiguration;

        @NestedConfigurationProperty
        private ConfiguratorCaller configuratorCaller;
    }

    @Getter
    @Setter
    public static class SimpleConfiguration {
        private boolean enabled;
    }

    @Getter
    @Setter
    public static class ConfiguratorCaller {
        private boolean enabled;
    }
}
