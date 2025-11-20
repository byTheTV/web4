package org.example.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Конфигурация Hazelcast клиента для подключения к standalone серверу.
 * Использует программную конфигурацию вместо XML из-за несовместимости
 * XML парсера WildFly с валидацией схем Hazelcast.
 */
@ApplicationScoped
public class HazelcastConfig {
    
    private HazelcastInstance hazelcastInstance;
    
    @PostConstruct
    public void init() {
        try {
            // Получаем настройки из переменных окружения или используем значения по умолчанию
            String hazelcastHost = System.getenv("HAZELCAST_HOST");
            String hazelcastPort = System.getenv("HAZELCAST_PORT");
            
            if (hazelcastHost == null || hazelcastHost.isEmpty()) {
                hazelcastHost = "localhost";
            }
            if (hazelcastPort == null || hazelcastPort.isEmpty()) {
                hazelcastPort = "5701";
            }
            
            String hazelcastAddress = hazelcastHost + ":" + hazelcastPort;
            
            // Используем программную конфигурацию (избегаем проблем с XML парсером WildFly)
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setClusterName("dev");
            clientConfig.getNetworkConfig().addAddress(hazelcastAddress);
            clientConfig.getNetworkConfig().setConnectionTimeout(60000);
            // В Hazelcast 5.x эти методы могут иметь другие названия или отсутствовать
            // Основные настройки уже установлены выше
            
            hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
            System.out.println("Hazelcast client connected successfully to: " + hazelcastAddress);
        } catch (Exception e) {
            System.err.println("Failed to initialize Hazelcast client: " + e.getMessage());
            e.printStackTrace();
            // Не бросаем исключение, чтобы приложение могло работать без кеша
            // Кеш будет недоступен, но приложение продолжит работу с fallback на БД
            System.err.println("Hazelcast cache will be unavailable. Application will use database fallback.");
        }
    }
    
    @PreDestroy
    public void destroy() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
            System.out.println("Hazelcast client shut down");
        }
    }
    
    @Produces
    @ApplicationScoped
    public HazelcastInstance getHazelcastInstance() {
        // Возвращаем null если инициализация не удалась - CacheService обработает это
        return hazelcastInstance;
    }
}

