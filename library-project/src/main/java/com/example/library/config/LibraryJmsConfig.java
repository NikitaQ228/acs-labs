package com.example.library.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.jms.ConnectionFactoryUnwrapper;
import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

/** Настраивает получение событий каталога из JMS topic. */
@Configuration(proxyBeanMethods = false)
public class LibraryJmsConfig {

    /**
     * Настраивает именованные долговечные подписки и подтверждение сообщений через транзакцию JMS.
     *
     * @param connectionFactory фабрика соединений с брокером
     * @param configurer настройки контейнера из Spring Boot
     * @return фабрика контейнеров для обоих обработчиков событий
     */
    @Bean
    DefaultJmsListenerContainerFactory libraryTopicListenerFactory(
            ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, ConnectionFactoryUnwrapper.unwrapCaching(connectionFactory));
        factory.setPubSubDomain(true);
        factory.setSubscriptionDurable(true);
        factory.setSubscriptionShared(true);
        factory.setSessionTransacted(true);
        return factory;
    }
}
