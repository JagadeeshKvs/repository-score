package com.jkrc.repositoryscore.configuration.executor;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class TaskExecutorConfiguration {

    private final TaskExecutorProperties properties;

    public TaskExecutorConfiguration(TaskExecutorProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TaskExecutor getTaskExecutor() {
        return new ThreadPoolTaskExecutorBuilder()
                .corePoolSize(properties.getCorePoolSize())
                .maxPoolSize(properties.getMaxPoolSize())
                .queueCapacity(properties.getQueueCapacity())
                .threadNamePrefix(properties.getThreadNamePrefix())
                .keepAlive(properties.getKeepAlive())
                .build();
    }
}
